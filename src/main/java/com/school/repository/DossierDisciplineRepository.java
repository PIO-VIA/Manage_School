package com.school.repository;

import com.school.entity.DossierDiscipline;
import com.school.entity.DossierExamen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DossierDisciplineRepository extends JpaRepository<DossierDiscipline, Long> {
    List<DossierDiscipline> findByIsActiveTrue();
    List<DossierDiscipline> findByEleveMatriculeAndIsActiveTrue(String matricule);
    List<DossierDiscipline> findByEtatAndIsActiveTrue(DossierExamen.EtatDossier etat);
}
