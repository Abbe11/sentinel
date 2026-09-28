package dev.abbe.sentinel.validation;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.DataFormatException;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.validation.FhirValidator;
import ca.uhn.fhir.validation.ValidationResult;
import org.hl7.fhir.r4.model.Claim;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ClaimValidationService {

    private final IParser jsonParser;
    private final FhirValidator validator;
    private final DenialRuleEngine denialRuleEngine;

    public ClaimValidationService(FhirContext fhirContext, FhirValidator validator, DenialRuleEngine denialRuleEngine) {
        this.jsonParser = fhirContext.newJsonParser();
        this.validator = validator;
        this.denialRuleEngine = denialRuleEngine;
    }

    public ValidationOutcome validate(String claimJson) {
        Claim claim;
        try {
            claim = jsonParser.parseResource(Claim.class, claimJson);
        } catch (DataFormatException e) {
            return ValidationOutcome.notParsed(e.getMessage());
        }

        List<ValidationIssue> issues = new ArrayList<>();

        // Layer 1: is this structurally valid FHIR R4?
        ValidationResult structural = validator.validateWithResult(claim);
        structural.getMessages().forEach(m -> issues.add(new ValidationIssue(
            m.getSeverity().name(),
            "",
            m.getLocationString() == null ? "" : m.getLocationString(),
            m.getMessage())));

        // Layer 2: payer denial rules that FHIR cannot see, mapped to NPHIES codes.
        List<ValidationIssue> denialIssues = denialRuleEngine.evaluate(claim);
        issues.addAll(denialIssues);

        boolean structuralOk = structural.isSuccessful();
        boolean noDenialErrors = denialIssues.stream().noneMatch(i -> "ERROR".equals(i.severity()));

        return ValidationOutcome.of(structuralOk && noDenialErrors, issues);
    }
}