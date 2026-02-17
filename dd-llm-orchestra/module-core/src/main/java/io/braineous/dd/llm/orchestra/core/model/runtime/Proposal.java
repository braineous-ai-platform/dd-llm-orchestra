package io.braineous.dd.llm.orchestra.core.model.runtime;

import io.braineous.dd.llm.orchestra.core.model.OrchestraBaseModel;

/**
 * V0 Proposal (Runtime Layer).
 *
 * Represents a structured proposal produced by the LLM_PROPOSE phase.
 *
 * A Proposal describes:
 * - The workflow + step that produced it.
 * - The deterministic proposal payload (structured JSON).
 * - Optional model metadata (model name/version) for traceability.
 *
 * This is a runtime entity.
 *
 * V0 Rules:
 * - Proposal must be serializable and deterministic (structured JSON, not free-form text).
 * - Proposal must reference the exact WorkflowDef + StepDef version context used.
 * - Proposal must not perform any side effects.
 *
 * This entity evolves into the canonical "LLM proposes" artifact for audit and replay.
 */
public class Proposal extends OrchestraBaseModel {

    /**
     * Reference to the orchestration request ID that triggered this proposal.
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
     * Step definition identity that produced this proposal.
     */
    private String stepDefId;

    /**
     * Step definition version that produced this proposal.
     */
    private String stepDefVersion;

    /**
     * Deterministic structured proposal payload (JSON string).
     * Example: list of proposed actions, parameters, and expected outputs.
     */
    private String proposalPayload;

    /**
     * Optional: model identifier used to produce the proposal (for traceability).
     * Example: "gpt-5.2" or "internal-llm-v1".
     */
    private String model;

    /**
     * Optional: prompt/template version used to produce the proposal.
     */
    private String promptVersion;

    public Proposal() {
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
    public String getProposalPayload() {
        return proposalPayload;
    }

    public void setProposalPayload(String proposalPayload) {
        this.proposalPayload = proposalPayload;
    }

    //---------------------------------------------
    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    //---------------------------------------------
    public String getPromptVersion() {
        return promptVersion;
    }

    public void setPromptVersion(String promptVersion) {
        this.promptVersion = promptVersion;
    }
}
