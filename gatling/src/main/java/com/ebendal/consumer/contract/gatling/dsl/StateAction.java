package com.ebendal.consumer.contract.gatling.dsl;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter(AccessLevel.PACKAGE)
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public abstract class StateAction {

    private final String stateKey;

    public String getStateKeyExpression() {
        return String.format("#{%s}", stateKey);
    }
}
