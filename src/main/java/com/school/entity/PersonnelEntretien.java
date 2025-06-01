package com.school.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "personnel_entretien")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonnelEntretien {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_entretien")
    private Long idEntretien;

    @Column(name = "nom", nullable = false)
    private String nom;

    @Column(name = "prenom")
    private String prenom;

    @Enumerated(EnumType.STRING)
    @Column(name = "sexe", nullable = false)
    private PersonnelAdministratif.Sexe sexe;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false)
    private PersonnelAdministratif.StatutPersonnel statut;

    @Column(name = "telephone_1", nullable = false)
    private String telephone1;

    @Column(name = "telephone_2")
    private String telephone2;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "date_prise_service", nullable = false)
    private LocalDate datePriseService;

    @Column(name = "lieu_service", nullable = false)
    private String lieuService;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // Relations
    @OneToMany(mappedBy = "personnelEntretien", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Materiel> materiels;
}