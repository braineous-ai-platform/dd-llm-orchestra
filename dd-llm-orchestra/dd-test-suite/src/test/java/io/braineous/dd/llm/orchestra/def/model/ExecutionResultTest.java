package io.braineous.dd.llm.orchestra.def.model;

import ai.braineous.rag.prompt.observe.Console;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ExecutionResultTest {

    @Test
    public void success_shouldCreateSuccessResult_andPreserveList() {

        List<QueryExecutionResult> results = new ArrayList<QueryExecutionResult>();
        results.add(QueryExecutionResult.executed("q1"));
        results.add(QueryExecutionResult.executed("q2"));

        ExecutionResult r = ExecutionResult.success("wf1", "tx desc", results);

        Console.log("ExecutionResultTest.success_shouldCreateSuccessResult_andPreserveList", r.toString());

        assertNotNull(r);
        assertTrue(r.isSuccess());
        assertEquals("wf1", r.getWorkflowName());
        assertEquals("tx desc", r.getTransactionDescription());
        assertNull(r.getWhy());
        assertNull(r.getStoppedAtQueryId());

        assertNotNull(r.getQueryResults());
        assertEquals(2, r.getQueryResults().size());
        assertEquals("q1", r.getQueryResults().get(0).getQueryId());
        assertEquals("q2", r.getQueryResults().get(1).getQueryId());
    }

    @Test
    public void success_shouldDefaultToEmptyList_whenNullListPassed() {

        ExecutionResult r = ExecutionResult.success("wf1", "tx desc", null);

        Console.log("ExecutionResultTest.success_shouldDefaultToEmptyList_whenNullListPassed", r.toString());

        assertNotNull(r);
        assertTrue(r.isSuccess());
        assertNotNull(r.getQueryResults());
        assertEquals(0, r.getQueryResults().size());
    }

    @Test
    public void failure_shouldCreateFailureResult_withWhy_andPartialResults() {

        List<QueryExecutionResult> partial = new ArrayList<QueryExecutionResult>();
        partial.add(QueryExecutionResult.executed("q1"));
        partial.add(QueryExecutionResult.blocked("q2", "NEEDS_APPROVAL", "WHY_APPROVAL_REQUIRED", "needs HITL"));

        ExecutionResult r = ExecutionResult.failure(
                "wf1",
                "tx desc",
                "q2",
                "ORCH_EXEC_STOPPED",
                "execution stopped",
                partial
        );

        Console.log("ExecutionResultTest.failure_shouldCreateFailureResult_withWhy_andPartialResults", r.toString());

        assertNotNull(r);
        assertFalse(r.isSuccess());

        assertEquals("wf1", r.getWorkflowName());
        assertEquals("tx desc", r.getTransactionDescription());
        assertEquals("q2", r.getStoppedAtQueryId());

        assertNotNull(r.getWhy());
        assertEquals("ORCH_EXEC_STOPPED", r.getWhy().getReason());
        assertEquals("execution stopped", r.getWhy().getDetails());

        assertNotNull(r.getQueryResults());
        assertEquals(2, r.getQueryResults().size());
        assertEquals("q1", r.getQueryResults().get(0).getQueryId());
        assertEquals("q2", r.getQueryResults().get(1).getQueryId());
    }

    @Test
    public void failure_shouldThrow_whenWhyCodeNull() {

        try {
            ExecutionResult.failure("wf1", "tx desc", "q1", null, "details", null);
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            Console.log("ExecutionResultTest.failure_shouldThrow_whenWhyCodeNull", e.getMessage());
            assertEquals("whyCode cannot be null", e.getMessage());
        }
    }

    @Test
    public void failure_shouldDefaultToEmptyList_whenPartialResultsNull() {

        ExecutionResult r = ExecutionResult.failure(
                "wf1",
                "tx desc",
                "q9",
                "ORCH_EXEC_STOPPED",
                "execution stopped",
                null
        );

        Console.log("ExecutionResultTest.failure_shouldDefaultToEmptyList_whenPartialResultsNull", r.toString());

        assertNotNull(r);
        assertFalse(r.isSuccess());
        assertNotNull(r.getQueryResults());
        assertEquals(0, r.getQueryResults().size());
    }

    @Test
    public void toString_shouldIncludeKeyFields() {

        List<QueryExecutionResult> partial = new ArrayList<QueryExecutionResult>();
        partial.add(QueryExecutionResult.executed("q1"));

        ExecutionResult r = ExecutionResult.failure(
                "wfX",
                "txY",
                "q1",
                "ORCH_EXEC_STOPPED",
                "stop",
                partial
        );

        String s = r.toString();

        Console.log("ExecutionResultTest.toString_shouldIncludeKeyFields", s);

        assertNotNull(s);
        assertTrue(s.contains("wfX"));
        assertTrue(s.contains("txY"));
        assertTrue(s.contains("q1"));
        assertTrue(s.contains("ORCH_EXEC_STOPPED"));
    }
}