package com.school.service;

import com.school.dto.MatiereDTOs;
import com.school.entity.Matiere;
import com.school.exception.ConflictException;
import com.school.exception.NotFoundException;
import com.school.mapper.MatiereMapper;
import com.school.repository.ComposerRepository;
import com.school.repository.EnseignerRepository;
import com.school.repository.MatiereRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MatiereService {

    private final MatiereRepository matiereRepository;
    private final EnseignerRepository enseignerRepository;
    private final ComposerRepository composerRepository;
    private final MatiereMapper matiereMapper;

    @Transactional
    public MatiereDTOs.MatiereResponse createMatiere(MatiereDTOs.MatiereCreateRequest request) {
        log.info("Création d'une nouvelle matière: {}", request.getNom());

        // Vérifier si une matière avec ce nom existe déjà
        if (matiereRepository.existsByNom(request.getNom())) {
            throw new ConflictException("Une matière avec ce nom existe déjà: " + request.getNom());
        }

        Matiere matiere = Matiere.builder()
                .nom(request.getNom())
                .coefficient(request.getCoefficient())
                .description(request.getDescription())
                .isActive(true)
                .build();

        Matiere savedMatiere = matiereRepository.save(matiere);
        log.info("Matière créée avec succès: {}", savedMatiere.getNom());

        return matiereMapper.toResponse(savedMatiere);
    }

    @Transactional(readOnly = true)
    public List<MatiereDTOs.MatiereResponse> getAllMatieres() {
        log.info("Récupération de toutes les matières actives");

        List<Matiere> matieres = matiereRepository.findByIsActiveTrue();
        return matieres.stream()
                .map(matiereMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MatiereDTOs.MatiereDetailResponse getMatiereById(Long id) {
        log.info("Récupération de la matière avec l'ID: {}", id);

        Matiere matiere = matiereRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Matière introuvable avec l'ID: " + id));

        return matiereMapper.toDetailResponse(matiere);
    }

    @Transactional(readOnly = true)
    public MatiereDTOs.MatiereStatsResponse getMatiereStats(Long id) {
        log.info("Récupération des statistiques de la matière: {}", id);

        Matiere matiere = matiereRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Matière introuvable avec l'ID: " + id));

        return matiereMapper.toStatsResponse(matiere);
    }

    @Transactional(readOnly = true)
    public List<MatiereDTOs.MatiereResponse> searchMatieres(String nom) {
        log.info("Recherche de matières avec le nom: {}", nom);

        List<Matiere> matieres = matiereRepository.findByIsActiveTrue();
        return matieres.stream()
                .filter(matiere -> matiere.getNom().toLowerCase().contains(nom.toLowerCase()))
                .map(matiereMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MatiereDTOs.MatiereResponse> getMatieresByCoefficient(Integer coefficient) {
        log.info("Récupération des matières avec coefficient: {}", coefficient);

        List<Matiere> matieres = matiereRepository.findByIsActiveTrue();
        return matieres.stream()
                .filter(matiere -> matiere.getCoefficient().equals(coefficient))
                .map(matiereMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public MatiereDTOs.MatiereResponse updateMatiere(Long id, MatiereDTOs.MatiereUpdateRequest request) {
        log.info("Mise à jour de la matière avec l'ID: {}", id);

        Matiere matiere = matiereRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Matière introuvable avec l'ID: " + id));

        // Vérifier si le nouveau nom existe déjà (si changé)
        if (request.getNom() != null && !request.getNom().equals(matiere.getNom())) {
            if (matiereRepository.existsByNom(request.getNom())) {
                throw new ConflictException("Une matière avec ce nom existe déjà: " + request.getNom());
            }
            matiere.setNom(request.getNom());
        }

        if (request.getCoefficient() != null) {
            matiere.setCoefficient(request.getCoefficient());
        }

        if (request.getDescription() != null) {
            matiere.setDescription(request.getDescription());
        }

        if (request.getIsActive() != null) {
            matiere.setIsActive(request.getIsActive());
        }

        Matiere updatedMatiere = matiereRepository.save(matiere);
        log.info("Matière mise à jour avec succès: {}", updatedMatiere.getNom());

        return matiereMapper.toResponse(updatedMatiere);
    }

    @Transactional
    public void deleteMatiere(Long id) {
        log.info("Suppression logique de la matière avec l'ID: {}", id);

        Matiere matiere = matiereRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Matière introuvable avec l'ID: " + id));

        // Vérifier s'il y a des enseignants assignés ou des notes
        long nombreEnseignants = enseignerRepository.findByIdMatiereAndIsActiveTrue(id).size();
        long nombreNotes = composerRepository.findByIdMatiere(id).size();

        if (nombreEnseignants > 0 || nombreNotes > 0) {
            throw new ConflictException("Impossible de supprimer cette matière car elle est utilisée par des enseignants ou contient des notes");
        }

        matiere.setIsActive(false);
        matiereRepository.save(matiere);

        log.info("Matière supprimée avec succès: {}", matiere.getNom());
    }

    @Transactional
    public void activateMatiere(Long id) {
        log.info("Activation de la matière avec l'ID: {}", id);

        Matiere matiere = matiereRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Matière introuvable avec l'ID: " + id));

        matiere.setIsActive(true);
        matiereRepository.save(matiere);

        log.info("Matière activée avec succès: {}", matiere.getNom());
    }

    @Transactional
    public void deactivateMatiere(Long id) {
        log.info("Désactivation de la matière avec l'ID: {}", id);

        Matiere matiere = matiereRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Matière introuvable avec l'ID: " + id));

        matiere.setIsActive(false);
        matiereRepository.save(matiere);

        // Désactiver toutes les attributions enseignant-matière
        var enseignements = enseignerRepository.findByIdMatiereAndIsActiveTrue(id);
        enseignements.forEach(enseigner -> enseigner.setIsActive(false));
        enseignerRepository.saveAll(enseignements);

        log.info("Matière désactivée avec succès: {}", matiere.getNom());
    }

    @Transactional(readOnly = true)
    public Double calculateMoyenneMatiere(Long id, String matricule) {
        log.info("Calcul de la moyenne de l'élève {} pour la matière {}", matricule, id);

        return composerRepository.calculateAverageNote(matricule, id);
    }

    @Transactional(readOnly = true)
    public List<MatiereDTOs.MatiereResponse> getMatieresByEnseignant(Long enseignantId) {
        log.info("Récupération des matières enseignées par l'enseignant: {}", enseignantId);

        var enseignements = enseignerRepository.findByIdMaitreAndIsActiveTrue(enseignantId);
        return enseignements.stream()
                .map(enseigner -> enseigner.getMatiere())
                .filter(matiere -> matiere.getIsActive())
                .map(matiereMapper::toResponse)
                .collect(Collectors.toList());
    }
}