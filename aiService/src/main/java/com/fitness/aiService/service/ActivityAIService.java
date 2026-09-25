package com.fitness.aiService.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitness.aiService.models.Activity;
import com.fitness.aiService.models.Recommendation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityAIService {

    private final GeminiService geminiService;

    public Recommendation generateRecommendation(Activity activity) {

        String prompt = createPromptForActivity(activity);

        String aiResponse = geminiService.getRecommendation(prompt);

        log.info("RESPONSE FROM AI: {}", aiResponse);

        return processAiResponse(activity, aiResponse);
    }


    private Recommendation processAiResponse(
            Activity activity,
            String aiResponse
    ) {

        try {

            ObjectMapper mapper = new ObjectMapper();

            JsonNode rootNode = mapper.readTree(aiResponse);

            // Gemini response se text extract karo
            JsonNode textNode = rootNode
                    .path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text");

            String jsonContent = textNode.asText()
                    .replace("```json", "")
                    .replace("```", "")
                    .trim();

            log.info("PARSED RESPONSE FROM AI: {}", jsonContent);


            // AI ke actual JSON ko parse karo
            JsonNode analysisJson = mapper.readTree(jsonContent);

            JsonNode analysisNode = analysisJson.path("analysis");


            // ==========================
            // ANALYSIS
            // ==========================

            StringBuilder fullAnalysis = new StringBuilder();

            addAnalysisSection(
                    fullAnalysis,
                    analysisNode,
                    "overall",
                    "Overall:"
            );

            addAnalysisSection(
                    fullAnalysis,
                    analysisNode,
                    "pace",
                    "Pace:"
            );

            addAnalysisSection(
                    fullAnalysis,
                    analysisNode,
                    "heartRate",
                    "Heart Rate:"
            );

            addAnalysisSection(
                    fullAnalysis,
                    analysisNode,
                    "caloriesBurned",
                    "Calories:"
            );


            // ==========================
            // IMPROVEMENTS
            // ==========================

            List<String> improvements =
                    extractImprovements(
                            analysisJson.path("improvements")
                    );


            // ==========================
            // SUGGESTIONS
            // ==========================

            List<String> suggestions =
                    extractSuggestions(
                            analysisJson.path("suggestions")
                    );


            // ==========================
            // SAFETY
            // ==========================

            List<String> safety =
                    extractSafetyGuidelines(
                            analysisJson.path("safety")
                    );


            // ==========================
            // CREATE RECOMMENDATION
            // ==========================

            return Recommendation.builder()
                    .activityId(activity.getId())
                    .userId(activity.getUserId())
                    .recommendation(fullAnalysis.toString().trim())
                    .improvements(improvements)
                    .suggestions(suggestions)
                    .safety(safety)
                    .createdAt(LocalDateTime.now())
                    .build();

        } catch (Exception e) {

            log.error("Error while processing AI response", e);

            return createDefaultRecommendation(activity);
        }
    }


    // ==========================
    // DEFAULT RECOMMENDATION
    // ==========================

    private Recommendation createDefaultRecommendation(
            Activity activity
    ) {

        return Recommendation.builder()
                .activityId(activity.getId())
                .userId(activity.getUserId())
                .recommendation(
                        "Unable to generate detailed analysis"
                )
                .improvements(
                        Collections.singletonList(
                                "Continue with your current routine"
                        )
                )
                .suggestions(
                        Collections.singletonList(
                                "Consider consulting a fitness professional"
                        )
                )
                .safety(
                        Arrays.asList(
                                "Always warm up before exercise",
                                "Stay hydrated",
                                "Listen to your body"
                        )
                )
                .createdAt(LocalDateTime.now())
                .build();
    }


    // ==========================
    // SAFETY
    // ==========================

    private List<String> extractSafetyGuidelines(
            JsonNode safetyNode
    ) {

        List<String> safety = new ArrayList<>();

        if (safetyNode.isArray()) {

            safetyNode.forEach(item ->
                    safety.add(item.asText())
            );
        }

        if (safety.isEmpty()) {

            return Collections.singletonList(
                    "Follow general safety guidelines"
            );
        }

        return safety;
    }


    // ==========================
    // SUGGESTIONS
    // ==========================

    private List<String> extractSuggestions(
            JsonNode suggestionsNode
    ) {

        List<String> suggestions = new ArrayList<>();

        if (suggestionsNode.isArray()) {

            suggestionsNode.forEach(suggestion -> {

                String workout =
                        suggestion.path("workout").asText();

                String description =
                        suggestion.path("description").asText();

                suggestions.add(
                        String.format(
                                "%s: %s",
                                workout,
                                description
                        )
                );
            });
        }

        if (suggestions.isEmpty()) {

            return Collections.singletonList(
                    "No specific suggestions provided"
            );
        }

        return suggestions;
    }


    // ==========================
    // IMPROVEMENTS
    // ==========================

    private List<String> extractImprovements(
            JsonNode improvementsNode
    ) {

        List<String> improvements = new ArrayList<>();

        if (improvementsNode.isArray()) {

            improvementsNode.forEach(improvement -> {

                String area =
                        improvement.path("area").asText();

                String detail =
                        improvement
                                .path("recommendation")
                                .asText();

                improvements.add(
                        String.format(
                                "%s: %s",
                                area,
                                detail
                        )
                );
            });
        }

        if (improvements.isEmpty()) {

            return Collections.singletonList(
                    "No specific improvements provided"
            );
        }

        return improvements;
    }


    // ==========================
    // ANALYSIS SECTION
    // ==========================

    private void addAnalysisSection(
            StringBuilder fullAnalysis,
            JsonNode analysisNode,
            String key,
            String prefix
    ) {

        if (!analysisNode.path(key).isMissingNode()) {

            fullAnalysis
                    .append(prefix)
                    .append(" ")
                    .append(
                            analysisNode
                                    .path(key)
                                    .asText()
                    )
                    .append("\n\n");
        }
    }


    // ==========================
    // PROMPT
    // ==========================

    private String createPromptForActivity(
            Activity activity
    ) {

        return String.format("""
                
                Analyze this fitness activity and provide detailed recommendations in the following EXACT JSON format:

                {
                  "analysis": {
                    "overall": "Overall analysis here",
                    "pace": "Pace analysis here",
                    "heartRate": "Heart rate analysis here",
                    "caloriesBurned": "Calories analysis here"
                  },
                  "improvements": [
                    {
                      "area": "Area name",
                      "recommendation": "Detailed recommendation"
                    }
                  ],
                  "suggestions": [
                    {
                      "workout": "Workout name",
                      "description": "Detailed workout description"
                    }
                  ],
                  "safety": [
                    "Safety point 1",
                    "Safety point 2"
                  ]
                }

                Analyze this activity:

                Activity Type: %s
                Duration: %d minutes
                Calories Burned: %d
                Additional Metrics: %s

                Provide detailed analysis focusing on:

                - Overall performance
                - Pace
                - Heart rate
                - Calories burned
                - Areas for improvement
                - Next workout suggestions
                - Safety guidelines

                Return ONLY valid JSON.
                Do not use markdown.
                Do not wrap the response in ```json.
                
                """,
                activity.getType(),
                activity.getDuration(),
                activity.getCaloriesBurned(),
                activity.getAdditionalMetrics()
        );
    }
}