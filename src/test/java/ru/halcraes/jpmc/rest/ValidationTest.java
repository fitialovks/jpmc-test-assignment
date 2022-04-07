package ru.halcraes.jpmc.rest;

import java.util.List;

import org.junit.jupiter.api.Assertions;
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
                ValidationTest.AdditionalValidators.class,
        },
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class ValidationTest {
    @Autowired
    private WebTestClient webTestClient;

    @Test
    void emptyRequestIsAllSourcesAnd200() {
        MultiValidationRequest request = MultiValidationRequest.builder()
                .accountNumber("acc")
                .sources(List.of())
                .build();
        webTestClient.post()
                .uri("/v1/api/account/validate")
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.OK)
                .expectBody(MultiValidationResponse.class)
                .value(b -> {
                    Assertions.assertTrue(b.getResult().size() > 0);
                });
    }

    @Test
    void successfulValidationIs200() {
        MultiValidationResponse expected = MultiValidationResponse.builder()
                .result(List.of(
                        MultiValidationResponse.SourceResponse.builder()
                                .source("anything")
                                .isValid(true)
                                .build(),
                        MultiValidationResponse.SourceResponse.builder()
                                .source("nothing")
                                .isValid(false)
                                .build()
                ))
                .build();
        MultiValidationRequest request = MultiValidationRequest.builder()
                .accountNumber("acc")
                .sources(List.of("nothing", "anything"))
                .build();
        webTestClient.post()
                .uri("/v1/api/account/validate")
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.OK)
                .expectBody(MultiValidationResponse.class).isEqualTo(expected);
    }

    @Test
    void unknownValidatorIs400() {
        MultiValidationRequest request = MultiValidationRequest.builder()
                .accountNumber("acc")
                .sources(List.of("unknown"))
                .build();
        webTestClient.post()
                .uri("/v1/api/account/validate")
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Configuration
    public static class AdditionalValidators {
        @Bean
        public AccountValidator anythingValidator() {
            return new AccountValidator() {
                @Override
                public String getName() {
                    return "anything";
                }

                @Override
                public Mono<ValidationResponse> validate(ValidationRequest request) {
                    return Mono.just(ValidationResponse.builder().isValid(true).build());
                }
            };
        }

        @Bean
        public AccountValidator nothingValidator() {
            return new AccountValidator() {
                @Override
                public String getName() {
                    return "nothing";
                }

                @Override
                public Mono<ValidationResponse> validate(ValidationRequest request) {
                    return Mono.just(ValidationResponse.builder().isValid(false).build());
                }
            };
        }
    }
}
