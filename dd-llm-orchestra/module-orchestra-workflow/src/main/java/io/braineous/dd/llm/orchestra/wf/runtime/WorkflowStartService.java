package io.braineous.dd.llm.orchestra.wf.runtime;

import com.netflix.conductor.client.http.WorkflowClient;
import com.netflix.conductor.common.metadata.workflow.StartWorkflowRequest;
import com.netflix.conductor.common.metadata.workflow.WorkflowDef;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Map;

@ApplicationScoped
public class WorkflowStartService {

    @Inject
    private ConductorClientService clientService;

    public WorkflowStartService() {
    }

    public String start(WorkflowDef workflowDef, Map<String, Object> input) {
        if (workflowDef == null) {
            throw new IllegalArgumentException("Workflow definition must not be null");
        }
        if (workflowDef.getName() == null || workflowDef.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Workflow definition name must not be blank");
        }
        if (input == null) {
            throw new IllegalArgumentException("Workflow input must not be null");
        }

        WorkflowClient client = this.clientService.getWorkflowClient();
        if (client == null) {
            throw new IllegalStateException("WorkflowClient not available");
        }

        return startWorkflow(client, workflowDef, input);
    }

    private String startWorkflow(WorkflowClient client, WorkflowDef workflowDef, Map<String, Object> input) {
        StartWorkflowRequest req = new StartWorkflowRequest();
        req.setName(workflowDef.getName());
        req.setVersion(workflowDef.getVersion());
        req.setInput(input);

        return client.startWorkflow(req);
    }
}
