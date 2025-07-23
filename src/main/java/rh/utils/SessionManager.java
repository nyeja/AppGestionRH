package rh.utils; // Assurez-vous que le package correspond à l'endroit où vous le placez

import rh.model.UtilisateurModel; // Importez votre modèle Utilisateur

/**
 * Gère la session de l'utilisateur connecté.
 * Utilise une approche Singleton/méthodes statiques pour un accès facile depuis n'importe où.
 */
public class SessionManager {
    // Cette variable statique retiendra l'utilisateur actuellement connecté
    private static UtilisateurModel currentUser;

    /**
     * Retourne l'utilisateur actuellement connecté.
     * @return L'objet UtilisateurModel de l'utilisateur connecté, ou null si personne n'est connecté.
     */
    public static UtilisateurModel getCurrentUser() {
        return currentUser;
    }

    /**
     * Définit l'utilisateur comme étant connecté.
     * @param user L'objet UtilisateurModel de l'utilisateur qui vient de se connecter.
     */
    public static void setCurrentUser(UtilisateurModel user) {
        currentUser = user;
    }

    /**
     * Vérifie si un utilisateur est actuellement connecté.
     * @return true si un utilisateur est connecté, false sinon.
     */
    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    /**
     * Déconnecte l'utilisateur en effaçant les informations de session.
     */
    public static void logout() {
        currentUser = null;
    }
}