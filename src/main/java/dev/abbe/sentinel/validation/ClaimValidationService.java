package dev.abbe.sentinel.validation;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.DataFormatException;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.validation.FhirValidator;
import ca.uhn.fhir.validation.ValidationResult;
import dev.abbe.sentinel.security.AuditLogger;
import org.hl7.fhir.r4.model.Claim;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ClaimValidationService {

    private final IParser jsonParser;
    private final FhirValidator validator;
    private final DenialRuleEngine denialRuleEngine;
    private final AuditLogger auditLogger;

    public ClaimValidationService(FhirContext fhirContext, FhirValidator validator,
                                  DenialRuleEngine denialRuleEngine, AuditLogger auditLogger) {
        this.jsonParser = fhirContext.newJsonParser();
        this.validator = validator;
        this.denialRuleEngine = denialRuleEngine;
        this.auditLogger = auditLogger;
    }

    public ValidationOutcome validate(String claimJson) {
        Claim claim;
        try {
            claim = jsonParser.parseResource(Claim.class, claimJson);
        } catch (DataFormatException e) {
            ValidationOutcome outcome = ValidationOutcome.notParsed(e.getMessage());
            auditLogger.record("(unparsed)", outcome);
            return outcome;
        }

        List<ValidationIssue> issues = new ArrayList<>();

        // Layer 1: structural FHIR R4 validation.
        ValidationResult structural = validator.validateWithResult(claim);
        structural.getMessages().forEach(m -> issues.add(new ValidationIssue(
            m.getSeverity().name(),
            "",
            m.getLocationString() == null ? "" : m.getLocationString(),
            m.getMessage())));

        // Layer 2: payer denial rules mapped to NPHIES codes.
        List<ValidationIssue> denialIssues = denialRuleEngine.evaluate(claim);
        issues.addAll(denialIssues);

        boolean structuralOk = structural.isSuccessful();
        boolean noDenialErrors = denialIssues.stream().noneMatch(i -> "ERROR".equals(i.severity()));
        ValidationOutcome outcome = ValidationOutcome.of(structuralOk && noDenialErrors, issues);

        String claimId = claim.hasId() ? claim.getIdElement().getIdPart() : "(none)";
        auditLogger.record(claimId, outcome);
        return outcome;
    }
}