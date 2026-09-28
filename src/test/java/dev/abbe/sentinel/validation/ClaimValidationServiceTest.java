package dev.abbe.sentinel.validation;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class ClaimValidationServiceTest {

    @Autowired
    ClaimValidationService service;

    @Test
    void rejectsMalformedJson() {
        ValidationOutcome outcome = service.validate("{ this is not json ");
        assertFalse(outcome.parsed(), "Malformed JSON should not parse");
    }

    @Test
    void rejectsWrongResourceType() {
        // A Patient sent to a Claim endpoint is not a claim at all.
        ValidationOutcome outcome = service.validate("{\"resourceType\":\"Patient\",\"id\":\"p1\"}");
        assertFalse(outcome.parsed(), "A Patient is not a Claim");
    }

    @Test
    void flagsClaimMissingRequiredFields() {
        // Well-formed FHIR, but missing the fields a real Claim requires.
        String minimalClaim = "{\"resourceType\":\"Claim\",\"id\":\"claim-001\",\"status\":\"active\"}";
        ValidationOutcome outcome = service.validate(minimalClaim);

        assertTrue(outcome.parsed(), "It is valid JSON and a real Claim resource");
        assertFalse(outcome.valid(), "But it is missing required fields, so it is not valid");
        assertFalse(outcome.issues().isEmpty(), "There should be reported issues");
    }
}