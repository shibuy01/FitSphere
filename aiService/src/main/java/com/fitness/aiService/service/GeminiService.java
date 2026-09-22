package com.fitness.aiService.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class GeminiService {

    private final WebClient webClient;

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Value("${spring.ai.google.genai.chat.model}")
    private String geminiModel;

    public GeminiService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public String getRecommendation(String details) {

        Map<String, Object> requestBody = Map.of(
                "model", geminiModel,
                "input", details
        );

        return webClient.post()
                .uri(geminiApiUrl)
                .header("x-goog-api-key", geminiApiKey)
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()

                // 429 ko clearly identify karo
                .onStatus(
                        status -> status.value() == 429,
                        response -> response.bodyToMono(String.class)
                                .map(body -> new RuntimeException(
                                        "Gemini API rate limit exceeded. Response: " + body
                                ))
                )

                // Baaki HTTP errors
                .onStatus(
                        HttpStatusCode::isError,
                        response -> response.bodyToMono(String.class)
                                .map(body -> new RuntimeException(
                                        "Gemini API error: "
                                                + response.statusCode()
                                                + " Response: "
                                                + body
                                ))
                )

                .bodyToMono(String.class)
                .block();
    }
}