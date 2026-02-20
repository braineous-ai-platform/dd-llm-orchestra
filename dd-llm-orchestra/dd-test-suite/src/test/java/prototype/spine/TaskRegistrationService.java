package prototype.spine;

import com.netflix.conductor.common.metadata.workflow.WorkflowTask;

import java.util.HashMap;
import java.util.Map;

public class TaskRegistrationService {

    public WorkflowTask execute() {

        WorkflowTask workflowTask = new WorkflowTask();

        // task type == TaskDef name (what worker polls)
        workflowTask.setName("process_payload_task");

        // unique ref name inside workflow
        workflowTask.setTaskReferenceName("process_payload_ref");

        // pass only payload (string) to the worker
        Map<String, Object> inputParams = new HashMap<String, Object>();
        inputParams.put("payload", "${workflow.input.payload}");
        workflowTask.setInputParameters(inputParams);

        return workflowTask;
    }
}

