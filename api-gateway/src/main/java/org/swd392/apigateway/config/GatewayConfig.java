package org.swd392.apigateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator myRoutesLocator(RouteLocatorBuilder routeLocatorBuilder) {
        return routeLocatorBuilder.routes()
                .route(p -> p
                        .path("/swd391/career/**")
                        .filters(f -> f.rewritePath("/swd391/career/(?<segment>.*)", "/career/${segment}")
                                .addResponseHeader("X-Response-Time", LocalDateTime.now().toString()))
                        .uri("lb://CAREER"))
                .route(p -> p
                        .path("/swd391/quiz/**")
                        .filters(f -> f.rewritePath("/swd391/quiz/(?<segment>.*)", "/quiz/${segment}")
                                .addResponseHeader("X-Response-Time", LocalDateTime.now().toString()))
                        .uri("lb://quiz"))
                .route(p -> p
                        .path("/swd391/university/**")
                        .filters(f -> f.rewritePath("/swd391/university/(?<segment>.*)", "/university/${segment}")
                                .addResponseHeader("X-Response-Time", LocalDateTime.now().toString()))
                        .uri("lb://university"))
                .route(p -> p
                        .path("/swd391/user/**")
                        .filters(f -> f.rewritePath("/swd391/user/(?<segment>.*)", "/user/${segment}")
                                .addResponseHeader("X-Response-Time", LocalDateTime.now().toString()))
                        .uri("lb://user"))
                .build();
    }
}