package ru.halcraes.jpmc.rest;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import ru.halcraes.jpmc.Main;
import ru.halcraes.jpmc.validation.AccountValidator;
import ru.halcraes.jpmc.validation.ValidationRequest;
import ru.halcraes.jpmc.validation.ValidationResponse;

/**
 * @author fitialovks
 */
@SpringBootTest(
        classes = {
                Main.class,
                ValidationErrorTest.AdditionalValidators.class,
        },
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class ValidationErrorTest {
    @Autowired
    private WebTestClient webTestClient;

    @Test
    void validatorErrorIs500() {
        MultiValidationRequest request = MultiValidationRequest.builder()
                .accountNumber("acc")
                .sources(List.of("failing"))
                .build();
        webTestClient.post()
                .uri("/v1/api/account/validate")
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Configuration
    public static class AdditionalValidators {
        @Bean
        public AccountValidator failingValidator() {
            return new AccountValidator() {
                @Override
                public String getName() {
                    return "failing";
                }

                @Override
                public Mono<ValidationResponse> validate(ValidationRequest request) {
                    return Mono.error(new RuntimeException());
                }
            };
        }
    }
}
