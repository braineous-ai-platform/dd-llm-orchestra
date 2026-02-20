package io.braineous.dd.llm.orchestra.def.model;

import ai.braineous.rag.prompt.observe.Console;
import io.braineous.dd.llm.orchestra.core.model.OrchestraBaseModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TransactionTest {

    private static final String TX_JSON =
            "{\n" +
                    "  \"description\": \"Agentic flow: gather → decide → draft.\",\n" +
                    "  \"queries\": [\n" +
                    "    {\n" +
                    "      \"id\": \"q1\",\n" +
                    "      \"description\": \"First query\",\n" +
                    "      \"sql\": \"select decision from llm where task = \\\"A\\\"\"\n" +
                    "    },\n" +
                    "    {\n" +
                    "      \"id\": \"q2\",\n" +
                    "      \"description\": \"Second query\",\n" +
                    "      \"sql\": \"select decision from llm where task = \\\"B\\\"\"\n" +
                    "    }\n" +
                    "  ],\n" +
                    "  \"commitOrder\": [\"q1\", \"q2\"]\n" +
                    "}";

    @Test
    public void fromJson_shouldParseTransaction() {
        Console.log("TransactionTest.fromJson_shouldParseTransaction", "START");

        Transaction tx = OrchestraBaseModel.fromJson(TX_JSON, Transaction.class);

        Assertions.assertNotNull(tx);
        Assertions.assertEquals("Agentic flow: gather → decide → draft.", tx.getDescription());

        Assertions.assertEquals(2, tx.getQueries().size());
        Assertions.assertEquals("q1", tx.getQueries().get(0).getId());
        Assertions.assertEquals("q2", tx.getQueries().get(1).getId());

        Assertions.assertEquals(2, tx.getCommitOrder().size());
        Assertions.assertEquals("q1", tx.getCommitOrder().get(0));
        Assertions.assertEquals("q2", tx.getCommitOrder().get(1));

        Console.log("TransactionTest.fromJson_shouldParseTransaction", "OK");
    }

    @Test
    public void toJson_shouldRoundTrip() {
        Console.log("TransactionTest.toJson_shouldRoundTrip", "START");

        Query q1 = new Query();
        q1.setId("q1");
        q1.setDescription("First query");
        q1.setSql("select decision from llm where task = \"A\"");

        Query q2 = new Query();
        q2.setId("q2");
        q2.setDescription("Second query");
        q2.setSql("select decision from llm where task = \"B\"");

        Transaction tx = new Transaction();
        tx.setDescription("Agentic flow: gather → decide → draft.");
        tx.getQueries().add(q1);
        tx.getQueries().add(q2);
        tx.getCommitOrder().add("q1");
        tx.getCommitOrder().add("q2");

        String json = tx.toJson();

        Assertions.assertNotNull(json);
        Assertions.assertFalse(json.trim().isEmpty());

        Transaction parsed = OrchestraBaseModel.fromJson(json, Transaction.class);

        Assertions.assertEquals(2, parsed.getQueries().size());
        Assertions.assertEquals("q1", parsed.getQueries().get(0).getId());
        Assertions.assertEquals("q2", parsed.getQueries().get(1).getId());

        Assertions.assertEquals("q1", parsed.getCommitOrder().get(0));
        Assertions.assertEquals("q2", parsed.getCommitOrder().get(1));

        Console.log("TransactionTest.toJson_shouldRoundTrip", "OK");
    }

    @Test
    public void shouldSupportBaseModelFields() {
        Console.log("TransactionTest.shouldSupportBaseModelFields", "START");

        Transaction tx = new Transaction();
        tx.setId("tx1");
        tx.setVersion("v1");
        tx.setStatus("ACTIVE");

        String json = tx.toJson();
        Transaction parsed = OrchestraBaseModel.fromJson(json, Transaction.class);

        Assertions.assertEquals("tx1", parsed.getId());
        Assertions.assertEquals("v1", parsed.getVersion());
        Assertions.assertEquals("ACTIVE", parsed.getStatus());

        Console.log("TransactionTest.shouldSupportBaseModelFields", "OK");
    }
}
