package io.braineous.dd.llm.orchestra.wf.runtime;

import ai.braineous.rag.prompt.observe.Console;
import com.netflix.conductor.client.http.TaskClient;
import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.metadata.tasks.TaskResult;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class LLMDDTaskService {

    private static final String TASK_TYPE = "llmdd_query_task";
    private static final String WORKER_ID = "payload-worker-id";

    @Inject
    private ConductorClientService clientService;

    @Inject
    private LLMDDTaskWorker worker;

    @Inject
    @ConfigProperty(name = "orchestra.llmdd.worker.autostart", defaultValue = "true")
    private boolean autoStartEnabled;

    @PostConstruct
    public void start() {
        if (!autoStartEnabled) {
            Console.log("llmdd.worker.disabled", TASK_TYPE);
            return;
        }

        Console.log("llmdd.worker.start", TASK_TYPE);

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                runForever();
            }
        });

        t.setName("llmdd-task-poller");
        t.setDaemon(true);
        t.start();
    }

    private void runForever() {
        TaskClient taskClient = this.clientService.getTaskClient();
        if (taskClient == null) {
            throw new IllegalStateException("TaskClient not available");
        }

        while (true) {
            try {
                boolean didWork = pollOnceInternal(taskClient);
                if (!didWork) {
                    sleep(250);
                }
            } catch (Exception e) {
                Console.log("task.error", String.valueOf(e));
                sleep(500);
            }
        }
    }

    public boolean pollOnce() {
        TaskClient taskClient = this.clientService.getTaskClient();
        if (taskClient == null) {
            throw new IllegalStateException("TaskClient not available");
        }
        return pollOnceInternal(taskClient);
    }

    private boolean pollOnceInternal(TaskClient taskClient) {
        Task task = taskClient.pollTask(TASK_TYPE, WORKER_ID, null);
        if (task == null || task.getTaskId() == null) {
            return false;
        }

        Console.log("task.received", task.getTaskId());

        TaskResult result = worker.execute(task);

        Console.log("task.result", String.valueOf(result));

        taskClient.updateTask(result);

        Console.log("task.updated", task.getTaskId());
        return true;
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignored) {
        }
    }
}
