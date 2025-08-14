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

/**
 * Contrôleur principal pour le tableau de bord (Dashboard).
 * Gère la navigation entre les différentes vues (demande de congé, liste des congés RH, gestion des employés)
 * en chargeant les fichiers FXML correspondants dans la zone de contenu principale.
 * Il assure également le passage des données nécessaires, comme le matricule de l'employé, entre les contrôleurs.
 *
 * @author Rajerisoa Ny Eja Manoa
 * @version 1.0
 * @since 2025-08-12
 */
public class DashboardController implements Initializable {


    @FXML
    private StackPane contentArea;

    /**
     * Le matricule de l'employé actuellement connecté, utilisé pour le passage de données.
     */
    private String connectedEmployeMatricule;

    /**
     * Définit le matricule de l'employé connecté. Cette méthode est appelée par LoginController.
     *
     * @param matricule Le matricule de l'employé.
     */
    public void setConnectedEmployeMatricule(String matricule) {
        this.connectedEmployeMatricule = matricule;
        System.out.println("Dashboard initialisé avec matricule: " + matricule);
        // Charge la vue de demande de congé par défaut après la connexion.Tu peux changer en Dashboard
        loadDemandeConge(null);
    }

    /**
     * Méthode d'initialisation du contrôleur. Appelée automatiquement après le chargement du fichier FXML.
     *
     * @param location L'emplacement utilisé pour résoudre les chemins relatifs.
     * @param resources Les ressources utilisées pour localiser l'objet racine.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Aucune initialisation spécifique requise au chargement de la vue.
    }

    // --- Méthodes pour la navigation entre les vues ---

    /**
     * Charge et affiche la vue de demande de congé de l'employé.
     * Le matricule de l'employé est passé au contrôleur de la nouvelle vue.
     *
     * @param event L'événement de l'interface utilisateur qui a déclenché cette méthode. Peut être null.
     */
    @FXML
    private void loadDemandeConge(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Conge.fxml"));
            Parent demandeCongeView = loader.load();

            CongeController congeController = loader.getController();
            // S'assure que le contrôleur et le matricule existent avant de les passer.
            if (congeController != null && connectedEmployeMatricule != null) {
                congeController.setEmployeMatricule(connectedEmployeMatricule);
            } else {
                System.err.println("ERREUR: CongeController ou matricule manquant pour la vue de Demande de Congé.");
            }

            contentArea.getChildren().setAll(demandeCongeView);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement de DemandeConge.fxml: " + e.getMessage());
        }
    }

    /**
     * Charge et affiche la vue de la liste des congés pour le service des ressources humaines.
     *
     * @param event L'événement de l'interface utilisateur.
     */
    @FXML
    private void loadListeCongesRH(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/rh_conge_view.fxml"));
            Parent listeCongesView = loader.load();
            contentArea.getChildren().setAll(listeCongesView);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement de rh_conge_view.fxml: " + e.getMessage());
        }
    }

    /**
     * Charge et affiche la vue de gestion des employés (CRUD).
     *
     * @param event L'événement de l'interface utilisateur.
     */
    @FXML
    private void loadGererEmployes(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Employe.fxml"));
            Parent employeeManagementView = loader.load();
            contentArea.getChildren().setAll(employeeManagementView);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement de Employe.fxml: " + e.getMessage());
        }
    }

    /**
     * Gère la déconnexion de l'utilisateur.
     * Ferme la fenêtre actuelle et charge la vue de connexion.
     *
     * @param event L'événement de l'interface utilisateur.
     */
    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            Stage stage = (Stage) contentArea.getScene().getWindow();

            // Chargement de la vue de connexion.
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/Login.fxml"));
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