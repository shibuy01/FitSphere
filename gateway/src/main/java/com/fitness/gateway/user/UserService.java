    package com.fitness.gateway.user;

    import lombok.RequiredArgsConstructor;
    import lombok.extern.slf4j.Slf4j;
    import org.springframework.http.HttpStatus;
    import org.springframework.stereotype.Service;
    import org.springframework.web.reactive.function.client.WebClient;
    import org.springframework.web.reactive.function.client.WebClientResponseException;
    import reactor.core.publisher.Mono;
    import reactor.netty.ChannelPipelineConfigurer;

    @Service
    @RequiredArgsConstructor
    @Slf4j
    public class UserService {

        private final WebClient userServiceWebClient;

        public Mono<Boolean> validateUser(String userId) {

            log.info("Validating user: {}", userId);

            return userServiceWebClient
                    .get()
                    .uri("/api/users/{userId}/validate", userId)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .doOnNext(result ->
                            log.info("User {} validation: {}", userId, result)
                    )
                    .onErrorReturn(false);
        }

        public Mono<UserResponse> registerUser(RegistorRequest request) {

            log.info("Calling User Registration Request: {}", request.getFirstName());

            return userServiceWebClient
                    .post()
                    .uri("/api/users/registor")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(UserResponse.class)
                    .doOnNext(response ->
                            log.info("User registered successfully: {}", response)
                    )
                    .onErrorResume(WebClientResponseException.class, e -> {

                        log.error(
                                "User registration failed | status: {} | response: {}",
                                e.getStatusCode(),
                                e.getResponseBodyAsString()
                        );

                        return Mono.error(e);
                    });
        }
    }