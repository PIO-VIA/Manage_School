package com.school.dto;

import com.school.entity.DossierDiscipline;
import com.school.entity.DossierExamen;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

public class DossierDisciplineDTOs {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DossierDisciplineCreateRequest {
        @NotBlank(message = "La convocation est obligatoire")
        private String convocation;

        @NotNull(message = "L'état est obligatoire")
        private DossierExamen.EtatDossier etat;

        @NotNull(message = "La sanction est obligatoire")
        private DossierDiscipline.TypeSanction sanction;

        @NotNull(message = "La date de l'incident est obligatoire")
        private LocalDate dateIncident;

        private String descriptionIncident;

        @NotBlank(message = "Le matricule de l'élève est obligatoire")
        private String matricule;

        @NotNull(message = "L'ID de l'administrateur est obligatoire")
        private Long idAdmin;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DossierDisciplineUpdateRequest {
        private String convocation;
        private DossierExamen.EtatDossier etat;
        private DossierDiscipline.TypeSanction sanction;
        private LocalDate dateIncident;
        private String descriptionIncident;
        private Long idAdmin;
        private Boolean isActive;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DossierDisciplineResponse {
        private Long idDiscipline;
        private String convocation;
        private DossierExamen.EtatDossier etat;
        private DossierDiscipline.TypeSanction sanction;
        private LocalDate dateIncident;
        private String descriptionIncident;
        private Boolean isActive;
        private EleveInfo eleve;
        private PersonnelAdministratifInfo personnelAdministratif;
        private Integer nombreAbsencesLiees;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DossierDisciplineDetailResponse {
        private Long idDiscipline;
        private String convocation;
        private DossierExamen.EtatDossier etat;
        private DossierDiscipline.TypeSanction sanction;
        private LocalDate dateIncident;
        private String descriptionIncident;
        private Boolean isActive;
        private EleveInfo eleve;
        private PersonnelAdministratifInfo personnelAdministratif;
        private List<AbsenceInfo> absencesLiees;
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
    public static class AbsenceInfo {
        private Long idAbsence;
        private LocalDate jour;
        private String typeAbsence;
        private String motif;
        private Boolean justifiee;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DossierDisciplineStatsResponse {
        private Long totalDossiers;
        private Long dossiersEnCours;
        private Long dossiersTermines;
        private Long dossiersEnAttente;
        private Long dossiersRejetes;
        private List<StatistiqueParSanction> statistiquesParSanction;
        private List<StatistiqueParClasse> statistiquesParClasse;
        private List<StatistiqueParMois> statistiquesParMois;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StatistiqueParSanction {
        private DossierDiscipline.TypeSanction sanction;
        private Long nombreDossiers;
        private Double pourcentage;
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
        private Long nombreIncidents;
    }
}