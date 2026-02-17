package io.braineous.dd.llm.orchestra.core.model.runtime;

import ai.braineous.rag.prompt.models.cgo.graph.GraphSnapshot;
import io.braineous.dd.llm.orchestra.core.model.OrchestraBaseModel;

/**
 * V0 Context Snapshot (Runtime Layer).
 *
 * Represents a deterministic snapshot of the graph/context state
 * at a specific point in orchestration execution.
 *
 * A ContextSnapshot describes:
 * - The associated request.
 * - The workflow reference.
 * - The immutable GraphSnapshot captured at time T.
 *
 * This is a runtime entity.
 *
 * V0 Rules:
 * - Snapshot must be immutable once captured.
 * - Snapshot must be fully serializable.
 * - snapshotHash must represent the deterministic hash of GraphSnapshot.
 * - Replay must use the exact same snapshotHash to ensure determinism.
 *
 * This entity evolves into the replay substrate of Orchestra.
 */
public class ContextSnapshot extends OrchestraBaseModel {

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
     * Deterministic graph snapshot captured at execution time.
     * This represents the substrate state used for scoring and decisioning.
     */
    private GraphSnapshot graphSnapshot;

    public ContextSnapshot() {
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
    public GraphSnapshot getGraphSnapshot() {
        return graphSnapshot;
    }

    public void setGraphSnapshot(GraphSnapshot graphSnapshot) {
        this.graphSnapshot = graphSnapshot;
    }
}

