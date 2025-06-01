package com.school.repository;

import com.school.entity.Absence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AbsenceRepository extends JpaRepository<Absence, Long> {
    List<Absence> findByIsActiveTrue();
    List<Absence> findByEleveMatriculeAndIsActiveTrue(String matricule);

    @Query("SELECT a FROM Absence a WHERE a.eleve.matricule = :matricule AND a.jour BETWEEN :dateDebut AND :dateFin AND a.isActive = true")
    List<Absence> findByEleveAndDateRange(@Param("matricule") String matricule, @Param("dateDebut") LocalDate dateDebut, @Param("dateFin") LocalDate dateFin);

    @Query("SELECT COUNT(a) FROM Absence a WHERE a.eleve.matricule = :matricule AND a.justifiee = false AND a.isActive = true")
    Long countAbsencesNonJustifiees(@Param("matricule") String matricule);
}
