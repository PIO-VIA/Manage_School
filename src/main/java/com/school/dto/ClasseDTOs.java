package com.school.dto;

import com.school.entity.SalleDeClasse;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class ClasseDTOs {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ClasseCreateRequest {
        @NotBlank(message = "Le nom de la classe est obligatoire")
        private String nom;

        @NotNull(message = "Le niveau est obligatoire")
        private SalleDeClasse.Niveau niveau;

        @Min(value = 1, message = "La capacité maximale doit être d'au moins 1")
        private Integer capacityMax = 50;

        @NotNull(message = "L'ID de la section est obligatoire")
        private Long idSection;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ClasseUpdateRequest {
        private String nom;
        private SalleDeClasse.Niveau niveau;
        private Integer capacityMax;
        private Long idSection;
        private Boolean isActive;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ClasseResponse {
        private Long idClasse;
        private String nom;
        private SalleDeClasse.Niveau niveau;
        private Integer effectif;
        private Integer capacityMax;
        private Boolean isActive;
        private SectionInfo section;
        private Integer nombreEnseignants;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ClasseDetailResponse {
        private Long idClasse;
        private String nom;
        private SalleDeClasse.Niveau niveau;
        private Integer effectif;
        private Integer capacityMax;
        private Boolean isActive;
        private SectionInfo section;
        private List<EleveBasicInfo> eleves;
        private List<EnseignantBasicInfo> enseignants;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SectionInfo {
        private Long idSection;
        private String nom;
        private String typeSection;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EleveBasicInfo {
        private String matricule;
        private String nom;
        private String prenom;
        private String statut;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EnseignantBasicInfo {
        private Long idMaitre;
        private String nom;
        private String prenom;
        private String specialite;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ClasseStatsResponse {
        private Long idClasse;
        private String nom;
        private String niveau;
        private Integer effectif;
        private Integer capacityMax;
        private Double tauxOccupation;
        private Integer nombreGarcons;
        private Integer nombreFilles;
        private Double moyenneAge;
    }
}