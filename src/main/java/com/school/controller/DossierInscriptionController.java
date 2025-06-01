package com.school.controller;

import com.school.dto.DossierInscriptionDTOs;
import com.school.entity.DossierInscription;
import com.school.service.DossierInscriptionService;
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
@RequestMapping("/inscriptions")
@RequiredArgsConstructor
@Tag(name = "Dossiers d'Inscription", description = "API de gestion des dossiers d'inscription et paiements")
public class DossierInscriptionController {

    private final DossierInscriptionService dossierInscriptionService;

    @PostMapping
    @Operation(summary = "Créer un dossier d'inscription", description = "Crée un nouveau dossier d'inscription pour un élève")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Dossier créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "404", description = "Élève ou administrateur introuvable")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<DossierInscriptionDTOs.DossierInscriptionResponse> createDossierInscription(
            @Valid @RequestBody DossierInscriptionDTOs.DossierInscriptionCreateRequest request) {
        DossierInscriptionDTOs.DossierInscriptionResponse response = dossierInscriptionService.createDossierInscription(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Lister tous les dossiers d'inscription", description = "Récupère la liste de tous les dossiers d'inscription actifs")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<DossierInscriptionDTOs.DossierInscriptionResponse>> getAllDossiersInscription() {
        List<DossierInscriptionDTOs.DossierInscriptionResponse> dossiers = dossierInscriptionService.getAllDossiersInscription();
        return ResponseEntity.ok(dossiers);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un dossier d'inscription", description = "Récupère les détails d'un dossier d'inscription par son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dossier trouvé"),
            @ApiResponse(responseCode = "404", description = "Dossier non trouvé")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<DossierInscriptionDTOs.DossierInscriptionResponse> getDossierInscriptionById(
            @Parameter(description = "ID du dossier d'inscription") @PathVariable Long id) {
        DossierInscriptionDTOs.DossierInscriptionResponse dossier = dossierInscriptionService.getDossierInscriptionById(id);
        return ResponseEntity.ok(dossier);
    }

    @GetMapping("/eleve/{matricule}")
    @Operation(summary = "Dossiers d'un élève", description = "Récupère tous les dossiers d'inscription d'un élève")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dossiers récupérés avec succès"),
            @ApiResponse(responseCode = "404", description = "Élève non trouvé")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<DossierInscriptionDTOs.DossierInscriptionResponse>> getDossiersByEleve(
            @Parameter(description = "Matricule de l'élève") @PathVariable String matricule) {
        List<DossierInscriptionDTOs.DossierInscriptionResponse> dossiers = dossierInscriptionService.getDossiersByEleve(matricule);
        return ResponseEntity.ok(dossiers);
    }

    @GetMapping("/annee-scolaire/{anneeScolaire}")
    @Operation(summary = "Dossiers par année scolaire", description = "Récupère tous les dossiers d'inscription d'une année scolaire")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dossiers récupérés avec succès")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<DossierInscriptionDTOs.DossierInscriptionResponse>> getDossiersByAnneeScolaire(
            @Parameter(description = "Année scolaire (ex: 2024-2025)") @PathVariable String anneeScolaire) {
        List<DossierInscriptionDTOs.DossierInscriptionResponse> dossiers = dossierInscriptionService.getDossiersByAnneeScolaire(anneeScolaire);
        return ResponseEntity.ok(dossiers);
    }

    @GetMapping("/etat/{etat}")
    @Operation(summary = "Dossiers par état", description = "Récupère tous les dossiers d'inscription ayant un état donné")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dossiers récupérés avec succès")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<DossierInscriptionDTOs.DossierInscriptionResponse>> getDossiersByEtat(
            @Parameter(description = "État du dossier") @PathVariable DossierInscription.EtatInscription etat) {
        List<DossierInscriptionDTOs.DossierInscriptionResponse> dossiers = dossierInscriptionService.getDossiersByEtat(etat);
        return ResponseEntity.ok(dossiers);
    }

    @GetMapping("/statistiques")
    @Operation(summary = "Statistiques des inscriptions", description = "Récupère les statistiques globales des inscriptions et paiements")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statistiques récupérées avec succès")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<DossierInscriptionDTOs.DossierInscriptionStatsResponse> getStatistiquesInscriptions() {
        DossierInscriptionDTOs.DossierInscriptionStatsResponse stats = dossierInscriptionService.getStatistiquesInscriptions();
        return ResponseEntity.ok(stats);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un dossier d'inscription", description = "Met à jour les informations d'un dossier d'inscription")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dossier mis à jour avec succès"),
            @ApiResponse(responseCode = "404", description = "Dossier non trouvé"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<DossierInscriptionDTOs.DossierInscriptionResponse> updateDossierInscription(
            @Parameter(description = "ID du dossier d'inscription") @PathVariable Long id,
            @Valid @RequestBody DossierInscriptionDTOs.DossierInscriptionUpdateRequest request) {
        DossierInscriptionDTOs.DossierInscriptionResponse response = dossierInscriptionService.updateDossierInscription(id, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/paiements")
    @Operation(summary = "Enregistrer un paiement", description = "Enregistre un nouveau paiement pour un dossier d'inscription")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Paiement enregistré avec succès"),
            @ApiResponse(responseCode = "404", description = "Dossier non trouvé"),
            @ApiResponse(responseCode = "400", description = "Montant invalide ou dépasse le reste à payer")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<DossierInscriptionDTOs.DossierInscriptionResponse> enregistrerPaiement(
            @Parameter(description = "ID du dossier d'inscription") @PathVariable Long id,
            @Valid @RequestBody DossierInscriptionDTOs.PaiementRequest paiementRequest) {
        DossierInscriptionDTOs.DossierInscriptionResponse response = dossierInscriptionService.enregistrerPaiement(id, paiementRequest);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un dossier d'inscription", description = "Supprime un dossier d'inscription (suppression logique)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Dossier supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Dossier non trouvé")
    })
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> deleteDossierInscription(
            @Parameter(description = "ID du dossier d'inscription") @PathVariable Long id) {
        dossierInscriptionService.deleteDossierInscription(id);
        return ResponseEntity.noContent().build();
    }
}