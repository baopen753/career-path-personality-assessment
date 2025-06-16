package org.swd392.apigateway.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
public class KeyCloakRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    /**
     * This method is used to convert roles from a Keycloak JWT token into a collection of GrantedAuthority objects which Spring Security can use for authorization.
     *
     * @param source the source object to convert, which must be an instance of {@code S} (never {@code null})
     * @return the converted object, which must be an instance of {@code T} (potentially {@code null})
     * @throws IllegalArgumentException if the source cannot be converted to the desired target type
     */
    @Override
    public Collection<GrantedAuthority> convert(Jwt source) {
       Map<String, Object> realmAccess = (Map<String, Object>) source.getClaims().get("realm_access");

        if (realmAccess == null || realmAccess.isEmpty()) return Collections.emptyList();

        Collection<GrantedAuthority> authorities = ((List<String>) realmAccess.get("roles"))
                .stream()
                .map(role -> "ROLE_" + role)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        return authorities;
    }
}