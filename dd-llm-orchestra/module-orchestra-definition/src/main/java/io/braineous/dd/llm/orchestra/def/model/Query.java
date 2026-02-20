package io.braineous.dd.llm.orchestra.def.model;

import io.braineous.dd.llm.orchestra.core.model.OrchestraBaseModel;

/**
 * Dev-authored Agentic Query definition.
 *
 * <p>
 * A Query represents a single deterministic LLM decision request expressed
 * using the t-shirt SQL model:
 * </p>
 *
 * <pre>
 * select decision
 * from llm
 * where task = ...
 *   and factId = ...
 *   and relatedFacts = ...
 * </pre>
 *
 * <p>
 * Queries are executed in the order defined within a Transaction.
 * Each query result is staged and subject to a policy gate before
 * any downstream mutation occurs.
 * </p>
 *
 * <p>
 * This model does not expose internal execution mechanics such as
 * step expansion, tool invocation, scoring, or policy definitions.
 * Those concerns are compiled deterministically by the Orchestra runtime.
 * </p>
 *
 * <p>
 * The {@code id} field must be unique within a Workflow and is used
 * to define commit ordering at the transaction boundary.
 * </p>
 */
public class Query extends OrchestraBaseModel {

    private String description;
    private String sql;

    public Query() {
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }
}
