package com.ebendal.consumer.contract.wiremock.dsl;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public abstract class TemplatedValue {

    private final String expression;
}
