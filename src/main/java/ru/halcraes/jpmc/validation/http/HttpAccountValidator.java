package ru.halcraes.jpmc.validation.http;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.halcraes.jpmc.validation.AccountValidator;
import ru.halcraes.jpmc.validation.ValidationRequest;
import ru.halcraes.jpmc.validation.ValidationResponse;

/**
 * @author fitialovks
 */
public class HttpAccountValidator implements AccountValidator {
    private final String name;
    private final WebClient webClient;

    private HttpAccountValidator(String name, WebClient webClient) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Validator name cannot be null or blank");
        }
        this.name = name;
        this.webClient = webClient;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Mono<ValidationResponse> validate(ValidationRequest request) {
        return webClient.post()
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ValidationResponse.class);
    }

    public static HttpAccountValidator create(String name, HttpAccountValidatorProperties properties) {
        WebClient wc = WebClient.builder()
                .baseUrl(properties.getUri().toString())
                .defaultHeaders(httpHeaders -> {
                    httpHeaders.setAccept(List.of(MediaType.APPLICATION_JSON));
                })
                .build();
        return new HttpAccountValidator(name, wc);
    }
}
