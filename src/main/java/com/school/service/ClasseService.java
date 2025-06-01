package com.school.service;

import com.school.dto.ClasseDTOs;
import com.school.entity.SalleDeClasse;
import com.school.entity.Section;
import com.school.exception.ConflictException;
import com.school.exception.NotFoundException;
import com.school.mapper.ClasseMapper;
import com.school.repository.SalleDeClasseRepository;
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
public class ClasseService {

    private final SalleDeClasseRepository classeRepository;
    private final SectionRepository sectionRepository;
    private final ClasseMapper classeMapper;

    @Transactional
    public ClasseDTOs.ClasseResponse createClasse(ClasseDTOs.ClasseCreateRequest request) {
        log.info("Création d'une nouvelle classe: {}", request.getNom());

        // Vérifier que la section existe
        Section section = sectionRepository.findById(request.getIdSection())
                .orElseThrow(() -> new NotFoundException("Section introuvable avec l'ID: " + request.getIdSection()));

        if (!section.getIsActive()) {
            throw new ConflictException("Impossible de créer une classe dans une section inactive");
        }

        SalleDeClasse classe = SalleDeClasse.builder()
                .nom(request.getNom())
                .niveau(request.getNiveau())
                .effectif(0)
                .capacityMax(request.getCapacityMax())
                .section(section)
                .isActive(true)
                .build();

        SalleDeClasse savedClasse = classeRepository.save(classe);
        log.info("Classe créée avec succès: {}", savedClasse.getNom());

        return classeMapper.toResponse(savedClasse);
    }

    @Transactional(readOnly = true)
    public List<ClasseDTOs.ClasseResponse> getAllClasses() {
        log.info("Récupération de toutes les classes actives");

        List<SalleDeClasse> classes = classeRepository.findByIsActiveTrue();
        return classes.stream()
                .map(classeMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ClasseDTOs.ClasseDetailResponse getClasseById(Long id) {
        log.info("Récupération de la classe avec l'ID: {}", id);

        SalleDeClasse classe = classeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Classe introuvable avec l'ID: " + id));

        return classeMapper.toDetailResponse(classe);
    }

    @Transactional(readOnly = true)
    public List<ClasseDTOs.ClasseResponse> getClassesBySection(Long idSection) {
        log.info("Récupération des classes de la section: {}", idSection);

        List<SalleDeClasse> classes = classeRepository.findBySectionIdSectionAndIsActiveTrue(idSection);
        return classes.stream()
                .map(classeMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ClasseDTOs.ClasseResponse> getClassesBySectionAndNiveau(Long idSection, SalleDeClasse.Niveau niveau) {
        log.info("Récupération des classes de la section {} et niveau {}", idSection, niveau);

        List<SalleDeClasse> classes = classeRepository.findBySectionAndNiveau(idSection, niveau);
        return classes.stream()
                .map(classeMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ClasseDTOs.ClasseStatsResponse getClasseStats(Long id) {
        log.info("Récupération des statistiques de la classe: {}", id);

        SalleDeClasse classe = classeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Classe introuvable avec l'ID: " + id));

        return classeMapper.toStatsResponse(classe);
    }

    @Transactional
    public ClasseDTOs.ClasseResponse updateClasse(Long id, ClasseDTOs.ClasseUpdateRequest request) {
        log.info("Mise à jour de la classe avec l'ID: {}", id);

        SalleDeClasse classe = classeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Classe introuvable avec l'ID: " + id));

        if (request.getNom() != null) {
            classe.setNom(request.getNom());
        }

        if (request.getNiveau() != null) {
            classe.setNiveau(request.getNiveau());
        }

        if (request.getCapacityMax() != null) {
            // Vérifier que la nouvelle capacité n'est pas inférieure à l'effectif actuel
            if (request.getCapacityMax() < classe.getEffectif()) {
                throw new ConflictException("La nouvelle capacité ne peut pas être inférieure à l'effectif actuel");
            }
            classe.setCapacityMax(request.getCapacityMax());
        }

        if (request.getIdSection() != null && !request.getIdSection().equals(classe.getSection().getIdSection())) {
            Section nouvelleSection = sectionRepository.findById(request.getIdSection())
                    .orElseThrow(() -> new NotFoundException("Section introuvable avec l'ID: " + request.getIdSection()));

            if (!nouvelleSection.getIsActive()) {
                throw new ConflictException("Impossible de déplacer vers une section inactive");
            }

            classe.setSection(nouvelleSection);
        }

        if (request.getIsActive() != null) {
            if (!request.getIsActive() && classe.getEffectif() > 0) {
                throw new ConflictException("Impossible de désactiver une classe qui contient des élèves");
            }
            classe.setIsActive(request.getIsActive());
        }

        SalleDeClasse updatedClasse = classeRepository.save(classe);
        log.info("Classe mise à jour avec succès: {}", updatedClasse.getNom());

        return classeMapper.toResponse(updatedClasse);
    }

    @Transactional
    public void deleteClasse(Long id) {
        log.info("Suppression logique de la classe avec l'ID: {}", id);

        SalleDeClasse classe = classeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Classe introuvable avec l'ID: " + id));

        if (classe.getEffectif() > 0) {
            throw new ConflictException("Impossible de supprimer une classe qui contient des élèves");
        }

        if (!classe.getEnseignants().isEmpty()) {
            throw new ConflictException("Impossible de supprimer une classe qui a des enseignants assignés");
        }

        classe.setIsActive(false);
        classeRepository.save(classe);

        log.info("Classe supprimée avec succès: {}", classe.getNom());
    }

    @Transactional
    public void activateClasse(Long id) {
        log.info("Activation de la classe avec l'ID: {}", id);

        SalleDeClasse classe = classeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Classe introuvable avec l'ID: " + id));

        // Vérifier que la section est active
        if (!classe.getSection().getIsActive()) {
            throw new ConflictException("Impossible d'activer une classe dans une section inactive");
        }

        classe.setIsActive(true);
        classeRepository.save(classe);

        log.info("Classe activée avec succès: {}", classe.getNom());
    }
}