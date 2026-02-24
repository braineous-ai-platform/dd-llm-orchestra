package io.braineous.dd.llm.orchestra.def.service;

import ai.braineous.rag.prompt.observe.Console;
import io.braineous.dd.llm.orchestra.def.model.ExecutionResult;
import io.braineous.dd.llm.orchestra.def.model.Query;
import io.braineous.dd.llm.orchestra.def.model.QueryExecutionResult;
import io.braineous.dd.llm.orchestra.def.model.Transaction;
import io.braineous.dd.llm.orchestra.def.model.Workflow;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ExecutorTest {

    @Test
    public void executeTransaction_shouldFail_whenWorkflowNull() {

        Executor ex = new Executor();

        ExecutionResult r = ex.executeTransaction(null);

        Console.log("ExecutorTest.executeTransaction_shouldFail_whenWorkflowNull", r.toString());

        assertNotNull(r);
        assertFalse(r.isSuccess());
        assertNotNull(r.getWhy());
        assertEquals("ORCH_EXEC_NULL_WORKFLOW", r.getWhy().getReason());
    }

    @Test
    public void executeTransaction_shouldFail_whenTransactionNull() {

        Workflow wf = new Workflow();
        wf.setName("wf1");
        wf.setTransaction(null);

        Executor ex = new Executor();

        ExecutionResult r = ex.executeTransaction(wf);

        Console.log("ExecutorTest.executeTransaction_shouldFail_whenTransactionNull", r.toString());

        assertNotNull(r);
        assertFalse(r.isSuccess());
        assertNotNull(r.getWhy());
        assertEquals("ORCH_EXEC_NULL_TRANSACTION", r.getWhy().getReason());
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

        ExecutionResult r = ex.executeTransaction(wf);

        Console.log("ExecutorTest.executeTransaction_shouldFail_whenCommitOrderEmpty", r.toString());

        assertNotNull(r);
        assertFalse(r.isSuccess());
        assertNotNull(r.getWhy());
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
        commitOrder.add("q2"); // extra, mismatch
        tx.setCommitOrder(commitOrder);

        wf.setTransaction(tx);

        Executor ex = new Executor();

        ExecutionResult r = ex.executeTransaction(wf);

        Console.log("ExecutorTest.executeTransaction_shouldFailSurfaceGate_whenCommitOrderMismatch_uniqueQueryIds", r.toString());

        assertNotNull(r);
        assertFalse(r.isSuccess());
        assertNotNull(r.getWhy());
        assertEquals("ORCH_EXEC_COMMIT_ORDER_MISMATCH", r.getWhy().getReason());

        // surface gate failure returns null partialResults in current implementation
        assertNotNull(r.getQueryResults());
        assertEquals(0, r.getQueryResults().size());
    }

    @Test
    public void executeTransaction_shouldStop_whenSqlEmpty() {

        Workflow wf = new Workflow();
        wf.setName("wf1");

        Transaction tx = new Transaction();
        tx.setDescription("tx1");

        List<Query> queries = new ArrayList<Query>();

        Query q1 = new Query();
        q1.setId("q1");
        q1.setSql("select decision from llm where task = \"t1\"");
        queries.add(q1);

        Query q2 = new Query();
        q2.setId("q2");
        q2.setSql("   "); // empty -> ERROR
        queries.add(q2);

        tx.setQueries(queries);

        List<String> commitOrder = new ArrayList<String>();
        commitOrder.add("q1");
        commitOrder.add("q2");
        tx.setCommitOrder(commitOrder);

        wf.setTransaction(tx);

        Executor ex = new Executor();

        ExecutionResult r = ex.executeTransaction(wf);

        Console.log("ExecutorTest.executeTransaction_shouldStop_whenSqlEmpty", r.toString());

        assertNotNull(r);
        assertFalse(r.isSuccess());

        assertEquals("q2", r.getStoppedAtQueryId());

        assertNotNull(r.getWhy());
        assertEquals("ORCH_EXEC_STOPPED", r.getWhy().getReason());

        assertNotNull(r.getQueryResults());
        assertEquals(2, r.getQueryResults().size());

        assertEquals("q1", r.getQueryResults().get(0).getQueryId());
        assertTrue(r.getQueryResults().get(0).isSuccess());
        assertEquals("EXECUTED", r.getQueryResults().get(0).getStatus());

        QueryExecutionResult q2r = r.getQueryResults().get(1);
        assertEquals("q2", q2r.getQueryId());
        assertFalse(q2r.isSuccess());
        assertEquals("ERROR", q2r.getStatus());
        assertNotNull(q2r.getWhy());
        assertEquals("ORCH_EXEC_EMPTY_SQL", q2r.getWhy().getReason());
    }

    @Test
    public void executeTransaction_shouldSucceed_whenAllQueriesValid() {

        Workflow wf = new Workflow();
        wf.setName("wf_ok");

        Transaction tx = new Transaction();
        tx.setDescription("tx_ok");

        List<Query> queries = new ArrayList<Query>();

        Query q1 = new Query();
        q1.setId("q1");
        q1.setSql("select decision from llm where task = \"t1\"");
        queries.add(q1);

        Query q2 = new Query();
        q2.setId("q2");
        q2.setSql("select decision from llm where task = \"t2\"");
        queries.add(q2);

        tx.setQueries(queries);

        List<String> commitOrder = new ArrayList<String>();
        commitOrder.add("q1");
        commitOrder.add("q2");
        tx.setCommitOrder(commitOrder);

        wf.setTransaction(tx);

        Executor ex = new Executor();

        ExecutionResult r = ex.executeTransaction(wf);

        Console.log("ExecutorTest.executeTransaction_shouldSucceed_whenAllQueriesValid", r.toString());

        assertNotNull(r);
        assertTrue(r.isSuccess());
        assertNull(r.getWhy());
        assertNull(r.getStoppedAtQueryId());

        assertEquals("wf_ok", r.getWorkflowName());
        assertEquals("tx_ok", r.getTransactionDescription());

        assertNotNull(r.getQueryResults());
        assertEquals(2, r.getQueryResults().size());

        assertEquals("q1", r.getQueryResults().get(0).getQueryId());
        assertEquals("EXECUTED", r.getQueryResults().get(0).getStatus());

        assertEquals("q2", r.getQueryResults().get(1).getQueryId());
        assertEquals("EXECUTED", r.getQueryResults().get(1).getStatus());
    }
}