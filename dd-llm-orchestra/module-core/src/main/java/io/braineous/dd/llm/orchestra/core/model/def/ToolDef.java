package io.braineous.dd.llm.orchestra.core.model.def;

import io.braineous.dd.llm.orchestra.core.model.OrchestraBaseModel;

/**
 * V0 Tool Definition (Definition Layer).
 *
 * Represents a single allowed tool/action contract inside Orchestra.
 *
 * A ToolDef describes:
 * - The stable tool name (allowlist key).
 * - The deterministic input schema expected by the tool.
 * - The deterministic output schema produced by the tool.
 * - Optional risk classification / default policy requirements.
 *
 * This is a static configuration entity and must NOT contain runtime state.
 *
 * V0 Rules:
 * - ToolDef is versioned.
 * - ToolDef must be deterministic.
 * - Any change requires a version bump.
 * - Workflows/steps reference ToolDef by ID to enforce strict allowlists.
 *
 * This entity evolves into the Orchestra Tool Catalog and enforcement layer.
 */
public class ToolDef extends OrchestraBaseModel {

    /**
     * Stable allowlist key for the tool, e.g. "HTTP_GET", "KAFKA_PUBLISH", "DB_WRITE".
     * Must use stable vocabulary.
     */
    private String toolKey;

    /**
     * Human-readable name for the tool.
     */
    private String name;

    /**
     * Optional description for catalogs/audit UIs.
     */
    private String description;

    /**
     * Deterministic input schema reference (could be JSON schema ID, class name, or doc link).
     * V0 is semantic-only; no parsing/validation logic here.
     */
    private String inputSchemaRef;

    /**
     * Deterministic output schema reference (could be JSON schema ID, class name, or doc link).
     * V0 is semantic-only; no parsing/validation logic here.
     */
    private String outputSchemaRef;

    /**
     * Optional stable risk level vocabulary (e.g. LOW/MEDIUM/HIGH).
     * V0 keeps this as a String to avoid enum churn.
     */
    private String riskLevel;

    public ToolDef() {
    }

    //---------------------------------------------
    public String getToolKey() {
        return toolKey;
    }

    public void setToolKey(String toolKey) {
        this.toolKey = toolKey;
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
    public String getInputSchemaRef() {
        return inputSchemaRef;
    }

    public void setInputSchemaRef(String inputSchemaRef) {
        this.inputSchemaRef = inputSchemaRef;
    }

    //---------------------------------------------
    public String getOutputSchemaRef() {
        return outputSchemaRef;
    }

    public void setOutputSchemaRef(String outputSchemaRef) {
        this.outputSchemaRef = outputSchemaRef;
    }

    //---------------------------------------------
    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }
}

