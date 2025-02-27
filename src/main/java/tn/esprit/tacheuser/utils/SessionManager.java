package tn.esprit.tacheuser.utils;

import tn.esprit.tacheuser.models.User;

public class SessionManager {
    private static User currentUser;

    public static void setUser(User user) {
        currentUser = user;
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
}
