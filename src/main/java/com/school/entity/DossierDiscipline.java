package com.school.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "dossiers_discipline")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DossierDiscipline {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_discipline")
    private Long idDiscipline;

    @Column(name = "convocation", nullable = false)
    private String convocation;

    @Enumerated(EnumType.STRING)
    @Column(name = "etat", nullable = false)
    private DossierExamen.EtatDossier etat;

    @Enumerated(EnumType.STRING)
    @Column(name = "sanction", nullable = false)
    private TypeSanction sanction;

    @Column(name = "date_incident", nullable = false)
    private LocalDate dateIncident;

    @Column(name = "description_incident")
    private String descriptionIncident;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // Relations
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matricule", nullable = false)
    private Eleve eleve;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_admin", nullable = false)
    private PersonnelAdministratif personnelAdministratif;

    @OneToMany(mappedBy = "dossierDiscipline", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Absence> absences;

    public enum TypeSanction {
        AVERTISSEMENT,
        BLAME,
        EXCLUSION_TEMPORAIRE,
        EXCLUSION_DEFINITIVE,
        TRAVAUX_SUPPLEMENTAIRES,
        RETENUE
    }
}