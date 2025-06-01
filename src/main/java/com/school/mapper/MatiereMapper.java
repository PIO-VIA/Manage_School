package com.school.mapper;

import com.school.dto.MatiereDTOs;
import com.school.entity.Composer;
import com.school.entity.Matiere;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.*;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface MatiereMapper {

    @Mapping(target = "nombreEnseignants", expression = "java(countEnseignants(matiere))")
    @Mapping(target = "nombreEleves", expression = "java(countEleves(matiere))")
    MatiereDTOs.MatiereResponse toResponse(Matiere matiere);

    @Mapping(target = "enseignants", source = "enseignements", qualifiedByName = "toEnseignantMatiereInfoList")
    @Mapping(target = "statistiquesNotes", source = "compositions", qualifiedByName = "toNoteStatistiqueList")
    @Mapping(target = "moyenneGenerale", expression = "java(calculateMoyenneGenerale(matiere))")
    MatiereDTOs.MatiereDetailResponse toDetailResponse(Matiere matiere);

    @Mapping(target = "nombreEnseignants", expression = "java(countEnseignants(matiere))")
    @Mapping(target = "nombreElevesTotal", expression = "java(countEleves(matiere))")
    @Mapping(target = "moyenneGenerale", expression = "java(calculateMoyenneGenerale(matiere))")
    @Mapping(target = "tauxReussiteGlobal", expression = "java(calculateTauxReussiteGlobal(matiere))")
    @Mapping(target = "nombreNotesTotal", expression = "java(countNotesTotal(matiere))")
    @Mapping(target = "statistiquesParSequence", source = "compositions", qualifiedByName = "toStatistiqueParSequenceList")
    @Mapping(target = "statistiquesParSection", expression = "java(calculateStatistiquesParSection(matiere))")
    MatiereDTOs.MatiereStatsResponse toStatsResponse(Matiere matiere);

    List<MatiereDTOs.MatiereResponse> toResponseList(List<Matiere> matieres);

    @Named("toEnseignantMatiereInfoList")
    default List<MatiereDTOs.EnseignantMatiereInfo> toEnseignantMatiereInfoList(List<com.school.entity.Enseigner> enseignements) {
        if (enseignements == null) return null;
        return enseignements.stream()
                .filter(enseigner -> enseigner.getIsActive())
                .map(enseigner -> {
                    var enseignant = enseigner.getEnseignant();
                    return MatiereDTOs.EnseignantMatiereInfo.builder()
                            .idMaitre(enseigner.getIdMaitre())
                            .nom(enseignant != null ? enseignant.getNom() : "")
                            .prenom(enseignant != null ? enseignant.getPrenom() : "")
                            .specialite(enseignant != null ? enseignant.getSpecialite() : "")
                            .dateOccupation(enseigner.getDateOccupation())
                            .section(enseignant != null && enseignant.getSection() != null ? enseignant.getSection().getNom() : "")
                            .classe(enseignant != null && enseignant.getSalleDeClasse() != null ? enseignant.getSalleDeClasse().getNom() : "")
                            .isActive(enseigner.getIsActive())
                            .build();
                })
                .toList();
    }

    @Named("toNoteStatistiqueList")
    default List<MatiereDTOs.NoteStatistique> toNoteStatistiqueList(List<Composer> compositions) {
        if (compositions == null || compositions.isEmpty()) return new ArrayList<>();

        Map<Integer, List<Composer>> parSequence = compositions.stream()
                .collect(Collectors.groupingBy(Composer::getSequence));

        return parSequence.entrySet().stream()
                .map(entry -> {
                    Integer sequence = entry.getKey();
                    List<Composer> notesSequence = entry.getValue();

                    double moyenne = notesSequence.stream()
                            .mapToDouble(Composer::getNote)
                            .average()
                            .orElse(0.0);

                    OptionalDouble minNote = notesSequence.stream()
                            .mapToDouble(Composer::getNote)
                            .min();

                    OptionalDouble maxNote = notesSequence.stream()
                            .mapToDouble(Composer::getNote)
                            .max();

                    long reussites = notesSequence.stream()
                            .mapToDouble(Composer::getNote)
                            .filter(note -> note >= 10.0)
                            .count();

                    double tauxReussite = notesSequence.isEmpty() ? 0.0 :
                            (double) reussites / notesSequence.size() * 100;

                    return MatiereDTOs.NoteStatistique.builder()
                            .sequence(sequence)
                            .moyenneSequence(Math.round(moyenne * 100.0) / 100.0)
                            .nombreEleves(notesSequence.size())
                            .noteMin(minNote.isPresent() ? Math.round(minNote.getAsDouble() * 100.0) / 100.0 : 0.0)
                            .noteMax(maxNote.isPresent() ? Math.round(maxNote.getAsDouble() * 100.0) / 100.0 : 0.0)
                            .nombreReussites((int) reussites)
                            .tauxReussite(Math.round(tauxReussite * 100.0) / 100.0)
                            .build();
                })
                .sorted(Comparator.comparing(MatiereDTOs.NoteStatistique::getSequence))
                .collect(Collectors.toList());
    }

    @Named("toStatistiqueParSequenceList")
    default List<MatiereDTOs.StatistiqueParSequence> toStatistiqueParSequenceList(List<Composer> compositions) {
        if (compositions == null || compositions.isEmpty()) return new ArrayList<>();

        Map<Integer, List<Composer>> parSequence = compositions.stream()
                .collect(Collectors.groupingBy(Composer::getSequence));

        return parSequence.entrySet().stream()
                .map(entry -> {
                    Integer sequence = entry.getKey();
                    List<Composer> notesSequence = entry.getValue();

                    double moyenne = notesSequence.stream()
                            .mapToDouble(Composer::getNote)
                            .average()
                            .orElse(0.0);

                    OptionalDouble minNote = notesSequence.stream()
                            .mapToDouble(Composer::getNote)
                            .min();

                    OptionalDouble maxNote = notesSequence.stream()
                            .mapToDouble(Composer::getNote)
                            .max();

                    long reussites = notesSequence.stream()
                            .mapToDouble(Composer::getNote)
                            .filter(note -> note >= 10.0)
                            .count();

                    double tauxReussite = notesSequence.isEmpty() ? 0.0 :
                            (double) reussites / notesSequence.size() * 100;

                    return MatiereDTOs.StatistiqueParSequence.builder()
                            .sequence(sequence)
                            .nombreEleves(notesSequence.size())
                            .moyenne(Math.round(moyenne * 100.0) / 100.0)
                            .noteMin(minNote.isPresent() ? Math.round(minNote.getAsDouble() * 100.0) / 100.0 : 0.0)
                            .noteMax(maxNote.isPresent() ? Math.round(maxNote.getAsDouble() * 100.0) / 100.0 : 0.0)
                            .tauxReussite(Math.round(tauxReussite * 100.0) / 100.0)
                            .build();
                })
                .sorted(Comparator.comparing(MatiereDTOs.StatistiqueParSequence::getSequence))
                .collect(Collectors.toList());
    }

    // Méthodes utilitaires
    default Integer countEnseignants(Matiere matiere) {
        if (matiere.getEnseignements() == null) return 0;
        return (int) matiere.getEnseignements().stream()
                .filter(enseigner -> enseigner.getIsActive())
                .count();
    }

    default Integer countEleves(Matiere matiere) {
        if (matiere.getCompositions() == null) return 0;
        return (int) matiere.getCompositions().stream()
                .map(Composer::getMatricule)
                .distinct()
                .count();
    }

    default Integer countNotesTotal(Matiere matiere) {
        if (matiere.getCompositions() == null) return 0;
        return matiere.getCompositions().size();
    }

    default Double calculateMoyenneGenerale(Matiere matiere) {
        if (matiere.getCompositions() == null || matiere.getCompositions().isEmpty()) return 0.0;

        double moyenne = matiere.getCompositions().stream()
                .mapToDouble(Composer::getNote)
                .average()
                .orElse(0.0);

        return Math.round(moyenne * 100.0) / 100.0;
    }

    default Double calculateTauxReussiteGlobal(Matiere matiere) {
        if (matiere.getCompositions() == null || matiere.getCompositions().isEmpty()) return 0.0;

        long totalNotes = matiere.getCompositions().size();
        long reussites = matiere.getCompositions().stream()
                .mapToDouble(Composer::getNote)
                .filter(note -> note >= 10.0)
                .count();

        double taux = (double) reussites / totalNotes * 100;
        return Math.round(taux * 100.0) / 100.0;
    }

    default List<MatiereDTOs.StatistiqueParSection> calculateStatistiquesParSection(Matiere matiere) {
        if (matiere.getCompositions() == null || matiere.getCompositions().isEmpty()) {
            return new ArrayList<>();
        }

        Map<String, List<Composer>> parSection = matiere.getCompositions().stream()
                .filter(composition -> composition.getEleve() != null &&
                        composition.getEleve().getSection() != null)
                .collect(Collectors.groupingBy(
                        composition -> composition.getEleve().getSection().getNom()
                ));

        return parSection.entrySet().stream()
                .map(entry -> {
                    String sectionNom = entry.getKey();
                    List<Composer> notesSection = entry.getValue();

                    double moyenne = notesSection.stream()
                            .mapToDouble(Composer::getNote)
                            .average()
                            .orElse(0.0);

                    long reussites = notesSection.stream()
                            .mapToDouble(Composer::getNote)
                            .filter(note -> note >= 10.0)
                            .count();

                    double tauxReussite = notesSection.isEmpty() ? 0.0 :
                            (double) reussites / notesSection.size() * 100;

                    int nombreEleves = (int) notesSection.stream()
                            .map(Composer::getMatricule)
                            .distinct()
                            .count();

                    return MatiereDTOs.StatistiqueParSection.builder()
                            .section(sectionNom)
                            .nombreEleves(nombreEleves)
                            .moyenne(Math.round(moyenne * 100.0) / 100.0)
                            .tauxReussite(Math.round(tauxReussite * 100.0) / 100.0)
                            .nombreEnseignants(0) // À calculer séparément si nécessaire
                            .build();
                })
                .sorted(Comparator.comparing(MatiereDTOs.StatistiqueParSection::getSection))
                .collect(Collectors.toList());
    }
}