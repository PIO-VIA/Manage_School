package com.school.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "absences")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Absence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_absence")
    private Long idAbsence;

    @Column(name = "horaire", nullable = false)
    private LocalTime horaire;

    @Column(name = "jour", nullable = false)
    private LocalDate jour;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_absence", nullable = false)
    private TypeAbsence typeAbsence;

    @Column(name = "motif")
    private String motif;

    @Column(name = "justifiee", nullable = false)
    private Boolean justifiee = false;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // Relations
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matricule", nullable = false)
    private Eleve eleve;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_discipline")
    private DossierDiscipline dossierDiscipline;

    public enum TypeAbsence {
        MATIN,
        APRES_MIDI,
        JOURNEE_COMPLETE,
        RETARD
    }
}