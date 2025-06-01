package com.school.service;

import com.school.dto.MaterielDTOs;
import com.school.entity.Materiel;
import com.school.entity.PersonnelEntretien;
import com.school.exception.NotFoundException;
import com.school.mapper.MaterielMapper;
import com.school.repository.MaterielRepository;
import com.school.repository.PersonnelEntretienRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MaterielService {

    private final MaterielRepository materielRepository;
    private final PersonnelEntretienRepository personnelEntretienRepository;
    private final MaterielMapper materielMapper;

    @Transactional
    public MaterielDTOs.MaterielResponse createMateriel(MaterielDTOs.MaterielCreateRequest request) {
        log.info("Création d'un nouveau matériel: {}", request.getNom());

        // Vérifier que le personnel d'entretien existe
        PersonnelEntretien personnelEntretien = personnelEntretienRepository.findById(request.getIdEntretien())
                .orElseThrow(() -> new NotFoundException("Personnel d'entretien introuvable avec l'ID: " + request.getIdEntretien()));

        Materiel materiel = Materiel.builder()
                .nom(request.getNom())
                .quantite(request.getQuantite())
                .etat(request.getEtat())
                .description(request.getDescription())
                .personnelEntretien(personnelEntretien)
                .isActive(true)
                .build();

        Materiel savedMateriel = materielRepository.save(materiel);
        log.info("Matériel créé avec succès: {}", savedMateriel.getNom());

        return materielMapper.toResponse(savedMateriel);
    }

    @Transactional(readOnly = true)
    public List<MaterielDTOs.MaterielResponse> getAllMateriels() {
        log.info("Récupération de tous les matériels actifs");

        List<Materiel> materiels = materielRepository.findByIsActiveTrue();
        return materiels.stream()
                .map(materielMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MaterielDTOs.MaterielResponse getMaterielById(Long id) {
        log.info("Récupération du matériel avec l'ID: {}", id);

        Materiel materiel = materielRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Matériel introuvable avec l'ID: " + id));

        return materielMapper.toResponse(materiel);
    }

    @Transactional(readOnly = true)
    public List<MaterielDTOs.MaterielResponse> getMaterielsByPersonnel(Long idPersonnelEntretien) {
        log.info("Récupération des matériels du personnel: {}", idPersonnelEntretien);

        List<Materiel> materiels = materielRepository.findByPersonnelEntretienIdEntretienAndIsActiveTrue(idPersonnelEntretien);
        return materiels.stream()
                .map(materielMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MaterielDTOs.MaterielResponse> getMaterielsByEtat(Materiel.EtatMateriel etat) {
        log.info("Récupération des matériels avec l'état: {}", etat);

        List<Materiel> materiels = materielRepository.findByEtatAndIsActiveTrue(etat);
        return materiels.stream()
                .map(materielMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MaterielDTOs.MaterielStatsResponse getStatistiquesMateriels() {
        log.info("Calcul des statistiques des matériels");

        List<Materiel> materiels = materielRepository.findByIsActiveTrue();
        return materielMapper.toStatsResponse(materiels);
    }

    @Transactional
    public MaterielDTOs.MaterielResponse updateMateriel(Long id, MaterielDTOs.MaterielUpdateRequest request) {
        log.info("Mise à jour du matériel avec l'ID: {}", id);

        Materiel materiel = materielRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Matériel introuvable avec l'ID: " + id));

        if (request.getNom() != null) {
            materiel.setNom(request.getNom());
        }

        if (request.getQuantite() != null) {
            materiel.setQuantite(request.getQuantite());
        }

        if (request.getEtat() != null) {
            materiel.setEtat(request.getEtat());
        }

        if (request.getDescription() != null) {
            materiel.setDescription(request.getDescription());
        }

        if (request.getIdEntretien() != null) {
            PersonnelEntretien personnelEntretien = personnelEntretienRepository.findById(request.getIdEntretien())
                    .orElseThrow(() -> new NotFoundException("Personnel d'entretien introuvable avec l'ID: " + request.getIdEntretien()));
            materiel.setPersonnelEntretien(personnelEntretien);
        }

        if (request.getIsActive() != null) {
            materiel.setIsActive(request.getIsActive());
        }

        Materiel updatedMateriel = materielRepository.save(materiel);
        log.info("Matériel mis à jour avec succès: {}", updatedMateriel.getNom());

        return materielMapper.toResponse(updatedMateriel);
    }

    @Transactional
    public void deleteMateriel(Long id) {
        log.info("Suppression logique du matériel avec l'ID: {}", id);

        Materiel materiel = materielRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Matériel introuvable avec l'ID: " + id));

        materiel.setIsActive(false);
        materielRepository.save(materiel);

        log.info("Matériel supprimé avec succès: {}", materiel.getNom());
    }

    @Transactional
    public MaterielDTOs.MaterielResponse changerEtatMateriel(Long id, Materiel.EtatMateriel nouvelEtat) {
        log.info("Changement d'état du matériel {} vers {}", id, nouvelEtat);

        Materiel materiel = materielRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Matériel introuvable avec l'ID: " + id));

        materiel.setEtat(nouvelEtat);
        Materiel updatedMateriel = materielRepository.save(materiel);

        log.info("État du matériel changé avec succès: {} -> {}", materiel.getNom(), nouvelEtat);

        return materielMapper.toResponse(updatedMateriel);
    }
}