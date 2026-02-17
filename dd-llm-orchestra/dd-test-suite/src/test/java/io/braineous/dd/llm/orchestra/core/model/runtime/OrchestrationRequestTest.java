package io.braineous.dd.llm.orchestra.core.model.runtime;

import ai.braineous.rag.prompt.observe.Console;
import io.braineous.dd.llm.orchestra.core.model.OrchestraBaseModel;

public class OrchestrationRequestTest {

    @org.junit.jupiter.api.Test
    public void serialize_and_deserialize_roundTrip_preservesAllFields() {

        Console.log("UT", "create orchestration request");

        OrchestrationRequest req = new OrchestrationRequest();
        req.setId("req-001");
        req.setVersion("v1");
        req.setCreatedAt("2026-02-16T20:50:00Z");
        req.setUpdatedAt("2026-02-16T20:51:00Z");
        req.setStatus("CREATED");
        req.setCorrelationId("corr-req-111");
        req.setSnapshotHash("hash-req-aaa");

        req.setWorkflowDefId("wf-001");
        req.setWorkflowDefVersion("v1");
        req.setInputPayload("{\"hello\":\"world\",\"count\":1}");

        java.util.Map<String, String> metadata = new java.util.HashMap<String, String>();
        metadata.put("source", "unit-test");
        metadata.put("tenant", "demo");
        req.setMetadata(metadata);

        Console.log("UT", "serialize to json");

        String json = req.toJson();
        org.junit.jupiter.api.Assertions.assertNotNull(json);

        Console.log("UT", "deserialize from json");

        OrchestrationRequest restored =
                OrchestraBaseModel.fromJson(json, OrchestrationRequest.class);

        Console.log("UT", "assert roundtrip integrity");

        org.junit.jupiter.api.Assertions.assertEquals("req-001", restored.getId());
        org.junit.jupiter.api.Assertions.assertEquals("v1", restored.getVersion());
        org.junit.jupiter.api.Assertions.assertEquals("CREATED", restored.getStatus());
        org.junit.jupiter.api.Assertions.assertEquals("corr-req-111", restored.getCorrelationId());
        org.junit.jupiter.api.Assertions.assertEquals("hash-req-aaa", restored.getSnapshotHash());

        org.junit.jupiter.api.Assertions.assertEquals("wf-001", restored.getWorkflowDefId());
        org.junit.jupiter.api.Assertions.assertEquals("v1", restored.getWorkflowDefVersion());
        org.junit.jupiter.api.Assertions.assertEquals("{\"hello\":\"world\",\"count\":1}", restored.getInputPayload());

        org.junit.jupiter.api.Assertions.assertEquals(2, restored.getMetadata().size());
        org.junit.jupiter.api.Assertions.assertEquals("unit-test", restored.getMetadata().get("source"));
        org.junit.jupiter.api.Assertions.assertEquals("demo", restored.getMetadata().get("tenant"));

        Console.log("UT", "roundtrip test completed successfully");
    }
}

