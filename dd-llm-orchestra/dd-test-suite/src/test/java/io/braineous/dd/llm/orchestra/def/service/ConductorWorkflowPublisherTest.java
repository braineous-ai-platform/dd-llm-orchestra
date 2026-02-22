package io.braineous.dd.llm.orchestra.def.service;


import io.braineous.dd.llm.orchestra.def.model.Query;
import io.braineous.dd.llm.orchestra.def.model.RegistrationResult;
import io.braineous.dd.llm.orchestra.def.model.Transaction;
import io.braineous.dd.llm.orchestra.def.model.Workflow;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class ConductorWorkflowPublisherTest {

    @Test
    void publishOrchestraDef_nullInput_returnsDefNullFailure() {

        ConductorWorkflowPublisher publisher = new ConductorWorkflowPublisher();

        RegistrationResult result = publisher.publishOrchestraDef(null);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertNotNull(result.getWhy());
        assertEquals("DEF_NULL", result.getWhy().getReason());
    }

    @Test
    void publishOrchestraDef_blankName_returnsDefInvalidFailure() {

        ConductorWorkflowPublisher publisher = new ConductorWorkflowPublisher();

        Workflow wf = new Workflow();
        wf.setName("   ");
        wf.setDescription("Rebook disrupted passengers");

        Transaction tx = new Transaction();
        Query q1 = new Query();
        q1.setId("q1_fetch_options");
        q1.setDescription("Gather viable rebooking options.");
        q1.setSql("select decision from llm where task = \"x\"");
        tx.setQueries(Arrays.asList(q1));

        wf.setTransaction(tx);

        RegistrationResult result = publisher.publishOrchestraDef(wf);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertNotNull(result.getWhy());
        assertEquals("DEF_INVALID", result.getWhy().getReason());
    }

    @Test
    void publishOrchestraDef_blankDescription_returnsDefInvalidFailure() {

        ConductorWorkflowPublisher publisher = new ConductorWorkflowPublisher();

        Workflow wf = new Workflow();
        wf.setName("fno_rebook_v1");
        wf.setDescription("   ");

        Transaction tx = new Transaction();
        Query q1 = new Query();
        q1.setId("q1_fetch_options");
        q1.setDescription("Gather viable rebooking options.");
        q1.setSql("select decision from llm where task = \"x\"");
        tx.setQueries(Arrays.asList(q1));

        wf.setTransaction(tx);

        RegistrationResult result = publisher.publishOrchestraDef(wf);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertNotNull(result.getWhy());
        assertEquals("DEF_INVALID", result.getWhy().getReason());
    }

    @Test
    void publishOrchestraDef_nullTransaction_returnsDefInvalidFailure() {

        ConductorWorkflowPublisher publisher = new ConductorWorkflowPublisher();

        Workflow wf = new Workflow();
        wf.setName("fno_rebook_v1");
        wf.setDescription("Rebook disrupted passengers");
        wf.setTransaction(null);

        RegistrationResult result = publisher.publishOrchestraDef(wf);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertNotNull(result.getWhy());
        assertEquals("DEF_INVALID", result.getWhy().getReason());
    }

    @Test
    void publishOrchestraDef_nullQueries_returnsDefInvalidFailure() {

        ConductorWorkflowPublisher publisher = new ConductorWorkflowPublisher();

        Workflow wf = new Workflow();
        wf.setName("fno_rebook_v1");
        wf.setDescription("Rebook disrupted passengers");

        Transaction tx = new Transaction();
        tx.setQueries(null);
        wf.setTransaction(tx);

        RegistrationResult result = publisher.publishOrchestraDef(wf);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertNotNull(result.getWhy());
        assertEquals("DEF_INVALID", result.getWhy().getReason());
    }

    @Test
    void publishOrchestraDef_queriesHasNullEntry_returnsDefInvalidFailure() {

        ConductorWorkflowPublisher publisher = new ConductorWorkflowPublisher();

        Workflow wf = new Workflow();
        wf.setName("fno_rebook_v1");
        wf.setDescription("Rebook disrupted passengers");

        Transaction tx = new Transaction();
        tx.setQueries(Arrays.asList((Query) null));
        wf.setTransaction(tx);

        RegistrationResult result = publisher.publishOrchestraDef(wf);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertNotNull(result.getWhy());
        assertEquals("DEF_INVALID", result.getWhy().getReason());
    }

    @Test
    void publishOrchestraDef_queryMissingId_returnsDefInvalidFailure() {

        ConductorWorkflowPublisher publisher = new ConductorWorkflowPublisher();

        Workflow wf = new Workflow();
        wf.setName("fno_rebook_v1");
        wf.setDescription("Rebook disrupted passengers");

        Transaction tx = new Transaction();
        Query q1 = new Query();
        q1.setId("   ");
        q1.setDescription("Gather viable rebooking options.");
        q1.setSql("select decision from llm where task = \"x\"");
        tx.setQueries(Arrays.asList(q1));

        wf.setTransaction(tx);

        RegistrationResult result = publisher.publishOrchestraDef(wf);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertNotNull(result.getWhy());
        assertEquals("DEF_INVALID", result.getWhy().getReason());
    }

    @Test
    void publishOrchestraDef_queryMissingDescription_returnsDefInvalidFailure() {

        ConductorWorkflowPublisher publisher = new ConductorWorkflowPublisher();

        Workflow wf = new Workflow();
        wf.setName("fno_rebook_v1");
        wf.setDescription("Rebook disrupted passengers");

        Transaction tx = new Transaction();
        Query q1 = new Query();
        q1.setId("q1_fetch_options");
        q1.setDescription("   ");
        q1.setSql("select decision from llm where task = \"x\"");
        tx.setQueries(Arrays.asList(q1));

        wf.setTransaction(tx);

        RegistrationResult result = publisher.publishOrchestraDef(wf);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertNotNull(result.getWhy());
        assertEquals("DEF_INVALID", result.getWhy().getReason());
    }

    @Test
    void publishOrchestraDef_queryMissingSql_returnsDefInvalidFailure() {

        ConductorWorkflowPublisher publisher = new ConductorWorkflowPublisher();

        Workflow wf = new Workflow();
        wf.setName("fno_rebook_v1");
        wf.setDescription("Rebook disrupted passengers");

        Transaction tx = new Transaction();
        Query q1 = new Query();
        q1.setId("q1_fetch_options");
        q1.setDescription("Gather viable rebooking options.");
        q1.setSql("   ");
        tx.setQueries(Arrays.asList(q1));

        wf.setTransaction(tx);

        RegistrationResult result = publisher.publishOrchestraDef(wf);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertNotNull(result.getWhy());
        assertEquals("DEF_INVALID", result.getWhy().getReason());
    }

    @Test
    void publishOrchestraDef_validWorkflow_engineNotWired_returnsFailure() {

        ConductorWorkflowPublisher publisher = new ConductorWorkflowPublisher();

        Workflow wf = new Workflow();
        wf.setName("fno_rebook_v1");
        wf.setDescription("Rebook disrupted passengers");

        Transaction tx = new Transaction();

        Query q1 = new Query();
        q1.setId("q1_fetch_options");
        q1.setDescription("Gather viable rebooking options.");
        q1.setSql("select decision from llm where task = \"Fetch viable rebooking options\"");

        Query q2 = new Query();
        q2.setId("q2_rank_and_pick");
        q2.setDescription("Rank options and pick best.");
        q2.setSql("select decision from llm where task = \"Rank options and pick best\"");

        tx.setQueries(Arrays.asList(q1, q2));

        wf.setTransaction(tx);

        RegistrationResult result = publisher.publishOrchestraDef(wf);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertNotNull(result.getWhy());
        assertEquals("ENGINE_NOT_WIRED", result.getWhy().getReason());
    }

    @Test
    void publishOrchestraDef_validWorkflow_translationValid_engineNotWired() {

        ConductorWorkflowPublisher publisher = new ConductorWorkflowPublisher();

        Workflow wf = buildValidWorkflow();

        RegistrationResult result = publisher.publishOrchestraDef(wf);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals("ENGINE_NOT_WIRED", result.getWhy().getReason());
    }

    @Test
    void publishOrchestraDef_missingSql_afterTranslation_returnsDefInvalid() {

        ConductorWorkflowPublisher publisher = new ConductorWorkflowPublisher();

        Workflow wf = buildValidWorkflow();
        wf.getTransaction().getQueries().get(0).setSql("   "); // break sql

        RegistrationResult result = publisher.publishOrchestraDef(wf);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals("DEF_INVALID", result.getWhy().getReason());
    }

    @Test
    void publishOrchestraDef_missingQueryDescription_afterTranslation_returnsDefInvalid() {

        ConductorWorkflowPublisher publisher = new ConductorWorkflowPublisher();

        Workflow wf = buildValidWorkflow();
        wf.getTransaction().getQueries().get(0).setDescription("   ");

        RegistrationResult result = publisher.publishOrchestraDef(wf);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals("DEF_INVALID", result.getWhy().getReason());
    }

    //-------------------------------------------------
    private Workflow buildValidWorkflow() {

        Workflow wf = new Workflow();
        wf.setName("fno_rebook_v1");
        wf.setDescription("Rebook disrupted passengers");

        Transaction tx = new Transaction();

        Query q1 = new Query();
        q1.setId("q1_fetch_options");
        q1.setDescription("Gather viable rebooking options.");
        q1.setSql("select decision from llm where task = \"Fetch\"");

        Query q2 = new Query();
        q2.setId("q2_rank_and_pick");
        q2.setDescription("Rank options and pick best.");
        q2.setSql("select decision from llm where task = \"Rank\"");

        tx.setQueries(java.util.Arrays.asList(q1, q2));
        wf.setTransaction(tx);

        return wf;
    }
}