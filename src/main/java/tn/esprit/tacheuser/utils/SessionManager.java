package tn.esprit.tacheuser.utils;

import tn.esprit.tacheuser.models.User;

public class SessionManager {
    private static User currentUser;
    private static String resetEmail;  // Stocke l'email pour la réinitialisation du mot de passe
    private static String verificationCode;  // Stocke le code de vérification

    // Gérer l'utilisateur connecté
    public static void setUser(User user) {
        currentUser = user;
    }
    public static void clearResetSession() {
        resetEmail = null; // Efface l'email après la réinitialisation
    }
    public static User getUser() {
        return currentUser;
    }

    public static boolean isUserLoggedIn() {
        return currentUser != null;
    }

    public static void logout() {
        currentUser = null;
        System.out.println("🔴 Utilisateur déconnecté.");
    }

    // Gérer l'email de réinitialisation
    public static void setResetEmail(String email) {
        resetEmail = email;
    }

    public static String getResetEmail() {
        return resetEmail;
    }

    public static void clearResetEmail() {
        resetEmail = null;
    }

    // Gérer le code de vérification
    public static void setVerificationCode(String code) {
        verificationCode = code;
    }

    public static String getVerificationCode() {
        return verificationCode;
    }

    public static void clearVerificationCode() {
        verificationCode = null;
    }
}
