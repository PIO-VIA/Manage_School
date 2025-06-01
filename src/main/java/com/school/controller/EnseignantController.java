package com.school.controller;

import com.school.dto.EnseignantDTOs;
import com.school.service.EnseignantService;
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
@RequestMapping("/enseignants")
@RequiredArgsConstructor
@Tag(name = "Enseignants", description = "API de gestion des enseignants")
public class EnseignantController {

    private final EnseignantService enseignantService;

    @PostMapping
    @Operation(summary = "Créer un enseignant", description = "Ajoute un nouvel enseignant au système")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Enseignant créé avec succès"),
            @ApiResponse(responseCode = "409", description = "Email déjà utilisé ou section/classe inactive"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "404", description = "Section ou classe introuvable")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<EnseignantDTOs.EnseignantResponse> createEnseignant(
            @Valid @RequestBody EnseignantDTOs.EnseignantCreateRequest request) {
        EnseignantDTOs.EnseignantResponse response = enseignantService.createEnseignant(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Lister tous les enseignants", description = "Récupère la liste de tous les enseignants actifs")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<EnseignantDTOs.EnseignantResponse>> getAllEnseignants() {
        List<EnseignantDTOs.EnseignantResponse> enseignants = enseignantService.getAllEnseignants();
        return ResponseEntity.ok(enseignants);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un enseignant", description = "Récupère les détails complets d'un enseignant par son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Enseignant trouvé"),
            @ApiResponse(responseCode = "404", description = "Enseignant non trouvé")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<EnseignantDTOs.EnseignantDetailResponse> getEnseignantById(
            @Parameter(description = "ID de l'enseignant") @PathVariable Long id) {
        EnseignantDTOs.EnseignantDetailResponse enseignant = enseignantService.getEnseignantById(id);
        return ResponseEntity.ok(enseignant);
    }

    @GetMapping("/section/{idSection}")
    @Operation(summary = "Enseignants par section", description = "Récupère tous les enseignants d'une section donnée")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Enseignants trouvés"),
            @ApiResponse(responseCode = "404", description = "Section non trouvée")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<EnseignantDTOs.EnseignantResponse>> getEnseignantsBySection(
            @Parameter(description = "ID de la section") @PathVariable Long idSection) {
        List<EnseignantDTOs.EnseignantResponse> enseignants = enseignantService.getEnseignantsBySection(idSection);
        return ResponseEntity.ok(enseignants);
    }

    @GetMapping("/classe/{idClasse}")
    @Operation(summary = "Enseignants par classe", description = "Récupère tous les enseignants d'une classe donnée")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Enseignants trouvés"),
            @ApiResponse(responseCode = "404", description = "Classe non trouvée")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<EnseignantDTOs.EnseignantResponse>> getEnseignantsByClasse(
            @Parameter(description = "ID de la classe") @PathVariable Long idClasse) {
        List<EnseignantDTOs.EnseignantResponse> enseignants = enseignantService.getEnseignantsByClasse(idClasse);
        return ResponseEntity.ok(enseignants);
    }

    @GetMapping("/section/{idSection}/specialite/{specialite}")
    @Operation(summary = "Enseignants par spécialité", description = "Récupère les enseignants d'une section avec une spécialité donnée")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Enseignants trouvés")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<EnseignantDTOs.EnseignantResponse>> getEnseignantsBySpecialite(
            @Parameter(description = "ID de la section") @PathVariable Long idSection,
            @Parameter(description = "Spécialité de l'enseignant") @PathVariable String specialite) {
        List<EnseignantDTOs.EnseignantResponse> enseignants = enseignantService.getEnseignantsBySpecialite(idSection, specialite);
        return ResponseEntity.ok(enseignants);
    }

    @GetMapping("/{id}/stats")
    @Operation(summary = "Statistiques enseignant", description = "Récupère les statistiques d'un enseignant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statistiques récupérées"),
            @ApiResponse(responseCode = "404", description = "Enseignant non trouvé")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<EnseignantDTOs.EnseignantStatsResponse> getEnseignantStats(
            @Parameter(description = "ID de l'enseignant") @PathVariable Long id) {
        EnseignantDTOs.EnseignantStatsResponse stats = enseignantService.getEnseignantStats(id);
        return ResponseEntity.ok(stats);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un enseignant", description = "Met à jour les informations d'un enseignant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Enseignant mis à jour avec succès"),
            @ApiResponse(responseCode = "404", description = "Enseignant non trouvé"),
            @ApiResponse(responseCode = "409", description = "Email déjà utilisé ou conflit"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<EnseignantDTOs.EnseignantResponse> updateEnseignant(
            @Parameter(description = "ID de l'enseignant") @PathVariable Long id,
            @Valid @RequestBody EnseignantDTOs.EnseignantUpdateRequest request) {
        EnseignantDTOs.EnseignantResponse response = enseignantService.updateEnseignant(id, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/matieres")
    @Operation(summary = "Attribuer une matière", description = "Attribue une matière à un enseignant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Matière attribuée avec succès"),
            @ApiResponse(responseCode = "404", description = "Enseignant ou matière non trouvé"),
            @ApiResponse(responseCode = "409", description = "Enseignant enseigne déjà cette matière"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<String> assignMatiere(
            @Parameter(description = "ID de l'enseignant") @PathVariable Long id,
            @Valid @RequestBody EnseignantDTOs.AssignMatiereRequest request) {
        enseignantService.assignMatiere(id, request);
        return ResponseEntity.ok("Matière attribuée avec succès");
    }

    @DeleteMapping("/{id}/matieres/{matiereId}")
    @Operation(summary = "Retirer une matière", description = "Retire une matière d'un enseignant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Matière retirée avec succès"),
            @ApiResponse(responseCode = "404", description = "Attribution non trouvée")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<String> unassignMatiere(
            @Parameter(description = "ID de l'enseignant") @PathVariable Long id,
            @Parameter(description = "ID de la matière") @PathVariable Long matiereId) {
        enseignantService.unassignMatiere(id, matiereId);
        return ResponseEntity.ok("Matière retirée avec succès");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un enseignant", description = "Supprime un enseignant (suppression logique)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Enseignant supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Enseignant non trouvé")
    })
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> deleteEnseignant(
            @Parameter(description = "ID de l'enseignant") @PathVariable Long id) {
        enseignantService.deleteEnseignant(id);
        return ResponseEntity.noContent().build();
    }
}