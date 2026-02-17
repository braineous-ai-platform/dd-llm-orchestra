package io.braineous.dd.llm.orchestra.core.model.runtime;

import io.braineous.dd.llm.orchestra.core.model.OrchestraBaseModel;

/**
 * V0 Score Result (Runtime Layer).
 *
 * Represents the aggregated deterministic scoring outcome
 * for a proposal/step.
 *
 * A ScoreResult describes:
 * - The associated request/workflow/step.
 * - The total aggregated score.
 * - The derived risk level.
 * - The ordered list of ScoreBreakdown entries.
 *
 * This is a runtime entity.
 *
 * V0 Rules:
 * - Must be fully deterministic for identical inputs.
 * - Breakdown ordering must remain stable.
 * - No scoring logic allowed here (aggregation handled elsewhere).
 *
 * This entity evolves into the canonical decision surface
 * consumed by PolicyGate.
 */
public class ScoreResult extends OrchestraBaseModel {

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
     * Deterministic aggregated score.
     */
    private Double totalScore;

    /**
     * Derived risk level (stable vocabulary e.g., LOW/MEDIUM/HIGH).
     */
    private String riskLevel;

    /**
     * Ordered list of atomic breakdowns.
     * Order must remain stable for replay determinism.
     */
    private java.util.List<ScoreBreakdown> breakdowns;

    public ScoreResult() {
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
    public Double getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(Double totalScore) {
        this.totalScore = totalScore;
    }

    //---------------------------------------------
    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    //---------------------------------------------
    public java.util.List<ScoreBreakdown> getBreakdowns() {
        return breakdowns;
    }

    public void setBreakdowns(java.util.List<ScoreBreakdown> breakdowns) {
        this.breakdowns = breakdowns;
    }
}

