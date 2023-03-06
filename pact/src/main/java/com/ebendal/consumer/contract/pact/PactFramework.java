package com.ebendal.consumer.contract.pact;

import au.com.dius.pact.core.model.IRequest;
import au.com.dius.pact.core.model.OptionalBody;
import au.com.dius.pact.core.model.Pact;
import au.com.dius.pact.core.model.SynchronousRequestResponse;
import au.com.dius.pact.core.model.matchingrules.MatchingRuleGroup;
import au.com.dius.pact.core.model.matchingrules.RegexMatcher;
import com.ebendal.consumer.contract.core.ConsumerContractFramework;
import com.ebendal.consumer.contract.core.HttpInteraction;
import com.ebendal.consumer.contract.core.HttpMethod;
import com.ebendal.consumer.contract.core.Interaction;
import com.ebendal.consumer.contract.core.Path;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
            .filter(au.com.dius.pact.core.model.Interaction::isSynchronousRequestResponse)
            .map(interaction -> {
                var request = Optional.ofNullable(interaction.asSynchronousRequestResponse())
                    .map(SynchronousRequestResponse::getRequest)
                    .orElseThrow();
                var response = Optional.ofNullable(interaction.asSynchronousRequestResponse())
                    .map(SynchronousRequestResponse::getResponse)
                    .orElseThrow();
                var requestBody = Optional.of(request.getBody())
                    .filter(OptionalBody::isPresent)
                    .map(OptionalBody::valueAsString)
                    .orElse(null);
                return HttpInteraction.builder()
                    .providerName(pact.getProvider().getName())
                    .consumerName(pact.getConsumer().getName())
                    .interactionName(interaction.getDescription())
                    .method(HttpMethod.valueOf(request.getMethod()))
                    .path(getPath(request))
                    .requestBody(requestBody)
                    .requestHeaders(request.getHeaders())
                    .queryParameters(request.getQuery())
                    .statusCode(response.getStatus())
                    .responseHeaders(response.getHeaders())
                    .responseBody(response.getBody().valueAsString())
                    .build();
            })
            .collect(Collectors.toSet());
    }

    private static Path getPath(IRequest request) {
        String path = request.getPath();
        String regex = request.getMatchingRules().rulesForCategory("path").getMatchingRules().values().stream()
            .findFirst()
            .map(MatchingRuleGroup::getRules)
            .flatMap(rules -> rules.stream().findFirst())
            .filter(RegexMatcher.class::isInstance)
            .map(RegexMatcher.class::cast)
            .map(RegexMatcher::getRegex)
            .orElse(String.format("^%s$", path));
        return Path.builder()
            .example(path)
            .regularExpression(regex)
            .build();
    }
}
