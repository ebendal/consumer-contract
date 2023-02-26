package com.ebendal.consumer.contract.core;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@EqualsAndHashCode
public abstract class Body {

    @NonNull
    private final String example;
}
