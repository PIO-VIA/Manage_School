package com.school.dto;

import com.school.entity.Materiel;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class MaterielDTOs {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MaterielCreateRequest {
        @NotBlank(message = "Le nom du matériel est obligatoire")
        private String nom;

        @NotNull(message = "La quantité est obligatoire")
        @Min(value = 1, message = "La quantité doit être d'au moins 1")
        private Integer quantite;

        @NotNull(message = "L'état du matériel est obligatoire")
        private Materiel.EtatMateriel etat;

        private String description;

        @NotNull(message = "L'ID du personnel d'entretien est obligatoire")
        private Long idEntretien;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MaterielUpdateRequest {
        private String nom;
        private Integer quantite;
        private Materiel.EtatMateriel etat;
        private String description;
        private Long idEntretien;
        private Boolean isActive;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MaterielResponse {
        private Long idMateriel;
        private String nom;
        private Integer quantite;
        private Materiel.EtatMateriel etat;
        private String description;
        private Boolean isActive;
        private PersonnelEntretienInfo personnelEntretien;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PersonnelEntretienInfo {
        private Long idEntretien;
        private String nom;
        private String prenom;
        private String lieuService;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MaterielStatsResponse {
        private Long totalMateriels;
        private Long materielNeuf;
        private Long materielBon;
        private Long materielMoyen;
        private Long materielMauvais;
        private Long materielHorsService;
        private Integer quantiteTotale;
        private List<StatistiqueParPersonnel> statistiquesParPersonnel;
        private List<StatistiqueParEtat> statistiquesParEtat;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StatistiqueParPersonnel {
        private String nomPersonnel;
        private String lieuService;
        private Integer nombreMateriels;
        private Integer quantiteTotale;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StatistiqueParEtat {
        private Materiel.EtatMateriel etat;
        private Long nombreMateriels;
        private Integer quantiteTotale;
        private Double pourcentage;
    }
}