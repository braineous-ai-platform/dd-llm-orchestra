package io.braineous.dd.llm.orchestra.def.service;

import ai.braineous.rag.prompt.observe.Console;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.netflix.conductor.common.metadata.workflow.WorkflowDef;
import io.braineous.dd.llm.orchestra.def.model.PublishResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AssemblerTest {

    @Test
    public void assemble_shouldReturnDefNull_whenDefinitionNull() {

        Assembler a = new Assembler();

        PublishResult r = a.assemble(null);

        Console.log("ut.assembler.def_null.json", r.toJson());

        JsonObject obj = JsonParser.parseString(r.toJson()).getAsJsonObject();
        assertFalse(obj.get("success").getAsBoolean());

        JsonObject why = obj.getAsJsonObject("why");
        assertEquals("DEF_NULL", why.get("reason").getAsString());
        assertEquals("WorkflowDef is null", why.get("details").getAsString());

        assertTrue(obj.has("engineWorkflowName"));
        assertTrue(obj.get("engineWorkflowName").isJsonNull());
        assertTrue(obj.has("engineWorkflowVersion"));
        assertTrue(obj.get("engineWorkflowVersion").isJsonNull());
    }

    @Test
    public void assemble_shouldReturnDefInvalid_whenNameNull() {

        WorkflowDef def = new WorkflowDef();
        def.setVersion(1);

        Assembler a = new Assembler();

        PublishResult r = a.assemble(def);

        Console.log("ut.assembler.def_invalid.name_null.json", r.toJson());

        JsonObject why = JsonParser.parseString(r.toJson()).getAsJsonObject().getAsJsonObject("why");
        assertEquals("DEF_INVALID", why.get("reason").getAsString());
        assertEquals("WorkflowDef.name is required", why.get("details").getAsString());
    }

    @Test
    public void assemble_shouldReturnDefInvalid_whenNameBlank() {

        WorkflowDef def = new WorkflowDef();
        def.setName("   ");
        def.setVersion(1);

        Assembler a = new Assembler();

        PublishResult r = a.assemble(def);

        Console.log("ut.assembler.def_invalid.name_blank.json", r.toJson());

        JsonObject why = JsonParser.parseString(r.toJson()).getAsJsonObject().getAsJsonObject("why");
        assertEquals("DEF_INVALID", why.get("reason").getAsString());
        assertEquals("WorkflowDef.name is blank", why.get("details").getAsString());
    }

    @Test
    public void assemble_shouldReturnDefInvalid_whenVersionNonPositive() {

        WorkflowDef def = new WorkflowDef();
        def.setName("wf");
        def.setVersion(0);

        Assembler a = new Assembler();

        PublishResult r = a.assemble(def);

        Console.log("ut.assembler.def_invalid.version_non_positive.json", r.toJson());

        JsonObject why = JsonParser.parseString(r.toJson()).getAsJsonObject().getAsJsonObject("why");
        assertEquals("DEF_INVALID", why.get("reason").getAsString());
        assertEquals("WorkflowDef.version must be > 0", why.get("details").getAsString());
    }


    @Test
    public void assemble_shouldReturnPublishFailed_whenPublisherNotWiredYet() {

        WorkflowDef def = new WorkflowDef();
        def.setName("wf");
        def.setVersion(1);

        Assembler a = new Assembler();

        PublishResult r = a.assemble(def);

        Console.log("ut.assembler.publish_failed.json", r.toJson());

        JsonObject obj = JsonParser.parseString(r.toJson()).getAsJsonObject();
        assertFalse(obj.get("success").getAsBoolean());

        JsonObject why = obj.getAsJsonObject("why");
        assertEquals("PUBLISH_FAILED", why.get("reason").getAsString());
        assertEquals("publisher not wired yet", why.get("details").getAsString());

        assertTrue(obj.has("engineWorkflowName"));
        assertTrue(obj.get("engineWorkflowName").isJsonNull());
        assertTrue(obj.has("engineWorkflowVersion"));
        assertTrue(obj.get("engineWorkflowVersion").isJsonNull());
    }
}

