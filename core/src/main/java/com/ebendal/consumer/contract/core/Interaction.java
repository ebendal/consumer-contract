package com.ebendal.consumer.contract.core;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@EqualsAndHashCode
public abstract class Interaction {

    @NonNull
    private final String consumerName;

    @NonNull
    private final String providerName;

    @NonNull
    private final String interactionName;


    @Builder
    @Getter
    @EqualsAndHashCode
    public static class Key {

        private final String providerName;
        private final String consumerName;
        private final String interactionName;
    }
}