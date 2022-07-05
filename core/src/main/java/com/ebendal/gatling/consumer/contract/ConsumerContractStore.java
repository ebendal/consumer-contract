package com.ebendal.gatling.consumer.contract;

import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.http.Http;
import io.gatling.javaapi.http.HttpRequestActionBuilder;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Optional;

import static io.gatling.javaapi.core.CoreDsl.StringBody;
import static io.gatling.javaapi.core.CoreDsl.exec;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;
import static java.util.Collections.emptyMap;
import static java.util.function.UnaryOperator.identity;
import static java.util.stream.Collectors.toMap;

@RequiredArgsConstructor
public class ConsumerContractStore {

    private final ConsumerContractFramework framework;

    private String providerName;

    private Map<InteractionKey, Interaction> interactions = emptyMap();

    public void loadInteractions(String providerName) {
        this.providerName = providerName;
        interactions = framework.findInteractions(providerName).stream()
            .collect(toMap(interaction -> InteractionKey.builder()
                .consumerName(interaction.getConsumerName())
                .providerName(interaction.getProviderName())
                .interactionName(interaction.getInteractionName())
                .build(), identity()));
    }

    public ChainBuilder interaction(String consumerName, String interactionName) {
        Interaction interaction = getInteraction(consumerName, interactionName);
        Http http = http(interactionName);
        HttpRequestActionBuilder httpRequestActionBuilder = baseBuilder(http, interaction);
        body(httpRequestActionBuilder, interaction);
        headers(httpRequestActionBuilder, interaction);
        queryParameters(httpRequestActionBuilder, interaction);
        httpRequestActionBuilder.check(status().is(interaction.getStatusCode()));
        return exec(httpRequestActionBuilder);
    }

    private void headers(HttpRequestActionBuilder httpRequestActionBuilder, Interaction interaction) {
        interaction.getRequestHeaders().forEach(httpRequestActionBuilder::header);
    }

    private void body(HttpRequestActionBuilder httpRequestActionBuilder, Interaction interaction) {
        Optional.ofNullable(interaction.getBody())
            .ifPresent(body -> httpRequestActionBuilder.body(StringBody(body)));
    }

    private void queryParameters(HttpRequestActionBuilder httpRequestActionBuilder, Interaction interaction) {
        interaction.getQueryParameters().forEach(httpRequestActionBuilder::queryParam);
    }

    private HttpRequestActionBuilder baseBuilder(Http http, Interaction interaction) {
        String path = interaction.getPath();
        HttpMethod method = interaction.getMethod();
        switch (method) {
            case PUT:
                return http.put(path);
            case POST:
                return http.post(path);
            case GET:
                return http.get(path);
            case DELETE:
                return http.delete(path);
            case HEAD:
                return http.head(path);
            case OPTIONS:
                return http.options(path);
            case PATCH:
                return http.patch(path);
            default:
                throw new GatlingConsumerContractException("Interaction method not allowed: " + method);
        }
    }

    private Interaction getInteraction(String consumerName, String interactionName) {
        return Optional.ofNullable(interactions.get(InteractionKey.builder()
                .consumerName(consumerName)
                .providerName(providerName)
                .interactionName(interactionName)
                .build()))
            .orElseThrow(() -> new GatlingConsumerContractException("Interaction not found"));
    }

    @Builder
    @Getter
    @EqualsAndHashCode
    private static class InteractionKey {

        private final String providerName;
        private final String consumerName;
        private final String interactionName;
    }
}
