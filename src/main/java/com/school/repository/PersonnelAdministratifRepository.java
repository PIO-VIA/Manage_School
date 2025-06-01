package com.school.repository;

import com.school.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PersonnelAdministratifRepository extends JpaRepository<PersonnelAdministratif, Long> {
    Optional<PersonnelAdministratif> findByEmail(String email);
    Optional<PersonnelAdministratif> findByEmailAndIsActiveTrue(String email);
    boolean existsByEmail(String email);
    List<PersonnelAdministratif> findByIsActiveTrue();
}

