package com.school.repository;

import com.school.entity.Enseigner;
import com.school.entity.EnseignerId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnseignerRepository extends JpaRepository<Enseigner, EnseignerId> {
    List<Enseigner> findByIsActiveTrue();
    List<Enseigner> findByIdMaitreAndIsActiveTrue(Long idMaitre);
    List<Enseigner> findByIdMatiereAndIsActiveTrue(Long idMatiere);

    @Query("SELECT e FROM Enseigner e WHERE e.idMaitre = :enseignantId AND e.idMatiere = :matiereId AND e.isActive = true")
    Optional<Enseigner> findByEnseignantAndMatiere(@Param("enseignantId") Long enseignantId, @Param("matiereId") Long matiereId);
}
