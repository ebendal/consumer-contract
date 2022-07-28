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

import static au.com.dius.pact.consumer.dsl.LambdaDsl.newJsonArray;
import static au.com.dius.pact.consumer.dsl.LambdaDsl.newJsonBody;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

@ExtendWith(PactConsumerTestExt.class)
class ConsumerContractTest {

    private final RestTemplate restTemplate = new RestTemplate();

    @Pact(provider = Name.PROVIDER, consumer = Name.CONSUMER_ONE)
    public RequestResponsePact consumerOneCreateBook(PactDslWithProvider builder) {
        return builder
            .uponReceiving(Name.CREATE_BOOK_INTERACTION)
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
                body.stringType("title", "Consumer One title");
            }).build())
            .toPact();
    }

    @Pact(provider = Name.PROVIDER, consumer = Name.CONSUMER_TWO)
    public RequestResponsePact consumerTwoCreateBook(PactDslWithProvider builder) {
        return builder
            .uponReceiving(Name.CREATE_BOOK_INTERACTION)
            .path("/api/books")
            .method("POST")
            .headers(Map.of("Content-Type", "application/json"))
            .body(newJsonBody(body -> {
                body.stringType("title", "CDCT explained");
            }).build())
            .willRespondWith()
            .status(201)
            .body(newJsonBody(body -> {
                body.uuid("id", UUID.fromString("c45c7026-be9b-4819-acce-7ab91ecc9c3a"));
                body.stringType("title", "Consumer Two title");
            }).build())
            .toPact();
    }

    @Pact(provider = Name.PROVIDER, consumer = Name.CONSUMER_TWO)
    public RequestResponsePact consumerTwoGetAllBooks(PactDslWithProvider builder) {
        return builder
            .uponReceiving(Name.GET_ALL_BOOKS_INTERACTION)
            .path("/api/books")
            .method("GET")
            .willRespondWith()
            .status(200)
            .body(newJsonArray((item) -> item.object(book -> {
                book.uuid("id", UUID.fromString("c45c7026-be9b-4819-acce-7ab91ecc9c3a"));
                book.stringType("title", "Consumer Two title");
            })).build())
            .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "consumerOneCreateBook", pactVersion = PactSpecVersion.V3)
    void consumerOneCreateBookTest(MockServer mockServer) throws JSONException {
        String response = restTemplate.postForObject(mockServer.getUrl() + "/api/books", new CreateBookDto("Random title"), String.class);

        assertEquals("{\n" +
            "  \"title\": \"Consumer One title\",\n" +
            "  \"id\": \"4d956508-473c-4d91-ae4b-abc666666cd5\"\n" +
            "}", response, JSONCompareMode.STRICT);
    }

    @Test
    @PactTestFor(pactMethod = "consumerTwoCreateBook", pactVersion = PactSpecVersion.V3)
    void consumerTwoCreateBookTest(MockServer mockServer) throws JSONException {
        String response = restTemplate.postForObject(mockServer.getUrl() + "/api/books", new CreateBookDto("Random title"), String.class);

        assertEquals("{\n" +
            "  \"title\": \"Consumer Two title\",\n" +
            "  \"id\": \"c45c7026-be9b-4819-acce-7ab91ecc9c3a\"\n" +
            "}", response, JSONCompareMode.STRICT);
    }

    @Test
    @PactTestFor(pactMethod = "consumerTwoGetAllBooks", pactVersion = PactSpecVersion.V3)
    void consumerTwoGetAllBooksTest(MockServer mockServer) throws JSONException {
        String response = restTemplate.getForObject(mockServer.getUrl() + "/api/books", String.class);

        assertEquals("[\n" +
            "  {\n" +
            "    \"title\": \"Consumer Two title\",\n" +
            "    \"id\": \"c45c7026-be9b-4819-acce-7ab91ecc9c3a\"\n" +
            "}\n" +
            "]", response, JSONCompareMode.STRICT);
    }

    @Getter
    @AllArgsConstructor
    static class CreateBookDto {
        private String title;
    }
}
