package com.ebendal.consumer.contract.pact;

import au.com.dius.pact.core.model.Pact;
import au.com.dius.pact.provider.junitsupport.loader.PactBroker;
import au.com.dius.pact.provider.junitsupport.loader.PactBrokerLoader;
import com.ebendal.consumer.contract.core.ConsumerContractException;

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
            throw new ConsumerContractException(e);
        }
    }

    @PactBroker
    static class BrokerConfig {
    }
}
