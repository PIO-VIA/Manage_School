package com.school.mapper;

import com.school.dto.AbsenceDTOs;
import com.school.entity.Absence;
import com.school.entity.Eleve;
import com.school.entity.SalleDeClasse;
import com.school.repository.AbsenceRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class AbsenceMapper {

    @Autowired
    private AbsenceRepository absenceRepository;

    @Mapping(target = "eleve", source = "eleve", qualifiedByName = "toEleveInfo")
    @Mapping(target = "dossierDiscipline", source = "dossierDiscipline", qualifiedByName = "toDossierDisciplineInfo")
    public abstract AbsenceDTOs.AbsenceResponse toResponse(Absence absence);

    public abstract List<AbsenceDTOs.AbsenceResponse> toResponseList(List<Absence> absences);

    @Named("toEleveInfo")
    protected AbsenceDTOs.EleveInfo toEleveInfo(Eleve eleve) {
        if (eleve == null) return null;
        return AbsenceDTOs.EleveInfo.builder()
                .matricule(eleve.getMatricule())
                .nom(eleve.getNom())
                .prenom(eleve.getPrenom())
                .classe(eleve.getSalleDeClasse() != null ? eleve.getSalleDeClasse().getNom() : "")
                .section(eleve.getSection() != null ? eleve.getSection().getNom() : "")
                .build();
    }

    @Named("toDossierDisciplineInfo")
    protected AbsenceDTOs.DossierDisciplineInfo toDossierDisciplineInfo(com.school.entity.DossierDiscipline dossierDiscipline) {
        if (dossierDiscipline == null) return null;
        return AbsenceDTOs.DossierDisciplineInfo.builder()
                .idDiscipline(dossierDiscipline.getIdDiscipline())
                .convocation(dossierDiscipline.getConvocation())
                .etat(dossierDiscipline.getEtat().name())
                .build();
    }

    public AbsenceDTOs.AbsenceStatsResponse toStatsResponse(Eleve eleve, List<Absence> absences) {
        if (eleve == null) return null;

        int totalAbsences = absences.size();
        int absencesJustifiees = (int) absences.stream().filter(Absence::getJustifiee).count();
        int absencesNonJustifiees = totalAbsences - absencesJustifiees;

        int retards = (int) absences.stream()
                .filter(a -> Absence.TypeAbsence.RETARD.equals(a.getTypeAbsence()))
                .count();

        int journeesCompletes = (int) absences.stream()
                .filter(a -> Absence.TypeAbsence.JOURNEE_COMPLETE.equals(a.getTypeAbsence()))
                .count();

        // Calcul du taux d'absentéisme (approximatif basé sur 180 jours d'école par an)
        double tauxAbsenteisme = totalAbsences > 0 ? (double) totalAbsences / 180 * 100 : 0.0;

        LocalDate premiereAbsence = absences.stream()
                .map(Absence::getJour)
                .min(LocalDate::compareTo)
                .orElse(null);

        LocalDate derniereAbsence = absences.stream()
                .map(Absence::getJour)
                .max(LocalDate::compareTo)
                .orElse(null);

        return AbsenceDTOs.AbsenceStatsResponse.builder()
                .matricule(eleve.getMatricule())
                .nomEleve(eleve.getNom())
                .prenomEleve(eleve.getPrenom())
                .classe(eleve.getSalleDeClasse() != null ? eleve.getSalleDeClasse().getNom() : "")
                .section(eleve.getSection() != null ? eleve.getSection().getNom() : "")
                .totalAbsences(totalAbsences)
                .absencesJustifiees(absencesJustifiees)
                .absencesNonJustifiees(absencesNonJustifiees)
                .retards(retards)
                .journeesCompletes(journeesCompletes)
                .tauxAbsenteisme(Math.round(tauxAbsenteisme * 100.0) / 100.0)
                .premiereAbsence(premiereAbsence)
                .derniereAbsence(derniereAbsence)
                .build();
    }

    public AbsenceDTOs.AbsenceClasseStatsResponse toClasseStatsResponse(SalleDeClasse classe, List<Eleve> eleves) {
        if (classe == null || eleves == null) return null;

        List<AbsenceDTOs.AbsenceStatsResponse> detailsParEleve = eleves.stream()
                .map(eleve -> {
                    List<Absence> absencesEleve = absenceRepository.findByEleveMatriculeAndIsActiveTrue(eleve.getMatricule());
                    return toStatsResponse(eleve, absencesEleve);
                })
                .sorted(Comparator.comparing(AbsenceDTOs.AbsenceStatsResponse::getTotalAbsences).reversed())
                .collect(Collectors.toList());

        int totalAbsences = detailsParEleve.stream()
                .mapToInt(AbsenceDTOs.AbsenceStatsResponse::getTotalAbsences)
                .sum();

        int elevesAvecAbsences = (int) detailsParEleve.stream()
                .filter(stats -> stats.getTotalAbsences() > 0)
                .count();

        double moyenneAbsencesParEleve = eleves.isEmpty() ? 0.0 : (double) totalAbsences / eleves.size();

        double tauxAbsenteismeClasse = detailsParEleve.stream()
                .mapToDouble(AbsenceDTOs.AbsenceStatsResponse::getTauxAbsenteisme)
                .average()
                .orElse(0.0);

        return AbsenceDTOs.AbsenceClasseStatsResponse.builder()
                .idClasse(classe.getIdClasse())
                .nomClasse(classe.getNom())
                .section(classe.getSection() != null ? classe.getSection().getNom() : "")
                .effectifClasse(eleves.size())
                .totalAbsences(totalAbsences)
                .elevesAvecAbsences(elevesAvecAbsences)
                .moyenneAbsencesParEleve(Math.round(moyenneAbsencesParEleve * 100.0) / 100.0)
                .tauxAbsenteismeClasse(Math.round(tauxAbsenteismeClasse * 100.0) / 100.0)
                .detailsParEleve(detailsParEleve)
                .build();
    }
}