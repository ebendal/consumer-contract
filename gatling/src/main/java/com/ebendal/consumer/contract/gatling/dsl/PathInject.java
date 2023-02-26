package com.ebendal.consumer.contract.gatling.dsl;

import lombok.Getter;

@Getter
public class PathInject extends StateAction {

    private final int index;

    public PathInject(int index, String stateKey) {
        super(stateKey);
        this.index = index;
    }
}
