package com.school.controller;

import com.school.dto.PersonnelAdministratifDTOs;
import com.school.service.PersonnelAdministratifService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/personnel-administratif")
@RequiredArgsConstructor
@Tag(name = "Personnel Administratif", description = "API de gestion du personnel administratif")
public class PersonnelAdministratifController {

    private final PersonnelAdministratifService personnelAdministratifService;

    @PostMapping
    @Operation(summary = "Créer un personnel administratif", description = "Ajoute un nouveau membre du personnel administratif")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Personnel créé avec succès"),
            @ApiResponse(responseCode = "409", description = "Email déjà utilisé"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<PersonnelAdministratifDTOs.PersonnelAdministratifResponse> createPersonnelAdministratif(
            @Valid @RequestBody PersonnelAdministratifDTOs.PersonnelAdministratifCreateRequest request) {
        PersonnelAdministratifDTOs.PersonnelAdministratifResponse response =
                personnelAdministratifService.createPersonnelAdministratif(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Lister le personnel administratif", description = "Récupère la liste de tout le personnel administratif actif")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<PersonnelAdministratifDTOs.PersonnelAdministratifResponse>> getAllPersonnelAdministratif() {
        List<PersonnelAdministratifDTOs.PersonnelAdministratifResponse> personnel =
                personnelAdministratifService.getAllPersonnelAdministratif();
        return ResponseEntity.ok(personnel);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un personnel administratif", description = "Récupère les détails complets d'un membre du personnel administratif par son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Personnel trouvé"),
            @ApiResponse(responseCode = "404", description = "Personnel non trouvé")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<PersonnelAdministratifDTOs.PersonnelAdministratifDetailResponse> getPersonnelAdministratifById(
            @Parameter(description = "ID du personnel administratif") @PathVariable Long id) {
        PersonnelAdministratifDTOs.PersonnelAdministratifDetailResponse personnel =
                personnelAdministratifService.getPersonnelAdministratifById(id);
        return ResponseEntity.ok(personnel);
    }

    @GetMapping("/statistiques")
    @Operation(summary = "Statistiques du personnel administratif", description = "Récupère les statistiques globales du personnel administratif")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statistiques récupérées avec succès")
    })
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<PersonnelAdministratifDTOs.PersonnelAdministratifStatsResponse> getStatistiquesPersonnelAdministratif() {
        PersonnelAdministratifDTOs.PersonnelAdministratifStatsResponse stats =
                personnelAdministratifService.getStatistiquesPersonnelAdministratif();
        return ResponseEntity.ok(stats);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un personnel administratif", description = "Met à jour les informations d'un membre du personnel administratif")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Personnel mis à jour avec succès"),
            @ApiResponse(responseCode = "404", description = "Personnel non trouvé"),
            @ApiResponse(responseCode = "409", description = "Email déjà utilisé"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "403", description = "Permissions insuffisantes")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<PersonnelAdministratifDTOs.PersonnelAdministratifResponse> updatePersonnelAdministratif(
            @Parameter(description = "ID du personnel administratif") @PathVariable Long id,
            @Valid @RequestBody PersonnelAdministratifDTOs.PersonnelAdministratifUpdateRequest request) {
        PersonnelAdministratifDTOs.PersonnelAdministratifResponse response =
                personnelAdministratifService.updatePersonnelAdministratif(id, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/activate")
    @Operation(summary = "Activer un personnel administratif", description = "Active un membre du personnel administratif désactivé")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Personnel activé avec succès"),
            @ApiResponse(responseCode = "404", description = "Personnel non trouvé")
    })
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<String> activatePersonnelAdministratif(
            @Parameter(description = "ID du personnel administratif") @PathVariable Long id) {
        personnelAdministratifService.activatePersonnelAdministratif(id);
        return ResponseEntity.ok("Personnel administratif activé avec succès");
    }

    @PatchMapping("/{id}/change-password")
    @Operation(summary = "Changer le mot de passe", description = "Change le mot de passe d'un membre du personnel administratif")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mot de passe changé avec succès"),
            @ApiResponse(responseCode = "404", description = "Personnel non trouvé"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "403", description = "Permissions insuffisantes")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<String> changePasswordAdmin(
            @Parameter(description = "ID du personnel administratif") @PathVariable Long id,
            @Valid @RequestBody PersonnelAdministratifDTOs.ChangePasswordAdminRequest request) {
        personnelAdministratifService.changePasswordAdmin(id, request);
        return ResponseEntity.ok("Mot de passe changé avec succès");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un personnel administratif", description = "Supprime un membre du personnel administratif (suppression logique)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Personnel supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Personnel non trouvé"),
            @ApiResponse(responseCode = "400", description = "Impossible de supprimer son propre compte"),
            @ApiResponse(responseCode = "403", description = "Permissions insuffisantes")
    })
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> deletePersonnelAdministratif(
            @Parameter(description = "ID du personnel administratif") @PathVariable Long id) {
        personnelAdministratifService.deletePersonnelAdministratif(id);
        return ResponseEntity.noContent().build();
    }
}