package io.braineous.dd.llm.orchestra.def.model;

import io.braineous.dd.llm.orchestra.core.model.OrchestraBaseModel;
import io.braineous.dd.llm.orchestra.core.model.Why;

/**
 * QueryExecutionResult
 *
 * Deterministic per-query outcome for execution. This does NOT implement governance.
 * It records what governance (LLMDD) returned for the query.
 */
public class QueryExecutionResult extends OrchestraBaseModel {

    private String queryId;

    private boolean success;

    private Why why;

    /**
     * V1 stable vocabulary: EXECUTED, NEEDS_APPROVAL, DENIED, ERROR
     */
    private String status;

    public QueryExecutionResult() {
    }

    public static QueryExecutionResult executed(String queryId) {
        if (queryId == null) {
            throw new IllegalArgumentException("queryId cannot be null");
        }
        QueryExecutionResult r = new QueryExecutionResult();
        r.queryId = queryId;
        r.success = true;
        r.status = "EXECUTED";
        return r;
    }

    public static QueryExecutionResult blocked(String queryId, String status, String whyCode, String details) {
        if (queryId == null) {
            throw new IllegalArgumentException("queryId cannot be null");
        }
        if (status == null) {
            throw new IllegalArgumentException("status cannot be null");
        }
        if (whyCode == null) {
            throw new IllegalArgumentException("whyCode cannot be null");
        }
        QueryExecutionResult r = new QueryExecutionResult();
        r.queryId = queryId;
        r.success = false;
        r.status = status;
        r.why = new Why(whyCode, details);
        return r;
    }

    //---------------------------------------------

    public String getQueryId() {
        return queryId;
    }

    public boolean isSuccess() {
        return success;
    }

    public Why getWhy() {
        return why;
    }

    public String getStatus() {
        return status;
    }

    //---------------------------------------------

    @Override
    public String toString() {
        return "QueryExecutionResult{" +
                "queryId='" + queryId + '\'' +
                ", success=" + success +
                ", why=" + why +
                ", status='" + status + '\'' +
                '}';
    }
}
