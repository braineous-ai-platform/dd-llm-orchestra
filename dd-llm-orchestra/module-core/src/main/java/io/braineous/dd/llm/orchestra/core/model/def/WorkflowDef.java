package io.braineous.dd.llm.orchestra.core.model.def;

import io.braineous.dd.llm.orchestra.core.model.OrchestraBaseModel;

/**
 * V0 Workflow Definition (Definition Layer).
 *
 * Represents a versioned, immutable workflow contract inside Orchestra.
 *
 * A WorkflowDef describes:
 * - The ordered set of StepDef definitions.
 * - Associated PolicyDef rules.
 * - ScoringDef configuration.
 * - Allowed ToolDef definitions.
 *
 * This is a static configuration entity and must NOT contain runtime state.
 *
 * V0 Rules:
 * - WorkflowDef is versioned.
 * - WorkflowDef must be deterministic.
 * - Any change requires a version bump.
 * - Runtime execution must reference a specific WorkflowDef version.
 *
 * This entity evolves into the core of the Orchestra Catalog.
 */
public class WorkflowDef extends OrchestraBaseModel {

    private String name;

    private String description;

    /**
     * Stable, ordered list of StepDef IDs that define the workflow shape.
     * Order matters and must remain deterministic.
     */
    private java.util.List<String> stepDefIds;

    /**
     * Optional reference to a PolicyDef (definition layer).
     */
    private String policyDefId;

    /**
     * Optional reference to a ScoringDef (definition layer).
     */
    private String scoringDefId;

    /**
     * Allowed tool/action definitions for this workflow (strict allowlist).
     */
    private java.util.List<String> toolDefIds;

    public WorkflowDef() {
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
    public java.util.List<String> getStepDefIds() {
        return stepDefIds;
    }

    public void setStepDefIds(java.util.List<String> stepDefIds) {
        this.stepDefIds = stepDefIds;
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

