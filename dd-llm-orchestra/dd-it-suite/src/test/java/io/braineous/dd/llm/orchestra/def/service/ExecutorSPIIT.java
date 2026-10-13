package io.braineous.dd.llm.orchestra.def.service;

import ai.braineous.rag.prompt.cgo.api.Fact;
import ai.braineous.rag.prompt.models.cgo.graph.GraphBuilder;
import ai.braineous.rag.prompt.observe.Console;
import com.netflix.conductor.common.metadata.workflow.WorkflowDef;
import io.braineous.dd.llm.orchestra.def.model.ExecutionResult;
import io.braineous.dd.llm.orchestra.def.model.Query;
import io.braineous.dd.llm.orchestra.def.model.QueryExecutionResult;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@QuarkusTest
public class ExecutorSPIIT {

    private static final String PAY_1001 = "PaymentRequest:PAY-1001";
    private static final String CUST_2001 = "CustomerAccount:CUST-2001";
    private static final String PM_3001 = "PaymentMethod:PM-3001";
    private static final String RISK_4001 = "RiskProfile:RISK-4001";
    private static final String POL_5001 = "MerchantPolicy:POL-5001";

    private static final String PAY_1001_TEXT =
            "{\"id\":\"PaymentRequest:PAY-1001\",\"kind\":\"PaymentRequest\",\"mode\":\"atomic\",\"amount\":\"125.00\",\"currency\":\"USD\"}";
    private static final String CUST_2001_TEXT =
            "{\"id\":\"CustomerAccount:CUST-2001\",\"kind\":\"CustomerAccount\",\"mode\":\"atomic\",\"status\":\"ACTIVE\"}";
    private static final String PM_3001_TEXT =
            "{\"id\":\"PaymentMethod:PM-3001\",\"kind\":\"PaymentMethod\",\"mode\":\"atomic\",\"type\":\"CARD\"}";
    private static final String RISK_4001_TEXT =
            "{\"id\":\"RiskProfile:RISK-4001\",\"kind\":\"RiskProfile\",\"mode\":\"atomic\",\"level\":\"LOW\"}";
    private static final String POL_5001_TEXT =
            "{\"id\":\"MerchantPolicy:POL-5001\",\"kind\":\"MerchantPolicy\",\"mode\":\"atomic\",\"capture\":\"AUTO\"}";

    @Inject
    private ExecutorSPI executor;

    @Inject
    private WorkflowRegistrationService workflowRegistrationService;

    @Inject
    private ConductorClientService clientService;

    @Test
    public void test_1() throws Exception {

        GraphBuilder graphBuilder = GraphBuilder.getInstance();
        graphBuilder.clear();

        graphBuilder.addNode(payFact(PAY_1001, PAY_1001_TEXT));
        graphBuilder.addNode(payFact(CUST_2001, CUST_2001_TEXT));
        graphBuilder.addNode(payFact(PM_3001, PM_3001_TEXT));
        graphBuilder.addNode(payFact(RISK_4001, RISK_4001_TEXT));
        graphBuilder.addNode(payFact(POL_5001, POL_5001_TEXT));

        Workflow workflow = buildWorkflow();

        WorkflowDef def = translate(workflow);
        workflowRegistrationService.register(def);

        Map<String, Object> input = new HashMap<String, Object>();
        input.put("k1", "v1");

        ExecutionResult result = executor.executeTransaction(workflow, input);

        Console.log("ExecutorSPIIT.test_1", "executionResult=" + result);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isSuccess());

        String executionId = result.getExecutionId();
        Assertions.assertNotNull(executionId, "Conductor executionId must be present");
        Assertions.assertFalse(executionId.trim().isEmpty(), "Conductor executionId must be non-blank");

        List<QueryExecutionResult> queryResults = result.getQueryResults();
        Assertions.assertNotNull(queryResults);
        Assertions.assertEquals(3, queryResults.size());

        QueryExecutionResult r1 = queryResults.get(0);
        QueryExecutionResult r2 = queryResults.get(1);
        QueryExecutionResult r3 = queryResults.get(2);

        Assertions.assertNotNull(r1);
        Assertions.assertNotNull(r2);
        Assertions.assertNotNull(r3);

        Assertions.assertEquals("q1_fetch_options", r1.getQueryId());
        Assertions.assertEquals("q2_rank_and_pick", r2.getQueryId());
        Assertions.assertEquals("q3_customer_message", r3.getQueryId());

        Assertions.assertTrue(r1.isSuccess());
        Assertions.assertTrue(r2.isSuccess());
        Assertions.assertTrue(r3.isSuccess());

        Assertions.assertEquals("EXECUTED", r1.getStatus());
        Assertions.assertEquals("EXECUTED", r2.getStatus());
        Assertions.assertEquals("EXECUTED", r3.getStatus());

        String p1 = r1.getQueryResultJson();
        String p2 = r2.getQueryResultJson();
        String p3 = r3.getQueryResultJson();

        Assertions.assertNotNull(p1, "q1 queryResultJson must be present");
        Assertions.assertNotNull(p2, "q2 queryResultJson must be present");
        Assertions.assertNotNull(p3, "q3 queryResultJson must be present");
        Assertions.assertFalse(p1.trim().isEmpty(), "q1 queryResultJson must be non-blank");
        Assertions.assertFalse(p2.trim().isEmpty(), "q2 queryResultJson must be non-blank");
        Assertions.assertFalse(p3.trim().isEmpty(), "q3 queryResultJson must be non-blank");

        Assertions.assertTrue(p1.contains("queryExecutionJson"), "q1 must contain Beam QueryResult.queryExecutionJson");
        Assertions.assertTrue(p2.contains("queryExecutionJson"), "q2 must contain Beam QueryResult.queryExecutionJson");
        Assertions.assertTrue(p3.contains("queryExecutionJson"), "q3 must contain Beam QueryResult.queryExecutionJson");
        Assertions.assertTrue(p1.contains("requestJson"), "q1 must contain Beam QueryResult.requestJson");
        Assertions.assertTrue(p2.contains("requestJson"), "q2 must contain Beam QueryResult.requestJson");
        Assertions.assertTrue(p3.contains("requestJson"), "q3 must contain Beam QueryResult.requestJson");

        Console.log("it.query.surface",
                "q1 id=" + r1.getQueryId() + " success=" + r1.isSuccess()
                        + " status=" + r1.getStatus() + " why=" + r1.getWhy());
        Console.log("it.query.surface",
                "q2 id=" + r2.getQueryId() + " success=" + r2.isSuccess()
                        + " status=" + r2.getStatus() + " why=" + r2.getWhy());
        Console.log("it.query.surface",
                "q3 id=" + r3.getQueryId() + " success=" + r3.isSuccess()
                        + " status=" + r3.getStatus() + " why=" + r3.getWhy());
        Console.log("it.query.payload", "q1 queryResultJson=" + p1);
        Console.log("it.query.payload", "q2 queryResultJson=" + p2);
        Console.log("it.query.payload", "q3 queryResultJson=" + p3);

        waitForWorkflowCompletion(executionId, 60_000L);
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

        String PAY_1001 = "PaymentRequest:PAY-1001";
        String CUST_2001 = "CustomerAccount:CUST-2001";
        String PM_3001 = "PaymentMethod:PM-3001";
        String RISK_4001 = "RiskProfile:RISK-4001";
        String POL_5001 = "MerchantPolicy:POL-5001";

        String sql = ""
                + "select decision, reason, code "
                + "from llm "
                + "where factId = '" + PAY_1001 + "' "
                + "and relatedFactIds = '"
                + CUST_2001 + ","
                + PM_3001 + ","
                + RISK_4001 + ","
                + POL_5001 + "' "
                + "control intent = 'decide_payment_capture', "
                + "action = 'determine', "
                + "subject = 'primary_payment_request', "
                + "decision = 'allow_capture', "
                + "basis = 'related_system_facts', "
                + "goal = 'decision'";

        Query q1 = new Query();
        q1.setId("q1_fetch_options");
        q1.setDescription("Gather viable rebooking options.");
        q1.setSql(sql);
        queries.add(q1);

        Query q2 = new Query();
        q2.setId("q2_rank_and_pick");
        q2.setDescription("Rank options and pick best.");
        q2.setSql(sql);
        queries.add(q2);

        Query q3 = new Query();
        q3.setId("q3_customer_message");
        q3.setDescription("Draft customer message.");
        q3.setSql(sql);
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

    private Fact payFact(String id, String text) {
        return new Fact(id, text, new HashSet<String>(), "atomic");
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
