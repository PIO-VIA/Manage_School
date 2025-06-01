package com.school.repository;

import com.school.entity.SalleDeClasse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalleDeClasseRepository extends JpaRepository<SalleDeClasse, Long> {
    List<SalleDeClasse> findByIsActiveTrue();
    List<SalleDeClasse> findBySectionIdSectionAndIsActiveTrue(Long idSection);

    @Query("SELECT s FROM SalleDeClasse s WHERE s.section.idSection = :sectionId AND s.niveau = :niveau AND s.isActive = true")
    List<SalleDeClasse> findBySectionAndNiveau(@Param("sectionId") Long sectionId, @Param("niveau") SalleDeClasse.Niveau niveau);

    @Query("SELECT COUNT(e) FROM Eleve e WHERE e.salleDeClasse.idClasse = :classeId AND e.isActive = true")
    Integer countActiveEleves(@Param("classeId") Long classeId);
}
