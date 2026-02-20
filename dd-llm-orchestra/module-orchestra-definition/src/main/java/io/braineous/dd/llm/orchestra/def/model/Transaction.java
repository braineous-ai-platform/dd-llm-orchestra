package io.braineous.dd.llm.orchestra.def.model;

import io.braineous.dd.llm.orchestra.core.model.OrchestraBaseModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Dev-authored Agentic Transaction definition.
 *
 * <p>
 * A Transaction represents a single coarse-grained agentic execution boundary.
 * All contained Queries execute in deterministic order and are evaluated
 * together under a single policy gate.
 * </p>
 *
 * <p>
 * Conceptually, this mirrors a service-layer transaction:
 * </p>
 *
 * <ul>
 *     <li>Queries execute in declared order</li>
 *     <li>Results are staged (no mutation yet)</li>
 *     <li>A policy gate defines the commit boundary (HITL in v0)</li>
 *     <li>If approved, downstream mutations execute in {@code commitOrder}</li>
 *     <li>If rejected, no mutation occurs</li>
 * </ul>
 *
 * <p>
 * The {@code commitOrder} list must contain exactly all Query IDs once each.
 * It defines the explicit mutation sequence for downstream systems.
 * </p>
 *
 * <p>
 * Internal execution steps, tool invocation, scoring overlays,
 * and policy definitions are not exposed here. Those concerns are
 * compiled deterministically by the Orchestra runtime.
 * </p>
 */
public class Transaction extends OrchestraBaseModel {

    private String description;

    private List<Query> queries = new ArrayList<>();

    private List<String> commitOrder = new ArrayList<>();

    public Transaction() {
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Query> getQueries() {
        return queries;
    }

    public void setQueries(List<Query> queries) {
        this.queries = queries;
    }

    public List<String> getCommitOrder() {
        return commitOrder;
    }

    public void setCommitOrder(List<String> commitOrder) {
        this.commitOrder = commitOrder;
    }
}
