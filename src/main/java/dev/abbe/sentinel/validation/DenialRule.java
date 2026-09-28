package dev.abbe.sentinel.validation;

import org.hl7.fhir.r4.model.Claim;

import java.util.List;

// A payer-side check that FHIR structural validation cannot see. Each rule
// maps to a real NPHIES adjudication reason code. To add one, implement this
// interface and annotate the class @Component; the engine picks it up.
public interface DenialRule {
    List<ValidationIssue> check(Claim claim);
}