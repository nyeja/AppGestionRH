package rh.dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import rh.model.Employer;
import rh.model.employe.employe;
import rh.utils.ConnexionDB;
import javafx.scene.control.*;
import java.sql.*;


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
    @FXML private Image imageView;
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
    public static employe trouverParUsername(String username) {
        String sql = "SELECT * FROM employe WHERE nom = ?";
        try (Connection conn = ConnexionDB.getConnection() ;
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new employe(
                        rs.getString("id"),
                        rs.getString("nom"),
                        rs.getString("mdp"),
                        rs.getString("role")
                );

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null; // utilisateur non trouvé
    }
    public static employe getEmployeById(String id) throws SQLException {
        String sql = "SELECT * FROM employe WHERE id=?";
        try (Connection conn = ConnexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new employe(
                            rs.getString("id"),
                            rs.getString("nom"),
                            rs.getString("mdp"),
                            rs.getString("role")
                    );

                }
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public void ajouterEmploye(employe Employe) throws SQLException {
        String sql = "INSERT INTO employe (nom, prenoms, telephone, email, adresse, date_embauche , departement , id_poste , img , mdp , role) VALUES ( ? , ? , ? , ?, ?, ?, ? , ? , ? , ? , ? )";
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
            ps.setString(9,Employe.getImage());
            ps.setString(10,Employe.getMotDePasse());
            ps.setString(11,Employe.getRole());
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
                String prenoms = rs.getString("prenoms");
                int telephone = rs.getInt("telephone");
                String email = rs.getString("email");
                String adresse = rs.getString("adresse");
                Date date = rs.getDate("date_embauche");
                String departement = rs.getString("departement");
                String poste = rs.getString("id_poste");
                String role = rs.getString("role");

                // Ajout de l'objet employe avec tous les paramètres
                listEmploye.add(new employe(
                        id, nom, prenoms, telephone, email, adresse, date,
                        departement, poste ,role ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listEmploye;
    }



}
