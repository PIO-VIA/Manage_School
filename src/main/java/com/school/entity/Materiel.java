package com.school.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "materiels")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Materiel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_materiel")
    private Long idMateriel;

    @Column(name = "nom", nullable = false)
    private String nom;

    @Column(name = "quantite", nullable = false)
    private Integer quantite;

    @Enumerated(EnumType.STRING)
    @Column(name = "etat", nullable = false)
    private EtatMateriel etat;

    @Column(name = "description")
    private String description;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // Relations
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_entretien", nullable = false)
    private PersonnelEntretien personnelEntretien;

    public enum EtatMateriel {
        NEUF,
        BON,
        MOYEN,
        MAUVAIS,
        HORS_SERVICE
    }
}