package com.ebendal.consumer.contract.gatling.dsl;

import io.gatling.javaapi.core.CheckBuilder;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DSL {

    public static CheckAndSave checkAndSave(CheckBuilder.Final check, String key) {
        return new CheckAndSave(check, key);
    }

    public static PathInject pathInject(int index, String key) {
        return new PathInject(index, key);
    }

    public static BodyInject bodyInject(String jsonPath, String key) {
        return new BodyInject(jsonPath, key);
    }

    public static HeaderReplace headerReplace(String headerKey, String stateKey) {
        return new HeaderReplace(headerKey, stateKey);
    }

    public static SingleValueQueryParameterReplace queryParameterReplace(String parameterKey, String stateKey) {
        return new SingleValueQueryParameterReplace(parameterKey, stateKey);
    }

    public static MultiValueQueryParameterReplace multiValueQueryParameterReplace(String parameterKey, String stateCollectionKey) {
        return new MultiValueQueryParameterReplace(parameterKey, stateCollectionKey);
    }
}
