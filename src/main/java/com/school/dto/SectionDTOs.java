package com.school.dto;

import com.school.entity.Section;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class SectionDTOs {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SectionCreateRequest {
        @NotBlank(message = "Le nom de la section est obligatoire")
        private String nom;

        private String description;

        @NotNull(message = "Le type de section est obligatoire")
        private Section.TypeSection typeSection;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SectionUpdateRequest {
        private String nom;
        private String description;
        private Section.TypeSection typeSection;
        private Boolean isActive;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SectionResponse {
        private Long idSection;
        private String nom;
        private String description;
        private Section.TypeSection typeSection;
        private Boolean isActive;
        private Integer nombreClasses;
        private Integer nombreEnseignants;
        private Integer nombreEleves;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SectionDetailResponse {
        private Long idSection;
        private String nom;
        private String description;
        private Section.TypeSection typeSection;
        private Boolean isActive;
        private List<SalleClasseBasicResponse> sallesDeClasse;
        private List<EnseignantBasicResponse> enseignants;
        private List<EleveBasicResponse> eleves;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SalleClasseBasicResponse {
        private Long idClasse;
        private String nom;
        private String niveau;
        private Integer effectif;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EnseignantBasicResponse {
        private Long idMaitre;
        private String nom;
        private String prenom;
        private String specialite;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EleveBasicResponse {
        private String matricule;
        private String nom;
        private String prenom;
        private String statut;
    }
}