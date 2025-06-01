package com.school.mapper;

import com.school.dto.NoteDTOs;
import com.school.entity.Composer;
import com.school.entity.Eleve;
import com.school.entity.SalleDeClasse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.*;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface NoteMapper {

    @Mapping(target = "eleve", source = "eleve", qualifiedByName = "toEleveInfo")
    @Mapping(target = "matiere", source = "matiere", qualifiedByName = "toMatiereInfo")
    NoteDTOs.NoteResponse toResponse(Composer composer);

    List<NoteDTOs.NoteResponse> toResponseList(List<Composer> composers);

    @Named("toEleveInfo")
    default NoteDTOs.EleveInfo toEleveInfo(Eleve eleve) {
        if (eleve == null) return null;
        return NoteDTOs.EleveInfo.builder()
                .matricule(eleve.getMatricule())
                .nom(eleve.getNom())
                .prenom(eleve.getPrenom())
                .classe(eleve.getSalleDeClasse() != null ? eleve.getSalleDeClasse().getNom() : "")
                .section(eleve.getSection() != null ? eleve.getSection().getNom() : "")
                .build();
    }

    @Named("toMatiereInfo")
    default NoteDTOs.MatiereInfo toMatiereInfo(com.school.entity.Matiere matiere) {
        if (matiere == null) return null;
        return NoteDTOs.MatiereInfo.builder()
                .idMatiere(matiere.getIdMatiere())
                .nom(matiere.getNom())
                .coefficient(matiere.getCoefficient())
                .build();
    }

    default NoteDTOs.BulletinEleveResponse toBulletinResponse(Eleve eleve, List<Composer> notes, Integer sequence) {
        if (eleve == null) return null;

        List<NoteDTOs.NoteDetaillee> notesDetaillees = notes.stream()
                .map(note -> NoteDTOs.NoteDetaillee.builder()
                        .matiere(note.getMatiere() != null ? note.getMatiere().getNom() : "")
                        .coefficient(note.getMatiere() != null ? note.getMatiere().getCoefficient() : 0)
                        .note(note.getNote())
                        .noteFinale(note.getNoteFinale())
                        .typeEvaluation(note.getTypeEvaluation().name())
                        .dateComposition(note.getDateComposition())
                        .build())
                .sorted(Comparator.comparing(NoteDTOs.NoteDetaillee::getMatiere))
                .collect(Collectors.toList());

        // Calcul de la moyenne générale
        double moyenneGenerale = calculerMoyenneGenerale(notes);
        String mention = determinerMention(moyenneGenerale);
        String appreciation = determinerAppreciation(moyenneGenerale);

        return NoteDTOs.BulletinEleveResponse.builder()
                .matricule(eleve.getMatricule())
                .nom(eleve.getNom())
                .prenom(eleve.getPrenom())
                .classe(eleve.getSalleDeClasse() != null ? eleve.getSalleDeClasse().getNom() : "")
                .section(eleve.getSection() != null ? eleve.getSection().getNom() : "")
                .sequence(sequence)
                .notes(notesDetaillees)
                .moyenneGenerale(moyenneGenerale)
                .rang(null) // À calculer séparément
                .mention(mention)
                .appreciation(appreciation)
                .build();
    }

    default NoteDTOs.StatistiquesSequenceResponse toStatistiquesSequenceResponse(
            SalleDeClasse classe, List<Composer> notes, Integer sequence) {

        if (classe == null || notes.isEmpty()) {
            return NoteDTOs.StatistiquesSequenceResponse.builder()
                    .sequence(sequence)
                    .classe(classe != null ? classe.getNom() : "")
                    .section(classe != null && classe.getSection() != null ? classe.getSection().getNom() : "")
                    .nombreEleves(0)
                    .moyenneClasse(0.0)
                    .noteMin(0.0)
                    .noteMax(0.0)
                    .nombreReussites(0)
                    .tauxReussite(0.0)
                    .statistiquesParMatiere(new ArrayList<>())
                    .build();
        }

        // Grouper les notes par élève pour calculer les moyennes individuelles
        Map<String, List<Composer>> notesParEleve = notes.stream()
                .collect(Collectors.groupingBy(Composer::getMatricule));

        List<Double> moyennesEleves = notesParEleve.values().stream()
                .map(this::calculerMoyenneGenerale)
                .collect(Collectors.toList());

        double moyenneClasse = moyennesEleves.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);

        double noteMin = moyennesEleves.stream()
                .mapToDouble(Double::doubleValue)
                .min()
                .orElse(0.0);

        double noteMax = moyennesEleves.stream()
                .mapToDouble(Double::doubleValue)
                .max()
                .orElse(0.0);

        long reussites = moyennesEleves.stream()
                .mapToDouble(Double::doubleValue)
                .filter(moyenne -> moyenne >= 10.0)
                .count();

        double tauxReussite = moyennesEleves.isEmpty() ? 0.0 :
                (double) reussites / moyennesEleves.size() * 100;

        // Statistiques par matière
        Map<String, List<Composer>> notesParMatiere = notes.stream()
                .filter(note -> note.getMatiere() != null)
                .collect(Collectors.groupingBy(note -> note.getMatiere().getNom()));

        List<NoteDTOs.StatistiqueMatiereSequence> statsParMatiere = notesParMatiere.entrySet().stream()
                .map(entry -> {
                    String matiere = entry.getKey();
                    List<Composer> notesMatiere = entry.getValue();

                    double moyenneMatiere = notesMatiere.stream()
                            .mapToDouble(Composer::getNoteFinale)
                            .average()
                            .orElse(0.0);

                    double minMatiere = notesMatiere.stream()
                            .mapToDouble(Composer::getNoteFinale)
                            .min()
                            .orElse(0.0);

                    double maxMatiere = notesMatiere.stream()
                            .mapToDouble(Composer::getNoteFinale)
                            .max()
                            .orElse(0.0);

                    long reussitesMatiere = notesMatiere.stream()
                            .mapToDouble(Composer::getNoteFinale)
                            .filter(note -> note >= 10.0)
                            .count();

                    double tauxReussiteMatiere = notesMatiere.isEmpty() ? 0.0 :
                            (double) reussitesMatiere / notesMatiere.size() * 100;

                    Integer coefficient = notesMatiere.get(0).getMatiere() != null ?
                            notesMatiere.get(0).getMatiere().getCoefficient() : 0;

                    return NoteDTOs.StatistiqueMatiereSequence.builder()
                            .matiere(matiere)
                            .coefficient(coefficient)
                            .nombreEleves(notesMatiere.size())
                            .moyenne(Math.round(moyenneMatiere * 100.0) / 100.0)
                            .noteMin(Math.round(minMatiere * 100.0) / 100.0)
                            .noteMax(Math.round(maxMatiere * 100.0) / 100.0)
                            .nombreReussites((int) reussitesMatiere)
                            .tauxReussite(Math.round(tauxReussiteMatiere * 100.0) / 100.0)
                            .build();
                })
                .sorted(Comparator.comparing(NoteDTOs.StatistiqueMatiereSequence::getMatiere))
                .collect(Collectors.toList());

        return NoteDTOs.StatistiquesSequenceResponse.builder()
                .sequence(sequence)
                .classe(classe.getNom())
                .section(classe.getSection().getNom())
                .nombreEleves(moyennesEleves.size())
                .moyenneClasse(Math.round(moyenneClasse * 100.0) / 100.0)
                .noteMin(Math.round(noteMin * 100.0) / 100.0)
                .noteMax(Math.round(noteMax * 100.0) / 100.0)
                .nombreReussites((int) reussites)
                .tauxReussite(Math.round(tauxReussite * 100.0) / 100.0)
                .statistiquesParMatiere(statsParMatiere)
                .build();
    }

    // Méthodes utilitaires
    default double calculerMoyenneGenerale(List<Composer> notes) {
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

    default String determinerMention(double moyenne) {
        if (moyenne >= 16) return "Très Bien";
        if (moyenne >= 14) return "Bien";
        if (moyenne >= 12) return "Assez Bien";
        if (moyenne >= 10) return "Passable";
        return "Insuffisant";
    }

    default String determinerAppreciation(double moyenne) {
        if (moyenne >= 16) return "Excellent travail, continuez ainsi!";
        if (moyenne >= 14) return "Bon travail, quelques efforts encore.";
        if (moyenne >= 12) return "Travail satisfaisant, peut mieux faire.";
        if (moyenne >= 10) return "Travail acceptable, des efforts à fournir.";
        return "Travail insuffisant, beaucoup d'efforts nécessaires.";
    }
}