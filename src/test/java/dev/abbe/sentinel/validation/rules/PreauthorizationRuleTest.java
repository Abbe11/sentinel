package dev.abbe.sentinel.validation.rules;

import dev.abbe.sentinel.validation.ValidationIssue;
import org.hl7.fhir.r4.model.Claim;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PreauthorizationRuleTest {

    private final PreauthorizationRule rule = new PreauthorizationRule();

    @Test
    void warnsWhenNoPreauthorizationReferenceIsPresent() {
        Claim claim = new Claim();
        claim.addItem(new Claim.ItemComponent());

        List<ValidationIssue> issues = rule.check(claim);

        assertEquals(1, issues.size());
        assertEquals("BE-1-4", issues.get(0).code());
        assertEquals("WARNING", issues.get(0).severity());
    }

    @Test
    void passesWhenPreauthorizationReferenceIsPresent() {
        Claim claim = new Claim();
        claim.addItem(new Claim.ItemComponent());
        claim.addInsurance(new Claim.InsuranceComponent().addPreAuthRef("PA-12345"));

        assertTrue(rule.check(claim).isEmpty());
    }
}