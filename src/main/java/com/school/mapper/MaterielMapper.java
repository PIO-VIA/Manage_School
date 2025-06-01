package com.school.mapper;

import com.school.dto.MaterielDTOs;
import com.school.entity.Materiel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface MaterielMapper {

    @Mapping(target = "personnelEntretien", source = "personnelEntretien", qualifiedByName = "toPersonnelEntretienInfo")
    MaterielDTOs.MaterielResponse toResponse(Materiel materiel);

    List<MaterielDTOs.MaterielResponse> toResponseList(List<Materiel> materiels);

    @Named("toPersonnelEntretienInfo")
    default MaterielDTOs.PersonnelEntretienInfo toPersonnelEntretienInfo(com.school.entity.PersonnelEntretien personnel) {
        if (personnel == null) return null;
        return MaterielDTOs.PersonnelEntretienInfo.builder()
                .idEntretien(personnel.getIdEntretien())
                .nom(personnel.getNom())
                .prenom(personnel.getPrenom())
                .lieuService(personnel.getLieuService())
                .build();
    }

    default MaterielDTOs.MaterielStatsResponse toStatsResponse(List<Materiel> materiels) {
        if (materiels == null || materiels.isEmpty()) {
            return MaterielDTOs.MaterielStatsResponse.builder()
                    .totalMateriels(0L)
                    .materielNeuf(0L)
                    .materielBon(0L)
                    .materielMoyen(0L)
                    .materielMauvais(0L)
                    .materielHorsService(0L)
                    .quantiteTotale(0)
                    .statistiquesParPersonnel(List.of())
                    .statistiquesParEtat(List.of())
                    .build();
        }

        long totalMateriels = materiels.size();

        // Statistiques par état
        Map<Materiel.EtatMateriel, List<Materiel>> parEtat = materiels.stream()
                .collect(Collectors.groupingBy(Materiel::getEtat));

        long materielNeuf = parEtat.getOrDefault(Materiel.EtatMateriel.NEUF, List.of()).size();
        long materielBon = parEtat.getOrDefault(Materiel.EtatMateriel.BON, List.of()).size();
        long materielMoyen = parEtat.getOrDefault(Materiel.EtatMateriel.MOYEN, List.of()).size();
        long materielMauvais = parEtat.getOrDefault(Materiel.EtatMateriel.MAUVAIS, List.of()).size();
        long materielHorsService = parEtat.getOrDefault(Materiel.EtatMateriel.HORS_SERVICE, List.of()).size();

        int quantiteTotale = materiels.stream()
                .mapToInt(Materiel::getQuantite)
                .sum();

        // Statistiques par personnel
        Map<String, List<Materiel>> parPersonnel = materiels.stream()
                .filter(m -> m.getPersonnelEntretien() != null)
                .collect(Collectors.groupingBy(m ->
                        m.getPersonnelEntretien().getNom() + " " +
                                (m.getPersonnelEntretien().getPrenom() != null ? m.getPersonnelEntretien().getPrenom() : "")));

        List<MaterielDTOs.StatistiqueParPersonnel> statsParPersonnel = parPersonnel.entrySet().stream()
                .map(entry -> {
                    String nomPersonnel = entry.getKey();
                    List<Materiel> materielPersonnel = entry.getValue();
                    String lieuService = materielPersonnel.get(0).getPersonnelEntretien().getLieuService();

                    return MaterielDTOs.StatistiqueParPersonnel.builder()
                            .nomPersonnel(nomPersonnel)
                            .lieuService(lieuService)
                            .nombreMateriels(materielPersonnel.size())
                            .quantiteTotale(materielPersonnel.stream().mapToInt(Materiel::getQuantite).sum())
                            .build();
                })
                .collect(Collectors.toList());

        // Statistiques détaillées par état
        List<MaterielDTOs.StatistiqueParEtat> statsParEtat = parEtat.entrySet().stream()
                .map(entry -> {
                    Materiel.EtatMateriel etat = entry.getKey();
                    List<Materiel> materielEtat = entry.getValue();
                    long nombre = materielEtat.size();
                    int quantite = materielEtat.stream().mapToInt(Materiel::getQuantite).sum();
                    double pourcentage = totalMateriels > 0 ? (double) nombre / totalMateriels * 100 : 0.0;

                    return MaterielDTOs.StatistiqueParEtat.builder()
                            .etat(etat)
                            .nombreMateriels(nombre)
                            .quantiteTotale(quantite)
                            .pourcentage(Math.round(pourcentage * 100.0) / 100.0)
                            .build();
                })
                .collect(Collectors.toList());

        return MaterielDTOs.MaterielStatsResponse.builder()
                .totalMateriels(totalMateriels)
                .materielNeuf(materielNeuf)
                .materielBon(materielBon)
                .materielMoyen(materielMoyen)
                .materielMauvais(materielMauvais)
                .materielHorsService(materielHorsService)
                .quantiteTotale(quantiteTotale)
                .statistiquesParPersonnel(statsParPersonnel)
                .statistiquesParEtat(statsParEtat)
                .build();
    }
}