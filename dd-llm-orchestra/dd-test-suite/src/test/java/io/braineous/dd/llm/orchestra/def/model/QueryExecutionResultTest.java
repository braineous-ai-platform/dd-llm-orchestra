package io.braineous.dd.llm.orchestra.def.model;

import ai.braineous.rag.prompt.observe.Console;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class QueryExecutionResultTest {

    @Test
    public void executed_shouldCreateSuccessResult() {

        QueryExecutionResult r = QueryExecutionResult.executed("q1");

        Console.log("QueryExecutionResultTest.executed_shouldCreateSuccessResult", r.toString());

        assertNotNull(r);
        assertEquals("q1", r.getQueryId());
        assertTrue(r.isSuccess());
        assertEquals("EXECUTED", r.getStatus());
        assertNull(r.getWhy());
    }

    @Test
    public void executed_shouldThrow_whenQueryIdNull() {

        try {
            QueryExecutionResult.executed(null);
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            Console.log("QueryExecutionResultTest.executed_shouldThrow_whenQueryIdNull", e.getMessage());
            assertEquals("queryId cannot be null", e.getMessage());
        }
    }

    @Test
    public void blocked_shouldCreateFailureResult_withWhy() {

        QueryExecutionResult r = QueryExecutionResult.blocked(
                "q1",
                "NEEDS_APPROVAL",
                "WHY_APPROVAL_REQUIRED",
                "requires HITL approval"
        );

        Console.log("QueryExecutionResultTest.blocked_shouldCreateFailureResult_withWhy", r.toString());

        assertNotNull(r);
        assertEquals("q1", r.getQueryId());
        assertFalse(r.isSuccess());
        assertEquals("NEEDS_APPROVAL", r.getStatus());

        assertNotNull(r.getWhy());
        assertEquals("WHY_APPROVAL_REQUIRED", r.getWhy().getReason());
        assertEquals("requires HITL approval", r.getWhy().getDetails());
    }

    @Test
    public void blocked_shouldThrow_whenQueryIdNull() {

        try {
            QueryExecutionResult.blocked(null, "ERROR", "WHY", "details");
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            Console.log("QueryExecutionResultTest.blocked_shouldThrow_whenQueryIdNull", e.getMessage());
            assertEquals("queryId cannot be null", e.getMessage());
        }
    }

    @Test
    public void blocked_shouldThrow_whenStatusNull() {

        try {
            QueryExecutionResult.blocked("q1", null, "WHY", "details");
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            Console.log("QueryExecutionResultTest.blocked_shouldThrow_whenStatusNull", e.getMessage());
            assertEquals("status cannot be null", e.getMessage());
        }
    }

    @Test
    public void blocked_shouldThrow_whenWhyCodeNull() {

        try {
            QueryExecutionResult.blocked("q1", "ERROR", null, "details");
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            Console.log("QueryExecutionResultTest.blocked_shouldThrow_whenWhyCodeNull", e.getMessage());
            assertEquals("whyCode cannot be null", e.getMessage());
        }
    }

    @Test
    public void toString_shouldIncludeKeyFields() {

        QueryExecutionResult r = QueryExecutionResult.blocked("q9", "DENIED", "WHY_DENIED", "nope");

        String s = r.toString();

        Console.log("QueryExecutionResultTest.toString_shouldIncludeKeyFields", s);

        assertNotNull(s);
        assertTrue(s.contains("q9"));
        assertTrue(s.contains("DENIED"));
        assertTrue(s.contains("WHY_DENIED"));
    }
}