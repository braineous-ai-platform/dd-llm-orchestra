package io.braineous.dd.llm.orchestra.def.service;

import io.braineous.dd.llm.orchestra.def.model.ExecutionResult;
import io.braineous.dd.llm.orchestra.def.model.QueryExecutionResult;
import io.braineous.dd.llm.orchestra.def.model.Query;
import io.braineous.dd.llm.orchestra.def.model.Transaction;
import io.braineous.dd.llm.orchestra.def.model.Workflow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Executor {

    public ExecutionResult executeTransaction(Workflow workflow) {

        if (workflow == null) {
            return ExecutionResult.failure(null, null, null,
                    "ORCH_EXEC_NULL_WORKFLOW",
                    "workflow cannot be null",
                    null);
        }

        if (workflow.getTransaction() == null) {
            return ExecutionResult.failure(workflow.getName(), null, null,
                    "ORCH_EXEC_NULL_TRANSACTION",
                    "workflow.transaction cannot be null",
                    null);
        }

        Transaction tx = workflow.getTransaction();

        if (tx.getCommitOrder() == null || tx.getCommitOrder().isEmpty()) {
            return ExecutionResult.failure(workflow.getName(), tx.getDescription(), null,
                    "ORCH_EXEC_EMPTY_COMMIT_ORDER",
                    "transaction.commitOrder cannot be null/empty",
                    null);
        }

        Map<String, Query> byId = indexQueries(tx);

        // Surface gate: commitOrder must contain exactly all Query IDs once each.
        int commitOrderSize = tx.getCommitOrder().size();

        int queryListSize = 0;
        if (tx.getQueries() != null) {
            queryListSize = tx.getQueries().size();
        }

        int uniqueQueryIdCount = byId.size();

        if (uniqueQueryIdCount != commitOrderSize) {
            return ExecutionResult.failure(workflow.getName(), tx.getDescription(), null,
                    "ORCH_EXEC_COMMIT_ORDER_MISMATCH",
                    "commitOrder size must equal unique query id count. commitOrderSize=" + commitOrderSize +
                            ", uniqueQueryIdCount=" + uniqueQueryIdCount +
                            ", queriesListSize=" + queryListSize,
                    null);
        }

        List<QueryExecutionResult> results = new ArrayList<QueryExecutionResult>();

        for (int i = 0; i < tx.getCommitOrder().size(); i++) {

            String queryId = tx.getCommitOrder().get(i);

            if (queryId == null) {
                return ExecutionResult.failure(workflow.getName(), tx.getDescription(), null,
                        "ORCH_EXEC_NULL_QUERY_ID",
                        "commitOrder contains null queryId at index " + i,
                        results);
            }

            Query q = byId.get(queryId);
            if (q == null) {
                return ExecutionResult.failure(workflow.getName(), tx.getDescription(), queryId,
                        "ORCH_EXEC_QUERY_NOT_FOUND",
                        "commitOrder references queryId not found in transaction.queries: " + queryId,
                        results);
            }

            QueryExecutionResult r = executeQueryViaGovernance(workflow, tx, q);

            results.add(r);

            if (!r.isSuccess()) {
                return ExecutionResult.failure(workflow.getName(), tx.getDescription(), queryId,
                        "ORCH_EXEC_STOPPED",
                        "execution stopped at queryId=" + queryId + ", status=" + r.getStatus(),
                        results);
            }
        }

        return ExecutionResult.success(workflow.getName(), tx.getDescription(), results);
    }

    private Map<String, Query> indexQueries(Transaction tx) {

        Map<String, Query> byId = new HashMap<String, Query>();

        if (tx.getQueries() == null) {
            return byId;
        }

        for (int i = 0; i < tx.getQueries().size(); i++) {

            Query q = tx.getQueries().get(i);

            if (q == null) {
                continue;
            }

            if (q.getId() == null) {
                continue;
            }

            if (!byId.containsKey(q.getId())) {
                byId.put(q.getId(), q);
            }
        }

        return byId;
    }

    private QueryExecutionResult executeQueryViaGovernance(Workflow workflow, Transaction tx, Query q) {

        if (q.getSql() == null || q.getSql().trim().isEmpty()) {
            return QueryExecutionResult.blocked(q.getId(), "ERROR",
                    "ORCH_EXEC_EMPTY_SQL",
                    "query.sql cannot be null/empty for queryId=" + q.getId());
        }

        return QueryExecutionResult.executed(q.getId());
    }
}
