package io.braineous.dd.llm.orchestra.def.service;

import ai.braineous.rag.prompt.observe.Console;
import io.braineous.dd.llm.orchestra.def.model.Query;
import io.braineous.dd.llm.orchestra.def.model.RegistrationResult;
import io.braineous.dd.llm.orchestra.def.model.Transaction;
import io.braineous.dd.llm.orchestra.def.model.Workflow;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class ConductorWorkflowPublisherIT {

    @Inject
    private ConductorWorkflowPublisher publisher;

    @Test
    public void it_registers_orchestra_workflow_in_conductor_metadata() throws Exception {

        Workflow wf = buildWorkflow();

        Console.log("it", "orchestra.register.start name=" + wf.getName());

        RegistrationResult result = publisher.publishOrchestraDef(wf);

        Console.log("it", "orchestra.register.result " + String.valueOf(result));

        org.junit.jupiter.api.Assertions.assertNotNull(result);
        org.junit.jupiter.api.Assertions.assertTrue(result.isSuccess());
        org.junit.jupiter.api.Assertions.assertNotNull(result.getEngineWorkflowName());

        String json = awaitWorkflowMetadata(result.getEngineWorkflowName(), 10, 500);

        Console.log("it", "orchestra.register.fetched name=" + result.getEngineWorkflowName());

        org.junit.jupiter.api.Assertions.assertTrue(json.contains("\"name\":\"" + result.getEngineWorkflowName() + "\""));
    }

    private Workflow buildWorkflow() {

        String name = "it_orch_register_" + System.currentTimeMillis();

        Workflow wf = new Workflow();
        wf.setName(name);
        wf.setDescription("IT orchestra registration");

        Transaction tx = new Transaction();

        Query q1 = new Query();
        q1.setId("q1_fetch_options");
        q1.setDescription("Gather viable rebooking options.");
        q1.setSql("select decision from llm where task = \"Fetch\"");

        tx.setQueries(java.util.Arrays.asList(q1));
        wf.setTransaction(tx);

        return wf;
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