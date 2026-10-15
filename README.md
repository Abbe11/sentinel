# Sentinel

A FHIR R4 claim service that will catch malformed and non-conformant health-insurance claims **before** they reach a payer.

Backend / security engineering project targeting the FHIR-based claims platforms now mandated in the Gulf (Saudi **NPHIES**) and the EU (**European Health Data Space**). Work in progress.

## What it does today
- Accepts a FHIR R4 `Claim` as JSON over a REST API (`POST /fhir/validate`)
- Parses it with HAPI FHIR and rejects anything that is not a valid FHIR resource (HTTP 400)
- Runs structural validation against the base FHIR R4 profiles and reports each issue with its location
- Runs a pluggable denial-rule engine modelled on NPHIES adjudication reasons. Adding a rule means adding one class:
  - **BE-1-4** no preauthorization reference attached (warning, because the claim alone cannot prove preauth was needed)
  - **BE-1-6** claim total does not equal the sum of its line items (error)
- Writes a PHI-safe audit line for every validation: claim id, outcome and which codes fired, never patient data
- Ships a web UI with single-claim validation and a batch view that summarises which denial codes fire most, plus Swagger UI
- Unit tests for the validation service and each rule

> The thesis: "parsed" is not "valid". A claim can be well-formed JSON and still be rejected by a payer. Sentinel surfaces that gap before submission.

## Tech stack
Java 21 · Spring Boot 4 · HAPI FHIR 8 (R4) · Maven (wrapper included) · springdoc-openapi (Swagger UI) · JUnit

## Run locally
    ./mvnw clean package
    ./mvnw spring-boot:run

- Web UI: http://localhost:8080/
- API: POST http://localhost:8080/fhir/validate
- Swagger UI: http://localhost:8080/swagger-ui.html

## Roadmap
- [x] Base FHIR R4 structural validation with issue reporting
- [ ] NPHIES-specific profile validation
- [ ] More NPHIES denial rules (code/eligibility mismatch, expired coverage). Done so far: BE-1-4, BE-1-6
- [ ] Security & compliance layer: RBAC, PHI field encryption, consent checks (PHI-safe audit logging is done)
- [ ] Synthetic test data (Synthea) + automated tests
- [ ] Docker + CI