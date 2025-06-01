package com.school.mapper;

import com.school.dto.EleveDTOs;
import com.school.entity.Eleve;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EleveMapper {

    @Mapping(target = "salleDeClasse", source = "salleDeClasse", qualifiedByName = "toSalleClasseInfo")
    @Mapping(target = "section", source = "section", qualifiedByName = "toSectionInfo")
    EleveDTOs.EleveResponse toResponse(Eleve eleve);

    @Mapping(target = "salleDeClasse", source = "salleDeClasse", qualifiedByName = "toSalleClasseInfo")
    @Mapping(target = "section", source = "section", qualifiedByName = "toSectionInfo")
    @Mapping(target = "notes", source = "compositions", qualifiedByName = "toNoteInfoList")
    @Mapping(target = "absences", source = "absences", qualifiedByName = "toAbsenceInfoList")
    @Mapping(target = "dossiersDiscipline", source = "dossiersDiscipline", qualifiedByName = "toDossierInfoList")
    EleveDTOs.EleveDetailResponse toDetailResponse(Eleve eleve);

    List<EleveDTOs.EleveResponse> toResponseList(List<Eleve> eleves);

    @Named("toSalleClasseInfo")
    default EleveDTOs.SalleClasseInfo toSalleClasseInfo(com.school.entity.SalleDeClasse salleDeClasse) {
        if (salleDeClasse == null) return null;
        return EleveDTOs.SalleClasseInfo.builder()
                .idClasse(salleDeClasse.getIdClasse())
                .nom(salleDeClasse.getNom())
                .niveau(salleDeClasse.getNiveau().name())
                .build();
    }

    @Named("toSectionInfo")
    default EleveDTOs.SectionInfo toSectionInfo(com.school.entity.Section section) {
        if (section == null) return null;
        return EleveDTOs.SectionInfo.builder()
                .idSection(section.getIdSection())
                .nom(section.getNom())
                .typeSection(section.getTypeSection().name())
                .build();
    }

    @Named("toNoteInfoList")
    default List<EleveDTOs.NoteInfo> toNoteInfoList(List<com.school.entity.Composer> compositions) {
        if (compositions == null) return null;
        return compositions.stream()
                .map(composition -> EleveDTOs.NoteInfo.builder()
                        .matiere(composition.getMatiere() != null ? composition.getMatiere().getNom() : "")
                        .note(composition.getNote())
                        .noteFinale(composition.getNoteFinale())
                        .sequence(composition.getSequence())
                        .dateComposition(composition.getDateComposition())
                        .typeEvaluation(composition.getTypeEvaluation().name())
                        .build())
                .toList();
    }

    @Named("toAbsenceInfoList")
    default List<EleveDTOs.AbsenceInfo> toAbsenceInfoList(List<com.school.entity.Absence> absences) {
        if (absences == null) return null;
        return absences.stream()
                .filter(absence -> absence.getIsActive())
                .map(absence -> EleveDTOs.AbsenceInfo.builder()
                        .jour(absence.getJour())
                        .typeAbsence(absence.getTypeAbsence().name())
                        .justifiee(absence.getJustifiee())
                        .motif(absence.getMotif())
                        .build())
                .toList();
    }

    @Named("toDossierInfoList")
    default List<EleveDTOs.DossierInfo> toDossierInfoList(List<com.school.entity.DossierDiscipline> dossiers) {
        if (dossiers == null) return null;
        return dossiers.stream()
                .filter(dossier -> dossier.getIsActive())
                .map(dossier -> EleveDTOs.DossierInfo.builder()
                        .type("DISCIPLINE")
                        .description(dossier.getConvocation())
                        .etat(dossier.getEtat().name())
                        .date(dossier.getDateIncident())
                        .build())
                .toList();
    }
}