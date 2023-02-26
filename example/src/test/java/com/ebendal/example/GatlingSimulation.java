package com.ebendal.example;

import com.ebendal.consumer.contract.gatling.GatlingConsumerContractStore;
import com.ebendal.consumer.contract.pact.PactFramework;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import java.time.Duration;

import static com.ebendal.consumer.contract.gatling.dsl.DSL.bodyInject;
import static com.ebendal.consumer.contract.gatling.dsl.DSL.checkAndSave;
import static com.ebendal.consumer.contract.gatling.dsl.DSL.headerReplace;
import static com.ebendal.consumer.contract.gatling.dsl.DSL.pathInject;
import static com.ebendal.consumer.contract.gatling.dsl.DSL.queryParameterReplace;
import static io.gatling.javaapi.core.CoreDsl.constantUsersPerSec;
import static io.gatling.javaapi.core.CoreDsl.global;
import static io.gatling.javaapi.core.CoreDsl.jsonPath;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.http;

public class GatlingSimulation extends Simulation {

    private final GatlingConsumerContractStore contractStore = new GatlingConsumerContractStore(new PactFramework());

    public GatlingSimulation() {
        contractStore.loadInteractions(Name.BOOK_PROVIDER);
        HttpProtocolBuilder httpProtocol = http.baseUrl("http://localhost:8080");
        var statefulScenario = scenario("Stateful scenario")
            .exec(contractStore.interaction(Name.CONSUMER_ONE, Name.CREATE_BOOK_INTERACTION, checkAndSave(jsonPath("$.id"), "id")))
            .exec(contractStore.interaction(Name.CONSUMER_ONE, Name.GET_BOOK_BY_ID_INTERACTION, pathInject(3, "id")))
            .exec(contractStore.interaction(Name.CONSUMER_ONE, Name.CHECK_ID,
                pathInject(3, "id"),
                headerReplace("headerId", "id"),
                queryParameterReplace("queryParameterId", "id"),
                bodyInject("$.id", "id")
            ));
        var statelessScenario = scenario("Stateless scenario")
            .exec(contractStore.allInteractionsForConsumer(Name.CONSUMER_TWO));

        setUp(
            statefulScenario.injectOpen(constantUsersPerSec(1).during(Duration.ofMinutes(1))),
            statelessScenario.injectOpen(constantUsersPerSec(1).during(Duration.ofMinutes(1))))
            .protocols(httpProtocol)
            .assertions(global().successfulRequests().percent().gte(100.0));
    }
}
