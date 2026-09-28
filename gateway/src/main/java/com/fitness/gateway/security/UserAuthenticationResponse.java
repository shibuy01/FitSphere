package com.fitness.gateway.security;

public record UserAuthenticationResponse(
        boolean authenticated,
        String id,
        String email,
        String role
) {
}