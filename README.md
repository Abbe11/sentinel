# Sentinel

A FHIR R4 claim service that will catch malformed and non-conformant health-insurance claims **before** they reach a payer.

Backend / security engineering project targeting the FHIR-based claims platforms now mandated in the Gulf (Saudi **NPHIES**) and the EU (**European Health Data Space**). Work in progress.

## What it does today
- Accepts a FHIR R4 `Claim` as JSON over a REST API
- Parses it with HAPI FHIR and rejects anything that is not a valid FHIR resource
- Ships a lightweight web UI and Swagger UI to submit claims and inspect responses

> The thesis: "parsed" is not "valid". A claim can be well-formed JSON and still be rejected by a payer. Sentinel is being built to surface that gap.

## Tech stack
Java 21 · Spring Boot 3 · HAPI FHIR (R4) · Maven (wrapper included) · springdoc-openapi (Swagger UI)

## Run locally
    ./mvnw clean package
    ./mvnw spring-boot:run

- Web UI: http://localhost:8080/
- API: POST http://localhost:8080/fhir/validate
- Swagger UI: http://localhost:8080/swagger-ui.html

## Roadmap
- [ ] Full FHIR R4 structural / profile validation with issue reporting
- [ ] NPHIES-specific denial rules (missing prior auth, code/eligibility mismatch, expired coverage)
- [ ] Security & compliance layer (audit logging, RBAC, PHI field encryption, consent checks)
- [ ] Synthetic test data (Synthea) + automated tests
- [ ] Docker + CI