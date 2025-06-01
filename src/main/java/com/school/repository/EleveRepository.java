package com.school.repository;

import com.school.entity.Eleve;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EleveRepository extends JpaRepository<Eleve, String> {
    List<Eleve> findByIsActiveTrue();
    Page<Eleve> findByIsActiveTrue(Pageable pageable);
    List<Eleve> findBySalleDeClasseIdClasseAndIsActiveTrue(Long idClasse);
    List<Eleve> findBySectionIdSectionAndIsActiveTrue(Long idSection);

    @Query("SELECT e FROM Eleve e WHERE e.section.idSection = :sectionId AND e.salleDeClasse.idClasse = :classeId AND e.isActive = true")
    List<Eleve> findBySectionAndClasse(@Param("sectionId") Long sectionId, @Param("classeId") Long classeId);

    @Query("SELECT e FROM Eleve e WHERE (LOWER(e.nom) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(e.prenom) LIKE LOWER(CONCAT('%', :keyword, '%')) OR e.matricule LIKE CONCAT('%', :keyword, '%')) AND e.isActive = true")
    Page<Eleve> searchEleves(@Param("keyword") String keyword, Pageable pageable);

    boolean existsByMatricule(String matricule);
}
