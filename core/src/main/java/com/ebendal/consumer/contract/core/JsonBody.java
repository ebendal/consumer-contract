package com.ebendal.consumer.contract.core;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Getter
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class JsonBody extends Body {

    private final Set<Matcher> matchers;

    @Getter
    @SuperBuilder
    @EqualsAndHashCode
    public static class Matcher {

        private final String jsonPath;


    }
}
