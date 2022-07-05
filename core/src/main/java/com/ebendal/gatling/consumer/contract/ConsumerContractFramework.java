package com.ebendal.gatling.consumer.contract;

import java.util.Set;

public interface ConsumerContractFramework {
    Set<Interaction> findInteractions(String providerName);
}
