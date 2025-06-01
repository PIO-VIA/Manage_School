package com.school.repository;

import com.school.entity.Composer;
import com.school.entity.ComposerId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComposerRepository extends JpaRepository<Composer, ComposerId> {
    List<Composer> findByMatricule(String matricule);
    List<Composer> findByIdMatiere(Long idMatiere);
    List<Composer> findBySequence(Integer sequence);

    @Query("SELECT c FROM Composer c WHERE c.matricule = :matricule AND c.sequence = :sequence")
    List<Composer> findByEleveAndSequence(@Param("matricule") String matricule, @Param("sequence") Integer sequence);

    @Query("SELECT AVG(c.note) FROM Composer c WHERE c.matricule = :matricule AND c.idMatiere = :matiereId")
    Double calculateAverageNote(@Param("matricule") String matricule, @Param("matiereId") Long matiereId);
}
