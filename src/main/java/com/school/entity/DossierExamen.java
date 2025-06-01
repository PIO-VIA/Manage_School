package com.school.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "dossiers_examen")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DossierExamen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_examen")
    private Long idExamen;

    @Column(name = "examen", nullable = false)
    private String examen;

    @Enumerated(EnumType.STRING)
    @Column(name = "etat", nullable = false)
    private EtatDossier etat;

    @Column(name = "date_depot", nullable = false)
    private LocalDate dateDepot;

    @Column(name = "observations")
    private String observations;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // Relations
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matricule", nullable = false)
    private Eleve eleve;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_admin", nullable = false)
    private PersonnelAdministratif personnelAdministratif;

    public enum EtatDossier {
        EN_COURS,
        TERMINE,
        EN_ATTENTE,
        REJETE
    }
}