package com.school.controller;

import com.school.dto.AbsenceDTOs;
import com.school.service.AbsenceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/absences")
@RequiredArgsConstructor
@Tag(name = "Absences", description = "API de gestion des absences des élèves")
public class AbsenceController {

    private final AbsenceService absenceService;

    @PostMapping
    @Operation(summary = "Créer une absence", description = "Enregistre une nouvelle absence pour un élève")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Absence créée avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "404", description = "Élève ou dossier discipline introuvable")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<AbsenceDTOs.AbsenceResponse> createAbsence(
            @Valid @RequestBody AbsenceDTOs.AbsenceCreateRequest request) {
        AbsenceDTOs.AbsenceResponse response = absenceService.createAbsence(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Lister toutes les absences", description = "Récupère la liste de toutes les absences actives")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<AbsenceDTOs.AbsenceResponse>> getAllAbsences() {
        List<AbsenceDTOs.AbsenceResponse> absences = absenceService.getAllAbsences();
        return ResponseEntity.ok(absences);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer une absence", description = "Récupère les détails d'une absence par son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Absence trouvée"),
            @ApiResponse(responseCode = "404", description = "Absence non trouvée")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<AbsenceDTOs.AbsenceResponse> getAbsenceById(
            @Parameter(description = "ID de l'absence") @PathVariable Long id) {
        AbsenceDTOs.AbsenceResponse absence = absenceService.getAbsenceById(id);
        return ResponseEntity.ok(absence);
    }

    @GetMapping("/eleve/{matricule}")
    @Operation(summary = "Absences d'un élève", description = "Récupère toutes les absences d'un élève")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Absences récupérées avec succès"),
            @ApiResponse(responseCode = "404", description = "Élève non trouvé")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<AbsenceDTOs.AbsenceResponse>> getAbsencesByEleve(
            @Parameter(description = "Matricule de l'élève") @PathVariable String matricule) {
        List<AbsenceDTOs.AbsenceResponse> absences = absenceService.getAbsencesByEleve(matricule);
        return ResponseEntity.ok(absences);
    }

    @GetMapping("/eleve/{matricule}/periode")
    @Operation(summary = "Absences par période", description = "Récupère les absences d'un élève sur une période donnée")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Absences récupérées avec succès"),
            @ApiResponse(responseCode = "404", description = "Élève non trouvé"),
            @ApiResponse(responseCode = "400", description = "Période invalide")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<AbsenceDTOs.AbsenceResponse>> getAbsencesByPeriode(
            @Parameter(description = "Matricule de l'élève") @PathVariable String matricule,
            @Parameter(description = "Date de début (yyyy-MM-dd)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @Parameter(description = "Date de fin (yyyy-MM-dd)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {
        List<AbsenceDTOs.AbsenceResponse> absences = absenceService.getAbsencesByPeriode(matricule, dateDebut, dateFin);
        return ResponseEntity.ok(absences);
    }

    @GetMapping("/eleve/{matricule}/statistiques")
    @Operation(summary = "Statistiques d'absences", description = "Calcule les statistiques d'absences d'un élève")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statistiques calculées avec succès"),
            @ApiResponse(responseCode = "404", description = "Élève non trouvé")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<AbsenceDTOs.AbsenceStatsResponse> getStatistiquesAbsencesEleve(
            @Parameter(description = "Matricule de l'élève") @PathVariable String matricule) {
        AbsenceDTOs.AbsenceStatsResponse stats = absenceService.getStatistiquesAbsencesEleve(matricule);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/classe/{idClasse}/statistiques")
    @Operation(summary = "Statistiques classe", description = "Calcule les statistiques d'absences d'une classe")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statistiques calculées avec succès"),
            @ApiResponse(responseCode = "404", description = "Classe non trouvée")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<AbsenceDTOs.AbsenceClasseStatsResponse> getStatistiquesAbsencesClasse(
            @Parameter(description = "ID de la classe") @PathVariable Long idClasse) {
        AbsenceDTOs.AbsenceClasseStatsResponse stats = absenceService.getStatistiquesAbsencesClasse(idClasse);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/eleve/{matricule}/non-justifiees/count")
    @Operation(summary = "Compteur absences non justifiées", description = "Compte les absences non justifiées d'un élève")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comptage effectué avec succès"),
            @ApiResponse(responseCode = "404", description = "Élève non trouvé")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Long> countAbsencesNonJustifiees(
            @Parameter(description = "Matricule de l'élève") @PathVariable String matricule) {
        Long count = absenceService.countAbsencesNonJustifiees(matricule);
        return ResponseEntity.ok(count);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier une absence", description = "Met à jour les informations d'une absence")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Absence mise à jour avec succès"),
            @ApiResponse(responseCode = "404", description = "Absence non trouvée"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<AbsenceDTOs.AbsenceResponse> updateAbsence(
            @Parameter(description = "ID de l'absence") @PathVariable Long id,
            @Valid @RequestBody AbsenceDTOs.AbsenceUpdateRequest request) {
        AbsenceDTOs.AbsenceResponse response = absenceService.updateAbsence(id, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/justifier")
    @Operation(summary = "Justifier une absence", description = "Marque une absence comme justifiée avec un motif")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Absence justifiée avec succès"),
            @ApiResponse(responseCode = "404", description = "Absence non trouvée")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<String> justifierAbsence(
            @Parameter(description = "ID de l'absence") @PathVariable Long id,
            @Parameter(description = "Motif de la justification") @RequestParam(required = false) String motif) {
        absenceService.justifierAbsence(id, motif);
        return ResponseEntity.ok("Absence justifiée avec succès");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une absence", description = "Supprime une absence (suppression logique)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Absence supprimée avec succès"),
            @ApiResponse(responseCode = "404", description = "Absence non trouvée")
    })
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> deleteAbsence(
            @Parameter(description = "ID de l'absence") @PathVariable Long id) {
        absenceService.deleteAbsence(id);
        return ResponseEntity.noContent().build();
    }
}