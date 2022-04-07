package ru.halcraes.jpmc.validation;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * This is just an example that can be used when running locally
 * with no working remote validators.
 *
 * @author fitialovks
 */
@Component
public class SampleAccountValidator implements AccountValidator {
    @Override
    public String getName() {
        return "sample";
    }

    @Override
    public Mono<ValidationResponse> validate(ValidationRequest request) {
        return Mono.just(ValidationResponse.builder().isValid(true).build());
    }
}
