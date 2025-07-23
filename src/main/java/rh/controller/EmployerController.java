package rh.controller;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class EmployerController {

    @FXML
    private void DemandeConger(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Changement de mot de passe");
        alert.setHeaderText(null);
        alert.setContentText("Fonction à implémenter : demande de changement de mot de passe.");
        alert.showAndWait();
    }

    @FXML
    private void handleViewProfile(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Profil");
        alert.setHeaderText(null);
        alert.setContentText("Fonction à implémenter : consultation du profil.");
        alert.showAndWait();
    }

   @FXML
private javafx.scene.control.Button logoutButton;


 @FXML
    private void handleLogout(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/rh/fxml/Login/Login.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Connexion");
            stage.setScene(new Scene(root));
            stage.show();

            // Fermer la fenêtre actuelle
            ((Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow()).close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
