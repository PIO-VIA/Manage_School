package com.school.dto;

import com.school.entity.Composer;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

public class NoteDTOs {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class NoteCreateRequest {
        @NotBlank(message = "Le matricule de l'élève est obligatoire")
        private String matricule;

        @NotNull(message = "L'ID de la matière est obligatoire")
        private Long idMatiere;

        @NotNull(message = "La séquence est obligatoire")
        @Min(value = 1, message = "La séquence doit être d'au moins 1")
        @Max(value = 6, message = "La séquence ne peut pas dépasser 6")
        private Integer sequence;

        @NotNull(message = "La note est obligatoire")
        @DecimalMin(value = "0.0", message = "La note ne peut pas être négative")
        @DecimalMax(value = "20.0", message = "La note ne peut pas dépasser 20")
        private Float note;

        private Float noteFinale;

        @NotNull(message = "La date de composition est obligatoire")
        private LocalDate dateComposition;

        @NotNull(message = "Le type d'évaluation est obligatoire")
        private Composer.TypeEvaluation typeEvaluation;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class NoteUpdateRequest {
        @DecimalMin(value = "0.0", message = "La note ne peut pas être négative")
        @DecimalMax(value = "20.0", message = "La note ne peut pas dépasser 20")
        private Float note;

        private Float noteFinale;

        private LocalDate dateComposition;

        private Composer.TypeEvaluation typeEvaluation;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class NoteResponse {
        private String matricule;
        private Long idMatiere;
        private Integer sequence;
        private Float note;
        private Float noteFinale;
        private LocalDate dateComposition;
        private Composer.TypeEvaluation typeEvaluation;
        private EleveInfo eleve;
        private MatiereInfo matiere;
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
    public static class MatiereInfo {
        private Long idMatiere;
        private String nom;
        private Integer coefficient;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BulletinEleveResponse {
        private String matricule;
        private String nom;
        private String prenom;
        private String classe;
        private String section;
        private Integer sequence;
        private List<NoteDetaillee> notes;
        private Double moyenneGenerale;
        private Integer rang;
        private String mention;
        private String appreciation;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class NoteDetaillee {
        private String matiere;
        private Integer coefficient;
        private Float note;
        private Float noteFinale;
        private String typeEvaluation;
        private LocalDate dateComposition;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StatistiquesSequenceResponse {
        private Integer sequence;
        private String classe;
        private String section;
        private Integer nombreEleves;
        private Double moyenneClasse;
        private Double noteMin;
        private Double noteMax;
        private Integer nombreReussites;
        private Double tauxReussite;
        private List<StatistiqueMatiereSequence> statistiquesParMatiere;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StatistiqueMatiereSequence {
        private String matiere;
        private Integer coefficient;
        private Integer nombreEleves;
        private Double moyenne;
        private Double noteMin;
        private Double noteMax;
        private Integer nombreReussites;
        private Double tauxReussite;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ClassementEleveResponse {
        private Integer rang;
        private String matricule;
        private String nom;
        private String prenom;
        private Double moyenne;
        private String mention;
        private String classe;
        private String section;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ReleveNotesRequest {
        @NotBlank(message = "Le matricule de l'élève est obligatoire")
        private String matricule;

        @NotNull(message = "La séquence est obligatoire")
        private Integer sequence;
    }
}