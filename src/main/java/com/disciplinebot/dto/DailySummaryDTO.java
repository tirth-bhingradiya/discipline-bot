package com.disciplinebot.dto;

import java.util.List;

public class DailySummaryDTO {

    private String date;
    private int totalHabits;
    private int completedHabits;
    private int missedHabits;
    private double completionPercentage;
    private List<HabitStatus> habitStatuses;
    private String aiMotivationalMessage;
    private String overallGrade;

    public DailySummaryDTO() {}

    // Builder pattern (manual)
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final DailySummaryDTO dto = new DailySummaryDTO();
        public Builder date(String v) { dto.date = v; return this; }
        public Builder totalHabits(int v) { dto.totalHabits = v; return this; }
        public Builder completedHabits(int v) { dto.completedHabits = v; return this; }
        public Builder missedHabits(int v) { dto.missedHabits = v; return this; }
        public Builder completionPercentage(double v) { dto.completionPercentage = v; return this; }
        public Builder habitStatuses(List<HabitStatus> v) { dto.habitStatuses = v; return this; }
        public Builder aiMotivationalMessage(String v) { dto.aiMotivationalMessage = v; return this; }
        public Builder overallGrade(String v) { dto.overallGrade = v; return this; }
        public DailySummaryDTO build() { return dto; }
    }

    // Getters & Setters
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public int getTotalHabits() { return totalHabits; }
    public void setTotalHabits(int totalHabits) { this.totalHabits = totalHabits; }
    public int getCompletedHabits() { return completedHabits; }
    public void setCompletedHabits(int completedHabits) { this.completedHabits = completedHabits; }
    public int getMissedHabits() { return missedHabits; }
    public void setMissedHabits(int missedHabits) { this.missedHabits = missedHabits; }
    public double getCompletionPercentage() { return completionPercentage; }
    public void setCompletionPercentage(double completionPercentage) { this.completionPercentage = completionPercentage; }
    public List<HabitStatus> getHabitStatuses() { return habitStatuses; }
    public void setHabitStatuses(List<HabitStatus> habitStatuses) { this.habitStatuses = habitStatuses; }
    public String getAiMotivationalMessage() { return aiMotivationalMessage; }
    public void setAiMotivationalMessage(String aiMotivationalMessage) { this.aiMotivationalMessage = aiMotivationalMessage; }
    public String getOverallGrade() { return overallGrade; }
    public void setOverallGrade(String overallGrade) { this.overallGrade = overallGrade; }

    // ── Inner class: HabitStatus ──
    public static class HabitStatus {
        private String habitName;
        private String emoji;
        private boolean completed;
        private int completionCount;
        private int targetCount;
        private int streak;
        private String status;

        public HabitStatus() {}

        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private final HabitStatus hs = new HabitStatus();
            public Builder habitName(String v) { hs.habitName = v; return this; }
            public Builder emoji(String v) { hs.emoji = v; return this; }
            public Builder completed(boolean v) { hs.completed = v; return this; }
            public Builder completionCount(int v) { hs.completionCount = v; return this; }
            public Builder targetCount(int v) { hs.targetCount = v; return this; }
            public Builder streak(int v) { hs.streak = v; return this; }
            public Builder status(String v) { hs.status = v; return this; }
            public HabitStatus build() { return hs; }
        }

        public String getHabitName() { return habitName; }
        public void setHabitName(String habitName) { this.habitName = habitName; }
        public String getEmoji() { return emoji; }
        public void setEmoji(String emoji) { this.emoji = emoji; }
        public boolean isCompleted() { return completed; }
        public void setCompleted(boolean completed) { this.completed = completed; }
        public int getCompletionCount() { return completionCount; }
        public void setCompletionCount(int completionCount) { this.completionCount = completionCount; }
        public int getTargetCount() { return targetCount; }
        public void setTargetCount(int targetCount) { this.targetCount = targetCount; }
        public int getStreak() { return streak; }
        public void setStreak(int streak) { this.streak = streak; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
}
