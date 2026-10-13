package io.braineous.dd.llm.orchestra.def.service;

import ai.braineous.rag.prompt.observe.Console;
import io.braineous.dd.llm.orchestra.def.model.ExecutionResult;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ExecutorSPITest {

    @Test
    public void test_1() {

        assertTrue(ExecutorSPI.class.isAssignableFrom(Executor.class));

        Executor ex = new Executor();
        ex.deactivateExeMode();

        ExecutorSPI spi = ex;

        Map<String, Object> input = new HashMap<String, Object>();
        ExecutionResult r = spi.executeTransaction(null, input);

        Console.log("ExecutorSPITest.test_1", r.toString());

        assertNotNull(r);
        assertFalse(r.isSuccess());
        assertEquals("ORCH_EXEC_NULL_WORKFLOW", r.getWhy().getReason());
    }
}
