package io.braineous.dd.llm.orchestra.def.service;

import ai.braineous.rag.prompt.observe.Console;
import io.braineous.dd.llm.orchestra.def.model.PublishResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class WorkflowCompilerTest {

    private static final String VALID_JSON =
            "{\n" +
                    "  \"name\": \"fno_rebook_v1\",\n" +
                    "  \"description\": \"Rebook disrupted passengers\",\n" +
                    "  \"transaction\": {\n" +
                    "    \"description\": \"Agentic flow\",\n" +
                    "    \"queries\": [\n" +
                    "      {\n" +
                    "        \"id\": \"q1_fetch_options\",\n" +
                    "        \"description\": \"Gather viable rebooking options.\",\n" +
                    "        \"sql\": \"select decision from llm where task = \\\"Fetch viable rebooking options\\\" and factId = \\\"cgo:pnr:123\\\" and relatedFacts = \\\"cgo:[f1,f2]\\\"\"\n" +
                    "      },\n" +
                    "      {\n" +
                    "        \"id\": \"q2_rank_and_pick\",\n" +
                    "        \"description\": \"Rank options and pick best.\",\n" +
                    "        \"sql\": \"select decision from llm where task = \\\"Rank options and pick best\\\" and factId = \\\"cgo:pnr:123\\\" and relatedFacts = \\\"cgo:[f1,f2,f3]\\\"\"\n" +
                    "      }\n" +
                    "    ],\n" +
                    "    \"commitOrder\": [\"q1_fetch_options\", \"q2_rank_and_pick\"]\n" +
                    "  }\n" +
                    "}";

    private static final String NO_TX_JSON =
            "{\n" +
                    "  \"name\": \"wf_no_tx\",\n" +
                    "  \"description\": \"No transaction\"\n" +
                    "}";

    private static final String NULL_QUERIES_JSON =
            "{\n" +
                    "  \"name\": \"wf_null_queries\",\n" +
                    "  \"description\": \"Null queries\",\n" +
                    "  \"transaction\": {\n" +
                    "    \"description\": \"Agentic flow\",\n" +
                    "    \"queries\": null,\n" +
                    "    \"commitOrder\": []\n" +
                    "  }\n" +
                    "}";

    private static final String EMPTY_QUERIES_JSON =
            "{\n" +
                    "  \"name\": \"wf_empty_queries\",\n" +
                    "  \"description\": \"Empty queries\",\n" +
                    "  \"transaction\": {\n" +
                    "    \"description\": \"Agentic flow\",\n" +
                    "    \"queries\": [],\n" +
                    "    \"commitOrder\": []\n" +
                    "  }\n" +
                    "}";

    private static final String NULL_QUERY_ELEMENT_JSON =
            "{\n" +
                    "  \"name\": \"wf_null_query_element\",\n" +
                    "  \"description\": \"Null query element\",\n" +
                    "  \"transaction\": {\n" +
                    "    \"description\": \"Agentic flow\",\n" +
                    "    \"queries\": [ null ],\n" +
                    "    \"commitOrder\": []\n" +
                    "  }\n" +
                    "}";

    private static final String NULL_QUERY_ID_JSON =
            "{\n" +
                    "  \"name\": \"wf_null_query_id\",\n" +
                    "  \"description\": \"Null query id\",\n" +
                    "  \"transaction\": {\n" +
                    "    \"description\": \"Agentic flow\",\n" +
                    "    \"queries\": [ { \"id\": null, \"description\": \"x\", \"sql\": \"select 1\" } ],\n" +
                    "    \"commitOrder\": []\n" +
                    "  }\n" +
                    "}";

    private static final String BLANK_QUERY_ID_JSON =
            "{\n" +
                    "  \"name\": \"wf_blank_query_id\",\n" +
                    "  \"description\": \"Blank query id\",\n" +
                    "  \"transaction\": {\n" +
                    "    \"description\": \"Agentic flow\",\n" +
                    "    \"queries\": [ { \"id\": \"   \", \"description\": \"x\", \"sql\": \"select 1\" } ],\n" +
                    "    \"commitOrder\": []\n" +
                    "  }\n" +
                    "}";

    private static final String DUP_QUERY_ID_JSON =
            "{\n" +
                    "  \"name\": \"wf_dup_query_id\",\n" +
                    "  \"description\": \"Duplicate query ids\",\n" +
                    "  \"transaction\": {\n" +
                    "    \"description\": \"Agentic flow\",\n" +
                    "    \"queries\": [\n" +
                    "      { \"id\": \"q1\", \"description\": \"x\", \"sql\": \"select 1\" },\n" +
                    "      { \"id\": \" q1 \", \"description\": \"y\", \"sql\": \"select 2\" }\n" +
                    "    ],\n" +
                    "    \"commitOrder\": []\n" +
                    "  }\n" +
                    "}";

    private static final String BLANK_NAME_JSON =
            "{\n" +
                    "  \"name\": \"   \",\n" +
                    "  \"description\": \"Blank name\"\n" +
                    "}";

    private static final String NULL_NAME_JSON =
            "{\n" +
                    "  \"name\": null,\n" +
                    "  \"description\": \"Null name\"\n" +
                    "}";

    // ---------------------------------------------------------

    @Test
    public void compile_shouldSucceed_whenValid() {
        Console.log("WorkflowCompilerTest.compile_shouldSucceed_whenValid", "START");

        WorkflowCompiler compiler = new WorkflowCompiler();

        PublishResult r = compiler.compile(VALID_JSON);

        Assertions.assertNotNull(r);
        Assertions.assertTrue(r.isSuccess());
        Assertions.assertNull(r.getWhy());
        Assertions.assertEquals("fno_rebook_v1", r.getEngineWorkflowName());
        Assertions.assertEquals(Integer.valueOf(1), r.getEngineWorkflowVersion());

        Console.log("WorkflowCompilerTest.compile_shouldSucceed_whenValid", "OK");
    }

    @Test
    public void compile_shouldSucceed_whenTransactionMissing() {
        Console.log("WorkflowCompilerTest.compile_shouldSucceed_whenTransactionMissing", "START");

        WorkflowCompiler compiler = new WorkflowCompiler();

        PublishResult r = compiler.compile(NO_TX_JSON);

        Assertions.assertNotNull(r);
        Assertions.assertTrue(r.isSuccess());
        Assertions.assertNull(r.getWhy());
        Assertions.assertEquals("wf_no_tx", r.getEngineWorkflowName());
        Assertions.assertEquals(Integer.valueOf(1), r.getEngineWorkflowVersion());

        Console.log("WorkflowCompilerTest.compile_shouldSucceed_whenTransactionMissing", "OK");
    }

    @Test
    public void compile_shouldFail_whenJsonNull() {
        Console.log("WorkflowCompilerTest.compile_shouldFail_whenJsonNull", "START");

        WorkflowCompiler compiler = new WorkflowCompiler();

        PublishResult r = compiler.compile(null);

        assertInvalid(r);

        Console.log("WorkflowCompilerTest.compile_shouldFail_whenJsonNull", "OK");
    }

    @Test
    public void compile_shouldFail_whenJsonBlank() {
        Console.log("WorkflowCompilerTest.compile_shouldFail_whenJsonBlank", "START");

        WorkflowCompiler compiler = new WorkflowCompiler();

        PublishResult r = compiler.compile("   ");

        assertInvalid(r);

        Console.log("WorkflowCompilerTest.compile_shouldFail_whenJsonBlank", "OK");
    }

    @Test
    public void compile_shouldFail_whenJsonNullLiteral() {
        Console.log("WorkflowCompilerTest.compile_shouldFail_whenJsonNullLiteral", "START");

        WorkflowCompiler compiler = new WorkflowCompiler();

        PublishResult r = compiler.compile("null");

        assertInvalid(r);

        Console.log("WorkflowCompilerTest.compile_shouldFail_whenJsonNullLiteral", "OK");
    }

    @Test
    public void compile_shouldFail_whenWorkflowNameNull() {
        Console.log("WorkflowCompilerTest.compile_shouldFail_whenWorkflowNameNull", "START");

        WorkflowCompiler compiler = new WorkflowCompiler();

        PublishResult r = compiler.compile(NULL_NAME_JSON);

        assertInvalid(r);

        Console.log("WorkflowCompilerTest.compile_shouldFail_whenWorkflowNameNull", "OK");
    }

    @Test
    public void compile_shouldFail_whenWorkflowNameBlank() {
        Console.log("WorkflowCompilerTest.compile_shouldFail_whenWorkflowNameBlank", "START");

        WorkflowCompiler compiler = new WorkflowCompiler();

        PublishResult r = compiler.compile(BLANK_NAME_JSON);

        assertInvalid(r);

        Console.log("WorkflowCompilerTest.compile_shouldFail_whenWorkflowNameBlank", "OK");
    }

    @Test
    public void compile_shouldFail_whenQueriesNull() {
        Console.log("WorkflowCompilerTest.compile_shouldFail_whenQueriesNull", "START");

        WorkflowCompiler compiler = new WorkflowCompiler();

        PublishResult r = compiler.compile(NULL_QUERIES_JSON);

        assertInvalid(r);

        Console.log("WorkflowCompilerTest.compile_shouldFail_whenQueriesNull", "OK");
    }

    @Test
    public void compile_shouldFail_whenQueriesEmpty() {
        Console.log("WorkflowCompilerTest.compile_shouldFail_whenQueriesEmpty", "START");

        WorkflowCompiler compiler = new WorkflowCompiler();

        PublishResult r = compiler.compile(EMPTY_QUERIES_JSON);

        assertInvalid(r);

        Console.log("WorkflowCompilerTest.compile_shouldFail_whenQueriesEmpty", "OK");
    }

    @Test
    public void compile_shouldFail_whenQueryElementNull() {
        Console.log("WorkflowCompilerTest.compile_shouldFail_whenQueryElementNull", "START");

        WorkflowCompiler compiler = new WorkflowCompiler();

        PublishResult r = compiler.compile(NULL_QUERY_ELEMENT_JSON);

        assertInvalid(r);

        Console.log("WorkflowCompilerTest.compile_shouldFail_whenQueryElementNull", "OK");
    }

    @Test
    public void compile_shouldFail_whenQueryIdNull() {
        Console.log("WorkflowCompilerTest.compile_shouldFail_whenQueryIdNull", "START");

        WorkflowCompiler compiler = new WorkflowCompiler();

        PublishResult r = compiler.compile(NULL_QUERY_ID_JSON);

        assertInvalid(r);

        Console.log("WorkflowCompilerTest.compile_shouldFail_whenQueryIdNull", "OK");
    }

    @Test
    public void compile_shouldFail_whenQueryIdBlank() {
        Console.log("WorkflowCompilerTest.compile_shouldFail_whenQueryIdBlank", "START");

        WorkflowCompiler compiler = new WorkflowCompiler();

        PublishResult r = compiler.compile(BLANK_QUERY_ID_JSON);

        assertInvalid(r);

        Console.log("WorkflowCompilerTest.compile_shouldFail_whenQueryIdBlank", "OK");
    }

    @Test
    public void compile_shouldFail_whenQueryIdsDuplicate_afterTrim() {
        Console.log("WorkflowCompilerTest.compile_shouldFail_whenQueryIdsDuplicate_afterTrim", "START");

        WorkflowCompiler compiler = new WorkflowCompiler();

        PublishResult r = compiler.compile(DUP_QUERY_ID_JSON);

        assertInvalid(r);

        Console.log("WorkflowCompilerTest.compile_shouldFail_whenQueryIdsDuplicate_afterTrim", "OK");
    }

    // ---------------------------------------------------------

    private static void assertInvalid(PublishResult r) {
        Assertions.assertNotNull(r);
        Assertions.assertFalse(r.isSuccess());
        Assertions.assertNotNull(r.getWhy());
        Assertions.assertEquals("WF_INVALID", r.getWhy().getReason());
    }
}