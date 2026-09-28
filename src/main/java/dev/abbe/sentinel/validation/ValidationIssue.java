package dev.abbe.sentinel.validation;

// A single problem found in a claim, in a shape the API and UI can render.
public record ValidationIssue(String severity, String location, String message) {
}