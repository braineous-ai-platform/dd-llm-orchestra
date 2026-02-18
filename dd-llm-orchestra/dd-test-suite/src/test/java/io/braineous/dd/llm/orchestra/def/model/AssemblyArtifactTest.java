package io.braineous.dd.llm.orchestra.def.model;

import ai.braineous.rag.prompt.observe.Console;
import com.netflix.conductor.common.metadata.workflow.WorkflowDef;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AssemblyArtifactTest {

    @Test
    public void setConductorWorkflowDef_null_throws() {

        Console.log("UT", "setConductorWorkflowDef_null_throws - start");

        AssemblyArtifact artifact = new AssemblyArtifact();

        try {
            artifact.setConductorWorkflowDef(null);
            Assertions.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {

            Console.log("UT", e.getMessage());

            Assertions.assertEquals(
                    "conductorWorkflowDef cannot be null",
                    e.getMessage()
            );
        }

        Console.log("UT", "setConductorWorkflowDef_null_throws - end");
    }

    @Test
    public void setAndGetConductorWorkflowDef_roundTrip() {

        Console.log("UT", "setAndGetConductorWorkflowDef_roundTrip - start");

        AssemblyArtifact artifact = new AssemblyArtifact();
        WorkflowDef workflowDef = new WorkflowDef();

        artifact.setConductorWorkflowDef(workflowDef);

        Console.log("UT", artifact.getConductorWorkflowDef());

        Assertions.assertSame(workflowDef, artifact.getConductorWorkflowDef());

        Console.log("UT", "setAndGetConductorWorkflowDef_roundTrip - end");
    }
}

