package com.school.service;

import com.school.dto.PersonnelAdministratifDTOs;
import com.school.entity.PersonnelAdministratif;
import com.school.exception.BadRequestException;
import com.school.exception.ConflictException;
import com.school.exception.NotFoundException;
import com.school.exception.UnauthorizedException;
import com.school.mapper.PersonnelAdministratifMapper;
import com.school.repository.PersonnelAdministratifRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PersonnelAdministratifService {

    private final PersonnelAdministratifRepository personnelAdministratifRepository;
    private final PersonnelAdministratifMapper personnelAdministratifMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public PersonnelAdministratifDTOs.PersonnelAdministratifResponse createPersonnelAdministratif(
            PersonnelAdministratifDTOs.PersonnelAdministratifCreateRequest request) {
        log.info("Création d'un nouveau personnel administratif: {} {}", request.getNom(), request.getPrenom());

        // Vérifier que l'email n'existe pas déjà
        if (request.getEmail() != null && personnelAdministratifRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Un personnel avec cet email existe déjà: " + request.getEmail());
        }

        PersonnelAdministratif personnel = PersonnelAdministratif.builder()
                .nom(request.getNom())
                .prenom(request.getPrenom())
                .sexe(request.getSexe())
                .statut(request.getStatut())
                .telephone1(request.getTelephone1())
                .telephone2(request.getTelephone2())
                .email(request.getEmail())
                .datePriseService(request.getDatePriseService())
                .motDePasse(passwordEncoder.encode(request.getMotDePasse()))
                .role(request.getRole())
                .isActive(true)
                .build();

        PersonnelAdministratif savedPersonnel = personnelAdministratifRepository.save(personnel);
        log.info("Personnel administratif créé avec succès: {} {}", savedPersonnel.getNom(), savedPersonnel.getPrenom());

        return personnelAdministratifMapper.toResponse(savedPersonnel);
    }

    @Transactional(readOnly = true)
    public List<PersonnelAdministratifDTOs.PersonnelAdministratifResponse> getAllPersonnelAdministratif() {
        log.info("Récupération de tout le personnel administratif actif");

        List<PersonnelAdministratif> personnel = personnelAdministratifRepository.findByIsActiveTrue();
        return personnel.stream()
                .map(personnelAdministratifMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PersonnelAdministratifDTOs.PersonnelAdministratifDetailResponse getPersonnelAdministratifById(Long id) {
        log.info("Récupération du personnel administratif avec l'ID: {}", id);

        PersonnelAdministratif personnel = personnelAdministratifRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Personnel administratif introuvable avec l'ID: " + id));

        return personnelAdministratifMapper.toDetailResponse(personnel);
    }

    @Transactional(readOnly = true)
    public PersonnelAdministratifDTOs.PersonnelAdministratifStatsResponse getStatistiquesPersonnelAdministratif() {
        log.info("Calcul des statistiques du personnel administratif");

        List<PersonnelAdministratif> personnel = personnelAdministratifRepository.findAll();
        return personnelAdministratifMapper.toStatsResponse(personnel);
    }

    @Transactional
    public PersonnelAdministratifDTOs.PersonnelAdministratifResponse updatePersonnelAdministratif(
            Long id, PersonnelAdministratifDTOs.PersonnelAdministratifUpdateRequest request) {
        log.info("Mise à jour du personnel administratif avec l'ID: {}", id);

        PersonnelAdministratif personnel = personnelAdministratifRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Personnel administratif introuvable avec l'ID: " + id));

        // Vérifier que l'utilisateur connecté a les droits pour modifier
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        PersonnelAdministratif currentUser = personnelAdministratifRepository
                .findByEmailAndIsActiveTrue(auth.getName())
                .orElseThrow(() -> new UnauthorizedException("Utilisateur non trouvé"));

        // Seul un SUPER_ADMIN peut modifier un autre SUPER_ADMIN
        if (personnel.getRole() == PersonnelAdministratif.Role.SUPER_ADMIN &&
                currentUser.getRole() != PersonnelAdministratif.Role.SUPER_ADMIN) {
            throw new UnauthorizedException("Seul un SUPER_ADMIN peut modifier un autre SUPER_ADMIN");
        }

        // Vérifier l'email si changé
        if (request.getEmail() != null && !request.getEmail().equals(personnel.getEmail())) {
            if (personnelAdministratifRepository.existsByEmail(request.getEmail())) {
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
        if (request.getRole() != null && currentUser.getRole() == PersonnelAdministratif.Role.SUPER_ADMIN) {
            personnel.setRole(request.getRole());
        }
        if (request.getIsActive() != null) {
            personnel.setIsActive(request.getIsActive());
        }

        PersonnelAdministratif updatedPersonnel = personnelAdministratifRepository.save(personnel);
        log.info("Personnel administratif mis à jour avec succès: {} {}",
                updatedPersonnel.getNom(), updatedPersonnel.getPrenom());

        return personnelAdministratifMapper.toResponse(updatedPersonnel);
    }

    @Transactional
    public void deletePersonnelAdministratif(Long id) {
        log.info("Suppression logique du personnel administratif avec l'ID: {}", id);

        PersonnelAdministratif personnel = personnelAdministratifRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Personnel administratif introuvable avec l'ID: " + id));

        // Vérifier que l'utilisateur connecté a les droits pour supprimer
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        PersonnelAdministratif currentUser = personnelAdministratifRepository
                .findByEmailAndIsActiveTrue(auth.getName())
                .orElseThrow(() -> new UnauthorizedException("Utilisateur non trouvé"));

        // Empêcher l'auto-suppression
        if (personnel.getIdAdmin().equals(currentUser.getIdAdmin())) {
            throw new BadRequestException("Vous ne pouvez pas supprimer votre propre compte");
        }

        // Seul un SUPER_ADMIN peut supprimer un autre SUPER_ADMIN
        if (personnel.getRole() == PersonnelAdministratif.Role.SUPER_ADMIN &&
                currentUser.getRole() != PersonnelAdministratif.Role.SUPER_ADMIN) {
            throw new UnauthorizedException("Seul un SUPER_ADMIN peut supprimer un autre SUPER_ADMIN");
        }

        personnel.setIsActive(false);
        personnelAdministratifRepository.save(personnel);

        log.info("Personnel administratif supprimé avec succès: {} {}", personnel.getNom(), personnel.getPrenom());
    }

    @Transactional
    public void activatePersonnelAdministratif(Long id) {
        log.info("Activation du personnel administratif avec l'ID: {}", id);

        PersonnelAdministratif personnel = personnelAdministratifRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Personnel administratif introuvable avec l'ID: " + id));

        personnel.setIsActive(true);
        personnelAdministratifRepository.save(personnel);

        log.info("Personnel administratif activé avec succès: {} {}", personnel.getNom(), personnel.getPrenom());
    }

    @Transactional
    public void changePasswordAdmin(Long id, PersonnelAdministratifDTOs.ChangePasswordAdminRequest request) {
        log.info("Changement de mot de passe pour le personnel administratif avec l'ID: {}", id);

        PersonnelAdministratif personnel = personnelAdministratifRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Personnel administratif introuvable avec l'ID: " + id));

        // Vérifier que les nouveaux mots de passe correspondent
        if (!request.getNouveauMotDePasse().equals(request.getConfirmationMotDePasse())) {
            throw new BadRequestException("Les nouveaux mots de passe ne correspondent pas");
        }

        // Vérifier que l'utilisateur connecté a les droits pour changer le mot de passe
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        PersonnelAdministratif currentUser = personnelAdministratifRepository
                .findByEmailAndIsActiveTrue(auth.getName())
                .orElseThrow(() -> new UnauthorizedException("Utilisateur non trouvé"));

        // Seul un SUPER_ADMIN peut changer le mot de passe d'un autre utilisateur
        if (!personnel.getIdAdmin().equals(currentUser.getIdAdmin()) &&
                currentUser.getRole() != PersonnelAdministratif.Role.SUPER_ADMIN) {
            throw new UnauthorizedException("Seul un SUPER_ADMIN peut changer le mot de passe d'un autre utilisateur");
        }

        personnel.setMotDePasse(passwordEncoder.encode(request.getNouveauMotDePasse()));
        personnelAdministratifRepository.save(personnel);

        log.info("Mot de passe changé avec succès pour le personnel administratif: {}", personnel.getEmail());
    }
}