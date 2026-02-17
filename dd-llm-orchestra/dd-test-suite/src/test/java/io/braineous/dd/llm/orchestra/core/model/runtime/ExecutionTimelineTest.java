package io.braineous.dd.llm.orchestra.core.model.runtime;

import ai.braineous.rag.prompt.observe.Console;
import io.braineous.dd.llm.orchestra.core.model.OrchestraBaseModel;

public class ExecutionTimelineTest {

    @org.junit.jupiter.api.Test
    public void serialize_and_deserialize_roundTrip_preservesAllFields() {

        Console.log("UT", "create execution timeline");

        ExecutionTimeline timeline = new ExecutionTimeline();
        timeline.setId("timeline-001");
        timeline.setVersion("v1");
        timeline.setCreatedAt("2026-02-16T21:10:00Z");
        timeline.setUpdatedAt("2026-02-16T21:11:00Z");
        timeline.setStatus("CREATED");
        timeline.setCorrelationId("corr-timeline-777");
        timeline.setSnapshotHash("hash-timeline-abc");

        timeline.setRequestId("req-001");
        timeline.setWorkflowDefId("wf-001");
        timeline.setWorkflowDefVersion("v1");

        java.util.List<String> events = new java.util.ArrayList<String>();
        events.add("REQUEST_RECEIVED");
        events.add("PROPOSAL_CREATED");
        events.add("POLICY_GATE_PENDING");
        timeline.setEvents(events);

        Console.log("UT", "serialize to json");

        String json = timeline.toJson();
        org.junit.jupiter.api.Assertions.assertNotNull(json);

        Console.log("UT", "deserialize from json");

        ExecutionTimeline restored =
                OrchestraBaseModel.fromJson(json, ExecutionTimeline.class);

        Console.log("UT", "assert roundtrip integrity");

        org.junit.jupiter.api.Assertions.assertEquals("timeline-001", restored.getId());
        org.junit.jupiter.api.Assertions.assertEquals("v1", restored.getVersion());
        org.junit.jupiter.api.Assertions.assertEquals("CREATED", restored.getStatus());
        org.junit.jupiter.api.Assertions.assertEquals("corr-timeline-777", restored.getCorrelationId());
        org.junit.jupiter.api.Assertions.assertEquals("hash-timeline-abc", restored.getSnapshotHash());

        org.junit.jupiter.api.Assertions.assertEquals("req-001", restored.getRequestId());
        org.junit.jupiter.api.Assertions.assertEquals("wf-001", restored.getWorkflowDefId());
        org.junit.jupiter.api.Assertions.assertEquals("v1", restored.getWorkflowDefVersion());

        org.junit.jupiter.api.Assertions.assertEquals(3, restored.getEvents().size());
        org.junit.jupiter.api.Assertions.assertEquals("REQUEST_RECEIVED", restored.getEvents().get(0));
        org.junit.jupiter.api.Assertions.assertEquals("PROPOSAL_CREATED", restored.getEvents().get(1));
        org.junit.jupiter.api.Assertions.assertEquals("POLICY_GATE_PENDING", restored.getEvents().get(2));

        Console.log("UT", "roundtrip test completed successfully");
    }
}

