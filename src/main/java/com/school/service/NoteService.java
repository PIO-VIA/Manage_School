package com.school.service;

import com.school.dto.NoteDTOs;
import com.school.entity.*;
import com.school.exception.ConflictException;
import com.school.exception.NotFoundException;
import com.school.mapper.NoteMapper;
import com.school.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NoteService {

    private final ComposerRepository composerRepository;
    private final EleveRepository eleveRepository;
    private final MatiereRepository matiereRepository;
    private final SalleDeClasseRepository salleDeClasseRepository;
    private final NoteMapper noteMapper;

    @Transactional
    public NoteDTOs.NoteResponse createNote(NoteDTOs.NoteCreateRequest request) {
        log.info("Création d'une nouvelle note pour l'élève {} en {}",
                request.getMatricule(), request.getIdMatiere());

        // Vérifier que l'élève existe
        Eleve eleve = eleveRepository.findById(request.getMatricule())
                .orElseThrow(() -> new NotFoundException("Élève introuvable avec le matricule: " + request.getMatricule()));

        // Vérifier que la matière existe
        Matiere matiere = matiereRepository.findById(request.getIdMatiere())
                .orElseThrow(() -> new NotFoundException("Matière introuvable avec l'ID: " + request.getIdMatiere()));

        // Vérifier si une note existe déjà pour cette combinaison
        ComposerId composerId = new ComposerId(request.getMatricule(), request.getIdMatiere(), request.getSequence());
        if (composerRepository.existsById(composerId)) {
            throw new ConflictException("Une note existe déjà pour cet élève dans cette matière et séquence");
        }

        Composer composer = Composer.builder()
                .matricule(request.getMatricule())
                .idMatiere(request.getIdMatiere())
                .sequence(request.getSequence())
                .note(request.getNote())
                .noteFinale(request.getNoteFinale() != null ? request.getNoteFinale() : request.getNote())
                .dateComposition(request.getDateComposition())
                .typeEvaluation(request.getTypeEvaluation())
                .build();

        Composer savedComposer = composerRepository.save(composer);
        log.info("Note créée avec succès pour l'élève {}", request.getMatricule());

        return noteMapper.toResponse(savedComposer);
    }

    @Transactional(readOnly = true)
    public List<NoteDTOs.NoteResponse> getNotesByEleve(String matricule) {
        log.info("Récupération des notes de l'élève: {}", matricule);

        List<Composer> notes = composerRepository.findByMatricule(matricule);
        return notes.stream()
                .map(noteMapper::toResponse)
                .sorted(Comparator.comparing(NoteDTOs.NoteResponse::getSequence)
                        .thenComparing(note -> note.getMatiere().getNom()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<NoteDTOs.NoteResponse> getNotesByMatiere(Long idMatiere) {
        log.info("Récupération des notes de la matière: {}", idMatiere);

        List<Composer> notes = composerRepository.findByIdMatiere(idMatiere);
        return notes.stream()
                .map(noteMapper::toResponse)
                .sorted(Comparator.comparing(NoteDTOs.NoteResponse::getSequence)
                        .thenComparing(note -> note.getEleve().getNom()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<NoteDTOs.NoteResponse> getNotesBySequence(Integer sequence) {
        log.info("Récupération des notes de la séquence: {}", sequence);

        List<Composer> notes = composerRepository.findBySequence(sequence);
        return notes.stream()
                .map(noteMapper::toResponse)
                .sorted(Comparator.comparing(note -> note.getEleve().getNom())
                        .thenComparing(note -> note.getMatiere().getNom()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public NoteDTOs.BulletinEleveResponse getBulletinEleve(String matricule, Integer sequence) {
        log.info("Génération du bulletin de l'élève {} pour la séquence {}", matricule, sequence);

        Eleve eleve = eleveRepository.findById(matricule)
                .orElseThrow(() -> new NotFoundException("Élève introuvable avec le matricule: " + matricule));

        List<Composer> notes = composerRepository.findByEleveAndSequence(matricule, sequence);

        return noteMapper.toBulletinResponse(eleve, notes, sequence);
    }

    @Transactional(readOnly = true)
    public NoteDTOs.StatistiquesSequenceResponse getStatistiquesSequence(Integer sequence, Long idClasse) {
        log.info("Calcul des statistiques de la séquence {} pour la classe {}", sequence, idClasse);

        SalleDeClasse classe = salleDeClasseRepository.findById(idClasse)
                .orElseThrow(() -> new NotFoundException("Classe introuvable avec l'ID: " + idClasse));

        // Récupérer tous les élèves de la classe
        List<Eleve> elevesClasse = eleveRepository.findBySalleDeClasseIdClasseAndIsActiveTrue(idClasse);
        List<String> matricules = elevesClasse.stream()
                .map(Eleve::getMatricule)
                .collect(Collectors.toList());

        // Récupérer toutes les notes de la séquence pour ces élèves
        List<Composer> notesSequence = composerRepository.findBySequence(sequence).stream()
                .filter(note -> matricules.contains(note.getMatricule()))
                .collect(Collectors.toList());

        return noteMapper.toStatistiquesSequenceResponse(classe, notesSequence, sequence);
    }

    @Transactional(readOnly = true)
    public List<NoteDTOs.ClassementEleveResponse> getClassementClasse(Long idClasse, Integer sequence) {
        log.info("Calcul du classement de la classe {} pour la séquence {}", idClasse, sequence);

        List<Eleve> elevesClasse = eleveRepository.findBySalleDeClasseIdClasseAndIsActiveTrue(idClasse);

        List<NoteDTOs.ClassementEleveResponse> classement = new ArrayList<>();

        for (Eleve eleve : elevesClasse) {
            List<Composer> notesEleve = composerRepository.findByEleveAndSequence(eleve.getMatricule(), sequence);

            if (!notesEleve.isEmpty()) {
                double moyenne = calculerMoyenneEleve(notesEleve);
                String mention = determinerMention(moyenne);

                NoteDTOs.ClassementEleveResponse eleveClassement = NoteDTOs.ClassementEleveResponse.builder()
                        .matricule(eleve.getMatricule())
                        .nom(eleve.getNom())
                        .prenom(eleve.getPrenom())
                        .moyenne(moyenne)
                        .mention(mention)
                        .classe(eleve.getSalleDeClasse().getNom())
                        .section(eleve.getSection().getNom())
                        .build();

                classement.add(eleveClassement);
            }
        }

        // Trier par moyenne décroissante et attribuer les rangs
        classement.sort(Comparator.comparing(NoteDTOs.ClassementEleveResponse::getMoyenne).reversed());

        for (int i = 0; i < classement.size(); i++) {
            classement.get(i).setRang(i + 1);
        }

        return classement;
    }

    @Transactional
    public NoteDTOs.NoteResponse updateNote(String matricule, Long idMatiere, Integer sequence,
                                            NoteDTOs.NoteUpdateRequest request) {
        log.info("Mise à jour de la note de l'élève {} en matière {} séquence {}",
                matricule, idMatiere, sequence);

        ComposerId composerId = new ComposerId(matricule, idMatiere, sequence);
        Composer composer = composerRepository.findById(composerId)
                .orElseThrow(() -> new NotFoundException("Note introuvable"));

        if (request.getNote() != null) {
            composer.setNote(request.getNote());
            if (request.getNoteFinale() == null) {
                composer.setNoteFinale(request.getNote());
            }
        }

        if (request.getNoteFinale() != null) {
            composer.setNoteFinale(request.getNoteFinale());
        }

        if (request.getDateComposition() != null) {
            composer.setDateComposition(request.getDateComposition());
        }

        if (request.getTypeEvaluation() != null) {
            composer.setTypeEvaluation(request.getTypeEvaluation());
        }

        Composer updatedComposer = composerRepository.save(composer);
        log.info("Note mise à jour avec succès");

        return noteMapper.toResponse(updatedComposer);
    }

    @Transactional
    public void deleteNote(String matricule, Long idMatiere, Integer sequence) {
        log.info("Suppression de la note de l'élève {} en matière {} séquence {}",
                matricule, idMatiere, sequence);

        ComposerId composerId = new ComposerId(matricule, idMatiere, sequence);
        if (!composerRepository.existsById(composerId)) {
            throw new NotFoundException("Note introuvable");
        }

        composerRepository.deleteById(composerId);
        log.info("Note supprimée avec succès");
    }

    @Transactional(readOnly = true)
    public Double calculerMoyenneEleveMatiere(String matricule, Long idMatiere) {
        log.info("Calcul de la moyenne de l'élève {} en matière {}", matricule, idMatiere);

        return composerRepository.calculateAverageNote(matricule, idMatiere);
    }

    // Méthodes utilitaires
    private double calculerMoyenneEleve(List<Composer> notes) {
        if (notes.isEmpty()) return 0.0;

        double totalPoints = 0.0;
        int totalCoefficients = 0;

        for (Composer note : notes) {
            if (note.getMatiere() != null) {
                totalPoints += note.getNoteFinale() * note.getMatiere().getCoefficient();
                totalCoefficients += note.getMatiere().getCoefficient();
            }
        }

        return totalCoefficients > 0 ? Math.round((totalPoints / totalCoefficients) * 100.0) / 100.0 : 0.0;
    }

    private String determinerMention(double moyenne) {
        if (moyenne >= 16) return "Très Bien";
        if (moyenne >= 14) return "Bien";
        if (moyenne >= 12) return "Assez Bien";
        if (moyenne >= 10) return "Passable";
        return "Insuffisant";
    }

    private String determinerAppreciation(double moyenne) {
        if (moyenne >= 16) return "Excellent travail, continuez ainsi!";
        if (moyenne >= 14) return "Bon travail, quelques efforts encore.";
        if (moyenne >= 12) return "Travail satisfaisant, peut mieux faire.";
        if (moyenne >= 10) return "Travail acceptable, des efforts à fournir.";
        return "Travail insuffisant, beaucoup d'efforts nécessaires.";
    }
}