package com.ebendal.gatling.consumer.contract;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.util.Map;

@Builder
@Getter
class Interaction {

    @NonNull
    private final String consumerName;

    @NonNull
    private final String providerName;

    @NonNull
    private final String interactionName;

    @NonNull
    private final String path;

    @NonNull
    private final HttpMethod method;

    @NonNull
    private final Integer statusCode;

    @NonNull
    private final Map<String, String> requestHeaders;

    @NonNull
    private final Map<String, String> queryParameters;

    private final String body;
}
