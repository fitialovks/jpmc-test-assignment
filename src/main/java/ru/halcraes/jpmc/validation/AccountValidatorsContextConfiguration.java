package ru.halcraes.jpmc.validation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.halcraes.jpmc.validation.http.HttpAccountValidator;

/**
 * @author fitialovks
 */
@Configuration
public class AccountValidatorsContextConfiguration {
    @Bean
    @ConfigurationProperties(prefix = "validators")
    public AccountValidatorProperties accountValidatorProperties() {
        AccountValidatorProperties properties = new AccountValidatorProperties();
        properties.setHttp(new HashMap<>());
        return properties;
    }

    /**
     * Creates validators from configuration, finds validators in the {@code ApplicationContext}
     * and merges them into an {@link AccountValidatorContainer}. Duplicate names are not allowed.
     */
    @Bean
    public AccountValidatorContainer accountValidatorContainer(
            AccountValidatorProperties properties,
            @Autowired(required = false) List<AccountValidator> beans
    ) {
        Map<String, AccountValidator> validators = new HashMap<>();

        properties.getHttp().forEach((name, config) -> {
            if (validators.putIfAbsent(name, HttpAccountValidator.create(name, config)) != null) {
                throwDuplicate(name);
            }
        });

        if (beans != null) {
            beans.forEach(validator -> {
                if (validators.putIfAbsent(validator.getName(), validator) != null) {
                    throwDuplicate(validator.getName());
                }
            });
        }

        return new AccountValidatorContainer(validators.values());
    }

    private static void throwDuplicate(String name) {
        throw new IllegalStateException(String.format("Validator '%s' has a duplicate", name));
    }
}
