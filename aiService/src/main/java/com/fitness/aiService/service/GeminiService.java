package com.fitness.aiService.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class GeminiService {

    private final WebClient webClient;

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    public GeminiService(WebClient.Builder builder) {

        this.webClient = builder
                .defaultHeader("Accept-Encoding", "identity")
                .build();
    }

    public String getRecommendation(String details) {

        System.out.println("Gemini URL = " + geminiApiUrl);
        System.out.println("Gemini API Key Present = "
                + (geminiApiKey != null && !geminiApiKey.isBlank()));

        System.out.println("Gemini API Key Length = "
                + (geminiApiKey == null ? 0 : geminiApiKey.length()));

        String requestBody = """
                {
                  "contents": [
                    {
                      "parts": [
                        {
                          "text": "%s"
                        }
                      ]
                    }
                  ]
                }
                """.formatted(
                details
                        .replace("\\", "\\\\")
                        .replace("\"", "\\\"")
                        .replace("\n", "\\n")
                        .replace("\r", "\\r")
        );

        return webClient.post()
                .uri(geminiApiUrl)
                .header("x-goog-api-key", geminiApiKey)
                .header("Accept-Encoding", "identity")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(
                        status -> status.isError(),
                        response -> response.bodyToMono(String.class)
                                .map(body -> new RuntimeException(
                                        "Gemini API Error: "
                                                + response.statusCode()
                                                + " | "
                                                + body
                                ))
                )
                .bodyToMono(String.class)
                .block();
    }
}