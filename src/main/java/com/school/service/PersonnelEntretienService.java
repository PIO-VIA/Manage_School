package com.school.service;

import com.school.dto.PersonnelEntretienDTOs;
import com.school.entity.PersonnelEntretien;
import com.school.exception.ConflictException;
import com.school.exception.NotFoundException;
import com.school.mapper.PersonnelEntretienMapper;
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
public class PersonnelEntretienService {

    private final PersonnelEntretienRepository personnelEntretienRepository;
    private final PersonnelEntretienMapper personnelEntretienMapper;

    @Transactional
    public PersonnelEntretienDTOs.PersonnelEntretienResponse createPersonnelEntretien(
            PersonnelEntretienDTOs.PersonnelEntretienCreateRequest request) {
        log.info("Création d'un nouveau personnel d'entretien: {} {}", request.getNom(), request.getPrenom());

        // Vérifier que l'email n'existe pas déjà si fourni
        if (request.getEmail() != null && personnelEntretienRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Un personnel avec cet email existe déjà: " + request.getEmail());
        }

        PersonnelEntretien personnel = PersonnelEntretien.builder()
                .nom(request.getNom())
                .prenom(request.getPrenom())
                .sexe(request.getSexe())
                .statut(request.getStatut())
                .telephone1(request.getTelephone1())
                .telephone2(request.getTelephone2())
                .email(request.getEmail())
                .datePriseService(request.getDatePriseService())
                .lieuService(request.getLieuService())
                .isActive(true)
                .build();

        PersonnelEntretien savedPersonnel = personnelEntretienRepository.save(personnel);
        log.info("Personnel d'entretien créé avec succès: {} {}", savedPersonnel.getNom(), savedPersonnel.getPrenom());

        return personnelEntretienMapper.toResponse(savedPersonnel);
    }

    @Transactional(readOnly = true)
    public List<PersonnelEntretienDTOs.PersonnelEntretienResponse> getAllPersonnelEntretien() {
        log.info("Récupération de tout le personnel d'entretien actif");

        List<PersonnelEntretien> personnel = personnelEntretienRepository.findByIsActiveTrue();
        return personnel.stream()
                .map(personnelEntretienMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PersonnelEntretienDTOs.PersonnelEntretienDetailResponse getPersonnelEntretienById(Long id) {
        log.info("Récupération du personnel d'entretien avec l'ID: {}", id);

        PersonnelEntretien personnel = personnelEntretienRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Personnel d'entretien introuvable avec l'ID: " + id));

        return personnelEntretienMapper.toDetailResponse(personnel);
    }

    @Transactional
    public PersonnelEntretienDTOs.PersonnelEntretienResponse updatePersonnelEntretien(
            Long id, PersonnelEntretienDTOs.PersonnelEntretienUpdateRequest request) {
        log.info("Mise à jour du personnel d'entretien avec l'ID: {}", id);

        PersonnelEntretien personnel = personnelEntretienRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Personnel d'entretien introuvable avec l'ID: " + id));

        // Vérifier l'email si changé
        if (request.getEmail() != null && !request.getEmail().equals(personnel.getEmail())) {
            if (personnelEntretienRepository.existsByEmail(request.getEmail())) {
                throw new ConflictException("Un personnel avec cet email existe déjà: " + request.getEmail());
            }
            personnel.setEmail(request.getEmail());
        }

        // Mise à jour des champs
        if (request.getNom() != null) {
            personnel.setNom(request.getNom());
        }
        if (request.getPrenom() != null) {
            personnel.setPrenom(request.getPrenom());
        }
        if (request.getSexe() != null) {
            personnel.setSexe(request.getSexe());
        }
        if (request.getStatut() != null) {
            personnel.setStatut(request.getStatut());
        }
        if (request.getTelephone1() != null) {
            personnel.setTelephone1(request.getTelephone1());
        }
        if (request.getTelephone2() != null) {
            personnel.setTelephone2(request.getTelephone2());
        }
        if (request.getDatePriseService() != null) {
            personnel.setDatePriseService(request.getDatePriseService());
        }
        if (request.getLieuService() != null) {
            personnel.setLieuService(request.getLieuService());
        }
        if (request.getIsActive() != null) {
            personnel.setIsActive(request.getIsActive());
        }

        PersonnelEntretien updatedPersonnel = personnelEntretienRepository.save(personnel);
        log.info("Personnel d'entretien mis à jour avec succès: {} {}",
                updatedPersonnel.getNom(), updatedPersonnel.getPrenom());

        return personnelEntretienMapper.toResponse(updatedPersonnel);
    }

    @Transactional
    public void deletePersonnelEntretien(Long id) {
        log.info("Suppression logique du personnel d'entretien avec l'ID: {}", id);

        PersonnelEntretien personnel = personnelEntretienRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Personnel d'entretien introuvable avec l'ID: " + id));

        personnel.setIsActive(false);
        personnelEntretienRepository.save(personnel);

        log.info("Personnel d'entretien supprimé avec succès: {} {}", personnel.getNom(), personnel.getPrenom());
    }
}