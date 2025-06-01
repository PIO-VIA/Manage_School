package com.school.controller;

import com.school.dto.NoteDTOs;
import com.school.service.NoteService;
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
@RequestMapping("/notes")
@RequiredArgsConstructor
@Tag(name = "Notes et Évaluations", description = "API de gestion des notes et évaluations des élèves")
public class NoteController {

    private final NoteService noteService;

    @PostMapping
    @Operation(summary = "Créer une note", description = "Ajoute une nouvelle note pour un élève dans une matière")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Note créée avec succès"),
            @ApiResponse(responseCode = "409", description = "Une note existe déjà pour cette combinaison"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "404", description = "Élève ou matière introuvable")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<NoteDTOs.NoteResponse> createNote(
            @Valid @RequestBody NoteDTOs.NoteCreateRequest request) {
        NoteDTOs.NoteResponse response = noteService.createNote(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/eleve/{matricule}")
    @Operation(summary = "Notes d'un élève", description = "Récupère toutes les notes d'un élève")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notes récupérées avec succès"),
            @ApiResponse(responseCode = "404", description = "Élève non trouvé")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<NoteDTOs.NoteResponse>> getNotesByEleve(
            @Parameter(description = "Matricule de l'élève") @PathVariable String matricule) {
        List<NoteDTOs.NoteResponse> notes = noteService.getNotesByEleve(matricule);
        return ResponseEntity.ok(notes);
    }

    @GetMapping("/matiere/{idMatiere}")
    @Operation(summary = "Notes par matière", description = "Récupère toutes les notes d'une matière")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notes récupérées avec succès"),
            @ApiResponse(responseCode = "404", description = "Matière non trouvée")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<NoteDTOs.NoteResponse>> getNotesByMatiere(
            @Parameter(description = "ID de la matière") @PathVariable Long idMatiere) {
        List<NoteDTOs.NoteResponse> notes = noteService.getNotesByMatiere(idMatiere);
        return ResponseEntity.ok(notes);
    }

    @GetMapping("/sequence/{sequence}")
    @Operation(summary = "Notes par séquence", description = "Récupère toutes les notes d'une séquence")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notes récupérées avec succès")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<NoteDTOs.NoteResponse>> getNotesBySequence(
            @Parameter(description = "Numéro de la séquence") @PathVariable Integer sequence) {
        List<NoteDTOs.NoteResponse> notes = noteService.getNotesBySequence(sequence);
        return ResponseEntity.ok(notes);
    }

    @GetMapping("/bulletin/{matricule}/sequence/{sequence}")
    @Operation(summary = "Bulletin d'un élève", description = "Génère le bulletin d'un élève pour une séquence")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bulletin généré avec succès"),
            @ApiResponse(responseCode = "404", description = "Élève non trouvé")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<NoteDTOs.BulletinEleveResponse> getBulletinEleve(
            @Parameter(description = "Matricule de l'élève") @PathVariable String matricule,
            @Parameter(description = "Numéro de la séquence") @PathVariable Integer sequence) {
        NoteDTOs.BulletinEleveResponse bulletin = noteService.getBulletinEleve(matricule, sequence);
        return ResponseEntity.ok(bulletin);
    }

    @GetMapping("/statistiques/sequence/{sequence}/classe/{idClasse}")
    @Operation(summary = "Statistiques de séquence", description = "Calcule les statistiques d'une séquence pour une classe")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statistiques calculées avec succès"),
            @ApiResponse(responseCode = "404", description = "Classe non trouvée")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<NoteDTOs.StatistiquesSequenceResponse> getStatistiquesSequence(
            @Parameter(description = "Numéro de la séquence") @PathVariable Integer sequence,
            @Parameter(description = "ID de la classe") @PathVariable Long idClasse) {
        NoteDTOs.StatistiquesSequenceResponse stats = noteService.getStatistiquesSequence(sequence, idClasse);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/classement/classe/{idClasse}/sequence/{sequence}")
    @Operation(summary = "Classement de classe", description = "Génère le classement d'une classe pour une séquence")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Classement généré avec succès"),
            @ApiResponse(responseCode = "404", description = "Classe non trouvée")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<NoteDTOs.ClassementEleveResponse>> getClassementClasse(
            @Parameter(description = "ID de la classe") @PathVariable Long idClasse,
            @Parameter(description = "Numéro de la séquence") @PathVariable Integer sequence) {
        List<NoteDTOs.ClassementEleveResponse> classement = noteService.getClassementClasse(idClasse, sequence);
        return ResponseEntity.ok(classement);
    }

    @GetMapping("/moyenne/{matricule}/matiere/{idMatiere}")
    @Operation(summary = "Moyenne élève-matière", description = "Calcule la moyenne d'un élève dans une matière")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Moyenne calculée avec succès"),
            @ApiResponse(responseCode = "404", description = "Élève ou matière non trouvé")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Double> getMoyenneEleveMatiere(
            @Parameter(description = "Matricule de l'élève") @PathVariable String matricule,
            @Parameter(description = "ID de la matière") @PathVariable Long idMatiere) {
        Double moyenne = noteService.calculerMoyenneEleveMatiere(matricule, idMatiere);
        return ResponseEntity.ok(moyenne != null ? moyenne : 0.0);
    }

    @PutMapping("/{matricule}/matiere/{idMatiere}/sequence/{sequence}")
    @Operation(summary = "Modifier une note", description = "Met à jour une note existante")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Note mise à jour avec succès"),
            @ApiResponse(responseCode = "404", description = "Note non trouvée"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<NoteDTOs.NoteResponse> updateNote(
            @Parameter(description = "Matricule de l'élève") @PathVariable String matricule,
            @Parameter(description = "ID de la matière") @PathVariable Long idMatiere,
            @Parameter(description = "Numéro de la séquence") @PathVariable Integer sequence,
            @Valid @RequestBody NoteDTOs.NoteUpdateRequest request) {
        NoteDTOs.NoteResponse response = noteService.updateNote(matricule, idMatiere, sequence, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{matricule}/matiere/{idMatiere}/sequence/{sequence}")
    @Operation(summary = "Supprimer une note", description = "Supprime une note")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Note supprimée avec succès"),
            @ApiResponse(responseCode = "404", description = "Note non trouvée")
    })
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> deleteNote(
            @Parameter(description = "Matricule de l'élève") @PathVariable String matricule,
            @Parameter(description = "ID de la matière") @PathVariable Long idMatiere,
            @Parameter(description = "Numéro de la séquence") @PathVariable Integer sequence) {
        noteService.deleteNote(matricule, idMatiere, sequence);
        return ResponseEntity.noContent().build();
    }
}