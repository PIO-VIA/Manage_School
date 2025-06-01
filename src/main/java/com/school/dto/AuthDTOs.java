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

public class AuthDTOs {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LoginRequest {
        @NotBlank(message = "L'email est obligatoire")
        @Email(message = "Format d'email invalide")
        private String email;

        @NotBlank(message = "Le mot de passe est obligatoire")
        private String motDePasse;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LoginResponse {
        private String token;
        private String type = "Bearer";
        private Long id;
        private String nom;
        private String prenom;
        private String email;
        private PersonnelAdministratif.Role role;
        private Long expiresIn;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RegisterRequest {
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

        @NotBlank(message = "L'email est obligatoire")
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
    public static class ChangePasswordRequest {
        @NotBlank(message = "L'ancien mot de passe est obligatoire")
        private String ancienMotDePasse;

        @NotBlank(message = "Le nouveau mot de passe est obligatoire")
        private String nouveauMotDePasse;

        @NotBlank(message = "La confirmation du mot de passe est obligatoire")
        private String confirmationMotDePasse;
    }
}