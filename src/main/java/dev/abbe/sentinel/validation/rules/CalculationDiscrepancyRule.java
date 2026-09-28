package dev.abbe.sentinel.validation.rules;

import dev.abbe.sentinel.validation.DenialRule;
import dev.abbe.sentinel.validation.ValidationIssue;
import org.hl7.fhir.r4.model.Claim;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

// NPHIES adjudication reason BE-1-6: calculation discrepancy.
// The claim's stated total must equal the sum of its line-item net amounts.
@Component
public class CalculationDiscrepancyRule implements DenialRule {

    private static final String CODE = "BE-1-6";

    @Override
    public List<ValidationIssue> check(Claim claim) {
        // If there is no total or no items, this rule cannot judge the claim.
        if (!claim.hasTotal() || !claim.getTotal().hasValue() || claim.getItem().isEmpty()) {
            return List.of();
        }

        BigDecimal stated = claim.getTotal().getValue();
        BigDecimal sumOfItems = claim.getItem().stream()
            .filter(item -> item.hasNet() && item.getNet().hasValue())
            .map(item -> item.getNet().getValue())
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (stated.compareTo(sumOfItems) != 0) {
            String message = "Claim total (" + stated.toPlainString()
                + ") does not equal the sum of line item amounts (" + sumOfItems.toPlainString() + ").";
            return List.of(new ValidationIssue("ERROR", CODE, "Claim.total", message));
        }
        return List.of();
    }
}