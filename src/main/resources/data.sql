-- Script d'initialisation de la base de données pour le système de gestion d'école

-- Insertion des sections par défaut
INSERT INTO sections (nom, description, type_section, is_active) VALUES
('Section Francophone', 'Section d''enseignement en français', 'FRANCOPHONE', true),
('Section Anglophone', 'Section d''enseignement en anglais', 'ANGLOPHONE', true),
('Section Bilingue', 'Section d''enseignement bilingue français-anglais', 'BILINGUE', true)
ON CONFLICT (nom) DO NOTHING;

-- Insertion des matières par défaut
INSERT INTO matieres (nom, coefficient, description, is_active) VALUES
('Français', 4, 'Langue française - lecture, écriture, grammaire', true),
('Mathématiques', 4, 'Calcul, géométrie, logique mathématique', true),
('Sciences', 3, 'Sciences naturelles et expérimentales', true),
('Histoire-Géographie', 2, 'Histoire et géographie du Cameroun et du monde', true),
('Éducation Civique', 2, 'Instruction civique et morale', true),
('Anglais', 3, 'Langue anglaise', true),
('Arts Plastiques', 1, 'Dessin, peinture, arts créatifs', true),
('Éducation Physique', 2, 'Sport et activités physiques', true),
('Musique', 1, 'Chant, rythme, éducation musicale', true),
('Informatique', 2, 'Initiation à l''informatique', true)
ON CONFLICT (nom) DO NOTHING;

-- Insertion d'un administrateur par défaut (mot de passe: admin123)
-- Le mot de passe est hashé avec BCrypt
INSERT INTO personnel_administratif (
    nom, prenom, sexe, statut, telephone_1, email,
    date_prise_service, mot_de_passe, role, is_active
) VALUES (
    'ADMIN', 'Système', 'MASCULIN', 'ACTIF', '+237600000000',
    'admin@school.com', '2024-01-01',
    '$2a$10$rIsA0D5hX8K0.lQQfxzRLO6YBRfVJ8nQqKTT.8uKGWGZOy1Dq3M/G',
    'SUPER_ADMIN', true
) ON CONFLICT (email) DO NOTHING;

-- Insertion de quelques classes par défaut pour la section francophone
DO $$
DECLARE
    section_francophone_id BIGINT;
BEGIN
    -- Récupérer l'ID de la section francophone
    SELECT id_section INTO section_francophone_id
    FROM sections WHERE nom = 'Section Francophone';

    -- Insérer les classes si la section existe
    IF section_francophone_id IS NOT NULL THEN
        INSERT INTO salles_classe (nom, niveau, effectif, capacity_max, id_section, is_active) VALUES
        ('Maternelle A', 'MATERNELLE', 0, 25, section_francophone_id, true),
        ('Maternelle B', 'MATERNELLE', 0, 25, section_francophone_id, true),
        ('CP A', 'CP', 0, 30, section_francophone_id, true),
        ('CP B', 'CP', 0, 30, section_francophone_id, true),
        ('CE1 A', 'CE1', 0, 35, section_francophone_id, true),
        ('CE1 B', 'CE1', 0, 35, section_francophone_id, true),
        ('CE2 A', 'CE2', 0, 35, section_francophone_id, true),
        ('CM1 A', 'CM1', 0, 40, section_francophone_id, true),
        ('CM2 A', 'CM2', 0, 40, section_francophone_id, true)
        ON CONFLICT DO NOTHING;
    END IF;
END $$;

-- Insertion de quelques classes pour la section anglophone
DO $$
DECLARE
    section_anglophone_id BIGINT;
BEGIN
    -- Récupérer l'ID de la section anglophone
    SELECT id_section INTO section_anglophone_id
    FROM sections WHERE nom = 'Section Anglophone';

    -- Insérer les classes si la section existe
    IF section_anglophone_id IS NOT NULL THEN
        INSERT INTO salles_classe (nom, niveau, effectif, capacity_max, id_section, is_active) VALUES
        ('Nursery A', 'MATERNELLE', 0, 25, section_anglophone_id, true),
        ('Class 1 A', 'CP', 0, 30, section_anglophone_id, true),
        ('Class 2 A', 'CE1', 0, 35, section_anglophone_id, true),
        ('Class 3 A', 'CE2', 0, 35, section_anglophone_id, true),
        ('Class 4 A', 'CM1', 0, 40, section_anglophone_id, true),
        ('Class 5 A', 'CM2', 0, 40, section_anglophone_id, true)
        ON CONFLICT DO NOTHING;
    END IF;
END $$;

-- Insertion d'un personnel d'entretien par défaut
INSERT INTO personnel_entretien (
    nom, prenom, sexe, statut, telephone_1, email,
    date_prise_service, lieu_service, is_active
) VALUES (
    'NKOMO', 'Jean', 'MASCULIN', 'ACTIF', '+237677123456',
    'jean.nkomo@school.com', '2024-01-01', 'Bâtiment Principal', true
) ON CONFLICT (email) DO NOTHING;

-- Insertion de quelques matériels par défaut
DO $$
DECLARE
    personnel_entretien_id BIGINT;
BEGIN
    -- Récupérer l'ID du personnel d'entretien
    SELECT id_entretien INTO personnel_entretien_id
    FROM personnel_entretien WHERE email = 'jean.nkomo@school.com';

    -- Insérer les matériels si le personnel existe
    IF personnel_entretien_id IS NOT NULL THEN
        INSERT INTO materiels (nom, quantite, etat, description, id_entretien, is_active) VALUES
        ('Tables d''élèves', 50, 'BON', 'Tables en bois pour les salles de classe', personnel_entretien_id, true),
        ('Chaises d''élèves', 200, 'BON', 'Chaises en plastique pour les élèves', personnel_entretien_id, true),
        ('Tableaux noirs', 15, 'MOYEN', 'Tableaux noirs muraux', personnel_entretien_id, true),
        ('Projecteurs', 5, 'NEUF', 'Projecteurs pour présentations', personnel_entretien_id, true),
        ('Ordinateurs', 10, 'BON', 'Ordinateurs pour salle informatique', personnel_entretien_id, true),
        ('Balais', 20, 'BON', 'Balais pour nettoyage des classes', personnel_entretien_id, true),
        ('Seaux', 15, 'BON', 'Seaux en plastique', personnel_entretien_id, true)
        ON CONFLICT DO NOTHING;
    END IF;
END $$;

-- Message de confirmation
SELECT 'Base de données initialisée avec succès!' as message;