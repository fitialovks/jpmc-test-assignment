package ru.halcraes.jpmc.validation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;
import ru.halcraes.jpmc.Main;

/**
 * @author fitialovks
 */
@SpringBootTest(classes = {
        Main.class,
        ValidatorConfigurationTest.AdditionalValidator.class
})
// Cannot use @TestPropertySource with YAML :-(
@ActiveProfiles("junit")
public class ValidatorConfigurationTest {
    @Autowired
    private AccountValidatorContainer container;

    @Test
    void checkValidatorsFromProperties() {
        // added in junit profile
        container.get("test-1");
        container.get("test-2");
        // bar is in production profile
        Assertions.assertThrows(IllegalArgumentException.class, () -> container.get("bar"));
    }

    @Test
    void checkValidatorFromContext() {
        container.get("additional");
    }

    @Configuration
    public static class AdditionalValidator {
        @Bean
        public AccountValidator additionalValidator() {
            return new AccountValidator() {
                @Override
                public String getName() {
                    return "additional";
                }

                @Override
                public Mono<ValidationResponse> validate(ValidationRequest request) {
                    return Mono.just(ValidationResponse.builder().isValid(false).build());
                }
            };
        }
    }
}
