package io.braineous.dd.llm.orchestra.def.service;

import ai.braineous.rag.prompt.observe.Console;
import io.braineous.dd.llm.orchestra.def.model.PublishResult;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class WorkflowCompilerIT {

    @Inject
    private WorkflowCompiler compiler;

    @Test
    public void it_compiles_and_registers_workflow_in_conductor_metadata() throws Exception {

        String json = buildDevJson();

        Console.log("it", "workflow.compiler.it.start");

        PublishResult result = compiler.compile(json);

        Console.log("it", "workflow.compiler.it.result " + String.valueOf(result));

        org.junit.jupiter.api.Assertions.assertNotNull(result);
        org.junit.jupiter.api.Assertions.assertTrue(result.isSuccess());
        org.junit.jupiter.api.Assertions.assertNotNull(result.getEngineWorkflowName());
        org.junit.jupiter.api.Assertions.assertNotNull(result.getEngineWorkflowVersion());

        String engineName = result.getEngineWorkflowName();

        String meta = awaitWorkflowMetadata(engineName, 10, 500);

        Console.log("it", "workflow.compiler.it.fetched name=" + engineName);

        org.junit.jupiter.api.Assertions.assertTrue(meta.contains("\"name\":\"" + engineName + "\""));
    }

    private String buildDevJson() {

        String name = "it_orch_compile_" + System.currentTimeMillis();

        return "{\n" +
                "  \"name\": \"" + name + "\",\n" +
                "  \"description\": \"IT dev_json compile -> assembler -> conductor\",\n" +
                "  \"transaction\": {\n" +
                "    \"description\": \"Agentic flow\",\n" +
                "    \"queries\": [\n" +
                "      {\n" +
                "        \"id\": \"q1_fetch_options\",\n" +
                "        \"description\": \"Gather viable rebooking options.\",\n" +
                "        \"sql\": \"select decision from llm where task = \\\"Fetch\\\"\"\n" +
                "      }\n" +
                "    ],\n" +
                "    \"commitOrder\": [\"q1_fetch_options\"]\n" +
                "  }\n" +
                "}";
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