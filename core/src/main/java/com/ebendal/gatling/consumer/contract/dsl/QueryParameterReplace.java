package com.ebendal.gatling.consumer.contract.dsl;

import lombok.Getter;

@Getter
public abstract class QueryParameterReplace extends StateAction {

    private final String parameterKey;

    QueryParameterReplace(String stateKey, String parameterKey) {
        super(stateKey);
        this.parameterKey = parameterKey;
    }
}
