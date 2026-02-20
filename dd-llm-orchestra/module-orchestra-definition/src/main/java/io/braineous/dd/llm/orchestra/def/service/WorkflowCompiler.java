package io.braineous.dd.llm.orchestra.def.service;

import ai.braineous.rag.prompt.observe.Console;
import io.braineous.dd.llm.orchestra.core.model.OrchestraBaseModel;
import io.braineous.dd.llm.orchestra.core.model.def.WorkflowDef;
import io.braineous.dd.llm.orchestra.def.model.PublishResult;
import io.braineous.dd.llm.orchestra.def.model.Query;
import io.braineous.dd.llm.orchestra.def.model.Transaction;
import io.braineous.dd.llm.orchestra.def.model.Workflow;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * Compiles a dev-authored Agentic Workflow JSON into an internal WorkflowDef.
 *
 * <p>
 * Flow:
 * </p>
 * <ul>
 *     <li>Parse JSON into Workflow model</li>
 *     <li>Transform into internal WorkflowDef</li>
 *     <li>Return compiled definition (validation later)</li>
 * </ul>
 *
 * <p>
 * No validation logic in v0.
 * No assembler invocation here.
 * Pure compilation responsibility.
 * </p>
 */

@ApplicationScoped
public class WorkflowCompiler {

    private static final Integer ENGINE_VERSION_INT = Integer.valueOf(1);
    private static final String DEF_VERSION_STR = "1";

    @Inject
    private Assembler assembler;

    public WorkflowCompiler() {
    }

    public PublishResult compile(String json) {

        Console.log("workflow.compiler.compile.in", json);

        PublishResult invalid = validateJson(json);
        if (invalid != null) {
            Console.log("workflow.compiler.compile.out", invalid);
            return invalid;
        }

        Workflow workflow;
        try {
            workflow = OrchestraBaseModel.fromJson(json, Workflow.class);
        } catch (RuntimeException e) {
            PublishResult r = PublishResult.failure(
                    "WF_INVALID",
                    "Workflow JSON parse failed: " + e.getClass().getSimpleName()
            );
            Console.log("workflow.compiler.compile.out", r);
            return r;
        }

        PublishResult v = validateWorkflow(workflow);
        if (v != null) {
            Console.log("workflow.compiler.compile.out", v);
            return v;
        }

        WorkflowDef def = buildDefinition(workflow);

        //TODO: forward for Assembler


        PublishResult ok = PublishResult.success(def.getName(), ENGINE_VERSION_INT);

        Console.log("workflow.compiler.compile.out", ok);
        return ok;
    }

    // ---------------------------------------------------------

    private PublishResult validateJson(String json) {

        if (json == null) {
            return PublishResult.failure("WF_INVALID", "Workflow JSON is required");
        }

        String trimmed = json.trim();

        if (trimmed.length() == 0) {
            return PublishResult.failure("WF_INVALID", "Workflow JSON is blank");
        }

        if ("null".equalsIgnoreCase(trimmed)) {
            return PublishResult.failure("WF_INVALID", "Workflow JSON is null literal");
        }

        return null;
    }

    private PublishResult validateWorkflow(Workflow workflow) {

        if (workflow == null) {
            return PublishResult.failure("WF_INVALID", "Workflow is required");
        }

        String name = workflow.getName();
        if (name == null) {
            return PublishResult.failure("WF_INVALID", "Workflow.name is required");
        }
        if (name.trim().length() == 0) {
            return PublishResult.failure("WF_INVALID", "Workflow.name is blank");
        }

        Transaction tx = workflow.getTransaction();
        if (tx != null) {

            java.util.List<Query> queries = tx.getQueries();
            if (queries == null) {
                return PublishResult.failure("WF_INVALID", "Workflow.transaction.queries is required");
            }
            if (queries.isEmpty()) {
                return PublishResult.failure("WF_INVALID", "Workflow.transaction.queries is empty");
            }

            java.util.Set<String> seen = new java.util.HashSet<String>();

            for (int i = 0; i < queries.size(); i++) {

                Query q = queries.get(i);
                if (q == null) {
                    return PublishResult.failure("WF_INVALID", "Workflow.transaction.queries[" + i + "] is null");
                }

                String qid = q.getId();
                if (qid == null) {
                    return PublishResult.failure("WF_INVALID", "Workflow.transaction.queries[" + i + "].id is required");
                }

                String qidTrimmed = qid.trim();
                if (qidTrimmed.length() == 0) {
                    return PublishResult.failure("WF_INVALID", "Workflow.transaction.queries[" + i + "].id is blank");
                }

                if (seen.contains(qidTrimmed)) {
                    return PublishResult.failure("WF_INVALID", "Workflow.transaction.queries has duplicate id: " + qidTrimmed);
                }
                seen.add(qidTrimmed);
            }
        }

        return null;
    }

    private WorkflowDef buildDefinition(Workflow workflow) {

        WorkflowDef def = new WorkflowDef();

        def.setName(workflow.getName().trim());
        def.setVersion(DEF_VERSION_STR);

        java.util.List<String> stepIds = new java.util.ArrayList<String>();

        Transaction tx = workflow.getTransaction();
        if (tx != null) {

            java.util.List<Query> queries = tx.getQueries();
            if (queries != null) {
                for (int i = 0; i < queries.size(); i++) {

                    Query q = queries.get(i);
                    if (q == null) {
                        continue;
                    }

                    String qid = q.getId();
                    if (qid == null) {
                        continue;
                    }

                    String qidTrimmed = qid.trim();
                    if (qidTrimmed.length() == 0) {
                        continue;
                    }

                    stepIds.add("step." + qidTrimmed);
                }
            }
        }

        def.setStepDefIds(stepIds);

        return def;
    }
}