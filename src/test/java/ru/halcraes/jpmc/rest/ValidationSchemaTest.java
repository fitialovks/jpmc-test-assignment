package ru.halcraes.jpmc.rest;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;
import ru.halcraes.jpmc.Main;
import ru.halcraes.jpmc.validation.AccountValidator;
import ru.halcraes.jpmc.validation.ValidationRequest;
import ru.halcraes.jpmc.validation.ValidationResponse;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

/**
 * @author fitialovks
 */
@SpringBootTest(
        classes = {
                Main.class,
                ValidationSchemaTest.AdditionalValidators.class,
        },
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class ValidationSchemaTest {

    @LocalServerPort
    private int port;

    @Test
    void checkSuccessfulResponseSchema() {
        given()
                .port(port)
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .log().all()
                .body("{\"accountNumber\": \"acc\", \"sources\": [\"additional\"]}")
                .post("/v1/api/account/validate")
                .then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("ru/halcraes/jpmc/rest/MultiValidationResponseSchema.json"));
    }

    @Configuration
    public static class AdditionalValidators {
        @Bean
        public AccountValidator additionalValidator() {
            return new AccountValidator() {
                @Override
                public String getName() {
                    return "additional";
                }

                @Override
                public Mono<ValidationResponse> validate(ValidationRequest request) {
                    return Mono.just(ValidationResponse.builder().isValid(true).build());
                }
            };
        }
    }
}
