package com.school.dto;

import com.school.entity.Eleve;
import com.school.entity.PersonnelAdministratif;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

public class EleveDTOs {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EleveCreateRequest {
        @NotBlank(message = "Le matricule est obligatoire")
        private String matricule;

        @NotBlank(message = "Le nom est obligatoire")
        private String nom;

        private String prenom;

        @NotNull(message = "La date de naissance est obligatoire")
        private LocalDate dateNaissance;

        @NotNull(message = "Le sexe est obligatoire")
        private PersonnelAdministratif.Sexe sexe;

        @NotBlank(message = "Le nom du tuteur est obligatoire")
        private String nomTuteur;

        @NotBlank(message = "Le numéro 1 du tuteur est obligatoire")
        private String numero1Tuteur;

        private String numero2Tuteur;

        @NotNull(message = "Le statut est obligatoire")
        private Eleve.StatutEleve statut;

        @NotBlank(message = "L'adresse est obligatoire")
        private String adresse;

        private String emailTuteur;

        @NotNull(message = "L'ID de la classe est obligatoire")
        private Long idClasse;

        @NotNull(message = "L'ID de la section est obligatoire")
        private Long idSection;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EleveUpdateRequest {
        private String nom;
        private String prenom;
        private LocalDate dateNaissance;
        private PersonnelAdministratif.Sexe sexe;
        private String nomTuteur;
        private String numero1Tuteur;
        private String numero2Tuteur;
        private Eleve.StatutEleve statut;
        private String adresse;
        private String emailTuteur;
        private Long idClasse;
        private Long idSection;
        private Boolean isActive;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EleveResponse {
        private String matricule;
        private String nom;
        private String prenom;
        private LocalDate dateNaissance;
        private PersonnelAdministratif.Sexe sexe;
        private String nomTuteur;
        private String numero1Tuteur;
        private String numero2Tuteur;
        private Eleve.StatutEleve statut;
        private String adresse;
        private String emailTuteur;
        private Boolean isActive;
        private SalleClasseInfo salleDeClasse;
        private SectionInfo section;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EleveDetailResponse {
        private String matricule;
        private String nom;
        private String prenom;
        private LocalDate dateNaissance;
        private PersonnelAdministratif.Sexe sexe;
        private String nomTuteur;
        private String numero1Tuteur;
        private String numero2Tuteur;
        private Eleve.StatutEleve statut;
        private String adresse;
        private String emailTuteur;
        private Boolean isActive;
        private SalleClasseInfo salleDeClasse;
        private SectionInfo section;
        private List<NoteInfo> notes;
        private List<AbsenceInfo> absences;
        private List<DossierInfo> dossiersDiscipline;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SalleClasseInfo {
        private Long idClasse;
        private String nom;
        private String niveau;
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
    public static class NoteInfo {
        private String matiere;
        private Float note;
        private Float noteFinale;
        private Integer sequence;
        private LocalDate dateComposition;
        private String typeEvaluation;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AbsenceInfo {
        private LocalDate jour;
        private String typeAbsence;
        private Boolean justifiee;
        private String motif;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DossierInfo {
        private String type;
        private String description;
        private String etat;
        private LocalDate date;
    }
}