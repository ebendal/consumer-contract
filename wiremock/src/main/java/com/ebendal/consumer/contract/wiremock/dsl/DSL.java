package com.ebendal.consumer.contract.wiremock.dsl;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DSL {

    public static JsonBodyTemplatedValue bodyInject(String jsonPath, String expression) {
        return new JsonBodyTemplatedValue(jsonPath, expression);
    }

    public static HeaderTemplatedValue headerReplace(String headerKey, String expression) {
        return new HeaderTemplatedValue(headerKey, expression);
    }
}
