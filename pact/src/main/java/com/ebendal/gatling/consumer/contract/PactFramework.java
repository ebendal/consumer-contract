package com.ebendal.gatling.consumer.contract;

import au.com.dius.pact.core.model.Pact;

import java.util.Set;
import java.util.stream.Collectors;

public class PactFramework implements ConsumerContractFramework {
    private final CustomPactBrokerLoader customPactBrokerLoader;

    public PactFramework() {
        customPactBrokerLoader = new CustomPactBrokerLoader();
    }

    @Override
    public Set<Interaction> findInteractions(String providerName) {
        return customPactBrokerLoader.load(providerName).stream()
            .filter(Pact::isRequestResponsePact)
            .flatMap(pact -> map(pact).stream())
            .collect(Collectors.toSet());
    }

    private Set<Interaction> map(Pact pact) {
        return pact.getInteractions().stream()
            .map(interaction -> Interaction.builder()
                .providerName(pact.getProvider().getName())
                .consumerName(pact.getConsumer().getName())
                .interactionName(interaction.getDescription())
                .method(HttpMethod.valueOf(interaction.asSynchronousRequestResponse().getRequest().getMethod()))
                .path(interaction.asSynchronousRequestResponse().getRequest().getPath())
                .body(interaction.asSynchronousRequestResponse().getRequest().getPath())
                .requestHeaders(interaction.asSynchronousRequestResponse().getRequest().getHeaders())
                .queryParameters(interaction.asSynchronousRequestResponse().getRequest().getQuery())
                .statusCode(interaction.asSynchronousRequestResponse().getResponse().getStatus())
                .build())
            .collect(Collectors.toSet());
    }
}
