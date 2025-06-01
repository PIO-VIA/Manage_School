package com.school.controller;

import com.school.dto.ClasseDTOs;
import com.school.entity.SalleDeClasse;
import com.school.service.ClasseService;
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
@RequestMapping("/classes")
@RequiredArgsConstructor
@Tag(name = "Classes", description = "API de gestion des classes/salles de classe")
public class ClasseController {

    private final ClasseService classeService;

    @PostMapping
    @Operation(summary = "Créer une classe", description = "Crée une nouvelle classe dans une section")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Classe créée avec succès"),
            @ApiResponse(responseCode = "409", description = "Section inactive ou conflit"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "404", description = "Section introuvable")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ClasseDTOs.ClasseResponse> createClasse(
            @Valid @RequestBody ClasseDTOs.ClasseCreateRequest request) {
        ClasseDTOs.ClasseResponse response = classeService.createClasse(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Lister toutes les classes", description = "Récupère la liste de toutes les classes actives")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<ClasseDTOs.ClasseResponse>> getAllClasses() {
        List<ClasseDTOs.ClasseResponse> classes = classeService.getAllClasses();
        return ResponseEntity.ok(classes);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer une classe", description = "Récupère les détails complets d'une classe par son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Classe trouvée"),
            @ApiResponse(responseCode = "404", description = "Classe non trouvée")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ClasseDTOs.ClasseDetailResponse> getClasseById(
            @Parameter(description = "ID de la classe") @PathVariable Long id) {
        ClasseDTOs.ClasseDetailResponse classe = classeService.getClasseById(id);
        return ResponseEntity.ok(classe);
    }

    @GetMapping("/section/{idSection}")
    @Operation(summary = "Classes par section", description = "Récupère toutes les classes d'une section donnée")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Classes trouvées"),
            @ApiResponse(responseCode = "404", description = "Section non trouvée")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<ClasseDTOs.ClasseResponse>> getClassesBySection(
            @Parameter(description = "ID de la section") @PathVariable Long idSection) {
        List<ClasseDTOs.ClasseResponse> classes = classeService.getClassesBySection(idSection);
        return ResponseEntity.ok(classes);
    }

    @GetMapping("/section/{idSection}/niveau/{niveau}")
    @Operation(summary = "Classes par section et niveau", description = "Récupère les classes d'une section et d'un niveau donné")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Classes trouvées"),
            @ApiResponse(responseCode = "400", description = "Niveau invalide")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<ClasseDTOs.ClasseResponse>> getClassesBySectionAndNiveau(
            @Parameter(description = "ID de la section") @PathVariable Long idSection,
            @Parameter(description = "Niveau de la classe") @PathVariable SalleDeClasse.Niveau niveau) {
        List<ClasseDTOs.ClasseResponse> classes = classeService.getClassesBySectionAndNiveau(idSection, niveau);
        return ResponseEntity.ok(classes);
    }

    @GetMapping("/{id}/stats")
    @Operation(summary = "Statistiques de classe", description = "Récupère les statistiques détaillées d'une classe")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statistiques récupérées"),
            @ApiResponse(responseCode = "404", description = "Classe non trouvée")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ClasseDTOs.ClasseStatsResponse> getClasseStats(
            @Parameter(description = "ID de la classe") @PathVariable Long id) {
        ClasseDTOs.ClasseStatsResponse stats = classeService.getClasseStats(id);
        return ResponseEntity.ok(stats);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier une classe", description = "Met à jour les informations d'une classe")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Classe mise à jour avec succès"),
            @ApiResponse(responseCode = "404", description = "Classe non trouvée"),
            @ApiResponse(responseCode = "409", description = "Conflit avec les données existantes"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ClasseDTOs.ClasseResponse> updateClasse(
            @Parameter(description = "ID de la classe") @PathVariable Long id,
            @Valid @RequestBody ClasseDTOs.ClasseUpdateRequest request) {
        ClasseDTOs.ClasseResponse response = classeService.updateClasse(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une classe", description = "Supprime une classe (suppression logique)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Classe supprimée avec succès"),
            @ApiResponse(responseCode = "404", description = "Classe non trouvée"),
            @ApiResponse(responseCode = "409", description = "Impossible de supprimer, classe contient des données")
    })
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> deleteClasse(
            @Parameter(description = "ID de la classe") @PathVariable Long id) {
        classeService.deleteClasse(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activate")
    @Operation(summary = "Activer une classe", description = "Active une classe désactivée")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Classe activée avec succès"),
            @ApiResponse(responseCode = "404", description = "Classe non trouvée"),
            @ApiResponse(responseCode = "409", description = "Section inactive")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<String> activateClasse(
            @Parameter(description = "ID de la classe") @PathVariable Long id) {
        classeService.activateClasse(id);
        return ResponseEntity.ok("Classe activée avec succès");
    }
}