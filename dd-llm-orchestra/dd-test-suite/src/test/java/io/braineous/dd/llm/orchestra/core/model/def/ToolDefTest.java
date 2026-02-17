package io.braineous.dd.llm.orchestra.core.model.def;

import ai.braineous.rag.prompt.observe.Console;
import io.braineous.dd.llm.orchestra.core.model.OrchestraBaseModel;

public class ToolDefTest {

    @org.junit.jupiter.api.Test
    public void serialize_and_deserialize_roundTrip_preservesAllFields() {

        Console.log("UT", "create tool definition");

        ToolDef def = new ToolDef();
        def.setId("tool-001");
        def.setVersion("v1");
        def.setCreatedAt("2026-02-16T20:20:00Z");
        def.setUpdatedAt("2026-02-16T20:21:00Z");
        def.setStatus("CREATED");
        def.setCorrelationId("corr-789");
        def.setSnapshotHash("hash-tool-123");

        def.setToolKey("HTTP_GET");
        def.setName("HTTP GET");
        def.setDescription("Performs an outbound HTTP GET request");
        def.setInputSchemaRef("schema:tool/http_get/input:v1");
        def.setOutputSchemaRef("schema:tool/http_get/output:v1");
        def.setRiskLevel("LOW");

        Console.log("UT", "serialize to json");

        String json = def.toJson();
        org.junit.jupiter.api.Assertions.assertNotNull(json);

        Console.log("UT", "deserialize from json");

        ToolDef restored =
                OrchestraBaseModel.fromJson(json, ToolDef.class);

        Console.log("UT", "assert roundtrip integrity");

        org.junit.jupiter.api.Assertions.assertEquals("tool-001", restored.getId());
        org.junit.jupiter.api.Assertions.assertEquals("v1", restored.getVersion());
        org.junit.jupiter.api.Assertions.assertEquals("CREATED", restored.getStatus());
        org.junit.jupiter.api.Assertions.assertEquals("corr-789", restored.getCorrelationId());
        org.junit.jupiter.api.Assertions.assertEquals("hash-tool-123", restored.getSnapshotHash());

        org.junit.jupiter.api.Assertions.assertEquals("HTTP_GET", restored.getToolKey());
        org.junit.jupiter.api.Assertions.assertEquals("HTTP GET", restored.getName());
        org.junit.jupiter.api.Assertions.assertEquals("Performs an outbound HTTP GET request", restored.getDescription());
        org.junit.jupiter.api.Assertions.assertEquals("schema:tool/http_get/input:v1", restored.getInputSchemaRef());
        org.junit.jupiter.api.Assertions.assertEquals("schema:tool/http_get/output:v1", restored.getOutputSchemaRef());
        org.junit.jupiter.api.Assertions.assertEquals("LOW", restored.getRiskLevel());

        Console.log("UT", "roundtrip test completed successfully");
    }
}
