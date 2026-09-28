package com.fitness.gateway.security;


import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GatewayAuthenticationManager
        implements ReactiveAuthenticationManager {

    private final WebClient.Builder webClientBuilder;

    @Override
    public Mono<Authentication> authenticate(
            Authentication authentication) {

        String email = authentication.getName();

        String password =
                authentication.getCredentials().toString();

        UserAuthenticationRequest request =
                new UserAuthenticationRequest(
                        email,
                        password
                );

        return webClientBuilder.build()
                .post()
                .uri("http://USER-SERVICE/api/users/authenticate")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(UserAuthenticationResponse.class)

                .flatMap(response -> {

                    if (!response.authenticated()) {

                        return Mono.error(
                                new BadCredentialsException(
                                        "Invalid email or password"
                                )
                        );
                    }

                    String role = response.role();

                    if (role == null || role.isBlank()) {
                        role = "USER";
                    }

                    Authentication authenticatedUser =
                            new UsernamePasswordAuthenticationToken(
                                    response.email(),
                                    null,
                                    List.of(
                                            new SimpleGrantedAuthority(
                                                    "ROLE_" + role
                                            )
                                    )
                            );

                    return Mono.just(authenticatedUser);
                });
    }
}