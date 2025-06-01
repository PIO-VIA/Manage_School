package com.school.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComposerId implements Serializable {
    private String matricule;
    private Long idMatiere;
    private Integer sequence;
}