package com.school.service;

import com.school.dto.AuthDTOs;
import com.school.entity.PersonnelAdministratif;
import com.school.exception.BadRequestException;
import com.school.exception.ConflictException;
import com.school.exception.UnauthorizedException;
import com.school.repository.PersonnelAdministratifRepository;
import com.school.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final PersonnelAdministratifRepository personnelRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    @Transactional
    public AuthDTOs.LoginResponse login(AuthDTOs.LoginRequest request) {
        log.info("Tentative de connexion pour l'utilisateur: {}", request.getEmail());

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getMotDePasse()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = tokenProvider.generateToken(authentication);

            PersonnelAdministratif personnel = personnelRepository
                    .findByEmailAndIsActiveTrue(request.getEmail())
                    .orElseThrow(() -> new UnauthorizedException("Utilisateur introuvable ou inactif"));

            log.info("Connexion réussie pour l'utilisateur: {}", request.getEmail());

            return AuthDTOs.LoginResponse.builder()
                    .token(token)
                    .id(personnel.getIdAdmin())
                    .nom(personnel.getNom())
                    .prenom(personnel.getPrenom())
                    .email(personnel.getEmail())
                    .role(personnel.getRole())
                    .expiresIn(tokenProvider.getJwtExpirationInMs())
                    .build();

        } catch (Exception e) {
            log.error("Échec de connexion pour l'utilisateur: {}", request.getEmail(), e);
            throw new UnauthorizedException("Email ou mot de passe incorrect");
        }
    }

    @Transactional
    public PersonnelAdministratif register(AuthDTOs.RegisterRequest request) {
        log.info("Tentative d'inscription pour l'utilisateur: {}", request.getEmail());

        if (personnelRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Un utilisateur avec cet email existe déjà");
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

        PersonnelAdministratif savedPersonnel = personnelRepository.save(personnel);
        log.info("Inscription réussie pour l'utilisateur: {}", request.getEmail());

        return savedPersonnel;
    }

    @Transactional
    public void changePassword(AuthDTOs.ChangePasswordRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        PersonnelAdministratif personnel = personnelRepository
                .findByEmailAndIsActiveTrue(email)
                .orElseThrow(() -> new UnauthorizedException("Utilisateur introuvable"));

        // Vérifier l'ancien mot de passe
        if (!passwordEncoder.matches(request.getAncienMotDePasse(), personnel.getMotDePasse())) {
            throw new BadRequestException("Ancien mot de passe incorrect");
        }

        // Vérifier que les nouveaux mots de passe correspondent
        if (!request.getNouveauMotDePasse().equals(request.getConfirmationMotDePasse())) {
            throw new BadRequestException("Les nouveaux mots de passe ne correspondent pas");
        }

        personnel.setMotDePasse(passwordEncoder.encode(request.getNouveauMotDePasse()));
        personnelRepository.save(personnel);

        log.info("Mot de passe changé avec succès pour l'utilisateur: {}", email);
    }

    public PersonnelAdministratif getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        return personnelRepository
                .findByEmailAndIsActiveTrue(email)
                .orElseThrow(() -> new UnauthorizedException("Utilisateur introuvable"));
    }
}