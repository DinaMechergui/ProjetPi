# 💍 Wedding Planner App – JavaFX

## 🎓 Présentation

Ce projet a été développé dans le cadre du cours PIDEV 3A à [Esprit School of Engineering](https://esprit.tn). Il vise à faciliter l'organisation des mariages grâce à une application de bureau construite avec JavaFX. L'application permet aux utilisateurs de planifier leurs événements, réserver des services et produits, gérer les invités, les hébergements et les déplacements... avec une interface moderne et intuitive.

## ✨ Fonctionnalités

- *Gestion des utilisateurs* : Authentification, profils, rôles
- *Création d'événements* : Choix de saison, type de mariage, date
- *Réservation de services* : Photographe, traiteur, DJ, maquillage
- *Module boutique* : Robes, costumes, accessoires beauté
- *Hébergement et transport* : Réservation d'hôtels et voitures
- *Liste d'invités* : Ajout, envoi d'invitations avec QR code
- *Liste de cadeaux* : Générée et accessible via QR personnalisé
- *Calendrier & météo* : Suivi des réservations + météo intégrée
- *Facturation* : Génération de factures PDF après réservation
- *Interface graphique JavaFX* : Responsive et stylisée

## 🛠️ Technologies utilisées

### Frontend
- JavaFX (FXML)
- SceneBuilder
- CSS personnalisé

### Backend / Données
- Java 11+
- JDBC / Hibernate
- SQLite ou MySQL

### Outils supplémentaires
- Maven
- Git / GitHub
- SceneBuilder
- IntelliJ IDEA 

## 🗂️ Structure du projet

wedding-planner-java/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── controller/     # Contrôleurs JavaFX
│   │   │   ├── entities/       # Classes entités
│   │   │   ├── services/       # Services métier
│   │   │   └── utils/          # Utilitaires
│   │   └── resources/
│   │       ├── fxml/           # Fichiers FXML
│   │       ├── css/            # Styles CSS
│   │       └── images/         # Images et ressources
│   └── test/                   # Tests unitaires
├── uploads/                    # Dossier pour les uploads
│   ├── events/                 # Images des événements
│   └── services/               # Images des services
├── pom.xml                     # Configuration Maven
└── README.md                   # Documentation

## 🚀 Installation et démarrage

### Prérequis
- Java JDK 11 ou supérieur
- Maven
- SceneBuilder (pour éditer les fichiers FXML)
- IDE (IntelliJ IDEA ou Eclipse)

### Installation

1. Cloner le dépôt
git clone (https://github.com/DinaMechergui/ProjetPi.git)
cd wedding-planner-java

2. Compiler le projet
mvn clean install

3. Lancer l'application
mvn javafx:run

### Configuration

1. Base de données
   - Créer une base de données MySQL
   - Configurer les paramètres de connexion dans src/main/resources/database.properties

2. Dossiers d'upload
   - S'assurer que les dossiers uploads/events et uploads/services existent
   - Vérifier les permissions d'écriture
   - Les chemins sont relatifs au répertoire de l'application
   - Les dossiers sont créés automatiquement au démarrage

## 📝 Utilisation

### Gestion des images
- Les images sont stockées dans le dossier uploads/ de l'application
- Formats supportés : JPG, JPEG, PNG, GIF
- Taille maximale : 5MB par image
- Les images sont automatiquement redimensionnées pour l'affichage

### Gestion des événements
1. *Création d'un événement*
   - Cliquer sur "Ajouter un événement"
   - Remplir les informations requises
   - Sélectionner une image (le FileChooser affichera les dossiers Pictures, Downloads, Desktop)
   - Valider la création

2. *Modification d'un événement*
   - Sélectionner l'événement à modifier
   - Modifier les informations
   - Changer l'image si nécessaire
   - Sauvegarder les modifications

### Gestion des services
- Parcourir les services disponibles
- Ajouter/modifier des services
- Gérer les images associées

## 🔧 Dépannage

### Problèmes courants avec les images
1. *Images non visibles*
   - Vérifier que le dossier uploads/ existe
   - Vérifier les permissions d'écriture
   - Redémarrer l'application

2. *Erreur lors de la sélection d'image*
   - Vérifier le format de l'image (JPG, PNG, GIF)
   - Vérifier la taille (max 5MB)
   - Essayer un autre dossier source

3. *FileChooser ne montre pas d'images*
   - Vérifier les filtres d'extension
   - Essayer un autre dossier source
   - Redémarrer l'application

## 🤝 Contribution

Les contributions sont les bienvenues ! Pour contribuer :

1. Fork le projet
2. Créer une branche (git checkout -b feature/AmazingFeature)
3. Commit les changements (git commit -m 'Add some AmazingFeature')
4. Push sur la branche (git push origin feature/AmazingFeature)
5. Ouvrir une Pull Request

## 📄 Licence

Ce projet est sous licence [MIT](LICENSE).

## 👥 Auteurs

-Mechergui Dina
-Rouissi Nesrine
-Gharbi Sourour
-Ben Salah Skander
-Zidi Ouayess

## 🙏 Remerciements

- *Mme Hichri Chaima * - Superviseur - Esprit School of Engineering
- *Mmr Hechmi Moataz* - Superviseur - Esprit School of Engineering


## 📞 Support

Pour toute question ou problème :
- Ouvrir une issue sur GitHub
- Contacter l'équipe de développement
- Consulter la section Dépannage ci-dessus

## 📌 Topics GitHub
- java
- javafx
- wedding-planner
- desktop-app
- event-management
- qr-code
- pdf-generation
- esprit-school

---

⭐️ N'oubliez pas de mettre une étoile au projet si vous l'appréciez !
