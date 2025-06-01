package com.school.controller;

import com.school.dto.MatiereDTOs;
import com.school.service.MatiereService;
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
@RequestMapping("/matieres")
@RequiredArgsConstructor
@Tag(name = "Matières", description = "API de gestion des matières scolaires")
public class MatiereController {

    private final MatiereService matiereService;

    @PostMapping
    @Operation(summary = "Créer une matière", description = "Ajoute une nouvelle matière au système")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Matière créée avec succès"),
            @ApiResponse(responseCode = "409", description = "Une matière avec ce nom existe déjà"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<MatiereDTOs.MatiereResponse> createMatiere(
            @Valid @RequestBody MatiereDTOs.MatiereCreateRequest request) {
        MatiereDTOs.MatiereResponse response = matiereService.createMatiere(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Lister toutes les matières", description = "Récupère la liste de toutes les matières actives")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<MatiereDTOs.MatiereResponse>> getAllMatieres() {
        List<MatiereDTOs.MatiereResponse> matieres = matiereService.getAllMatieres();
        return ResponseEntity.ok(matieres);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer une matière", description = "Récupère les détails complets d'une matière par son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Matière trouvée"),
            @ApiResponse(responseCode = "404", description = "Matière non trouvée")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<MatiereDTOs.MatiereDetailResponse> getMatiereById(
            @Parameter(description = "ID de la matière") @PathVariable Long id) {
        MatiereDTOs.MatiereDetailResponse matiere = matiereService.getMatiereById(id);
        return ResponseEntity.ok(matiere);
    }

    @GetMapping("/{id}/stats")
    @Operation(summary = "Statistiques de matière", description = "Récupère les statistiques détaillées d'une matière")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statistiques récupérées"),
            @ApiResponse(responseCode = "404", description = "Matière non trouvée")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<MatiereDTOs.MatiereStatsResponse> getMatiereStats(
            @Parameter(description = "ID de la matière") @PathVariable Long id) {
        MatiereDTOs.MatiereStatsResponse stats = matiereService.getMatiereStats(id);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/search")
    @Operation(summary = "Rechercher des matières", description = "Recherche des matières par nom")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Résultats de recherche")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<MatiereDTOs.MatiereResponse>> searchMatieres(
            @Parameter(description = "Nom de la matière à rechercher") @RequestParam String nom) {
        List<MatiereDTOs.MatiereResponse> matieres = matiereService.searchMatieres(nom);
        return ResponseEntity.ok(matieres);
    }

    @GetMapping("/coefficient/{coefficient}")
    @Operation(summary = "Matières par coefficient", description = "Récupère les matières ayant un coefficient donné")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Matières trouvées")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<MatiereDTOs.MatiereResponse>> getMatieresByCoefficient(
            @Parameter(description = "Coefficient de la matière") @PathVariable Integer coefficient) {
        List<MatiereDTOs.MatiereResponse> matieres = matiereService.getMatieresByCoefficient(coefficient);
        return ResponseEntity.ok(matieres);
    }

    @GetMapping("/enseignant/{enseignantId}")
    @Operation(summary = "Matières par enseignant", description = "Récupère les matières enseignées par un enseignant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Matières trouvées"),
            @ApiResponse(responseCode = "404", description = "Enseignant non trouvé")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<MatiereDTOs.MatiereResponse>> getMatieresByEnseignant(
            @Parameter(description = "ID de l'enseignant") @PathVariable Long enseignantId) {
        List<MatiereDTOs.MatiereResponse> matieres = matiereService.getMatieresByEnseignant(enseignantId);
        return ResponseEntity.ok(matieres);
    }

    @GetMapping("/{id}/moyenne/{matricule}")
    @Operation(summary = "Moyenne d'un élève", description = "Calcule la moyenne d'un élève dans une matière")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Moyenne calculée"),
            @ApiResponse(responseCode = "404", description = "Matière ou élève non trouvé")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Double> calculateMoyenneEleve(
            @Parameter(description = "ID de la matière") @PathVariable Long id,
            @Parameter(description = "Matricule de l'élève") @PathVariable String matricule) {
        Double moyenne = matiereService.calculateMoyenneMatiere(id, matricule);
        return ResponseEntity.ok(moyenne != null ? moyenne : 0.0);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier une matière", description = "Met à jour les informations d'une matière")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Matière mise à jour avec succès"),
            @ApiResponse(responseCode = "404", description = "Matière non trouvée"),
            @ApiResponse(responseCode = "409", description = "Le nouveau nom existe déjà"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<MatiereDTOs.MatiereResponse> updateMatiere(
            @Parameter(description = "ID de la matière") @PathVariable Long id,
            @Valid @RequestBody MatiereDTOs.MatiereUpdateRequest request) {
        MatiereDTOs.MatiereResponse response = matiereService.updateMatiere(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une matière", description = "Supprime une matière (suppression logique)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Matière supprimée avec succès"),
            @ApiResponse(responseCode = "404", description = "Matière non trouvée"),
            @ApiResponse(responseCode = "409", description = "Impossible de supprimer, matière utilisée")
    })
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> deleteMatiere(
            @Parameter(description = "ID de la matière") @PathVariable Long id) {
        matiereService.deleteMatiere(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activate")
    @Operation(summary = "Activer une matière", description = "Active une matière désactivée")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Matière activée avec succès"),
            @ApiResponse(responseCode = "404", description = "Matière non trouvée")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<String> activateMatiere(
            @Parameter(description = "ID de la matière") @PathVariable Long id) {
        matiereService.activateMatiere(id);
        return ResponseEntity.ok("Matière activée avec succès");
    }

    @PatchMapping("/{id}/deactivate")
    @Operation(summary = "Désactiver une matière", description = "Désactive une matière")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Matière désactivée avec succès"),
            @ApiResponse(responseCode = "404", description = "Matière non trouvée")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<String> deactivateMatiere(
            @Parameter(description = "ID de la matière") @PathVariable Long id) {
        matiereService.deactivateMatiere(id);
        return ResponseEntity.ok("Matière désactivée avec succès");
    }
}