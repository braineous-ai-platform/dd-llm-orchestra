package io.braineous.dd.llm.orchestra.core.model.runtime;

import io.braineous.dd.llm.orchestra.core.model.OrchestraBaseModel;

/**
 * V0 Commit Record (Runtime Layer).
 *
 * Represents the execution outcome of an approved proposal.
 *
 * A CommitRecord describes:
 * - The associated request/workflow/step.
 * - The associated proposal.
 * - The commit execution status.
 * - The deterministic execution result payload.
 *
 * This is a runtime entity.
 *
 * V0 Rules:
 * - Must only exist after approval.
 * - Must reference the exact WorkflowDef + StepDef version context used.
 * - Must be fully serializable and replayable.
 * - Must not contain business logic.
 *
 * This entity evolves into the canonical side-effect execution artifact
 * of Orchestra.
 */
public class CommitRecord extends OrchestraBaseModel {

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
     * Step definition identity responsible for commit.
     */
    private String stepDefId;

    /**
     * Step definition version responsible for commit.
     */
    private String stepDefVersion;

    /**
     * Associated proposal ID.
     */
    private String proposalId;

    /**
     * Stable commit status vocabulary
     * (e.g., COMMIT_PENDING, COMMITTED, COMMIT_FAILED).
     */
    private String commitStatus;

    /**
     * Optional deterministic execution result payload (JSON string).
     */
    private String commitResultPayload;

    /**
     * Optional failure code for commit errors (stable vocabulary).
     */
    private String failureCode;

    /**
     * Optional failure message for audit.
     */
    private String failureMessage;

    /**
     * Optional commit timestamp (ISO-8601 UTC).
     */
    private String committedAt;

    public CommitRecord() {
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
    public String getCommitStatus() {
        return commitStatus;
    }

    public void setCommitStatus(String commitStatus) {
        this.commitStatus = commitStatus;
    }

    //---------------------------------------------
    public String getCommitResultPayload() {
        return commitResultPayload;
    }

    public void setCommitResultPayload(String commitResultPayload) {
        this.commitResultPayload = commitResultPayload;
    }

    //---------------------------------------------
    public String getFailureCode() {
        return failureCode;
    }

    public void setFailureCode(String failureCode) {
        this.failureCode = failureCode;
    }

    //---------------------------------------------
    public String getFailureMessage() {
        return failureMessage;
    }

    public void setFailureMessage(String failureMessage) {
        this.failureMessage = failureMessage;
    }

    //---------------------------------------------
    public String getCommittedAt() {
        return committedAt;
    }

    public void setCommittedAt(String committedAt) {
        this.committedAt = committedAt;
    }
}

