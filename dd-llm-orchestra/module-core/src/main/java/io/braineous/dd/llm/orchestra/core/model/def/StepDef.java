package io.braineous.dd.llm.orchestra.core.model.def;

import io.braineous.dd.llm.orchestra.core.model.OrchestraBaseModel;

/**
 * V0 Step Definition (Definition Layer).
 *
 * Represents a single, ordered step inside a WorkflowDef.
 *
 * A StepDef describes:
 * - The kind of control transition (e.g., PROPOSE, POLICY_GATE, COMMIT).
 * - The deterministic inputs/outputs shape expected at this step.
 * - Optional references to scoring/policy/tool configuration required by this step.
 *
 * This is a static configuration entity and must NOT contain runtime state.
 *
 * V0 Rules:
 * - StepDef is versioned.
 * - StepDef must be deterministic.
 * - Any change requires a version bump.
 * - WorkflowDef references StepDef by ID in a stable, ordered list.
 *
 * This entity evolves into the core unit of the Orchestra Workflow Runtime.
 */
public class StepDef extends OrchestraBaseModel {

    /**
     * Stable step kind (control transition), e.g. PROPOSE, POLICY_GATE, COMMIT.
     * Must use stable vocabulary.
     */
    private String stepKind;

    /**
     * Human-readable name for the step.
     */
    private String name;

    /**
     * Optional description for clarity in catalogs/audit UIs.
     */
    private String description;

    /**
     * Stable ordering key within the workflow, if used.
     * WorkflowDef ordering is primary; this can be used as an extra guardrail.
     */
    private Integer orderIndex;

    /**
     * Optional: reference to a PolicyDef required for this step (definition layer).
     * Typically used for POLICY_GATE.
     */
    private String policyDefId;

    /**
     * Optional: reference to a ScoringDef used by this step (definition layer).
     * Typically used for PROPOSE evaluation prior to PolicyGate decision.
     */
    private String scoringDefId;

    /**
     * Optional: allowed tool/action definitions for this step (strict allowlist).
     * This is step-scoped allowlist (can be narrower than WorkflowDef).
     */
    private java.util.List<String> toolDefIds;

    public StepDef() {
    }

    //---------------------------------------------
    public String getStepKind() {
        return stepKind;
    }

    public void setStepKind(String stepKind) {
        this.stepKind = stepKind;
    }

    //---------------------------------------------
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    //---------------------------------------------
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    //---------------------------------------------
    public Integer getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }

    //---------------------------------------------
    public String getPolicyDefId() {
        return policyDefId;
    }

    public void setPolicyDefId(String policyDefId) {
        this.policyDefId = policyDefId;
    }

    //---------------------------------------------
    public String getScoringDefId() {
        return scoringDefId;
    }

    public void setScoringDefId(String scoringDefId) {
        this.scoringDefId = scoringDefId;
    }

    //---------------------------------------------
    public java.util.List<String> getToolDefIds() {
        return toolDefIds;
    }

    public void setToolDefIds(java.util.List<String> toolDefIds) {
        this.toolDefIds = toolDefIds;
    }
}

