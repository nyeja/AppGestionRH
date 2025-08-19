package rh.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;


import javafx.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Contrôleur pour la vue de l'employé
 * Gère la navigation entre les différentes vues: demande de congé,demande de permission,paramètre
 *
 * @author Rajerisoa Ny Eja Manoa
 * @version 1.0
 * @since 19/08/2025
 */

public class EmployeViewController implements Initializable{
    @FXML private StackPane contentArea;
    @FXML private Button btnLogout;
    private String connectedEmployeMatricule;

    /**
     * Définit le matricule de l'employé connecté.
     * @param matricule Le matricule de l'employé connecté.
     */
    public void setConnectedEmployeMatricule(String matricule) {
        this.connectedEmployeMatricule = matricule;
        // On ne charge rien ici, le tableau de bord est juste une page de navigation.
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    /**
     * Gère l'action du bouton "Demande de Congé".
     * Charge la vue de gestion des congés pour l'employé.
     */
    @FXML
    private void handleCongeButton(ActionEvent event ) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Conge.fxml"));
            Parent congeView = loader.load();

            // Si le contrôleur a une méthode setMatriculeEmploye, appelez-la
            CongeController congeController = loader.getController();
            congeController.setEmployeMatricule(this.connectedEmployeMatricule);


            contentArea.getChildren().setAll(congeView);
        } catch (IOException e) {
            e.printStackTrace();
            // Gérer l'erreur de chargement
        }
    }

    /**
     * Gère l'action du bouton "Demande de Permission".
     * Charge la vue de gestion des permissions pour l'employé.
     */
    @FXML
    private void handlePermissionButton(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/employe_permission_view.fxml"));
            Parent permissionView = loader.load();

            // Appelez la méthode setMatriculeEmploye dans le contrôleur de permission
            EmployePermissionController permissionController = loader.getController();
            permissionController.setMatriculeEmploye(this.connectedEmployeMatricule);


            contentArea.getChildren().setAll(permissionView);
        } catch (IOException e) {
            e.printStackTrace();
            // Gérer l'erreur de chargement
        }
    }

    /**
     * Gère la déconnexion de l'utilisateur.
     */
    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnLogout.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
