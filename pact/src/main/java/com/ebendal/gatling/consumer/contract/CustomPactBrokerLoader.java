package com.ebendal.gatling.consumer.contract;

import au.com.dius.pact.core.model.Pact;
import au.com.dius.pact.provider.junitsupport.loader.PactBroker;
import au.com.dius.pact.provider.junitsupport.loader.PactBrokerLoader;

import java.io.IOException;
import java.util.List;

class CustomPactBrokerLoader extends PactBrokerLoader {

    CustomPactBrokerLoader() {
        super(BrokerConfig.class.getAnnotation(PactBroker.class));
    }

    @Override
    public List<Pact> load(String providerName) {
        try {
            return super.load(providerName);
        } catch (IOException e) {
            throw new GatlingConsumerContractException(e);
        }
    }

    @PactBroker
    static class BrokerConfig {
    }
}
