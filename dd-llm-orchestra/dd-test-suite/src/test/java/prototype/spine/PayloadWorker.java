package prototype.spine;

import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.metadata.tasks.TaskResult;

import java.util.Map;

public class PayloadWorker {

    public TaskResult execute(Task task) {

        // Extract input
        Map<String, Object> input = task.getInputData();
        String payload = input == null ? null : (String) input.get("payload");

        // Print payload
        System.out.println("Received payload: " + payload);

        // Mark task as COMPLETED
        TaskResult result = new TaskResult(task);
        result.addOutputData("payload_processed", payload);
        result.setStatus(TaskResult.Status.COMPLETED);

        return result;
    }
}
