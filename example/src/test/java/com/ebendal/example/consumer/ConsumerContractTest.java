package com.ebendal.example.consumer;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.PactSpecVersion;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import com.ebendal.example.Name;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.UUID;

import static au.com.dius.pact.consumer.dsl.LambdaDsl.newJsonBody;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

@ExtendWith(PactConsumerTestExt.class)
class ConsumerContractTest {

    private final RestTemplate restTemplate = new RestTemplate();

    @Pact(provider = Name.PROVIDER, consumer = Name.CONSUMER)
    public RequestResponsePact createPact(PactDslWithProvider builder) {
        return builder
            .uponReceiving(Name.INTERACTION)
            .path("/api/books")
            .method("POST")
            .headers(Map.of("Content-Type", "application/json"))
            .body(newJsonBody(body -> {
                body.stringType("title", "CDCT explained");
            }).build())
            .willRespondWith()
            .status(201)
            .body(newJsonBody(body -> {
                body.uuid("id", UUID.fromString("4d956508-473c-4d91-ae4b-abc666666cd5"));
                body.stringType("title", "CDCT explained");
            }).build())
            .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "createPact", pactVersion = PactSpecVersion.V3)
    void testDossierVersiePact(MockServer mockServer) throws JSONException {
        String response = restTemplate.postForObject(mockServer.getUrl() + "/api/books", new CreateBookDto("Random title"), String.class);

        assertEquals("{\n" +
            "  \"title\": \"CDCT explained\",\n" +
            "  \"id\": \"4d956508-473c-4d91-ae4b-abc666666cd5\"\n" +
            "}", response, JSONCompareMode.STRICT);
    }

    @Getter
    @AllArgsConstructor
    static class CreateBookDto {
        private String title;
    }
}
