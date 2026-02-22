package io.braineous.dd.llm.orchestra.def.service;

import ai.braineous.rag.prompt.observe.Console;
import com.netflix.conductor.common.metadata.workflow.WorkflowDef;
import io.braineous.dd.llm.orchestra.def.model.*;
import io.braineous.dd.llm.orchestra.wf.runtime.LLMDDTaskService;
import io.braineous.dd.llm.orchestra.wf.runtime.WorkflowRegistrationService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ConductorWorkflowPublisher {

    @Inject
    private WorkflowRegistrationService wrs;

    //-------------------------------------------------
    //For UT/IT mode testing-only
    private boolean activePublishMode = true;
    void deactivatePublishMode(){
        this.activePublishMode = false;
    }
    //---------------------------------------------------

    public RegistrationResult publishOrchestraDef(Workflow workflow) {

        Console.log("wf.publisher.publish.in", String.valueOf(workflow));

        if (workflow == null) {
            RegistrationResult r = RegistrationResult.failure("DEF_NULL", "Workflow is null");
            Console.log("wf.publisher.publish.out", String.valueOf(r));
            return r;
        }

        java.util.List<String> errors = new java.util.ArrayList<String>();

        String name = workflow.getName();
        if (name == null || name.trim().length() == 0) {
            errors.add("Workflow.name is required");
        }

        String description = workflow.getDescription();
        if (description == null || description.trim().length() == 0) {
            errors.add("Workflow.description is required");
        }

        Transaction tx = workflow.getTransaction();
        if (tx == null) {
            errors.add("Workflow.transaction is required");
        } else {

            java.util.List<Query> queries = tx.getQueries();
            if (queries == null || queries.isEmpty()) {
                errors.add("Transaction.queries is required");
            } else {

                for (int i = 0; i < queries.size(); i++) {

                    Query q = queries.get(i);
                    if (q == null) {
                        errors.add("Transaction.queries[" + i + "] is null");
                        continue;
                    }

                    String qId = q.getId();
                    if (qId == null || qId.trim().length() == 0) {
                        errors.add("Query.id is required (queries[" + i + "])");
                    }

                    String qDesc = q.getDescription();
                    if (qDesc == null || qDesc.trim().length() == 0) {
                        errors.add("Query.description is required (queries[" + i + "])");
                    }

                    String qSql = q.getSql();
                    if (qSql == null || qSql.trim().length() == 0) {
                        errors.add("Query.sql is required (queries[" + i + "])");
                    }
                }
            }
        }

        if (!errors.isEmpty()) {
            StringBuilder msg = new StringBuilder();
            for (int i = 0; i < errors.size(); i++) {
                if (i > 0) {
                    msg.append("; ");
                }
                msg.append(errors.get(i));
            }

            RegistrationResult r = RegistrationResult.failure("DEF_INVALID", msg.toString());
            Console.log("wf.publisher.publish.out", String.valueOf(r));
            return r;
        }

        //------Translate Orchestra Workflow to Conductor defs---------------

        com.netflix.conductor.common.metadata.workflow.WorkflowDef conductorDef =
                translateToConductorDef(workflow);

        RegistrationResult translationValidation =
                validateConductorWorkflowDef(conductorDef);

        if (translationValidation != null) {
            Console.log("wf.publisher.publish.out", String.valueOf(translationValidation));
            return translationValidation;
        }

        //------------------------------------------------------------------

        //------Publish to Conductor engine---------------------------------
        if (!activePublishMode) {
            return RegistrationResult.success(conductorDef.getName(), conductorDef.getVersion());
        }

        RegistrationResult publishResult = this.publishToConductor(wrs, conductorDef);

        Console.log("wf.publisher.publish.out", String.valueOf(publishResult));
        return publishResult;
    }


    //----------------------------------------------------------------------------------
    private RegistrationResult publishToConductor(WorkflowRegistrationService wrs,
                                                  WorkflowDef conductorDef) {
        wrs.register(conductorDef);
        RegistrationResult result = RegistrationResult.success(
                conductorDef.getName(),
                conductorDef.getVersion()
        );
        return result;
    }

    //------Validate translated Conductor WorkflowDef
    private RegistrationResult validateConductorWorkflowDef(
            com.netflix.conductor.common.metadata.workflow.WorkflowDef conductorDef
    ) {

        if (conductorDef == null) {
            return RegistrationResult.failure("DEF_INVALID", "Conductor.WorkflowDef is null");
        }

        String name = conductorDef.getName();
        if (name == null || name.trim().length() == 0) {
            return RegistrationResult.failure("DEF_INVALID", "Conductor.WorkflowDef.name is required");
        }

        String description = conductorDef.getDescription();
        if (description == null || description.trim().length() == 0) {
            return RegistrationResult.failure("DEF_INVALID", "Conductor.WorkflowDef.description is required");
        }

        Integer version = conductorDef.getVersion();
        if (version == null || version.intValue() <= 0) {
            return RegistrationResult.failure("DEF_INVALID", "Conductor.WorkflowDef.version must be > 0");
        }

        java.util.List<com.netflix.conductor.common.metadata.workflow.WorkflowTask> tasks =
                conductorDef.getTasks();

        if (tasks == null || tasks.isEmpty()) {
            return RegistrationResult.failure("DEF_INVALID", "Conductor.WorkflowDef.tasks is required");
        }

        for (int i = 0; i < tasks.size(); i++) {

            com.netflix.conductor.common.metadata.workflow.WorkflowTask t = tasks.get(i);

            if (t == null) {
                return RegistrationResult.failure("DEF_INVALID",
                        "Conductor.WorkflowDef.tasks[" + i + "] is null");
            }

            String tName = t.getName();
            if (tName == null || tName.trim().length() == 0) {
                return RegistrationResult.failure("DEF_INVALID",
                        "Conductor.WorkflowTask.name is required (tasks[" + i + "])");
            }

            String ref = t.getTaskReferenceName();
            if (ref == null || ref.trim().length() == 0) {
                return RegistrationResult.failure("DEF_INVALID",
                        "Conductor.WorkflowTask.taskReferenceName is required (tasks[" + i + "])");
            }

            String type = t.getType();
            if (type == null || type.trim().length() == 0) {
                return RegistrationResult.failure("DEF_INVALID",
                        "Conductor.WorkflowTask.type is required (tasks[" + i + "])");
            }

            String tDesc = t.getDescription();
            if (tDesc == null || tDesc.trim().length() == 0) {
                return RegistrationResult.failure("DEF_INVALID",
                        "Conductor.WorkflowTask.description is required (tasks[" + i + "])");
            }

            java.util.Map<String, Object> input = t.getInputParameters();
            Object sqlObj = null;
            if (input != null) {
                sqlObj = input.get("sql");
            }

            String sql = null;
            if (sqlObj != null) {
                sql = String.valueOf(sqlObj);
            }

            if (sql == null || sql.trim().length() == 0) {
                return RegistrationResult.failure("DEF_INVALID",
                        "Conductor.WorkflowTask.inputParameters.sql is required (tasks[" + i + "])");
            }
        }

        return null;
    }


    //------Translate OrchestraDef to ConductorDef
    //------Translate Orchestra Workflow to Conductor WorkflowDef
    private com.netflix.conductor.common.metadata.workflow.WorkflowDef translateToConductorDef(Workflow workflow) {

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

            t.setName(id);
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
