package org.swd392.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity // Enable debug mode for detailed security logs)
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity serverHttpSecurity) {
        serverHttpSecurity.authorizeExchange(exchange -> exchange

                        .pathMatchers(HttpMethod.GET).permitAll()
                        .pathMatchers("/swd391/career/**").authenticated()
                        .pathMatchers("/swd391/quiz/**").authenticated()
                        .pathMatchers("/swd391/university/**").hasRole("ADMIN")
                        .pathMatchers("/swd391/user/**").authenticated())

                .oauth2ResourceServer(oAuth2ResourceServerSpec -> oAuth2ResourceServerSpec
        //                .jwt(Customizer.withDefaults()));  // use default Customizer when use don't have role converter
                .jwt(jwtSpec -> jwtSpec.jwtAuthenticationConverter(jwtAuthenticationConverter())));    // use customized role converter

        serverHttpSecurity.csrf(csrfSpec -> csrfSpec.disable());

        return  serverHttpSecurity.build();
    }

    private Converter<Jwt, Mono<AbstractAuthenticationToken>> jwtAuthenticationConverter() {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new KeyCloakRoleConverter());
        return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);
    }
}