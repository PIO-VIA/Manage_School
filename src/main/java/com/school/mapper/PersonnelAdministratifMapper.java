package com.school.mapper;

import com.school.dto.PersonnelAdministratifDTOs;
import com.school.entity.PersonnelAdministratif;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface PersonnelAdministratifMapper {

    @Mapping(target = "anneesService", expression = "java(calculateAnneesService(personnel))")
    PersonnelAdministratifDTOs.PersonnelAdministratifResponse toResponse(PersonnelAdministratif personnel);

    @Mapping(target = "anneesService", expression = "java(calculateAnneesService(personnel))")
    @Mapping(target = "dossiersExamen", source = "dossiersExamen", qualifiedByName = "toDossierInfoList")
    @Mapping(target = "dossiersDiscipline", source = "dossiersDiscipline", qualifiedByName = "toDossierDisciplineInfoList")
    @Mapping(target = "dossiersInscription", source = "dossiersInscription", qualifiedByName = "toDossierInscriptionInfoList")
    @Mapping(target = "statistiquesActivite", expression = "java(calculateStatistiquesActivite(personnel))")
    PersonnelAdministratifDTOs.PersonnelAdministratifDetailResponse toDetailResponse(PersonnelAdministratif personnel);

    List<PersonnelAdministratifDTOs.PersonnelAdministratifResponse> toResponseList(List<PersonnelAdministratif> personnel);

    @Named("toDossierInfoList")
    default List<PersonnelAdministratifDTOs.DossierInfo> toDossierInfoList(List<com.school.entity.DossierExamen> dossiers) {
        if (dossiers == null) return null;
        return dossiers.stream()
                .filter(dossier -> dossier.getIsActive())
                .map(dossier -> PersonnelAdministratifDTOs.DossierInfo.builder()
                        .idDossier(dossier.getIdExamen())
                        .type("EXAMEN")
                        .description(dossier.getExamen())
                        .etat(dossier.getEtat().name())
                        .date(dossier.getDateDepot())
                        .eleveNom(dossier.getEleve() != null ?
                                dossier.getEleve().getNom() + " " +
                                        (dossier.getEleve().getPrenom() != null ? dossier.getEleve().getPrenom() : "") : "")
                        .eleveMatricule(dossier.getEleve() != null ? dossier.getEleve().getMatricule() : "")
                        .build())
                .collect(Collectors.toList());
    }

    @Named("toDossierDisciplineInfoList")
    default List<PersonnelAdministratifDTOs.DossierInfo> toDossierDisciplineInfoList(List<com.school.entity.DossierDiscipline> dossiers) {
        if (dossiers == null) return null;
        return dossiers.stream()
                .filter(dossier -> dossier.getIsActive())
                .map(dossier -> PersonnelAdministratifDTOs.DossierInfo.builder()
                        .idDossier(dossier.getIdDiscipline())
                        .type("DISCIPLINE")
                        .description(dossier.getConvocation())
                        .etat(dossier.getEtat().name())
                        .date(dossier.getDateIncident())
                        .eleveNom(dossier.getEleve() != null ?
                                dossier.getEleve().getNom() + " " +
                                        (dossier.getEleve().getPrenom() != null ? dossier.getEleve().getPrenom() : "") : "")
                        .eleveMatricule(dossier.getEleve() != null ? dossier.getEleve().getMatricule() : "")
                        .build())
                .collect(Collectors.toList());
    }

    @Named("toDossierInscriptionInfoList")
    default List<PersonnelAdministratifDTOs.DossierInfo> toDossierInscriptionInfoList(List<com.school.entity.DossierInscription> dossiers) {
        if (dossiers == null) return null;
        return dossiers.stream()
                .filter(dossier -> dossier.getIsActive())
                .map(dossier -> PersonnelAdministratifDTOs.DossierInfo.builder()
                        .idDossier(dossier.getIdInscription())
                        .type("INSCRIPTION")
                        .description("Inscription " + dossier.getAnneeScolaire())
                        .etat(dossier.getEtat().name())
                        .date(dossier.getDateInscription())
                        .eleveNom(dossier.getEleve() != null ?
                                dossier.getEleve().getNom() + " " +
                                        (dossier.getEleve().getPrenom() != null ? dossier.getEleve().getPrenom() : "") : "")
                        .eleveMatricule(dossier.getEleve() != null ? dossier.getEleve().getMatricule() : "")
                        .build())
                .collect(Collectors.toList());
    }

    default PersonnelAdministratifDTOs.PersonnelAdministratifStatsResponse toStatsResponse(List<PersonnelAdministratif> personnelList) {
        if (personnelList == null || personnelList.isEmpty()) {
            return PersonnelAdministratifDTOs.PersonnelAdministratifStatsResponse.builder()
                    .totalPersonnel(0L)
                    .personnelActif(0L)
                    .personnelInactif(0L)
                    .admins(0L)
                    .superAdmins(0L)
                    .statistiquesParRole(List.of())
                    .statistiquesParAnnee(List.of())
                    .moyenneAnneesService(0.0)
                    .build();
        }

        long totalPersonnel = personnelList.size();
        long personnelActif = personnelList.stream().mapToLong(p -> p.getIsActive() ? 1 : 0).sum();
        long personnelInactif = totalPersonnel - personnelActif;

        // Statistiques par rôle
        Map<PersonnelAdministratif.Role, Long> parRole = personnelList.stream()
                .collect(Collectors.groupingBy(PersonnelAdministratif::getRole, Collectors.counting()));

        long admins = parRole.getOrDefault(PersonnelAdministratif.Role.ADMIN, 0L);
        long superAdmins = parRole.getOrDefault(PersonnelAdministratif.Role.SUPER_ADMIN, 0L);

        List<PersonnelAdministratifDTOs.StatistiqueParRole> statsParRole = parRole.entrySet().stream()
                .map(entry -> {
                    PersonnelAdministratif.Role role = entry.getKey();
                    Long nombre = entry.getValue();
                    double pourcentage = totalPersonnel > 0 ? (double) nombre / totalPersonnel * 100 : 0.0;

                    return PersonnelAdministratifDTOs.StatistiqueParRole.builder()
                            .role(role)
                            .nombre(nombre)
                            .pourcentage(Math.round(pourcentage * 100.0) / 100.0)
                            .build();
                })
                .collect(Collectors.toList());

        // Statistiques par année d'embauche
        Map<String, Long> parAnnee = personnelList.stream()
                .filter(p -> p.getDatePriseService() != null)
                .collect(Collectors.groupingBy(
                        p -> String.valueOf(p.getDatePriseService().getYear()),
                        Collectors.counting()
                ));

        List<PersonnelAdministratifDTOs.StatistiqueParAnnee> statsParAnnee = parAnnee.entrySet().stream()
                .map(entry -> PersonnelAdministratifDTOs.StatistiqueParAnnee.builder()
                        .annee(entry.getKey())
                        .nombreEmbauches(entry.getValue())
                        .build())
                .sorted((s1, s2) -> s1.getAnnee().compareTo(s2.getAnnee()))
                .collect(Collectors.toList());

        // Moyenne années de service
        double moyenneAnneesService = personnelList.stream()
                .filter(p -> p.getDatePriseService() != null)
                .mapToInt(this::calculateAnneesService)
                .average()
                .orElse(0.0);

        return PersonnelAdministratifDTOs.PersonnelAdministratifStatsResponse.builder()
                .totalPersonnel(totalPersonnel)
                .personnelActif(personnelActif)
                .personnelInactif(personnelInactif)
                .admins(admins)
                .superAdmins(superAdmins)
                .statistiquesParRole(statsParRole)
                .statistiquesParAnnee(statsParAnnee)
                .moyenneAnneesService(Math.round(moyenneAnneesService * 100.0) / 100.0)
                .build();
    }

    // Méthodes utilitaires
    default Integer calculateAnneesService(PersonnelAdministratif personnel) {
        if (personnel.getDatePriseService() == null) return 0;
        LocalDate today = LocalDate.now();
        return Period.between(personnel.getDatePriseService(), today).getYears();
    }

    default PersonnelAdministratifDTOs.StatistiquesActivite calculateStatistiquesActivite(PersonnelAdministratif personnel) {
        if (personnel == null) return null;

        int totalDossiersExamen = personnel.getDossiersExamen() != null ?
                (int) personnel.getDossiersExamen().stream().filter(d -> d.getIsActive()).count() : 0;

        int totalDossiersDiscipline = personnel.getDossiersDiscipline() != null ?
                (int) personnel.getDossiersDiscipline().stream().filter(d -> d.getIsActive()).count() : 0;

        int totalDossiersInscription = personnel.getDossiersInscription() != null ?
                (int) personnel.getDossiersInscription().stream().filter(d -> d.getIsActive()).count() : 0;

        // Compter les dossiers en cours
        long dossiersEnCours = 0;
        long dossiersTermines = 0;

        if (personnel.getDossiersExamen() != null) {
            dossiersEnCours += personnel.getDossiersExamen().stream()
                    .filter(d -> d.getIsActive() &&
                            (d.getEtat() == com.school.entity.DossierExamen.EtatDossier.EN_COURS ||
                                    d.getEtat() == com.school.entity.DossierExamen.EtatDossier.EN_ATTENTE))
                    .count();
            dossiersTermines += personnel.getDossiersExamen().stream()
                    .filter(d -> d.getIsActive() && d.getEtat() == com.school.entity.DossierExamen.EtatDossier.TERMINE)
                    .count();
        }

        if (personnel.getDossiersDiscipline() != null) {
            dossiersEnCours += personnel.getDossiersDiscipline().stream()
                    .filter(d -> d.getIsActive() &&
                            (d.getEtat() == com.school.entity.DossierExamen.EtatDossier.EN_COURS ||
                                    d.getEtat() == com.school.entity.DossierExamen.EtatDossier.EN_ATTENTE))
                    .count();
            dossiersTermines += personnel.getDossiersDiscipline().stream()
                    .filter(d -> d.getIsActive() && d.getEtat() == com.school.entity.DossierExamen.EtatDossier.TERMINE)
                    .count();
        }

        // Trouver la date du dernier dossier
        LocalDate derniereDossier = null;

        if (personnel.getDossiersExamen() != null) {
            derniereDossier = personnel.getDossiersExamen().stream()
                    .filter(d -> d.getIsActive())
                    .map(d -> d.getDateDepot())
                    .max(LocalDate::compareTo)
                    .orElse(derniereDossier);
        }

        if (personnel.getDossiersDiscipline() != null) {
            LocalDate derniereDiscipline = personnel.getDossiersDiscipline().stream()
                    .filter(d -> d.getIsActive())
                    .map(d -> d.getDateIncident())
                    .max(LocalDate::compareTo)
                    .orElse(null);

            if (derniereDiscipline != null && (derniereDossier == null || derniereDiscipline.isAfter(derniereDossier))) {
                derniereDossier = derniereDiscipline;
            }
        }

        if (personnel.getDossiersInscription() != null) {
            LocalDate derniereInscription = personnel.getDossiersInscription().stream()
                    .filter(d -> d.getIsActive())
                    .map(d -> d.getDateInscription())
                    .max(LocalDate::compareTo)
                    .orElse(null);

            if (derniereInscription != null && (derniereDossier == null || derniereInscription.isAfter(derniereDossier))) {
                derniereDossier = derniereInscription;
            }
        }

        return PersonnelAdministratifDTOs.StatistiquesActivite.builder()
                .totalDossiersExamen(totalDossiersExamen)
                .totalDossiersDiscipline(totalDossiersDiscipline)
                .totalDossiersInscription(totalDossiersInscription)
                .dossiersEnCours((int) dossiersEnCours)
                .dossiersTermines((int) dossiersTermines)
                .derniereDossier(derniereDossier)
                .build();
    }
}