package rh.dao;

import rh.utils.ConnexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Data Access Object (DAO) pour la gestion des utilisateurs.
 * Gère les opérations de base de données pour la table Utilisateur.
 * Les méthodes ont été corrigées pour s'assurer que l'employe_id est correctement géré.
 *
 * @author Manoa
 * @version 1.0
 * @since 2025-08-14
 */
public class Utilisateurdao {

    public void supprimerUtilisateur(String employeId) throws SQLException {
        String sql = "DELETE FROM Utilisateur WHERE employe_id = ?";
        try (Connection connection = ConnexionDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, employeId); // L'employe_id est une chaîne de caractères
            pstmt.executeUpdate();
        }
    }
}
