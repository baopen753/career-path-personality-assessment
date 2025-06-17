package org.swd392.apigateway.config;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
public class RouteValidator {



    public static final List<String> PUBLIC_ENDPOINTS = List.of(
            "/swd391/user/authentication/register",
            "/swd391/user/authentication/login",
            "/swd391/user/authentication/logout"
    );

    public Predicate<ServerHttpRequest> isPublic =
            request -> PUBLIC_ENDPOINTS.stream().anyMatch(uir -> request.getURI().getPath().contains(uir));

}