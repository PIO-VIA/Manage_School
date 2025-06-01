package com.school.dto;

import com.school.entity.Absence;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class AbsenceDTOs {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AbsenceCreateRequest {
        @NotBlank(message = "Le matricule de l'élève est obligatoire")
        private String matricule;

        @NotNull(message = "L'horaire est obligatoire")
        private LocalTime horaire;

        @NotNull(message = "Le jour est obligatoire")
        private LocalDate jour;

        @NotNull(message = "Le type d'absence est obligatoire")
        private Absence.TypeAbsence typeAbsence;

        private String motif;

        private Boolean justifiee = false;

        private Long idDiscipline;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AbsenceUpdateRequest {
        private LocalTime horaire;
        private LocalDate jour;
        private Absence.TypeAbsence typeAbsence;
        private String motif;
        private Boolean justifiee;
        private Long idDiscipline;
        private Boolean isActive;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AbsenceResponse {
        private Long idAbsence;
        private LocalTime horaire;
        private LocalDate jour;
        private Absence.TypeAbsence typeAbsence;
        private String motif;
        private Boolean justifiee;
        private Boolean isActive;
        private EleveInfo eleve;
        private DossierDisciplineInfo dossierDiscipline;
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
    public static class DossierDisciplineInfo {
        private Long idDiscipline;
        private String convocation;
        private String etat;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AbsenceStatsResponse {
        private String matricule;
        private String nomEleve;
        private String prenomEleve;
        private String classe;
        private String section;
        private Integer totalAbsences;
        private Integer absencesJustifiees;
        private Integer absencesNonJustifiees;
        private Integer retards;
        private Integer journeesCompletes;
        private Double tauxAbsenteisme;
        private LocalDate premiereAbsence;
        private LocalDate derniereAbsence;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AbsenceClasseStatsResponse {
        private Long idClasse;
        private String nomClasse;
        private String section;
        private Integer effectifClasse;
        private Integer totalAbsences;
        private Integer elevesAvecAbsences;
        private Double moyenneAbsencesParEleve;
        private Double tauxAbsenteismeClasse;
        private List<AbsenceStatsResponse> detailsParEleve;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AbsencePeriodeRequest {
        @NotNull(message = "La date de début est obligatoire")
        private LocalDate dateDebut;

        @NotNull(message = "La date de fin est obligatoire")
        private LocalDate dateFin;
    }
}