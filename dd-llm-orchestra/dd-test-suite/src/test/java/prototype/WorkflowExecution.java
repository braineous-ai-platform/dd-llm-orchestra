package prototype;

import ai.braineous.rag.prompt.observe.Console;
import com.netflix.conductor.client.http.MetadataClient;
import com.netflix.conductor.client.http.WorkflowClient;
import com.netflix.conductor.common.metadata.workflow.StartWorkflowRequest;
import com.netflix.conductor.common.metadata.workflow.WorkflowDef;
import com.netflix.conductor.common.metadata.workflow.WorkflowTask;

import java.util.*;

public class WorkflowExecution {

    public static void main(String[] args) throws Exception{
        // baseUrl example: "http://localhost:8080/api/"
        String baseUrl = "http://localhost:8080/api/";

        MetadataClient metadataClient = new MetadataClient();
        metadataClient.setRootURI(baseUrl);

        WorkflowClient workflowClient = new WorkflowClient();
        workflowClient.setRootURI(baseUrl);

        //register workflow
        WorkflowExecution registration = new WorkflowExecution();

        WorkflowDef workflowDef = registration.createWorkflowDef();
        //registration.publishWorkflowDef(metadataClient, workflowDef);

        //execute workflow
        String id = registration.executeWorkflow(metadataClient, workflowDef, new HashMap<>());
        Console.log("___execution_id___", id);
    }

    public void publishWorkflowDef(MetadataClient metadataClient, WorkflowDef workflowDef) {
        // For OSS Conductor: metadata endpoint is /metadata/workflow
        // SDK typically supports: metadataClient.registerWorkflowDef(workflowDef) OR metadataClient.updateWorkflowDefs(...)
        metadataClient.registerWorkflowDef(workflowDef);
    }

    public String executeWorkflow(MetadataClient workflowClient, WorkflowDef def, Map<String, Object> input){
        WorkflowClient c = new WorkflowClient();
        c.setRootURI("http://localhost:8080/api/");

        StartWorkflowRequest req = new StartWorkflowRequest();
        req.setName(def.getName());
        req.setInput(input);

        return c.startWorkflow(req);
    }

    public WorkflowDef createWorkflowDef(){
        WorkflowDef workflowDef = new WorkflowDef();

        //create simple task
        WorkflowTask workflowTask = new WorkflowTask();
        workflowTask.setTaskReferenceName("hello_llmdd_orchestra");
        workflowTask.setName("hello_llmdd_orchestra_name");
        workflowTask.setTaskReferenceName("hello_llmdd_orchestra_ref");
        List<WorkflowTask> tasks = new ArrayList<>();
        tasks.add(workflowTask);

        workflowDef.setName("test11_workflow");
        workflowDef.setOwnerEmail("test@orkes.io");
        workflowDef.setTimeoutSeconds(600);
        workflowDef.setTimeoutPolicy(WorkflowDef.TimeoutPolicy.TIME_OUT_WF);
        workflowDef.setInputParameters(Arrays.asList("value", "inlineValue"));
        workflowDef.setDescription("Workflow to monitor order state");
        workflowDef.setTasks(tasks);

        return workflowDef;
    }

}
