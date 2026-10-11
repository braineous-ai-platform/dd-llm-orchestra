package io.braineous.dd.llm.orchestra.def.service;

import ai.braineous.rag.prompt.observe.Console;
import io.braineous.dd.llm.orchestra.def.model.ExecutionResult;
import io.braineous.dd.llm.orchestra.def.model.Query;
import io.braineous.dd.llm.orchestra.def.model.Transaction;
import io.braineous.dd.llm.orchestra.def.model.Workflow;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ExecutorTest {

    private Map<String, Object> input() {
        return new HashMap<String, Object>();
    }

    @Test
    public void executeTransaction_shouldFail_whenWorkflowNull() {

        Executor ex = new Executor();
        ex.deactivateExeMode();

        ExecutionResult r = ex.executeTransaction(null, input());

        Console.log("ExecutorTest.executeTransaction_shouldFail_whenWorkflowNull", r.toString());

        assertNotNull(r);
        assertFalse(r.isSuccess());
        assertEquals("ORCH_EXEC_NULL_WORKFLOW", r.getWhy().getReason());
    }

    @Test
    public void executeTransaction_shouldFail_whenTransactionNull() {

        Workflow wf = new Workflow();
        wf.setName("wf1");
        wf.setTransaction(null);

        Executor ex = new Executor();
        ex.deactivateExeMode();

        ExecutionResult r = ex.executeTransaction(wf, input());

        Console.log("ExecutorTest.executeTransaction_shouldFail_whenTransactionNull", r.toString());

        assertNotNull(r);
        assertFalse(r.isSuccess());
        assertEquals("ORCH_EXEC_NULL_TRANSACTION", r.getWhy().getReason());
    }

    @Test
    public void executeTransaction_shouldFail_whenInputNull() {

        Workflow wf = new Workflow();
        wf.setName("wf1");
        wf.setTransaction(new Transaction());

        Executor ex = new Executor();
        ex.deactivateExeMode();

        ExecutionResult r = ex.executeTransaction(wf, null);

        Console.log("ExecutorTest.executeTransaction_shouldFail_whenInputNull", r.toString());

        assertNotNull(r);
        assertFalse(r.isSuccess());
        assertEquals("ORCH_EXEC_NULL_INPUT", r.getWhy().getReason());
    }

    @Test
    public void executeTransaction_shouldFail_whenCommitOrderEmpty() {

        Workflow wf = new Workflow();
        wf.setName("wf1");

        Transaction tx = new Transaction();
        tx.setDescription("tx1");
        tx.setCommitOrder(new ArrayList<String>());

        wf.setTransaction(tx);

        Executor ex = new Executor();
        ex.deactivateExeMode();

        ExecutionResult r = ex.executeTransaction(wf, input());

        Console.log("ExecutorTest.executeTransaction_shouldFail_whenCommitOrderEmpty", r.toString());

        assertNotNull(r);
        assertFalse(r.isSuccess());
        assertEquals("ORCH_EXEC_EMPTY_COMMIT_ORDER", r.getWhy().getReason());
    }

    @Test
    public void executeTransaction_shouldFailSurfaceGate_whenCommitOrderMismatch_uniqueQueryIds() {

        Workflow wf = new Workflow();
        wf.setName("wf1");

        Transaction tx = new Transaction();
        tx.setDescription("tx1");

        List<Query> queries = new ArrayList<Query>();
        Query q1 = new Query();
        q1.setId("q1");
        q1.setSql("select decision from llm where task = \"t1\"");
        queries.add(q1);

        tx.setQueries(queries);

        List<String> commitOrder = new ArrayList<String>();
        commitOrder.add("q1");
        commitOrder.add("q2");
        tx.setCommitOrder(commitOrder);

        wf.setTransaction(tx);

        Executor ex = new Executor();
        ex.deactivateExeMode();

        ExecutionResult r = ex.executeTransaction(wf, input());

        Console.log("ExecutorTest.executeTransaction_shouldFailSurfaceGate_whenCommitOrderMismatch_uniqueQueryIds", r.toString());

        assertNotNull(r);
        assertFalse(r.isSuccess());
        assertEquals("ORCH_EXEC_COMMIT_ORDER_MISMATCH", r.getWhy().getReason());
        assertEquals(0, r.getQueryResults().size());
    }
}