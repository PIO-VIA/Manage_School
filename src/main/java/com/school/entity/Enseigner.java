package com.school.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "enseigner")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(EnseignerId.class)
public class Enseigner {

    @Id
    @Column(name = "id_matiere")
    private Long idMatiere;

    @Id
    @Column(name = "id_maitre")
    private Long idMaitre;

    @Column(name = "date_occupation", nullable = false)
    private LocalDate dateOccupation;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // Relations
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_matiere", insertable = false, updatable = false)
    private Matiere matiere;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_maitre", insertable = false, updatable = false)
    private Enseignant enseignant;
}