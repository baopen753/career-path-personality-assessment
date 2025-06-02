package org.swd392.apigateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator myRoutesLocator(RouteLocatorBuilder routeLocatorBuilder) {
        return routeLocatorBuilder.routes()
                .route(p -> p
                        .path("/swd391/quizzes/**")
                        .filters(f -> f.rewritePath("/swd391/quizzes/(?<segment>.*)", "/${segment}"))
                        .uri("lb://QUIZZES"))
                .route(p -> p
                        .path("/swd391/seminars/**")
                        .filters(f -> f.rewritePath("/swd391/seminars/(?<segment>.*)", "/${segment}"))
                        .uri("lb://SEMINARS"))
                .build();
    }
}
