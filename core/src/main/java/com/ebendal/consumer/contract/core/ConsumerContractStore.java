package com.ebendal.consumer.contract.core;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static java.util.Collections.emptyMap;
import static java.util.function.UnaryOperator.identity;
import static java.util.stream.Collectors.toMap;

@Slf4j
@RequiredArgsConstructor
public abstract class ConsumerContractStore {

    private final ConsumerContractFramework framework;

    private String providerName;

    protected Map<Interaction.Key, Interaction> interactions = emptyMap();

    public void loadInteractions(String providerName) {
        this.providerName = providerName;
        interactions = framework.findInteractions(providerName).stream()
            .filter(interaction -> supportedInteractionsTypes().contains(interaction.getClass()))
            .collect(toMap(interaction -> Interaction.Key.builder()
                .consumerName(interaction.getConsumerName())
                .providerName(interaction.getProviderName())
                .interactionName(interaction.getInteractionName())
                .build(), identity()));
    }

    protected Interaction getInteraction(String consumerName, String interactionName) {
        return Optional.ofNullable(interactions.get(Interaction.Key.builder()
                .consumerName(consumerName)
                .providerName(providerName)
                .interactionName(interactionName)
                .build()))
            .orElseThrow(() -> new ConsumerContractException("Interaction not found"));
    }

    protected abstract Set<Class<? extends Interaction>> supportedInteractionsTypes();
}
