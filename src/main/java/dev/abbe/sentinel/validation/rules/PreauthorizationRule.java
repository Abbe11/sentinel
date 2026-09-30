package dev.abbe.sentinel.validation.rules;

import dev.abbe.sentinel.validation.DenialRule;
import dev.abbe.sentinel.validation.ValidationIssue;
import org.hl7.fhir.r4.model.Claim;
import org.springframework.stereotype.Component;

import java.util.List;

// NPHIES adjudication reason BE-1-4: preauthorization required and not obtained.
// The claim alone cannot tell us whether a service needed preauthorization, so
// a missing reference is a WARNING (confirm this), not an ERROR (reject this).
@Component
public class PreauthorizationRule implements DenialRule {

    private static final String CODE = "BE-1-4";

    @Override
    public List<ValidationIssue> check(Claim claim) {
        if (claim.getItem().isEmpty()) {
            return List.of();
        }
        boolean hasPreauth = claim.getInsurance().stream().anyMatch(ins -> ins.hasPreAuthRef());
        if (!hasPreauth) {
            return List.of(new ValidationIssue(
                "WARNING",
                CODE,
                "Claim.insurance.preAuthRef",
                "No preauthorization reference is attached. Confirm whether these services require preauthorization."));
        }
        return List.of();
    }
}