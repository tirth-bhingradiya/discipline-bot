package com.disciplinebot.repository;

import com.disciplinebot.model.Habit;
import com.disciplinebot.model.HabitLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface HabitLogRepository extends JpaRepository<HabitLog, Long> {

    Optional<HabitLog> findByHabitAndLogDate(Habit habit, LocalDate logDate);

    List<HabitLog> findByLogDate(LocalDate logDate);

    List<HabitLog> findByHabitOrderByLogDateDesc(Habit habit);

    @Query("SELECT hl FROM HabitLog hl WHERE hl.logDate BETWEEN :startDate AND :endDate ORDER BY hl.logDate DESC")
    List<HabitLog> findByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(hl) FROM HabitLog hl WHERE hl.habit = :habit AND hl.fullyCompleted = true")
    long countCompletedDays(@Param("habit") Habit habit);

    @Query("SELECT hl FROM HabitLog hl WHERE hl.logDate = :date AND hl.fullyCompleted = true")
    List<HabitLog> findCompletedForDate(@Param("date") LocalDate date);
}
