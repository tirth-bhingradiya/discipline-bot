package com.disciplinebot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class WhatsAppBotService {

    private static final Logger log = LoggerFactory.getLogger(WhatsAppBotService.class);
    private static final String GREEN_API_URL = "https://7107.api.greenapi.com/waInstance{idInstance}/sendMessage/{apiTokenInstance}";

    @Value("${whatsapp.greenapi.instance-id}")
    private String instanceId;

    @Value("${whatsapp.greenapi.api-token}")
    private String apiToken;

    @Value("${whatsapp.target-phone}")
    private String targetPhone;

    @Value("${whatsapp.enabled:true}")
    private boolean enabled;

    private final WebClient webClient;

    public WhatsAppBotService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public boolean sendMessage(String message) {
        if (!enabled) {
            log.info("[WHATSAPP DISABLED] Would send: {}", message);
            return true;
        }

        if (instanceId == null || instanceId.isBlank() || instanceId.contains("YOUR_")) {
            log.warn("⚠️ WhatsApp Green API Instance ID not configured! Check application.properties");
            return false;
        }

        if (targetPhone == null || targetPhone.isBlank() || targetPhone.contains("YOUR_")) {
            log.warn("⚠️ WhatsApp Target Phone not configured! Check application.properties");
            return false;
        }

        try {
            // Green API requires the phone number with @c.us for personal chats
            String chatId = targetPhone + "@c.us";
            
            Map<String, String> body = Map.of(
                    "chatId", chatId,
                    "message", message
            );

            String response = webClient.post()
                    .uri(GREEN_API_URL, instanceId, apiToken)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            log.info("✅ WhatsApp message sent successfully via Green API!");
            return true;

        } catch (Exception e) {
            log.error("❌ Failed to send WhatsApp message: {}", e.getMessage());
            return false;
        }
    }

    public boolean sendHabitReminder(String habitName, String emoji, String aiMessage) {
        String message = String.format(
                "🤖 *DISCIPLINEBOT*\n" +
                "━━━━━━━━━━━━━━━━━━━\n" +
                "%s *%s* Reminder\n\n" +
                "_%s_\n\n" +
                "━━━━━━━━━━━━━━━━━━━\n" +
                "Mark done: http://localhost:8080",
                emoji, habitName, aiMessage
        );
        return sendMessage(message);
    }

    public boolean sendDailySummary(String summaryMessage) {
        return sendMessage(summaryMessage);
    }
}
