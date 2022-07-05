package com.ebendal.gatling.consumer.contract;

import au.com.dius.pact.core.model.Pact;
import au.com.dius.pact.core.support.expressions.ExpressionParser;
import au.com.dius.pact.provider.junitsupport.loader.PactBrokerLoader;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

class CustomPactBrokerLoader extends PactBrokerLoader {

    CustomPactBrokerLoader(String pactBrokerUrl) {
        super(null, null, null, null, Collections.emptyList(), Collections.emptyList(), false, null, null, null, "false", Collections.emptyList(), "", "", pactBrokerUrl, "false", new ExpressionParser());
    }

    @Override
    public List<Pact> load(String providerName) {
        try {
            return super.load(providerName);
        } catch (IOException e) {
            throw new GatlingConsumerContractException(e);
        }
    }
}
