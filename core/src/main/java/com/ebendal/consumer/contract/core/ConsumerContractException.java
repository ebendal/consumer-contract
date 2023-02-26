package com.ebendal.consumer.contract.core;

public class ConsumerContractException extends RuntimeException {

    public ConsumerContractException(String message) {
        super(message);
    }

    public ConsumerContractException(Throwable t) {
        super(t);
    }
}
