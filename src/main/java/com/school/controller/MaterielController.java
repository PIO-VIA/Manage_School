package com.school.controller;

import com.school.dto.MaterielDTOs;
import com.school.entity.Materiel;
import com.school.service.MaterielService;
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
@RequestMapping("/materiels")
@RequiredArgsConstructor
@Tag(name = "Matériel", description = "API de gestion du matériel scolaire")
public class MaterielController {

    private final MaterielService materielService;

    @PostMapping
    @Operation(summary = "Créer un matériel", description = "Ajoute un nouveau matériel au système")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Matériel créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "404", description = "Personnel d'entretien introuvable")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<MaterielDTOs.MaterielResponse> createMateriel(
            @Valid @RequestBody MaterielDTOs.MaterielCreateRequest request) {
        MaterielDTOs.MaterielResponse response = materielService.createMateriel(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Lister tous les matériels", description = "Récupère la liste de tous les matériels actifs")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<MaterielDTOs.MaterielResponse>> getAllMateriels() {
        List<MaterielDTOs.MaterielResponse> materiels = materielService.getAllMateriels();
        return ResponseEntity.ok(materiels);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un matériel", description = "Récupère les détails d'un matériel par son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Matériel trouvé"),
            @ApiResponse(responseCode = "404", description = "Matériel non trouvé")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<MaterielDTOs.MaterielResponse> getMaterielById(
            @Parameter(description = "ID du matériel") @PathVariable Long id) {
        MaterielDTOs.MaterielResponse materiel = materielService.getMaterielById(id);
        return ResponseEntity.ok(materiel);
    }

    @GetMapping("/personnel/{idPersonnelEntretien}")
    @Operation(summary = "Matériels par personnel", description = "Récupère tous les matériels d'un personnel d'entretien")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Matériels trouvés"),
            @ApiResponse(responseCode = "404", description = "Personnel non trouvé")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<MaterielDTOs.MaterielResponse>> getMaterielsByPersonnel(
            @Parameter(description = "ID du personnel d'entretien") @PathVariable Long idPersonnelEntretien) {
        List<MaterielDTOs.MaterielResponse> materiels = materielService.getMaterielsByPersonnel(idPersonnelEntretien);
        return ResponseEntity.ok(materiels);
    }

    @GetMapping("/etat/{etat}")
    @Operation(summary = "Matériels par état", description = "Récupère tous les matériels ayant un état donné")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Matériels trouvés")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<MaterielDTOs.MaterielResponse>> getMaterielsByEtat(
            @Parameter(description = "État du matériel") @PathVariable Materiel.EtatMateriel etat) {
        List<MaterielDTOs.MaterielResponse> materiels = materielService.getMaterielsByEtat(etat);
        return ResponseEntity.ok(materiels);
    }

    @GetMapping("/statistiques")
    @Operation(summary = "Statistiques matériels", description = "Récupère les statistiques globales des matériels")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statistiques récupérées avec succès")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<MaterielDTOs.MaterielStatsResponse> getStatistiquesMateriels() {
        MaterielDTOs.MaterielStatsResponse stats = materielService.getStatistiquesMateriels();
        return ResponseEntity.ok(stats);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un matériel", description = "Met à jour les informations d'un matériel")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Matériel mis à jour avec succès"),
            @ApiResponse(responseCode = "404", description = "Matériel non trouvé"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<MaterielDTOs.MaterielResponse> updateMateriel(
            @Parameter(description = "ID du matériel") @PathVariable Long id,
            @Valid @RequestBody MaterielDTOs.MaterielUpdateRequest request) {
        MaterielDTOs.MaterielResponse response = materielService.updateMateriel(id, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/etat")
    @Operation(summary = "Changer l'état d'un matériel", description = "Change l'état d'un matériel (ex: BON vers MAUVAIS)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "État changé avec succès"),
            @ApiResponse(responseCode = "404", description = "Matériel non trouvé")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<MaterielDTOs.MaterielResponse> changerEtatMateriel(
            @Parameter(description = "ID du matériel") @PathVariable Long id,
            @Parameter(description = "Nouvel état du matériel") @RequestParam Materiel.EtatMateriel nouvelEtat) {
        MaterielDTOs.MaterielResponse response = materielService.changerEtatMateriel(id, nouvelEtat);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un matériel", description = "Supprime un matériel (suppression logique)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Matériel supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Matériel non trouvé")
    })
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> deleteMateriel(
            @Parameter(description = "ID du matériel") @PathVariable Long id) {
        materielService.deleteMateriel(id);
        return ResponseEntity.noContent().build();
    }
}