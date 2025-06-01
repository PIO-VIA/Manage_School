package com.school.mapper;

import com.school.dto.EnseignantDTOs;
import com.school.entity.Enseignant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Mapper(componentModel = "spring")
public interface EnseignantMapper {

    @Mapping(target = "salleDeClasse", source = "salleDeClasse", qualifiedByName = "toSalleClasseInfo")
    @Mapping(target = "section", source = "section", qualifiedByName = "toSectionInfo")
    EnseignantDTOs.EnseignantResponse toResponse(Enseignant enseignant);

    @Mapping(target = "salleDeClasse", source = "salleDeClasse", qualifiedByName = "toSalleClasseInfo")
    @Mapping(target = "section", source = "section", qualifiedByName = "toSectionInfo")
    @Mapping(target = "matieresEnseignees", source = "enseignements", qualifiedByName = "toMatiereEnseigneeInfoList")
    @Mapping(target = "nombreElevesClasse", expression = "java(countElevesClasse(enseignant))")
    EnseignantDTOs.EnseignantDetailResponse toDetailResponse(Enseignant enseignant);

    @Mapping(target = "nombreMatieresEnseignees", expression = "java(countMatieresEnseignees(enseignant))")
    @Mapping(target = "nombreElevesClasse", expression = "java(countElevesClasse(enseignant))")
    @Mapping(target = "anneesExperience", expression = "java(calculateAnneesExperience(enseignant))")
    @Mapping(target = "section", expression = "java(enseignant.getSection().getNom())")
    @Mapping(target = "classe", expression = "java(enseignant.getSalleDeClasse() != null ? enseignant.getSalleDeClasse().getNom() : null)")
    EnseignantDTOs.EnseignantStatsResponse toStatsResponse(Enseignant enseignant);

    List<EnseignantDTOs.EnseignantResponse> toResponseList(List<Enseignant> enseignants);

    @Named("toSalleClasseInfo")
    default EnseignantDTOs.SalleClasseInfo toSalleClasseInfo(com.school.entity.SalleDeClasse salleDeClasse) {
        if (salleDeClasse == null) return null;
        return EnseignantDTOs.SalleClasseInfo.builder()
                .idClasse(salleDeClasse.getIdClasse())
                .nom(salleDeClasse.getNom())
                .niveau(salleDeClasse.getNiveau().name())
                .effectif(salleDeClasse.getEffectif())
                .build();
    }

    @Named("toSectionInfo")
    default EnseignantDTOs.SectionInfo toSectionInfo(com.school.entity.Section section) {
        if (section == null) return null;
        return EnseignantDTOs.SectionInfo.builder()
                .idSection(section.getIdSection())
                .nom(section.getNom())
                .typeSection(section.getTypeSection().name())
                .build();
    }

    @Named("toMatiereEnseigneeInfoList")
    default List<EnseignantDTOs.MatiereEnseigneeInfo> toMatiereEnseigneeInfoList(List<com.school.entity.Enseigner> enseignements) {
        if (enseignements == null) return null;
        return enseignements.stream()
                .filter(enseigner -> enseigner.getIsActive())
                .map(enseigner -> EnseignantDTOs.MatiereEnseigneeInfo.builder()
                        .idMatiere(enseigner.getIdMatiere())
                        .nomMatiere(enseigner.getMatiere() != null ? enseigner.getMatiere().getNom() : "")
                        .coefficient(enseigner.getMatiere() != null ? enseigner.getMatiere().getCoefficient() : 0)
                        .dateOccupation(enseigner.getDateOccupation())
                        .isActive(enseigner.getIsActive())
                        .build())
                .toList();
    }

    // Méthodes utilitaires
    default Integer countMatieresEnseignees(Enseignant enseignant) {
        if (enseignant.getEnseignements() == null) return 0;
        return (int) enseignant.getEnseignements().stream()
                .filter(enseigner -> enseigner.getIsActive())
                .count();
    }

    default Integer countElevesClasse(Enseignant enseignant) {
        if (enseignant.getSalleDeClasse() == null) return 0;
        return enseignant.getSalleDeClasse().getEffectif();
    }

    default Integer calculateAnneesExperience(Enseignant enseignant) {
        if (enseignant.getDatePriseService() == null) return 0;
        LocalDate today = LocalDate.now();
        return Period.between(enseignant.getDatePriseService(), today).getYears();
    }
}