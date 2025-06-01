package com.school.repository;

import com.school.entity.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SectionRepository extends JpaRepository<Section, Long> {
    List<Section> findByIsActiveTrue();
    Optional<Section> findByNomAndIsActiveTrue(String nom);
    boolean existsByNom(String nom);

    @Query("SELECT s FROM Section s WHERE s.typeSection = :type AND s.isActive = true")
    List<Section> findByTypeSection(@Param("type") Section.TypeSection type);
}
