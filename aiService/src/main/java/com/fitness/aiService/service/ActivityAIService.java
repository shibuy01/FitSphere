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
    private final ObjectMapper objectMapper;

    public Recommendation generateRecommendation(Activity activity) {
        String prompt = createPromptForActivity(activity);
        String aiResponse = geminiService.getRecommendation(prompt);
        log.info("Response received from Gemini AI");
        return processAiResponse(activity, aiResponse);
    }

    private Recommendation processAiResponse(
            Activity activity,
            String aiResponse) {

        try {

            JsonNode rootNode = objectMapper.readTree(aiResponse);

            JsonNode textNode = rootNode
                    .path("candidates")
                    .path(0)
                    .path("content")
                    .path("parts")
                    .path(0)
                    .path("text");

            // Check Gemini response text
            if (textNode.isMissingNode()
                    || textNode.isNull()
                    || textNode.asText().isBlank()) {

                log.error("Could not find Gemini response text");

                // Temporary debugging - Gemini response dekhne ke liye
                log.error("Gemini response: {}", aiResponse);

                return createDefaultRecommendation(activity);
            }

//          Extract actual AI generated JSON
            String jsonContent = textNode.asText()
                    .replace("```json", "")
                    .replace("```JSON", "")
                    .replace("```", "")
                    .trim();

            log.info("Parsing Gemini recommendation JSON");


//          Parse AI generated JSON
            JsonNode analysisJson =
                    objectMapper.readTree(jsonContent);


//          Extract analysis
            JsonNode analysisNode =
                    analysisJson.path("analysis");

            StringBuilder fullAnalysis =
                    new StringBuilder();

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


//          Extract improvements
            List<String> improvements =
                    extractImprovements(
                            analysisJson.path("improvements")
                    );


//          Extract suggestion
            List<String> suggestions =
                    extractSuggestions(
                            analysisJson.path("suggestions")
                    );


//          safety guidelines
            List<String> safety =
                    extractSafetyGuidelines(
                            analysisJson.path("safety")
                    );


//          Create recommendation
            return Recommendation.builder()
                    .activityId(activity.getId())
                    .userId(activity.getUserId())
                    .recommendation(
                            fullAnalysis.toString().trim()
                    )
                    .improvements(improvements)
                    .suggestions(suggestions)
                    .safety(safety)
                    .createdAt(LocalDateTime.now())
                    .build();

        } catch (Exception e) {

            log.error(
                    "Error while processing AI response for activity {}",
                    activity.getId(),
                    e
            );

            return createDefaultRecommendation(activity);
        }
    }


//  Default recommendation
    private Recommendation createDefaultRecommendation(
            Activity activity) {

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


//  Extract safety guidelines
    private List<String> extractSafetyGuidelines(
            JsonNode safetyNode) {

        List<String> safety =
                new ArrayList<>();

        if (safetyNode.isArray()) {
            safetyNode.forEach(item -> {
                if (!item.isNull() && !item.asText().isBlank()) {
                    safety.add(item.asText());
                }
            });
        }

        if (safety.isEmpty()) {
            return Collections.singletonList(
                    "Follow general safety guidelines"
            );
        }

        return safety;
    }


//  workout suggestions
    private List<String> extractSuggestions(
            JsonNode suggestionsNode) {

        List<String> suggestions =
                new ArrayList<>();

        if (suggestionsNode.isArray()) {
            suggestionsNode.forEach(suggestion -> {
                String workout =
                        suggestion
                                .path("workout")
                                .asText("");

                String description =
                        suggestion
                                .path("description")
                                .asText("");

                if (!workout.isBlank()
                        || !description.isBlank()) {
                    String result;
                    if (!workout.isBlank() && !description.isBlank()) {
                        result = workout + ": " + description;
                    } else if (!workout.isBlank()) {
                        result = workout;
                    } else {
                        result = description;
                    }
                    suggestions.add(result);
                }
            });
        }

        if (suggestions.isEmpty()) {
            return Collections.singletonList(
                    "No specific suggestions provided"
            );
        }
        return suggestions;
    }


//  Extract improvements
    private List<String> extractImprovements(
            JsonNode improvementsNode) {

        List<String> improvements =
                new ArrayList<>();

        if (improvementsNode.isArray()) {

            improvementsNode.forEach(improvement -> {

                String area =
                        improvement
                                .path("area")
                                .asText("");

                String detail =
                        improvement
                                .path("recommendation")
                                .asText("");

                if (!area.isBlank()
                        || !detail.isBlank()) {
                    String result;
                    if (!area.isBlank()
                            && !detail.isBlank()) {
                        result = area + ": " + detail;
                    } else if (!area.isBlank()) {
                        result = area;
                    } else {
                        result = detail;
                    }
                    improvements.add(result);
                }
            });
        }

        if (improvements.isEmpty()) {

            return Collections.singletonList(
                    "No specific improvements provided"
            );
        }

        return improvements;
    }

//   Append analysis sections
    private void addAnalysisSection(
            StringBuilder fullAnalysis,
            JsonNode analysisNode,
            String key,
            String prefix) {

        JsonNode value =
                analysisNode.path(key);

        if (!value.isMissingNode()
                && !value.isNull()
                && !value.asText().isBlank()) {

            fullAnalysis
                    .append(prefix)
                    .append(" ")
                    .append(value.asText())
                    .append("\n\n");
        }
    }

//    Gemini prompt
    private String createPromptForActivity(
            Activity activity) {

        return String.format("""

                Analyze this fitness activity and provide detailed
                recommendations in the following EXACT JSON format.

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

                Focus on:
                - Overall performance
                - Pace
                - Heart rate
                - Calories burned
                - Areas for improvement
                - Next workout suggestions
                - Safety guidelines

                Return ONLY valid JSON.
                Do not use Markdown.
                Do not wrap the response in code fences.

                """,
                activity.getType(),
                activity.getDuration(),
                activity.getCaloriesBurned(),
                activity.getAdditionalMetrics()
        );
    }
}