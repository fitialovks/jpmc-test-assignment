package ru.halcraes.jpmc.validation;

import java.util.Map;

import lombok.Data;
import ru.halcraes.jpmc.validation.http.HttpAccountValidatorProperties;

/**
 * Container for settings of different types of validators.
 *
 * @author fitialovks
 */
@Data
public class AccountValidatorProperties {
    private Map<String, HttpAccountValidatorProperties> http;
}
