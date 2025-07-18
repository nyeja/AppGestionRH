package rh.dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import rh.model.employe.employe;
import rh.utils.ConnexionDB;
import javafx.scene.control.*;

import java.sql.*;
import java.time.LocalDate;

public class employedao {
    // Champs du formulaire
    @FXML private TextField txtEmployeeId;
    @FXML private TextField txtNom;
    @FXML private TextField txtPrenom;
    @FXML private TextField txtEmail;
    @FXML private TextField txtTelephone;
    @FXML private DatePicker dateEmbauche;
    @FXML private TextField txtAdresse;
    @FXML private ComboBox<String> comboDepartement;
    @FXML private ComboBox<String> comboPoste;
    @FXML
    private TableView<employe> tableEmployes;
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

    public void ajouterEmploye(employe Employe) throws SQLException {
        String sql = "INSERT INTO employe (nom, prenoms, telephone, email, adresse, date_embauche , departement , id_poste ) VALUES (?, ?, ?, ?, ?, ? , ? , ?)";
        Connection conn = ConnexionDB.getConnection();
        if (conn == null || conn.isClosed()) {
            System.out.println("Connexion fermée ou nulle !");
        } else {
            System.out.println("Connexion active !");
        }
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, Employe.getNom());
            ps.setString(2, Employe.getPrenom());
            ps.setInt(3, Employe.getTelephone());
            ps.setString(4, Employe.getEmail());
            ps.setString(5, Employe.getAdresse());
            ps.setDate(6, new java.sql.Date(Employe.getDateEmbauche().getTime()));
            ps.setString(7, Employe.getDepartement());
            ps.setString(8, Employe.getPoste());

            int rowsInserted = ps.executeUpdate();
            if (rowsInserted > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        String generatedId = rs.getString(1);
                        Employe.setId(generatedId);
                        System.out.println("Employé ajouté avec ID : " + generatedId);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("ereur : " + e);
        }
    }
    private void fillFormWithEmployee(employe employee) {
        txtEmployeeId.setText(employee.getId());
        txtNom.setText(employee.getNom());
        txtPrenom.setText(employee.getPrenom());
        txtEmail.setText(employee.getEmail());
        txtTelephone.setText(String.valueOf(employee.getTelephone()));

        // Convertir Date vers LocalDate pour le DatePicker
        if (employee.getDateEmbauche() != null) {
            dateEmbauche.setValue(new java.sql.Date(employee.getDateEmbauche().getTime()).toLocalDate());
        }

        txtAdresse.setText(employee.getAdresse());

        // Sélectionner ou saisir le département et le poste
        comboDepartement.setValue(employee.getDepartement());
        comboPoste.setValue(employee.getPoste());
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

    public ObservableList<employe> getAllEmployes() {
        Connection conn = ConnexionDB.getConnection();
        ObservableList<employe> listEmploye = FXCollections.observableArrayList();
        String sql = "SELECT * FROM employe ";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {

                String id = rs.getString("id");
                String nom = rs.getString("nom");
                String prenoms  = rs.getString("prenoms");
                int telephone = rs.getInt("telephone");
                String email = rs.getString("email");
                String addresse = rs.getString("adresse");
                Date date = rs.getDate("date_embauche"); // type java.sql.Date
                String departement = rs.getString("departement");
                String poste = rs.getString("ID_POSTE");

                listEmploye.add(new employe(id , nom , prenoms , telephone , email , addresse , date , departement, poste ));

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listEmploye;
    }

    public employe getEmployeById(String id) throws SQLException {
        String sql = "SELECT * FROM employe WHERE id=?";
        try (Connection conn = ConnexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new employe(
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
