package ru.halcraes.jpmc.rest;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

/**
 * @author fitialovks
 */
@Value
@Builder
@Jacksonized
public class MultiValidationResponse {

    List<SourceResponse> result;

    @Value
    @Builder
    @Jacksonized
    public static class SourceResponse {
        String source;

        boolean isValid;

        @JsonProperty("isValid")
        public boolean isValid() {
            return isValid;
        }
    }
}
