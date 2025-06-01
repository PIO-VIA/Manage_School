package com.school.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

public class MatiereDTOs {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MatiereCreateRequest {
        @NotBlank(message = "Le nom de la matière est obligatoire")
        private String nom;

        @NotNull(message = "Le coefficient est obligatoire")
        @Min(value = 1, message = "Le coefficient doit être d'au moins 1")
        private Integer coefficient;

        private String description;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MatiereUpdateRequest {
        private String nom;
        private Integer coefficient;
        private String description;
        private Boolean isActive;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MatiereResponse {
        private Long idMatiere;
        private String nom;
        private Integer coefficient;
        private String description;
        private Boolean isActive;
        private Integer nombreEnseignants;
        private Integer nombreEleves;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MatiereDetailResponse {
        private Long idMatiere;
        private String nom;
        private Integer coefficient;
        private String description;
        private Boolean isActive;
        private List<EnseignantMatiereInfo> enseignants;
        private List<NoteStatistique> statistiquesNotes;
        private Double moyenneGenerale;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EnseignantMatiereInfo {
        private Long idMaitre;
        private String nom;
        private String prenom;
        private String specialite;
        private LocalDate dateOccupation;
        private String section;
        private String classe;
        private Boolean isActive;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class NoteStatistique {
        private Integer sequence;
        private Double moyenneSequence;
        private Integer nombreEleves;
        private Double noteMin;
        private Double noteMax;
        private Integer nombreReussites; // Note >= 10
        private Double tauxReussite;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MatiereStatsResponse {
        private Long idMatiere;
        private String nom;
        private Integer coefficient;
        private Integer nombreEnseignants;
        private Integer nombreElevesTotal;
        private Double moyenneGenerale;
        private Double tauxReussiteGlobal;
        private Integer nombreNotesTotal;
        private List<StatistiqueParSequence> statistiquesParSequence;
        private List<StatistiqueParSection> statistiquesParSection;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StatistiqueParSequence {
        private Integer sequence;
        private Integer nombreEleves;
        private Double moyenne;
        private Double noteMin;
        private Double noteMax;
        private Double tauxReussite;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StatistiqueParSection {
        private String section;
        private Integer nombreEleves;
        private Double moyenne;
        private Double tauxReussite;
        private Integer nombreEnseignants;
    }
}