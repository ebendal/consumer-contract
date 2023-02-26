package com.ebendal.consumer.contract.wiremock.dsl;

import lombok.Getter;

@Getter
public class HeaderTemplatedValue extends TemplatedValue {

    public HeaderTemplatedValue(String key, String expression) {
        super(expression);
        this.key = key;
    }

    private final String key;
}
