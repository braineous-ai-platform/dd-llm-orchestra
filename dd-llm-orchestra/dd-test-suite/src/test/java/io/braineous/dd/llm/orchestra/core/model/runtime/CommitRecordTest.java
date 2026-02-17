package io.braineous.dd.llm.orchestra.core.model.runtime;

import ai.braineous.rag.prompt.observe.Console;
import io.braineous.dd.llm.orchestra.core.model.OrchestraBaseModel;

public class CommitRecordTest {

    @org.junit.jupiter.api.Test
    public void serialize_and_deserialize_roundTrip_preservesAllFields() {

        Console.log("UT", "create commit record");

        CommitRecord rec = new CommitRecord();
        rec.setId("commit-001");
        rec.setVersion("v1");
        rec.setCreatedAt("2026-02-16T21:40:00Z");
        rec.setUpdatedAt("2026-02-16T21:41:00Z");
        rec.setStatus("CREATED");
        rec.setCorrelationId("corr-commit-444");
        rec.setSnapshotHash("hash-commit-xyz");

        rec.setRequestId("req-001");
        rec.setWorkflowDefId("wf-001");
        rec.setWorkflowDefVersion("v1");
        rec.setStepDefId("step-003");
        rec.setStepDefVersion("v1");

        rec.setProposalId("proposal-001");

        rec.setCommitStatus("COMMITTED");
        rec.setCommitResultPayload("{\"result\":\"ok\",\"records\":1}");
        rec.setFailureCode(null);
        rec.setFailureMessage(null);
        rec.setCommittedAt("2026-02-16T21:42:00Z");

        Console.log("UT", "serialize to json");

        String json = rec.toJson();
        org.junit.jupiter.api.Assertions.assertNotNull(json);

        Console.log("UT", "deserialize from json");

        CommitRecord restored =
                OrchestraBaseModel.fromJson(json, CommitRecord.class);

        Console.log("UT", "assert roundtrip integrity");

        org.junit.jupiter.api.Assertions.assertEquals("commit-001", restored.getId());
        org.junit.jupiter.api.Assertions.assertEquals("v1", restored.getVersion());
        org.junit.jupiter.api.Assertions.assertEquals("CREATED", restored.getStatus());
        org.junit.jupiter.api.Assertions.assertEquals("corr-commit-444", restored.getCorrelationId());
        org.junit.jupiter.api.Assertions.assertEquals("hash-commit-xyz", restored.getSnapshotHash());

        org.junit.jupiter.api.Assertions.assertEquals("req-001", restored.getRequestId());
        org.junit.jupiter.api.Assertions.assertEquals("wf-001", restored.getWorkflowDefId());
        org.junit.jupiter.api.Assertions.assertEquals("v1", restored.getWorkflowDefVersion());
        org.junit.jupiter.api.Assertions.assertEquals("step-003", restored.getStepDefId());
        org.junit.jupiter.api.Assertions.assertEquals("v1", restored.getStepDefVersion());

        org.junit.jupiter.api.Assertions.assertEquals("proposal-001", restored.getProposalId());

        org.junit.jupiter.api.Assertions.assertEquals("COMMITTED", restored.getCommitStatus());
        org.junit.jupiter.api.Assertions.assertEquals("{\"result\":\"ok\",\"records\":1}", restored.getCommitResultPayload());
        org.junit.jupiter.api.Assertions.assertNull(restored.getFailureCode());
        org.junit.jupiter.api.Assertions.assertNull(restored.getFailureMessage());
        org.junit.jupiter.api.Assertions.assertEquals("2026-02-16T21:42:00Z", restored.getCommittedAt());

        Console.log("UT", "roundtrip test completed successfully");
    }
}
