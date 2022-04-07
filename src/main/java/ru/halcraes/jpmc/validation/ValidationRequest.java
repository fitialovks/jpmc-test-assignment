package ru.halcraes.jpmc.validation;


import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

/**
 * @author fitialovks
 */
@Value
@Builder
@Jacksonized
public class ValidationRequest {
    String accountNumber;
}
