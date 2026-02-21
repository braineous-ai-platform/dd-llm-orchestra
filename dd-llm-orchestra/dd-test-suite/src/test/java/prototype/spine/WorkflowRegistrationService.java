package prototype.spine;

import ai.braineous.rag.prompt.observe.Console;

import com.netflix.conductor.client.http.MetadataClient;
import com.netflix.conductor.client.http.WorkflowClient;
import com.netflix.conductor.common.metadata.workflow.WorkflowDef;
import com.netflix.conductor.common.metadata.workflow.WorkflowTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WorkflowRegistrationService {

    private TaskRegistrationService taskRegistrationService = new TaskRegistrationService();

    public WorkflowDef execute(){
        try {
            // baseUrl example: "http://localhost:8080/api/"
            String baseUrl = "http://localhost:8080/api/";

            MetadataClient metadataClient = new MetadataClient();
            metadataClient.setRootURI(baseUrl);

            WorkflowClient workflowClient = new WorkflowClient();
            workflowClient.setRootURI(baseUrl);

            //create workflow
            WorkflowDef workflowDef = this.createWorkflowDef();

            //register workflow
            this.publishWorkflowDef(metadataClient, workflowDef);

            return workflowDef;

        }catch (Exception e){
            Console.log("___registration_exception___", e.getMessage());
            return null;
        }
    }

    public void publishWorkflowDef(MetadataClient metadataClient, WorkflowDef workflowDef) {
        // For OSS Conductor: metadata endpoint is /metadata/workflow
        // SDK typically supports: metadataClient.registerWorkflowDef(workflowDef) OR metadataClient.updateWorkflowDefs(...)
        metadataClient.registerWorkflowDef(workflowDef);
    }

    public WorkflowDef createWorkflowDef() {
        WorkflowDef workflowDef = new WorkflowDef();

        WorkflowTask workflowTask = this.taskRegistrationService.execute();
        List<WorkflowTask> tasks = new ArrayList<WorkflowTask>();
        tasks.add(workflowTask);

        workflowDef.setName("llmdd_orchestra_workflow_8");
        workflowDef.setOwnerEmail("test@orkes.io");
        workflowDef.setTimeoutSeconds(600);
        workflowDef.setTimeoutPolicy(WorkflowDef.TimeoutPolicy.TIME_OUT_WF);

        // Your workflow input contract: Map<String,String> with JSON strings
        workflowDef.setInputParameters(Arrays.asList("meta", "payload", "state"));

        workflowDef.setDescription("Workflow to monitor order state");
        workflowDef.setTasks(tasks);

        return workflowDef;
    }
}
