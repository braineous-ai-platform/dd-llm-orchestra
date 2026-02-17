package io.braineous.dd.llm.orchestra.core.model.runtime;

import io.braineous.dd.llm.orchestra.core.model.OrchestraBaseModel;

/**
 * V0 Execution Timeline (Runtime Layer).
 *
 * Represents the immutable audit trail of a single orchestration execution.
 *
 * An ExecutionTimeline describes:
 * - The associated request.
 * - The workflow definition reference.
 * - The ordered list of lifecycle events.
 *
 * This is a runtime entity and must be append-only once execution begins.
 *
 * V0 Rules:
 * - Events must remain in stable chronological order.
 * - No event mutation after recording.
 * - Timeline must be fully serializable for replay and audit.
 *
 * This entity evolves into the core audit and replay surface of Orchestra.
 */
public class ExecutionTimeline extends OrchestraBaseModel {

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
     * Ordered list of lifecycle events.
     * Order must reflect actual execution progression.
     */
    private java.util.List<String> events;

    public ExecutionTimeline() {
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
    public java.util.List<String> getEvents() {
        return events;
    }

    public void setEvents(java.util.List<String> events) {
        this.events = events;
    }
}
