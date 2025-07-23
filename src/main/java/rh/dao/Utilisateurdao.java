package rh.dao ;


import rh.utils.ConnexionDB;
import rh.model.UtilisateurModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;


public class Utilisateurdao { // Renamed to UtilisateurDAO for convention

    // Méthode existante, mise à jour pour inclure le rôle
    public UtilisateurModel findByUsername(String username) throws SQLException {
        UtilisateurModel utilisateur = null;
        // Mise à jour de la requête SQL pour inclure la colonne 'role'
        String sql = "SELECT idUtilisateur, username, password, employe_id, role FROM Utilisateur WHERE username = ?";

        try (Connection connection = ConnexionDB.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    utilisateur = new UtilisateurModel(
                            rs.getInt("idUtilisateur"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("employe_id"),
                            rs.getString("role") // <-- NOUVEAU : Récupération du rôle
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding user by username: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
        return utilisateur;
    }

    // NOUVELLE MÉTHODE : Pour la connexion et l'authentification
    public UtilisateurModel login(String username, String password) throws SQLException {
        UtilisateurModel utilisateur = null;

        String sql = "SELECT idUtilisateur, username, password, employe_id, role FROM Utilisateur WHERE username = ?";

        try (Connection connection = ConnexionDB.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String storedPasswordHash = rs.getString("password"); // Récupère le mot de passe (haché) stocké
                    String retrievedRole = rs.getString("role");
                    String retrievedEmployeId = rs.getString("employe_id");

                    // --- IMPORTANT : Validation du mot de passe ---
                    // Actuellement, c'est une simple comparaison de chaînes.
                    // EN PRODUCTION, VOUS DEVEZ UTILISER UNE BIBLIOTHÈQUE DE HACHAGE (ex: BCrypt)
                    // pour comparer le mot de passe fourni par l'utilisateur avec le hachage stocké.
                    // Exemple (pseudo-code avec BCrypt):
                    // if (BCrypt.checkpw(password, storedPasswordHash)) {
                    if (password.equals(storedPasswordHash)) { // <-- À REMPLACER PAR UNE VRAIE VÉRIFICATION DE HASH EN PRODUCTION
                        utilisateur = new UtilisateurModel(
                                rs.getInt("idUtilisateur"),
                                username, // Utilisez le username fourni car il est validé
                                storedPasswordHash, // Le mot de passe haché
                                retrievedEmployeId,
                                retrievedRole
                        );
                    } else {
                        // Mot de passe incorrect
                        System.out.println("Login failed: Incorrect password for user " + username);
                    }
                } else {
                    // Nom d'utilisateur non trouvé
                    System.out.println("Login failed: User " + username + " not found.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error during login for user " + username + ": " + e.getMessage());
            e.printStackTrace();
            throw e; // Relance l'exception pour que le contrôleur puisse la gérer
        }
        return utilisateur; // Retourne l'objet UtilisateurModel si succès, null sinon
    }
}