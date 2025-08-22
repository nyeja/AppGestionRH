package rh.controller;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class EmployerController {

    @FXML
    private Button logoutButton; // Référence au bouton de déconnexion

    @FXML
    private void DemandeConger(ActionEvent event) {
        showAlert("Demande de congé", "Fonctionnalité de demande de congé à implémenter");
    }

    @FXML
    private void handleViewProfile(ActionEvent event) {
        showAlert("Profil Utilisateur", "Fonctionnalité de profil à implémenter");
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            // Charger la vue de connexion
            Parent root = FXMLLoader.load(getClass().getResource("/rh/fxml/Login/Login.fxml"));
            Stage loginStage = new Stage();
            loginStage.setTitle("Connexion");
            loginStage.setScene(new Scene(root));
            loginStage.show();

            // Fermer la fenêtre actuelle
            Stage currentStage = (Stage) logoutButton.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible de charger l'écran de connexion");
            e.printStackTrace();
        }
    }

    @FXML
    private void handlePresenceManagement(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/rh/fxml/Absence/GestionAbsence.fxml"));
            Stage presenceStage = new Stage();
            presenceStage.setTitle("Gestion de Présence");
            presenceStage.setScene(new Scene(root));
            
            // Empêcher le redimensionnement si nécessaire
            presenceStage.setResizable(false);
            
            presenceStage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir la gestion de présence");
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}