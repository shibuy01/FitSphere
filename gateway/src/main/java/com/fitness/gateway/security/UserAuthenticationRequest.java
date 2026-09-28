package com.fitness.gateway.security;

public record UserAuthenticationRequest(
        String email,
        String password
) {
}