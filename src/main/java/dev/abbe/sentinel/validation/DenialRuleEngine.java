package dev.abbe.sentinel.validation;

import org.hl7.fhir.r4.model.Claim;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DenialRuleEngine {

    private final List<DenialRule> rules;

    // Spring injects every DenialRule bean on the classpath. Adding a rule
    // means adding a class, not editing this one.
    public DenialRuleEngine(List<DenialRule> rules) {
        this.rules = rules;
    }

    public List<ValidationIssue> evaluate(Claim claim) {
        return rules.stream()
            .flatMap(rule -> rule.check(claim).stream())
            .toList();
    }
}