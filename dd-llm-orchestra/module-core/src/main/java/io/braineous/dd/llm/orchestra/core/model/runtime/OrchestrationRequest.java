package io.braineous.dd.llm.orchestra.core.model.runtime;

import io.braineous.dd.llm.orchestra.core.model.OrchestraBaseModel;

/**
 * V0 Orchestration Request (Runtime Layer).
 *
 * Represents a single execution request into Orchestra.
 *
 * An OrchestrationRequest describes:
 * - The target WorkflowDef ID.
 * - The specific WorkflowDef version.
 * - The deterministic input payload.
 * - Optional external metadata.
 *
 * This is a runtime entity.
 *
 * V0 Rules:
 * - Must reference a specific WorkflowDef version.
 * - Must remain immutable once execution begins (handled outside this model).
 * - Input payload must be serializable and deterministic.
 *
 * This entity evolves into the primary execution entry point for Orchestra.
 */
public class OrchestrationRequest extends OrchestraBaseModel {

    /**
     * Target workflow definition ID.
     */
    private String workflowDefId;

    /**
     * Target workflow definition version.
     * Must match an existing WorkflowDef version.
     */
    private String workflowDefVersion;

    /**
     * Deterministic input payload for the workflow.
     * Stored as JSON string in V0 for simplicity.
     */
    private String inputPayload;

    /**
     * Optional external metadata for tracing or enrichment.
     * Example: source system, tenant, environment.
     */
    private java.util.Map<String, String> metadata;

    public OrchestrationRequest() {
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
    public String getInputPayload() {
        return inputPayload;
    }

    public void setInputPayload(String inputPayload) {
        this.inputPayload = inputPayload;
    }

    //---------------------------------------------
    public java.util.Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(java.util.Map<String, String> metadata) {
        this.metadata = metadata;
    }
}

