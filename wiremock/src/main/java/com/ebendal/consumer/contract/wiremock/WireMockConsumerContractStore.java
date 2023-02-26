package com.ebendal.consumer.contract.wiremock;

import com.ebendal.consumer.contract.core.ConsumerContractFramework;
import com.ebendal.consumer.contract.core.ConsumerContractStore;
import com.ebendal.consumer.contract.core.HttpInteraction;
import com.ebendal.consumer.contract.core.Interaction;
import com.ebendal.consumer.contract.wiremock.dsl.HeaderTemplatedValue;
import com.ebendal.consumer.contract.wiremock.dsl.JsonBodyTemplatedValue;
import com.ebendal.consumer.contract.wiremock.dsl.TemplatedValue;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.http.HttpHeader;
import com.github.tomakehurst.wiremock.http.HttpHeaders;
import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.requestMatching;
import static java.util.stream.Collectors.toMap;

@Slf4j
public class WireMockConsumerContractStore extends ConsumerContractStore {

    public WireMockConsumerContractStore(ConsumerContractFramework framework) {
        super(framework);
    }

    @Override
    protected Set<Class<? extends Interaction>> supportedInteractionsTypes() {
        return Set.of(HttpInteraction.class);
    }

    public MappingBuilder interaction(String consumerName, String interactionName, TemplatedValue... templatedValues) {
        HttpInteraction interaction = (HttpInteraction) getInteraction(consumerName, interactionName);
        ResponseDefinitionBuilder responseDefinitionBuilder = aResponse()
            .withHeaders(extractHeaders(interaction, templatedValuesOfType(HeaderTemplatedValue.class, templatedValues)))
            .withStatus(interaction.getStatusCode());
        Optional<String> body = extractBody(interaction, templatedValuesOfType(JsonBodyTemplatedValue.class, templatedValues));
        if (body.isPresent()) {
            responseDefinitionBuilder = responseDefinitionBuilder.withBody(body.get());
        }
        if (templatedValues.length > 0) {
            responseDefinitionBuilder = responseDefinitionBuilder.withTransformers("response-template");
        }
        return requestMatching(new InteractionMatcher(interaction)).willReturn(responseDefinitionBuilder);
    }

    private Optional<String> extractBody(HttpInteraction interaction, Set<JsonBodyTemplatedValue> templatedValues) {
        return Optional.ofNullable(interaction.getResponseBody())
            .map(body -> {
                String result = body;
                for (var bodyInject : templatedValues) {
                    result = JsonPath.parse(result).set(bodyInject.getJsonPath(), String.format("{{%s}}", bodyInject.getExpression())).jsonString();
                }
                return result;
            });
    }

    private static HttpHeaders extractHeaders(HttpInteraction interaction, Set<HeaderTemplatedValue> templatedValues) {
        Map<String, String> headerTemplates = templatedValues.stream()
            .collect(toMap(HeaderTemplatedValue::getKey, HeaderTemplatedValue::getExpression));
        return new HttpHeaders(interaction.getResponseHeaders().entrySet().stream()
            .map(header -> {
                if (headerTemplates.containsKey(header.getKey())) {
                    return new HttpHeader(header.getKey(), String.format("{{%s}}", headerTemplates.get(header.getKey())));
                }
                return new HttpHeader(header.getKey(), header.getValue());
            })
            .collect(Collectors.toList()));
    }

    private static <E> Set<E> templatedValuesOfType(Class<E> clazz, TemplatedValue[] stateActions) {
        return Arrays.stream(stateActions).filter(clazz::isInstance).map(clazz::cast).collect(Collectors.toSet());
    }
}
