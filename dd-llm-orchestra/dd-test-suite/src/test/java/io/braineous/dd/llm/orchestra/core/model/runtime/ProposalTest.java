package io.braineous.dd.llm.orchestra.core.model.runtime;

import ai.braineous.rag.prompt.observe.Console;
import io.braineous.dd.llm.orchestra.core.model.OrchestraBaseModel;

public class ProposalTest {

    @org.junit.jupiter.api.Test
    public void serialize_and_deserialize_roundTrip_preservesAllFields() {

        Console.log("UT", "create proposal");

        Proposal proposal = new Proposal();
        proposal.setId("proposal-001");
        proposal.setVersion("v1");
        proposal.setCreatedAt("2026-02-16T21:00:00Z");
        proposal.setUpdatedAt("2026-02-16T21:01:00Z");
        proposal.setStatus("PROPOSED");
        proposal.setCorrelationId("corr-prop-123");
        proposal.setSnapshotHash("hash-proposal-xyz");

        proposal.setRequestId("req-001");
        proposal.setWorkflowDefId("wf-001");
        proposal.setWorkflowDefVersion("v1");
        proposal.setStepDefId("step-001");
        proposal.setStepDefVersion("v1");

        proposal.setProposalPayload("{\"actions\":[{\"tool\":\"HTTP_GET\",\"url\":\"https://example.com\"}]}");
        proposal.setModel("gpt-5.2");
        proposal.setPromptVersion("prompt-v1");

        Console.log("UT", "serialize to json");

        String json = proposal.toJson();
        org.junit.jupiter.api.Assertions.assertNotNull(json);

        Console.log("UT", "deserialize from json");

        Proposal restored =
                OrchestraBaseModel.fromJson(json, Proposal.class);

        Console.log("UT", "assert roundtrip integrity");

        org.junit.jupiter.api.Assertions.assertEquals("proposal-001", restored.getId());
        org.junit.jupiter.api.Assertions.assertEquals("v1", restored.getVersion());
        org.junit.jupiter.api.Assertions.assertEquals("PROPOSED", restored.getStatus());
        org.junit.jupiter.api.Assertions.assertEquals("corr-prop-123", restored.getCorrelationId());
        org.junit.jupiter.api.Assertions.assertEquals("hash-proposal-xyz", restored.getSnapshotHash());

        org.junit.jupiter.api.Assertions.assertEquals("req-001", restored.getRequestId());
        org.junit.jupiter.api.Assertions.assertEquals("wf-001", restored.getWorkflowDefId());
        org.junit.jupiter.api.Assertions.assertEquals("v1", restored.getWorkflowDefVersion());
        org.junit.jupiter.api.Assertions.assertEquals("step-001", restored.getStepDefId());
        org.junit.jupiter.api.Assertions.assertEquals("v1", restored.getStepDefVersion());

        org.junit.jupiter.api.Assertions.assertEquals(
                "{\"actions\":[{\"tool\":\"HTTP_GET\",\"url\":\"https://example.com\"}]}",
                restored.getProposalPayload()
        );
        org.junit.jupiter.api.Assertions.assertEquals("gpt-5.2", restored.getModel());
        org.junit.jupiter.api.Assertions.assertEquals("prompt-v1", restored.getPromptVersion());

        Console.log("UT", "roundtrip test completed successfully");
    }
}
