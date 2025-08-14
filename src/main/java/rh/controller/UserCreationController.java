package rh.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField; // Importation du PasswordField
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import rh.utils.ConnexionDB;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class UserCreationController implements Initializable {

    @FXML
    private ComboBox<String> employeeIdComboBox;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField; // Changement de TextField à PasswordField

    @FXML
    private ComboBox<String> roleComboBox; // Nouveau FXML pour la sélection du rôle

    @FXML
    private Label messageLabel;

    /**
     * Initialise le contrôleur. Cette méthode est appelée automatiquement
     * après le chargement du fichier FXML.
     * Elle peuple les ComboBox avec les données nécessaires au démarrage.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        populateEmployeeComboBox();
        // Remplir le ComboBox des rôles
        ObservableList<String> roles = FXCollections.observableArrayList("administrateur", "rh", "employe");
        roleComboBox.setItems(roles);
    }

    /**
     * Remplit la ComboBox `employeeIdComboBox` avec les identifiants
     * des employés qui n'ont pas encore de compte utilisateur.
     * Cela évite de créer un compte en double pour un même employé.
     */
    private void populateEmployeeComboBox() {
        ObservableList<String> employeeIds = FXCollections.observableArrayList();
        String sql = "SELECT id FROM EMPLOYE WHERE id NOT IN (SELECT EMPLOYE_ID FROM UTILISATEUR)";

        try (Connection conn = ConnexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                employeeIds.add(rs.getString("id"));
            }
            employeeIdComboBox.setItems(employeeIds);

        } catch (SQLException e) {
            System.err.println("Erreur lors du remplissage de la ComboBox : " + e.getMessage());
            e.printStackTrace();
            messageLabel.setText("Erreur lors du chargement des employés.");
        }
    }

    /**
     * Gère l'action de création d'un nouvel utilisateur.
     * Cette méthode est déclenchée lors du clic sur le bouton "Créer l'utilisateur".
     * Elle valide les champs du formulaire, puis utilise une tâche asynchrone
     * pour insérer les nouvelles données de l'utilisateur dans la base de données.
     *
     * @param event L'événement d'action.
     */
    @FXML
    public void handleCreateUser(ActionEvent event) {
        String selectedEmployeeId = employeeIdComboBox.getValue();
        String email = emailField.getText().trim();
        String password = passwordField.getText(); // Récupère la valeur du PasswordField
        String role = roleComboBox.getValue(); // Récupère la valeur du ComboBox du rôle

        if (selectedEmployeeId == null || email.isEmpty() || password.isEmpty() || role == null) {
            messageLabel.setText("Veuillez sélectionner un employé, un rôle et remplir tous les champs.");
            return;
        }

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                String sql = "INSERT INTO UTILISATEUR (EMAIL, PASSWORD, EMPLOYE_ID, ROLE) VALUES (?, ?, ?, ?)";
                try (Connection conn = ConnexionDB.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, email);
                    pstmt.setString(2, password);
                    pstmt.setString(3, selectedEmployeeId);
                    pstmt.setString(4, role);
                    pstmt.executeUpdate();
                }
                return null;
            }
        };

        task.setOnSucceeded(e -> {
            messageLabel.setText("Utilisateur créé avec succès !");
            employeeIdComboBox.getSelectionModel().clearSelection();
            roleComboBox.getSelectionModel().clearSelection();
            emailField.clear();
            passwordField.clear();
            populateEmployeeComboBox();
        });

        task.setOnFailed(e -> {
            messageLabel.setText("Erreur lors de la création de l'utilisateur.");
            task.getException().printStackTrace();
        });

        new Thread(task).start();
    }

    /**
     * Gère l'action du bouton "Retour à la connexion".
     * Cette méthode est déclenchée lors du clic sur le bouton de retour.
     * Elle charge la vue FXML de la page de connexion et remplace la vue actuelle.
     *
     * @param event L'événement d'action.
     */
    @FXML
    public void handleBackToLogin(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Connexion");
            stage.show();
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de la vue de connexion : " + e.getMessage());
            e.printStackTrace();
            messageLabel.setText("Impossible de retourner à la page de connexion.");
        }
    }
}