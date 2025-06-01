package com.school.service;

import com.school.dto.DossierExamenDTOs;
import com.school.entity.DossierExamen;
import com.school.entity.Eleve;
import com.school.entity.PersonnelAdministratif;
import com.school.exception.NotFoundException;
import com.school.mapper.DossierExamenMapper;
import com.school.repository.DossierExamenRepository;
import com.school.repository.EleveRepository;
import com.school.repository.PersonnelAdministratifRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DossierExamenService {

    private final DossierExamenRepository dossierExamenRepository;
    private final EleveRepository eleveRepository;
    private final PersonnelAdministratifRepository personnelAdministratifRepository;
    private final DossierExamenMapper dossierExamenMapper;

    @Transactional
    public DossierExamenDTOs.DossierExamenResponse createDossierExamen(
            DossierExamenDTOs.DossierExamenCreateRequest request) {
        log.info("Création d'un nouveau dossier d'examen pour l'élève: {}", request.getMatricule());

        // Vérifier que l'élève existe
        Eleve eleve = eleveRepository.findById(request.getMatricule())
                .orElseThrow(() -> new NotFoundException("Élève introuvable avec le matricule: " + request.getMatricule()));

        // Vérifier que l'administrateur existe
        PersonnelAdministratif admin = personnelAdministratifRepository.findById(request.getIdAdmin())
                .orElseThrow(() -> new NotFoundException("Personnel administratif introuvable avec l'ID: " + request.getIdAdmin()));

        DossierExamen dossier = DossierExamen.builder()
                .examen(request.getExamen())
                .etat(request.getEtat())
                .dateDepot(request.getDateDepot())
                .observations(request.getObservations())
                .eleve(eleve)
                .personnelAdministratif(admin)
                .isActive(true)
                .build();

        DossierExamen savedDossier = dossierExamenRepository.save(dossier);
        log.info("Dossier d'examen créé avec succès pour l'élève: {}", request.getMatricule());

        return dossierExamenMapper.toResponse(savedDossier);
    }

    @Transactional(readOnly = true)
    public List<DossierExamenDTOs.DossierExamenResponse> getAllDossiersExamen() {
        log.info("Récupération de tous les dossiers d'examen actifs");

        List<DossierExamen> dossiers = dossierExamenRepository.findByIsActiveTrue();
        return dossiers.stream()
                .map(dossierExamenMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DossierExamenDTOs.DossierExamenResponse getDossierExamenById(Long id) {
        log.info("Récupération du dossier d'examen avec l'ID: {}", id);

        DossierExamen dossier = dossierExamenRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Dossier d'examen introuvable avec l'ID: " + id));

        return dossierExamenMapper.toResponse(dossier);
    }

    @Transactional(readOnly = true)
    public List<DossierExamenDTOs.DossierExamenResponse> getDossiersByEleve(String matricule) {
        log.info("Récupération des dossiers d'examen de l'élève: {}", matricule);

        List<DossierExamen> dossiers = dossierExamenRepository.findByEleveMatriculeAndIsActiveTrue(matricule);
        return dossiers.stream()
                .map(dossierExamenMapper::toResponse)
                .sorted((d1, d2) -> d2.getDateDepot().compareTo(d1.getDateDepot())) // Plus récents en premier
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DossierExamenDTOs.DossierExamenResponse> getDossiersByEtat(DossierExamen.EtatDossier etat) {
        log.info("Récupération des dossiers d'examen avec l'état: {}", etat);

        List<DossierExamen> dossiers = dossierExamenRepository.findByEtatAndIsActiveTrue(etat);
        return dossiers.stream()
                .map(dossierExamenMapper::toResponse)
                .sorted((d1, d2) -> d2.getDateDepot().compareTo(d1.getDateDepot()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DossierExamenDTOs.DossierExamenStatsResponse getStatistiquesExamens() {
        log.info("Calcul des statistiques des examens");

        List<DossierExamen> dossiers = dossierExamenRepository.findByIsActiveTrue();
        return dossierExamenMapper.toStatsResponse(dossiers);
    }

    @Transactional
    public DossierExamenDTOs.DossierExamenResponse updateDossierExamen(
            Long id, DossierExamenDTOs.DossierExamenUpdateRequest request) {
        log.info("Mise à jour du dossier d'examen avec l'ID: {}", id);

        DossierExamen dossier = dossierExamenRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Dossier d'examen introuvable avec l'ID: " + id));

        if (request.getExamen() != null) {
            dossier.setExamen(request.getExamen());
        }

        if (request.getEtat() != null) {
            dossier.setEtat(request.getEtat());
        }

        if (request.getDateDepot() != null) {
            dossier.setDateDepot(request.getDateDepot());
        }

        if (request.getObservations() != null) {
            dossier.setObservations(request.getObservations());
        }

        if (request.getIdAdmin() != null) {
            PersonnelAdministratif admin = personnelAdministratifRepository.findById(request.getIdAdmin())
                    .orElseThrow(() -> new NotFoundException("Personnel administratif introuvable avec l'ID: " + request.getIdAdmin()));
            dossier.setPersonnelAdministratif(admin);
        }

        if (request.getIsActive() != null) {
            dossier.setIsActive(request.getIsActive());
        }

        DossierExamen updatedDossier = dossierExamenRepository.save(dossier);
        log.info("Dossier d'examen mis à jour avec succès");

        return dossierExamenMapper.toResponse(updatedDossier);
    }

    @Transactional
    public void deleteDossierExamen(Long id) {
        log.info("Suppression logique du dossier d'examen avec l'ID: {}", id);

        DossierExamen dossier = dossierExamenRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Dossier d'examen introuvable avec l'ID: " + id));

        dossier.setIsActive(false);
        dossierExamenRepository.save(dossier);

        log.info("Dossier d'examen supprimé avec succès");
    }

    @Transactional
    public DossierExamenDTOs.DossierExamenResponse finaliserExamen(Long id, String resultat) {
        log.info("Finalisation du dossier d'examen avec l'ID: {}", id);

        DossierExamen dossier = dossierExamenRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Dossier d'examen introuvable avec l'ID: " + id));

        dossier.setEtat(DossierExamen.EtatDossier.TERMINE);
        if (resultat != null && !resultat.trim().isEmpty()) {
            dossier.setObservations(dossier.getObservations() != null ?
                    dossier.getObservations() + "\n\nRésultat: " + resultat :
                    "Résultat: " + resultat);
        }

        DossierExamen updatedDossier = dossierExamenRepository.save(dossier);
        log.info("Dossier d'examen finalisé avec succès");

        return dossierExamenMapper.toResponse(updatedDossier);
    }
}