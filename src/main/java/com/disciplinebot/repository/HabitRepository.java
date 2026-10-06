package com.disciplinebot.repository;

import com.disciplinebot.model.Habit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HabitRepository extends JpaRepository<Habit, Long> {
    List<Habit> findByActiveTrue();
    List<Habit> findByCategory(String category);
}
