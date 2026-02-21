package io.braineous.dd.llm.orchestra.wf.runtime;

import jakarta.enterprise.context.ApplicationScoped;

import com.netflix.conductor.client.http.MetadataClient;
import com.netflix.conductor.common.metadata.workflow.WorkflowDef;

import jakarta.inject.Inject;

@ApplicationScoped
public class WorkflowRegistrationService {

    @Inject
    private ConductorClientService clientService;

    public WorkflowRegistrationService() {
    }

    public void register(WorkflowDef workflowDef) {
        if (workflowDef == null) {
            throw new IllegalArgumentException("Workflow definition must not be null");
        }
        if (workflowDef.getName() == null || workflowDef.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Workflow definition name must not be blank");
        }

        MetadataClient metadataClient = this.clientService.getMetadataClient();
        if (metadataClient == null) {
            throw new IllegalStateException("MetadataClient not available");
        }

        publishWorkflowDef(metadataClient, workflowDef);
    }

    private void publishWorkflowDef(MetadataClient metadataClient, WorkflowDef workflowDef) {
        // For OSS Conductor: metadata endpoint is /metadata/workflow
        // SDK typically supports: metadataClient.registerWorkflowDef(workflowDef) OR metadataClient.updateWorkflowDefs(...)
        metadataClient.registerWorkflowDef(workflowDef);
    }
}
