package tn.esprit.tacheuser.utils;

import tn.esprit.tacheuser.service.ReclamationService;
import tn.esprit.tacheuser.service.UserService;
import tn.esprit.tacheuser.service.ReponseService;
import tn.esprit.tacheuser.models.Reclamation;
import tn.esprit.tacheuser.models.User;
import tn.esprit.tacheuser.models.Reponse;
import java.util.List;
import java.util.Scanner;

public class Menu {
    private final ReponseService reponseService = new ReponseService();

    private final UserService userService = new UserService();
    private final ReclamationService reclamationService = new ReclamationService();
    private final Scanner scanner = new Scanner(System.in);

    public void afficherMenuPrincipal() {
        while (true) {
            System.out.println("\n===== MENU PRINCIPAL =====");
            System.out.println("1️⃣  Gestion des Utilisateurs");
            System.out.println("2️⃣  Gestion des Réclamations");
            System.out.println("3️⃣  Gestion des reponses");
            System.out.println("0️⃣  Quitter");
            System.out.print("👉 Faites votre choix : ");

            int choix = scanner.nextInt();
            scanner.nextLine();

            switch (choix) {
                case 1 -> afficherMenuUtilisateur();
                case 2 -> afficherMenuReclamation();
                case 3 -> menuReponse();

                case 0 -> {
                    System.out.println("👋 Au revoir !");
                    return;
                }
                default -> System.out.println("⚠️ Choix invalide, veuillez réessayer !");
            }
        }
    }

    private void afficherMenuUtilisateur() {
        while (true) {
            System.out.println("\n===== GESTION DES UTILISATEURS =====");
            System.out.println("1️⃣  Ajouter un utilisateur");
            System.out.println("2️⃣  Afficher tous les utilisateurs");
            System.out.println("3️⃣  Modifier un utilisateur");
            System.out.println("4️⃣  Supprimer un utilisateur");
            System.out.println("0️⃣  Retour au menu principal");
            System.out.print("👉 Faites votre choix : ");

            int choix = scanner.nextInt();
            scanner.nextLine();

            switch (choix) {
                case 1 -> ajouterUtilisateur();
                case 2 -> afficherUtilisateurs();
                case 3 -> modifierUtilisateur();
                case 4 -> supprimerUtilisateur();
                case 0 -> { return; }
                default -> System.out.println("⚠️ Choix invalide !");
            }
        }
    }

    private void afficherMenuReclamation() {
        while (true) {
            System.out.println("\n===== GESTION DES RÉCLAMATIONS =====");
            System.out.println("1️⃣  Ajouter une réclamation");
            System.out.println("2️⃣  Afficher toutes les réclamations");
            System.out.println("3️⃣  Modifier une réclamation");
            System.out.println("4️⃣  Supprimer une réclamation");
            System.out.println("0️⃣  Retour au menu principal");
            System.out.print("👉 Faites votre choix : ");

            int choix = scanner.nextInt();
            scanner.nextLine();

            switch (choix) {
                case 1 -> ajouterReclamation();
                case 2 -> afficherReclamations();
                case 3 -> modifierReclamation();
                case 4 -> supprimerReclamation();
                case 0 -> { return; }
                default -> System.out.println("⚠️ Choix invalide !");
            }
        }
    }

    private void ajouterUtilisateur() {
        System.out.print("👤 Nom : ");
        String nom = scanner.nextLine();
        System.out.print("👤 Prénom : ");
        String prenom = scanner.nextLine();
        System.out.print("📧 Email : ");
        String mail = scanner.nextLine();
        System.out.print("📞 Téléphone : ");
        String tel = scanner.nextLine();
        System.out.print("🚻 Genre : ");
        String gender = scanner.nextLine();
        System.out.print("🔑 Mot de passe : ");
        String password = scanner.nextLine();
        System.out.print("🔑 Confirmez le mot de passe : ");
        String confirmPassword = scanner.nextLine();
        System.out.print("📆 Age : ");
        String age = scanner.nextLine();

        // Vérification des mots de passe
        if (!password.equals(confirmPassword)) {
            System.out.println("❌ Les mots de passe ne correspondent pas ! L'utilisateur n'a pas été ajouté.");
            return; // Arrêter la méthode
        }

        // Création et ajout de l'utilisateur
        User user = new User(0, nom, prenom, mail, tel, gender, password, age, "USER");
        userService.addUser(user);

    }


    private void afficherUtilisateurs() {
        List<User> users = userService.getAllUsers();
        if (users.isEmpty()) {
            System.out.println("📭 Aucun utilisateur trouvé !");
            return;
        }

        System.out.println("\n📋 Liste des utilisateurs :");
        System.out.printf("%-5s | %-15s | %-15s | %-25s | %-10s | %-6s | %-5s%n",
                "ID", "Nom", "Prénom", "Email", "Téléphone", "Genre", "Âge");
        System.out.println("----------------------------------------------------------------------------");

        for (User u : users) {
            System.out.printf("%-5d | %-15s | %-15s | %-25s | %-10s | %-6s | %-5s%n",
                    u.getId(), u.getNom(), u.getPrenom(), u.getMail()
                    ,
                    u.getTel(), u.getGender(), u.getAge());
        }
    }


    private void modifierUtilisateur() {
        System.out.print("🆔 ID de l'utilisateur à modifier : ");
        int id = scanner.nextInt();
        scanner.nextLine();

        System.out.print("👤 Nouveau nom : ");
        String nom = scanner.nextLine();
        System.out.print("👤 Nouveau prénom : ");
        String prenom = scanner.nextLine();
        System.out.print("📧 Nouvel email : ");
        String mail = scanner.nextLine();
        System.out.print("📞 Nouveau téléphone : ");
        String tel = scanner.nextLine();
        System.out.print("🚻 Nouveau genre : ");
        String gender = scanner.nextLine();
        System.out.print("📆 Nouvel âge : ");
        String age = scanner.nextLine();

        User user = new User(id, nom, prenom, mail, tel, gender, age, "USER");
        userService.updateUser(user);
    }

    private void supprimerUtilisateur() {
        System.out.print("🆔 ID de l'utilisateur à supprimer : ");
        int id = scanner.nextInt();
        scanner.nextLine();
        userService.deleteUser(id);
    }

    private void ajouterReclamation() {
        System.out.print("🆔 ID de l'utilisateur : ");
        int userId = scanner.nextInt();
        scanner.nextLine();

        System.out.print("📝 Description : ");
        String description = scanner.nextLine();

        System.out.print("📌 Statut (En attente/Traité) : ");
        String statut = scanner.nextLine();

        Reclamation reclamation = new Reclamation(description, statut);


        reclamationService.addReclamation(reclamation);
    }

    private void afficherReclamations() {
        List<Reclamation> reclamations = reclamationService.getAllReclamations();
        if (reclamations.isEmpty()) {
            System.out.println("📭 Aucune réclamation trouvée !");
            return;
        }

        System.out.println("\n📋 Liste des réclamations :");
        System.out.printf("%-5s | %-30s | %-15s%n", "ID", "Description", "Statut");
        System.out.println("--------------------------------------------------------------");

        for (Reclamation r : reclamations) {
            System.out.printf("%-5d | %-30s | %-15s%n", r.getId(), r.getDescription(), r.getStatut());
        }
    }


    private void modifierReclamation() {
        System.out.print("🆔 ID de la réclamation à modifier : ");
        int id = scanner.nextInt();
        scanner.nextLine();

        System.out.print("📝 Nouvelle description : ");
        String description = scanner.nextLine();

        System.out.print("📌 Nouveau statut : ");
        String statut = scanner.nextLine();

        Reclamation reclamation = new Reclamation(description, statut);

        reclamationService.updateReclamation(reclamation);
    }

    private void supprimerReclamation() {
        System.out.print("🆔 ID de la réclamation à supprimer : ");
        int id = scanner.nextInt();
        scanner.nextLine();
        reclamationService.deleteReclamation(id);
    }
    private void menuReponse() {
        int choix;
        do {
            System.out.println("\n===== GESTION DES RÉPONSES =====");
            System.out.println("1️⃣  Ajouter une réponse");
            System.out.println("2️⃣  Afficher toutes les réponses");
            System.out.println("3️⃣  Modifier une réponse");
            System.out.println("4️⃣  Supprimer une réponse");
            System.out.println("5️⃣  Retour au menu principal");
            System.out.print("👉 Faites votre choix : ");

            choix = scanner.nextInt();
            scanner.nextLine(); // Pour éviter le bug de saut de ligne

            switch (choix) {
                case 1 -> ajouterReponse();
                case 2 -> afficherReponses();
                case 3 -> modifierReponse();
                case 4 -> supprimerReponse();
                case 5 -> System.out.println("🔙 Retour au menu principal...");
                default -> System.out.println("⚠ Choix invalide, réessayez.");
            }
        } while (choix != 5);
    }

    private void ajouterReponse() {
        System.out.print("🆔 ID de la réclamation : ");
        int reclamationId = scanner.nextInt();
        scanner.nextLine();

        System.out.print("📝 Message de la réponse : ");
        String message = scanner.nextLine();

        Reponse reponse = new Reponse(reclamationId, message);
        reponseService.addReponse(reponse);
    }

    private void afficherReponses() {
        List<Reponse> reponses = reponseService.getAllReponses();
        if (reponses.isEmpty()) {
            System.out.println("📭 Aucune réponse trouvée !");
        } else {
            System.out.println("\n📋 Liste des réponses :");
            reponses.forEach(System.out::println);
        }
    }

    private void modifierReponse() {
        System.out.print("🆔 ID de la réponse à modifier : ");
        int id = scanner.nextInt();
        scanner.nextLine();

        System.out.print("📝 Nouveau message : ");
        String message = scanner.nextLine();

        reponseService.updateReponse(id, message);
    }

    private void supprimerReponse() {
        System.out.print("🗑 ID de la réponse à supprimer : ");
        int id = scanner.nextInt();
        scanner.nextLine();

        reponseService.deleteReponse(id);
    }
}


