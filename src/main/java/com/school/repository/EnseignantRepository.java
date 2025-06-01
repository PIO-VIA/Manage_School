package com.school.repository;

import com.school.entity.Enseignant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnseignantRepository extends JpaRepository<Enseignant, Long> {
    List<Enseignant> findByIsActiveTrue();
    List<Enseignant> findBySectionIdSectionAndIsActiveTrue(Long idSection);
    List<Enseignant> findBySalleDeClasseIdClasseAndIsActiveTrue(Long idClasse);
    Optional<Enseignant> findByEmail(String email);
    boolean existsByEmail(String email);

    @Query("SELECT e FROM Enseignant e WHERE e.section.idSection = :sectionId AND e.specialite = :specialite AND e.isActive = true")
    List<Enseignant> findBySectionAndSpecialite(@Param("sectionId") Long sectionId, @Param("specialite") String specialite);
}
