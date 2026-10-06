package com.disciplinebot.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class GeminiAIService {

    private static final Logger log = LoggerFactory.getLogger(GeminiAIService.class);
    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent";

    @Value("${gemini.api.key:}")
    private String apiKey;

    @Value("${gemini.enabled:true}")
    private boolean enabled;

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final Random random = new Random();

    // Fallback messages if API is unavailable
    private static final Map<String, List<String>> FALLBACK_MESSAGES = Map.of(
        "WATER", List.of(
            "Your body is 60% water and you're treating it like a desert. DRINK NOW. No excuses.",
            "600ml of water. RIGHT NOW. Your cells are screaming for hydration and you're ignoring them.",
            "Every high performer hydrates religiously. Are you a performer or an excuse machine? DRINK.",
            "Dehydration kills focus, energy and discipline. Stop everything and drink your water NOW.",
            "Your brain needs water to function. Right now it's running on fumes. HYDRATE IMMEDIATELY."
        ),
        "SKINCARE", List.of(
            "Your skin is your armor. 3 minutes of care now saves years of regret. DO IT NOW.",
            "Champions take care of their physical vessel. Skincare is not optional - it's discipline. GO.",
            "Skip skincare once, skip it twice, skip it forever. Don't let that be you. DO IT.",
            "Your future self will thank you or curse you based on what you do RIGHT NOW. Choose wisely.",
            "Discipline is doing it even when you don't feel like it. SKINCARE. NOW. No debates."
        ),
        "CHANTING", List.of(
            "5 minutes of mental clarity through chanting can transform your entire day. START NOW.",
            "Your mind is cluttered with noise. Chanting cuts through it all. BEGIN IMMEDIATELY.",
            "Every great leader has a morning ritual. This is yours. Don't break the chain. CHANT NOW.",
            "Spiritual discipline IS discipline. Skip this and you skip building your inner fortress. GO.",
            "The most successful people protect their mental practices fiercely. Yours is chanting. NOW."
        ),
        "MEDICINE", List.of(
            "Skipping your hair medicine for even one day undoes weeks of progress. TAKE IT NOW.",
            "Consistency is the only secret. You can't skip medicine and expect results. TAKE IT.",
            "Your hair won't fix itself. The medicine will. But only if you TAKE IT. Do it right now.",
            "Missing doses is the fastest way to fail your own goals. Medicine. NOW. No skipping.",
            "Discipline means doing the small things consistently. Your medicine is non-negotiable. TAKE IT."
        )
    );

    public GeminiAIService(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.webClient = webClientBuilder.build();
        this.objectMapper = objectMapper;
    }

    /**
     * Generates a strict, motivating reminder message for a specific habit
     */
    public String generateHabitReminder(String habitCategory, String habitName, int streakDays) {
        if (!enabled || apiKey == null || apiKey.isBlank() || apiKey.equals("YOUR_GEMINI_API_KEY")) {
            log.warn("Gemini API not configured, using fallback message");
            return getFallbackMessage(habitCategory);
        }

        try {
            String prompt = buildHabitPrompt(habitName, habitCategory, streakDays);
            return callGeminiAPI(prompt);
        } catch (Exception e) {
            log.error("Gemini API error: {}. Using fallback.", e.getMessage());
            return getFallbackMessage(habitCategory);
        }
    }

    /**
     * Generates a strict evening summary message
     */
    public String generateDailySummaryMessage(int completed, int total, double percentage, String grade) {
        if (!enabled || apiKey == null || apiKey.isBlank() || apiKey.equals("YOUR_GEMINI_API_KEY")) {
            return generateFallbackSummary(completed, total, percentage, grade);
        }

        try {
            String prompt = String.format(
                "You are a strict, no-nonsense personal coach sending an end-of-day performance review. " +
                "The person completed %d out of %d daily habits (%s%%). Their grade is %s. " +
                "Write a SHORT (3-4 lines max), direct, uncompromising assessment. " +
                "If grade is S or A: intense praise that pushes them to maintain it. " +
                "If grade is B or C: stern disappointment with specific urgency. " +
                "If grade is F: harsh reality check - no sugarcoating. " +
                "Use NO emojis in the message itself. Keep it sharp and personal. Max 80 words.",
                completed, total, String.format("%.0f", percentage), grade
            );
            return callGeminiAPI(prompt);
        } catch (Exception e) {
            return generateFallbackSummary(completed, total, percentage, grade);
        }
    }

    private String buildHabitPrompt(String habitName, String category, int streak) {
        return String.format(
            "You are a strict, intense personal discipline coach. Generate a SHORT (2-3 sentences max, 40 words max) " +
            "motivational reminder for this habit: '%s' (category: %s). " +
            "Current streak: %d days. " +
            "If streak > 7: acknowledge the streak briefly then push harder. " +
            "If streak < 3: be extra harsh about building consistency. " +
            "Be direct, intense, zero tolerance for excuses. No soft language. " +
            "Do NOT use emojis. End with an action word in CAPS.",
            habitName, category, streak
        );
    }

    private String callGeminiAPI(String prompt) {
        try {
            ObjectNode requestBody = objectMapper.createObjectNode();
            ArrayNode contents = requestBody.putArray("contents");
            ObjectNode content = contents.addObject();
            ArrayNode parts = content.putArray("parts");
            parts.addObject().put("text", prompt);

            // Safety settings - relax to allow strict language
            ArrayNode safetySettings = requestBody.putArray("safetySettings");
            for (String category : List.of("HARM_CATEGORY_HARASSMENT", "HARM_CATEGORY_HATE_SPEECH")) {
                ObjectNode setting = safetySettings.addObject();
                setting.put("category", category);
                setting.put("threshold", "BLOCK_ONLY_HIGH");
            }

            // Generation config
            ObjectNode genConfig = requestBody.putObject("generationConfig");
            genConfig.put("maxOutputTokens", 100);
            genConfig.put("temperature", 0.8);

            String url = GEMINI_API_URL + "?key=" + apiKey;

            String response = webClient.post()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode responseJson = objectMapper.readTree(response);
            return responseJson
                    .path("candidates").get(0)
                    .path("content")
                    .path("parts").get(0)
                    .path("text").asText("Stay disciplined and focused!");

        } catch (Exception e) {
            log.error("Error calling Gemini API: {}", e.getMessage());
            throw new RuntimeException("Gemini API call failed", e);
        }
    }

    private String getFallbackMessage(String category) {
        List<String> messages = FALLBACK_MESSAGES.getOrDefault(category,
                List.of("This is non-negotiable. Do it NOW. No excuses accepted."));
        return messages.get(random.nextInt(messages.size()));
    }

    private String generateFallbackSummary(int completed, int total, double percentage, String grade) {
        if (grade.equals("S") || grade.equals("A")) {
            return String.format("EXCEPTIONAL. %d/%d habits completed. You're building an unstoppable machine. " +
                    "Don't you dare get comfortable — tomorrow we go even harder. This is who you are becoming.", completed, total);
        } else if (grade.equals("B")) {
            return String.format("%d/%d habits done. Not bad. But 'not bad' doesn't build greatness. " +
                    "You left habits on the table today. Tomorrow, no mercy. Every single one gets done.", completed, total);
        } else if (grade.equals("C")) {
            return String.format("Only %d/%d habits? Mediocrity is a choice you made today. " +
                    "Your future self is watching and judging. Tomorrow, you either rise or you accept failure. Choose.", completed, total);
        } else {
            return String.format("%d/%d habits. This is unacceptable. You promised yourself better. " +
                    "The habits you skip today become the regrets you carry tomorrow. Tomorrow is your redemption. START.", completed, total);
        }
    }
}
