package com.school.mapper;

import com.school.dto.DossierDisciplineDTOs;
import com.school.entity.DossierDiscipline;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface DossierDisciplineMapper {

    @Mapping(target = "eleve", source = "eleve", qualifiedByName = "toEleveInfo")
    @Mapping(target = "personnelAdministratif", source = "personnelAdministratif", qualifiedByName = "toPersonnelAdministratifInfo")
    @Mapping(target = "nombreAbsencesLiees", expression = "java(countAbsencesLiees(dossier))")
    DossierDisciplineDTOs.DossierDisciplineResponse toResponse(DossierDiscipline dossier);

    @Mapping(target = "eleve", source = "eleve", qualifiedByName = "toEleveInfo")
    @Mapping(target = "personnelAdministratif", source = "personnelAdministratif", qualifiedByName = "toPersonnelAdministratifInfo")
    @Mapping(target = "absencesLiees", source = "absences", qualifiedByName = "toAbsenceInfoList")
    DossierDisciplineDTOs.DossierDisciplineDetailResponse toDetailResponse(DossierDiscipline dossier);

    List<DossierDisciplineDTOs.DossierDisciplineResponse> toResponseList(List<DossierDiscipline> dossiers);

    @Named("toEleveInfo")
    default DossierDisciplineDTOs.EleveInfo toEleveInfo(com.school.entity.Eleve eleve) {
        if (eleve == null) return null;
        return DossierDisciplineDTOs.EleveInfo.builder()
                .matricule(eleve.getMatricule())
                .nom(eleve.getNom())
                .prenom(eleve.getPrenom())
                .classe(eleve.getSalleDeClasse() != null ? eleve.getSalleDeClasse().getNom() : "")
                .section(eleve.getSection() != null ? eleve.getSection().getNom() : "")
                .build();
    }

    @Named("toPersonnelAdministratifInfo")
    default DossierDisciplineDTOs.PersonnelAdministratifInfo toPersonnelAdministratifInfo(
            com.school.entity.PersonnelAdministratif personnel) {
        if (personnel == null) return null;
        return DossierDisciplineDTOs.PersonnelAdministratifInfo.builder()
                .idAdmin(personnel.getIdAdmin())
                .nom(personnel.getNom())
                .prenom(personnel.getPrenom())
                .email(personnel.getEmail())
                .build();
    }

    @Named("toAbsenceInfoList")
    default List<DossierDisciplineDTOs.AbsenceInfo> toAbsenceInfoList(List<com.school.entity.Absence> absences) {
        if (absences == null) return null;
        return absences.stream()
                .filter(absence -> absence.getIsActive())
                .map(absence -> DossierDisciplineDTOs.AbsenceInfo.builder()
                        .idAbsence(absence.getIdAbsence())
                        .jour(absence.getJour())
                        .typeAbsence(absence.getTypeAbsence().name())
                        .motif(absence.getMotif())
                        .justifiee(absence.getJustifiee())
                        .build())
                .toList();
    }

    default DossierDisciplineDTOs.DossierDisciplineStatsResponse toStatsResponse(List<DossierDiscipline> dossiers) {
        if (dossiers == null || dossiers.isEmpty()) {
            return DossierDisciplineDTOs.DossierDisciplineStatsResponse.builder()
                    .totalDossiers(0L)
                    .dossiersEnCours(0L)
                    .dossiersTermines(0L)
                    .dossiersEnAttente(0L)
                    .dossiersRejetes(0L)
                    .statistiquesParSanction(List.of())
                    .statistiquesParClasse(List.of())
                    .statistiquesParMois(List.of())
                    .build();
        }

        long totalDossiers = dossiers.size();

        // Statistiques par état
        Map<com.school.entity.DossierExamen.EtatDossier, Long> parEtat = dossiers.stream()
                .collect(Collectors.groupingBy(DossierDiscipline::getEtat, Collectors.counting()));

        long dossiersEnCours = parEtat.getOrDefault(com.school.entity.DossierExamen.EtatDossier.EN_COURS, 0L);
        long dossiersTermines = parEtat.getOrDefault(com.school.entity.DossierExamen.EtatDossier.TERMINE, 0L);
        long dossiersEnAttente = parEtat.getOrDefault(com.school.entity.DossierExamen.EtatDossier.EN_ATTENTE, 0L);
        long dossiersRejetes = parEtat.getOrDefault(com.school.entity.DossierExamen.EtatDossier.REJETE, 0L);

        // Statistiques par sanction
        Map<DossierDiscipline.TypeSanction, Long> parSanction = dossiers.stream()
                .collect(Collectors.groupingBy(DossierDiscipline::getSanction, Collectors.counting()));

        List<DossierDisciplineDTOs.StatistiqueParSanction> statsParSanction = parSanction.entrySet().stream()
                .map(entry -> {
                    DossierDiscipline.TypeSanction sanction = entry.getKey();
                    Long nombre = entry.getValue();
                    double pourcentage = totalDossiers > 0 ? (double) nombre / totalDossiers * 100 : 0.0;

                    return DossierDisciplineDTOs.StatistiqueParSanction.builder()
                            .sanction(sanction)
                            .nombreDossiers(nombre)
                            .pourcentage(Math.round(pourcentage * 100.0) / 100.0)
                            .build();
                })
                .collect(Collectors.toList());

        // Statistiques par classe
        Map<String, List<DossierDiscipline>> parClasse = dossiers.stream()
                .filter(d -> d.getEleve() != null && d.getEleve().getSalleDeClasse() != null)
                .collect(Collectors.groupingBy(d -> d.getEleve().getSalleDeClasse().getNom()));

        List<DossierDisciplineDTOs.StatistiqueParClasse> statsParClasse = parClasse.entrySet().stream()
                .map(entry -> {
                    String classe = entry.getKey();
                    List<DossierDiscipline> dossiersClasse = entry.getValue();
                    String section = dossiersClasse.get(0).getEleve().getSection() != null ?
                            dossiersClasse.get(0).getEleve().getSection().getNom() : "";

                    long nombreElevesImpliques = dossiersClasse.stream()
                            .map(d -> d.getEleve().getMatricule())
                            .distinct()
                            .count();

                    return DossierDisciplineDTOs.StatistiqueParClasse.builder()
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
                        d -> d.getDateIncident().format(DateTimeFormatter.ofPattern("yyyy-MM")),
                        Collectors.counting()
                ));

        List<DossierDisciplineDTOs.StatistiqueParMois> statsParMois = parMois.entrySet().stream()
                .map(entry -> {
                    String mois = entry.getKey();
                    Long nombre = entry.getValue();

                    return DossierDisciplineDTOs.StatistiqueParMois.builder()
                            .mois(mois)
                            .nombreDossiers(nombre)
                            .nombreIncidents(nombre) // Un dossier = un incident
                            .build();
                })
                .sorted((s1, s2) -> s1.getMois().compareTo(s2.getMois()))
                .collect(Collectors.toList());

        return DossierDisciplineDTOs.DossierDisciplineStatsResponse.builder()
                .totalDossiers(totalDossiers)
                .dossiersEnCours(dossiersEnCours)
                .dossiersTermines(dossiersTermines)
                .dossiersEnAttente(dossiersEnAttente)
                .dossiersRejetes(dossiersRejetes)
                .statistiquesParSanction(statsParSanction)
                .statistiquesParClasse(statsParClasse)
                .statistiquesParMois(statsParMois)
                .build();
    }

    // Méthodes utilitaires
    default Integer countAbsencesLiees(DossierDiscipline dossier) {
        if (dossier.getAbsences() == null) return 0;
        return (int) dossier.getAbsences().stream()
                .filter(absence -> absence.getIsActive())
                .count();
    }
}