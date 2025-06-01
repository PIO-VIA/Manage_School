package com.school.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "dossiers_inscription")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DossierInscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_inscription")
    private Long idInscription;

    @Column(name = "somme_a_payer", nullable = false, precision = 10, scale = 2)
    private BigDecimal sommeAPayer;

    @Column(name = "somme_versee", nullable = false, precision = 10, scale = 2)
    private BigDecimal sommeVersee = BigDecimal.ZERO;

    @Column(name = "reste", nullable = false, precision = 10, scale = 2)
    private BigDecimal reste;

    @Enumerated(EnumType.STRING)
    @Column(name = "etat", nullable = false)
    private EtatInscription etat;

    @Column(name = "date_paiement")
    private LocalDate datePaiement;

    @Column(name = "annee_scolaire", nullable = false)
    private String anneeScolaire;

    @Column(name = "date_inscription", nullable = false)
    private LocalDate dateInscription;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // Relations
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matricule", nullable = false)
    private Eleve eleve;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_admin", nullable = false)
    private PersonnelAdministratif personnelAdministratif;

    // Méthode pour calculer le reste
    @PrePersist
    @PreUpdate
    public void calculerReste() {
        if (sommeAPayer != null && sommeVersee != null) {
            this.reste = sommeAPayer.subtract(sommeVersee);
        }
    }

    public enum EtatInscription {
        EN_ATTENTE,
        PARTIELLE,
        COMPLETE,
        EXPIREE
    }
}