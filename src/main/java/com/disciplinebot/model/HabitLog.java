package com.disciplinebot.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "habit_logs")
public class HabitLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "habit_id", nullable = false)
    private Habit habit;

    @Column(nullable = false)
    private LocalDate logDate;

    @Column(nullable = false)
    private LocalDateTime completedAt;

    private int completionCount;

    private boolean fullyCompleted;

    @Column(columnDefinition = "TEXT")
    private String notes;

    private int currentStreak;
    private int bestStreak;

    public HabitLog() {}
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Habit getHabit() { return habit; }

    public void setHabit(Habit habit) { this.habit = habit; }
    public LocalDate getLogDate() { return logDate; }
    public void setLogDate(LocalDate logDate) { this.logDate = logDate; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
    public int getCompletionCount() { return completionCount; }
    public void setCompletionCount(int completionCount) { this.completionCount = completionCount; }
    public boolean isFullyCompleted() { return fullyCompleted; }
    public void setFullyCompleted(boolean fullyCompleted) { this.fullyCompleted = fullyCompleted; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public int getCurrentStreak() { return currentStreak; }
    public void setCurrentStreak(int currentStreak) { this.currentStreak = currentStreak; }
    public int getBestStreak() { return bestStreak; }
    public void setBestStreak(int bestStreak) { this.bestStreak = bestStreak; }
}
