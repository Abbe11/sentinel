package dev.abbe.sentinel.config;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.support.DefaultProfileValidationSupport;
import ca.uhn.fhir.validation.FhirValidator;
import org.hl7.fhir.common.hapi.validation.support.CommonCodeSystemsTerminologyService;
import org.hl7.fhir.common.hapi.validation.support.InMemoryTerminologyServerValidationSupport;
import org.hl7.fhir.common.hapi.validation.support.ValidationSupportChain;
import org.hl7.fhir.common.hapi.validation.validator.FhirInstanceValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FhirConfig {

    // One shared R4 context for the whole app. It is thread-safe and costly
    // to build, so it is a singleton. R4 is the version NPHIES and EHDS use.
    @Bean
    public FhirContext fhirContext() {
        return FhirContext.forR4();
    }

    // Loads the R4 profiles once at startup. Also a singleton because
    // rebuilding it per request would be slow.
    @Bean
    public FhirValidator fhirValidator(FhirContext fhirContext) {
        ValidationSupportChain support = new ValidationSupportChain(
            new DefaultProfileValidationSupport(fhirContext),
            new InMemoryTerminologyServerValidationSupport(fhirContext),
            new CommonCodeSystemsTerminologyService(fhirContext)
        );
        FhirValidator validator = fhirContext.newValidator();
        validator.registerValidatorModule(new FhirInstanceValidator(support));
        return validator;
    }
}