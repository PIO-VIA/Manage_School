package com.school.dto;

import com.school.entity.PersonnelAdministratif;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

public class EnseignantDTOs {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EnseignantCreateRequest {
        @NotBlank(message = "Le nom est obligatoire")
        private String nom;

        private String prenom;

        @NotNull(message = "Le sexe est obligatoire")
        private PersonnelAdministratif.Sexe sexe;

        @NotNull(message = "Le statut est obligatoire")
        private PersonnelAdministratif.StatutPersonnel statut;

        @NotBlank(message = "Le téléphone 1 est obligatoire")
        private String telephone1;

        private String telephone2;

        @Email(message = "Format d'email invalide")
        private String email;

        @NotNull(message = "La date de prise de service est obligatoire")
        private LocalDate datePriseService;

        private String specialite;

        private Long idClasse;

        @NotNull(message = "L'ID de la section est obligatoire")
        private Long idSection;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EnseignantUpdateRequest {
        private String nom;
        private String prenom;
        private PersonnelAdministratif.Sexe sexe;
        private PersonnelAdministratif.StatutPersonnel statut;
        private String telephone1;
        private String telephone2;
        private String email;
        private LocalDate datePriseService;
        private String specialite;
        private Long idClasse;
        private Long idSection;
        private Boolean isActive;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EnseignantResponse {
        private Long idMaitre;
        private String nom;
        private String prenom;
        private PersonnelAdministratif.Sexe sexe;
        private PersonnelAdministratif.StatutPersonnel statut;
        private String telephone1;
        private String telephone2;
        private String email;
        private LocalDate datePriseService;
        private String specialite;
        private Boolean isActive;
        private SalleClasseInfo salleDeClasse;
        private SectionInfo section;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EnseignantDetailResponse {
        private Long idMaitre;
        private String nom;
        private String prenom;
        private PersonnelAdministratif.Sexe sexe;
        private PersonnelAdministratif.StatutPersonnel statut;
        private String telephone1;
        private String telephone2;
        private String email;
        private LocalDate datePriseService;
        private String specialite;
        private Boolean isActive;
        private SalleClasseInfo salleDeClasse;
        private SectionInfo section;
        private List<MatiereEnseigneeInfo> matieresEnseignees;
        private Integer nombreElevesClasse;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SalleClasseInfo {
        private Long idClasse;
        private String nom;
        private String niveau;
        private Integer effectif;
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
    public static class MatiereEnseigneeInfo {
        private Long idMatiere;
        private String nomMatiere;
        private Integer coefficient;
        private LocalDate dateOccupation;
        private Boolean isActive;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EnseignantStatsResponse {
        private Long idMaitre;
        private String nom;
        private String prenom;
        private String specialite;
        private Integer nombreMatieresEnseignees;
        private Integer nombreElevesClasse;
        private Integer anneesExperience;
        private String section;
        private String classe;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AssignMatiereRequest {
        @NotNull(message = "L'ID de la matière est obligatoire")
        private Long idMatiere;

        @NotNull(message = "La date d'occupation est obligatoire")
        private LocalDate dateOccupation;
    }
}