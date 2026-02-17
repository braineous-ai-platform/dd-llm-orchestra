package io.braineous.dd.llm.orchestra.core.model.runtime;

import ai.braineous.rag.prompt.models.cgo.graph.GraphSnapshot;
import ai.braineous.rag.prompt.models.cgo.graph.SnapshotHash;
import ai.braineous.rag.prompt.observe.Console;
import io.braineous.dd.llm.orchestra.core.model.OrchestraBaseModel;

public class ContextSnapshotTest {

    @org.junit.jupiter.api.Test
    public void serialize_and_deserialize_roundTrip_preservesAllFields() {

        Console.log("UT", "create context snapshot");

        ContextSnapshot snapshot = new ContextSnapshot();
        snapshot.setId("snapshot-001");
        snapshot.setVersion("v1");
        snapshot.setCreatedAt("2026-02-16T21:20:00Z");
        snapshot.setUpdatedAt("2026-02-16T21:21:00Z");
        snapshot.setStatus("CREATED");
        snapshot.setCorrelationId("corr-snap-222");
        snapshot.setSnapshotHash("hash-snapshot-xyz");

        snapshot.setRequestId("req-001");
        snapshot.setWorkflowDefId("wf-001");
        snapshot.setWorkflowDefVersion("v1");

        Console.log("UT", "create graph snapshot (empty maps, no guessing)");

        java.util.Map<String, ai.braineous.rag.prompt.cgo.api.Fact> nodes =
                new java.util.LinkedHashMap<String, ai.braineous.rag.prompt.cgo.api.Fact>();

        java.util.Map<String, ai.braineous.rag.prompt.cgo.api.Edge> edges =
                new java.util.LinkedHashMap<String, ai.braineous.rag.prompt.cgo.api.Edge>();

        GraphSnapshot graph = new GraphSnapshot(nodes, edges);
        snapshot.setGraphSnapshot(graph);

        Console.log("UT", "serialize to json");

        String json = snapshot.toJson();
        org.junit.jupiter.api.Assertions.assertNotNull(json);

        Console.log("UT", "deserialize from json");

        ContextSnapshot restored =
                OrchestraBaseModel.fromJson(json, ContextSnapshot.class);

        Console.log("UT", "assert roundtrip integrity");

        org.junit.jupiter.api.Assertions.assertEquals("snapshot-001", restored.getId());
        org.junit.jupiter.api.Assertions.assertEquals("v1", restored.getVersion());
        org.junit.jupiter.api.Assertions.assertEquals("CREATED", restored.getStatus());
        org.junit.jupiter.api.Assertions.assertEquals("corr-snap-222", restored.getCorrelationId());
        org.junit.jupiter.api.Assertions.assertEquals("hash-snapshot-xyz", restored.getSnapshotHash());

        org.junit.jupiter.api.Assertions.assertEquals("req-001", restored.getRequestId());
        org.junit.jupiter.api.Assertions.assertEquals("wf-001", restored.getWorkflowDefId());
        org.junit.jupiter.api.Assertions.assertEquals("v1", restored.getWorkflowDefVersion());

        org.junit.jupiter.api.Assertions.assertNotNull(restored.getGraphSnapshot());
        org.junit.jupiter.api.Assertions.assertNotNull(restored.getGraphSnapshot().nodes());
        org.junit.jupiter.api.Assertions.assertNotNull(restored.getGraphSnapshot().edges());
        org.junit.jupiter.api.Assertions.assertEquals(0, restored.getGraphSnapshot().nodes().size());
        org.junit.jupiter.api.Assertions.assertEquals(0, restored.getGraphSnapshot().edges().size());

        Console.log("UT", "validate snapshotHash computation (empty graph)");

        SnapshotHash hash = restored.getGraphSnapshot().snapshotHash();
        org.junit.jupiter.api.Assertions.assertNotNull(hash);
        org.junit.jupiter.api.Assertions.assertNotNull(hash.getValue());
        org.junit.jupiter.api.Assertions.assertEquals(64, hash.getValue().length());

        Console.log("UT", "roundtrip test completed successfully");
    }
}


