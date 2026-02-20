package prototype;

import ai.braineous.rag.prompt.observe.Console;
import com.netflix.conductor.client.http.MetadataClient;
import com.netflix.conductor.client.http.WorkflowClient;
import com.netflix.conductor.common.metadata.workflow.StartWorkflowRequest;
import com.netflix.conductor.common.metadata.workflow.WorkflowDef;
import com.netflix.conductor.common.metadata.workflow.WorkflowTask;

import java.util.*;

public class WorkflowRegistration {

    private static final String BASE_URL = "http://localhost:8080/api/";
    private static final String WF_NAME = "llmdd_orchestra_smoke";
    private static final int WF_VERSION = 1;

    public static void main(String[] args) throws Exception {
        MetadataClient metadataClient = new MetadataClient();
        metadataClient.setRootURI(BASE_URL);

        WorkflowClient workflowClient = new WorkflowClient();
        workflowClient.setRootURI(BASE_URL);

        WorkflowRegistration registration = new WorkflowRegistration();

        WorkflowDef workflowDef = registration.createWorkflowDef();

        // 1) Register / update workflow definition
        registration.publishWorkflowDef(metadataClient, workflowDef);
        Console.log("___workflow_registered___", workflowDef.getName() + ":" + workflowDef.getVersion());

        // 2) Start workflow
        Map<String, Object> input = new HashMap<String, Object>();
        input.put("value", "hello");
        input.put("inlineValue", "world");

        String workflowId = registration.startWorkflow(workflowClient, workflowDef, input);
        Console.log("___workflow_started___", workflowId);

        // 3) Poll workflow state until terminal (or timeout)
        registration.pollUntilTerminal(workflowClient, workflowId, 30, 1000);
        Console.log("___done___", workflowId);
    }

    public void publishWorkflowDef(MetadataClient metadataClient, WorkflowDef workflowDef) {
        // This will register if new, update if exists (behavior depends on server config/version).
        metadataClient.registerWorkflowDef(workflowDef);
    }

    public String startWorkflow(WorkflowClient workflowClient, WorkflowDef def, Map<String, Object> input) {
        StartWorkflowRequest req = new StartWorkflowRequest();
        req.setName(def.getName());
        req.setVersion(def.getVersion());
        req.setInput(input);

        return workflowClient.startWorkflow(req);
    }

    public void pollUntilTerminal(WorkflowClient workflowClient,
                                  String workflowId,
                                  int maxAttempts,
                                  long sleepMs) throws InterruptedException {

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            // includeTasks=true gives you task status visibility in one call
            com.netflix.conductor.common.run.Workflow wf =
                    workflowClient.getWorkflow(workflowId, true);

            String status = wf.getStatus() == null ? "UNKNOWN" : wf.getStatus().name();
            Console.log("___wf_poll___", "attempt=" + attempt + " status=" + status);

            // Minimal visibility: list tasks with refName + status
            List<com.netflix.conductor.common.metadata.tasks.Task> tasks = wf.getTasks();
            if (tasks != null && !tasks.isEmpty()) {
                List<String> taskLines = new ArrayList<String>();
                for (com.netflix.conductor.common.metadata.tasks.Task t : tasks) {
                    String ref = t.getReferenceTaskName();
                    String tStatus = t.getStatus() == null ? "UNKNOWN" : t.getStatus().name();
                    taskLines.add(ref + ":" + tStatus);
                }
                Console.log("___wf_tasks___", taskLines);
            }

            // Terminal states typically: COMPLETED / FAILED / TERMINATED / TIMED_OUT
            if ("COMPLETED".equals(status) ||
                    "FAILED".equals(status) ||
                    "TERMINATED".equals(status) ||
                    "TIMED_OUT".equals(status)) {
                return;
            }

            Thread.sleep(sleepMs);
        }

        Console.log("___wf_poll_timeout___", workflowId);
    }

    public WorkflowDef createWorkflowDef() {
        WorkflowDef workflowDef = new WorkflowDef();
        workflowDef.setName(WF_NAME);
        workflowDef.setVersion(WF_VERSION);

        // Helps some servers validate schema properly
        workflowDef.setSchemaVersion(2);

        workflowDef.setOwnerEmail("test@orkes.io");
        workflowDef.setDescription("Smoke workflow: register -> start -> observe tasks");
        workflowDef.setTimeoutSeconds(600);
        workflowDef.setTimeoutPolicy(WorkflowDef.TimeoutPolicy.TIME_OUT_WF);

        // Input params are optional; set if you want UI to show expected inputs
        workflowDef.setInputParameters(Arrays.asList("value", "inlineValue"));

        // --- Single SIMPLE task (will require a worker to complete) ---
        WorkflowTask t1 = new WorkflowTask();
        t1.setName("SIMPLE"); // Conductor task type
        t1.setTaskReferenceName("hello_llmdd_orchestra"); // stable ref id inside workflow

        // Optional: input template for the task (data mapping)
        Map<String, Object> taskInput = new HashMap<String, Object>();
        taskInput.put("valueFromWorkflow", "${workflow.input.value}");
        taskInput.put("inlineFromWorkflow", "${workflow.input.inlineValue}");
        t1.setInputParameters(taskInput);

        List<WorkflowTask> tasks = new ArrayList<WorkflowTask>();
        tasks.add(t1);

        workflowDef.setTasks(tasks);
        return workflowDef;
    }
}

