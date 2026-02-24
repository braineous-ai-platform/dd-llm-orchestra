package io.braineous.dd.llm.orchestra.def.model;

import com.netflix.conductor.common.metadata.workflow.WorkflowDef;

/**
 * AssemblyArtifact
 *
 * Represents the compiled artifact produced by the Orchestra Definition compiler.
 *
 * Conceptually:
 * - WorkflowDef (Orchestra) = source program
 * - AssemblerService        = compiler
 * - AssemblyArtifact        = compiled artifact
 *
 * This artifact contains the fully compiled, validated, and normalized
 * Conductor Workflow definition derived from an Orchestra WorkflowDef.
 *
 * Key Properties:
 * - Deterministic: identical WorkflowDef inputs must produce identical
 *   Conductor WorkflowDef outputs.
 * - Immutable after publication (immutability enforced at service layer).
 * - No semantic duplication: this class does NOT redefine workflow fields.
 *   It wraps the compiled Conductor representation only.
 * - No business logic: this is a pure transport container.
 *
 * Architectural Invariants:
 * - Conductor is the execution source of truth.
 * - Orchestra stores no workflow execution state.
 * - AssemblyArtifact is publish-ready and immediately deployable to Conductor.
 *
 * This class intentionally remains minimal to avoid:
 * - field duplication
 * - divergence between Orchestra and Conductor models
 * - accidental secondary sources of truth
 */
public class AssemblyArtifact {

    private Workflow workflow;

    public AssemblyArtifact() {
    }

    public Workflow getWorkflow() {
        return workflow;
    }

    public void setWorkflow(Workflow workflow) {
        if(workflow == null){
            throw new IllegalArgumentException("workflow cannot be null");
        }
        this.workflow = workflow;
    }
}

