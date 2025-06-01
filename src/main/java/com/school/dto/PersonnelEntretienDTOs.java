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

public class PersonnelEntretienDTOs {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PersonnelEntretienCreateRequest {
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

        @NotBlank(message = "Le lieu de service est obligatoire")
        private String lieuService;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PersonnelEntretienUpdateRequest {
        private String nom;
        private String prenom;
        private PersonnelAdministratif.Sexe sexe;
        private PersonnelAdministratif.StatutPersonnel statut;
        private String telephone1;
        private String telephone2;
        private String email;
        private LocalDate datePriseService;
        private String lieuService;
        private Boolean isActive;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PersonnelEntretienResponse {
        private Long idEntretien;
        private String nom;
        private String prenom;
        private PersonnelAdministratif.Sexe sexe;
        private PersonnelAdministratif.StatutPersonnel statut;
        private String telephone1;
        private String telephone2;
        private String email;
        private LocalDate datePriseService;
        private String lieuService;
        private Boolean isActive;
        private Integer nombreMateriels;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PersonnelEntretienDetailResponse {
        private Long idEntretien;
        private String nom;
        private String prenom;
        private PersonnelAdministratif.Sexe sexe;
        private PersonnelAdministratif.StatutPersonnel statut;
        private String telephone1;
        private String telephone2;
        private String email;
        private LocalDate datePriseService;
        private String lieuService;
        private Boolean isActive;
        private List<MaterielInfo> materiels;
        private Integer anneesService;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MaterielInfo {
        private Long idMateriel;
        private String nom;
        private Integer quantite;
        private String etat;
        private String description;
    }
}