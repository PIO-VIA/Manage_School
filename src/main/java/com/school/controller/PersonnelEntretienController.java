package com.school.controller;

import com.school.dto.PersonnelEntretienDTOs;
import com.school.service.PersonnelEntretienService;
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
@RequestMapping("/personnel-entretien")
@RequiredArgsConstructor
@Tag(name = "Personnel d'Entretien", description = "API de gestion du personnel d'entretien")
public class PersonnelEntretienController {

    private final PersonnelEntretienService personnelEntretienService;

    @PostMapping
    @Operation(summary = "Créer un personnel d'entretien", description = "Ajoute un nouveau membre du personnel d'entretien")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Personnel créé avec succès"),
            @ApiResponse(responseCode = "409", description = "Email déjà utilisé"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<PersonnelEntretienDTOs.PersonnelEntretienResponse> createPersonnelEntretien(
            @Valid @RequestBody PersonnelEntretienDTOs.PersonnelEntretienCreateRequest request) {
        PersonnelEntretienDTOs.PersonnelEntretienResponse response = personnelEntretienService.createPersonnelEntretien(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Lister le personnel d'entretien", description = "Récupère la liste de tout le personnel d'entretien actif")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<PersonnelEntretienDTOs.PersonnelEntretienResponse>> getAllPersonnelEntretien() {
        List<PersonnelEntretienDTOs.PersonnelEntretienResponse> personnel = personnelEntretienService.getAllPersonnelEntretien();
        return ResponseEntity.ok(personnel);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un personnel d'entretien", description = "Récupère les détails d'un membre du personnel d'entretien par son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Personnel trouvé"),
            @ApiResponse(responseCode = "404", description = "Personnel non trouvé")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<PersonnelEntretienDTOs.PersonnelEntretienDetailResponse> getPersonnelEntretienById(
            @Parameter(description = "ID du personnel d'entretien") @PathVariable Long id) {
        PersonnelEntretienDTOs.PersonnelEntretienDetailResponse personnel = personnelEntretienService.getPersonnelEntretienById(id);
        return ResponseEntity.ok(personnel);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un personnel d'entretien", description = "Met à jour les informations d'un membre du personnel d'entretien")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Personnel mis à jour avec succès"),
            @ApiResponse(responseCode = "404", description = "Personnel non trouvé"),
            @ApiResponse(responseCode = "409", description = "Email déjà utilisé"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<PersonnelEntretienDTOs.PersonnelEntretienResponse> updatePersonnelEntretien(
            @Parameter(description = "ID du personnel d'entretien") @PathVariable Long id,
            @Valid @RequestBody PersonnelEntretienDTOs.PersonnelEntretienUpdateRequest request) {
        PersonnelEntretienDTOs.PersonnelEntretienResponse response = personnelEntretienService.updatePersonnelEntretien(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un personnel d'entretien", description = "Supprime un membre du personnel d'entretien (suppression logique)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Personnel supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Personnel non trouvé")
    })
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> deletePersonnelEntretien(
            @Parameter(description = "ID du personnel d'entretien") @PathVariable Long id) {
        personnelEntretienService.deletePersonnelEntretien(id);
        return ResponseEntity.noContent().build();
    }
}