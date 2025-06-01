package com.school.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "eleves")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Eleve {

    @Id
    @Column(name = "matricule")
    private String matricule;

    @Column(name = "nom", nullable = false)
    private String nom;

    @Column(name = "prenom")
    private String prenom;

    @Column(name = "date_naissance", nullable = false)
    private LocalDate dateNaissance;

    @Enumerated(EnumType.STRING)
    @Column(name = "sexe", nullable = false)
    private PersonnelAdministratif.Sexe sexe;

    @Column(name = "nom_tuteur", nullable = false)
    private String nomTuteur;

    @Column(name = "numero_1_tuteur", nullable = false)
    private String numero1Tuteur;

    @Column(name = "numero_2_tuteur")
    private String numero2Tuteur;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false)
    private StatutEleve statut;

    @Column(name = "adresse", nullable = false)
    private String adresse;

    @Column(name = "email_tuteur")
    private String emailTuteur;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // Relations
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_classe", nullable = false)
    private SalleDeClasse salleDeClasse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_section", nullable = false)
    private Section section;

    @OneToMany(mappedBy = "eleve", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Composer> compositions;

    @OneToMany(mappedBy = "eleve", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DossierExamen> dossiersExamen;

    @OneToMany(mappedBy = "eleve", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Absence> absences;

    @OneToMany(mappedBy = "eleve", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DossierDiscipline> dossiersDiscipline;

    @OneToMany(mappedBy = "eleve", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DossierInscription> dossiersInscription;

    public enum StatutEleve {
        INSCRIT,
        REDOUBLANT,
        NOUVEAU,
        TRANSFERE,
        ABANDONNE
    }
}