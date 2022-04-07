package ru.halcraes.jpmc.rest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicate;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import ru.halcraes.jpmc.validation.AccountValidatorContainer;

/**
 * @author fitialovks
 */
@Configuration
public class MultiValidatorContextConfiguration {
    @Bean
    public MultiValidator multiValidator(
            AccountValidatorContainer validators
    ) {
        return new MultiValidator(validators);
    }

    @Bean
    public RouterFunction<ServerResponse> multiValidatorRouter(
            MultiValidator validator
    ) {
        RequestPredicate predicate = RequestPredicates.POST("/v1/api/account/validate")
                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON));
        return RouterFunctions.route(predicate, validator::validate);
    }
}
