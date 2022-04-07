package ru.halcraes.jpmc.validation;

import reactor.core.publisher.Mono;

/**
 * @author fitialovks
 */
public interface AccountValidator {
    String getName();

    Mono<ValidationResponse> validate(ValidationRequest request);
}
