package dev.abbe.sentinel.validation.rules;

import dev.abbe.sentinel.validation.ValidationIssue;
import org.hl7.fhir.r4.model.Claim;
import org.hl7.fhir.r4.model.Money;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CalculationDiscrepancyRuleTest {

    private final CalculationDiscrepancyRule rule = new CalculationDiscrepancyRule();

    @Test
    void flagsTotalThatDoesNotMatchLineItems() {
        Claim claim = new Claim();
        claim.setTotal(new Money().setValue(new BigDecimal("150.00")));
        claim.addItem(new Claim.ItemComponent().setNet(new Money().setValue(new BigDecimal("100.00"))));

        List<ValidationIssue> issues = rule.check(claim);

        assertEquals(1, issues.size());
        assertEquals("BE-1-6", issues.get(0).code());
    }

    @Test
    void passesWhenTotalMatchesLineItems() {
        Claim claim = new Claim();
        claim.setTotal(new Money().setValue(new BigDecimal("100.00")));
        claim.addItem(new Claim.ItemComponent().setNet(new Money().setValue(new BigDecimal("100.00"))));

        assertTrue(rule.check(claim).isEmpty());
    }
}