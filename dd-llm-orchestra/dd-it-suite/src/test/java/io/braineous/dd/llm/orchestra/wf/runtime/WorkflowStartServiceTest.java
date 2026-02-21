package io.braineous.dd.llm.orchestra.wf.runtime;

import ai.braineous.rag.prompt.observe.Console;
import com.netflix.conductor.common.metadata.workflow.WorkflowDef;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class WorkflowStartServiceTest {

    @Inject
    private WorkflowStartService workflowStartService;

    @Inject
    private WorkflowRegistrationService workflowRegistrationService;

    @Test
    public void it_starts_workflow_and_is_visible_in_conductor() throws Exception {
        WorkflowDef def = buildWorkflowDef(); // same helper: dynamic name + 1 task
        workflowRegistrationService.register(def);

        java.util.Map<String, Object> input = new java.util.HashMap<String, Object>();
        input.put("k1", "v1");

        Console.log("it", "start.begin name=" + def.getName() + " version=" + def.getVersion());

        String workflowId = workflowStartService.start(def, input);

        Console.log("it", "start.workflowId=" + workflowId);

        org.junit.jupiter.api.Assertions.assertNotNull(workflowId);
        org.junit.jupiter.api.Assertions.assertTrue(workflowId.trim().length() > 0);

        String json = awaitWorkflowExecution(workflowId, 10, 500);

        Console.log("it", "start.execution " + json);

        org.junit.jupiter.api.Assertions.assertTrue(json.contains("\"workflowId\":\"" + workflowId + "\""));
        // different conductor versions vary; this usually exists:
        org.junit.jupiter.api.Assertions.assertTrue(json.contains(def.getName()));
    }

    private WorkflowDef buildWorkflowDef() {
        String name = "it_wf_" + System.currentTimeMillis();

        WorkflowDef def = new WorkflowDef();
        def.setName(name);
        def.setVersion(1);
        def.setDescription("IT workflow");

        java.util.List<com.netflix.conductor.common.metadata.workflow.WorkflowTask> tasks =
                new java.util.ArrayList<com.netflix.conductor.common.metadata.workflow.WorkflowTask>();

        com.netflix.conductor.common.metadata.workflow.WorkflowTask t =
                new com.netflix.conductor.common.metadata.workflow.WorkflowTask();
        t.setName("it_dummy_task");
        t.setTaskReferenceName("t1");
        t.setType("SIMPLE");

        tasks.add(t);
        def.setTasks(tasks);

        return def;
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

            Console.log("it", "awaitWorkflowExecution attempt=" + attempt + " status=" + status);
            Thread.sleep(sleepMs);
            attempt = attempt + 1;
        }

        throw new AssertionError("Workflow execution not visible in Conductor: " + workflowId);
    }
}
