package io.braineous.dd.llm.orchestra.def.model;


import io.braineous.dd.llm.orchestra.core.model.OrchestraBaseModel;
import io.braineous.dd.llm.orchestra.core.model.Why;

/**
 * PublishResult
 *
 * Represents the deterministic outcome of attempting to publish an AssemblyArtifact
 * to the workflow engine (Conductor).
 *
 * This is the only outward truth surface for the internal compiler pipeline.
 * - No exception bubbling is expected at the boundary (Assembler maps to failure results).
 * - Failure must be expressed via stable WHY codes.
 *
 * V0: Minimal but structured.
 */
public class PublishResult extends OrchestraBaseModel {

    private boolean success;

    private Why why;

    /**
     * Optional engine identifiers for traceability (e.g., Conductor workflow name/version).
     */
    private String engineWorkflowName;

    private Integer engineWorkflowVersion;

    public PublishResult() {
    }

    public static PublishResult success(String engineWorkflowName, Integer engineWorkflowVersion) {
        PublishResult r = new PublishResult();
        r.success = true;
        r.engineWorkflowName = engineWorkflowName;
        r.engineWorkflowVersion = engineWorkflowVersion;
        return r;
    }

    public static PublishResult failure(String whyCode, String details) {
        if (whyCode == null) {
            throw new IllegalArgumentException("whyCode cannot be null");
        }
        PublishResult r = new PublishResult();
        r.success = false;
        r.why = new Why(whyCode, details);
        return r;
    }

    //---------------------------------------------

    public boolean isSuccess() {
        return success;
    }

    public Why getWhy() {
        return why;
    }

    public String getEngineWorkflowName() {
        return engineWorkflowName;
    }

    public Integer getEngineWorkflowVersion() {
        return engineWorkflowVersion;
    }

    //---------------------------------------------

    @Override
    public String toString() {
        return "PublishResult{" +
                "success=" + success +
                ", why=" + why +
                ", engineWorkflowName='" + engineWorkflowName + '\'' +
                ", engineWorkflowVersion=" + engineWorkflowVersion +
                '}';
    }
}
