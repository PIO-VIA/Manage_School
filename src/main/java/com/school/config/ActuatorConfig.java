package com.school.config;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDateTime;
import java.util.Map;

@Configuration
public class ActuatorConfig {

    @Bean
    public HealthIndicator databaseHealthIndicator(JdbcTemplate jdbcTemplate) {
        return () -> {
            try {
                // Test de connectivité à la base de données
                jdbcTemplate.queryForObject("SELECT 1", Integer.class);

                // Vérifier quelques tables importantes
                Integer sectionsCount = jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM sections WHERE is_active = true", Integer.class);
                Integer elevesCount = jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM eleves WHERE is_active = true", Integer.class);
                Integer enseignantsCount = jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM enseignants WHERE is_active = true", Integer.class);

                return Health.up()
                        .withDetail("database", "PostgreSQL")
                        .withDetail("sections_actives", sectionsCount)
                        .withDetail("eleves_actifs", elevesCount)
                        .withDetail("enseignants_actifs", enseignantsCount)
                        .withDetail("status", "Connexion réussie")
                        .build();

            } catch (Exception e) {
                return Health.down()
                        .withDetail("database", "PostgreSQL")
                        .withDetail("error", e.getMessage())
                        .withDetail("status", "Connexion échouée")
                        .build();
            }
        };
    }

    @Bean
    public InfoContributor schoolInfoContributor() {
        return builder -> builder
                .withDetail("application", Map.of(
                        "name", "Système de Gestion d'École Primaire",
                        "version", "1.0.0",
                        "description", "API complète pour la gestion d'une école primaire",
                        "author", "École Management Team"
                ))
                .withDetail("features", Map.of(
                        "sections", "Gestion des sections (Francophone, Anglophone, Bilingue)",
                        "eleves", "Gestion complète des élèves et inscriptions",
                        "enseignants", "Gestion du personnel enseignant",
                        "notes", "Système d'évaluation et bulletins",
                        "absences", "Suivi des absences et retards",
                        "discipline", "Gestion des dossiers disciplinaires",
                        "materiels", "Inventaire du matériel scolaire",
                        "finance", "Suivi des paiements et inscriptions"
                ))
                .withDetail("environment", Map.of(
                        "java_version", System.getProperty("java.version"),
                        "spring_boot", "3.2.0",
                        "database", "PostgreSQL",
                        "security", "JWT Authentication",
                        "documentation", "OpenAPI 3 (Swagger)"
                ))
                .withDetail("statistics", Map.of(
                        "startup_time", LocalDateTime.now().toString(),
                        "timezone", java.time.ZoneId.systemDefault().toString()
                ));
    }

    @Bean
    public HealthIndicator applicationHealthIndicator() {
        return () -> {
            try {
                // Vérifications de santé de l'application
                long totalMemory = Runtime.getRuntime().totalMemory();
                long freeMemory = Runtime.getRuntime().freeMemory();
                long usedMemory = totalMemory - freeMemory;

                double memoryUsagePercent = (double) usedMemory / totalMemory * 100;

                Health.Builder healthBuilder = Health.up()
                        .withDetail("memory", Map.of(
                                "total", totalMemory + " bytes",
                                "used", usedMemory + " bytes",
                                "free", freeMemory + " bytes",
                                "usage_percent", String.format("%.2f%%", memoryUsagePercent)
                        ))
                        .withDetail("processors", Runtime.getRuntime().availableProcessors())
                        .withDetail("uptime", java.lang.management.ManagementFactory.getRuntimeMXBean().getUptime() + " ms");

                // Alerte si utilisation mémoire > 80%
                if (memoryUsagePercent > 80) {
                    healthBuilder = Health.down()
                            .withDetail("warning", "Utilisation mémoire élevée: " + String.format("%.2f%%", memoryUsagePercent));
                }

                return healthBuilder.build();

            } catch (Exception e) {
                return Health.down()
                        .withDetail("error", "Erreur lors de la vérification: " + e.getMessage())
                        .build();
            }
        };
    }
}