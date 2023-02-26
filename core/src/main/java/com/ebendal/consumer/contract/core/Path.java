package com.ebendal.consumer.contract.core;


import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
@EqualsAndHashCode
public class Path {

    @NonNull
    private final String example;
    @NonNull
    private final String regularExpression;
}
