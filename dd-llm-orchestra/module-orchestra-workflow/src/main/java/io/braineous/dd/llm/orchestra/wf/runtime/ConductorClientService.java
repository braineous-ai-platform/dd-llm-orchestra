package io.braineous.dd.llm.orchestra.wf.runtime;

import com.netflix.conductor.client.http.MetadataClient;
import com.netflix.conductor.client.http.TaskClient;
import com.netflix.conductor.client.http.WorkflowClient;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ConductorClientService {
    private MetadataClient metadataClient;
    private WorkflowClient workflowClient;
    private TaskClient taskClient;

    @PostConstruct
    public void start(){
        //meta data client
        // baseUrl example: "http://localhost:8080/api/"
        //wire later with CGO config service for local/docker env portability
        String baseUrl = "http://localhost:8080/api/";

        this.metadataClient = new MetadataClient();
        this.metadataClient.setRootURI(baseUrl);

        this.workflowClient = new WorkflowClient();
        this.workflowClient.setRootURI(baseUrl);

        this.taskClient = new TaskClient();
        this.taskClient.setRootURI(baseUrl);

    }

    public MetadataClient getMetadataClient() {
        return metadataClient;
    }

    public WorkflowClient getWorkflowClient() {
        return workflowClient;
    }

    public TaskClient getTaskClient() {
        return taskClient;
    }
}
