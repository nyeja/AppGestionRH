package rh.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    @FXML
    private StackPane contentArea; // This is where dynamic content will be loaded

    private String connectedEmployeMatricule;


    public void setConnectedEmployeMatricule(String matricule) {
        this.connectedEmployeMatricule = matricule;
        System.out.println("Dashboard initialisé avec matricule: " + matricule);
        loadDemandeConge(null); // Load employee leave request by default
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initial setup for the dashboard if needed
    }

    // --- Navigation Methods ---

    @FXML
    private void loadDemandeConge(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Conge.fxml")); // Path to your employee leave request FXML
            Parent demandeCongeView = loader.load();

            // Pass the matricule to the CongeController
            CongeController congeController = loader.getController();
            if (congeController != null && connectedEmployeMatricule != null) {
                congeController.setEmployeMatricule(connectedEmployeMatricule);
            } else {
                System.err.println("ERREUR: CongeController ou matricule manquant pour Demande de Congé.");
            }

            contentArea.getChildren().setAll(demandeCongeView);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement de DemandeConge.fxml: " + e.getMessage());
        }
    }

    @FXML
    private void loadListeCongesRH(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/rh_conge_view.fxml")); // Path to your HR leave list FXML
            Parent listeCongesView = loader.load();
            contentArea.getChildren().setAll(listeCongesView);
            // The RhCongeController will handle loading its own data in its initialize method
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement de rh_conge_view.fxml: " + e.getMessage());
        }
    }

    @FXML
    private void loadGererEmployes(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Employe.fxml")); // Path to new Employee CRUD FXML
            Parent employeeManagementView = loader.load();
            contentArea.getChildren().setAll(employeeManagementView);
            // The EmployeeManagementController will handle loading its own data
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement de EmployeeManagement.fxml: " + e.getMessage());
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            // Get the current stage
            Stage stage = (Stage) contentArea.getScene().getWindow();

            // Load the login view
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/Login.fxml")); // Adjust to your Login FXML path
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Login");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement de la vue de connexion: " + e.getMessage());
        }
    }
}