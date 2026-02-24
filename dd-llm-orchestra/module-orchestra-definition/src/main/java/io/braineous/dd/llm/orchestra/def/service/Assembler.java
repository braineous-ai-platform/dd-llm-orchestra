package io.braineous.dd.llm.orchestra.def.service;

import ai.braineous.rag.prompt.observe.Console;
import io.braineous.dd.llm.orchestra.core.model.Why;
import io.braineous.dd.llm.orchestra.def.model.AssemblyArtifact;
import io.braineous.dd.llm.orchestra.def.model.PublishResult;
import io.braineous.dd.llm.orchestra.def.model.RegistrationResult;
import io.braineous.dd.llm.orchestra.def.model.Workflow;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class Assembler {

    @Inject
    private ConductorWorkflowPublisher publisher;

    //-------------------------------------------------
    //For UT/IT mode testing-only
    private boolean activePublishMode = true;
    void deactivatePublishMode(){
        this.activePublishMode = false;
    }
    //---------------------------------------------------

    public Assembler() {
    }

    public PublishResult assemble(Workflow workflow) {

        Console.log("assembler.assemble.in", workflow);

        if (workflow == null) {
            PublishResult r = PublishResult.failure("DEF_NULL", "WorkflowDef is null");
            Console.log("assembler.assemble.out", r);
            return r;
        }

        PublishResult invalid = validateDefinition(workflow);
        if (invalid != null) {
            Console.log("assembler.assemble.out", invalid);
            return invalid;
        }

        AssemblyArtifact artifact;
        try {
            artifact = buildArtifact(workflow);
        } catch (Exception e) {
            PublishResult r = PublishResult.failure("ARTIFACT_BUILD_FAILED", safeMsg(e));
            Console.log("assembler.assemble.out", r);
            return r;
        }

        if (!activePublishMode) {
            return PublishResult.success(
                    workflow.getName(),
                    Integer.parseInt(workflow.getVersion())
            );
        }

        PublishResult r = publish(artifact);
        Console.log("assembler.assemble.out", r);
        return r;
    }


    //-----private helpers, etc--------------------------------
    private PublishResult publish(AssemblyArtifact artifact) {

        Console.log("assembler.publish.in", artifact);

        try {
            RegistrationResult r = doPublish(artifact);
            Console.log("assembler.publish.out", r);
            if(r == null){
                return PublishResult.failure("PUBLISH_FAILED", "Publisher returned null");
            }
            if (!r.isSuccess()) {
                Why why = r.getWhy();
                if (why == null) {
                    return PublishResult.failure("PUBLISH_FAILED", "Publisher failed without WHY");
                }
                return PublishResult.failure(why.getReason(), why.getDetails());
            }

            PublishResult result = PublishResult.success(
                    artifact.getWorkflow().getName(),
                    Integer.parseInt(artifact.getWorkflow().getVersion())
            );
            return result;
        }
        catch (Exception e) {
            PublishResult r = PublishResult.failure("PUBLISH_FAILED", safeMsg(e));
            Console.log("assembler.publish.out", r);
            return r;
        }
    }

    private PublishResult validateDefinition(Workflow definition) {

        // V0 minimal validation — extend later, but keep reason stable (DEF_INVALID)

        String name = definition.getName();
        String version = definition.getVersion();

        if (name == null) {
            return PublishResult.failure("DEF_INVALID", "WorkflowDef.name is required");
        }
        if (name.trim().length() == 0) {
            return PublishResult.failure("DEF_INVALID", "WorkflowDef.name is blank");
        }
        if (version == null || version.trim().length() == 0){
            return PublishResult.failure("DEF_INVALID", "WorkflowDef.version is required");
        }
        try {
            if (Integer.parseInt(version) <= 0) {
                return PublishResult.failure("DEF_INVALID", "WorkflowDef.version must be > 0");
            }
        }catch(NumberFormatException fne){
            return PublishResult.failure("DEF_INVALID", "WorkflowDef.version must be numeric");
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

    private AssemblyArtifact buildArtifact(Workflow definition) {

        AssemblyArtifact a = new AssemblyArtifact();
        a.setWorkflow(definition);
        return a;
    }


    private RegistrationResult doPublish(AssemblyArtifact artifact) {
        // V0: delegate to publisher adapter (to be injected)
        // Example expectations:
        // EnginePublishReceipt receipt = publisher.publish(artifact);
        // return PublishResult.success(receipt.getWorkflowName(), receipt.getWorkflowVersion());

        Workflow workflow = artifact.getWorkflow();
        RegistrationResult result = this.publisher.publishOrchestraDef(workflow);
        return result;
    }

}
