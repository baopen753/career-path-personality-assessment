package org.swd392.apigateway.config;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
public class RouteValidator {

    private static final String CONTEXT_PATH = "/swd391";

    public static final List<String> PUBLIC_ENDPOINTS = List.of(
            CONTEXT_PATH + "/user/authentication/register",
            CONTEXT_PATH + "/user/authentication/login",
            CONTEXT_PATH + "/user/authentication/logout"
    );

    public Predicate<ServerHttpRequest> isPublic =
            request -> PUBLIC_ENDPOINTS.stream().anyMatch(uir -> request.getURI().getPath().contains(uir));
}