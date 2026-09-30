package com.fitness.gateway.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class JsonAuthenticationConverter
        implements ServerAuthenticationConverter {

    private final ObjectMapper objectMapper;

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {

        String path = exchange.getRequest()
                .getPath()
                .value();

        // Sirf login request handle karo
        if (!"/api/users/login".equals(path)) {
            return Mono.empty();
        }

        MediaType contentType =
                exchange.getRequest()
                        .getHeaders()
                        .getContentType();

        if (contentType == null ||
                !MediaType.APPLICATION_JSON.isCompatibleWith(contentType)) {

            return Mono.error(
                    new IllegalArgumentException(
                            "Content-Type must be application/json"
                    )
            );
        }

        return DataBufferUtils.join(
                        exchange.getRequest().getBody()
                )
                .flatMap(dataBuffer -> {

                    try {

                        byte[] bytes =
                                new byte[dataBuffer.readableByteCount()];

                        dataBuffer.read(bytes);

                        UserAuthenticationRequest request =
                                objectMapper.readValue(
                                        bytes,
                                        UserAuthenticationRequest.class
                                );

                        if (request.email() == null ||
                                request.password() == null) {

                            return Mono.error(
                                    new IllegalArgumentException(
                                            "Email and password are required"
                                    )
                            );
                        }

                        Authentication authentication =
                                new UsernamePasswordAuthenticationToken(
                                        request.email(),
                                        request.password()
                                );

                        return Mono.just(authentication);

                    } catch (Exception e) {

                        return Mono.error(e);

                    } finally {

                        DataBufferUtils.release(dataBuffer);
                    }
                });
    }
}