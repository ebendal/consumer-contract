package com.ebendal.gatling.consumer.contract;

import com.ebendal.gatling.consumer.contract.dsl.BodyInject;
import com.ebendal.gatling.consumer.contract.dsl.CheckAndSave;
import com.ebendal.gatling.consumer.contract.dsl.HeaderReplace;
import com.ebendal.gatling.consumer.contract.dsl.MultiValueQueryParameterReplace;
import com.ebendal.gatling.consumer.contract.dsl.PathInject;
import com.ebendal.gatling.consumer.contract.dsl.QueryParameterReplace;
import com.ebendal.gatling.consumer.contract.dsl.SingleValueQueryParameterReplace;
import com.ebendal.gatling.consumer.contract.dsl.StateAction;
import com.jayway.jsonpath.JsonPath;
import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.exec.Execs;
import io.gatling.javaapi.http.Http;
import io.gatling.javaapi.http.HttpRequestActionBuilder;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static io.gatling.javaapi.core.CoreDsl.StringBody;
import static io.gatling.javaapi.core.CoreDsl.exec;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;
import static java.util.Collections.emptyMap;
import static java.util.function.UnaryOperator.identity;
import static java.util.stream.Collectors.toMap;

@Slf4j
@RequiredArgsConstructor
public class ConsumerContractStore {

    private final ConsumerContractFramework framework;

    private String providerName;

    private Map<InteractionKey, Interaction> interactions = emptyMap();

    public void loadInteractions(String providerName) {
        this.providerName = providerName;
        interactions = framework.findInteractions(providerName).stream()
            .collect(toMap(interaction -> InteractionKey.builder()
                .consumerName(interaction.getConsumerName())
                .providerName(interaction.getProviderName())
                .interactionName(interaction.getInteractionName())
                .build(), identity()));
    }

    public ChainBuilder allInteractions() {
        return interactions.keySet().stream()
            .map(key -> interaction(key.getConsumerName(), key.getInteractionName()))
            .reduce(ChainBuilder.EMPTY, Execs::exec);
    }

    public ChainBuilder allInteractionsForConsumer(String consumerName) {
        return interactions.keySet().stream()
            .filter(key -> key.getConsumerName().equals(consumerName))
            .map(key -> interaction(key.getConsumerName(), key.getInteractionName()))
            .reduce(ChainBuilder.EMPTY, Execs::exec);
    }

    public ChainBuilder interaction(String consumerName, String interactionName, StateAction... stateActions) {
        Interaction interaction = getInteraction(consumerName, interactionName);
        Http http = http(interactionName);
        HttpRequestActionBuilder httpRequestActionBuilder = baseBuilder(http, interaction, actionsOfType(PathInject.class, stateActions));
        httpRequestActionBuilder = body(httpRequestActionBuilder, interaction, actionsOfType(BodyInject.class, stateActions));
        httpRequestActionBuilder = headers(httpRequestActionBuilder, interaction, actionsOfType(HeaderReplace.class, stateActions));
        httpRequestActionBuilder = queryParameters(httpRequestActionBuilder, interaction, actionsOfType(QueryParameterReplace.class, stateActions));
        httpRequestActionBuilder = checks(interaction, httpRequestActionBuilder, actionsOfType(CheckAndSave.class, stateActions));
        return exec(httpRequestActionBuilder);
    }

    private HttpRequestActionBuilder checks(Interaction interaction, HttpRequestActionBuilder httpRequestActionBuilder, Set<CheckAndSave> checkAndSaves) {
        for (CheckAndSave checkAndSave : checkAndSaves) {
            log.info("Check: {}", checkAndSave.getCheck());
            httpRequestActionBuilder = httpRequestActionBuilder.check(checkAndSave.getCheck());
        }
        return httpRequestActionBuilder.check(status().is(interaction.getStatusCode()));
    }

    private HttpRequestActionBuilder headers(HttpRequestActionBuilder httpRequestActionBuilder, Interaction interaction, Set<HeaderReplace> headerReplaces) {
        for (Map.Entry<String, List<String>> entry : interaction.getRequestHeaders().entrySet()) {
            HttpRequestActionBuilder copy = httpRequestActionBuilder;
            httpRequestActionBuilder = headerReplaces.stream()
                .filter(replace -> replace.getHeaderKey().equals(entry.getKey()))
                .findFirst()
                .map(replace -> copy.header(entry.getKey(), replace.getStateKeyExpression()))
                .orElse(copy.header(entry.getKey(), String.join(",", entry.getValue())));

        }
        return httpRequestActionBuilder;
    }

    private HttpRequestActionBuilder body(HttpRequestActionBuilder httpRequestActionBuilder, Interaction interaction, Set<BodyInject> bodyInjects) {
        return Optional.ofNullable(interaction.getBody())
            .map(body -> {
                String result = body;
                for (var bodyInject : bodyInjects) {
                    result = JsonPath.parse(result).set(bodyInject.getJsonPath(), bodyInject.getStateKeyExpression()).jsonString();
                }
                return result;
            })
            .map(body -> httpRequestActionBuilder.body(StringBody(body)))
            .orElse(httpRequestActionBuilder);
    }

    private HttpRequestActionBuilder queryParameters(HttpRequestActionBuilder httpRequestActionBuilder, Interaction interaction, Set<QueryParameterReplace> queryParameterReplaces) {
        for (Map.Entry<String, List<String>> entry : interaction.getQueryParameters().entrySet()) {
            Optional<QueryParameterReplace> queryParameterReplaceOptional = queryParameterReplaces.stream().filter(replace -> replace.getParameterKey().equals(entry.getKey())).findFirst();
            if (queryParameterReplaceOptional.isPresent()) {
                QueryParameterReplace queryParameterReplace = queryParameterReplaceOptional.get();
                if (queryParameterReplace instanceof SingleValueQueryParameterReplace) {
                    httpRequestActionBuilder = httpRequestActionBuilder.queryParam(entry.getKey(), queryParameterReplace.getStateKeyExpression());
                } else if (queryParameterReplace instanceof MultiValueQueryParameterReplace) {
                    httpRequestActionBuilder = httpRequestActionBuilder.multivaluedQueryParam(entry.getKey(), queryParameterReplace.getStateKeyExpression());
                }
            } else if (entry.getValue().size() == 1) {
                httpRequestActionBuilder = httpRequestActionBuilder.queryParam(entry.getKey(), entry.getValue().get(0));
            } else {
                httpRequestActionBuilder = httpRequestActionBuilder.multivaluedQueryParam(entry.getKey(), new ArrayList<>(entry.getValue()));
            }
        }
        return httpRequestActionBuilder;
    }

    private HttpRequestActionBuilder baseBuilder(Http http, Interaction interaction, Set<PathInject> pathInjects) {
        List<String> pathElements = new ArrayList<>(List.of(interaction.getPath().split("/")));
        pathInjects.forEach(pathInject -> pathElements.set(pathInject.getIndex(), pathInject.getStateKeyExpression()));
        String path = String.join("/", pathElements);
        log.info("Path: {}", path);
        HttpMethod method = interaction.getMethod();
        switch (method) {
            case PUT:
                return http.put(path);
            case POST:
                return http.post(path);
            case GET:
                return http.get(path);
            case DELETE:
                return http.delete(path);
            case HEAD:
                return http.head(path);
            case OPTIONS:
                return http.options(path);
            case PATCH:
                return http.patch(path);
            default:
                throw new GatlingConsumerContractException("Interaction method not allowed: " + method);
        }
    }

    private Interaction getInteraction(String consumerName, String interactionName) {
        return Optional.ofNullable(interactions.get(InteractionKey.builder()
                .consumerName(consumerName)
                .providerName(providerName)
                .interactionName(interactionName)
                .build()))
            .orElseThrow(() -> new GatlingConsumerContractException("Interaction not found"));
    }

    private static <E> Set<E> actionsOfType(Class<E> clazz, StateAction[] stateActions) {
        return Arrays.stream(stateActions).filter(clazz::isInstance).map(clazz::cast).collect(Collectors.toSet());
    }

    @Builder
    @Getter
    @EqualsAndHashCode
    private static class InteractionKey {

        private final String providerName;
        private final String consumerName;
        private final String interactionName;
    }
}
