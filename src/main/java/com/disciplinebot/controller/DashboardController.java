package com.disciplinebot.controller;

import com.disciplinebot.dto.DailySummaryDTO;
import com.disciplinebot.model.Habit;
import com.disciplinebot.model.HabitLog;
import com.disciplinebot.service.HabitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
public class DashboardController {

    private static final Logger log = LoggerFactory.getLogger(DashboardController.class);

    private final HabitService habitService;

    public DashboardController(HabitService habitService) {
        this.habitService = habitService;
    }

    @GetMapping("/")
    public String dashboard(Model model) {
        List<Habit> habits = habitService.getAllActiveHabits();
        List<DailySummaryDTO.HabitStatus> todayStatus = habitService.getTodayStatus();
        List<HabitLog> recentLogs = habitService.getRecentLogs(7);

        long completed = todayStatus.stream().filter(DailySummaryDTO.HabitStatus::isCompleted).count();
        long partial = todayStatus.stream().filter(s -> "PARTIAL".equals(s.getStatus())).count();
        long missed = todayStatus.stream().filter(s -> "MISSED".equals(s.getStatus())).count();
        double completionPct = habits.size() > 0 ? (completed * 100.0 / habits.size()) : 0;

        String grade;
        if (completionPct >= 100) grade = "S";
        else if (completionPct >= 85) grade = "A";
        else if (completionPct >= 70) grade = "B";
        else if (completionPct >= 50) grade = "C";
        else grade = "F";

        model.addAttribute("habits", habits);
        model.addAttribute("todayStatus", todayStatus);
        model.addAttribute("recentLogs", recentLogs);
        model.addAttribute("completedCount", completed);
        model.addAttribute("partialCount", partial);
        model.addAttribute("missedCount", missed);
        model.addAttribute("completionPct", String.format("%.0f", completionPct));
        model.addAttribute("grade", grade);
        model.addAttribute("today", LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM dd yyyy")));
        model.addAttribute("totalHabits", habits.size());

        return "dashboard";
    }

    @PostMapping("/habit/done")
    public String markDone(@RequestParam Long habitId, RedirectAttributes redirectAttributes) {
        try {
            HabitLog habitLog = habitService.markHabitDone(habitId);
            Habit habit = habitLog.getHabit();
            redirectAttributes.addFlashAttribute("successMessage",
                    habit.getEmoji() + " " + habit.getName() + " marked complete! " +
                    (habitLog.getCurrentStreak() > 1 ? "🔥 " + habitLog.getCurrentStreak() + " day streak!" : "Keep it up!"));
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
        }
        return "redirect:/";
    }

    @GetMapping("/summary")
    public String summary(Model model) {
        DailySummaryDTO summary = habitService.buildDailySummary();
        model.addAttribute("summary", summary);
        return "summary";
    }
}
