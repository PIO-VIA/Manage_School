package com.school.controller;

import com.school.dto.DossierDisciplineDTOs;
import com.school.entity.DossierExamen;
import com.school.service.DossierDisciplineService;
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
@RequestMapping("/discipline")
@RequiredArgsConstructor
@Tag(name = "Dossiers Disciplinaires", description = "API de gestion des dossiers disciplinaires")
public class DossierDisciplineController {

    private final DossierDisciplineService dossierDisciplineService;

    @PostMapping
    @Operation(summary = "Créer un dossier disciplinaire", description = "Crée un nouveau dossier disciplinaire pour un élève")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Dossier créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "404", description = "Élève ou administrateur introuvable")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<DossierDisciplineDTOs.DossierDisciplineResponse> createDossierDiscipline(
            @Valid @RequestBody DossierDisciplineDTOs.DossierDisciplineCreateRequest request) {
        DossierDisciplineDTOs.DossierDisciplineResponse response = dossierDisciplineService.createDossierDiscipline(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Lister tous les dossiers disciplinaires", description = "Récupère la liste de tous les dossiers disciplinaires actifs")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<DossierDisciplineDTOs.DossierDisciplineResponse>> getAllDossiersDiscipline() {
        List<DossierDisciplineDTOs.DossierDisciplineResponse> dossiers = dossierDisciplineService.getAllDossiersDiscipline();
        return ResponseEntity.ok(dossiers);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un dossier disciplinaire", description = "Récupère les détails complets d'un dossier disciplinaire par son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dossier trouvé"),
            @ApiResponse(responseCode = "404", description = "Dossier non trouvé")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<DossierDisciplineDTOs.DossierDisciplineDetailResponse> getDossierDisciplineById(
            @Parameter(description = "ID du dossier disciplinaire") @PathVariable Long id) {
        DossierDisciplineDTOs.DossierDisciplineDetailResponse dossier = dossierDisciplineService.getDossierDisciplineById(id);
        return ResponseEntity.ok(dossier);
    }

    @GetMapping("/eleve/{matricule}")
    @Operation(summary = "Dossiers d'un élève", description = "Récupère tous les dossiers disciplinaires d'un élève")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dossiers récupérés avec succès"),
            @ApiResponse(responseCode = "404", description = "Élève non trouvé")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<DossierDisciplineDTOs.DossierDisciplineResponse>> getDossiersByEleve(
            @Parameter(description = "Matricule de l'élève") @PathVariable String matricule) {
        List<DossierDisciplineDTOs.DossierDisciplineResponse> dossiers = dossierDisciplineService.getDossiersByEleve(matricule);
        return ResponseEntity.ok(dossiers);
    }

    @GetMapping("/etat/{etat}")
    @Operation(summary = "Dossiers par état", description = "Récupère tous les dossiers disciplinaires ayant un état donné")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dossiers récupérés avec succès")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<DossierDisciplineDTOs.DossierDisciplineResponse>> getDossiersByEtat(
            @Parameter(description = "État du dossier") @PathVariable DossierExamen.EtatDossier etat) {
        List<DossierDisciplineDTOs.DossierDisciplineResponse> dossiers = dossierDisciplineService.getDossiersByEtat(etat);
        return ResponseEntity.ok(dossiers);
    }

    @GetMapping("/statistiques")
    @Operation(summary = "Statistiques disciplinaires", description = "Récupère les statistiques globales des dossiers disciplinaires")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statistiques récupérées avec succès")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<DossierDisciplineDTOs.DossierDisciplineStatsResponse> getStatistiquesDiscipline() {
        DossierDisciplineDTOs.DossierDisciplineStatsResponse stats = dossierDisciplineService.getStatistiquesDiscipline();
        return ResponseEntity.ok(stats);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un dossier disciplinaire", description = "Met à jour les informations d'un dossier disciplinaire")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dossier mis à jour avec succès"),
            @ApiResponse(responseCode = "404", description = "Dossier non trouvé"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<DossierDisciplineDTOs.DossierDisciplineResponse> updateDossierDiscipline(
            @Parameter(description = "ID du dossier disciplinaire") @PathVariable Long id,
            @Valid @RequestBody DossierDisciplineDTOs.DossierDisciplineUpdateRequest request) {
        DossierDisciplineDTOs.DossierDisciplineResponse response = dossierDisciplineService.updateDossierDiscipline(id, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/cloturer")
    @Operation(summary = "Clôturer un dossier", description = "Clôture un dossier disciplinaire avec une résolution")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dossier clôturé avec succès"),
            @ApiResponse(responseCode = "404", description = "Dossier non trouvé")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<DossierDisciplineDTOs.DossierDisciplineResponse> cloturerDossier(
            @Parameter(description = "ID du dossier disciplinaire") @PathVariable Long id,
            @Parameter(description = "Résolution du dossier") @RequestParam(required = false) String resolution) {
        DossierDisciplineDTOs.DossierDisciplineResponse response = dossierDisciplineService.cloturerDossier(id, resolution);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un dossier disciplinaire", description = "Supprime un dossier disciplinaire (suppression logique)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Dossier supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Dossier non trouvé")
    })
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> deleteDossierDiscipline(
            @Parameter(description = "ID du dossier disciplinaire") @PathVariable Long id) {
        dossierDisciplineService.deleteDossierDiscipline(id);
        return ResponseEntity.noContent().build();
    }
}