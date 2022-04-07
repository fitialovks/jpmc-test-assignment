package ru.halcraes.jpmc.rest;

import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

import lombok.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import ru.halcraes.jpmc.validation.AccountValidator;
import ru.halcraes.jpmc.validation.AccountValidatorContainer;
import ru.halcraes.jpmc.validation.UnknownValidatorException;
import ru.halcraes.jpmc.validation.ValidationRequest;

/**
 * @author fitialovks
 */
public class MultiValidator {
    private final AccountValidatorContainer validatorContainer;

    public MultiValidator(AccountValidatorContainer validatorContainer) {
        this.validatorContainer = validatorContainer;
    }

    public Mono<ServerResponse> validate(ServerRequest request) {
        return request.bodyToMono(MultiValidationRequest.class)
                .flatMapIterable(r -> getSources(r).stream()
                        .map(source -> new AccountContext(r.getAccountNumber(), source))
                        .collect(Collectors.toList()))
                .parallel()
                .flatMap(accountContext -> {
                    AccountValidator validator = validatorContainer.get(accountContext.getValidatorName());
                    ValidationRequest validationRequest = ValidationRequest.builder()
                            .accountNumber(accountContext.getAccountNumber())
                            .build();
                    return validator.validate(validationRequest)
                            .map(validationResponse -> new MultiValidationResponse.SourceResponse(
                                    accountContext.getValidatorName(),
                                    validationResponse.isValid()
                            ));
                })
                .collectSortedList(Comparator.comparing(MultiValidationResponse.SourceResponse::getSource))
                .map(MultiValidationResponse::new)
                .onErrorResume(UnknownValidatorException.class, exception ->
                        Mono.error(new ResponseStatusException(
                                HttpStatus.BAD_REQUEST,
                                exception.getMessage(),
                                exception)))
                .flatMap(value -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(value)
                );
    }

    private Collection<String> getSources(MultiValidationRequest request) {
        if (request.getSources() != null && !request.getSources().isEmpty()) {
            return request.getSources();
        }
        return validatorContainer.getSourceNames();
    }

    @Value
    private static class AccountContext {
        String accountNumber;
        String validatorName;
    }
}
