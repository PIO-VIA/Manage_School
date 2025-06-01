package com.school.repository;

import com.school.entity.Materiel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaterielRepository extends JpaRepository<Materiel, Long> {
    List<Materiel> findByIsActiveTrue();
    List<Materiel> findByPersonnelEntretienIdEntretienAndIsActiveTrue(Long idEntretien);
    List<Materiel> findByEtatAndIsActiveTrue(Materiel.EtatMateriel etat);
}
