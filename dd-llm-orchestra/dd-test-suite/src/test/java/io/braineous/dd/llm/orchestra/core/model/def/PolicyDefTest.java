package io.braineous.dd.llm.orchestra.core.model.def;

import ai.braineous.rag.prompt.observe.Console;
import io.braineous.dd.llm.orchestra.core.model.OrchestraBaseModel;

public class PolicyDefTest {

    @org.junit.jupiter.api.Test
    public void serialize_and_deserialize_roundTrip_preservesAllFields() {

        Console.log("UT", "create policy definition");

        PolicyDef def = new PolicyDef();
        def.setId("policy-001");
        def.setVersion("v1");
        def.setCreatedAt("2026-02-16T20:30:00Z");
        def.setUpdatedAt("2026-02-16T20:31:00Z");
        def.setStatus("CREATED");
        def.setCorrelationId("corr-999");
        def.setSnapshotHash("hash-policy-abc");

        def.setPolicyKey("DEFAULT_APPROVAL");
        def.setApprovalMode("THRESHOLD");
        def.setMinScoreThreshold(Double.valueOf(0.80d));
        def.setMaxAllowedRiskLevel("MEDIUM");

        java.util.List<String> roles = new java.util.ArrayList<String>();
        roles.add("SECURITY_REVIEW");
        roles.add("DATA_OWNER");
        def.setRequiredApproverRoles(roles);

        Console.log("UT", "serialize to json");

        String json = def.toJson();
        org.junit.jupiter.api.Assertions.assertNotNull(json);

        Console.log("UT", "deserialize from json");

        PolicyDef restored =
                OrchestraBaseModel.fromJson(json, PolicyDef.class);

        Console.log("UT", "assert roundtrip integrity");

        org.junit.jupiter.api.Assertions.assertEquals("policy-001", restored.getId());
        org.junit.jupiter.api.Assertions.assertEquals("v1", restored.getVersion());
        org.junit.jupiter.api.Assertions.assertEquals("CREATED", restored.getStatus());
        org.junit.jupiter.api.Assertions.assertEquals("corr-999", restored.getCorrelationId());
        org.junit.jupiter.api.Assertions.assertEquals("hash-policy-abc", restored.getSnapshotHash());

        org.junit.jupiter.api.Assertions.assertEquals("DEFAULT_APPROVAL", restored.getPolicyKey());
        org.junit.jupiter.api.Assertions.assertEquals("THRESHOLD", restored.getApprovalMode());
        org.junit.jupiter.api.Assertions.assertEquals(Double.valueOf(0.80d), restored.getMinScoreThreshold());
        org.junit.jupiter.api.Assertions.assertEquals("MEDIUM", restored.getMaxAllowedRiskLevel());

        org.junit.jupiter.api.Assertions.assertEquals(2, restored.getRequiredApproverRoles().size());
        org.junit.jupiter.api.Assertions.assertEquals("SECURITY_REVIEW", restored.getRequiredApproverRoles().get(0));
        org.junit.jupiter.api.Assertions.assertEquals("DATA_OWNER", restored.getRequiredApproverRoles().get(1));

        Console.log("UT", "roundtrip test completed successfully");
    }
}
