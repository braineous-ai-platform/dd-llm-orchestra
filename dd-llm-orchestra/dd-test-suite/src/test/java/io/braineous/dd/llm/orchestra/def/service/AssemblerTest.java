package io.braineous.dd.llm.orchestra.def.service;

import ai.braineous.rag.prompt.observe.Console;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.braineous.dd.llm.orchestra.def.model.PublishResult;
import io.braineous.dd.llm.orchestra.def.model.Workflow;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AssemblerTest {

    @Test
    public void assemble_shouldReturnDefNull_whenWorkflowNull() {

        Assembler a = new Assembler();
        a.deactivatePublishMode();

        PublishResult r = a.assemble(null);

        JsonObject obj = JsonParser.parseString(r.toJson()).getAsJsonObject();
        assertFalse(obj.get("success").getAsBoolean());

        JsonObject why = obj.getAsJsonObject("why");
        assertEquals("DEF_NULL", why.get("reason").getAsString());
        // current message in code:
        assertEquals("WorkflowDef is null", why.get("details").getAsString());
    }

    @Test
    public void assemble_shouldReturnDefInvalid_whenNameNull() {

        Workflow wf = new Workflow();
        wf.setVersion("1");

        Assembler a = new Assembler();
        a.deactivatePublishMode();

        PublishResult r = a.assemble(wf);

        JsonObject why = JsonParser.parseString(r.toJson()).getAsJsonObject().getAsJsonObject("why");
        assertEquals("DEF_INVALID", why.get("reason").getAsString());
        assertEquals("WorkflowDef.name is required", why.get("details").getAsString());
    }

    @Test
    public void assemble_shouldReturnDefInvalid_whenNameBlank() {

        Workflow wf = new Workflow();
        wf.setName("   ");
        wf.setVersion("1");

        Assembler a = new Assembler();
        a.deactivatePublishMode();

        PublishResult r = a.assemble(wf);

        JsonObject why = JsonParser.parseString(r.toJson()).getAsJsonObject().getAsJsonObject("why");
        assertEquals("DEF_INVALID", why.get("reason").getAsString());
        assertEquals("WorkflowDef.name is blank", why.get("details").getAsString());
    }

    @Test
    public void assemble_shouldReturnDefInvalid_whenVersionNull() {

        Workflow wf = new Workflow();
        wf.setName("wf");

        Assembler a = new Assembler();
        a.deactivatePublishMode();

        PublishResult r = a.assemble(wf);

        JsonObject why = JsonParser.parseString(r.toJson()).getAsJsonObject().getAsJsonObject("why");
        assertEquals("DEF_INVALID", why.get("reason").getAsString());
        assertEquals("WorkflowDef.version is required", why.get("details").getAsString());
    }

    @Test
    public void assemble_shouldReturnDefInvalid_whenVersionBlank() {

        Workflow wf = new Workflow();
        wf.setName("wf");
        wf.setVersion("   ");

        Assembler a = new Assembler();
        a.deactivatePublishMode();

        PublishResult r = a.assemble(wf);

        JsonObject why = JsonParser.parseString(r.toJson()).getAsJsonObject().getAsJsonObject("why");
        assertEquals("DEF_INVALID", why.get("reason").getAsString());
        assertEquals("WorkflowDef.version is required", why.get("details").getAsString());
    }

    @Test
    public void assemble_shouldReturnDefInvalid_whenVersionNonNumeric() {

        Workflow wf = new Workflow();
        wf.setName("wf");
        wf.setVersion("abc");

        Assembler a = new Assembler();
        a.deactivatePublishMode();

        PublishResult r = a.assemble(wf);

        JsonObject why = JsonParser.parseString(r.toJson()).getAsJsonObject().getAsJsonObject("why");
        assertEquals("DEF_INVALID", why.get("reason").getAsString());
        assertEquals("WorkflowDef.version must be numeric", why.get("details").getAsString());
    }

    @Test
    public void assemble_shouldReturnDefInvalid_whenVersionNonPositive() {

        Workflow wf = new Workflow();
        wf.setName("wf");
        wf.setVersion("0");

        Assembler a = new Assembler();
        a.deactivatePublishMode();

        PublishResult r = a.assemble(wf);

        JsonObject why = JsonParser.parseString(r.toJson()).getAsJsonObject().getAsJsonObject("why");
        assertEquals("DEF_INVALID", why.get("reason").getAsString());
        assertEquals("WorkflowDef.version must be > 0", why.get("details").getAsString());
    }

    @Test
    public void assemble_shouldReturnSuccess_whenPublishModeDeactivated() {

        Workflow wf = new Workflow();
        wf.setName("wf");
        wf.setVersion("1");

        Assembler a = new Assembler();
        a.deactivatePublishMode();

        PublishResult r = a.assemble(wf);

        JsonObject obj = JsonParser.parseString(r.toJson()).getAsJsonObject();
        assertTrue(obj.get("success").getAsBoolean());
        assertEquals("wf", obj.get("engineWorkflowName").getAsString());
        assertEquals(1, obj.get("engineWorkflowVersion").getAsInt());
    }
}

