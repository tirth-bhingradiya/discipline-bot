package com.disciplinebot.controller;

import com.disciplinebot.dto.DailySummaryDTO;
import com.disciplinebot.service.WhatsAppBotService;
import com.disciplinebot.service.HabitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class HabitController {

    private static final Logger log = LoggerFactory.getLogger(HabitController.class);

    private final HabitService habitService;
    private final WhatsAppBotService whatsAppBotService;

    public HabitController(HabitService habitService, WhatsAppBotService whatsAppBotService) {
        this.habitService = habitService;
        this.whatsAppBotService = whatsAppBotService;
    }

    @PostMapping("/habit/{id}/done")
    public ResponseEntity<Map<String, Object>> markHabitDone(@PathVariable Long id) {
        try {
            var habitLog = habitService.markHabitDone(id);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "completionCount", habitLog.getCompletionCount(),
                    "fullyCompleted", habitLog.isFullyCompleted(),
                    "streak", habitLog.getCurrentStreak()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    @GetMapping("/today")
    public ResponseEntity<?> getTodayStatus() {
        return ResponseEntity.ok(habitService.getTodayStatus());
    }

    @GetMapping("/summary")
    public ResponseEntity<DailySummaryDTO> getDailySummary() {
        return ResponseEntity.ok(habitService.buildDailySummary());
    }

    @PostMapping("/test/whatsapp")
    public ResponseEntity<Map<String, Object>> testWhatsApp(@RequestBody Map<String, String> body) {
        String message = body.getOrDefault("message", "Everything is working perfectly.");
        boolean sent = whatsAppBotService.sendMessage(message);
        return ResponseEntity.ok(Map.of("sent", sent, "message", message));
    }

    @PostMapping("/send/summary")
    public ResponseEntity<Map<String, Object>> sendSummaryNow() {
        try {
            DailySummaryDTO summary = habitService.buildDailySummary();
            String message = habitService.formatWhatsAppSummary(summary);
            boolean sent = whatsAppBotService.sendDailySummary(message);
            return ResponseEntity.ok(Map.of("sent", sent, "grade", summary.getOverallGrade()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("sent", false, "error", e.getMessage()));
        }
    }
}
