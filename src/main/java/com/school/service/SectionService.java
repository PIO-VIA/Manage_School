package com.school.service;

import com.school.dto.SectionDTOs;
import com.school.entity.Section;
import com.school.exception.ConflictException;
import com.school.exception.NotFoundException;
import com.school.mapper.SectionMapper;
import com.school.repository.SectionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SectionService {

    private final SectionRepository sectionRepository;
    private final SectionMapper sectionMapper;

    @Transactional
    public SectionDTOs.SectionResponse createSection(SectionDTOs.SectionCreateRequest request) {
        log.info("Création d'une nouvelle section: {}", request.getNom());

        // Vérifier si une section avec ce nom existe déjà
        if (sectionRepository.existsByNom(request.getNom())) {
            throw new ConflictException("Une section avec ce nom existe déjà");
        }

        Section section = Section.builder()
                .nom(request.getNom())
                .description(request.getDescription())
                .typeSection(request.getTypeSection())
                .isActive(true)
                .build();

        Section savedSection = sectionRepository.save(section);
        log.info("Section créée avec succès: {}", savedSection.getNom());

        return sectionMapper.toResponse(savedSection);
    }

    @Transactional(readOnly = true)
    public List<SectionDTOs.SectionResponse> getAllSections() {
        log.info("Récupération de toutes les sections actives");

        List<Section> sections = sectionRepository.findByIsActiveTrue();
        return sections.stream()
                .map(sectionMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SectionDTOs.SectionDetailResponse getSectionById(Long id) {
        log.info("Récupération de la section avec l'ID: {}", id);

        Section section = sectionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Section introuvable avec l'ID: " + id));

        return sectionMapper.toDetailResponse(section);
    }

    @Transactional(readOnly = true)
    public List<SectionDTOs.SectionResponse> getSectionsByType(Section.TypeSection type) {
        log.info("Récupération des sections par type: {}", type);

        List<Section> sections = sectionRepository.findByTypeSection(type);
        return sections.stream()
                .map(sectionMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public SectionDTOs.SectionResponse updateSection(Long id, SectionDTOs.SectionUpdateRequest request) {
        log.info("Mise à jour de la section avec l'ID: {}", id);

        Section section = sectionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Section introuvable avec l'ID: " + id));

        // Vérifier si le nouveau nom existe déjà (si changé)
        if (request.getNom() != null && !request.getNom().equals(section.getNom())) {
            if (sectionRepository.existsByNom(request.getNom())) {
                throw new ConflictException("Une section avec ce nom existe déjà");
            }
            section.setNom(request.getNom());
        }

        if (request.getDescription() != null) {
            section.setDescription(request.getDescription());
        }

        if (request.getTypeSection() != null) {
            section.setTypeSection(request.getTypeSection());
        }

        if (request.getIsActive() != null) {
            section.setIsActive(request.getIsActive());
        }

        Section updatedSection = sectionRepository.save(section);
        log.info("Section mise à jour avec succès: {}", updatedSection.getNom());

        return sectionMapper.toResponse(updatedSection);
    }

    @Transactional
    public void deleteSection(Long id) {
        log.info("Suppression logique de la section avec l'ID: {}", id);

        Section section = sectionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Section introuvable avec l'ID: " + id));

        // Vérifier s'il y a des élèves ou enseignants associés
        if (!section.getEleves().isEmpty() || !section.getEnseignants().isEmpty()) {
            throw new ConflictException("Impossible de supprimer cette section car elle contient des élèves ou des enseignants");
        }

        section.setIsActive(false);
        sectionRepository.save(section);

        log.info("Section supprimée avec succès: {}", section.getNom());
    }

    @Transactional
    public void activateSection(Long id) {
        log.info("Activation de la section avec l'ID: {}", id);

        Section section = sectionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Section introuvable avec l'ID: " + id));

        section.setIsActive(true);
        sectionRepository.save(section);

        log.info("Section activée avec succès: {}", section.getNom());
    }

    @Transactional
    public void deactivateSection(Long id) {
        log.info("Désactivation de la section avec l'ID: {}", id);

        Section section = sectionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Section introuvable avec l'ID: " + id));

        section.setIsActive(false);
        sectionRepository.save(section);

        log.info("Section désactivée avec succès: {}", section.getNom());
    }
}