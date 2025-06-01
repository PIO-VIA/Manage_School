package com.school.mapper;

import com.school.dto.DossierInscriptionDTOs;
import com.school.entity.DossierInscription;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface DossierInscriptionMapper {

    @Mapping(target = "pourcentagePaye", expression = "java(calculatePourcentagePaye(dossier))")
    @Mapping(target = "eleve", source = "eleve", qualifiedByName = "toEleveInfo")
    @Mapping(target = "personnelAdministratif", source = "personnelAdministratif", qualifiedByName = "toPersonnelAdministratifInfo")
    DossierInscriptionDTOs.DossierInscriptionResponse toResponse(DossierInscription dossier);

    List<DossierInscriptionDTOs.DossierInscriptionResponse> toResponseList(List<DossierInscription> dossiers);

    @Named("toEleveInfo")
    default DossierInscriptionDTOs.EleveInfo toEleveInfo(com.school.entity.Eleve eleve) {
        if (eleve == null) return null;
        return DossierInscriptionDTOs.EleveInfo.builder()
                .matricule(eleve.getMatricule())
                .nom(eleve.getNom())
                .prenom(eleve.getPrenom())
                .classe(eleve.getSalleDeClasse() != null ? eleve.getSalleDeClasse().getNom() : "")
                .section(eleve.getSection() != null ? eleve.getSection().getNom() : "")
                .nomTuteur(eleve.getNomTuteur())
                .emailTuteur(eleve.getEmailTuteur())
                .build();
    }

    @Named("toPersonnelAdministratifInfo")
    default DossierInscriptionDTOs.PersonnelAdministratifInfo toPersonnelAdministratifInfo(
            com.school.entity.PersonnelAdministratif personnel) {
        if (personnel == null) return null;
        return DossierInscriptionDTOs.PersonnelAdministratifInfo.builder()
                .idAdmin(personnel.getIdAdmin())
                .nom(personnel.getNom())
                .prenom(personnel.getPrenom())
                .email(personnel.getEmail())
                .build();
    }

    default DossierInscriptionDTOs.DossierInscriptionStatsResponse toStatsResponse(List<DossierInscription> dossiers) {
        if (dossiers == null || dossiers.isEmpty()) {
            return DossierInscriptionDTOs.DossierInscriptionStatsResponse.builder()
                    .totalInscriptions(0L)
                    .inscriptionsCompletes(0L)
                    .inscriptionsPartielles(0L)
                    .inscriptionsEnAttente(0L)
                    .inscriptionsExpirees(0L)
                    .totalSommesAPayer(BigDecimal.ZERO)
                    .totalSommesVersees(BigDecimal.ZERO)
                    .totalRestes(BigDecimal.ZERO)
                    .tauxRecouvrement(0.0)
                    .statistiquesParAnneeScolaire(List.of())
                    .statistiquesParSection(List.of())
                    .statistiquesParMois(List.of())
                    .build();
        }

        long totalInscriptions = dossiers.size();

        // Statistiques par état
        Map<DossierInscription.EtatInscription, Long> parEtat = dossiers.stream()
                .collect(Collectors.groupingBy(DossierInscription::getEtat, Collectors.counting()));

        long inscriptionsCompletes = parEtat.getOrDefault(DossierInscription.EtatInscription.COMPLETE, 0L);
        long inscriptionsPartielles = parEtat.getOrDefault(DossierInscription.EtatInscription.PARTIELLE, 0L);
        long inscriptionsEnAttente = parEtat.getOrDefault(DossierInscription.EtatInscription.EN_ATTENTE, 0L);
        long inscriptionsExpirees = parEtat.getOrDefault(DossierInscription.EtatInscription.EXPIREE, 0L);

        // Calculs financiers
        BigDecimal totalSommesAPayer = dossiers.stream()
                .map(DossierInscription::getSommeAPayer)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalSommesVersees = dossiers.stream()
                .map(DossierInscription::getSommeVersee)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalRestes = dossiers.stream()
                .map(DossierInscription::getReste)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        double tauxRecouvrement = totalSommesAPayer.compareTo(BigDecimal.ZERO) > 0 ?
                totalSommesVersees.divide(totalSommesAPayer, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100)).doubleValue() : 0.0;

        // Statistiques par année scolaire
        Map<String, List<DossierInscription>> parAnneeScolaire = dossiers.stream()
                .collect(Collectors.groupingBy(DossierInscription::getAnneeScolaire));

        List<DossierInscriptionDTOs.StatistiqueParAnneeScolaire> statsParAnneeScolaire = parAnneeScolaire.entrySet().stream()
                .map(entry -> {
                    String anneeScolaire = entry.getKey();
                    List<DossierInscription> dossiersAnnee = entry.getValue();

                    BigDecimal totalAPayer = dossiersAnnee.stream()
                            .map(DossierInscription::getSommeAPayer)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    BigDecimal totalVerse = dossiersAnnee.stream()
                            .map(DossierInscription::getSommeVersee)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    double tauxRecouvrementAnnee = totalAPayer.compareTo(BigDecimal.ZERO) > 0 ?
                            totalVerse.divide(totalAPayer, 4, RoundingMode.HALF_UP)
                                    .multiply(BigDecimal.valueOf(100)).doubleValue() : 0.0;

                    return DossierInscriptionDTOs.StatistiqueParAnneeScolaire.builder()
                            .anneeScolaire(anneeScolaire)
                            .nombreInscriptions((long) dossiersAnnee.size())
                            .totalSommesAPayer(totalAPayer)
                            .totalSommesVersees(totalVerse)
                            .tauxRecouvrement(Math.round(tauxRecouvrementAnnee * 100.0) / 100.0)
                            .build();
                })
                .sorted((s1, s2) -> s1.getAnneeScolaire().compareTo(s2.getAnneeScolaire()))
                .collect(Collectors.toList());

        // Statistiques par section
        Map<String, List<DossierInscription>> parSection = dossiers.stream()
                .filter(d -> d.getEleve() != null && d.getEleve().getSection() != null)
                .collect(Collectors.groupingBy(d -> d.getEleve().getSection().getNom()));

        List<DossierInscriptionDTOs.StatistiqueParSection> statsParSection = parSection.entrySet().stream()
                .map(entry -> {
                    String section = entry.getKey();
                    List<DossierInscription> dossiersSection = entry.getValue();

                    BigDecimal moyenneSommeAPayer = dossiersSection.stream()
                            .map(DossierInscription::getSommeAPayer)
                            .reduce(BigDecimal.ZERO, BigDecimal::add)
                            .divide(BigDecimal.valueOf(dossiersSection.size()), 2, RoundingMode.HALF_UP);

                    BigDecimal totalAPayer = dossiersSection.stream()
                            .map(DossierInscription::getSommeAPayer)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    BigDecimal totalVerse = dossiersSection.stream()
                            .map(DossierInscription::getSommeVersee)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    double tauxRecouvrementSection = totalAPayer.compareTo(BigDecimal.ZERO) > 0 ?
                            totalVerse.divide(totalAPayer, 4, RoundingMode.HALF_UP)
                                    .multiply(BigDecimal.valueOf(100)).doubleValue() : 0.0;

                    return DossierInscriptionDTOs.StatistiqueParSection.builder()
                            .section(section)
                            .nombreInscriptions((long) dossiersSection.size())
                            .moyenneSommeAPayer(moyenneSommeAPayer)
                            .tauxRecouvrement(Math.round(tauxRecouvrementSection * 100.0) / 100.0)
                            .build();
                })
                .collect(Collectors.toList());

        // Statistiques par mois
        Map<String, List<DossierInscription>> parMois = dossiers.stream()
                .filter(d -> d.getDateInscription() != null)
                .collect(Collectors.groupingBy(
                        d -> d.getDateInscription().format(DateTimeFormatter.ofPattern("yyyy-MM"))
                ));

        List<DossierInscriptionDTOs.StatistiqueParMois> statsParMois = parMois.entrySet().stream()
                .map(entry -> {
                    String mois = entry.getKey();
                    List<DossierInscription> dossiersMois = entry.getValue();

                    BigDecimal totalPaiements = dossiersMois.stream()
                            .map(DossierInscription::getSommeVersee)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    long nombrePaiements = dossiersMois.stream()
                            .filter(d -> d.getSommeVersee().compareTo(BigDecimal.ZERO) > 0)
                            .count();

                    return DossierInscriptionDTOs.StatistiqueParMois.builder()
                            .mois(mois)
                            .nombreInscriptions((long) dossiersMois.size())
                            .totalPaiements(totalPaiements)
                            .nombrePaiements(nombrePaiements)
                            .build();
                })
                .sorted((s1, s2) -> s1.getMois().compareTo(s2.getMois()))
                .collect(Collectors.toList());

        return DossierInscriptionDTOs.DossierInscriptionStatsResponse.builder()
                .totalInscriptions(totalInscriptions)
                .inscriptionsCompletes(inscriptionsCompletes)
                .inscriptionsPartielles(inscriptionsPartielles)
                .inscriptionsEnAttente(inscriptionsEnAttente)
                .inscriptionsExpirees(inscriptionsExpirees)
                .totalSommesAPayer(totalSommesAPayer)
                .totalSommesVersees(totalSommesVersees)
                .totalRestes(totalRestes)
                .tauxRecouvrement(Math.round(tauxRecouvrement * 100.0) / 100.0)
                .statistiquesParAnneeScolaire(statsParAnneeScolaire)
                .statistiquesParSection(statsParSection)
                .statistiquesParMois(statsParMois)
                .build();
    }

    // Méthodes utilitaires
    default Double calculatePourcentagePaye(DossierInscription dossier) {
        if (dossier.getSommeAPayer() == null || dossier.getSommeAPayer().compareTo(BigDecimal.ZERO) == 0) {
            return 0.0;
        }

        double pourcentage = dossier.getSommeVersee()
                .divide(dossier.getSommeAPayer(), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();

        return Math.round(pourcentage * 100.0) / 100.0;
    }
}