package io.braineous.dd.llm.orchestra.def.model;

import ai.braineous.rag.prompt.observe.Console;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AssemblyArtifactTest {

    @Test
    public void setConductorWorkflowDef_null_throws() {

        Console.log("UT", "setConductorWorkflowDef_null_throws - start");

        AssemblyArtifact artifact = new AssemblyArtifact();

        try {
            artifact.setWorkflow(null);
            Assertions.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {

            Console.log("UT", e.getMessage());

            Assertions.assertEquals(
                    "workflow cannot be null",
                    e.getMessage()
            );
        }

        Console.log("UT", "setConductorWorkflowDef_null_throws - end");
    }

    @Test
    public void setAndGetConductorWorkflowDef_roundTrip() {

        Console.log("UT", "setAndGetConductorWorkflowDef_roundTrip - start");

        AssemblyArtifact artifact = new AssemblyArtifact();
        Workflow workflow = new Workflow();

        artifact.setWorkflow(workflow);

        Console.log("UT", artifact.getWorkflow());

        Assertions.assertSame(workflow, artifact.getWorkflow());

        Console.log("UT", "setAndGetConductorWorkflowDef_roundTrip - end");
    }
}

