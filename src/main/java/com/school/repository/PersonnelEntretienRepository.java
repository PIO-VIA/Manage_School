package com.school.repository;

import com.school.entity.PersonnelEntretien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonnelEntretienRepository extends JpaRepository<PersonnelEntretien, Long> {
    List<PersonnelEntretien> findByIsActiveTrue();
    Optional<PersonnelEntretien> findByEmail(String email);
    boolean existsByEmail(String email);
}
