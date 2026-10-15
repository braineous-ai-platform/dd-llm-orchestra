package io.braineous.dd.llm.orchestra.def.service;

import ai.braineous.rag.prompt.observe.Console;
import io.braineous.dd.llm.orchestra.def.model.ExecutionResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

public class WorkflowCompletionPollerTest {

    private WorkflowCompletionPoller poller;

    @AfterEach
    public void stopPoller() {
        if (this.poller != null) {
            this.poller.stop();
        }
    }

    @Test
    public void test_1_multipleExecutionIdsPolledConcurrently() throws Exception {

        this.poller = new WorkflowCompletionPoller(2, 20L);

        CountDownLatch bothEntered = new CountDownLatch(2);
        AtomicInteger inFlight = new AtomicInteger(0);
        AtomicInteger maxInFlight = new AtomicInteger(0);

        Function<String, String> reader = new Function<String, String>() {
            @Override
            public String apply(String executionId) {
                int n = inFlight.incrementAndGet();
                maxInFlight.accumulateAndGet(n, Math::max);
                bothEntered.countDown();
                try {
                    bothEntered.await(2, TimeUnit.SECONDS);
                } catch (InterruptedException ignored) {
                }
                inFlight.decrementAndGet();
                return "RUNNING";
            }
        };

        this.poller.register("exec-a", reader);
        this.poller.register("exec-b", reader);

        boolean concurrent = bothEntered.await(2, TimeUnit.SECONDS);

        Console.log("WorkflowCompletionPollerTest.test_1",
                "concurrent=" + concurrent + " maxInFlight=" + maxInFlight.get());

        assertTrue(concurrent, "both executions must enter status checks together");
        assertTrue(maxInFlight.get() >= 2, "status checks must overlap across executionIds");
        assertTrue(this.poller.isRegistered("exec-a"));
        assertTrue(this.poller.isRegistered("exec-b"));
    }

    @Test
    public void test_2_runningRemainsRegistered() throws Exception {

        this.poller = new WorkflowCompletionPoller(2, 20L);

        AtomicInteger polls = new AtomicInteger(0);
        Function<String, String> reader = new Function<String, String>() {
            @Override
            public String apply(String executionId) {
                polls.incrementAndGet();
                return "RUNNING";
            }
        };

        this.poller.register("exec-running", reader);

        waitUntil(new Condition() {
            @Override
            public boolean ok() {
                return polls.get() >= 2;
            }
        }, 2000L);

        Console.log("WorkflowCompletionPollerTest.test_2",
                "polls=" + polls.get() + " registered=" + this.poller.isRegistered("exec-running"));

        assertTrue(this.poller.isRegistered("exec-running"));
        assertFalse(this.poller.isCompleted("exec-running"));
        assertTrue(polls.get() >= 2);
    }

    @Test
    public void test_3_firstCompletedRemovesFromActivePolling() throws Exception {

        this.poller = new WorkflowCompletionPoller(2, 20L);

        AtomicInteger polls = new AtomicInteger(0);
        Function<String, String> reader = new Function<String, String>() {
            @Override
            public String apply(String executionId) {
                int n = polls.incrementAndGet();
                if (n == 1) {
                    return "RUNNING";
                }
                return "COMPLETED";
            }
        };

        this.poller.register("exec-done", reader);

        waitUntil(new Condition() {
            @Override
            public boolean ok() {
                return poller.isCompleted("exec-done");
            }
        }, 2000L);

        Console.log("WorkflowCompletionPollerTest.test_3",
                "polls=" + polls.get()
                        + " registered=" + this.poller.isRegistered("exec-done")
                        + " completed=" + this.poller.isCompleted("exec-done"));

        assertTrue(this.poller.isCompleted("exec-done"));
        assertFalse(this.poller.isRegistered("exec-done"));
    }

    @Test
    public void test_4_noStatusChecksAfterCompletion() throws Exception {

        this.poller = new WorkflowCompletionPoller(2, 20L);

        AtomicInteger polls = new AtomicInteger(0);
        Function<String, String> reader = new Function<String, String>() {
            @Override
            public String apply(String executionId) {
                polls.incrementAndGet();
                return "COMPLETED";
            }
        };

        this.poller.register("exec-once", reader);

        waitUntil(new Condition() {
            @Override
            public boolean ok() {
                return poller.isCompleted("exec-once");
            }
        }, 2000L);

        int afterComplete = polls.get();
        Thread.sleep(120L);
        int later = polls.get();

        Console.log("WorkflowCompletionPollerTest.test_4",
                "afterComplete=" + afterComplete + " later=" + later);

        assertEquals(afterComplete, later, "no further status checks after COMPLETED");
        assertEquals(1, afterComplete);
        assertFalse(this.poller.isRegistered("exec-once"));
    }

    @Test
    public void test_5_duplicateRegistrationDoesNotCreateDuplicatePollingTasks() throws Exception {

        this.poller = new WorkflowCompletionPoller(2, 30L);

        AtomicInteger polls = new AtomicInteger(0);
        Function<String, String> reader = new Function<String, String>() {
            @Override
            public String apply(String executionId) {
                polls.incrementAndGet();
                return "RUNNING";
            }
        };

        this.poller.register("exec-dup", reader);
        this.poller.register("exec-dup", reader);
        this.poller.register("exec-dup", reader);

        waitUntil(new Condition() {
            @Override
            public boolean ok() {
                return polls.get() >= 1;
            }
        }, 2000L);

        Thread.sleep(80L);
        int observed = polls.get();

        Console.log("WorkflowCompletionPollerTest.test_5", "observed=" + observed);

        assertTrue(this.poller.isRegistered("exec-dup"));
        assertTrue(observed >= 1);
        assertTrue(observed <= 5, "duplicate register must not multiply poll tasks, observed=" + observed);
    }

    @Test
    public void test_6_slowExecutionDoesNotSerializeIndependentExecutions() throws Exception {

        this.poller = new WorkflowCompletionPoller(2, 20L);

        CountDownLatch slowEntered = new CountDownLatch(1);
        CountDownLatch fastCompleted = new CountDownLatch(1);
        AtomicBoolean fastCompletedDuringSlow = new AtomicBoolean(false);

        Function<String, String> reader = new Function<String, String>() {
            @Override
            public String apply(String executionId) {
                if ("exec-slow".equals(executionId)) {
                    slowEntered.countDown();
                    try {
                        Thread.sleep(400L);
                    } catch (InterruptedException ignored) {
                    }
                    return "RUNNING";
                }

                fastCompletedDuringSlow.set(slowEntered.getCount() == 0 && !poller.isCompleted("exec-slow"));
                fastCompleted.countDown();
                return "COMPLETED";
            }
        };

        this.poller.register("exec-slow", reader);
        assertTrue(slowEntered.await(2, TimeUnit.SECONDS), "slow execution must start polling");
        this.poller.register("exec-fast", reader);

        boolean fastDone = fastCompleted.await(2, TimeUnit.SECONDS);

        Console.log("WorkflowCompletionPollerTest.test_6",
                "fastDone=" + fastDone
                        + " fastDuringSlow=" + fastCompletedDuringSlow.get()
                        + " slowRegistered=" + this.poller.isRegistered("exec-slow"));

        assertTrue(fastDone, "fast execution must complete while slow is in-flight");
        assertTrue(this.poller.isCompleted("exec-fast"));
        assertTrue(this.poller.isRegistered("exec-slow"));
        assertTrue(fastCompletedDuringSlow.get());
    }

    @Test
    public void test_7_executeTransactionReturnsWithoutWaitingForCompletion() {

        this.poller = new WorkflowCompletionPoller(1, 20L);

        CountDownLatch entered = new CountDownLatch(1);
        CountDownLatch release = new CountDownLatch(1);

        Function<String, String> blockingReader = new Function<String, String>() {
            @Override
            public String apply(String executionId) {
                entered.countDown();
                try {
                    release.await(2, TimeUnit.SECONDS);
                } catch (InterruptedException ignored) {
                }
                return "RUNNING";
            }
        };

        long t0 = System.nanoTime();
        this.poller.register("exec-block", blockingReader);
        long registerMs = (System.nanoTime() - t0) / 1_000_000L;

        Console.log("WorkflowCompletionPollerTest.test_7.registerMs", Long.valueOf(registerMs));

        assertTrue(registerMs < 200L, "register must not wait for status checks, tookMs=" + registerMs);

        Executor ex = new Executor();
        ex.deactivateExeMode();
        ex.setCompletionPoller(this.poller);

        Map<String, Object> input = new HashMap<String, Object>();

        long t1 = System.nanoTime();
        ExecutionResult r = ex.executeTransaction(null, input);
        long execMs = (System.nanoTime() - t1) / 1_000_000L;

        Console.log("WorkflowCompletionPollerTest.test_7.execMs", Long.valueOf(execMs));
        Console.log("WorkflowCompletionPollerTest.test_7.result", r.toString());

        assertNotNull(r);
        assertFalse(r.isSuccess());
        assertEquals("ORCH_EXEC_NULL_WORKFLOW", r.getWhy().getReason());
        assertTrue(execMs < 200L, "executeTransaction must not wait on the poller, tookMs=" + execMs);
        assertNull(r.getExecutionId());

        release.countDown();
    }

    @Test
    public void test_8_completedExecutionIsInternallyObservable() throws Exception {

        this.poller = new WorkflowCompletionPoller(1, 20L);

        ConcurrentHashMap<String, String> observed = new ConcurrentHashMap<String, String>();

        Function<String, String> reader = new Function<String, String>() {
            @Override
            public String apply(String executionId) {
                observed.put(executionId, "COMPLETED");
                return "COMPLETED";
            }
        };

        this.poller.register("exec-obs", reader);

        waitUntil(new Condition() {
            @Override
            public boolean ok() {
                return poller.isCompleted("exec-obs");
            }
        }, 2000L);

        Console.log("WorkflowCompletionPollerTest.test_8",
                "completed=" + this.poller.isCompleted("exec-obs"));

        assertTrue(this.poller.isCompleted("exec-obs"));
        assertEquals("COMPLETED", observed.get("exec-obs"));
        assertFalse(this.poller.isRegistered("exec-obs"));
    }

    @Test
    public void test_9_sameExecutionStatusChecksDoNotOverlap() throws Exception {

        this.poller = new WorkflowCompletionPoller(2, 10L);

        AtomicInteger inFlight = new AtomicInteger(0);
        AtomicInteger maxInFlight = new AtomicInteger(0);
        AtomicInteger polls = new AtomicInteger(0);

        Function<String, String> reader = new Function<String, String>() {
            @Override
            public String apply(String executionId) {
                int n = inFlight.incrementAndGet();
                maxInFlight.accumulateAndGet(n, Math::max);
                try {
                    Thread.sleep(40L);
                } catch (InterruptedException ignored) {
                } finally {
                    inFlight.decrementAndGet();
                }
                polls.incrementAndGet();
                return "RUNNING";
            }
        };

        this.poller.register("exec-serial", reader);
        this.poller.register("exec-serial", reader);

        waitUntil(new Condition() {
            @Override
            public boolean ok() {
                return polls.get() >= 2;
            }
        }, 2000L);

        Console.log("WorkflowCompletionPollerTest.test_9",
                "polls=" + polls.get() + " maxInFlight=" + maxInFlight.get());

        assertTrue(polls.get() >= 2);
        assertEquals(1, maxInFlight.get(), "one execution must not overlap its own status checks");
        assertTrue(this.poller.isRegistered("exec-serial"));
        assertFalse(this.poller.isCompleted("exec-serial"));
    }

    private interface Condition {
        boolean ok();
    }

    private void waitUntil(Condition condition, long timeoutMillis) throws Exception {
        long start = System.currentTimeMillis();
        while (!condition.ok()) {
            if (System.currentTimeMillis() - start > timeoutMillis) {
                fail("timed out waiting for poller condition");
            }
            Thread.sleep(10L);
        }
    }
}
