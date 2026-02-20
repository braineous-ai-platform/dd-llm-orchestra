package io.braineous.dd.llm.orchestra.def.model;

import ai.braineous.rag.prompt.observe.Console;
import io.braineous.dd.llm.orchestra.core.model.OrchestraBaseModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class QueryTest {

    private static final String QUERY_JSON =
            "{\n" +
                    "  \"id\": \"q1_fetch_options\",\n" +
                    "  \"description\": \"Gather viable rebooking options.\",\n" +
                    "  \"sql\": \"select decision from llm where task = \\\"Fetch viable rebooking options\\\" and factId = \\\"cgo:pnr:123\\\" and relatedFacts = \\\"cgo:[f1,f2]\\\"\"\n" +
                    "}";

    @Test
    public void fromJson_shouldParseQuery_fields() {
        Console.log("QueryTest.fromJson_shouldParseQuery_fields", "START");

        Query query = OrchestraBaseModel.fromJson(QUERY_JSON, Query.class);

        Assertions.assertNotNull(query);
        Assertions.assertEquals("q1_fetch_options", query.getId());
        Assertions.assertEquals("Gather viable rebooking options.", query.getDescription());
        Assertions.assertTrue(query.getSql().contains("select decision"));

        Console.log("QueryTest.fromJson_shouldParseQuery_fields", "OK");
    }

    @Test
    public void toJson_shouldRoundTrip() {
        Console.log("QueryTest.toJson_shouldRoundTrip", "START");

        Query query = new Query();
        query.setId("q2_rank_and_pick");
        query.setDescription("Rank options and pick best.");
        query.setSql("select decision from llm where task = \"Rank options\"");

        String json = query.toJson();

        Assertions.assertNotNull(json);
        Assertions.assertFalse(json.trim().isEmpty());

        Query parsed = OrchestraBaseModel.fromJson(json, Query.class);

        Assertions.assertEquals("q2_rank_and_pick", parsed.getId());
        Assertions.assertEquals("Rank options and pick best.", parsed.getDescription());
        Assertions.assertEquals("select decision from llm where task = \"Rank options\"", parsed.getSql());

        Console.log("QueryTest.toJson_shouldRoundTrip", "OK");
    }

    @Test
    public void shouldSupportBaseModelFields() {
        Console.log("QueryTest.shouldSupportBaseModelFields", "START");

        Query query = new Query();
        query.setId("q_meta");
        query.setVersion("v1");
        query.setStatus("ACTIVE");
        query.setCorrelationId("corr-123");

        String json = query.toJson();
        Query parsed = OrchestraBaseModel.fromJson(json, Query.class);

        Assertions.assertEquals("v1", parsed.getVersion());
        Assertions.assertEquals("ACTIVE", parsed.getStatus());
        Assertions.assertEquals("corr-123", parsed.getCorrelationId());

        Console.log("QueryTest.shouldSupportBaseModelFields", "OK");
    }
}
