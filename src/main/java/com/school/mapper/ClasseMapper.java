package com.school.mapper;

import com.school.dto.ClasseDTOs;
import com.school.entity.PersonnelAdministratif;
import com.school.entity.SalleDeClasse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ClasseMapper {

    @Mapping(target = "section", source = "section", qualifiedByName = "toSectionInfo")
    @Mapping(target = "nombreEnseignants", expression = "java(classe.getEnseignants() != null ? classe.getEnseignants().size() : 0)")
    ClasseDTOs.ClasseResponse toResponse(SalleDeClasse classe);

    @Mapping(target = "section", source = "section", qualifiedByName = "toSectionInfo")
    @Mapping(target = "eleves", source = "eleves", qualifiedByName = "toEleveBasicInfoList")
    @Mapping(target = "enseignants", source = "enseignants", qualifiedByName = "toEnseignantBasicInfoList")
    ClasseDTOs.ClasseDetailResponse toDetailResponse(SalleDeClasse classe);

    @Mapping(target = "niveau", expression = "java(classe.getNiveau().name())")
    @Mapping(target = "tauxOccupation", expression = "java(calculateTauxOccupation(classe))")
    @Mapping(target = "nombreGarcons", expression = "java(countGarcons(classe))")
    @Mapping(target = "nombreFilles", expression = "java(countFilles(classe))")
    @Mapping(target = "moyenneAge", expression = "java(calculateMoyenneAge(classe))")
    ClasseDTOs.ClasseStatsResponse toStatsResponse(SalleDeClasse classe);

    List<ClasseDTOs.ClasseResponse> toResponseList(List<SalleDeClasse> classes);

    @Named("toSectionInfo")
    default ClasseDTOs.SectionInfo toSectionInfo(com.school.entity.Section section) {
        if (section == null) return null;
        return ClasseDTOs.SectionInfo.builder()
                .idSection(section.getIdSection())
                .nom(section.getNom())
                .typeSection(section.getTypeSection().name())
                .build();
    }

    @Named("toEleveBasicInfoList")
    default List<ClasseDTOs.EleveBasicInfo> toEleveBasicInfoList(List<com.school.entity.Eleve> eleves) {
        if (eleves == null) return null;
        return eleves.stream()
                .filter(eleve -> eleve.getIsActive())
                .map(eleve -> ClasseDTOs.EleveBasicInfo.builder()
                        .matricule(eleve.getMatricule())
                        .nom(eleve.getNom())
                        .prenom(eleve.getPrenom())
                        .statut(eleve.getStatut().name())
                        .build())
                .toList();
    }

    @Named("toEnseignantBasicInfoList")
    default List<ClasseDTOs.EnseignantBasicInfo> toEnseignantBasicInfoList(List<com.school.entity.Enseignant> enseignants) {
        if (enseignants == null) return null;
        return enseignants.stream()
                .filter(enseignant -> enseignant.getIsActive())
                .map(enseignant -> ClasseDTOs.EnseignantBasicInfo.builder()
                        .idMaitre(enseignant.getIdMaitre())
                        .nom(enseignant.getNom())
                        .prenom(enseignant.getPrenom())
                        .specialite(enseignant.getSpecialite())
                        .build())
                .toList();
    }

    // Méthodes utilitaires pour les statistiques
    default Double calculateTauxOccupation(SalleDeClasse classe) {
        if (classe.getCapacityMax() == null || classe.getCapacityMax() == 0) return 0.0;
        return (double) classe.getEffectif() / classe.getCapacityMax() * 100;
    }

    default Integer countGarcons(SalleDeClasse classe) {
        if (classe.getEleves() == null) return 0;
        return (int) classe.getEleves().stream()
                .filter(eleve -> eleve.getIsActive())
                .filter(eleve -> PersonnelAdministratif.Sexe.MASCULIN.equals(eleve.getSexe()))
                .count();
    }

    default Integer countFilles(SalleDeClasse classe) {
        if (classe.getEleves() == null) return 0;
        return (int) classe.getEleves().stream()
                .filter(eleve -> eleve.getIsActive())
                .filter(eleve -> PersonnelAdministratif.Sexe.FEMININ.equals(eleve.getSexe()))
                .count();
    }

    default Double calculateMoyenneAge(SalleDeClasse classe) {
        if (classe.getEleves() == null || classe.getEleves().isEmpty()) return 0.0;

        List<com.school.entity.Eleve> elevesActifs = classe.getEleves().stream()
                .filter(eleve -> eleve.getIsActive())
                .toList();

        if (elevesActifs.isEmpty()) return 0.0;

        LocalDate today = LocalDate.now();
        double totalAge = elevesActifs.stream()
                .mapToInt(eleve -> Period.between(eleve.getDateNaissance(), today).getYears())
                .average()
                .orElse(0.0);

        return Math.round(totalAge * 100.0) / 100.0;
    }
}