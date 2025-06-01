package com.school.service;

import com.school.dto.DossierInscriptionDTOs;
import com.school.entity.DossierInscription;
import com.school.entity.Eleve;
import com.school.entity.PersonnelAdministratif;
import com.school.exception.BadRequestException;
import com.school.exception.NotFoundException;
import com.school.mapper.DossierInscriptionMapper;
import com.school.repository.DossierInscriptionRepository;
import com.school.repository.EleveRepository;
import com.school.repository.PersonnelAdministratifRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DossierInscriptionService {

    private final DossierInscriptionRepository dossierInscriptionRepository;
    private final EleveRepository eleveRepository;
    private final PersonnelAdministratifRepository personnelAdministratifRepository;
    private final DossierInscriptionMapper dossierInscriptionMapper;

    @Transactional
    public DossierInscriptionDTOs.DossierInscriptionResponse createDossierInscription(
            DossierInscriptionDTOs.DossierInscriptionCreateRequest request) {
        log.info("Création d'un nouveau dossier d'inscription pour l'élève: {}", request.getMatricule());

        // Vérifier que l'élève existe
        Eleve eleve = eleveRepository.findById(request.getMatricule())
                .orElseThrow(() -> new NotFoundException("Élève introuvable avec le matricule: " + request.getMatricule()));

        // Vérifier que l'administrateur existe
        PersonnelAdministratif admin = personnelAdministratifRepository.findById(request.getIdAdmin())
                .orElseThrow(() -> new NotFoundException("Personnel administratif introuvable avec l'ID: " + request.getIdAdmin()));

        // Calculer le reste
        BigDecimal reste = request.getSommeAPayer().subtract(request.getSommeVersee());

        DossierInscription dossier = DossierInscription.builder()
                .sommeAPayer(request.getSommeAPayer())
                .sommeVersee(request.getSommeVersee())
                .reste(reste)
                .etat(request.getEtat())
                .datePaiement(request.getDatePaiement())
                .anneeScolaire(request.getAnneeScolaire())
                .dateInscription(request.getDateInscription())
                .eleve(eleve)
                .personnelAdministratif(admin)
                .isActive(true)
                .build();

        DossierInscription savedDossier = dossierInscriptionRepository.save(dossier);
        log.info("Dossier d'inscription créé avec succès pour l'élève: {}", request.getMatricule());

        return dossierInscriptionMapper.toResponse(savedDossier);
    }

    @Transactional(readOnly = true)
    public List<DossierInscriptionDTOs.DossierInscriptionResponse> getAllDossiersInscription() {
        log.info("Récupération de tous les dossiers d'inscription actifs");

        List<DossierInscription> dossiers = dossierInscriptionRepository.findByIsActiveTrue();
        return dossiers.stream()
                .map(dossierInscriptionMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DossierInscriptionDTOs.DossierInscriptionResponse getDossierInscriptionById(Long id) {
        log.info("Récupération du dossier d'inscription avec l'ID: {}", id);

        DossierInscription dossier = dossierInscriptionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Dossier d'inscription introuvable avec l'ID: " + id));

        return dossierInscriptionMapper.toResponse(dossier);
    }

    @Transactional(readOnly = true)
    public List<DossierInscriptionDTOs.DossierInscriptionResponse> getDossiersByEleve(String matricule) {
        log.info("Récupération des dossiers d'inscription de l'élève: {}", matricule);

        List<DossierInscription> dossiers = dossierInscriptionRepository.findByEleveMatriculeAndIsActiveTrue(matricule);
        return dossiers.stream()
                .map(dossierInscriptionMapper::toResponse)
                .sorted((d1, d2) -> d2.getDateInscription().compareTo(d1.getDateInscription())) // Plus récents en premier
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DossierInscriptionDTOs.DossierInscriptionResponse> getDossiersByAnneeScolaire(String anneeScolaire) {
        log.info("Récupération des dossiers d'inscription pour l'année scolaire: {}", anneeScolaire);

        List<DossierInscription> dossiers = dossierInscriptionRepository.findByAnneeScolaireAndIsActiveTrue(anneeScolaire);
        return dossiers.stream()
                .map(dossierInscriptionMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DossierInscriptionDTOs.DossierInscriptionResponse> getDossiersByEtat(DossierInscription.EtatInscription etat) {
        log.info("Récupération des dossiers d'inscription avec l'état: {}", etat);

        List<DossierInscription> dossiers = dossierInscriptionRepository.findByEtatAndIsActiveTrue(etat);
        return dossiers.stream()
                .map(dossierInscriptionMapper::toResponse)
                .sorted((d1, d2) -> d2.getDateInscription().compareTo(d1.getDateInscription()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DossierInscriptionDTOs.DossierInscriptionStatsResponse getStatistiquesInscriptions() {
        log.info("Calcul des statistiques des inscriptions");

        List<DossierInscription> dossiers = dossierInscriptionRepository.findByIsActiveTrue();
        return dossierInscriptionMapper.toStatsResponse(dossiers);
    }

    @Transactional
    public DossierInscriptionDTOs.DossierInscriptionResponse updateDossierInscription(
            Long id, DossierInscriptionDTOs.DossierInscriptionUpdateRequest request) {
        log.info("Mise à jour du dossier d'inscription avec l'ID: {}", id);

        DossierInscription dossier = dossierInscriptionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Dossier d'inscription introuvable avec l'ID: " + id));

        if (request.getSommeAPayer() != null) {
            dossier.setSommeAPayer(request.getSommeAPayer());
        }

        if (request.getSommeVersee() != null) {
            dossier.setSommeVersee(request.getSommeVersee());
        }

        if (request.getEtat() != null) {
            dossier.setEtat(request.getEtat());
        }

        if (request.getDatePaiement() != null) {
            dossier.setDatePaiement(request.getDatePaiement());
        }

        if (request.getAnneeScolaire() != null) {
            dossier.setAnneeScolaire(request.getAnneeScolaire());
        }

        if (request.getDateInscription() != null) {
            dossier.setDateInscription(request.getDateInscription());
        }

        if (request.getIdAdmin() != null) {
            PersonnelAdministratif admin = personnelAdministratifRepository.findById(request.getIdAdmin())
                    .orElseThrow(() -> new NotFoundException("Personnel administratif introuvable avec l'ID: " + request.getIdAdmin()));
            dossier.setPersonnelAdministratif(admin);
        }

        if (request.getIsActive() != null) {
            dossier.setIsActive(request.getIsActive());
        }

        // Recalculer le reste et l'état si nécessaire
        dossier.calculerReste();
        updateEtatBasedOnPayment(dossier);

        DossierInscription updatedDossier = dossierInscriptionRepository.save(dossier);
        log.info("Dossier d'inscription mis à jour avec succès");

        return dossierInscriptionMapper.toResponse(updatedDossier);
    }

    @Transactional
    public DossierInscriptionDTOs.DossierInscriptionResponse enregistrerPaiement(
            Long id, DossierInscriptionDTOs.PaiementRequest paiementRequest) {
        log.info("Enregistrement d'un paiement pour le dossier d'inscription: {}", id);

        DossierInscription dossier = dossierInscriptionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Dossier d'inscription introuvable avec l'ID: " + id));

        // Vérifier que le montant ne dépasse pas le reste à payer
        if (paiementRequest.getMontant().compareTo(dossier.getReste()) > 0) {
            throw new BadRequestException("Le montant du paiement ne peut pas dépasser le reste à payer");
        }

        // Mettre à jour les montants
        BigDecimal nouvelleSommeVersee = dossier.getSommeVersee().add(paiementRequest.getMontant());
        dossier.setSommeVersee(nouvelleSommeVersee);
        dossier.setDatePaiement(paiementRequest.getDatePaiement());

        // Recalculer le reste
        dossier.calculerReste();

        // Mettre à jour l'état
        updateEtatBasedOnPayment(dossier);

        DossierInscription updatedDossier = dossierInscriptionRepository.save(dossier);
        log.info("Paiement enregistré avec succès. Nouveau solde: {}", updatedDossier.getReste());

        return dossierInscriptionMapper.toResponse(updatedDossier);
    }

    @Transactional
    public void deleteDossierInscription(Long id) {
        log.info("Suppression logique du dossier d'inscription avec l'ID: {}", id);

        DossierInscription dossier = dossierInscriptionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Dossier d'inscription introuvable avec l'ID: " + id));

        dossier.setIsActive(false);
        dossierInscriptionRepository.save(dossier);

        log.info("Dossier d'inscription supprimé avec succès");
    }

    // Méthode utilitaire pour mettre à jour l'état basé sur les paiements
    private void updateEtatBasedOnPayment(DossierInscription dossier) {
        if (dossier.getReste().compareTo(BigDecimal.ZERO) == 0) {
            dossier.setEtat(DossierInscription.EtatInscription.COMPLETE);
        } else if (dossier.getSommeVersee().compareTo(BigDecimal.ZERO) > 0) {
            dossier.setEtat(DossierInscription.EtatInscription.PARTIELLE);
        }

        // Vérifier si l'inscription a expiré (plus de 6 mois sans paiement complet)
        if (dossier.getDateInscription() != null &&
                dossier.getDateInscription().isBefore(LocalDate.now().minusMonths(6)) &&
                dossier.getReste().compareTo(BigDecimal.ZERO) > 0) {
            dossier.setEtat(DossierInscription.EtatInscription.EXPIREE);
        }
    }
}