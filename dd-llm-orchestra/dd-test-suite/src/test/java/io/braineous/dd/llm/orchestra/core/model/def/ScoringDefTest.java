package io.braineous.dd.llm.orchestra.core.model.def;

import ai.braineous.rag.prompt.observe.Console;
import io.braineous.dd.llm.orchestra.core.model.OrchestraBaseModel;

public class ScoringDefTest {

    @org.junit.jupiter.api.Test
    public void serialize_and_deserialize_roundTrip_preservesAllFields() {

        Console.log("UT", "create scoring definition");

        ScoringDef def = new ScoringDef();
        def.setId("scoring-001");
        def.setVersion("v1");
        def.setCreatedAt("2026-02-16T20:40:00Z");
        def.setUpdatedAt("2026-02-16T20:41:00Z");
        def.setStatus("CREATED");
        def.setCorrelationId("corr-555");
        def.setSnapshotHash("hash-scoring-xyz");

        def.setScoringKey("DEFAULT_SCORING_V1");

        java.util.List<String> scorerIds = new java.util.ArrayList<String>();
        scorerIds.add("SCORER_A");
        scorerIds.add("SCORER_B");
        def.setScorerIds(scorerIds);

        java.util.Map<String, Double> weights = new java.util.HashMap<String, Double>();
        weights.put("SCORER_A", Double.valueOf(0.70d));
        weights.put("SCORER_B", Double.valueOf(0.30d));
        def.setScorerWeights(weights);

        def.setRiskMappingRef("DEFAULT_RISK_MAPPING_V1");

        Console.log("UT", "serialize to json");

        String json = def.toJson();
        org.junit.jupiter.api.Assertions.assertNotNull(json);

        Console.log("UT", "deserialize from json");

        ScoringDef restored =
                OrchestraBaseModel.fromJson(json, ScoringDef.class);

        Console.log("UT", "assert roundtrip integrity");

        org.junit.jupiter.api.Assertions.assertEquals("scoring-001", restored.getId());
        org.junit.jupiter.api.Assertions.assertEquals("v1", restored.getVersion());
        org.junit.jupiter.api.Assertions.assertEquals("CREATED", restored.getStatus());
        org.junit.jupiter.api.Assertions.assertEquals("corr-555", restored.getCorrelationId());
        org.junit.jupiter.api.Assertions.assertEquals("hash-scoring-xyz", restored.getSnapshotHash());

        org.junit.jupiter.api.Assertions.assertEquals("DEFAULT_SCORING_V1", restored.getScoringKey());

        org.junit.jupiter.api.Assertions.assertEquals(2, restored.getScorerIds().size());
        org.junit.jupiter.api.Assertions.assertEquals("SCORER_A", restored.getScorerIds().get(0));
        org.junit.jupiter.api.Assertions.assertEquals("SCORER_B", restored.getScorerIds().get(1));

        org.junit.jupiter.api.Assertions.assertEquals(2, restored.getScorerWeights().size());
        org.junit.jupiter.api.Assertions.assertEquals(Double.valueOf(0.70d), restored.getScorerWeights().get("SCORER_A"));
        org.junit.jupiter.api.Assertions.assertEquals(Double.valueOf(0.30d), restored.getScorerWeights().get("SCORER_B"));

        org.junit.jupiter.api.Assertions.assertEquals("DEFAULT_RISK_MAPPING_V1", restored.getRiskMappingRef());

        Console.log("UT", "roundtrip test completed successfully");
    }
}
