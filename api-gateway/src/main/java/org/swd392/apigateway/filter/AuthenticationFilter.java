package org.swd392.apigateway.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import org.swd392.apigateway.config.RouteValidator;
import org.swd392.apigateway.dto.ApiResponse;
import org.swd392.apigateway.util.JwtUtil;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class AuthenticationFilter implements GlobalFilter, Ordered {

    private static final String BEARER = "Bearer ";

    @Autowired
    private RouteValidator routeValidator;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        // allow public endpoints
        if (routeValidator.isPublic.test(exchange.getRequest()))
            return chain.filter(exchange);

        // Get token from the authorization header
        List<String> authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION);
        if (CollectionUtils.isEmpty(authHeader))
            return unAuthenticated(exchange.getResponse());

        String token = authHeader.getFirst().replace(BEARER, "");

        if (jwtUtil.validateToken(token)) {

            ServerHttpRequest request = exchange.getRequest()
                    .mutate()
                    .header("X-User-Id", jwtUtil.extractUserId(token).toString())
                    .header("X-User-Role", jwtUtil.extractUserRole(token))
                    .build();

            return chain.filter(exchange.mutate().request(request).build());
        } else
            return unAuthenticated(exchange.getResponse());
    }


    @Override
    public int getOrder() {
        return 0;
    }

    private Mono<Void> unAuthenticated(ServerHttpResponse response) {
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .code(401)
                .message("Unauthorized")
                .build();

        String body = null;
        try {
            body = objectMapper.writeValueAsString(apiResponse);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        return response.writeWith(
                Mono.just(response.bufferFactory().wrap(body.getBytes())));
    }

}
