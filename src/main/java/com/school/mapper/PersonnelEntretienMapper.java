package com.school.mapper;

import com.school.dto.PersonnelEntretienDTOs;
import com.school.entity.PersonnelEntretien;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Mapper(componentModel = "spring")
public interface PersonnelEntretienMapper {

    @Mapping(target = "nombreMateriels", expression = "java(countMateriels(personnel))")
    PersonnelEntretienDTOs.PersonnelEntretienResponse toResponse(PersonnelEntretien personnel);

    @Mapping(target = "materiels", source = "materiels", qualifiedByName = "toMaterielInfoList")
    @Mapping(target = "anneesService", expression = "java(calculateAnneesService(personnel))")
    PersonnelEntretienDTOs.PersonnelEntretienDetailResponse toDetailResponse(PersonnelEntretien personnel);

    List<PersonnelEntretienDTOs.PersonnelEntretienResponse> toResponseList(List<PersonnelEntretien> personnel);

    @Named("toMaterielInfoList")
    default List<PersonnelEntretienDTOs.MaterielInfo> toMaterielInfoList(List<com.school.entity.Materiel> materiels) {
        if (materiels == null) return null;
        return materiels.stream()
                .filter(materiel -> materiel.getIsActive())
                .map(materiel -> PersonnelEntretienDTOs.MaterielInfo.builder()
                        .idMateriel(materiel.getIdMateriel())
                        .nom(materiel.getNom())
                        .quantite(materiel.getQuantite())
                        .etat(materiel.getEtat().name())
                        .description(materiel.getDescription())
                        .build())
                .toList();
    }

    // Méthodes utilitaires
    default Integer countMateriels(PersonnelEntretien personnel) {
        if (personnel.getMateriels() == null) return 0;
        return (int) personnel.getMateriels().stream()
                .filter(materiel -> materiel.getIsActive())
                .count();
    }

    default Integer calculateAnneesService(PersonnelEntretien personnel) {
        if (personnel.getDatePriseService() == null) return 0;
        LocalDate today = LocalDate.now();
        return Period.between(personnel.getDatePriseService(), today).getYears();
    }
}