package com.ebendal.consumer.contract.gatling.dsl;

import lombok.Getter;

@Getter
public class BodyInject extends StateAction {

    private final String jsonPath;

    public BodyInject(String jsonPath, String stateKey) {
        super(stateKey);
        this.jsonPath = jsonPath;
    }
}
