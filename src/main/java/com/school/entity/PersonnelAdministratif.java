package com.school.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "personnel_administratif")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonnelAdministratif implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_admin")
    private Long idAdmin;

    @Column(name = "nom", nullable = false)
    private String nom;

    @Column(name = "prenom")
    private String prenom;

    @Enumerated(EnumType.STRING)
    @Column(name = "sexe", nullable = false)
    private Sexe sexe;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false)
    private StatutPersonnel statut;

    @Column(name = "telephone_1", nullable = false)
    private String telephone1;

    @Column(name = "telephone_2")
    private String telephone2;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "date_prise_service", nullable = false)
    private LocalDate datePriseService;

    @Column(name = "mot_de_passe", nullable = false)
    private String motDePasse;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role = Role.ADMIN;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // Relations
    @OneToMany(mappedBy = "personnelAdministratif", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DossierExamen> dossiersExamen;

    @OneToMany(mappedBy = "personnelAdministratif", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DossierDiscipline> dossiersDiscipline;

    @OneToMany(mappedBy = "personnelAdministratif", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DossierInscription> dossiersInscription;

    // Énumérations
    public enum Sexe {
        MASCULIN, FEMININ
    }

    public enum StatutPersonnel {
        ACTIF, INACTIF, SUSPENDU, CONGE
    }

    public enum Role {
        ADMIN, SUPER_ADMIN
    }

    // Implémentation UserDetails
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return motDePasse;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return isActive;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isActive;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isActive;
    }

    @Override
    public boolean isEnabled() {
        return isActive;
    }
}