package com.fitness.gateway;

import com.fitness.gateway.user.RegistorRequest;
import com.fitness.gateway.user.UserService;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class KeycloakUserSyncFilter implements WebFilter {

    private final UserService userService;

    @Override
    public Mono<Void> filter(
            ServerWebExchange exchange,
            WebFilterChain chain) {

        String token = exchange.getRequest()
                .getHeaders()
                .getFirst("Authorization");

        // No token -> continue request
        if (token == null || !token.startsWith("Bearer ")) {
            return chain.filter(exchange);
        }

        RegistorRequest request = getUserDetails(token);

        // JWT details could not be extracted
        if (request == null || request.getKeycloakId() == null) {
            log.warn("Unable to extract user details from Keycloak token");
            return chain.filter(exchange);
        }

        String userId = exchange.getRequest()
                .getHeaders()
                .getFirst("X-User-ID");

        // If X-User-ID is not present, use Keycloak user ID
        if (userId == null || userId.isBlank()) {
            userId = request.getKeycloakId();
        }

        String finalUserId = userId;

        return userService.validateUser(finalUserId)
                .flatMap(exists -> {

                    if (exists) {
                        log.info(
                                "User already exists: {}. Skipping sync.",
                                finalUserId
                        );

                        return Mono.empty();
                    }
                    log.info(
                            "User not found: {}. Registering user.",
                            finalUserId
                    );

                    return userService.registerUser(request)
                            .doOnSuccess(response ->
                                    log.info(
                                            "Keycloak user synced successfully: {}",
                                            finalUserId
                                    )
                            )
                            .then();
                })
                .then(Mono.defer(() -> {

                    ServerHttpRequest mutatedRequest = exchange.getRequest()
                                    .mutate()
                                    .header("X-User-ID", finalUserId)
                                    .build();

                    ServerWebExchange mutatedExchange = exchange.mutate()
                                    .request(mutatedRequest)
                                    .build();

                    return chain.filter(mutatedExchange);
                }));
    }


    private RegistorRequest getUserDetails(String token) {

        try {

            String tokenWithoutBearer = token.replaceFirst("^Bearer\\s+", "").trim();
            SignedJWT signedJWT = SignedJWT.parse(tokenWithoutBearer);
            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();

            RegistorRequest request = new RegistorRequest();

            request.setEmail(claims.getStringClaim("email"));
            request.setKeycloakId(claims.getSubject());
            request.setFirstName(claims.getStringClaim("given_name"));
            request.setLastName(claims.getStringClaim("family_name"));

            return request;

        } catch (Exception e) {
            log.error("Failed to extract user details from Keycloak JWT", e);
            return null;
        }
    }
}