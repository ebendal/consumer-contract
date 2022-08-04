package com.ebendal.gatling.consumer.contract.dsl;

import io.gatling.javaapi.core.CheckBuilder;

public class CheckAndSave extends StateAction {

    private final CheckBuilder.Final finalCheckBuilder;

    CheckAndSave(CheckBuilder.Final finalCheckBuilder, String stateKey) {
        super(stateKey);
        this.finalCheckBuilder = finalCheckBuilder;
    }

    public CheckBuilder getCheck() {
        return finalCheckBuilder.saveAs(getStateKey());
    }
}
