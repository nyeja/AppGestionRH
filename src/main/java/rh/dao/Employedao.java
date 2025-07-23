package rh.dao;

import rh.model.EmployeModel;
import rh.utils.ConnexionDB;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Employedao {

    public String getNextEmployeId() throws SQLException {
        String sql = "SELECT get_next_employe_id() FROM dual";
        try (Connection con = ConnexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getString(1);
            } else {
                throw new SQLException("Impossible de récupérer le nouvel ID employé");
            }
        }
    }

    public void ajouterEmploye(EmployeModel EmployeModel) throws SQLException {
        String sql = "INSERT INTO employe (nom, prenoms, telephone, email, adresse, date_embauche) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = ConnexionDB.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, EmployeModel.getNom());
            ps.setString(2, EmployeModel.getPrenom());
            ps.setInt(3, EmployeModel.getTelephone());
            ps.setString(4, EmployeModel.getEmail());
            ps.setString(5, EmployeModel.getAdresse());
            ps.setDate(6, new java.sql.Date(EmployeModel.getDateEmbauche().getTime()));
//            ps.setString(7, Employe.getDepartement());
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
//            ps.setString(7, Employe.getDepartement());
//            ps.setString(8, Employe.getPoste());
           ps.setString(7, EmployeModel.getId());

            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Employé modifié avec succès : " + EmployeModel.getId());
            }


        }
    }

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
//                        rs.getString("departement"),
//                        rs.getString("poste")
                );
                liste.add(e);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }

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
//                            rs.getString("departement"),
//                            rs.getString("poste")
                    );
                }
            }
        }
        return null;
    }

//    public List<employe> rechercherEmployes(String critere) {
//        List<employe> liste = new ArrayList<>();
//        String sql = "SELECT * FROM employe WHERE UPPER(nom) LIKE ? OR UPPER(prenoms) LIKE ? OR UPPER(email) LIKE ? OR UPPER(departement) LIKE ? OR UPPER(poste) LIKE ? ORDER BY nom, prenoms";
//        try (Connection conn = ConnexionDB.getConnection();
//             PreparedStatement ps = conn.prepareStatement(sql)) {
//
//            String searchPattern = "%" + critere.toUpperCase() + "%";
//            ps.setString(1, searchPattern);
//            ps.setString(2, searchPattern);
//            ps.setString(3, searchPattern);
//            ps.setString(4, searchPattern);
//            ps.setString(5, searchPattern);
//
//            try (ResultSet rs = ps.executeQuery()) {
//                while (rs.next()) {
//                    employe e = new employe(
//                            rs.getString("id"),
//                            rs.getString("nom"),
//                            rs.getString("prenoms"),
//                            rs.getInt("telephone"),
//                            rs.getString("email"),
//                            rs.getString("adresse"),
//                            rs.getDate("date_embauche")
////                            rs.getString("departement"),
////                            rs.getString("poste")
//                    );
//                    liste.add(e);
//                }
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return liste;
//    }
}
