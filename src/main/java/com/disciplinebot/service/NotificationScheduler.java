package com.disciplinebot.service;

import com.disciplinebot.dto.DailySummaryDTO;
import com.disciplinebot.model.Habit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * All scheduled notification jobs for DisciplineBot.
 * Cron times are in IST (Asia/Kolkata timezone).
 */
@Service
public class NotificationScheduler {

    private static final Logger log = LoggerFactory.getLogger(NotificationScheduler.class);

    private final HabitService habitService;
    private final GeminiAIService geminiAIService;
    private final WhatsAppBotService whatsAppBotService;

    public NotificationScheduler(HabitService habitService,
                                  GeminiAIService geminiAIService,
                                  WhatsAppBotService whatsAppBotService) {
        this.habitService = habitService;
        this.geminiAIService = geminiAIService;
        this.whatsAppBotService = whatsAppBotService;
    }

    // ── WATER — 5 times daily ──────────────────────────

    @Scheduled(cron = "0 0 8 * * *", zone = "Asia/Kolkata")
    public void waterReminder8AM() {
        sendHabitReminder("Drink Water", "💧", "WATER",
                "It's 8AM. Start your day RIGHT. Drink your first 600ml NOW. No delays.");
    }

    @Scheduled(cron = "0 0 11 * * *", zone = "Asia/Kolkata")
    public void waterReminder11AM() {
        sendHabitReminder("Drink Water", "💧", "WATER", null);
    }

    @Scheduled(cron = "0 0 14 * * *", zone = "Asia/Kolkata")
    public void waterReminder2PM() {
        sendHabitReminder("Drink Water", "💧", "WATER", null);
    }

    @Scheduled(cron = "0 0 17 * * *", zone = "Asia/Kolkata")
    public void waterReminder5PM() {
        sendHabitReminder("Drink Water", "💧", "WATER", null);
    }

    @Scheduled(cron = "0 0 20 * * *", zone = "Asia/Kolkata")
    public void waterReminder8PM() {
        sendHabitReminder("Drink Water", "💧", "WATER",
                "Final water reminder. Have you hit 3 liters? Don't make excuses. DRINK NOW.");
    }

    // ── SKIN CARE — Morning & Night ───────────────────

    @Scheduled(cron = "0 0 7 * * *", zone = "Asia/Kolkata")
    public void skinCareMorning() {
        sendHabitReminder("Skin Care", "🧴", "SKINCARE",
                "MORNING ROUTINE. Your skin is your face to the world. 3 minutes. No skipping. DO IT NOW.");
    }

    @Scheduled(cron = "0 0 21 * * *", zone = "Asia/Kolkata")
    public void skinCareNight() {
        sendHabitReminder("Skin Care", "🧴", "SKINCARE",
                "NIGHT ROUTINE. Your skin repairs while you sleep — but only if you prep it. DO IT NOW.");
    }

    // ── CHANTING — Morning & Evening ─────────────────

    @Scheduled(cron = "0 0 6 * * *", zone = "Asia/Kolkata")
    public void chantingMorning() {
        sendHabitReminder("Chanting", "🙏", "CHANTING",
                "MORNING CHANTING. Before the world demands your attention, give 5 minutes to your soul. BEGIN.");
    }

    @Scheduled(cron = "0 0 19 * * *", zone = "Asia/Kolkata")
    public void chantingEvening() {
        sendHabitReminder("Chanting", "🙏", "CHANTING", null);
    }

    // ── HAIR MEDICINE — Once daily ────────────────────

    @Scheduled(cron = "0 5 8 * * *", zone = "Asia/Kolkata")
    public void hairMedicineReminder() {
        sendHabitReminder("Hair Medicine", "💊", "MEDICINE",
                "MEDICINE TIME. Take it with breakfast RIGHT NOW. One missed dose = one step backward. TAKE IT.");
    }

    // ── MORNING WAKE-UP — 5:00 AM ────────────────────

    @Scheduled(cron = "0 0 5 * * *", zone = "Asia/Kolkata")
    public void morningWakeUp() {
        log.info("🌅 Sending morning wake-up message...");
        String message =
                "⚡ *DISCIPLINEBOT — GOOD MORNING!*\n" +
                "━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                "A new day. A new chance to be better.\n\n" +
                "*YOUR MISSION TODAY:*\n" +
                "💊 Hair Medicine (8AM)\n" +
                "🙏 Chanting (6AM + 7PM)\n" +
                "🧴 Skin Care (7AM + 9PM)\n" +
                "💧 Water 3L (8,11,2,5,8)\n\n" +
                "_No excuses. Just execution._\n" +
                "━━━━━━━━━━━━━━━━━━━━━━━━";
        whatsAppBotService.sendMessage(message);
    }

    // ── DAILY SUMMARY — 9:30 PM ───────────────────────

    @Scheduled(cron = "0 30 21 * * *", zone = "Asia/Kolkata")
    public void dailySummaryReport() {
        log.info("📊 Generating daily summary report...");
        try {
            DailySummaryDTO summary = habitService.buildDailySummary();
            String message = habitService.formatWhatsAppSummary(summary);
            whatsAppBotService.sendDailySummary(message);
            log.info("✅ Daily summary sent! Grade: {}", summary.getOverallGrade());
        } catch (Exception e) {
            log.error("❌ Failed to send daily summary: {}", e.getMessage());
        }
    }

    // ── HELPER ────────────────────────────────────────

    private void sendHabitReminder(String habitName, String emoji, String category, String overrideMessage) {
        log.info("⏰ Triggering reminder: {} {}", emoji, habitName);
        try {
            List<Habit> habits = habitService.getAllActiveHabits();
            int streak = habits.stream()
                    .filter(h -> category.equals(h.getCategory()))
                    .findFirst()
                    .map(h -> habitService.getCurrentStreak(h.getId()))
                    .orElse(0);

            String aiMessage = (overrideMessage != null)
                    ? overrideMessage
                    : geminiAIService.generateHabitReminder(category, habitName, streak);

            boolean sent = whatsAppBotService.sendHabitReminder(habitName, emoji, aiMessage);
            String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));

            if (sent) {
                log.info("✅ [{}] {} {} reminder sent via WhatsApp", time, emoji, habitName);
            } else {
                log.warn("⚠️ [{}] Failed to send {} reminder", time, habitName);
            }
        } catch (Exception e) {
            log.error("❌ Error in reminder for {}: {}", habitName, e.getMessage());
        }
    }
}
