package com.ebendal.consumer.contract.core;

import java.util.Set;

public interface ConsumerContractFramework {

    Set<Interaction> findInteractions(String providerName);
}
