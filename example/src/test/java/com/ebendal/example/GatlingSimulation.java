package com.ebendal.example;

import com.ebendal.gatling.consumer.contract.ConsumerContractStore;
import com.ebendal.gatling.consumer.contract.PactFramework;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.constantUsersPerSec;
import static io.gatling.javaapi.core.CoreDsl.global;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.http;

public class GatlingSimulation extends Simulation {

    private final ConsumerContractStore contractStore = new ConsumerContractStore(new PactFramework());

    public GatlingSimulation() {
        contractStore.loadInteractions(Name.PROVIDER);
        HttpProtocolBuilder httpProtocol = http.baseUrl("http://localhost:8080");
        var scenario = scenario("Example scenario")
            .exec(contractStore.interaction(Name.CONSUMER, Name.INTERACTION));
        setUp(scenario.injectOpen(constantUsersPerSec(1).during(Duration.ofMinutes(1))))
            .protocols(httpProtocol)
            .assertions(global().successfulRequests().percent().gte(100.0));
    }

    @Override
    public void before() {
    }
}
