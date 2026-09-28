package dev.abbe.sentinel.validation;

// A single problem found in a claim. "code" carries the NPHIES adjudication
// reason for denial-rule findings, and is empty for FHIR structural issues.
public record ValidationIssue(String severity, String code, String location, String message) {
}