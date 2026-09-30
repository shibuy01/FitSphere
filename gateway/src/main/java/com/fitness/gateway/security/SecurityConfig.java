package com.fitness.gateway.security;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;


@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public AuthenticationWebFilter authenticationWebFilter(
            GatewayAuthenticationManager authenticationManager,
            JsonAuthenticationConverter authenticationConverter) {

        AuthenticationWebFilter filter =
                new AuthenticationWebFilter(authenticationManager);

        // JSON se email + password read karega
        filter.setServerAuthenticationConverter(authenticationConverter);

        // Successful authentication ko session mein save karega
        filter.setSecurityContextRepository(
                new WebSessionServerSecurityContextRepository()
        );

        // ==============================
        // LOGIN SUCCESS
        // ==============================
        filter.setAuthenticationSuccessHandler(
                (webFilterExchange, authentication) -> {

                    var response =
                            webFilterExchange
                                    .getExchange()
                                    .getResponse();

                    response.setStatusCode(HttpStatus.OK);

                    return response.setComplete();
                }
        );

        // ==============================
        // LOGIN FAILURE
        // ==============================
        filter.setAuthenticationFailureHandler(
                (webFilterExchange, exception) -> {

                    var response =
                            webFilterExchange
                                    .getExchange()
                                    .getResponse();

                    response.setStatusCode(HttpStatus.UNAUTHORIZED);

                    return response.setComplete();
                }
        );

        return filter;
    }


    @Bean
    public SecurityWebFilterChain securityWebFilterChain(
            ServerHttpSecurity http,
            AuthenticationWebFilter authenticationWebFilter) {

        return http

                // ==============================
                // CORS
                // ==============================
                .cors(cors -> {
                })

                // ==============================
                // CSRF
                // ==============================
                .csrf(ServerHttpSecurity.CsrfSpec::disable)

                // ==============================
                // CUSTOM JSON AUTHENTICATION
                // ==============================
                .addFilterAt(
                        authenticationWebFilter,
                        SecurityWebFiltersOrder.AUTHENTICATION
                )

                // ==============================
                // AUTHORIZATION
                // ==============================
                .authorizeExchange(exchange -> exchange

                        // PUBLIC ENDPOINTS
                        .pathMatchers(
                                "/api/users/login",
                                "/api/users/registor"
                        ).permitAll()

                        // PROTECTED ENDPOINTS
                        .anyExchange().authenticated()
                )

                .build();
    }


    // ==============================
    // CORS CONFIGURATION
    // ==============================
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        // React frontend
        configuration.setAllowedOrigins(
                List.of("http://localhost:5173")
        );

        // Allowed HTTP methods
        configuration.setAllowedMethods(
                List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "DELETE",
                        "OPTIONS"
                )
        );

        // Allow all headers
        configuration.setAllowedHeaders(
                List.of("*")
        );

        // Session / Cookie allow
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                configuration
        );

        return source;
    }
}
