package com.school.dto;

import com.school.entity.DossierExamen;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

public class DossierExamenDTOs {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DossierExamenCreateRequest {
        @NotBlank(message = "Le nom de l'examen est obligatoire")
        private String examen;

        @NotNull(message = "L'état est obligatoire")
        private DossierExamen.EtatDossier etat;

        @NotNull(message = "La date de dépôt est obligatoire")
        private LocalDate dateDepot;

        private String observations;

        @NotBlank(message = "Le matricule de l'élève est obligatoire")
        private String matricule;

        @NotNull(message = "L'ID de l'administrateur est obligatoire")
        private Long idAdmin;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DossierExamenUpdateRequest {
        private String examen;
        private DossierExamen.EtatDossier etat;
        private LocalDate dateDepot;
        private String observations;
        private Long idAdmin;
        private Boolean isActive;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DossierExamenResponse {
        private Long idExamen;
        private String examen;
        private DossierExamen.EtatDossier etat;
        private LocalDate dateDepot;
        private String observations;
        private Boolean isActive;
        private EleveInfo eleve;
        private PersonnelAdministratifInfo personnelAdministratif;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EleveInfo {
        private String matricule;
        private String nom;
        private String prenom;
        private String classe;
        private String section;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PersonnelAdministratifInfo {
        private Long idAdmin;
        private String nom;
        private String prenom;
        private String email;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DossierExamenStatsResponse {
        private Long totalDossiers;
        private Long dossiersEnCours;
        private Long dossiersTermines;
        private Long dossiersEnAttente;
        private Long dossiersRejetes;
        private List<StatistiqueParClasse> statistiquesParClasse;
        private List<StatistiqueParMois> statistiquesParMois;
        private List<StatistiqueParTypeExamen> statistiquesParTypeExamen;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StatistiqueParClasse {
        private String classe;
        private String section;
        private Long nombreDossiers;
        private Long nombreElevesImpliques;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StatistiqueParMois {
        private String mois;
        private Long nombreDossiers;
        private Long nombreDepots;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StatistiqueParTypeExamen {
        private String typeExamen;
        private Long nombreDossiers;
        private Double pourcentage;
    }
}