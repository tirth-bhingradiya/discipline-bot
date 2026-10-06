package com.disciplinebot.service;

import com.disciplinebot.dto.DailySummaryDTO;
import com.disciplinebot.model.Habit;
import com.disciplinebot.model.HabitLog;
import com.disciplinebot.repository.HabitLogRepository;
import com.disciplinebot.repository.HabitRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class HabitService {

    private static final Logger log = LoggerFactory.getLogger(HabitService.class);

    private final HabitRepository habitRepository;
    private final HabitLogRepository habitLogRepository;
    private final GeminiAIService geminiAIService;

    public HabitService(HabitRepository habitRepository,
                        HabitLogRepository habitLogRepository,
                        GeminiAIService geminiAIService) {
        this.habitRepository = habitRepository;
        this.habitLogRepository = habitLogRepository;
        this.geminiAIService = geminiAIService;
    }

    @Transactional
    public void initializeDefaultHabits() {
        if (habitRepository.count() == 0) {
            log.info("🌱 Initializing default habits...");

            List<Habit> defaults = List.of(
                createHabit("Drink Water", "💧", "Drink 600ml of water", "WATER", 5, "8AM, 11AM, 2PM, 5PM, 8PM"),
                createHabit("Skin Care", "🧴", "Morning + Night skin care routine", "SKINCARE", 2, "7AM, 9PM"),
                createHabit("Chanting", "🙏", "Daily chanting/meditation practice", "CHANTING", 2, "6AM, 7PM"),
                createHabit("Hair Medicine", "💊", "Take hair medicine after breakfast", "MEDICINE", 1, "8AM")
            );

            habitRepository.saveAll(defaults);
            log.info("✅ {} default habits created!", defaults.size());
        }
    }

    private Habit createHabit(String name, String emoji, String desc, String category,
                               int targetCount, String times) {
        Habit habit = new Habit();
        habit.setName(name);
        habit.setEmoji(emoji);
        habit.setDescription(desc);
        habit.setCategory(category);
        habit.setTargetCountPerDay(targetCount);
        habit.setReminderTimes(times);
        habit.setActive(true);
        return habit;
    }

    @Transactional
    public HabitLog markHabitDone(Long habitId) {
        Habit habit = habitRepository.findById(habitId)
                .orElseThrow(() -> new RuntimeException("Habit not found: " + habitId));

        LocalDate today = LocalDate.now();
        Optional<HabitLog> existingLog = habitLogRepository.findByHabitAndLogDate(habit, today);

        HabitLog habitLog;
        if (existingLog.isPresent()) {
            habitLog = existingLog.get();
            habitLog.setCompletionCount(habitLog.getCompletionCount() + 1);
        } else {
            habitLog = new HabitLog();
            habitLog.setHabit(habit);
            habitLog.setLogDate(today);
            habitLog.setCompletedAt(LocalDateTime.now());
            habitLog.setCompletionCount(1);
        }

        boolean fullyDone = habitLog.getCompletionCount() >= habit.getTargetCountPerDay();
        habitLog.setFullyCompleted(fullyDone);

        int streak = calculateStreak(habit);
        habitLog.setCurrentStreak(streak);

        return habitLogRepository.save(habitLog);
    }

    public List<DailySummaryDTO.HabitStatus> getTodayStatus() {
        List<Habit> habits = habitRepository.findByActiveTrue();
        LocalDate today = LocalDate.now();
        List<DailySummaryDTO.HabitStatus> statuses = new ArrayList<>();

        for (Habit habit : habits) {
            Optional<HabitLog> logOpt = habitLogRepository.findByHabitAndLogDate(habit, today);
            int completionCount = logOpt.map(HabitLog::getCompletionCount).orElse(0);
            boolean fullyDone = logOpt.map(HabitLog::isFullyCompleted).orElse(false);
            int streak = logOpt.map(HabitLog::getCurrentStreak).orElse(0);

            String status;
            if (fullyDone) status = "DONE";
            else if (completionCount > 0) status = "PARTIAL";
            else status = "MISSED";

            statuses.add(DailySummaryDTO.HabitStatus.builder()
                    .habitName(habit.getName())
                    .emoji(habit.getEmoji())
                    .completed(fullyDone)
                    .completionCount(completionCount)
                    .targetCount(habit.getTargetCountPerDay())
                    .streak(streak)
                    .status(status)
                    .build());
        }

        return statuses;
    }

    public DailySummaryDTO buildDailySummary() {
        List<DailySummaryDTO.HabitStatus> statuses = getTodayStatus();
        long completed = statuses.stream().filter(DailySummaryDTO.HabitStatus::isCompleted).count();
        int total = statuses.size();
        double percentage = total > 0 ? (completed * 100.0 / total) : 0;

        String grade = calculateGrade(percentage);
        String aiMessage = geminiAIService.generateDailySummaryMessage(
                (int) completed, total, percentage, grade);

        return DailySummaryDTO.builder()
                .date(LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM dd yyyy")))
                .totalHabits(total)
                .completedHabits((int) completed)
                .missedHabits((int) (total - completed))
                .completionPercentage(percentage)
                .habitStatuses(statuses)
                .aiMotivationalMessage(aiMessage)
                .overallGrade(grade)
                .build();
    }

    public String formatWhatsAppSummary(DailySummaryDTO summary) {
        StringBuilder sb = new StringBuilder();
        sb.append("🤖 *DISCIPLINEBOT — DAILY REPORT*\n");
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━\n");
        sb.append("📅 ").append(summary.getDate()).append("\n\n");

        sb.append("*📊 PERFORMANCE GRADE: ").append(summary.getOverallGrade()).append("*\n");
        sb.append(String.format("Completed: %d/%d habits (%.0f%%)\n\n",
                summary.getCompletedHabits(), summary.getTotalHabits(), summary.getCompletionPercentage()));

        sb.append("*HABIT BREAKDOWN:*\n");
        for (DailySummaryDTO.HabitStatus status : summary.getHabitStatuses()) {
            String mark = "DONE".equals(status.getStatus()) ? "✅" :
                         "PARTIAL".equals(status.getStatus()) ? "⚠️" : "❌";
            sb.append(String.format("%s %s %s — %d/%d",
                    mark, status.getEmoji(), status.getHabitName(),
                    status.getCompletionCount(), status.getTargetCount()));
            if (status.getStreak() > 1) {
                sb.append(" 🔥").append(status.getStreak()).append("d streak");
            }
            sb.append("\n");
        }

        sb.append("\n*COACH SAYS:*\n");
        sb.append("_").append(summary.getAiMotivationalMessage()).append("_\n");
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━");
        return sb.toString();
    }

    private String calculateGrade(double percentage) {
        if (percentage >= 100) return "S";
        if (percentage >= 85) return "A";
        if (percentage >= 70) return "B";
        if (percentage >= 50) return "C";
        return "F";
    }

    private int calculateStreak(Habit habit) {
        List<HabitLog> logs = habitLogRepository.findByHabitOrderByLogDateDesc(habit);
        int streak = 0;
        LocalDate checkDate = LocalDate.now();

        for (HabitLog entry : logs) {
            if (entry.isFullyCompleted() && entry.getLogDate().equals(checkDate)) {
                streak++;
                checkDate = checkDate.minusDays(1);
            } else {
                break;
            }
        }
        return streak;
    }

    public List<Habit> getAllActiveHabits() {
        return habitRepository.findByActiveTrue();
    }

    public List<HabitLog> getRecentLogs(int days) {
        LocalDate from = LocalDate.now().minusDays(days);
        return habitLogRepository.findByDateRange(from, LocalDate.now());
    }

    public Habit getHabitById(Long id) {
        return habitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Habit not found"));
    }

    public int getCurrentStreak(Long habitId) {
        Habit habit = getHabitById(habitId);
        return calculateStreak(habit);
    }
}
