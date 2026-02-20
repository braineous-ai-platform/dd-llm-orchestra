package prototype.spine;

import com.netflix.conductor.common.metadata.workflow.WorkflowDef;

import java.util.HashMap;
import java.util.Map;

public class RunWorkflow {

    public static void main(String[] args) throws Exception{
        WorkflowRegistrationService ws = new WorkflowRegistrationService();

        //1. register the workflow
        WorkflowDef def = ws.execute();


        //2. start_workflow
        Map<String, Object> input = new HashMap<>();
        input.put("meta", "\"meta\": {\n" +
                "    \"requestId\": \"REQ-1001\",\n" +
                "    \"submittedAt\": 1739923200000\n" +
                "  }");
        input.put("payload", "\"payload\": {\n" +
                "    \"action\": \"PROCESS_ORDER\",\n" +
                "    \"entityId\": \"ORD-9001\",\n" +
                "    \"amount\": 1250\n" +
                "  }");
        input.put("state", "\"state\": {\n" +
                "    \"approved\": false,\n" +
                "    \"committed\": false,\n" +
                "    \"emitted\": false\n" +
                "  }");

        WorkflowService wos = new WorkflowService();
        wos.startWorkflow(def, input);

        //3. start payload_task_service
        PayloadTaskService payloadTaskService = new PayloadTaskService();
        payloadTaskService.start();
    }
}
