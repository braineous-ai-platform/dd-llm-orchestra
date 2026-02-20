package io.braineous.dd.llm.orchestra.def.model;

import ai.braineous.rag.prompt.observe.Console;
import io.braineous.dd.llm.orchestra.core.model.OrchestraBaseModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class WorkflowTest {

    private static final String DATASET_JSON =
            "{\n" +
                    "  \"name\": \"fno_rebook_v1\",\n" +
                    "  \"description\": \"Rebook disrupted passengers\",\n" +
                    "  \"transaction\": {\n" +
                    "    \"description\": \"Agentic flow: gather → decide → draft. Commit after HITL approval.\",\n" +
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
                    "      },\n" +
                    "      {\n" +
                    "        \"id\": \"q3_customer_message\",\n" +
                    "        \"description\": \"Draft customer message.\",\n" +
                    "        \"sql\": \"select decision from llm where task = \\\"Draft customer message\\\" and factId = \\\"cgo:pnr:123\\\" and relatedFacts = \\\"cgo:[f4,f5]\\\"\"\n" +
                    "      }\n" +
                    "    ],\n" +
                    "    \"commitOrder\": [\"q1_fetch_options\", \"q2_rank_and_pick\", \"q3_customer_message\"]\n" +
                    "  }\n" +
                    "}";

    @Test
    public void fromJson_shouldParseWorkflow_nameAndDescription_only() {
        Console.log("WorkflowTest.fromJson_shouldParseWorkflow_nameAndDescription_only", "START");

        Workflow workflow = OrchestraBaseModel.fromJson(DATASET_JSON, Workflow.class);

        Assertions.assertNotNull(workflow, "workflow must not be null");
        Assertions.assertEquals("fno_rebook_v1", workflow.getName(), "name must match dataset");
        Assertions.assertEquals("Rebook disrupted passengers", workflow.getDescription(), "description must match dataset");

        Console.log("WorkflowTest.fromJson_shouldParseWorkflow_nameAndDescription_only", "OK");
    }

    @Test
    public void toJson_shouldRoundTrip_nameAndDescription() {
        Console.log("WorkflowTest.toJson_shouldRoundTrip_nameAndDescription", "START");

        Workflow workflow = new Workflow();
        workflow.setName("fno_rebook_v1");
        workflow.setDescription("Rebook disrupted passengers");

        String json = workflow.toJson();
        Assertions.assertNotNull(json, "json must not be null");
        Assertions.assertFalse(json.trim().isEmpty(), "json must not be blank");

        Workflow parsed = OrchestraBaseModel.fromJson(json, Workflow.class);

        Assertions.assertNotNull(parsed, "parsed workflow must not be null");
        Assertions.assertEquals("fno_rebook_v1", parsed.getName(), "name must round-trip");
        Assertions.assertEquals("Rebook disrupted passengers", parsed.getDescription(), "description must round-trip");

        Console.log("WorkflowTest.toJson_shouldRoundTrip_nameAndDescription", "OK");
    }

    @Test
    public void fromJson_shouldThrow_whenJsonNull() {
        Console.log("WorkflowTest.fromJson_shouldThrow_whenJsonNull", "START");

        IllegalArgumentException ex = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> OrchestraBaseModel.fromJson(null, Workflow.class)
        );

        Assertions.assertEquals("json cannot be null", ex.getMessage());

        Console.log("WorkflowTest.fromJson_shouldThrow_whenJsonNull", "OK");
    }

    @Test
    public void fromJson_shouldThrow_whenJsonBlank() {
        Console.log("WorkflowTest.fromJson_shouldThrow_whenJsonBlank", "START");

        IllegalArgumentException ex = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> OrchestraBaseModel.fromJson("   ", Workflow.class)
        );

        Assertions.assertEquals("json cannot be blank", ex.getMessage());

        Console.log("WorkflowTest.fromJson_shouldThrow_whenJsonBlank", "OK");
    }

    @Test
    public void fromJson_shouldThrow_whenTypeNull() {
        Console.log("WorkflowTest.fromJson_shouldThrow_whenTypeNull", "START");

        IllegalArgumentException ex = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> OrchestraBaseModel.fromJson(DATASET_JSON, null)
        );

        Assertions.assertEquals("type cannot be null", ex.getMessage());

        Console.log("WorkflowTest.fromJson_shouldThrow_whenTypeNull", "OK");
    }
}