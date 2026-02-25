package io.braineous.dd.llm.orchestra.def.service;

import io.braineous.dd.llm.orchestra.def.model.Query;
import io.braineous.dd.llm.orchestra.def.model.Transaction;
import io.braineous.dd.llm.orchestra.def.model.Workflow;
import io.braineous.dd.llm.orchestra.wf.runtime.LLMDDTaskService;

public class OrchestraWorkflowCompiler {

    static com.netflix.conductor.common.metadata.workflow.WorkflowDef
    translateToConductorDef(Workflow workflow) {

        com.netflix.conductor.common.metadata.workflow.WorkflowDef conductorDef =
                new com.netflix.conductor.common.metadata.workflow.WorkflowDef();

        conductorDef.setName(workflow.getName());
        conductorDef.setDescription(workflow.getDescription());

        // V1: fixed version until dev_json includes version
        conductorDef.setVersion(1);

        java.util.List<com.netflix.conductor.common.metadata.workflow.WorkflowTask> tasks =
                new java.util.ArrayList<com.netflix.conductor.common.metadata.workflow.WorkflowTask>();

        Transaction tx = workflow.getTransaction();
        java.util.List<Query> queries = tx.getQueries();

        for (int i = 0; i < queries.size(); i++) {

            Query q = queries.get(i);

            com.netflix.conductor.common.metadata.workflow.WorkflowTask t =
                    new com.netflix.conductor.common.metadata.workflow.WorkflowTask();

            String id = q.getId();

            t.setName(LLMDDTaskService.TASK_TYPE);
            t.setTaskReferenceName(id);
            t.setType(LLMDDTaskService.TASK_TYPE);
            t.setDescription(q.getDescription());

            java.util.Map<String, Object> input = new java.util.HashMap<String, Object>();
            input.put("sql", q.getSql());
            t.setInputParameters(input);

            tasks.add(t);
        }

        conductorDef.setTasks(tasks);

        return conductorDef;
    }
}
