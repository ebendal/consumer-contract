package com.ebendal.consumer.contract.gatling.dsl;

import lombok.Getter;

@Getter
public class HeaderReplace extends StateAction {

    private final String headerKey;

    HeaderReplace(String headerKey, String stateKey) {
        super(stateKey);
        this.headerKey = headerKey;
    }
}
