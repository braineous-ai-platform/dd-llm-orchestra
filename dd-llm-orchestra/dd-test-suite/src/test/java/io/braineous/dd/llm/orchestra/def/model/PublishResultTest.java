package io.braineous.dd.llm.orchestra.def.model;

import ai.braineous.rag.prompt.observe.Console;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.braineous.dd.llm.orchestra.core.model.OrchestraBaseModel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PublishResultTest {

    @Test
    public void success_shouldSerializeToJsonCorrectly() {

        PublishResult r = PublishResult.success("my_workflow", 7);

        String json = r.toJson();
        Console.log("ut.publish_result.success.json", json);

        assertNotNull(json);

        JsonObject obj = JsonParser.parseString(json).getAsJsonObject();

        assertTrue(obj.get("success").getAsBoolean());
        assertEquals("my_workflow", obj.get("engineWorkflowName").getAsString());
        assertEquals(7, obj.get("engineWorkflowVersion").getAsInt());

        assertTrue(obj.has("why"));
        assertTrue(obj.get("why").isJsonNull());
    }

    @Test
    public void failure_shouldThrowIfWhyCodeNull() {
        try {
            PublishResult.failure(null, "details");
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            Console.log("ut.publish_result.failure.null_whyCode.exception", e.getMessage());
            assertEquals("whyCode cannot be null", e.getMessage());
        }
    }

    @Test
    public void jsonRoundTrip_shouldRemainDeterministic() {

        PublishResult original = PublishResult.success("wfA", 1);
        original.setId("id-1");
        original.setVersion("v1");
        original.setStatus("PUBLISHED");

        String json = original.toJson();
        Console.log("ut.publish_result.roundtrip.json", json);

        PublishResult restored =
                OrchestraBaseModel.fromJson(json, PublishResult.class);

        assertNotNull(restored);
        assertTrue(restored.isSuccess());
        assertEquals("wfA", restored.getEngineWorkflowName());
        assertEquals(Integer.valueOf(1), restored.getEngineWorkflowVersion());

        assertEquals("id-1", restored.getId());
        assertEquals("v1", restored.getVersion());
        assertEquals("PUBLISHED", restored.getStatus());
    }

    @Test
    public void failure_shouldSerializeToJsonCorrectly() {

        PublishResult r = PublishResult.failure("DEF_PUBLISH_FAILED", "conductor rejected definition");

        String json = r.toJson();
        Console.log("ut.publish_result.failure.json", json);

        assertNotNull(json);

        JsonObject obj = JsonParser.parseString(json).getAsJsonObject();

        assertFalse(obj.get("success").getAsBoolean());

        assertTrue(obj.has("why"));
        assertFalse(obj.get("why").isJsonNull());

        JsonObject whyObj = obj.getAsJsonObject("why");
        Console.log("ut.publish_result.failure.why.json", whyObj.toString());

        assertTrue(whyObj.has("reason"));
        assertNotNull(whyObj.get("reason"));
        assertEquals("DEF_PUBLISH_FAILED", whyObj.get("reason").getAsString());

        assertTrue(whyObj.has("details"));
        assertNotNull(whyObj.get("details"));
        assertEquals("conductor rejected definition", whyObj.get("details").getAsString());

        assertTrue(obj.has("engineWorkflowName"));
        assertTrue(obj.get("engineWorkflowName").isJsonNull());

        assertTrue(obj.has("engineWorkflowVersion"));
        assertTrue(obj.get("engineWorkflowVersion").isJsonNull());
    }
}

