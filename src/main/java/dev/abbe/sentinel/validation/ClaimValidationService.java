package dev.abbe.sentinel.validation;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.DataFormatException;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.validation.FhirValidator;
import ca.uhn.fhir.validation.ValidationResult;
import org.hl7.fhir.r4.model.Claim;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClaimValidationService {

    private final IParser jsonParser;
    private final FhirValidator validator;

    public ClaimValidationService(FhirContext fhirContext, FhirValidator validator) {
        this.jsonParser = fhirContext.newJsonParser();
        this.validator = validator;
    }

    public ValidationOutcome validate(String claimJson) {
        Claim claim;
        try {
            claim = jsonParser.parseResource(Claim.class, claimJson);
        } catch (DataFormatException e) {
            // Not a parseable FHIR Claim, so there is nothing to validate.
            return ValidationOutcome.notParsed(e.getMessage());
        }

        ValidationResult result = validator.validateWithResult(claim);
        List<ValidationIssue> issues = result.getMessages().stream()
            .map(m -> new ValidationIssue(
                m.getSeverity().name(),
                m.getLocationString() == null ? "" : m.getLocationString(),
                m.getMessage()))
            .toList();

        return ValidationOutcome.of(result.isSuccessful(), issues);
    }
}