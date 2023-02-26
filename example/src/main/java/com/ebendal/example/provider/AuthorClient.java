package com.ebendal.example.provider;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Slf4j
@Component
public class AuthorClient {

    private final RestOperations restOperations = new RestTemplate();
    private final String url;

    public AuthorClient(@Value("${client.author.url}") String url) {
        this.url = url;
    }

    public boolean exists(UUID id) {
        ResponseEntity<Author> authorResponse = restOperations.getForEntity(url + "/api/authors/" + id, Author.class);
        log.info("Author response: status {}, headers {}, body {}", authorResponse.getStatusCode(), authorResponse.getHeaders(), authorResponse.getBody());
        return authorResponse.getStatusCode().is2xxSuccessful();
    }
}
