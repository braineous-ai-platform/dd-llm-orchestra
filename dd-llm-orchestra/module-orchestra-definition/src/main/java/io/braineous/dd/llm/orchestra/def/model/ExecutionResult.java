package io.braineous.dd.llm.orchestra.def.model;

import io.braineous.dd.llm.orchestra.core.model.OrchestraBaseModel;
import io.braineous.dd.llm.orchestra.core.model.Why;

import java.util.ArrayList;
import java.util.List;

/**
 * ExecutionResult
 *
 * Deterministic outcome of executing a Workflow Transaction (T-shirt queries)
 * via the governance runtime (LLMDD).
 *
 * Truth surface rules:
 * - No exception bubbling at boundary. Executor maps to failure results.
 * - Failures must be expressed via stable WHY codes.
 *
 * V1: Minimal but structured.
 */
public class ExecutionResult extends OrchestraBaseModel {

    private boolean success;

    private Why why;

    private String workflowName;

    private String transactionDescription;

    private List<QueryExecutionResult> queryResults = new ArrayList<QueryExecutionResult>();

    private String stoppedAtQueryId;

    public ExecutionResult() {
    }

    public static ExecutionResult success(String workflowName,
                                          String transactionDescription,
                                          List<QueryExecutionResult> queryResults) {
        ExecutionResult r = new ExecutionResult();
        r.success = true;
        r.workflowName = workflowName;
        r.transactionDescription = transactionDescription;

        if (queryResults != null) {
            r.queryResults = queryResults;
        }

        return r;
    }

    public static ExecutionResult failure(String workflowName,
                                          String transactionDescription,
                                          String stoppedAtQueryId,
                                          String whyCode,
                                          String details,
                                          List<QueryExecutionResult> partialResults) {
        if (whyCode == null) {
            throw new IllegalArgumentException("whyCode cannot be null");
        }
        ExecutionResult r = new ExecutionResult();
        r.success = false;
        r.workflowName = workflowName;
        r.transactionDescription = transactionDescription;
        r.stoppedAtQueryId = stoppedAtQueryId;
        r.why = new Why(whyCode, details);

        if (partialResults != null) {
            r.queryResults = partialResults;
        }

        return r;
    }

    //---------------------------------------------

    public boolean isSuccess() {
        return success;
    }

    public Why getWhy() {
        return why;
    }

    public String getWorkflowName() {
        return workflowName;
    }

    public String getTransactionDescription() {
        return transactionDescription;
    }

    public List<QueryExecutionResult> getQueryResults() {
        return queryResults;
    }

    public String getStoppedAtQueryId() {
        return stoppedAtQueryId;
    }

    //---------------------------------------------

    @Override
    public String toString() {
        return "ExecutionResult{" +
                "success=" + success +
                ", why=" + why +
                ", workflowName='" + workflowName + '\'' +
                ", transactionDescription='" + transactionDescription + '\'' +
                ", queryResults=" + queryResults +
                ", stoppedAtQueryId='" + stoppedAtQueryId + '\'' +
                '}';
    }
}
