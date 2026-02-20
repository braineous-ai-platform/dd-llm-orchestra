package io.braineous.dd.llm.orchestra.def.model;

import io.braineous.dd.llm.orchestra.core.model.OrchestraBaseModel;

import io.braineous.dd.llm.orchestra.core.model.OrchestraBaseModel;

/**
 * Dev-authored Agentic Workflow definition.
 *
 * <p>
 * This model represents a single coarse-grained agentic transaction boundary.
 * It is intentionally minimal and mirrors the service-layer mental model
 * familiar to enterprise developers.
 * </p>
 *
 * <p>
 * A Workflow contains:
 * <ul>
 *     <li>Human-readable identity (name, description)</li>
 *     <li>A single Transaction definition (defined separately)</li>
 * </ul>
 * </p>
 *
 * <p>
 * The workflow does NOT expose internal execution steps, tools, policy,
 * or scoring definitions. Those concerns are compiled deterministically
 * by the Orchestra runtime.
 * </p>
 *
 * <p>
 * Conceptually:
 * <ul>
 *     <li>Queries execute in order within a single transaction</li>
 *     <li>Results are staged</li>
 *     <li>A policy gate defines the commit boundary (HITL in v0)</li>
 *     <li>Downstream mutation order is explicitly preserved</li>
 * </ul>
 * </p>
 *
 * <p>
 * This class represents the external contract surface only.
 * Internal step expansion and governance overlays remain runtime concerns.
 * </p>
 */
public class Workflow extends OrchestraBaseModel {

    private String name;

    private String description;

    public Workflow() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
