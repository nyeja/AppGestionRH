package rh.dao;

import rh.model.EmployeModel;
import rh.utils.ConnexionDB;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Employedao {

    /**
     * Ajoute un nouvel employé dans la base de données.
     * Après l'insertion, récupère l'identifiant généré par la base de données
     * et l'assigne à l'objet EmployeModel.
     *
     * @param EmployeModel L'objet EmployeModel à ajouter.
     * @throws SQLException Si une erreur de base de données se produit.
     */
    public void ajouterEmploye(EmployeModel EmployeModel) throws SQLException {
        String sql = "INSERT INTO employe (nom, prenoms, telephone, email, adresse, date_embauche) VALUES (?, ?, ?, ?, ?, ?)";
        String[] generatedColumns = {"ID"};
        try (Connection conn = ConnexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, generatedColumns)) {

            ps.setString(1, EmployeModel.getNom());
            ps.setString(2, EmployeModel.getPrenom());
            ps.setInt(3, EmployeModel.getTelephone());
            ps.setString(4, EmployeModel.getEmail());
            ps.setString(5, EmployeModel.getAdresse());
            ps.setDate(6, new java.sql.Date(EmployeModel.getDateEmbauche().getTime()));
//            ps.setString(7, Employe.getDepartement()); // Lignes commentées
//            ps.setString(8, Employe.getPoste());

            int rowsInserted = ps.executeUpdate();
            if (rowsInserted > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        String generatedId = rs.getString(1);
                        EmployeModel.setId(generatedId);
                        System.out.println("Employé ajouté avec ID : " + generatedId);
                    }
                }
            }
        }
    }

    /**
     * Met à jour les informations d'un employé existant dans la base de données.
     *
     * @param EmployeModel L'objet EmployeModel avec les informations mises à jour.
     * @throws SQLException Si une erreur de base de données se produit.
     */
    public void modifierEmploye(EmployeModel EmployeModel) throws SQLException {
        String sql = "UPDATE employe SET nom=?, prenoms=?, telephone=?, email=?, adresse=?, date_embauche=? WHERE id=?";
        try (Connection connection = ConnexionDB.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, EmployeModel.getNom());
            ps.setString(2, EmployeModel.getPrenom());
            ps.setInt(3, EmployeModel.getTelephone());
            ps.setString(4, EmployeModel.getEmail());
            ps.setString(5, EmployeModel.getAdresse());
            ps.setDate(6, new java.sql.Date(EmployeModel.getDateEmbauche().getTime()));
//            ps.setString(7, Employe.getDepartement()); // Lignes commentées
//            ps.setString(8, Employe.getPoste());
            ps.setString(7, EmployeModel.getId());

            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Employé modifié avec succès : " + EmployeModel.getId());
            }
        }
    }

    /**
     * Supprime un employé de la base de données en utilisant son identifiant.
     *
     * @param id L'identifiant de l'employé à supprimer.
     * @throws SQLException Si une erreur de base de données se produit.
     */
    public void supprimerEmploye(String id) throws SQLException {
        String sql = "DELETE FROM employe WHERE id=?";
        try (Connection connection = ConnexionDB.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, id);
            int rowsDeleted = ps.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Employé supprimé avec succès : " + id);
            }
        }
    }

    /**
     * Récupère la liste de tous les employés de la base de données.
     *
     * @return Une liste d'objets EmployeModel.
     */
    public List<EmployeModel> getAllEmployes() {
        List<EmployeModel> liste = new ArrayList<>();
        String sql = "SELECT * FROM employe ";
        try (Connection conn = ConnexionDB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                EmployeModel e = new EmployeModel(
                        rs.getString("id"),
                        rs.getString("nom"),
                        rs.getString("prenoms"),
                        rs.getInt("telephone"),
                        rs.getString("email"),
                        rs.getString("adresse"),
                        rs.getDate("date_embauche")
//                        rs.getString("departement"), // Lignes commentées
//                        rs.getString("poste")
                );
                liste.add(e);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }

    /**
     * Récupère un employé spécifique en utilisant son identifiant.
     *
     * @param id L'identifiant de l'employé à rechercher.
     * @return L'objet EmployeModel correspondant, ou `null` si non trouvé.
     * @throws SQLException Si une erreur de base de données se produit.
     */
    public EmployeModel getEmployeById(String id) throws SQLException {
        String sql = "SELECT * FROM employe WHERE id=?";
        try (Connection conn = ConnexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new EmployeModel(
                            rs.getString("id"),
                            rs.getString("nom"),
                            rs.getString("prenoms"),
                            rs.getInt("telephone"),
                            rs.getString("email"),
                            rs.getString("adresse"),
                            rs.getDate("date_embauche")
//                            rs.getString("departement"), // Lignes commentées
//                            rs.getString("poste")
                    );
                }
            }
        }
        return null;
    }

    /**
     * Recherche des employés en fonction d'un critère donné (nom, prénom ou email).
     * La recherche n'est pas sensible à la casse.
     *
     * @param critere La chaîne de caractères à rechercher.
     * @return Une liste d'objets EmployeModel correspondant au critère.
     */
    public List<EmployeModel> rechercherEmployes(String critere) {
        List<EmployeModel> liste = new ArrayList<>();
        String sql = "SELECT * FROM employe WHERE UPPER(nom) LIKE ? OR UPPER(prenoms) LIKE ? OR UPPER(email) LIKE ? ORDER BY nom, prenoms";
        try (Connection conn = ConnexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String searchPattern = "%" + critere.toUpperCase() + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            ps.setString(3, searchPattern);


            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    EmployeModel e = new EmployeModel(
                            rs.getString("id"),
                            rs.getString("nom"),
                            rs.getString("prenoms"),
                            rs.getInt("telephone"),
                            rs.getString("email"),
                            rs.getString("adresse"),
                            rs.getDate("date_embauche")
//                            rs.getString("departement"), // Lignes commentées
//                            rs.getString("poste")
                    );
                    liste.add(e);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }
}