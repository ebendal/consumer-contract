package com.ebendal.consumer.contract.wiremock;

import com.ebendal.consumer.contract.core.HttpInteraction;
import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.matching.MatchResult;
import com.github.tomakehurst.wiremock.matching.RequestMatcherExtension;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Pattern;

@Slf4j
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class InteractionMatcher extends RequestMatcherExtension {

    private final HttpInteraction interaction;

    @Override
    public MatchResult match(Request request, Parameters parameters) {
        if (!request.getMethod().toString().equals(interaction.getMethod().toString())) {
            return MatchResult.noMatch();
        }
        String regularExpression = interaction.getPath().getRegularExpression();
        if (!Pattern.matches(regularExpression, request.getUrl())) {
            return MatchResult.noMatch();
        }
        return MatchResult.exactMatch();
    }
}
