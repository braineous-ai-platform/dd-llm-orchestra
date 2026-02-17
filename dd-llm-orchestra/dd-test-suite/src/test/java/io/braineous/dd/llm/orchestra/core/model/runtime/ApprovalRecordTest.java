package io.braineous.dd.llm.orchestra.core.model.runtime;

import ai.braineous.rag.prompt.observe.Console;
import io.braineous.dd.llm.orchestra.core.model.OrchestraBaseModel;

public class ApprovalRecordTest {

    @org.junit.jupiter.api.Test
    public void serialize_and_deserialize_roundTrip_preservesAllFields() {

        Console.log("UT", "create approval record");

        ApprovalRecord rec = new ApprovalRecord();
        rec.setId("approval-001");
        rec.setVersion("v1");
        rec.setCreatedAt("2026-02-16T21:30:00Z");
        rec.setUpdatedAt("2026-02-16T21:31:00Z");
        rec.setStatus("CREATED");
        rec.setCorrelationId("corr-approve-333");
        rec.setSnapshotHash("hash-approve-xyz");

        rec.setRequestId("req-001");
        rec.setWorkflowDefId("wf-001");
        rec.setWorkflowDefVersion("v1");
        rec.setStepDefId("step-002");
        rec.setStepDefVersion("v1");

        rec.setProposalId("proposal-001");
        rec.setPolicyDefId("policy-001");
        rec.setPolicyDefVersion("v1");

        rec.setApprovalStatus("APPROVED");
        rec.setApprovedBy("user:sohil");
        rec.setApprovedAt("2026-02-16T21:32:00Z");
        rec.setRationale("Threshold met, risk acceptable");

        Console.log("UT", "serialize to json");

        String json = rec.toJson();
        org.junit.jupiter.api.Assertions.assertNotNull(json);

        Console.log("UT", "deserialize from json");

        ApprovalRecord restored =
                OrchestraBaseModel.fromJson(json, ApprovalRecord.class);

        Console.log("UT", "assert roundtrip integrity");

        org.junit.jupiter.api.Assertions.assertEquals("approval-001", restored.getId());
        org.junit.jupiter.api.Assertions.assertEquals("v1", restored.getVersion());
        org.junit.jupiter.api.Assertions.assertEquals("CREATED", restored.getStatus());
        org.junit.jupiter.api.Assertions.assertEquals("corr-approve-333", restored.getCorrelationId());
        org.junit.jupiter.api.Assertions.assertEquals("hash-approve-xyz", restored.getSnapshotHash());

        org.junit.jupiter.api.Assertions.assertEquals("req-001", restored.getRequestId());
        org.junit.jupiter.api.Assertions.assertEquals("wf-001", restored.getWorkflowDefId());
        org.junit.jupiter.api.Assertions.assertEquals("v1", restored.getWorkflowDefVersion());
        org.junit.jupiter.api.Assertions.assertEquals("step-002", restored.getStepDefId());
        org.junit.jupiter.api.Assertions.assertEquals("v1", restored.getStepDefVersion());

        org.junit.jupiter.api.Assertions.assertEquals("proposal-001", restored.getProposalId());
        org.junit.jupiter.api.Assertions.assertEquals("policy-001", restored.getPolicyDefId());
        org.junit.jupiter.api.Assertions.assertEquals("v1", restored.getPolicyDefVersion());

        org.junit.jupiter.api.Assertions.assertEquals("APPROVED", restored.getApprovalStatus());
        org.junit.jupiter.api.Assertions.assertEquals("user:sohil", restored.getApprovedBy());
        org.junit.jupiter.api.Assertions.assertEquals("2026-02-16T21:32:00Z", restored.getApprovedAt());
        org.junit.jupiter.api.Assertions.assertEquals("Threshold met, risk acceptable", restored.getRationale());

        Console.log("UT", "roundtrip test completed successfully");
    }
}

