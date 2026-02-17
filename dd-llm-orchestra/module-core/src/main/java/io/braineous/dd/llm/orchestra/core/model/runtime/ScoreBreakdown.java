package io.braineous.dd.llm.orchestra.core.model.runtime;

import io.braineous.dd.llm.orchestra.core.model.OrchestraBaseModel;

/**
 * V0 Score Breakdown (Runtime Layer).
 *
 * Represents the atomic scoring output from a single scorer.
 *
 * A ScoreBreakdown describes:
 * - The associated request/workflow/step.
 * - The scorer identifier.
 * - The raw score value.
 * - Optional weighted score.
 * - Optional reasoning/WHY code.
 *
 * This is a runtime entity.
 *
 * V0 Rules:
 * - Must be deterministic.
 * - scorerId must be stable vocabulary.
 * - score values must be reproducible for identical input.
 * - No aggregation logic allowed here.
 *
 * This entity evolves into the atomic scoring unit
 * used by ScoreCombiner.
 */
public class ScoreBreakdown extends OrchestraBaseModel {

    /**
     * Associated orchestration request ID.
     */
    private String requestId;

    /**
     * Target workflow definition identity.
     */
    private String workflowDefId;

    /**
     * Target workflow definition version.
     */
    private String workflowDefVersion;

    /**
     * Step definition identity.
     */
    private String stepDefId;

    /**
     * Step definition version.
     */
    private String stepDefVersion;

    /**
     * Stable scorer identifier (e.g., RISK_SCORER, COST_SCORER).
     */
    private String scorerId;

    /**
     * Raw deterministic score produced by this scorer.
     */
    private Double rawScore;

    /**
     * Optional weighted score after applying ScoringDef weights.
     */
    private Double weightedScore;

    /**
     * Optional stable WHY code explaining this score.
     */
    private String whyCode;

    public ScoreBreakdown() {
    }

    //---------------------------------------------
    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    //---------------------------------------------
    public String getWorkflowDefId() {
        return workflowDefId;
    }

    public void setWorkflowDefId(String workflowDefId) {
        this.workflowDefId = workflowDefId;
    }

    //---------------------------------------------
    public String getWorkflowDefVersion() {
        return workflowDefVersion;
    }

    public void setWorkflowDefVersion(String workflowDefVersion) {
        this.workflowDefVersion = workflowDefVersion;
    }

    //---------------------------------------------
    public String getStepDefId() {
        return stepDefId;
    }

    public void setStepDefId(String stepDefId) {
        this.stepDefId = stepDefId;
    }

    //---------------------------------------------
    public String getStepDefVersion() {
        return stepDefVersion;
    }

    public void setStepDefVersion(String stepDefVersion) {
        this.stepDefVersion = stepDefVersion;
    }

    //---------------------------------------------
    public String getScorerId() {
        return scorerId;
    }

    public void setScorerId(String scorerId) {
        this.scorerId = scorerId;
    }

    //---------------------------------------------
    public Double getRawScore() {
        return rawScore;
    }

    public void setRawScore(Double rawScore) {
        this.rawScore = rawScore;
    }

    //---------------------------------------------
    public Double getWeightedScore() {
        return weightedScore;
    }

    public void setWeightedScore(Double weightedScore) {
        this.weightedScore = weightedScore;
    }

    //---------------------------------------------
    public String getWhyCode() {
        return whyCode;
    }

    public void setWhyCode(String whyCode) {
        this.whyCode = whyCode;
    }
}
