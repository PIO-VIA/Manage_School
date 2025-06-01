package com.school.service;

import com.school.dto.EleveDTOs;
import com.school.entity.Eleve;
import com.school.entity.SalleDeClasse;
import com.school.entity.Section;
import com.school.exception.ConflictException;
import com.school.exception.NotFoundException;
import com.school.mapper.EleveMapper;
import com.school.repository.EleveRepository;
import com.school.repository.SalleDeClasseRepository;
import com.school.repository.SectionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EleveService {

    private final EleveRepository eleveRepository;
    private final SalleDeClasseRepository salleDeClasseRepository;
    private final SectionRepository sectionRepository;
    private final EleveMapper eleveMapper;

    @Transactional
    public EleveDTOs.EleveResponse createEleve(EleveDTOs.EleveCreateRequest request) {
        log.info("Création d'un nouvel élève avec matricule: {}", request.getMatricule());

        // Vérifier si un élève avec ce matricule existe déjà
        if (eleveRepository.existsByMatricule(request.getMatricule())) {
            throw new ConflictException("Un élève avec ce matricule existe déjà: " + request.getMatricule());
        }

        // Vérifier que la classe existe
        SalleDeClasse salleDeClasse = salleDeClasseRepository.findById(request.getIdClasse())
                .orElseThrow(() -> new NotFoundException("Classe introuvable avec l'ID: " + request.getIdClasse()));

        // Vérifier que la section existe
        Section section = sectionRepository.findById(request.getIdSection())
                .orElseThrow(() -> new NotFoundException("Section introuvable avec l'ID: " + request.getIdSection()));

        // Vérifier que la classe appartient à la section
        if (!salleDeClasse.getSection().getIdSection().equals(section.getIdSection())) {
            throw new ConflictException("La classe sélectionnée n'appartient pas à la section spécifiée");
        }

        // Vérifier la capacité de la classe
        Integer effectifActuel = salleDeClasseRepository.countActiveEleves(request.getIdClasse());
        if (effectifActuel >= salleDeClasse.getCapacityMax()) {
            throw new ConflictException("La classe a atteint sa capacité maximale");
        }

        Eleve eleve = Eleve.builder()
                .matricule(request.getMatricule())
                .nom(request.getNom())
                .prenom(request.getPrenom())
                .dateNaissance(request.getDateNaissance())
                .sexe(request.getSexe())
                .nomTuteur(request.getNomTuteur())
                .numero1Tuteur(request.getNumero1Tuteur())
                .numero2Tuteur(request.getNumero2Tuteur())
                .statut(request.getStatut())
                .adresse(request.getAdresse())
                .emailTuteur(request.getEmailTuteur())
                .salleDeClasse(salleDeClasse)
                .section(section)
                .isActive(true)
                .build();

        Eleve savedEleve = eleveRepository.save(eleve);

        // Mettre à jour l'effectif de la classe
        salleDeClasse.setEffectif(effectifActuel + 1);
        salleDeClasseRepository.save(salleDeClasse);

        log.info("Élève créé avec succès: {}", savedEleve.getMatricule());

        return eleveMapper.toResponse(savedEleve);
    }

    @Transactional(readOnly = true)
    public Page<EleveDTOs.EleveResponse> getAllEleves(Pageable pageable) {
        log.info("Récupération de tous les élèves actifs - Page: {}, Taille: {}",
                pageable.getPageNumber(), pageable.getPageSize());

        Page<Eleve> eleves = eleveRepository.findByIsActiveTrue(pageable);
        return eleves.map(eleveMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public EleveDTOs.EleveDetailResponse getEleveByMatricule(String matricule) {
        log.info("Récupération de l'élève avec matricule: {}", matricule);

        Eleve eleve = eleveRepository.findById(matricule)
                .orElseThrow(() -> new NotFoundException("Élève introuvable avec le matricule: " + matricule));

        return eleveMapper.toDetailResponse(eleve);
    }

    @Transactional(readOnly = true)
    public List<EleveDTOs.EleveResponse> getElevesByClasse(Long idClasse) {
        log.info("Récupération des élèves de la classe: {}", idClasse);

        List<Eleve> eleves = eleveRepository.findBySalleDeClasseIdClasseAndIsActiveTrue(idClasse);
        return eleves.stream()
                .map(eleveMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EleveDTOs.EleveResponse> getElevesBySection(Long idSection) {
        log.info("Récupération des élèves de la section: {}", idSection);

        List<Eleve> eleves = eleveRepository.findBySectionIdSectionAndIsActiveTrue(idSection);
        return eleves.stream()
                .map(eleveMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<EleveDTOs.EleveResponse> searchEleves(String keyword, Pageable pageable) {
        log.info("Recherche d'élèves avec le mot-clé: {}", keyword);

        Page<Eleve> eleves = eleveRepository.searchEleves(keyword, pageable);
        return eleves.map(eleveMapper::toResponse);
    }

    @Transactional
    public EleveDTOs.EleveResponse updateEleve(String matricule, EleveDTOs.EleveUpdateRequest request) {
        log.info("Mise à jour de l'élève avec matricule: {}", matricule);

        Eleve eleve = eleveRepository.findById(matricule)
                .orElseThrow(() -> new NotFoundException("Élève introuvable avec le matricule: " + matricule));

        SalleDeClasse ancienneClasse = eleve.getSalleDeClasse();

        // Mise à jour des champs
        if (request.getNom() != null) {
            eleve.setNom(request.getNom());
        }
        if (request.getPrenom() != null) {
            eleve.setPrenom(request.getPrenom());
        }
        if (request.getDateNaissance() != null) {
            eleve.setDateNaissance(request.getDateNaissance());
        }
        if (request.getSexe() != null) {
            eleve.setSexe(request.getSexe());
        }
        if (request.getNomTuteur() != null) {
            eleve.setNomTuteur(request.getNomTuteur());
        }
        if (request.getNumero1Tuteur() != null) {
            eleve.setNumero1Tuteur(request.getNumero1Tuteur());
        }
        if (request.getNumero2Tuteur() != null) {
            eleve.setNumero2Tuteur(request.getNumero2Tuteur());
        }
        if (request.getStatut() != null) {
            eleve.setStatut(request.getStatut());
        }
        if (request.getAdresse() != null) {
            eleve.setAdresse(request.getAdresse());
        }
        if (request.getEmailTuteur() != null) {
            eleve.setEmailTuteur(request.getEmailTuteur());
        }
        if (request.getIsActive() != null) {
            eleve.setIsActive(request.getIsActive());
        }

        // Changement de classe
        if (request.getIdClasse() != null && !request.getIdClasse().equals(ancienneClasse.getIdClasse())) {
            SalleDeClasse nouvelleClasse = salleDeClasseRepository.findById(request.getIdClasse())
                    .orElseThrow(() -> new NotFoundException("Classe introuvable avec l'ID: " + request.getIdClasse()));

            // Vérifier la capacité de la nouvelle classe
            Integer effectifNouvelle = salleDeClasseRepository.countActiveEleves(request.getIdClasse());
            if (effectifNouvelle >= nouvelleClasse.getCapacityMax()) {
                throw new ConflictException("La nouvelle classe a atteint sa capacité maximale");
            }

            eleve.setSalleDeClasse(nouvelleClasse);

            // Mettre à jour les effectifs
            ancienneClasse.setEffectif(ancienneClasse.getEffectif() - 1);
            nouvelleClasse.setEffectif(effectifNouvelle + 1);
            salleDeClasseRepository.save(ancienneClasse);
            salleDeClasseRepository.save(nouvelleClasse);
        }

        // Changement de section
        if (request.getIdSection() != null && !request.getIdSection().equals(eleve.getSection().getIdSection())) {
            Section nouvelleSection = sectionRepository.findById(request.getIdSection())
                    .orElseThrow(() -> new NotFoundException("Section introuvable avec l'ID: " + request.getIdSection()));

            eleve.setSection(nouvelleSection);
        }

        Eleve updatedEleve = eleveRepository.save(eleve);
        log.info("Élève mis à jour avec succès: {}", updatedEleve.getMatricule());

        return eleveMapper.toResponse(updatedEleve);
    }

    @Transactional
    public void deleteEleve(String matricule) {
        log.info("Suppression logique de l'élève avec matricule: {}", matricule);

        Eleve eleve = eleveRepository.findById(matricule)
                .orElseThrow(() -> new NotFoundException("Élève introuvable avec le matricule: " + matricule));

        eleve.setIsActive(false);
        eleveRepository.save(eleve);

        // Mettre à jour l'effectif de la classe
        SalleDeClasse classe = eleve.getSalleDeClasse();
        classe.setEffectif(classe.getEffectif() - 1);
        salleDeClasseRepository.save(classe);

        log.info("Élève supprimé avec succès: {}", matricule);
    }

    @Transactional
    public void activateEleve(String matricule) {
        log.info("Activation de l'élève avec matricule: {}", matricule);

        Eleve eleve = eleveRepository.findById(matricule)
                .orElseThrow(() -> new NotFoundException("Élève introuvable avec le matricule: " + matricule));

        eleve.setIsActive(true);
        eleveRepository.save(eleve);

        log.info("Élève activé avec succès: {}", matricule);
    }
}