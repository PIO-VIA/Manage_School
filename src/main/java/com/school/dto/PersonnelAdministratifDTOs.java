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

public class PersonnelAdministratifDTOs {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PersonnelAdministratifCreateRequest {
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

        @NotBlank(message = "Le mot de passe est obligatoire")
        private String motDePasse;

        private PersonnelAdministratif.Role role = PersonnelAdministratif.Role.ADMIN;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PersonnelAdministratifUpdateRequest {
        private String nom;
        private String prenom;
        private PersonnelAdministratif.Sexe sexe;
        private PersonnelAdministratif.StatutPersonnel statut;
        private String telephone1;
        private String telephone2;
        @Email(message = "Format d'email invalide")
        private String email;
        private LocalDate datePriseService;
        private PersonnelAdministratif.Role role;
        private Boolean isActive;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PersonnelAdministratifResponse {
        private Long idAdmin;
        private String nom;
        private String prenom;
        private PersonnelAdministratif.Sexe sexe;
        private PersonnelAdministratif.StatutPersonnel statut;
        private String telephone1;
        private String telephone2;
        private String email;
        private LocalDate datePriseService;
        private PersonnelAdministratif.Role role;
        private Boolean isActive;
        private Integer anneesService;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PersonnelAdministratifDetailResponse {
        private Long idAdmin;
        private String nom;
        private String prenom;
        private PersonnelAdministratif.Sexe sexe;
        private PersonnelAdministratif.StatutPersonnel statut;
        private String telephone1;
        private String telephone2;
        private String email;
        private LocalDate datePriseService;
        private PersonnelAdministratif.Role role;
        private Boolean isActive;
        private Integer anneesService;
        private List<DossierInfo> dossiersExamen;
        private List<DossierInfo> dossiersDiscipline;
        private List<DossierInfo> dossiersInscription;
        private StatistiquesActivite statistiquesActivite;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DossierInfo {
        private Long idDossier;
        private String type;
        private String description;
        private String etat;
        private LocalDate date;
        private String eleveNom;
        private String eleveMatricule;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StatistiquesActivite {
        private Integer totalDossiersExamen;
        private Integer totalDossiersDiscipline;
        private Integer totalDossiersInscription;
        private Integer dossiersEnCours;
        private Integer dossiersTermines;
        private LocalDate derniereDossier;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PersonnelAdministratifStatsResponse {
        private Long totalPersonnel;
        private Long personnelActif;
        private Long personnelInactif;
        private Long admins;
        private Long superAdmins;
        private List<StatistiqueParRole> statistiquesParRole;
        private List<StatistiqueParAnnee> statistiquesParAnnee;
        private Double moyenneAnneesService;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StatistiqueParRole {
        private PersonnelAdministratif.Role role;
        private Long nombre;
        private Double pourcentage;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StatistiqueParAnnee {
        private String annee;
        private Long nombreEmbauches;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ChangePasswordAdminRequest {
        @NotBlank(message = "Le nouveau mot de passe est obligatoire")
        private String nouveauMotDePasse;

        @NotBlank(message = "La confirmation du mot de passe est obligatoire")
        private String confirmationMotDePasse;
    }
}