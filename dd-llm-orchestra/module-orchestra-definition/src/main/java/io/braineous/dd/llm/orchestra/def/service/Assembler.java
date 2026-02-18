package io.braineous.dd.llm.orchestra.def.service;

import ai.braineous.rag.prompt.observe.Console;
import com.netflix.conductor.common.metadata.workflow.WorkflowDef;
import io.braineous.dd.llm.orchestra.def.model.AssemblyArtifact;
import io.braineous.dd.llm.orchestra.def.model.PublishResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class Assembler {

    //TODO: Inject module-orchestra-worklow publisher to NetFlix Conductor

    public Assembler() {
    }

    public PublishResult assemble(WorkflowDef definition) {

        Console.log("assembler.assemble.in", definition);

        if (definition == null) {
            PublishResult r = PublishResult.failure("DEF_NULL", "WorkflowDef is null");
            Console.log("assembler.assemble.out", r);
            return r;
        }

        PublishResult invalid = validateDefinition(definition);
        if (invalid != null) {
            Console.log("assembler.assemble.out", invalid);
            return invalid;
        }

        AssemblyArtifact artifact;
        try {
            artifact = buildArtifact(definition);
        } catch (Exception e) {
            PublishResult r = PublishResult.failure("ARTIFACT_BUILD_FAILED", safeMsg(e));
            Console.log("assembler.assemble.out", r);
            return r;
        }

        PublishResult r = publish(artifact);
        Console.log("assembler.assemble.out", r);
        return r;
    }


    //-----private helpers, etc--------------------------------
    private PublishResult publish(AssemblyArtifact artifact) {

        Console.log("assembler.publish.in", artifact);

        try {
            PublishResult r = doPublish(artifact);
            Console.log("assembler.publish.out", r);
            return r;
        }
        //TODO: activate_once_engine module is implemented
        /*catch (EngineRejectedException e) {
            PublishResult r = PublishResult.failure("ENGINE_REJECTED", safeMsg(e));
            Console.log("assembler.publish.out", r);
            return r;
        }*/
        catch (Exception e) {
            PublishResult r = PublishResult.failure("PUBLISH_FAILED", safeMsg(e));
            Console.log("assembler.publish.out", r);
            return r;
        }
    }

    private PublishResult validateDefinition(WorkflowDef definition) {

        // V0 minimal validation — extend later, but keep reason stable (DEF_INVALID)

        String name = definition.getName();
        Integer version = definition.getVersion();

        if (name == null) {
            return PublishResult.failure("DEF_INVALID", "WorkflowDef.name is required");
        }
        if (name.trim().length() == 0) {
            return PublishResult.failure("DEF_INVALID", "WorkflowDef.name is blank");
        }
        if (version == null) {
            return PublishResult.failure("DEF_INVALID", "WorkflowDef.version is required");
        }
        if (version.intValue() <= 0) {
            return PublishResult.failure("DEF_INVALID", "WorkflowDef.version must be > 0");
        }

        return null;
    }

    private String safeMsg(Exception e) {
        if (e == null) {
            return "unknown";
        }
        String msg = e.getMessage();
        if (msg == null) {
            return e.getClass().getName();
        }
        return msg;
    }

    private AssemblyArtifact buildArtifact(WorkflowDef definition) {

        AssemblyArtifact a = new AssemblyArtifact();
        a.setConductorWorkflowDef(definition);
        return a;
    }


    private PublishResult doPublish(AssemblyArtifact artifact) {
        // V0: delegate to publisher adapter (to be injected)
        // Example expectations:
        // EnginePublishReceipt receipt = publisher.publish(artifact);
        // return PublishResult.success(receipt.getWorkflowName(), receipt.getWorkflowVersion());

        throw new UnsupportedOperationException("publisher not wired yet");
    }

}
