package com.ebendal.consumer.contract.pact;

import au.com.dius.pact.core.model.Pact;
import au.com.dius.pact.core.model.matchingrules.MatchingRuleGroup;
import au.com.dius.pact.core.model.matchingrules.RegexMatcher;
import com.ebendal.consumer.contract.core.ConsumerContractFramework;
import com.ebendal.consumer.contract.core.HttpInteraction;
import com.ebendal.consumer.contract.core.HttpMethod;
import com.ebendal.consumer.contract.core.Interaction;
import com.ebendal.consumer.contract.core.Path;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class PactFramework implements ConsumerContractFramework {
    private final CustomPactBrokerLoader customPactBrokerLoader;

    public PactFramework() {
        customPactBrokerLoader = new CustomPactBrokerLoader();
    }

    @Override
    public Set<Interaction> findInteractions(String providerName) {
        return customPactBrokerLoader.load(providerName).stream()
            .filter(Pact::isRequestResponsePact)
            .flatMap(pact -> map(pact).stream())
            .collect(Collectors.toSet());
    }

    private Set<Interaction> map(Pact pact) {
        return pact.getInteractions().stream()
            .map(interaction -> {
                var body = interaction.asSynchronousRequestResponse().getRequest().getBody().isMissing() ? null : interaction.asSynchronousRequestResponse().getRequest().getBody().valueAsString();
                return HttpInteraction.builder()
                    .providerName(pact.getProvider().getName())
                    .consumerName(pact.getConsumer().getName())
                    .interactionName(interaction.getDescription())
                    .method(HttpMethod.valueOf(interaction.asSynchronousRequestResponse().getRequest().getMethod()))
                    .path(getPath(interaction))
                    .requestBody(body)
                    .requestHeaders(interaction.asSynchronousRequestResponse().getRequest().getHeaders())
                    .queryParameters(interaction.asSynchronousRequestResponse().getRequest().getQuery())
                    .statusCode(interaction.asSynchronousRequestResponse().getResponse().getStatus())
                    .responseHeaders(interaction.asSynchronousRequestResponse().getResponse().getHeaders())
                    .responseBody(interaction.asSynchronousRequestResponse().getResponse().getBody().valueAsString())
                    .build();
            })
            .collect(Collectors.toSet());
    }

    @NonNull
    private static Path getPath(au.com.dius.pact.core.model.Interaction interaction) {
        String path = interaction.asSynchronousRequestResponse().getRequest().getPath();
        String regex = interaction.asSynchronousRequestResponse().getRequest().getMatchingRules().rulesForCategory("path").getMatchingRules().values().stream()
            .findFirst()
            .map(MatchingRuleGroup::getRules)
            .flatMap(rules -> rules.stream().findFirst())
            .filter(RegexMatcher.class::isInstance)
            .map(RegexMatcher.class::cast)
            .map(RegexMatcher::getRegex)
            .orElse(String.format("^%s$", path));
        log.info("Regex: {}", regex);
        return Path.builder()
            .example(path)
            .regularExpression(regex)
            .build();
    }
}
