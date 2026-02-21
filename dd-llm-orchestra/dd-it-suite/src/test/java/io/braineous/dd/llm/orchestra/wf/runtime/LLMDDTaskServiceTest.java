package io.braineous.dd.llm.orchestra.wf.runtime;

import ai.braineous.rag.prompt.observe.Console;
import com.netflix.conductor.common.metadata.workflow.WorkflowDef;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class LLMDDTaskServiceTest {

    @Inject
    private LLMDDTaskService llmddTaskService;

    @Inject
    private ConductorClientService conductorClientService;

    @Inject
    private WorkflowRegistrationService workflowRegistrationService;

    @Inject
    private WorkflowStartService workflowStartService;

    @Test
    public void it_llmdd_task_worker_completes_workflow() throws Exception {
        // 1) Register TaskDef (many conductors require this)
        registerTaskDef("llmdd_query_task");

        // 2) Register workflow def with SIMPLE task = llmdd_query_task
        WorkflowDef def = buildWorkflowDefWithTask("llmdd_query_task");
        workflowRegistrationService.register(def);

        // 3) Start workflow
        java.util.Map<String, Object> input = new java.util.HashMap<String, Object>();
        input.put("k1", "v1");

        String workflowId = workflowStartService.start(def, input);

        Console.log("it.workflow.started", workflowId);

        org.junit.jupiter.api.Assertions.assertNotNull(workflowId);
        org.junit.jupiter.api.Assertions.assertTrue(workflowId.trim().length() > 0);

        // 4) Drive worker deterministically (no background thread)
        boolean didWork = awaitWorkerDidWork(40, 250); // ~10s worst case
        org.junit.jupiter.api.Assertions.assertTrue(didWork);

        // 5) Assert workflow status completed
        String json = awaitWorkflowExecution(workflowId, 20, 250);

        Console.log("it.workflow.json", json);

        org.junit.jupiter.api.Assertions.assertTrue(json.contains("\"workflowId\":\"" + workflowId + "\""));
        org.junit.jupiter.api.Assertions.assertTrue(json.contains("\"status\":\"COMPLETED\""));
    }


    private WorkflowDef buildWorkflowDefWithTask(String taskName) {
        String name = "it_llmdd_wf_" + System.currentTimeMillis();

        WorkflowDef def = new WorkflowDef();
        def.setName(name);
        def.setVersion(1);
        def.setDescription("IT LLMDD task workflow");

        java.util.List<com.netflix.conductor.common.metadata.workflow.WorkflowTask> tasks =
                new java.util.ArrayList<com.netflix.conductor.common.metadata.workflow.WorkflowTask>();

        com.netflix.conductor.common.metadata.workflow.WorkflowTask t =
                new com.netflix.conductor.common.metadata.workflow.WorkflowTask();

        t.setName(taskName);
        t.setTaskReferenceName("t1");
        t.setType("SIMPLE");

        tasks.add(t);
        def.setTasks(tasks);

        return def;
    }


    private void registerTaskDef(String taskName) {
        com.netflix.conductor.common.metadata.tasks.TaskDef def =
                new com.netflix.conductor.common.metadata.tasks.TaskDef();

        def.setName(taskName);
        def.setRetryCount(0);
        def.setTimeoutSeconds(30);
        def.setResponseTimeoutSeconds(30);

        com.netflix.conductor.client.http.MetadataClient mc = conductorClientService.getMetadataClient();
        if (mc == null) {
            throw new IllegalStateException("MetadataClient not available");
        }

        java.util.List<com.netflix.conductor.common.metadata.tasks.TaskDef> defs =
                new java.util.ArrayList<com.netflix.conductor.common.metadata.tasks.TaskDef>();
        defs.add(def);

        mc.registerTaskDefs(defs);
    }

    private boolean awaitWorkerDidWork(int maxAttempts, long sleepMs) throws Exception {
        int attempt = 1;
        while (attempt <= maxAttempts) {
            boolean didWork = llmddTaskService.pollOnce();
            if (didWork) {
                return true;
            }
            Thread.sleep(sleepMs);
            attempt = attempt + 1;
        }
        return false;
    }


    private String awaitWorkflowExecution(String workflowId, int maxAttempts, long sleepMs) throws Exception {
        int attempt = 1;
        while (attempt <= maxAttempts) {
            int status = io.restassured.RestAssured
                    .given()
                    .baseUri("http://localhost:8080")
                    .basePath("/api")
                    .when()
                    .get("/workflow/" + workflowId)
                    .then()
                    .extract()
                    .statusCode();

            if (status == 200) {
                return io.restassured.RestAssured
                        .given()
                        .baseUri("http://localhost:8080")
                        .basePath("/api")
                        .when()
                        .get("/workflow/" + workflowId)
                        .then()
                        .extract()
                        .asString();
            }

            Console.log("it.await.workflow", "attempt=" + attempt + " status=" + status);
            Thread.sleep(sleepMs);
            attempt = attempt + 1;
        }

        throw new AssertionError("Workflow execution not visible in Conductor: " + workflowId);
    }
}
