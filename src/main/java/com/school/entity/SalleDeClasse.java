package com.school.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "salles_classe")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalleDeClasse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_classe")
    private Long idClasse;

    @Column(name = "nom", nullable = false)
    private String nom;

    @Enumerated(EnumType.STRING)
    @Column(name = "niveau", nullable = false)
    private Niveau niveau;

    @Column(name = "effectif", nullable = false)
    private Integer effectif = 0;

    @Column(name = "capacity_max")
    private Integer capacityMax = 50;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // Relations
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_section", nullable = false)
    private Section section;

    @OneToMany(mappedBy = "salleDeClasse", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Eleve> eleves;

    @OneToMany(mappedBy = "salleDeClasse", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Enseignant> enseignants;

    public enum Niveau {
        MATERNELLE("Maternelle"),
        CP("CP"),
        CE1("CE1"),
        CE2("CE2"),
        CM1("CM1"),
        CM2("CM2");

        private final String description;

        Niveau(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}