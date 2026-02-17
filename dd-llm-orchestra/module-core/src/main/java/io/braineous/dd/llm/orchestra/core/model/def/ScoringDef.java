package io.braineous.dd.llm.orchestra.core.model.def;

import io.braineous.dd.llm.orchestra.core.model.OrchestraBaseModel;

/**
 * V0 Scoring Definition (Definition Layer).
 *
 * Represents a deterministic scoring configuration inside Orchestra.
 *
 * A ScoringDef describes:
 * - The stable scoring key.
 * - The list of scorer identifiers involved.
 * - Optional weight configuration per scorer.
 * - Optional risk threshold mapping references.
 *
 * This is a static configuration entity and must NOT contain runtime state.
 *
 * V0 Rules:
 * - ScoringDef is versioned.
 * - ScoringDef must be deterministic.
 * - Any change requires a version bump.
 * - ScoreCombiner must produce identical results for identical inputs.
 *
 * This entity evolves into the deterministic scoring backbone of Orchestra.
 */
public class ScoringDef extends OrchestraBaseModel {

    /**
     * Stable allowlist key for the scoring configuration.
     */
    private String scoringKey;

    /**
     * Ordered list of scorer identifiers.
     * Order must remain stable for deterministic aggregation.
     */
    private java.util.List<String> scorerIds;

    /**
     * Optional weight mapping per scorerId.
     * Key = scorerId, Value = weight multiplier.
     */
    private java.util.Map<String, Double> scorerWeights;

    /**
     * Optional reference for risk mapping strategy.
     * Example: "DEFAULT_RISK_MAPPING_V1"
     */
    private String riskMappingRef;

    public ScoringDef() {
    }

    //---------------------------------------------
    public String getScoringKey() {
        return scoringKey;
    }

    public void setScoringKey(String scoringKey) {
        this.scoringKey = scoringKey;
    }

    //---------------------------------------------
    public java.util.List<String> getScorerIds() {
        return scorerIds;
    }

    public void setScorerIds(java.util.List<String> scorerIds) {
        this.scorerIds = scorerIds;
    }

    //---------------------------------------------
    public java.util.Map<String, Double> getScorerWeights() {
        return scorerWeights;
    }

    public void setScorerWeights(java.util.Map<String, Double> scorerWeights) {
        this.scorerWeights = scorerWeights;
    }

    //---------------------------------------------
    public String getRiskMappingRef() {
        return riskMappingRef;
    }

    public void setRiskMappingRef(String riskMappingRef) {
        this.riskMappingRef = riskMappingRef;
    }
}

