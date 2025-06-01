package com.school.mapper;

import com.school.dto.SectionDTOs;
import com.school.entity.Section;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SectionMapper {

    @Mapping(target = "nombreClasses", expression = "java(section.getSallesDeClasse() != null ? section.getSallesDeClasse().size() : 0)")
    @Mapping(target = "nombreEnseignants", expression = "java(section.getEnseignants() != null ? section.getEnseignants().size() : 0)")
    @Mapping(target = "nombreEleves", expression = "java(section.getEleves() != null ? section.getEleves().size() : 0)")
    SectionDTOs.SectionResponse toResponse(Section section);

    @Mapping(target = "sallesDeClasse", source = "sallesDeClasse", qualifiedByName = "toSalleClasseBasicResponse")
    @Mapping(target = "enseignants", source = "enseignants", qualifiedByName = "toEnseignantBasicResponse")
    @Mapping(target = "eleves", source = "eleves", qualifiedByName = "toEleveBasicResponse")
    SectionDTOs.SectionDetailResponse toDetailResponse(Section section);

    List<SectionDTOs.SectionResponse> toResponseList(List<Section> sections);

    @Named("toSalleClasseBasicResponse")
    default List<SectionDTOs.SalleClasseBasicResponse> toSalleClasseBasicResponse(List<com.school.entity.SalleDeClasse> salles) {
        if (salles == null) return null;
        return salles.stream()
                .filter(salle -> salle.getIsActive())
                .map(salle -> SectionDTOs.SalleClasseBasicResponse.builder()
                        .idClasse(salle.getIdClasse())
                        .nom(salle.getNom())
                        .niveau(salle.getNiveau().name())
                        .effectif(salle.getEffectif())
                        .build())
                .toList();
    }

    @Named("toEnseignantBasicResponse")
    default List<SectionDTOs.EnseignantBasicResponse> toEnseignantBasicResponse(List<com.school.entity.Enseignant> enseignants) {
        if (enseignants == null) return null;
        return enseignants.stream()
                .filter(enseignant -> enseignant.getIsActive())
                .map(enseignant -> SectionDTOs.EnseignantBasicResponse.builder()
                        .idMaitre(enseignant.getIdMaitre())
                        .nom(enseignant.getNom())
                        .prenom(enseignant.getPrenom())
                        .specialite(enseignant.getSpecialite())
                        .build())
                .toList();
    }

    @Named("toEleveBasicResponse")
    default List<SectionDTOs.EleveBasicResponse> toEleveBasicResponse(List<com.school.entity.Eleve> eleves) {
        if (eleves == null) return null;
        return eleves.stream()
                .filter(eleve -> eleve.getIsActive())
                .map(eleve -> SectionDTOs.EleveBasicResponse.builder()
                        .matricule(eleve.getMatricule())
                        .nom(eleve.getNom())
                        .prenom(eleve.getPrenom())
                        .statut(eleve.getStatut().name())
                        .build())
                .toList();
    }
}