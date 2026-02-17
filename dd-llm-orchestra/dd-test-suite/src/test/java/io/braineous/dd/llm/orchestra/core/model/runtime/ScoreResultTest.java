package io.braineous.dd.llm.orchestra.core.model.runtime;

import ai.braineous.rag.prompt.observe.Console;
import io.braineous.dd.llm.orchestra.core.model.OrchestraBaseModel;

public class ScoreResultTest {

    @org.junit.jupiter.api.Test
    public void serialize_and_deserialize_roundTrip_preservesAllFields() {

        Console.log("UT", "create score result");

        ScoreResult result = new ScoreResult();
        result.setId("score-result-001");
        result.setVersion("v1");
        result.setCreatedAt("2026-02-16T22:00:00Z");
        result.setUpdatedAt("2026-02-16T22:01:00Z");
        result.setStatus("CREATED");
        result.setCorrelationId("corr-score-result-888");
        result.setSnapshotHash("hash-score-result-xyz");

        result.setRequestId("req-001");
        result.setWorkflowDefId("wf-001");
        result.setWorkflowDefVersion("v1");
        result.setStepDefId("step-001");
        result.setStepDefVersion("v1");

        result.setTotalScore(Double.valueOf(0.85d));
        result.setRiskLevel("LOW");

        Console.log("UT", "create score breakdown list");

        ScoreBreakdown b1 = new ScoreBreakdown();
        b1.setScorerId("RISK_SCORER");
        b1.setRawScore(Double.valueOf(0.90d));
        b1.setWeightedScore(Double.valueOf(0.63d));
        b1.setWhyCode("RISK_LOW");

        ScoreBreakdown b2 = new ScoreBreakdown();
        b2.setScorerId("COST_SCORER");
        b2.setRawScore(Double.valueOf(0.75d));
        b2.setWeightedScore(Double.valueOf(0.22d));
        b2.setWhyCode("COST_ACCEPTABLE");

        java.util.List<ScoreBreakdown> list = new java.util.ArrayList<ScoreBreakdown>();
        list.add(b1);
        list.add(b2);

        result.setBreakdowns(list);

        Console.log("UT", "serialize to json");

        String json = result.toJson();
        org.junit.jupiter.api.Assertions.assertNotNull(json);

        Console.log("UT", "deserialize from json");

        ScoreResult restored =
                OrchestraBaseModel.fromJson(json, ScoreResult.class);

        Console.log("UT", "assert roundtrip integrity");

        org.junit.jupiter.api.Assertions.assertEquals("score-result-001", restored.getId());
        org.junit.jupiter.api.Assertions.assertEquals("v1", restored.getVersion());
        org.junit.jupiter.api.Assertions.assertEquals("CREATED", restored.getStatus());
        org.junit.jupiter.api.Assertions.assertEquals("corr-score-result-888", restored.getCorrelationId());
        org.junit.jupiter.api.Assertions.assertEquals("hash-score-result-xyz", restored.getSnapshotHash());

        org.junit.jupiter.api.Assertions.assertEquals("req-001", restored.getRequestId());
        org.junit.jupiter.api.Assertions.assertEquals("wf-001", restored.getWorkflowDefId());
        org.junit.jupiter.api.Assertions.assertEquals("v1", restored.getWorkflowDefVersion());
        org.junit.jupiter.api.Assertions.assertEquals("step-001", restored.getStepDefId());
        org.junit.jupiter.api.Assertions.assertEquals("v1", restored.getStepDefVersion());

        org.junit.jupiter.api.Assertions.assertEquals(Double.valueOf(0.85d), restored.getTotalScore());
        org.junit.jupiter.api.Assertions.assertEquals("LOW", restored.getRiskLevel());

        org.junit.jupiter.api.Assertions.assertEquals(2, restored.getBreakdowns().size());
        org.junit.jupiter.api.Assertions.assertEquals("RISK_SCORER",
                restored.getBreakdowns().get(0).getScorerId());
        org.junit.jupiter.api.Assertions.assertEquals("COST_SCORER",
                restored.getBreakdowns().get(1).getScorerId());

        Console.log("UT", "roundtrip test completed successfully");
    }
}
