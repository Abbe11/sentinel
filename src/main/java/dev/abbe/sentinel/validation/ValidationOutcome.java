package dev.abbe.sentinel.validation;

import java.util.List;

// The result of checking one claim. Typed on purpose: the old version
// returned a raw Map, which hid the response shape from anyone reading it.
public record ValidationOutcome(boolean parsed, boolean valid, List<ValidationIssue> issues) {

    public static ValidationOutcome notParsed(String error) {
        return new ValidationOutcome(false, false, List.of(new ValidationIssue("ERROR", "", error)));
    }

    public static ValidationOutcome of(boolean valid, List<ValidationIssue> issues) {
        return new ValidationOutcome(true, valid, issues);
    }
}