package com.school.repository;

import com.school.entity.DossierInscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DossierInscriptionRepository extends JpaRepository<DossierInscription, Long> {
    List<DossierInscription> findByIsActiveTrue();
    List<DossierInscription> findByEleveMatriculeAndIsActiveTrue(String matricule);
    List<DossierInscription> findByAnneeScolaireAndIsActiveTrue(String anneeScolaire);
    List<DossierInscription> findByEtatAndIsActiveTrue(DossierInscription.EtatInscription etat);
}
