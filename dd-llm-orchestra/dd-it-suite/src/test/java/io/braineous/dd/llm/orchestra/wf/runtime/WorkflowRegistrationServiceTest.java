package io.braineous.dd.llm.orchestra.wf.runtime;

import ai.braineous.rag.prompt.observe.Console;
import com.netflix.conductor.common.metadata.workflow.WorkflowDef;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

//@QuarkusTest
public class WorkflowRegistrationServiceTest {

    @Inject
    private WorkflowRegistrationService workflowRegistrationService;

    //@Test
    public void it_registers_workflow_in_conductor_metadata() throws Exception {
        WorkflowDef def = buildWorkflowDef();

        Console.log("it", "register.start name=" + def.getName());

        workflowRegistrationService.register(def);

        String json = awaitWorkflowMetadata(def.getName(), 10, 500);

        Console.log("it", "register.fetched name=" + def.getName());

        org.junit.jupiter.api.Assertions.assertTrue(json.contains("\"name\":\"" + def.getName() + "\""));
    }


    private WorkflowDef buildWorkflowDef() {
        String name = "it_wf_register_" + System.currentTimeMillis();

        WorkflowDef def = new WorkflowDef();
        def.setName(name);
        def.setVersion(1);
        def.setDescription("IT workflow registration");

        java.util.List<com.netflix.conductor.common.metadata.workflow.WorkflowTask> tasks =
                new java.util.ArrayList<com.netflix.conductor.common.metadata.workflow.WorkflowTask>();

        com.netflix.conductor.common.metadata.workflow.WorkflowTask t =
                new com.netflix.conductor.common.metadata.workflow.WorkflowTask();

        t.setName("it_dummy_task"); // must not be blank
        t.setTaskReferenceName("t1"); // must be unique within workflow
        t.setType("SIMPLE"); // simplest type for registration

        tasks.add(t);
        def.setTasks(tasks);

        return def;
    }

    private String awaitWorkflowMetadata(String workflowName, int maxAttempts, long sleepMs) throws Exception {
        int attempt = 1;
        while (attempt <= maxAttempts) {
            int status = io.restassured.RestAssured
                    .given()
                    .baseUri("http://localhost:8080")
                    .basePath("/api")
                    .when()
                    .get("/metadata/workflow/" + workflowName)
                    .then()
                    .extract()
                    .statusCode();

            if (status == 200) {
                return io.restassured.RestAssured
                        .given()
                        .baseUri("http://localhost:8080")
                        .basePath("/api")
                        .when()
                        .get("/metadata/workflow/" + workflowName)
                        .then()
                        .extract()
                        .asString();
            }

            Console.log("it", "awaitWorkflowMetadata attempt=" + attempt + " status=" + status);
            Thread.sleep(sleepMs);
            attempt = attempt + 1;
        }

        throw new AssertionError("Workflow not visible in Conductor metadata: " + workflowName);
    }
}
