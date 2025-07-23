package rh.controller; // Ou le package approprié, ex: rh.controller.error

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

import java.io.IOException;

import rh.utils.SessionManager; // Pour gérer la déconnexion

public class ErrorController {

    @FXML
    private Label errorMessageLabel; // Pour afficher un message personnalisé

    /**
     * Méthode pour définir un message d'erreur personnalisé.
     * Appelez cette méthode depuis le contrôleur qui charge ErrorView.fxml.
     * Par exemple: errorController.setErrorMessage("Votre rôle 'admin' n'est pas encore géré.");
     */
    public void setErrorMessage(String message) {
        if (errorMessageLabel != null) {
            errorMessageLabel.setText(message);
        }
    }

    @FXML
    private void handleReturnToLogin(ActionEvent event) {
        // Optionnel : Déconnecte l'utilisateur si la session était active
        SessionManager.logout();
        System.out.println("Déconnexion effectuée et retour à la page de login.");

        try {
            // Charger la vue de connexion
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LoginView.fxml"));
            Parent root = loader.load();

            // Obtenir la scène actuelle et la remplacer
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Connexion RHPlus");
            stage.show();
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de la page de connexion : " + e.getMessage());
            e.printStackTrace();
        }
    }
}