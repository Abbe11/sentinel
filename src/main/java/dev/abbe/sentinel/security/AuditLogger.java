package dev.abbe.sentinel.security;

import dev.abbe.sentinel.validation.ValidationIssue;
import dev.abbe.sentinel.validation.ValidationOutcome;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

// Writes an audit record for every validation. Deliberately PHI-safe: it logs
// the claim id, the outcome and which NPHIES codes fired, never the raw claim
// or any patient data. In a real deployment these lines are what a SOC watches.
@Component
public class AuditLogger {

    private static final Logger log = LoggerFactory.getLogger("sentinel.audit");

    public void record(String claimId, ValidationOutcome outcome) {
        String codes = outcome.issues().stream()
            .map(ValidationIssue::code)
            .filter(c -> c != null && !c.isBlank())
            .distinct()
            .collect(Collectors.joining(","));

        log.info("claimId={} parsed={} valid={} issues={} codes=[{}]",
            claimId, outcome.parsed(), outcome.valid(), outcome.issues().size(), codes);
    }
}