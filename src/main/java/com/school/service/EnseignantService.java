package com.school.service;

import com.school.dto.EnseignantDTOs;
import com.school.entity.*;
import com.school.exception.ConflictException;
import com.school.exception.NotFoundException;
import com.school.mapper.EnseignantMapper;
import com.school.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EnseignantService {

    private final EnseignantRepository enseignantRepository;
    private final SalleDeClasseRepository salleDeClasseRepository;
    private final SectionRepository sectionRepository;
    private final MatiereRepository matiereRepository;
    private final EnseignerRepository enseignerRepository;
    private final EnseignantMapper enseignantMapper;

    @Transactional
    public EnseignantDTOs.EnseignantResponse createEnseignant(EnseignantDTOs.EnseignantCreateRequest request) {
        log.info("Création d'un nouvel enseignant: {} {}", request.getNom(), request.getPrenom());

        // Vérifier que l'email n'existe pas déjà si fourni
        if (request.getEmail() != null && enseignantRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Un enseignant avec cet email existe déjà: " + request.getEmail());
        }

        // Vérifier que la section existe
        Section section = sectionRepository.findById(request.getIdSection())
                .orElseThrow(() -> new NotFoundException("Section introuvable avec l'ID: " + request.getIdSection()));

        if (!section.getIsActive()) {
            throw new ConflictException("Impossible d'assigner un enseignant à une section inactive");
        }

        SalleDeClasse salleDeClasse = null;
        if (request.getIdClasse() != null) {
            salleDeClasse = salleDeClasseRepository.findById(request.getIdClasse())
                    .orElseThrow(() -> new NotFoundException("Classe introuvable avec l'ID: " + request.getIdClasse()));

            // Vérifier que la classe appartient à la section
            if (!salleDeClasse.getSection().getIdSection().equals(section.getIdSection())) {
                throw new ConflictException("La classe sélectionnée n'appartient pas à la section spécifiée");
            }

            if (!salleDeClasse.getIsActive()) {
                throw new ConflictException("Impossible d'assigner un enseignant à une classe inactive");
            }
        }

        Enseignant enseignant = Enseignant.builder()
                .nom(request.getNom())
                .prenom(request.getPrenom())
                .sexe(request.getSexe())
                .statut(request.getStatut())
                .telephone1(request.getTelephone1())
                .telephone2(request.getTelephone2())
                .email(request.getEmail())
                .datePriseService(request.getDatePriseService())
                .specialite(request.getSpecialite())
                .salleDeClasse(salleDeClasse)
                .section(section)
                .isActive(true)
                .build();

        Enseignant savedEnseignant = enseignantRepository.save(enseignant);
        log.info("Enseignant créé avec succès: {} {}", savedEnseignant.getNom(), savedEnseignant.getPrenom());

        return enseignantMapper.toResponse(savedEnseignant);
    }

    @Transactional(readOnly = true)
    public List<EnseignantDTOs.EnseignantResponse> getAllEnseignants() {
        log.info("Récupération de tous les enseignants actifs");

        List<Enseignant> enseignants = enseignantRepository.findByIsActiveTrue();
        return enseignants.stream()
                .map(enseignantMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EnseignantDTOs.EnseignantDetailResponse getEnseignantById(Long id) {
        log.info("Récupération de l'enseignant avec l'ID: {}", id);

        Enseignant enseignant = enseignantRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Enseignant introuvable avec l'ID: " + id));

        return enseignantMapper.toDetailResponse(enseignant);
    }

    @Transactional(readOnly = true)
    public List<EnseignantDTOs.EnseignantResponse> getEnseignantsBySection(Long idSection) {
        log.info("Récupération des enseignants de la section: {}", idSection);

        List<Enseignant> enseignants = enseignantRepository.findBySectionIdSectionAndIsActiveTrue(idSection);
        return enseignants.stream()
                .map(enseignantMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EnseignantDTOs.EnseignantResponse> getEnseignantsByClasse(Long idClasse) {
        log.info("Récupération des enseignants de la classe: {}", idClasse);

        List<Enseignant> enseignants = enseignantRepository.findBySalleDeClasseIdClasseAndIsActiveTrue(idClasse);
        return enseignants.stream()
                .map(enseignantMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EnseignantDTOs.EnseignantResponse> getEnseignantsBySpecialite(Long idSection, String specialite) {
        log.info("Récupération des enseignants de la section {} avec spécialité: {}", idSection, specialite);

        List<Enseignant> enseignants = enseignantRepository.findBySectionAndSpecialite(idSection, specialite);
        return enseignants.stream()
                .map(enseignantMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EnseignantDTOs.EnseignantStatsResponse getEnseignantStats(Long id) {
        log.info("Récupération des statistiques de l'enseignant: {}", id);

        Enseignant enseignant = enseignantRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Enseignant introuvable avec l'ID: " + id));

        return enseignantMapper.toStatsResponse(enseignant);
    }

    @Transactional
    public EnseignantDTOs.EnseignantResponse updateEnseignant(Long id, EnseignantDTOs.EnseignantUpdateRequest request) {
        log.info("Mise à jour de l'enseignant avec l'ID: {}", id);

        Enseignant enseignant = enseignantRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Enseignant introuvable avec l'ID: " + id));

        // Vérifier l'email si changé
        if (request.getEmail() != null && !request.getEmail().equals(enseignant.getEmail())) {
            if (enseignantRepository.existsByEmail(request.getEmail())) {
                throw new ConflictException("Un enseignant avec cet email existe déjà: " + request.getEmail());
            }
            enseignant.setEmail(request.getEmail());
        }

        // Mise à jour des champs
        if (request.getNom() != null) {
            enseignant.setNom(request.getNom());
        }
        if (request.getPrenom() != null) {
            enseignant.setPrenom(request.getPrenom());
        }
        if (request.getSexe() != null) {
            enseignant.setSexe(request.getSexe());
        }
        if (request.getStatut() != null) {
            enseignant.setStatut(request.getStatut());
        }
        if (request.getTelephone1() != null) {
            enseignant.setTelephone1(request.getTelephone1());
        }
        if (request.getTelephone2() != null) {
            enseignant.setTelephone2(request.getTelephone2());
        }
        if (request.getDatePriseService() != null) {
            enseignant.setDatePriseService(request.getDatePriseService());
        }
        if (request.getSpecialite() != null) {
            enseignant.setSpecialite(request.getSpecialite());
        }
        if (request.getIsActive() != null) {
            enseignant.setIsActive(request.getIsActive());
        }

        // Changement de classe
        if (request.getIdClasse() != null) {
            if (request.getIdClasse() == 0) {
                // Retirer de la classe actuelle
                enseignant.setSalleDeClasse(null);
            } else {
                SalleDeClasse nouvelleClasse = salleDeClasseRepository.findById(request.getIdClasse())
                        .orElseThrow(() -> new NotFoundException("Classe introuvable avec l'ID: " + request.getIdClasse()));

                // Vérifier que la classe appartient à la section de l'enseignant
                if (!nouvelleClasse.getSection().getIdSection().equals(enseignant.getSection().getIdSection())) {
                    throw new ConflictException("La classe sélectionnée n'appartient pas à la section de l'enseignant");
                }

                enseignant.setSalleDeClasse(nouvelleClasse);
            }
        }

        // Changement de section
        if (request.getIdSection() != null && !request.getIdSection().equals(enseignant.getSection().getIdSection())) {
            Section nouvelleSection = sectionRepository.findById(request.getIdSection())
                    .orElseThrow(() -> new NotFoundException("Section introuvable avec l'ID: " + request.getIdSection()));

            enseignant.setSection(nouvelleSection);
            // Retirer de la classe actuelle si elle n'appartient plus à la nouvelle section
            if (enseignant.getSalleDeClasse() != null &&
                    !enseignant.getSalleDeClasse().getSection().getIdSection().equals(nouvelleSection.getIdSection())) {
                enseignant.setSalleDeClasse(null);
            }
        }

        Enseignant updatedEnseignant = enseignantRepository.save(enseignant);
        log.info("Enseignant mis à jour avec succès: {} {}", updatedEnseignant.getNom(), updatedEnseignant.getPrenom());

        return enseignantMapper.toResponse(updatedEnseignant);
    }

    @Transactional
    public void assignMatiere(Long enseignantId, EnseignantDTOs.AssignMatiereRequest request) {
        log.info("Attribution de la matière {} à l'enseignant {}", request.getIdMatiere(), enseignantId);

        Enseignant enseignant = enseignantRepository.findById(enseignantId)
                .orElseThrow(() -> new NotFoundException("Enseignant introuvable avec l'ID: " + enseignantId));

        Matiere matiere = matiereRepository.findById(request.getIdMatiere())
                .orElseThrow(() -> new NotFoundException("Matière introuvable avec l'ID: " + request.getIdMatiere()));

        // Vérifier si l'attribution existe déjà
        if (enseignerRepository.findByEnseignantAndMatiere(enseignantId, request.getIdMatiere()).isPresent()) {
            throw new ConflictException("Cet enseignant enseigne déjà cette matière");
        }

        Enseigner enseigner = Enseigner.builder()
                .idMaitre(enseignantId)
                .idMatiere(request.getIdMatiere())
                .dateOccupation(request.getDateOccupation())
                .isActive(true)
                .build();

        enseignerRepository.save(enseigner);
        log.info("Matière attribuée avec succès à l'enseignant");
    }

    @Transactional
    public void unassignMatiere(Long enseignantId, Long matiereId) {
        log.info("Retrait de la matière {} de l'enseignant {}", matiereId, enseignantId);

        Enseigner enseigner = enseignerRepository.findByEnseignantAndMatiere(enseignantId, matiereId)
                .orElseThrow(() -> new NotFoundException("Attribution enseignant-matière introuvable"));

        enseigner.setIsActive(false);
        enseignerRepository.save(enseigner);

        log.info("Matière retirée avec succès de l'enseignant");
    }

    @Transactional
    public void deleteEnseignant(Long id) {
        log.info("Suppression logique de l'enseignant avec l'ID: {}", id);

        Enseignant enseignant = enseignantRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Enseignant introuvable avec l'ID: " + id));

        enseignant.setIsActive(false);
        enseignantRepository.save(enseignant);

        // Désactiver toutes les attributions de matières
        List<Enseigner> enseignements = enseignerRepository.findByIdMaitreAndIsActiveTrue(id);
        enseignements.forEach(enseigner -> enseigner.setIsActive(false));
        enseignerRepository.saveAll(enseignements);

        log.info("Enseignant supprimé avec succès: {} {}", enseignant.getNom(), enseignant.getPrenom());
    }
}