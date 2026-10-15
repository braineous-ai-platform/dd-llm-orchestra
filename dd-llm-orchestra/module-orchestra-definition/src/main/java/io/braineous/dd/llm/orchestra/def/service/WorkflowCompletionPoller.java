package io.braineous.dd.llm.orchestra.def.service;

import ai.braineous.rag.prompt.observe.Console;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

@ApplicationScoped
public class WorkflowCompletionPoller {

    private static final int DEFAULT_POOL_SIZE = 4;
    private static final long DEFAULT_INTERVAL_MS = 250L;

    private final Object lifecycle = new Object();

    @Inject
    @ConfigProperty(name = "orchestra.completion.poller.pool-size", defaultValue = "4")
    private int poolSize;

    @Inject
    @ConfigProperty(name = "orchestra.completion.poller.interval-ms", defaultValue = "250")
    private long intervalMs;

    private ScheduledExecutorService scheduler;

    private final ConcurrentHashMap<String, AtomicBoolean> active =
            new ConcurrentHashMap<String, AtomicBoolean>();

    private final ConcurrentHashMap<String, ScheduledFuture<?>> registrations =
            new ConcurrentHashMap<String, ScheduledFuture<?>>();

    private final ConcurrentHashMap<String, String> completed =
            new ConcurrentHashMap<String, String>();

    public WorkflowCompletionPoller() {
    }

    WorkflowCompletionPoller(int poolSize, long intervalMs) {
        this.poolSize = poolSize;
        this.intervalMs = intervalMs;
        start();
    }

    @PostConstruct
    void start() {
        synchronized (this.lifecycle) {
            if (this.scheduler != null && !this.scheduler.isShutdown()) {
                return;
            }

            int size = this.poolSize;
            if (size <= 0) {
                size = DEFAULT_POOL_SIZE;
            }

            final AtomicInteger threadSeq = new AtomicInteger(1);
            this.scheduler = Executors.newScheduledThreadPool(size, new ThreadFactory() {
                @Override
                public Thread newThread(Runnable runnable) {
                    Thread t = new Thread(runnable);
                    t.setName("workflow-completion-poller-" + threadSeq.getAndIncrement());
                    t.setDaemon(true);
                    return t;
                }
            });
        }
    }

    @PreDestroy
    void stop() {
        synchronized (this.lifecycle) {
            ScheduledExecutorService current = this.scheduler;
            if (current != null) {
                current.shutdownNow();
            }

            for (ScheduledFuture<?> future : this.registrations.values()) {
                if (future != null) {
                    future.cancel(false);
                }
            }

            this.registrations.clear();
            this.active.clear();
        }
    }

    public void register(String executionId, Function<String, String> statusReader) {
        if (executionId == null || executionId.trim().isEmpty()) {
            return;
        }
        if (statusReader == null) {
            return;
        }
        if (this.completed.containsKey(executionId)) {
            return;
        }

        ensureStarted();

        AtomicBoolean gate = new AtomicBoolean(false);
        if (this.active.putIfAbsent(executionId, gate) != null) {
            return;
        }

        long delay = this.intervalMs;
        if (delay <= 0L) {
            delay = DEFAULT_INTERVAL_MS;
        }

        final CountDownLatch armed = new CountDownLatch(1);
        ScheduledFuture<?> future = null;
        try {
            ScheduledExecutorService current = this.scheduler;
            if (current == null || current.isShutdown()) {
                this.active.remove(executionId, gate);
                return;
            }

            future = current.scheduleWithFixedDelay(new Runnable() {
                @Override
                public void run() {
                    try {
                        armed.await();
                    } catch (InterruptedException interrupted) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                    poll(executionId, statusReader);
                }
            }, 0L, delay, TimeUnit.MILLISECONDS);

            this.registrations.put(executionId, future);
            if (this.completed.containsKey(executionId) || !this.active.containsKey(executionId)) {
                terminate(executionId);
            }
        } catch (Exception e) {
            this.active.remove(executionId, gate);
            ScheduledFuture<?> registered = this.registrations.remove(executionId);
            if (registered != null) {
                registered.cancel(false);
            } else if (future != null) {
                future.cancel(false);
            }
            Console.log("workflow.completion.poller.register.error", String.valueOf(e));
        } finally {
            armed.countDown();
        }
    }

    public boolean isRegistered(String executionId) {
        if (executionId == null) {
            return false;
        }
        return this.active.containsKey(executionId);
    }

    public boolean isCompleted(String executionId) {
        if (executionId == null) {
            return false;
        }
        return this.completed.containsKey(executionId);
    }

    private void ensureStarted() {
        synchronized (this.lifecycle) {
            if (this.scheduler == null || this.scheduler.isShutdown()) {
                start();
            }
        }
    }

    private void poll(String executionId, Function<String, String> statusReader) {
        AtomicBoolean gate = this.active.get(executionId);
        if (gate == null) {
            return;
        }
        if (!gate.compareAndSet(false, true)) {
            return;
        }

        boolean completedNow = false;
        try {
            if (this.completed.containsKey(executionId) || !this.active.containsKey(executionId)) {
                return;
            }

            String status = statusReader.apply(executionId);
            Console.log("workflow.completion.poller.status", executionId + "=" + status);

            if ("COMPLETED".equals(status)) {
                completedNow = this.completed.putIfAbsent(executionId, status) == null;
                terminate(executionId);
            }
        } catch (Exception e) {
            Console.log("workflow.completion.poller.error", String.valueOf(e));
        } finally {
            gate.set(false);
        }

        if (!completedNow) {
            return;
        }

        try {
            onCompleted(executionId);
        } catch (Exception e) {
            Console.log("workflow.completion.poller.completed.error", String.valueOf(e));
        }
    }

    private void terminate(String executionId) {
        this.active.remove(executionId);
        ScheduledFuture<?> future = this.registrations.remove(executionId);
        if (future != null) {
            future.cancel(false);
        }
    }

    void onCompleted(String executionId) {
        Console.log("workflow.completion.poller.completed", executionId);
    }
}
