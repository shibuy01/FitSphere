package com.fitness.aiService.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GeminiService {

    private final WebClient webClient;

    private String geminiApiUrl;
    private String geminiApiKey;

    public GeminiService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public String getRecommendation(String details) {
        Map<String, Object> requestBody = Map.of(
                "contents", new Object[]{
                        Map.of("parts" ,new Object[]{
                                Map.of("text", details)
                        })
                }
        );
        return re
    }

}
