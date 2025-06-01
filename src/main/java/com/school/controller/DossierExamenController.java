package com.school.controller;

import com.school.dto.DossierExamenDTOs;
import com.school.entity.DossierExamen;
import com.school.service.DossierExamenService;
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
@RequestMapping("/examens")
@RequiredArgsConstructor
@Tag(name = "Dossiers d'Examen", description = "API de gestion des dossiers d'examen")
public class DossierExamenController {

    private final DossierExamenService dossierExamenService;

    @PostMapping
    @Operation(summary = "Créer un dossier d'examen", description = "Crée un nouveau dossier d'examen pour un élève")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Dossier créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "404", description = "Élève ou administrateur introuvable")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<DossierExamenDTOs.DossierExamenResponse> createDossierExamen(
            @Valid @RequestBody DossierExamenDTOs.DossierExamenCreateRequest request) {
        DossierExamenDTOs.DossierExamenResponse response = dossierExamenService.createDossierExamen(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Lister tous les dossiers d'examen", description = "Récupère la liste de tous les dossiers d'examen actifs")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<DossierExamenDTOs.DossierExamenResponse>> getAllDossiersExamen() {
        List<DossierExamenDTOs.DossierExamenResponse> dossiers = dossierExamenService.getAllDossiersExamen();
        return ResponseEntity.ok(dossiers);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un dossier d'examen", description = "Récupère les détails d'un dossier d'examen par son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dossier trouvé"),
            @ApiResponse(responseCode = "404", description = "Dossier non trouvé")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<DossierExamenDTOs.DossierExamenResponse> getDossierExamenById(
            @Parameter(description = "ID du dossier d'examen") @PathVariable Long id) {
        DossierExamenDTOs.DossierExamenResponse dossier = dossierExamenService.getDossierExamenById(id);
        return ResponseEntity.ok(dossier);
    }

    @GetMapping("/eleve/{matricule}")
    @Operation(summary = "Dossiers d'un élève", description = "Récupère tous les dossiers d'examen d'un élève")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dossiers récupérés avec succès"),
            @ApiResponse(responseCode = "404", description = "Élève non trouvé")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<DossierExamenDTOs.DossierExamenResponse>> getDossiersByEleve(
            @Parameter(description = "Matricule de l'élève") @PathVariable String matricule) {
        List<DossierExamenDTOs.DossierExamenResponse> dossiers = dossierExamenService.getDossiersByEleve(matricule);
        return ResponseEntity.ok(dossiers);
    }

    @GetMapping("/etat/{etat}")
    @Operation(summary = "Dossiers par état", description = "Récupère tous les dossiers d'examen ayant un état donné")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dossiers récupérés avec succès")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<DossierExamenDTOs.DossierExamenResponse>> getDossiersByEtat(
            @Parameter(description = "État du dossier") @PathVariable DossierExamen.EtatDossier etat) {
        List<DossierExamenDTOs.DossierExamenResponse> dossiers = dossierExamenService.getDossiersByEtat(etat);
        return ResponseEntity.ok(dossiers);
    }

    @GetMapping("/statistiques")
    @Operation(summary = "Statistiques des examens", description = "Récupère les statistiques globales des dossiers d'examen")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statistiques récupérées avec succès")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<DossierExamenDTOs.DossierExamenStatsResponse> getStatistiquesExamens() {
        DossierExamenDTOs.DossierExamenStatsResponse stats = dossierExamenService.getStatistiquesExamens();
        return ResponseEntity.ok(stats);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un dossier d'examen", description = "Met à jour les informations d'un dossier d'examen")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dossier mis à jour avec succès"),
            @ApiResponse(responseCode = "404", description = "Dossier non trouvé"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<DossierExamenDTOs.DossierExamenResponse> updateDossierExamen(
            @Parameter(description = "ID du dossier d'examen") @PathVariable Long id,
            @Valid @RequestBody DossierExamenDTOs.DossierExamenUpdateRequest request) {
        DossierExamenDTOs.DossierExamenResponse response = dossierExamenService.updateDossierExamen(id, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/finaliser")
    @Operation(summary = "Finaliser un examen", description = "Finalise un dossier d'examen avec un résultat")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Examen finalisé avec succès"),
            @ApiResponse(responseCode = "404", description = "Dossier non trouvé")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<DossierExamenDTOs.DossierExamenResponse> finaliserExamen(
            @Parameter(description = "ID du dossier d'examen") @PathVariable Long id,
            @Parameter(description = "Résultat de l'examen") @RequestParam(required = false) String resultat) {
        DossierExamenDTOs.DossierExamenResponse response = dossierExamenService.finaliserExamen(id, resultat);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un dossier d'examen", description = "Supprime un dossier d'examen (suppression logique)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Dossier supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Dossier non trouvé")
    })
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> deleteDossierExamen(
            @Parameter(description = "ID du dossier d'examen") @PathVariable Long id) {
        dossierExamenService.deleteDossierExamen(id);
        return ResponseEntity.noContent().build();
    }
}