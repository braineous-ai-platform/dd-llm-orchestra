package io.braineous.dd.llm.orchestra.def.service;

import io.braineous.dd.llm.orchestra.def.model.ExecutionResult;
import io.braineous.dd.llm.orchestra.def.model.Workflow;

import java.util.Map;

public interface ExecutorSPI {

    ExecutionResult executeTransaction(
            Workflow workflow,
            Map<String, Object> input);
}
