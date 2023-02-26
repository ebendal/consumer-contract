package com.ebendal.gatling.consumer.contract;


import com.ebendal.consumer.contract.core.Interaction;
import com.ebendal.consumer.contract.pact.PactFramework;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class PactFrameworkTest {

    private final PactFramework sut = new PactFramework();

    @Test
    void test() {
        Set<Interaction> interactions = sut.findInteractions("book-service");

        assertThat(interactions).isNotEmpty();
    }

}