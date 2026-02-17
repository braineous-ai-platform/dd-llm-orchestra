package io.braineous.dd.llm.orchestra.core.model.def;

import ai.braineous.rag.prompt.observe.Console;
import io.braineous.dd.llm.orchestra.core.model.OrchestraBaseModel;

public class StepDefTest {

    @org.junit.jupiter.api.Test
    public void serialize_and_deserialize_roundTrip_preservesAllFields() {

        Console.log("UT", "create step definition");

        StepDef def = new StepDef();
        def.setId("step-001");
        def.setVersion("v1");
        def.setCreatedAt("2026-02-16T20:10:00Z");
        def.setUpdatedAt("2026-02-16T20:12:00Z");
        def.setStatus("CREATED");
        def.setCorrelationId("corr-456");
        def.setSnapshotHash("hash-step-xyz");

        def.setStepKind("PROPOSE");
        def.setName("Propose Actions");
        def.setDescription("LLM proposes structured actions for this step");
        def.setOrderIndex(Integer.valueOf(1));

        def.setPolicyDefId("policy-none");
        def.setScoringDefId("scoring-1");

        java.util.List<String> tools = new java.util.ArrayList<String>();
        tools.add("tool-A");
        tools.add("tool-B");
        def.setToolDefIds(tools);

        Console.log("UT", "serialize to json");

        String json = def.toJson();

        org.junit.jupiter.api.Assertions.assertNotNull(json);

        Console.log("UT", "deserialize from json");

        StepDef restored =
                OrchestraBaseModel.fromJson(json, StepDef.class);

        Console.log("UT", "assert roundtrip integrity");

        org.junit.jupiter.api.Assertions.assertEquals("step-001", restored.getId());
        org.junit.jupiter.api.Assertions.assertEquals("v1", restored.getVersion());
        org.junit.jupiter.api.Assertions.assertEquals("CREATED", restored.getStatus());
        org.junit.jupiter.api.Assertions.assertEquals("corr-456", restored.getCorrelationId());
        org.junit.jupiter.api.Assertions.assertEquals("hash-step-xyz", restored.getSnapshotHash());

        org.junit.jupiter.api.Assertions.assertEquals("PROPOSE", restored.getStepKind());
        org.junit.jupiter.api.Assertions.assertEquals("Propose Actions", restored.getName());
        org.junit.jupiter.api.Assertions.assertEquals("LLM proposes structured actions for this step", restored.getDescription());
        org.junit.jupiter.api.Assertions.assertEquals(Integer.valueOf(1), restored.getOrderIndex());

        org.junit.jupiter.api.Assertions.assertEquals("policy-none", restored.getPolicyDefId());
        org.junit.jupiter.api.Assertions.assertEquals("scoring-1", restored.getScoringDefId());

        org.junit.jupiter.api.Assertions.assertEquals(2, restored.getToolDefIds().size());
        org.junit.jupiter.api.Assertions.assertEquals("tool-A", restored.getToolDefIds().get(0));
        org.junit.jupiter.api.Assertions.assertEquals("tool-B", restored.getToolDefIds().get(1));

        Console.log("UT", "roundtrip test completed successfully");
    }
}
