package com.ebendal.consumer.contract.gatling.dsl;

import lombok.Getter;

@Getter
public class SingleValueQueryParameterReplace extends QueryParameterReplace {
    SingleValueQueryParameterReplace(String parameterKey, String stateKey) {
        super(stateKey, parameterKey);
    }
}
