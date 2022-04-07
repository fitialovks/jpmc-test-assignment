package ru.halcraes.jpmc.validation;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author fitialovks
 */
public class AccountValidatorContainer {
    private final Map<String, AccountValidator> validators;

    public AccountValidatorContainer(Collection<AccountValidator> validators) {
        this.validators = validators.stream()
                .collect(Collectors.toUnmodifiableMap(AccountValidator::getName, av -> av));
    }

    public AccountValidator get(String name) {
        AccountValidator validator = validators.get(name);
        if (validator == null) {
            throw new UnknownValidatorException(name);
        }
        return validator;
    }

    public Collection<String> getSourceNames() {
        return validators.keySet();
    }
}
