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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.UUID;

import static au.com.dius.pact.consumer.dsl.LambdaDsl.newJsonArray;
import static au.com.dius.pact.consumer.dsl.LambdaDsl.newJsonBody;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

@ExtendWith(PactConsumerTestExt.class)
class ConsumerContractTest {

    private final RestTemplate restTemplate = new RestTemplate();

    @Pact(provider = Name.BOOK_PROVIDER, consumer = Name.CONSUMER_ONE)
    public RequestResponsePact consumerOneCreateBook(PactDslWithProvider builder) {
        return builder
            .uponReceiving(Name.CREATE_BOOK_INTERACTION)
            .path("/api/books")
            .method("POST")
            .headers(Map.of("Content-Type", "application/json"))
            .body(newJsonBody(body -> {
                body.stringType("title", "CDCT explained");
                body.uuid("authorID", UUID.fromString("0e94abad-c30f-4333-b71e-8656747b87de"));
            }).build())
            .willRespondWith()
            .status(201)
            .body(newJsonBody(body -> {
                body.uuid("id", UUID.fromString("4d956508-473c-4d91-ae4b-abc666666cd5"));
                body.stringType("title", "Consumer One title");
            }).build())
            .toPact();
    }

    @Pact(provider = Name.BOOK_PROVIDER, consumer = Name.CONSUMER_ONE)
    public RequestResponsePact consumerOneGetAllBooks(PactDslWithProvider builder) {
        return builder
            .uponReceiving(Name.GET_ALL_BOOKS_INTERACTION)
            .path("/api/books")
            .method("GET")
            .willRespondWith()
            .status(200)
            .body(newJsonArray((item) -> item.object(book -> {
                book.uuid("id", UUID.fromString("4d956508-473c-4d91-ae4b-abc666666cd5"));
                book.stringType("title", "Consumer One title");
            })).build())
            .toPact();
    }

    @Pact(provider = Name.BOOK_PROVIDER, consumer = Name.CONSUMER_ONE)
    public RequestResponsePact consumerOneGetBookById(PactDslWithProvider builder) {
        return builder
            .uponReceiving(Name.GET_BOOK_BY_ID_INTERACTION)
            .matchPath("^/api/books/[0-9a-fA-F]{8}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{12}$", "/api/books/4d956508-473c-4d91-ae4b-abc666666cd5")
            .method("GET")
            .willRespondWith()
            .status(200)
            .body(newJsonBody(book -> {
                book.uuid("id", UUID.fromString("4d956508-473c-4d91-ae4b-abc666666cd5"));
                book.stringType("title", "Consumer One title");
            }).build())
            .toPact();
    }

    @Pact(provider = Name.BOOK_PROVIDER, consumer = Name.CONSUMER_ONE)
    public RequestResponsePact consumerOneCheckId(PactDslWithProvider builder) {
        return builder
            .uponReceiving(Name.CHECK_ID)
            .matchPath("^/api/books/[0-9a-fA-F]{8}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{12}$", "/api/books/4d956508-473c-4d91-ae4b-abc666666cd5")
            .method("POST")
            .headers(Map.of("Content-Type", "application/json"))
            .matchQuery("queryParameterId", "^[0-9a-fA-F]{8}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{12}$", "4d956508-473c-4d91-ae4b-abc666666cd5")
            .matchHeader("headerId", "^[0-9a-fA-F]{8}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{12}$", "4d956508-473c-4d91-ae4b-abc666666cd5")
            .body(newJsonBody(book -> {
                book.uuid("id", UUID.fromString("4d956508-473c-4d91-ae4b-abc666666cd5"));
            }).build())
            .willRespondWith()
            .status(204)
            .toPact();
    }

    @Pact(provider = Name.BOOK_PROVIDER, consumer = Name.CONSUMER_TWO)
    public RequestResponsePact consumerTwoCreateBook(PactDslWithProvider builder) {
        return builder
            .uponReceiving(Name.CREATE_BOOK_INTERACTION)
            .path("/api/books")
            .method("POST")
            .headers(Map.of("Content-Type", "application/json"))
            .body(newJsonBody(body -> {
                body.stringType("title", "CDCT explained");
                body.uuid("authorID", UUID.fromString("6ad88582-2bd5-4eab-b3b6-5f06f1ba6dc9"));
            }).build())
            .willRespondWith()
            .status(201)
            .body(newJsonBody(body -> {
                body.uuid("id", UUID.fromString("c45c7026-be9b-4819-acce-7ab91ecc9c3a"));
                body.stringType("title", "Consumer Two title");
            }).build())
            .toPact();
    }

    @Pact(provider = Name.BOOK_PROVIDER, consumer = Name.CONSUMER_TWO)
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
        String response = restTemplate.postForObject(mockServer.getUrl() + "/api/books", new CreateBookDto("Random title", UUID.fromString("0e94abad-c30f-4333-b71e-8656747b87de")), String.class);

        assertEquals("{\n" +
            "  \"title\": \"Consumer One title\",\n" +
            "  \"id\": \"4d956508-473c-4d91-ae4b-abc666666cd5\"\n" +
            "}", response, JSONCompareMode.STRICT);
    }

    @Test
    @PactTestFor(pactMethod = "consumerOneGetAllBooks", pactVersion = PactSpecVersion.V3)
    void consumerOneGetAllBooksTest(MockServer mockServer) throws JSONException {
        String response = restTemplate.getForObject(mockServer.getUrl() + "/api/books", String.class);

        assertEquals("[\n" +
            "  {\n" +
            "    \"title\": \"Consumer One title\",\n" +
            "    \"id\": \"4d956508-473c-4d91-ae4b-abc666666cd5\"\n" +
            "}\n" +
            "]", response, JSONCompareMode.STRICT);
    }

    @Test
    @PactTestFor(pactMethod = "consumerOneGetBookById", pactVersion = PactSpecVersion.V3)
    void consumerOneGetBookByIdTest(MockServer mockServer) throws JSONException {
        String response = restTemplate.getForObject(mockServer.getUrl() + "/api/books/4d956508-473c-4d91-ae4b-abc666666cd5", String.class);

        assertEquals("{\n" +
            "  \"title\": \"Consumer One title\",\n" +
            "  \"id\": \"4d956508-473c-4d91-ae4b-abc666666cd5\"\n" +
            "}", response, JSONCompareMode.STRICT);
    }

    @Test
    @PactTestFor(pactMethod = "consumerOneCheckId", pactVersion = PactSpecVersion.V3)
    void consumerOneCheckIdTest(MockServer mockServer) throws JSONException {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("headerId", "4d956508-473c-4d91-ae4b-abc666666cd5");
        HttpEntity<IdCheckDto> requestEntity = new HttpEntity<>(new IdCheckDto(UUID.fromString("4d956508-473c-4d91-ae4b-abc666666cd5")), httpHeaders);
        String url = mockServer.getUrl() + "/api/books/4d956508-473c-4d91-ae4b-abc666666cd5?queryParameterId=4d956508-473c-4d91-ae4b-abc666666cd5";
        restTemplate.exchange(url, HttpMethod.POST, requestEntity, Void.class);
    }

    @Test
    @PactTestFor(pactMethod = "consumerTwoCreateBook", pactVersion = PactSpecVersion.V3)
    void consumerTwoCreateBookTest(MockServer mockServer) throws JSONException {
        String response = restTemplate.postForObject(mockServer.getUrl() + "/api/books", new CreateBookDto("Random title", UUID.fromString("6ad88582-2bd5-4eab-b3b6-5f06f1ba6dc9")), String.class);

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
        private UUID authorID;
    }

    @Getter
    @AllArgsConstructor
    static class IdCheckDto {
        private UUID id;
    }
}
