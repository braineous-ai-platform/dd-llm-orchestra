package io.braineous.dd.llm.orchestra.core.model.runtime;

import ai.braineous.rag.prompt.observe.Console;
import io.braineous.dd.llm.orchestra.core.model.OrchestraBaseModel;

public class ScoreBreakdownTest {

    @org.junit.jupiter.api.Test
    public void serialize_and_deserialize_roundTrip_preservesAllFields() {

        Console.log("UT", "create score breakdown");

        ScoreBreakdown b = new ScoreBreakdown();
        b.setId("breakdown-001");
        b.setVersion("v1");
        b.setCreatedAt("2026-02-16T21:50:00Z");
        b.setUpdatedAt("2026-02-16T21:51:00Z");
        b.setStatus("CREATED");
        b.setCorrelationId("corr-score-555");
        b.setSnapshotHash("hash-score-xyz");

        b.setRequestId("req-001");
        b.setWorkflowDefId("wf-001");
        b.setWorkflowDefVersion("v1");
        b.setStepDefId("step-001");
        b.setStepDefVersion("v1");

        b.setScorerId("RISK_SCORER");
        b.setRawScore(Double.valueOf(0.90d));
        b.setWeightedScore(Double.valueOf(0.63d));
        b.setWhyCode("RISK_LOW");

        Console.log("UT", "serialize to json");

        String json = b.toJson();
        org.junit.jupiter.api.Assertions.assertNotNull(json);

        Console.log("UT", "deserialize from json");

        ScoreBreakdown restored =
                OrchestraBaseModel.fromJson(json, ScoreBreakdown.class);

        Console.log("UT", "assert roundtrip integrity");

        org.junit.jupiter.api.Assertions.assertEquals("breakdown-001", restored.getId());
        org.junit.jupiter.api.Assertions.assertEquals("v1", restored.getVersion());
        org.junit.jupiter.api.Assertions.assertEquals("CREATED", restored.getStatus());
        org.junit.jupiter.api.Assertions.assertEquals("corr-score-555", restored.getCorrelationId());
        org.junit.jupiter.api.Assertions.assertEquals("hash-score-xyz", restored.getSnapshotHash());

        org.junit.jupiter.api.Assertions.assertEquals("req-001", restored.getRequestId());
        org.junit.jupiter.api.Assertions.assertEquals("wf-001", restored.getWorkflowDefId());
        org.junit.jupiter.api.Assertions.assertEquals("v1", restored.getWorkflowDefVersion());
        org.junit.jupiter.api.Assertions.assertEquals("step-001", restored.getStepDefId());
        org.junit.jupiter.api.Assertions.assertEquals("v1", restored.getStepDefVersion());

        org.junit.jupiter.api.Assertions.assertEquals("RISK_SCORER", restored.getScorerId());
        org.junit.jupiter.api.Assertions.assertEquals(Double.valueOf(0.90d), restored.getRawScore());
        org.junit.jupiter.api.Assertions.assertEquals(Double.valueOf(0.63d), restored.getWeightedScore());
        org.junit.jupiter.api.Assertions.assertEquals("RISK_LOW", restored.getWhyCode());

        Console.log("UT", "roundtrip test completed successfully");
    }
}
