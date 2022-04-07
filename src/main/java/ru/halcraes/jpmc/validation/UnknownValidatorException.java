package ru.halcraes.jpmc.validation;

/**
 * @author fitialovks
 */
public class UnknownValidatorException extends IllegalArgumentException {
    private final String validatorName;

    public UnknownValidatorException(String validatorName) {
        super(String.format("Validation source '%s' does not exist", validatorName));
        this.validatorName = validatorName;
    }

    public String getValidatorName() {
        return validatorName;
    }
}
