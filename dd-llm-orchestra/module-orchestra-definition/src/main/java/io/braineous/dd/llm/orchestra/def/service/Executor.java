package io.braineous.dd.llm.orchestra.def.service;

import ai.braineous.rag.prompt.observe.Console;
import com.netflix.conductor.common.metadata.workflow.WorkflowDef;
import io.braineous.dd.llm.orchestra.def.model.ExecutionResult;
import io.braineous.dd.llm.orchestra.def.model.QueryExecutionResult;
import io.braineous.dd.llm.orchestra.def.model.Query;
import io.braineous.dd.llm.orchestra.def.model.Transaction;
import io.braineous.dd.llm.orchestra.def.model.Workflow;
import io.braineous.dd.llm.pg.model.TxStepResult;
import io.braineous.dd.llm.pg.services.PolicyGateOrchestrator;
import io.braineous.dd.llm.query.client.QueryResult;
import io.braineous.dd.llm.query.client.RESTClient;
import io.braineous.dd.llm.transaction.model.TxExecutionRequest;
import io.braineous.dd.llm.transaction.model.TxExecutionResult;
import io.braineous.dd.llm.transaction.model.TxStepRequest;
import io.braineous.dd.llm.transaction.services.TransactionService;
import io.braineous.dd.llm.orchestra.wf.runtime.LLMDDTaskService;
import io.braineous.dd.llm.orchestra.wf.runtime.WorkflowRegistrationService;
import io.braineous.dd.llm.orchestra.wf.runtime.WorkflowStartService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class Executor implements ExecutorSPI {

    @Inject
    private WorkflowRegistrationService registrationService;

    @Inject
    private WorkflowStartService startService;

    //-------------------------------------------------
    // For UT/IT mode testing-only
    private boolean activeExeMode = true;

    void deactivateExeMode() {
        this.activeExeMode = false;
    }
    //---------------------------------------------------

    public ExecutionResult executeTransaction(Workflow workflow, Map<String, Object> input) {

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

        if (input == null) {
            return ExecutionResult.failure(workflow.getName(),
                    workflow.getTransaction().getDescription(),
                    null,
                    "ORCH_EXEC_NULL_INPUT",
                    "execution input cannot be null",
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

        TxExecutionRequest request = new TxExecutionRequest();
        request.setDescription(tx.getDescription());

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

            TxStepRequest step = new TxStepRequest();
            step.setId(q.getId());
            step.setDescription(q.getDescription());
            step.setSql(q.getSql());
            request.getSteps().add(step);
            request.getCommitOrder().add(queryId);
        }

        TransactionService transactionService =
                new TransactionService(new RESTClient(), new PolicyGateOrchestrator());

        TxExecutionResult txResult = transactionService.execute(request);

        String stoppedAtQueryId = null;

        if (txResult != null && txResult.getStepResults() != null) {
            for (int i = 0; i < txResult.getStepResults().size(); i++) {
                TxStepResult stepResult = txResult.getStepResults().get(i);
                if (stepResult == null) {
                    continue;
                }

                QueryExecutionResult mapped = mapStepResult(stepResult);
                results.add(mapped);

                if (!mapped.isSuccess()) {
                    stoppedAtQueryId = mapped.getQueryId();
                }
            }
        }

        if (txResult == null || !txResult.isApproved()) {
            if (stoppedAtQueryId == null && !results.isEmpty()) {
                QueryExecutionResult last = results.get(results.size() - 1);
                if (last != null && !last.isSuccess()) {
                    stoppedAtQueryId = last.getQueryId();
                }
            }

            String status = "ERROR";
            if (stoppedAtQueryId != null) {
                for (int i = 0; i < results.size(); i++) {
                    QueryExecutionResult mapped = results.get(i);
                    if (mapped != null && stoppedAtQueryId.equals(mapped.getQueryId())) {
                        status = mapped.getStatus();
                        break;
                    }
                }
            }

            return ExecutionResult.failure(workflow.getName(), tx.getDescription(), stoppedAtQueryId,
                    "ORCH_EXEC_STOPPED",
                    "execution stopped at queryId=" + stoppedAtQueryId + ", status=" + status,
                    results);
        }

        //---------------------------------------------------------------------------------
        if(!activeExeMode){
            ExecutionResult result = ExecutionResult.success(workflow.getName(), tx.getDescription(), results);
            return result;
        }

        // Runtime bridge (not wired yet — controlled by activeExeMode)
        if (activeExeMode) {

            String name = workflow.getName();
            if (name == null || name.trim().length() == 0) {
                workflow.setName("fno_rebook_v1_" + System.currentTimeMillis());
            }

            WorkflowDef def = translateToConductorDef(workflow);
            this.registrationService.register(def);

            String executionId = this.startService.start(def, input);
            Console.log("__success_executionid___", executionId);

            if (executionId == null || executionId.trim().length() == 0) {

                return ExecutionResult.failure(
                        workflow.getName(),
                        tx.getDescription(),
                        null,
                        "ORCH_EXEC_RUNTIME_START_FAILED",
                        "Conductor start returned empty executionId",
                        results
                );
            }

            ExecutionResult result = ExecutionResult.success(workflow.getName(), tx.getDescription(), results);
            result.setExecutionId(executionId);
            return result;
        }

        ExecutionResult result = ExecutionResult.success(workflow.getName(), tx.getDescription(), results);
        return result;
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

    private QueryExecutionResult mapStepResult(TxStepResult stepResult) {

        String queryId = stepResult.getId();

        if (stepResult.isOk()) {
            QueryExecutionResult mapped = QueryExecutionResult.executed(queryId);
            QueryResult queryResult = stepResult.getQueryResult();
            if (queryResult != null) {
                mapped.setQueryResultJson(queryResult.toJson().toString());
            }
            return mapped;
        }

        QueryResult queryResult = stepResult.getQueryResult();

        String whyCode = "ORCH_EXEC_STOPPED";
        String details = "execution stopped at queryId=" + queryId + ", status=ERROR";

        if (queryResult != null && queryResult.getWhy() != null) {
            if (queryResult.getWhy().reason() != null) {
                whyCode = queryResult.getWhy().reason();
            }
            if (queryResult.getWhy().getDetails() != null) {
                details = queryResult.getWhy().getDetails();
            }
        }

        QueryExecutionResult mapped = QueryExecutionResult.blocked(queryId, "ERROR", whyCode, details);
        if (queryResult != null) {
            mapped.setQueryResultJson(queryResult.toJson().toString());
        }
        return mapped;
    }

    private com.netflix.conductor.common.metadata.workflow.WorkflowDef translateToConductorDef(Workflow workflow) {
        return OrchestraWorkflowCompiler.translateToConductorDef(workflow);
    }
}
