package com.school.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "composer")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(ComposerId.class)
public class Composer {

    @Id
    @Column(name = "matricule")
    private String matricule;

    @Id
    @Column(name = "id_matiere")
    private Long idMatiere;

    @Id
    @Column(name = "sequence")
    private Integer sequence;

    @Column(name = "note", nullable = false)
    private Float note;

    @Column(name = "note_finale")
    private Float noteFinale;

    @Column(name = "date_composition", nullable = false)
    private LocalDate dateComposition;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_evaluation", nullable = false)
    private TypeEvaluation typeEvaluation;

    // Relations
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matricule", insertable = false, updatable = false)
    private Eleve eleve;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_matiere", insertable = false, updatable = false)
    private Matiere matiere;

    public enum TypeEvaluation {
        COMPOSITION,
        CONTROLE,
        EXAMEN,
        DEVOIR
    }
}