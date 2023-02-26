package com.ebendal.consumer.contract.core;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Map;


@Getter
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class HttpInteraction extends Interaction {

    @NonNull
    private final Path path;

    @NonNull
    private final HttpMethod method;

    @NonNull
    private final Integer statusCode;

    @NonNull
    private final Map<String, List<String>> requestHeaders;

    @NonNull
    private final Map<String, List<String>> responseHeaders;

    @NonNull
    private final Map<String, List<String>> queryParameters;

    private final String requestBody;

    private final String responseBody;

    @Builder
    @Getter
    @EqualsAndHashCode
    public static class Key {

        private final String providerName;
        private final String consumerName;
        private final String interactionName;
    }
}
