package io.braineous.dd.llm.orchestra.core.model.def;

import ai.braineous.rag.prompt.observe.Console;
import io.braineous.dd.llm.orchestra.core.model.OrchestraBaseModel;

public class WorkflowDefTest {

    @org.junit.jupiter.api.Test
    public void serialize_and_deserialize_roundTrip_preservesAllFields() {

        Console.log("UT", "create workflow definition");

        WorkflowDef def = new WorkflowDef();
        def.setId("wf-001");
        def.setVersion("v1");
        def.setCreatedAt("2026-02-16T20:00:00Z");
        def.setUpdatedAt("2026-02-16T20:05:00Z");
        def.setStatus("CREATED");
        def.setCorrelationId("corr-123");
        def.setSnapshotHash("hash-abc");

        def.setName("Sample Workflow");
        def.setDescription("V0 deterministic workflow");

        java.util.List<String> steps = new java.util.ArrayList<String>();
        steps.add("step-1");
        steps.add("step-2");
        def.setStepDefIds(steps);

        def.setPolicyDefId("policy-1");
        def.setScoringDefId("scoring-1");

        java.util.List<String> tools = new java.util.ArrayList<String>();
        tools.add("tool-A");
        tools.add("tool-B");
        def.setToolDefIds(tools);

        Console.log("UT", "serialize to json");

        String json = def.toJson();

        org.junit.jupiter.api.Assertions.assertNotNull(json);

        Console.log("UT", "deserialize from json");

        WorkflowDef restored =
                OrchestraBaseModel.fromJson(json, WorkflowDef.class);

        Console.log("UT", "assert roundtrip integrity");

        org.junit.jupiter.api.Assertions.assertEquals("wf-001", restored.getId());
        org.junit.jupiter.api.Assertions.assertEquals("v1", restored.getVersion());
        org.junit.jupiter.api.Assertions.assertEquals("CREATED", restored.getStatus());
        org.junit.jupiter.api.Assertions.assertEquals("corr-123", restored.getCorrelationId());
        org.junit.jupiter.api.Assertions.assertEquals("hash-abc", restored.getSnapshotHash());

        org.junit.jupiter.api.Assertions.assertEquals("Sample Workflow", restored.getName());
        org.junit.jupiter.api.Assertions.assertEquals("V0 deterministic workflow", restored.getDescription());

        org.junit.jupiter.api.Assertions.assertEquals(2, restored.getStepDefIds().size());
        org.junit.jupiter.api.Assertions.assertEquals("step-1", restored.getStepDefIds().get(0));
        org.junit.jupiter.api.Assertions.assertEquals("step-2", restored.getStepDefIds().get(1));

        org.junit.jupiter.api.Assertions.assertEquals("policy-1", restored.getPolicyDefId());
        org.junit.jupiter.api.Assertions.assertEquals("scoring-1", restored.getScoringDefId());

        org.junit.jupiter.api.Assertions.assertEquals(2, restored.getToolDefIds().size());
        org.junit.jupiter.api.Assertions.assertEquals("tool-A", restored.getToolDefIds().get(0));
        org.junit.jupiter.api.Assertions.assertEquals("tool-B", restored.getToolDefIds().get(1));

        Console.log("UT", "roundtrip test completed successfully");
    }
}

