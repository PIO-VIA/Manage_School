package com.school.service;

import com.school.dto.AbsenceDTOs;
import com.school.entity.Absence;
import com.school.entity.DossierDiscipline;
import com.school.entity.Eleve;
import com.school.exception.NotFoundException;
import com.school.mapper.AbsenceMapper;
import com.school.repository.AbsenceRepository;
import com.school.repository.DossierDisciplineRepository;
import com.school.repository.EleveRepository;
import com.school.repository.SalleDeClasseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AbsenceService {

    private final AbsenceRepository absenceRepository;
    private final EleveRepository eleveRepository;
    private final DossierDisciplineRepository dossierDisciplineRepository;
    private final SalleDeClasseRepository salleDeClasseRepository;
    private final AbsenceMapper absenceMapper;

    @Transactional
    public AbsenceDTOs.AbsenceResponse createAbsence(AbsenceDTOs.AbsenceCreateRequest request) {
        log.info("Création d'une nouvelle absence pour l'élève: {}", request.getMatricule());

        // Vérifier que l'élève existe
        Eleve eleve = eleveRepository.findById(request.getMatricule())
                .orElseThrow(() -> new NotFoundException("Élève introuvable avec le matricule: " + request.getMatricule()));

        DossierDiscipline dossierDiscipline = null;
        if (request.getIdDiscipline() != null) {
            dossierDiscipline = dossierDisciplineRepository.findById(request.getIdDiscipline())
                    .orElseThrow(() -> new NotFoundException("Dossier discipline introuvable avec l'ID: " + request.getIdDiscipline()));
        }

        Absence absence = Absence.builder()
                .horaire(request.getHoraire())
                .jour(request.getJour())
                .typeAbsence(request.getTypeAbsence())
                .motif(request.getMotif())
                .justifiee(request.getJustifiee())
                .eleve(eleve)
                .dossierDiscipline(dossierDiscipline)
                .isActive(true)
                .build();

        Absence savedAbsence = absenceRepository.save(absence);
        log.info("Absence créée avec succès pour l'élève: {}", request.getMatricule());

        return absenceMapper.toResponse(savedAbsence);
    }

    @Transactional(readOnly = true)
    public List<AbsenceDTOs.AbsenceResponse> getAllAbsences() {
        log.info("Récupération de toutes les absences actives");

        List<Absence> absences = absenceRepository.findByIsActiveTrue();
        return absences.stream()
                .map(absenceMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AbsenceDTOs.AbsenceResponse getAbsenceById(Long id) {
        log.info("Récupération de l'absence avec l'ID: {}", id);

        Absence absence = absenceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Absence introuvable avec l'ID: " + id));

        return absenceMapper.toResponse(absence);
    }

    @Transactional(readOnly = true)
    public List<AbsenceDTOs.AbsenceResponse> getAbsencesByEleve(String matricule) {
        log.info("Récupération des absences de l'élève: {}", matricule);

        List<Absence> absences = absenceRepository.findByEleveMatriculeAndIsActiveTrue(matricule);
        return absences.stream()
                .map(absenceMapper::toResponse)
                .sorted((a1, a2) -> a2.getJour().compareTo(a1.getJour())) // Plus récentes en premier
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AbsenceDTOs.AbsenceResponse> getAbsencesByPeriode(String matricule, LocalDate dateDebut, LocalDate dateFin) {
        log.info("Récupération des absences de l'élève {} entre {} et {}", matricule, dateDebut, dateFin);

        List<Absence> absences = absenceRepository.findByEleveAndDateRange(matricule, dateDebut, dateFin);
        return absences.stream()
                .map(absenceMapper::toResponse)
                .sorted((a1, a2) -> a2.getJour().compareTo(a1.getJour()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AbsenceDTOs.AbsenceStatsResponse getStatistiquesAbsencesEleve(String matricule) {
        log.info("Calcul des statistiques d'absences pour l'élève: {}", matricule);

        Eleve eleve = eleveRepository.findById(matricule)
                .orElseThrow(() -> new NotFoundException("Élève introuvable avec le matricule: " + matricule));

        List<Absence> absences = absenceRepository.findByEleveMatriculeAndIsActiveTrue(matricule);

        return absenceMapper.toStatsResponse(eleve, absences);
    }

    @Transactional(readOnly = true)
    public AbsenceDTOs.AbsenceClasseStatsResponse getStatistiquesAbsencesClasse(Long idClasse) {
        log.info("Calcul des statistiques d'absences pour la classe: {}", idClasse);

        var classe = salleDeClasseRepository.findById(idClasse)
                .orElseThrow(() -> new NotFoundException("Classe introuvable avec l'ID: " + idClasse));

        List<Eleve> elevesClasse = eleveRepository.findBySalleDeClasseIdClasseAndIsActiveTrue(idClasse);

        return absenceMapper.toClasseStatsResponse(classe, elevesClasse);
    }

    @Transactional(readOnly = true)
    public Long countAbsencesNonJustifiees(String matricule) {
        log.info("Comptage des absences non justifiées pour l'élève: {}", matricule);

        return absenceRepository.countAbsencesNonJustifiees(matricule);
    }

    @Transactional
    public AbsenceDTOs.AbsenceResponse updateAbsence(Long id, AbsenceDTOs.AbsenceUpdateRequest request) {
        log.info("Mise à jour de l'absence avec l'ID: {}", id);

        Absence absence = absenceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Absence introuvable avec l'ID: " + id));

        if (request.getHoraire() != null) {
            absence.setHoraire(request.getHoraire());
        }

        if (request.getJour() != null) {
            absence.setJour(request.getJour());
        }

        if (request.getTypeAbsence() != null) {
            absence.setTypeAbsence(request.getTypeAbsence());
        }

        if (request.getMotif() != null) {
            absence.setMotif(request.getMotif());
        }

        if (request.getJustifiee() != null) {
            absence.setJustifiee(request.getJustifiee());
        }

        if (request.getIdDiscipline() != null) {
            DossierDiscipline dossierDiscipline = dossierDisciplineRepository.findById(request.getIdDiscipline())
                    .orElseThrow(() -> new NotFoundException("Dossier discipline introuvable avec l'ID: " + request.getIdDiscipline()));
            absence.setDossierDiscipline(dossierDiscipline);
        }

        if (request.getIsActive() != null) {
            absence.setIsActive(request.getIsActive());
        }

        Absence updatedAbsence = absenceRepository.save(absence);
        log.info("Absence mise à jour avec succès");

        return absenceMapper.toResponse(updatedAbsence);
    }

    @Transactional
    public void deleteAbsence(Long id) {
        log.info("Suppression logique de l'absence avec l'ID: {}", id);

        Absence absence = absenceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Absence introuvable avec l'ID: " + id));

        absence.setIsActive(false);
        absenceRepository.save(absence);

        log.info("Absence supprimée avec succès");
    }

    @Transactional
    public void justifierAbsence(Long id, String motif) {
        log.info("Justification de l'absence avec l'ID: {}", id);

        Absence absence = absenceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Absence introuvable avec l'ID: " + id));

        absence.setJustifiee(true);
        if (motif != null && !motif.trim().isEmpty()) {
            absence.setMotif(motif);
        }

        absenceRepository.save(absence);
        log.info("Absence justifiée avec succès");
    }
}