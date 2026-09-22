package dev.abbe.sentinel;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.DataFormatException;
import ca.uhn.fhir.parser.IParser;
import org.hl7.fhir.r4.model.Claim;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/fhir")
public class ValidationController {

    private final IParser jsonParser;

    public ValidationController(FhirContext fhirContext) {
        this.jsonParser = fhirContext.newJsonParser();
    }

    @PostMapping(value = "/validate", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Map<String, Object>> validate(@RequestBody String body) {
        try {
            Claim claim = jsonParser.parseResource(Claim.class, body);
            return ResponseEntity.ok(Map.of(
                "parsed", true,
                "resourceType", "Claim",
                "claimId", claim.hasId() ? claim.getIdElement().getIdPart() : "(none)",
                "status", claim.hasStatus() ? claim.getStatus().toCode() : "(none)"
            ));
        } catch (DataFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "parsed", false,
                "error", e.getMessage()
            ));
        }
    }
}