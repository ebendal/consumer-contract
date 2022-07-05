package com.ebendal.gatling.consumer.contract;

public class GatlingConsumerContractException extends RuntimeException {

    GatlingConsumerContractException(String message) {
        super(message);
    }

    public GatlingConsumerContractException(Throwable t) {
        super(t);
    }
}
