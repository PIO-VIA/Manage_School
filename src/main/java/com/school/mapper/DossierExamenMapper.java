package com.school.mapper;

import com.school.dto.DossierExamenDTOs;
import com.school.entity.DossierExamen;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface DossierExamenMapper {

    @Mapping(target = "eleve", source = "eleve", qualifiedByName = "toEleveInfo")
    @Mapping(target = "personnelAdministratif", source = "personnelAdministratif", qualifiedByName = "toPersonnelAdministratifInfo")
    DossierExamenDTOs.DossierExamenResponse toResponse(DossierExamen dossier);

    List<DossierExamenDTOs.DossierExamenResponse> toResponseList(List<DossierExamen> dossiers);

    @Named("toEleveInfo")
    default DossierExamenDTOs.EleveInfo toEleveInfo(com.school.entity.Eleve eleve) {
        if (eleve == null) return null;
        return DossierExamenDTOs.EleveInfo.builder()
                .matricule(eleve.getMatricule())
                .nom(eleve.getNom())
                .prenom(eleve.getPrenom())
                .classe(eleve.getSalleDeClasse() != null ? eleve.getSalleDeClasse().getNom() : "")
                .section(eleve.getSection() != null ? eleve.getSection().getNom() : "")
                .build();
    }

    @Named("toPersonnelAdministratifInfo")
    default DossierExamenDTOs.PersonnelAdministratifInfo toPersonnelAdministratifInfo(
            com.school.entity.PersonnelAdministratif personnel) {
        if (personnel == null) return null;
        return DossierExamenDTOs.PersonnelAdministratifInfo.builder()
                .idAdmin(personnel.getIdAdmin())
                .nom(personnel.getNom())
                .prenom(personnel.getPrenom())
                .email(personnel.getEmail())
                .build();
    }

    default DossierExamenDTOs.DossierExamenStatsResponse toStatsResponse(List<DossierExamen> dossiers) {
        if (dossiers == null || dossiers.isEmpty()) {
            return DossierExamenDTOs.DossierExamenStatsResponse.builder()
                    .totalDossiers(0L)
                    .dossiersEnCours(0L)
                    .dossiersTermines(0L)
                    .dossiersEnAttente(0L)
                    .dossiersRejetes(0L)
                    .statistiquesParClasse(List.of())
                    .statistiquesParMois(List.of())
                    .statistiquesParTypeExamen(List.of())
                    .build();
        }

        long totalDossiers = dossiers.size();

        // Statistiques par état
        Map<DossierExamen.EtatDossier, Long> parEtat = dossiers.stream()
                .collect(Collectors.groupingBy(DossierExamen::getEtat, Collectors.counting()));

        long dossiersEnCours = parEtat.getOrDefault(DossierExamen.EtatDossier.EN_COURS, 0L);
        long dossiersTermines = parEtat.getOrDefault(DossierExamen.EtatDossier.TERMINE, 0L);
        long dossiersEnAttente = parEtat.getOrDefault(DossierExamen.EtatDossier.EN_ATTENTE, 0L);
        long dossiersRejetes = parEtat.getOrDefault(DossierExamen.EtatDossier.REJETE, 0L);

        // Statistiques par classe
        Map<String, List<DossierExamen>> parClasse = dossiers.stream()
                .filter(d -> d.getEleve() != null && d.getEleve().getSalleDeClasse() != null)
                .collect(Collectors.groupingBy(d -> d.getEleve().getSalleDeClasse().getNom()));

        List<DossierExamenDTOs.StatistiqueParClasse> statsParClasse = parClasse.entrySet().stream()
                .map(entry -> {
                    String classe = entry.getKey();
                    List<DossierExamen> dossiersClasse = entry.getValue();
                    String section = dossiersClasse.get(0).getEleve().getSection() != null ?
                            dossiersClasse.get(0).getEleve().getSection().getNom() : "";

                    long nombreElevesImpliques = dossiersClasse.stream()
                            .map(d -> d.getEleve().getMatricule())
                            .distinct()
                            .count();

                    return DossierExamenDTOs.StatistiqueParClasse.builder()
                            .classe(classe)
                            .section(section)
                            .nombreDossiers((long) dossiersClasse.size())
                            .nombreElevesImpliques(nombreElevesImpliques)
                            .build();
                })
                .collect(Collectors.toList());

        // Statistiques par mois
        Map<String, Long> parMois = dossiers.stream()
                .collect(Collectors.groupingBy(
                        d -> d.getDateDepot().format(DateTimeFormatter.ofPattern("yyyy-MM")),
                        Collectors.counting()
                ));

        List<DossierExamenDTOs.StatistiqueParMois> statsParMois = parMois.entrySet().stream()
                .map(entry -> {
                    String mois = entry.getKey();
                    Long nombre = entry.getValue();

                    return DossierExamenDTOs.StatistiqueParMois.builder()
                            .mois(mois)
                            .nombreDossiers(nombre)
                            .nombreDepots(nombre)
                            .build();
                })
                .sorted((s1, s2) -> s1.getMois().compareTo(s2.getMois()))
                .collect(Collectors.toList());

        // Statistiques par type d'examen (basé sur le nom de l'examen)
        Map<String, Long> parTypeExamen = dossiers.stream()
                .collect(Collectors.groupingBy(
                        d -> categoriserTypeExamen(d.getExamen()),
                        Collectors.counting()
                ));

        List<DossierExamenDTOs.StatistiqueParTypeExamen> statsParTypeExamen = parTypeExamen.entrySet().stream()
                .map(entry -> {
                    String typeExamen = entry.getKey();
                    Long nombre = entry.getValue();
                    double pourcentage = totalDossiers > 0 ? (double) nombre / totalDossiers * 100 : 0.0;

                    return DossierExamenDTOs.StatistiqueParTypeExamen.builder()
                            .typeExamen(typeExamen)
                            .nombreDossiers(nombre)
                            .pourcentage(Math.round(pourcentage * 100.0) / 100.0)
                            .build();
                })
                .collect(Collectors.toList());

        return DossierExamenDTOs.DossierExamenStatsResponse.builder()
                .totalDossiers(totalDossiers)
                .dossiersEnCours(dossiersEnCours)
                .dossiersTermines(dossiersTermines)
                .dossiersEnAttente(dossiersEnAttente)
                .dossiersRejetes(dossiersRejetes)
                .statistiquesParClasse(statsParClasse)
                .statistiquesParMois(statsParMois)
                .statistiquesParTypeExamen(statsParTypeExamen)
                .build();
    }

    // Méthode utilitaire pour catégoriser les types d'examens
    default String categoriserTypeExamen(String examen) {
        if (examen == null) return "Autre";

        String examenLower = examen.toLowerCase();

        if (Pattern.matches(".*\\b(cep|certificat|etude|primaire)\\b.*", examenLower)) {
            return "CEP";
        } else if (Pattern.matches(".*\\b(sequence|devoir|controle)\\b.*", examenLower)) {
            return "Évaluation";
        } else if (Pattern.matches(".*\\b(concours|competition)\\b.*", examenLower)) {
            return "Concours";
        } else if (Pattern.matches(".*\\b(medical|sante|visite)\\b.*", examenLower)) {
            return "Médical";
        } else {
            return "Autre";
        }
    }
}