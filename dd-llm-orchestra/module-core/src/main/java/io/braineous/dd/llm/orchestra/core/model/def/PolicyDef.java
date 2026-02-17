package io.braineous.dd.llm.orchestra.core.model.def;

import io.braineous.dd.llm.orchestra.core.model.OrchestraBaseModel;

/**
 * V0 Policy Definition (Definition Layer).
 *
 * Represents a deterministic approval/governance contract inside Orchestra.
 *
 * A PolicyDef describes:
 * - The stable policy key.
 * - Required approval type (e.g., AUTO, MANUAL, THRESHOLD).
 * - Risk threshold boundaries.
 * - Optional required approver roles.
 *
 * This is a static configuration entity and must NOT contain runtime state.
 *
 * V0 Rules:
 * - PolicyDef is versioned.
 * - PolicyDef must be deterministic.
 * - Any change requires a version bump.
 * - Runtime execution must reference a specific PolicyDef version.
 *
 * This entity evolves into the core of the PolicyGate enforcement layer.
 */
public class PolicyDef extends OrchestraBaseModel {

    /**
     * Stable allowlist key for the policy.
     */
    private String policyKey;

    /**
     * Approval mode vocabulary (e.g., AUTO, MANUAL, THRESHOLD).
     * Must remain stable.
     */
    private String approvalMode;

    /**
     * Optional minimum score required for auto-approval.
     * Used when approvalMode = THRESHOLD.
     */
    private Double minScoreThreshold;

    /**
     * Optional maximum risk level allowed for auto-approval.
     * Vocabulary must be stable (e.g., LOW, MEDIUM, HIGH).
     */
    private String maxAllowedRiskLevel;

    /**
     * Optional list of required approver roles for MANUAL approval.
     */
    private java.util.List<String> requiredApproverRoles;

    public PolicyDef() {
    }

    //---------------------------------------------
    public String getPolicyKey() {
        return policyKey;
    }

    public void setPolicyKey(String policyKey) {
        this.policyKey = policyKey;
    }

    //---------------------------------------------
    public String getApprovalMode() {
        return approvalMode;
    }

    public void setApprovalMode(String approvalMode) {
        this.approvalMode = approvalMode;
    }

    //---------------------------------------------
    public Double getMinScoreThreshold() {
        return minScoreThreshold;
    }

    public void setMinScoreThreshold(Double minScoreThreshold) {
        this.minScoreThreshold = minScoreThreshold;
    }

    //---------------------------------------------
    public String getMaxAllowedRiskLevel() {
        return maxAllowedRiskLevel;
    }

    public void setMaxAllowedRiskLevel(String maxAllowedRiskLevel) {
        this.maxAllowedRiskLevel = maxAllowedRiskLevel;
    }

    //---------------------------------------------
    public java.util.List<String> getRequiredApproverRoles() {
        return requiredApproverRoles;
    }

    public void setRequiredApproverRoles(java.util.List<String> requiredApproverRoles) {
        this.requiredApproverRoles = requiredApproverRoles;
    }
}

