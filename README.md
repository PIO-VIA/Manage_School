# 🏫 Système de Gestion d'École Primaire

API REST complète pour la gestion d'une école primaire développée avec Spring Boot 3.4.6 et Java 21.

## 📋 Table des matières

- [Fonctionnalités](#-fonctionnalités)
- [Technologies utilisées](#-technologies-utilisées)
- [Prérequis](#-prérequis)
- [Installation](#-installation)
- [Configuration](#-configuration)
- [Lancement](#-lancement)
- [Documentation API](#-documentation-api)
- [Architecture](#-architecture)
- [Sécurité](#-sécurité)
- [Base de données](#-base-de-données)
- [Tests](#-tests)
- [Contribution](#-contribution)

## 🚀 Fonctionnalités

### 👥 Gestion du Personnel
- **Personnel Administratif** : ADMIN et SUPER_ADMIN avec authentification JWT
- **Enseignants** : Assignation aux classes et matières
- **Personnel d'Entretien** : Gestion du matériel scolaire

### 🎓 Gestion Pédagogique
- **Sections** : Francophone, Anglophone, Bilingue
- **Classes** : Maternelle, CP, CE1, CE2, CM1, CM2
- **Élèves** : Inscription, suivi, statistiques
- **Matières** : 10 matières avec coefficients

### 📊 Évaluations et Suivi
- **Notes** : Système de notation par séquences (1-6)
- **Bulletins** : Génération automatique avec moyennes et classements
- **Absences** : Suivi avec justifications
- **Discipline** : Gestion des dossiers disciplinaires

### 💰 Gestion Administrative
- **Inscriptions** : Suivi des paiements et états
- **Examens** : Dossiers d'examens officiels
- **Matériel** : Inventaire et état du matériel scolaire

## 🛠 Technologies utilisées

- **Framework** : Spring Boot 3.4.6
- **Langage** : Java 21
- **Base de données** : PostgreSQL
- **Sécurité** : Spring Security + JWT
- **Documentation** : OpenAPI 3 (Swagger)
- **Mapping** : MapStruct
- **Validation** : Bean Validation
- **Logs** : SLF4J + Logback
- **Build** : Maven

## 📋 Prérequis

- Java 21+
- Maven 3.6+
- PostgreSQL 12+

## 🔧 Installation

1. **Cloner le projet**
```bash
git clone https://github.com/PIO-VIA/Manage_School.git
cd Manage_School
```

2. **Créer la base de données**
```sql
CREATE DATABASE school_management;
```

3. **Configurer la base de données**
```properties
# src/main/resources/application.properties
spring.datasource.url=jdbc:postgresql://localhost:5432/school_management
spring.datasource.username=votre_username
spring.datasource.password=votre_password
```

4. **Installer les dépendances**
```bash
mvn clean install
```

## ⚙️ Configuration

### Variables d'environnement (optionnel)
```bash
export DB_URL=jdbc:postgresql://localhost:5432/school_management
export DB_USERNAME=postgres
export DB_PASSWORD=your_password
export JWT_SECRET=your_jwt_secret_key
```

### Configuration JWT
- **Durée de validité** : 24 heures (configurable)
- **Algorithme** : HS512
- **Secret** : Configurable via `app.jwt.secret`

## 🚀 Lancement

### Développement
```bash
mvn spring-boot:run
```

### Production
```bash
mvn clean package
java -jar target/Ecole-0.0.1-SNAPSHOT.jar
```

L'API sera accessible sur : `http://localhost:8080/api`

## 📖 Documentation API

### Swagger UI
Accédez à la documentation interactive : `http://localhost:8080/api/swagger-ui.html`

### Authentification
1. **Se connecter** via `/api/auth/login` :
```json
{
  "email": "admin@school.com",
  "motDePasse": "admin123"
}
```

2. **Utiliser le token JWT** :
    - Cliquer sur "Authorize" 🔒 dans Swagger
    - Entrer : `Bearer votre_token_jwt`

### Endpoints principaux

| Ressource | Endpoint | Description |
|-----------|----------|-------------|
| Auth | `/api/auth/*` | Authentification et gestion des comptes |
| Sections | `/api/sections/*` | Gestion des sections d'enseignement |
| Classes | `/api/classes/*` | Gestion des salles de classe |
| Élèves | `/api/eleves/*` | Gestion des élèves |
| Enseignants | `/api/enseignants/*` | Gestion du corps enseignant |
| Matières | `/api/matieres/*` | Gestion des matières scolaires |
| Notes | `/api/notes/*` | Système d'évaluation et bulletins |
| Absences | `/api/absences/*` | Suivi des absences |
| Discipline | `/api/discipline/*` | Dossiers disciplinaires |
| Inscriptions | `/api/inscriptions/*` | Gestion des inscriptions et paiements |
| Matériel | `/api/materiels/*` | Inventaire du matériel scolaire |

## 🏗 Architecture

```
src/main/java/com/school/
├── config/          # Configuration Spring
├── controller/      # Contrôleurs REST
├── dto/            # Data Transfer Objects
├── entity/         # Entités JPA
├── exception/      # Gestion des exceptions
├── mapper/         # Mappers MapStruct
├── repository/     # Repositories JPA
├── security/       # Configuration sécurité
└── service/        # Logique métier
```

### Principes appliqués
- **Séparation des responsabilités** : Couches distinctes
- **Clean Code** : Nommage explicite et code lisible
- **SOLID** : Respect des principes SOLID
- **RESTful** : API conforme aux standards REST

## 🔐 Sécurité

### Rôles et Permissions
- **SUPER_ADMIN** : Accès complet (gestion personnel, suppressions)
- **ADMIN** : Gestion courante (élèves, notes, absences)

### Sécurisation
- **JWT** : Tokens sécurisés avec expiration
- **BCrypt** : Hashage des mots de passe
- **CORS** : Configuration pour le frontend
- **HTTPS** : Recommandé en production

## 🗄️ Base de données

### Modèle de données
- **13 entités principales** reliées par des relations JPA
- **Suppression logique** : Préservation de l'historique
- **Contraintes** : Validation au niveau base de données
- **Index** : Optimisation des requêtes fréquentes

### Scripts d'initialisation
- **Données de base** : Sections, matières, admin par défaut
- **Classes exemples** : Structure de classes préchargée

## 🧪 Tests

```bash
# Lancer les tests
mvn test

# Tests avec couverture
mvn clean test jacoco:report
```

## 📊 Monitoring

### Actuator endpoints
- **Health** : `/api/actuator/health`
- **Info** : `/api/actuator/info`
- **Metrics** : `/api/actuator/metrics`

### Logs
- **Niveau** : DEBUG pour development
- **Format** : JSON structuré recommandé pour production

## 🚀 Déploiement


### Production
- Utiliser un profil `prod` avec configurations appropriées
- Base de données dédiée avec connexions poolées
- HTTPS obligatoire
- Monitoring et alertes

## 🤝 Contribution

1. Fork le projet
2. Créer une branche feature (`git checkout -b feature/nouvelle-fonctionnalite`)
3. Commiter les changements (`git commit -m 'Ajout nouvelle fonctionnalité'`)
4. Push vers la branche (`git push origin feature/nouvelle-fonctionnalite`)
5. Ouvrir une Pull Request

## 📝 License

Ce projet est sous licence MIT. Voir le fichier `LICENSE` pour plus de détails.

## Auteur

- **Développeur** : PIO VIANNEY
- **Email** : [piodjiele@gmail.com]


---

**⭐ N'hésitez pas à mettre une étoile si ce projet vous aide !**