package prototype.spine;

import com.netflix.conductor.client.http.MetadataClient;
import com.netflix.conductor.client.http.WorkflowClient;
import com.netflix.conductor.common.metadata.workflow.StartWorkflowRequest;
import com.netflix.conductor.common.metadata.workflow.WorkflowDef;

import java.util.Map;

public class WorkflowService {

    public String startWorkflow(WorkflowDef def, Map<String, Object> input) {
        // baseUrl example: "http://localhost:8080/api/"
        String baseUrl = "http://localhost:8080/api/";

        MetadataClient metadataClient = new MetadataClient();
        metadataClient.setRootURI(baseUrl);

        WorkflowClient workflowClient = new WorkflowClient();
        workflowClient.setRootURI(baseUrl);

        StartWorkflowRequest req = new StartWorkflowRequest();
        req.setName(def.getName());
        req.setVersion(def.getVersion());
        req.setInput(input);

        return workflowClient.startWorkflow(req);
    }
}
