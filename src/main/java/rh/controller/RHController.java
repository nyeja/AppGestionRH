/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rh.controller;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;

import java.io.IOException;

public class RHController {

    @FXML
    private void gererPostes(ActionEvent event) {
        afficherMessage("Gestion des postes", "Fonction non encore implémentée.");
    }

    @FXML
    private void validerDemandes(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/rh/fxml/Password/valider_demandes.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Validation des demandes de mot de passe");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            afficherMessage("Erreur", "Impossible de charger l'interface de validation.");
        }
    }

    @FXML
    private void deconnexion(ActionEvent event) {
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

    private void afficherMessage(String titre, String contenu) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(contenu);
        alert.showAndWait();
    }


}
