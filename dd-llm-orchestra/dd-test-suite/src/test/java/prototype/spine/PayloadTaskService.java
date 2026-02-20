package prototype.spine;

import com.netflix.conductor.client.http.TaskClient;
import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.metadata.tasks.TaskResult;

public class PayloadTaskService {

    private static final String BASE_URL = "http://localhost:8080/api/";
    private static final String TASK_TYPE = "process_payload_task";

    private final TaskClient taskClient;
    private final PayloadWorker worker;

    public PayloadTaskService() {
        this.taskClient = new TaskClient();
        this.taskClient.setRootURI(BASE_URL);
        this.worker = new PayloadWorker();
    }

    public void start() {

        System.out.println("Starting PayloadTaskService for task type: " + TASK_TYPE);

        while (true) {
            try {

                Task task = taskClient.pollTask(
                        TASK_TYPE,
                        "payload-worker-id", null
                );

                if (task == null || task.getTaskId() == null) {
                    sleep(250);
                    continue;
                }

                System.out.println("___TASK__RECEIVED__");

                TaskResult result = worker.execute(task);

                System.out.println("RESULT: " + result);

                taskClient.updateTask(result);

                System.out.println("Updated task: " + task.getTaskId());

            } catch (Exception e) {
                e.printStackTrace();
                sleep(500);
            }
        }
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignored) {
        }
    }
}

