package com.ebendal.consumer.contract.wiremock.dsl;


import lombok.Getter;

@Getter
public class JsonBodyTemplatedValue extends TemplatedValue {

    public JsonBodyTemplatedValue(String jsonPath, String expression) {
        super(expression);
        this.jsonPath = jsonPath;
    }

    private final String jsonPath;
}
