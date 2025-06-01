package com.school.controller;

import com.school.dto.SectionDTOs;
import com.school.entity.Section;
import com.school.service.SectionService;
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
@RequestMapping("/sections")
@RequiredArgsConstructor
@Tag(name = "Sections", description = "API de gestion des sections de l'école")
public class SectionController {

    private final SectionService sectionService;

    @PostMapping
    @Operation(summary = "Créer une section", description = "Crée une nouvelle section dans l'école")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Section créée avec succès"),
            @ApiResponse(responseCode = "409", description = "Une section avec ce nom existe déjà"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<SectionDTOs.SectionResponse> createSection(
            @Valid @RequestBody SectionDTOs.SectionCreateRequest request) {
        SectionDTOs.SectionResponse response = sectionService.createSection(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Lister toutes les sections", description = "Récupère la liste de toutes les sections actives")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<SectionDTOs.SectionResponse>> getAllSections() {
        List<SectionDTOs.SectionResponse> sections = sectionService.getAllSections();
        return ResponseEntity.ok(sections);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer une section", description = "Récupère les détails d'une section par son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Section trouvée"),
            @ApiResponse(responseCode = "404", description = "Section non trouvée")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<SectionDTOs.SectionDetailResponse> getSectionById(
            @Parameter(description = "ID de la section") @PathVariable Long id) {
        SectionDTOs.SectionDetailResponse section = sectionService.getSectionById(id);
        return ResponseEntity.ok(section);
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "Récupérer sections par type", description = "Récupère les sections d'un type donné (FRANCOPHONE, ANGLOPHONE, BILINGUE)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sections trouvées"),
            @ApiResponse(responseCode = "400", description = "Type de section invalide")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<SectionDTOs.SectionResponse>> getSectionsByType(
            @Parameter(description = "Type de section") @PathVariable Section.TypeSection type) {
        List<SectionDTOs.SectionResponse> sections = sectionService.getSectionsByType(type);
        return ResponseEntity.ok(sections);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier une section", description = "Met à jour les informations d'une section")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Section mise à jour avec succès"),
            @ApiResponse(responseCode = "404", description = "Section non trouvée"),
            @ApiResponse(responseCode = "409", description = "Le nouveau nom existe déjà"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<SectionDTOs.SectionResponse> updateSection(
            @Parameter(description = "ID de la section") @PathVariable Long id,
            @Valid @RequestBody SectionDTOs.SectionUpdateRequest request) {
        SectionDTOs.SectionResponse response = sectionService.updateSection(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une section", description = "Supprime une section (suppression logique)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Section supprimée avec succès"),
            @ApiResponse(responseCode = "404", description = "Section non trouvée"),
            @ApiResponse(responseCode = "409", description = "Impossible de supprimer, section contient des données")
    })
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> deleteSection(
            @Parameter(description = "ID de la section") @PathVariable Long id) {
        sectionService.deleteSection(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activate")
    @Operation(summary = "Activer une section", description = "Active une section désactivée")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Section activée avec succès"),
            @ApiResponse(responseCode = "404", description = "Section non trouvée")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<String> activateSection(
            @Parameter(description = "ID de la section") @PathVariable Long id) {
        sectionService.activateSection(id);
        return ResponseEntity.ok("Section activée avec succès");
    }

    @PatchMapping("/{id}/deactivate")
    @Operation(summary = "Désactiver une section", description = "Désactive une section")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Section désactivée avec succès"),
            @ApiResponse(responseCode = "404", description = "Section non trouvée")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<String> deactivateSection(
            @Parameter(description = "ID de la section") @PathVariable Long id) {
        sectionService.deactivateSection(id);
        return ResponseEntity.ok("Section désactivée avec succès");
    }
}