package com.ebendal.example.stub;

import com.ebendal.consumer.contract.pact.PactFramework;
import com.ebendal.consumer.contract.wiremock.WireMockConsumerContractStore;
import com.ebendal.example.Name;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.ebendal.consumer.contract.wiremock.dsl.DSL.bodyInject;
import static com.ebendal.consumer.contract.wiremock.dsl.DSL.headerReplace;

@Configuration
public class WireMockSpringConfiguration {

    @Bean
    WireMockServer wireMockServer() {
        WireMockConsumerContractStore consumerContractStore = new WireMockConsumerContractStore(new PactFramework());
        consumerContractStore.loadInteractions(Name.AUTHOR_PROVIDER);
        WireMockServer wireMockServer = new WireMockServer(WireMockConfiguration.options()
            .port(8081)
            .extensions(new ResponseTemplateTransformer(false)));
        wireMockServer.start();
        wireMockServer.stubFor(consumerContractStore.interaction(
            Name.BOOK_PROVIDER,
            Name.GET_AUTHOR_BY_ID_INTERACTION,
            bodyInject("$.id", "request.path.[2]"),
            headerReplace("headerId", "request.path.[2]")));
        return wireMockServer;
    }
}
