
package com.zindarna.catalogservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers("/actuator/health", "/actuator/info").permitAll()

                        // Produits du fournisseur connecté
                        .requestMatchers(HttpMethod.GET, "/api/products/my-products")
                        .hasRole("SUPPLIER")

                        // Consultation publique
                        .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/categories/**").permitAll()

                        // Gestion des produits réservée aux fournisseurs
                        .requestMatchers(HttpMethod.POST, "/api/products/**").hasRole("SUPPLIER")
                        .requestMatchers(HttpMethod.PUT, "/api/products/**").hasRole("SUPPLIER")
                        .requestMatchers(HttpMethod.DELETE, "/api/products/**").hasRole("SUPPLIER")

                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwt ->
                                jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())
                        )
                );

        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            List<GrantedAuthority> authorities = new ArrayList<>();

            Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");

            if (realmAccess != null) {
                Object rolesObject = realmAccess.get("roles");

                if (rolesObject instanceof Collection<?> roles) {
                    for (Object role : roles) {
                        authorities.add(
                                new SimpleGrantedAuthority("ROLE_" + role)
                        );
                    }
                }
            }

            return authorities;
        });

        return converter;
    }
}
