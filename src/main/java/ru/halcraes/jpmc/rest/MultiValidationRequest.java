package ru.halcraes.jpmc.rest;

import java.util.List;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

/**
 * @author fitialovks
 */
@Value
@Builder
@Jacksonized
public class MultiValidationRequest {
    String accountNumber;
    List<String> sources;
}
