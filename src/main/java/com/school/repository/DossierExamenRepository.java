package com.school.repository;

import com.school.entity.DossierExamen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DossierExamenRepository extends JpaRepository<DossierExamen, Long> {
    List<DossierExamen> findByIsActiveTrue();
    List<DossierExamen> findByEleveMatriculeAndIsActiveTrue(String matricule);
    List<DossierExamen> findByEtatAndIsActiveTrue(DossierExamen.EtatDossier etat);
}
