package io.braineous.dd.llm.orchestra.wf.runtime;

import ai.braineous.rag.prompt.observe.Console;
import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.metadata.tasks.TaskResult;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class LLMDDTaskWorker {

    public TaskResult execute(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Task must not be null");
        }
        if (task.getTaskId() == null || task.getTaskId().trim().isEmpty()) {
            throw new IllegalArgumentException("TaskId must not be blank");
        }

        Console.log("worker.execute.taskId", task.getTaskId());

        TaskResult result = new TaskResult(task);
        result.setStatus(TaskResult.Status.COMPLETED);

        Map<String, Object> out = new HashMap<String, Object>();
        out.put("ok", Boolean.TRUE);
        out.put("echo_taskId", task.getTaskId());
        result.setOutputData(out);

        return result;
    }
}
