package com.disciplinebot.config;

import com.disciplinebot.service.HabitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    private static final Logger log = LoggerFactory.getLogger(AppConfig.class);

    private final HabitService habitService;

    public AppConfig(HabitService habitService) {
        this.habitService = habitService;
    }

    @Bean
    public CommandLineRunner initializeData() {
        return args -> {
            log.info("🚀 DisciplineBot starting up...");
            habitService.initializeDefaultHabits();
            log.info("✅ Initialization complete. Scheduler is active.");
        };
    }
}
