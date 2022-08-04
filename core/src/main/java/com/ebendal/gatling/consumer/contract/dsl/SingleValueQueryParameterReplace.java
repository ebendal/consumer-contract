package com.ebendal.gatling.consumer.contract.dsl;

import lombok.Getter;

@Getter
public class SingleValueQueryParameterReplace extends QueryParameterReplace {
    SingleValueQueryParameterReplace(String parameterKey, String stateKey) {
        super(stateKey, parameterKey);
    }
}
