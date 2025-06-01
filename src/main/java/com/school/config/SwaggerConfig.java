package com.school.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Système de Gestion d'École Primaire")
                        .description("""
                    Cette API permet de gérer tous les aspects d'une école primaire:
                    - Gestion des sections (Francophone, Anglophone, Bilingue)
                    - Gestion des classes et des élèves
                    - Gestion du personnel (Administratif, Enseignants, Entretien)
                    - Suivi des notes et évaluations
                    - Gestion des absences et de la discipline
                    - Gestion des inscriptions et paiements
                    - Gestion du matériel scolaire
                    
                    **Authentification:** Utilisez le token JWT obtenu via `/auth/login` dans l'en-tête Authorization avec le préfixe 'Bearer '.
                    
                    **Rôles:**
                    - ADMIN: Accès à la plupart des fonctionnalités
                    - SUPER_ADMIN: Accès complet incluant la gestion du personnel et suppressions
                    """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Équipe de Développement")
                                .email("piodjiele@gmail.com")
                                .url("https://schoolmanagement.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort + "/api")
                                .description("Serveur de développement local"),
                        new Server()
                                .url("https://api.schoolmanagement.com")
                                .description("Serveur de production")
                ))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .name("bearerAuth")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Entrez le token JWT obtenu depuis /auth/login")
                        )
                );
    }
}