package com.disciplinebot.model;

import jakarta.persistence.*;

@Entity
@Table(name = "habits")
public class Habit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String emoji;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private boolean active = true;

    private int targetCountPerDay;

    @Column(columnDefinition = "TEXT")
    private String cronSchedules;

    @Column(columnDefinition = "TEXT")
    private String reminderTimes;

    public Habit() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmoji() { return emoji; }
    public void setEmoji(String emoji) { this.emoji = emoji; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public int getTargetCountPerDay() { return targetCountPerDay; }
    public void setTargetCountPerDay(int targetCountPerDay) { this.targetCountPerDay = targetCountPerDay; }
    public String getCronSchedules() { return cronSchedules; }
    public void setCronSchedules(String cronSchedules) { this.cronSchedules = cronSchedules; }
    public String getReminderTimes() { return reminderTimes; }
    public void setReminderTimes(String reminderTimes) { this.reminderTimes = reminderTimes; }
}
