package io.braineous.dd.llm.orchestra.def.service;

import ai.braineous.rag.prompt.observe.Console;
import com.netflix.conductor.common.metadata.workflow.WorkflowDef;
import io.braineous.dd.llm.orchestra.def.model.ExecutionResult;
import io.braineous.dd.llm.orchestra.def.model.Query;
import io.braineous.dd.llm.orchestra.def.model.Transaction;
import io.braineous.dd.llm.orchestra.def.model.Workflow;
import io.braineous.dd.llm.orchestra.wf.runtime.ConductorClientService;
import io.braineous.dd.llm.orchestra.wf.runtime.WorkflowRegistrationService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@QuarkusTest
public class ExecutorIT {

    @Inject
    private Executor executor;

    @Inject
    private WorkflowRegistrationService workflowRegistrationService;

    @Inject
    private ConductorClientService clientService;

    @Test
    public void it_executes_workflow_happy_path_end_to_end() throws Exception {

        Workflow workflow = buildWorkflow();

        WorkflowDef def = translate(workflow);
        workflowRegistrationService.register(def);

        Map<String, Object> input = new HashMap<String, Object>();
        input.put("k1", "v1");

        ExecutionResult result = executor.executeTransaction(workflow, input);

        Console.log("it", "executionResult=" + result);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isSuccess());

        sleep(15000);
    }

    private WorkflowDef translate(Workflow workflow) throws Exception {
        Method m = Executor.class.getDeclaredMethod("translateToConductorDef", Workflow.class);
        m.setAccessible(true);
        return (WorkflowDef) m.invoke(executor, workflow);
    }

    private Workflow buildWorkflow() {

        String name = "fno_rebook_v1_it_" + System.currentTimeMillis();

        Workflow workflow = new Workflow();
        workflow.setName(name);
        workflow.setDescription("Rebook disrupted passengers");

        Transaction tx = new Transaction();
        tx.setDescription("Agentic flow: gather → decide → draft. Commit after HITL approval.");

        List<Query> queries = new ArrayList<Query>();

        Query q1 = new Query();
        q1.setId("q1_fetch_options");
        q1.setDescription("Gather viable rebooking options.");
        q1.setSql("select decision from llm where task = \"Fetch viable rebooking options\" and factId = \"cgo:pnr:123\" and relatedFacts = \"cgo:[f1,f2]\"");
        queries.add(q1);

        Query q2 = new Query();
        q2.setId("q2_rank_and_pick");
        q2.setDescription("Rank options and pick best.");
        q2.setSql("select decision from llm where task = \"Rank options and pick best\" and factId = \"cgo:pnr:123\" and relatedFacts = \"cgo:[f1,f2,f3]\"");
        queries.add(q2);

        Query q3 = new Query();
        q3.setId("q3_customer_message");
        q3.setDescription("Draft customer message.");
        q3.setSql("select decision from llm where task = \"Draft customer message\" and factId = \"cgo:pnr:123\" and relatedFacts = \"cgo:[f4,f5]\"");
        queries.add(q3);

        tx.setQueries(queries);

        List<String> commitOrder = new ArrayList<String>();
        commitOrder.add("q1_fetch_options");
        commitOrder.add("q2_rank_and_pick");
        commitOrder.add("q3_customer_message");

        tx.setCommitOrder(commitOrder);

        workflow.setTransaction(tx);

        return workflow;
    }

    private void waitForWorkflowCompletion(String workflowId, long timeoutMillis) {
        long start = System.currentTimeMillis();

        while (true) {

            com.netflix.conductor.common.run.Workflow wf =
                    workflowClient().getWorkflow(workflowId, true);

            if (wf == null) {
                Assertions.fail("Workflow not found: " + workflowId);
            }

            String status = String.valueOf(wf.getStatus());
            Console.log("it.workflow.status", "id=" + workflowId + " status=" + status);

            if ("COMPLETED".equals(status)) {
                return;
            }

            if ("FAILED".equals(status) || "TERMINATED".equals(status) || "TIMED_OUT".equals(status)) {
                Console.log("it.workflow.dump", String.valueOf(wf));
                Assertions.fail("Workflow ended non-successfully: " + status + " id=" + workflowId);
            }

            long elapsed = System.currentTimeMillis() - start;
            if (elapsed > timeoutMillis) {
                Console.log("it.workflow.dump", String.valueOf(wf));
                Assertions.fail("Timeout waiting for COMPLETED. id=" + workflowId + " lastStatus=" + status);
            }

            sleep(250);
        }
    }

    private com.netflix.conductor.client.http.WorkflowClient workflowClient() {
        return clientService.getWorkflowClient();
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignored) {
        }
    }
}