package dev.abbe.sentinel.web;

import dev.abbe.sentinel.validation.ClaimValidationService;
import dev.abbe.sentinel.validation.ValidationOutcome;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fhir")
public class ValidationController {

    private final ClaimValidationService validationService;

    public ValidationController(ClaimValidationService validationService) {
        this.validationService = validationService;
    }

    // Thin on purpose: take the request, hand it to the service, map the
    // outcome to a status. The logic lives in the service so it is testable
    // without a running web server.
    @PostMapping(value = "/validate", consumes = "application/json", produces = "application/json")
    public ResponseEntity<ValidationOutcome> validate(@RequestBody String claimJson) {
        ValidationOutcome outcome = validationService.validate(claimJson);
        HttpStatus status = outcome.parsed() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(outcome);
    }
}