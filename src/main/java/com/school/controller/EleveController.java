package com.school.controller;

import com.school.dto.EleveDTOs;
import com.school.service.EleveService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/eleves")
@RequiredArgsConstructor
@Tag(name = "Élèves", description = "API de gestion des élèves")
public class EleveController {

    private final EleveService eleveService;

    @PostMapping
    @Operation(summary = "Créer un élève", description = "Inscrit un nouvel élève dans l'école")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Élève créé avec succès"),
            @ApiResponse(responseCode = "409", description = "Matricule déjà utilisé ou classe pleine"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "404", description = "Classe ou section introuvable")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<EleveDTOs.EleveResponse> createEleve(
            @Valid @RequestBody EleveDTOs.EleveCreateRequest request) {
        EleveDTOs.EleveResponse response = eleveService.createEleve(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Lister tous les élèves", description = "Récupère la liste paginée de tous les élèves actifs")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Page<EleveDTOs.EleveResponse>> getAllEleves(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<EleveDTOs.EleveResponse> eleves = eleveService.getAllEleves(pageable);
        return ResponseEntity.ok(eleves);
    }

    @GetMapping("/{matricule}")
    @Operation(summary = "Récupérer un élève", description = "Récupère les détails complets d'un élève par son matricule")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Élève trouvé"),
            @ApiResponse(responseCode = "404", description = "Élève non trouvé")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<EleveDTOs.EleveDetailResponse> getEleveByMatricule(
            @Parameter(description = "Matricule de l'élève") @PathVariable String matricule) {
        EleveDTOs.EleveDetailResponse eleve = eleveService.getEleveByMatricule(matricule);
        return ResponseEntity.ok(eleve);
    }

    @GetMapping("/classe/{idClasse}")
    @Operation(summary = "Élèves par classe", description = "Récupère tous les élèves d'une classe donnée")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Élèves trouvés"),
            @ApiResponse(responseCode = "404", description = "Classe non trouvée")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<EleveDTOs.EleveResponse>> getElevesByClasse(
            @Parameter(description = "ID de la classe") @PathVariable Long idClasse) {
        List<EleveDTOs.EleveResponse> eleves = eleveService.getElevesByClasse(idClasse);
        return ResponseEntity.ok(eleves);
    }

    @GetMapping("/section/{idSection}")
    @Operation(summary = "Élèves par section", description = "Récupère tous les élèves d'une section donnée")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Élèves trouvés"),
            @ApiResponse(responseCode = "404", description = "Section non trouvée")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<EleveDTOs.EleveResponse>> getElevesBySection(
            @Parameter(description = "ID de la section") @PathVariable Long idSection) {
        List<EleveDTOs.EleveResponse> eleves = eleveService.getElevesBySection(idSection);
        return ResponseEntity.ok(eleves);
    }

    @GetMapping("/search")
    @Operation(summary = "Rechercher des élèves", description = "Recherche des élèves par nom, prénom ou matricule")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Résultats de recherche")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Page<EleveDTOs.EleveResponse>> searchEleves(
            @Parameter(description = "Mot-clé de recherche") @RequestParam String keyword,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<EleveDTOs.EleveResponse> eleves = eleveService.searchEleves(keyword, pageable);
        return ResponseEntity.ok(eleves);
    }

    @PutMapping("/{matricule}")
    @Operation(summary = "Modifier un élève", description = "Met à jour les informations d'un élève")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Élève mis à jour avec succès"),
            @ApiResponse(responseCode = "404", description = "Élève non trouvé"),
            @ApiResponse(responseCode = "409", description = "Classe pleine lors du changement"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<EleveDTOs.EleveResponse> updateEleve(
            @Parameter(description = "Matricule de l'élève") @PathVariable String matricule,
            @Valid @RequestBody EleveDTOs.EleveUpdateRequest request) {
        EleveDTOs.EleveResponse response = eleveService.updateEleve(matricule, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{matricule}")
    @Operation(summary = "Supprimer un élève", description = "Supprime un élève (suppression logique)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Élève supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Élève non trouvé")
    })
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> deleteEleve(
            @Parameter(description = "Matricule de l'élève") @PathVariable String matricule) {
        eleveService.deleteEleve(matricule);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{matricule}/activate")
    @Operation(summary = "Activer un élève", description = "Active un élève désactivé")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Élève activé avec succès"),
            @ApiResponse(responseCode = "404", description = "Élève non trouvé")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<String> activateEleve(
            @Parameter(description = "Matricule de l'élève") @PathVariable String matricule) {
        eleveService.activateEleve(matricule);
        return ResponseEntity.ok("Élève activé avec succès");
    }
}