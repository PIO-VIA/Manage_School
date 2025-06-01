package com.school.service;

import com.school.dto.DossierDisciplineDTOs;
import com.school.entity.DossierDiscipline;
import com.school.entity.Eleve;
import com.school.entity.PersonnelAdministratif;
import com.school.exception.NotFoundException;
import com.school.mapper.DossierDisciplineMapper;
import com.school.repository.DossierDisciplineRepository;
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
public class DossierDisciplineService {

    private final DossierDisciplineRepository dossierDisciplineRepository;
    private final EleveRepository eleveRepository;
    private final PersonnelAdministratifRepository personnelAdministratifRepository;
    private final DossierDisciplineMapper dossierDisciplineMapper;

    @Transactional
    public DossierDisciplineDTOs.DossierDisciplineResponse createDossierDiscipline(
            DossierDisciplineDTOs.DossierDisciplineCreateRequest request) {
        log.info("Création d'un nouveau dossier disciplinaire pour l'élève: {}", request.getMatricule());

        // Vérifier que l'élève existe
        Eleve eleve = eleveRepository.findById(request.getMatricule())
                .orElseThrow(() -> new NotFoundException("Élève introuvable avec le matricule: " + request.getMatricule()));

        // Vérifier que l'administrateur existe
        PersonnelAdministratif admin = personnelAdministratifRepository.findById(request.getIdAdmin())
                .orElseThrow(() -> new NotFoundException("Personnel administratif introuvable avec l'ID: " + request.getIdAdmin()));

        DossierDiscipline dossier = DossierDiscipline.builder()
                .convocation(request.getConvocation())
                .etat(request.getEtat())
                .sanction(request.getSanction())
                .dateIncident(request.getDateIncident())
                .descriptionIncident(request.getDescriptionIncident())
                .eleve(eleve)
                .personnelAdministratif(admin)
                .isActive(true)
                .build();

        DossierDiscipline savedDossier = dossierDisciplineRepository.save(dossier);
        log.info("Dossier disciplinaire créé avec succès pour l'élève: {}", request.getMatricule());

        return dossierDisciplineMapper.toResponse(savedDossier);
    }

    @Transactional(readOnly = true)
    public List<DossierDisciplineDTOs.DossierDisciplineResponse> getAllDossiersDiscipline() {
        log.info("Récupération de tous les dossiers disciplinaires actifs");

        List<DossierDiscipline> dossiers = dossierDisciplineRepository.findByIsActiveTrue();
        return dossiers.stream()
                .map(dossierDisciplineMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DossierDisciplineDTOs.DossierDisciplineDetailResponse getDossierDisciplineById(Long id) {
        log.info("Récupération du dossier disciplinaire avec l'ID: {}", id);

        DossierDiscipline dossier = dossierDisciplineRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Dossier disciplinaire introuvable avec l'ID: " + id));

        return dossierDisciplineMapper.toDetailResponse(dossier);
    }

    @Transactional(readOnly = true)
    public List<DossierDisciplineDTOs.DossierDisciplineResponse> getDossiersByEleve(String matricule) {
        log.info("Récupération des dossiers disciplinaires de l'élève: {}", matricule);

        List<DossierDiscipline> dossiers = dossierDisciplineRepository.findByEleveMatriculeAndIsActiveTrue(matricule);
        return dossiers.stream()
                .map(dossierDisciplineMapper::toResponse)
                .sorted((d1, d2) -> d2.getDateIncident().compareTo(d1.getDateIncident())) // Plus récents en premier
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DossierDisciplineDTOs.DossierDisciplineResponse> getDossiersByEtat(
            com.school.entity.DossierExamen.EtatDossier etat) {
        log.info("Récupération des dossiers disciplinaires avec l'état: {}", etat);

        List<DossierDiscipline> dossiers = dossierDisciplineRepository.findByEtatAndIsActiveTrue(etat);
        return dossiers.stream()
                .map(dossierDisciplineMapper::toResponse)
                .sorted((d1, d2) -> d2.getDateIncident().compareTo(d1.getDateIncident()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DossierDisciplineDTOs.DossierDisciplineStatsResponse getStatistiquesDiscipline() {
        log.info("Calcul des statistiques disciplinaires");

        List<DossierDiscipline> dossiers = dossierDisciplineRepository.findByIsActiveTrue();
        return dossierDisciplineMapper.toStatsResponse(dossiers);
    }

    @Transactional
    public DossierDisciplineDTOs.DossierDisciplineResponse updateDossierDiscipline(
            Long id, DossierDisciplineDTOs.DossierDisciplineUpdateRequest request) {
        log.info("Mise à jour du dossier disciplinaire avec l'ID: {}", id);

        DossierDiscipline dossier = dossierDisciplineRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Dossier disciplinaire introuvable avec l'ID: " + id));

        if (request.getConvocation() != null) {
            dossier.setConvocation(request.getConvocation());
        }

        if (request.getEtat() != null) {
            dossier.setEtat(request.getEtat());
        }

        if (request.getSanction() != null) {
            dossier.setSanction(request.getSanction());
        }

        if (request.getDateIncident() != null) {
            dossier.setDateIncident(request.getDateIncident());
        }

        if (request.getDescriptionIncident() != null) {
            dossier.setDescriptionIncident(request.getDescriptionIncident());
        }

        if (request.getIdAdmin() != null) {
            PersonnelAdministratif admin = personnelAdministratifRepository.findById(request.getIdAdmin())
                    .orElseThrow(() -> new NotFoundException("Personnel administratif introuvable avec l'ID: " + request.getIdAdmin()));
            dossier.setPersonnelAdministratif(admin);
        }

        if (request.getIsActive() != null) {
            dossier.setIsActive(request.getIsActive());
        }

        DossierDiscipline updatedDossier = dossierDisciplineRepository.save(dossier);
        log.info("Dossier disciplinaire mis à jour avec succès");

        return dossierDisciplineMapper.toResponse(updatedDossier);
    }

    @Transactional
    public void deleteDossierDiscipline(Long id) {
        log.info("Suppression logique du dossier disciplinaire avec l'ID: {}", id);

        DossierDiscipline dossier = dossierDisciplineRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Dossier disciplinaire introuvable avec l'ID: " + id));

        dossier.setIsActive(false);
        dossierDisciplineRepository.save(dossier);

        log.info("Dossier disciplinaire supprimé avec succès");
    }

    @Transactional
    public DossierDisciplineDTOs.DossierDisciplineResponse cloturerDossier(Long id, String resolution) {
        log.info("Clôture du dossier disciplinaire avec l'ID: {}", id);

        DossierDiscipline dossier = dossierDisciplineRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Dossier disciplinaire introuvable avec l'ID: " + id));

        dossier.setEtat(com.school.entity.DossierExamen.EtatDossier.TERMINE);
        if (resolution != null && !resolution.trim().isEmpty()) {
            dossier.setDescriptionIncident(dossier.getDescriptionIncident() + "\n\nRésolution: " + resolution);
        }

        DossierDiscipline updatedDossier = dossierDisciplineRepository.save(dossier);
        log.info("Dossier disciplinaire clôturé avec succès");

        return dossierDisciplineMapper.toResponse(updatedDossier);
    }
}