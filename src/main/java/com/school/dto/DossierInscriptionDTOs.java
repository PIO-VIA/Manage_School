package com.school.dto;

import com.school.entity.DossierInscription;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class DossierInscriptionDTOs {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DossierInscriptionCreateRequest {
        @NotNull(message = "La somme à payer est obligatoire")
        @DecimalMin(value = "0.0", message = "La somme à payer ne peut pas être négative")
        private BigDecimal sommeAPayer;

        @DecimalMin(value = "0.0", message = "La somme versée ne peut pas être négative")
        private BigDecimal sommeVersee = BigDecimal.ZERO;

        @NotNull(message = "L'état de l'inscription est obligatoire")
        private DossierInscription.EtatInscription etat;

        private LocalDate datePaiement;

        @NotBlank(message = "L'année scolaire est obligatoire")
        private String anneeScolaire;

        @NotNull(message = "La date d'inscription est obligatoire")
        private LocalDate dateInscription;

        @NotBlank(message = "Le matricule de l'élève est obligatoire")
        private String matricule;

        @NotNull(message = "L'ID de l'administrateur est obligatoire")
        private Long idAdmin;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DossierInscriptionUpdateRequest {
        private BigDecimal sommeAPayer;
        private BigDecimal sommeVersee;
        private DossierInscription.EtatInscription etat;
        private LocalDate datePaiement;
        private String anneeScolaire;
        private LocalDate dateInscription;
        private Long idAdmin;
        private Boolean isActive;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DossierInscriptionResponse {
        private Long idInscription;
        private BigDecimal sommeAPayer;
        private BigDecimal sommeVersee;
        private BigDecimal reste;
        private DossierInscription.EtatInscription etat;
        private LocalDate datePaiement;
        private String anneeScolaire;
        private LocalDate dateInscription;
        private Boolean isActive;
        private Double pourcentagePaye;
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
        private String nomTuteur;
        private String emailTuteur;
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
    public static class PaiementRequest {
        @NotNull(message = "Le montant du paiement est obligatoire")
        @DecimalMin(value = "0.01", message = "Le montant du paiement doit être positif")
        private BigDecimal montant;

        @NotNull(message = "La date du paiement est obligatoire")
        private LocalDate datePaiement;

        private String modePaiement;
        private String numeroPaiement;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DossierInscriptionStatsResponse {
        private Long totalInscriptions;
        private Long inscriptionsCompletes;
        private Long inscriptionsPartielles;
        private Long inscriptionsEnAttente;
        private Long inscriptionsExpirees;
        private BigDecimal totalSommesAPayer;
        private BigDecimal totalSommesVersees;
        private BigDecimal totalRestes;
        private Double tauxRecouvrement;
        private List<StatistiqueParAnneeScolaire> statistiquesParAnneeScolaire;
        private List<StatistiqueParSection> statistiquesParSection;
        private List<StatistiqueParMois> statistiquesParMois;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StatistiqueParAnneeScolaire {
        private String anneeScolaire;
        private Long nombreInscriptions;
        private BigDecimal totalSommesAPayer;
        private BigDecimal totalSommesVersees;
        private Double tauxRecouvrement;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StatistiqueParSection {
        private String section;
        private Long nombreInscriptions;
        private BigDecimal moyenneSommeAPayer;
        private Double tauxRecouvrement;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StatistiqueParMois {
        private String mois;
        private Long nombreInscriptions;
        private BigDecimal totalPaiements;
        private Long nombrePaiements;
    }
}