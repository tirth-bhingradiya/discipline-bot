package com.disciplinebot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DisciplineBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(DisciplineBotApplication.class, args);
        System.out.println("\n╔════════════════════════════════════════════════╗");
        System.out.println("║        🤖 DisciplineBot is ACTIVE!              ║");
        System.out.println("║   Your Strict AI Coach is watching you...       ║");
        System.out.println("║   Dashboard: http://localhost:8080              ║");
        System.out.println("╚════════════════════════════════════════════════╝\n");
    }
}
