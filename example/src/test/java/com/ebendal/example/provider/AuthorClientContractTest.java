package com.ebendal.example.provider;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.PactSpecVersion;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import com.ebendal.example.Name;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

import static au.com.dius.pact.consumer.dsl.LambdaDsl.newJsonBody;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

@ExtendWith(PactConsumerTestExt.class)
class AuthorClientContractTest {

    private final RestTemplate restTemplate = new RestTemplate();

    @Pact(provider = Name.AUTHOR_PROVIDER, consumer = Name.BOOK_PROVIDER)
    public RequestResponsePact getAuthorById(PactDslWithProvider builder) {
        return builder
            .uponReceiving(Name.GET_AUTHOR_BY_ID_INTERACTION)
            .matchPath("^/api/authors/[0-9a-fA-F]{8}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{12}$", "/api/authors/4d956508-473c-4d91-ae4b-abc666666cd5")
            .method("GET")
            .willRespondWith()
            .status(200)
            .matchHeader("headerId", "^/api/authors/[0-9a-fA-F]{8}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{12}$", "4d956508-473c-4d91-ae4b-abc666666cd5")
            .body(newJsonBody(book -> {
                book.uuid("id", UUID.fromString("857f01e5-2535-4e9d-b3b2-baef06ab5699"));
                book.stringType("name", "John Doe");
            }).build())
            .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "getAuthorById", pactVersion = PactSpecVersion.V3)
    void getAuthorByIdTest(MockServer mockServer) throws JSONException {
        String response = restTemplate.getForObject(mockServer.getUrl() + "/api/authors/857f01e5-2535-4e9d-b3b2-baef06ab5699", String.class);

        assertEquals("{\n" +
            "  \"name\": \"John Doe\",\n" +
            "  \"id\": \"857f01e5-2535-4e9d-b3b2-baef06ab5699\"\n" +
            "}", response, JSONCompareMode.STRICT);
    }
}
