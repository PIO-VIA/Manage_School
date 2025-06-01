package com.school.repository;

import com.school.entity.Matiere;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MatiereRepository extends JpaRepository<Matiere, Long> {
    List<Matiere> findByIsActiveTrue();
    Optional<Matiere> findByNomAndIsActiveTrue(String nom);
    boolean existsByNom(String nom);
}
