package io.braineous.dd.llm.orchestra.def.service;

import ai.braineous.rag.prompt.observe.Console;
import io.braineous.dd.llm.orchestra.core.model.def.WorkflowDef;
import io.braineous.dd.llm.orchestra.def.model.Query;
import io.braineous.dd.llm.orchestra.def.model.RegistrationResult;
import io.braineous.dd.llm.orchestra.def.model.Transaction;
import io.braineous.dd.llm.orchestra.def.model.Workflow;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ConductorWorkflowPublisher {

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

        //------Translate Orchestra Workflow to Conductor defs

        RegistrationResult r = RegistrationResult.failure(
                "ENGINE_NOT_WIRED",
                "Conductor engine publisher not wired"
        );
        Console.log("wf.publisher.publish.out", String.valueOf(r));
        return r;
    }

    //------Translate OrchestraDef to ConductorDef
    private com.netflix.conductor.common.metadata.workflow.WorkflowDef translateToConductorDef(
            WorkflowDef orchestraDef
    ) {

        com.netflix.conductor.common.metadata.workflow.WorkflowDef conductorDef =
                new com.netflix.conductor.common.metadata.workflow.WorkflowDef();

        conductorDef.setName(orchestraDef.getName());

        String v = orchestraDef.getVersion();
        if (v != null) {
            conductorDef.setVersion(Integer.valueOf(v));
        }

        //convert steps to tasks


        return conductorDef;
    }
}
