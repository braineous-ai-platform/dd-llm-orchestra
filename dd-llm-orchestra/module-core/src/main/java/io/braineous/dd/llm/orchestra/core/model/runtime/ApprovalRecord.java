package io.braineous.dd.llm.orchestra.core.model.runtime;

import io.braineous.dd.llm.orchestra.core.model.OrchestraBaseModel;

/**
 * V0 Approval Record (Runtime Layer).
 *
 * Represents the outcome of the PolicyGate decision for a given proposal/step.
 *
 * An ApprovalRecord describes:
 * - The associated request/workflow/step.
 * - The associated proposal.
 * - The approval outcome (APPROVED/REJECTED/PENDING).
 * - Approver identity and rationale (if manual).
 *
 * This is a runtime entity.
 *
 * V0 Rules:
 * - Must reference the exact WorkflowDef + StepDef version context used.
 * - Must be serializable and replayable.
 * - Must not contain side-effect execution details (that belongs to CommitRecord).
 *
 * This entity evolves into the canonical approval/audit artifact for Orchestra.
 */
public class ApprovalRecord extends OrchestraBaseModel {

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
     * Step definition identity that requested approval.
     */
    private String stepDefId;

    /**
     * Step definition version that requested approval.
     */
    private String stepDefVersion;

    /**
     * Proposal being approved/rejected.
     */
    private String proposalId;

    /**
     * Policy definition reference used by the PolicyGate.
     */
    private String policyDefId;

    /**
     * Policy definition version used by the PolicyGate.
     */
    private String policyDefVersion;

    /**
     * Stable approval status vocabulary (e.g., PENDING, APPROVED, REJECTED).
     */
    private String approvalStatus;

    /**
     * Optional approver identity (human/system).
     */
    private String approvedBy;

    /**
     * Optional approval timestamp (ISO-8601 UTC).
     */
    private String approvedAt;

    /**
     * Optional rationale or note for audit.
     */
    private String rationale;

    public ApprovalRecord() {
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
    public String getProposalId() {
        return proposalId;
    }

    public void setProposalId(String proposalId) {
        this.proposalId = proposalId;
    }

    //---------------------------------------------
    public String getPolicyDefId() {
        return policyDefId;
    }

    public void setPolicyDefId(String policyDefId) {
        this.policyDefId = policyDefId;
    }

    //---------------------------------------------
    public String getPolicyDefVersion() {
        return policyDefVersion;
    }

    public void setPolicyDefVersion(String policyDefVersion) {
        this.policyDefVersion = policyDefVersion;
    }

    //---------------------------------------------
    public String getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    //---------------------------------------------
    public String getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(String approvedBy) {
        this.approvedBy = approvedBy;
    }

    //---------------------------------------------
    public String getApprovedAt() {
        return approvedAt;
    }

    public void setApprovedAt(String approvedAt) {
        this.approvedAt = approvedAt;
    }

    //---------------------------------------------
    public String getRationale() {
        return rationale;
    }

    public void setRationale(String rationale) {
        this.rationale = rationale;
    }
}
