package rh.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.event.ActionEvent;

import rh.dao.congedao; // Votre DAO pour les congés
import rh.model.Conge; // Votre modèle de demande de congé

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class RhCongeController implements Initializable {

    @FXML private TableView<Conge> congeTable;
    @FXML private TableColumn<Conge, String> colIdConge;
    @FXML private TableColumn<Conge, String> colMatriculeEmploye;
    @FXML private TableColumn<Conge, LocalDate> colDateDebut;
    @FXML private TableColumn<Conge, LocalDate> colDateFin;
    @FXML private TableColumn<Conge, String> colTypeConge;
    @FXML private TableColumn<Conge, String> colJustificatif;
    @FXML private TableColumn<Conge, String> colStatut;
    @FXML private Button btnApprouver;
    @FXML private Button btnRefuser;
    @FXML private Button btnActualiser;
    @FXML private Label messageStatutRh;

    private congedao congeDAO;
    private ObservableList<Conge> congeList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        congeDAO = new congedao();

        // Initialisation des colonnes de la TableView
        colIdConge.setCellValueFactory(new PropertyValueFactory<>("idConge"));
        colMatriculeEmploye.setCellValueFactory(new PropertyValueFactory<>("matriculeEmploye"));
        colDateDebut.setCellValueFactory(new PropertyValueFactory<>("dateDebut"));
        colDateFin.setCellValueFactory(new PropertyValueFactory<>("dateFin"));
        colTypeConge.setCellValueFactory(new PropertyValueFactory<>("typeConge"));
        colJustificatif.setCellValueFactory(new PropertyValueFactory<>("justificatif"));
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));

        congeList = FXCollections.observableArrayList();
        congeTable.setItems(congeList);

        // Désactiver les boutons d'action au début
        btnApprouver.setDisable(true);
        btnRefuser.setDisable(true);

        // Écouteur de sélection de ligne pour activer/désactiver les boutons
        congeTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    boolean isSelected = newSelection != null;
                    btnApprouver.setDisable(!isSelected);
                    btnRefuser.setDisable(!isSelected);
                }
        );

        loadCongeRequests(); // Charge les demandes au démarrage
    }

    // ⭐ Méthode pour charger toutes les demandes de congé
    @FXML
    private void loadCongeRequests() {
        try {
            congeList.clear();
            // Appelle une nouvelle méthode dans congedao pour récupérer toutes les demandes
            congeList.addAll(congeDAO.getAllConges());
            updateStatus("Demandes de congé chargées avec succès.");
        } catch (SQLException e) {
            showError("Erreur de base de données", "Impossible de charger les demandes de congé: " + e.getMessage());
            e.printStackTrace();
            updateStatus("Erreur de chargement des demandes.");
        }
    }

    // ⭐ Méthode pour approuver une demande sélectionnée
    @FXML
    private void handleApprouver(ActionEvent event) {
        Conge selectedConge = congeTable.getSelectionModel().getSelectedItem();
        if (selectedConge != null) {
            try {
                selectedConge.setStatut("Approuve"); // Met à jour le statut du modèle
                congeDAO.updateCongeStatut(selectedConge); // Appelle la méthode pour mettre à jour en DB
                congeTable.refresh(); // Rafraîchit la TableView pour montrer le nouveau statut
                showSuccess("Demande de congé approuvée pour " + selectedConge.getMatriculeEmploye());
                updateStatus("Demande de congé approuvée.");
            } catch (SQLException e) {
                showError("Erreur de base de données", "Impossible d'approuver la demande: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            showWarning("Aucune sélection", "Veuillez sélectionner une demande de congé à approuver.");
        }
    }

    // ⭐ Méthode pour refuser une demande sélectionnée
    @FXML
    private void handleRefuser(ActionEvent event) {
        Conge selectedConge = congeTable.getSelectionModel().getSelectedItem();
        if (selectedConge != null) {
            try {
                selectedConge.setStatut("Refuse"); // Met à jour le statut du modèle
                congeDAO.updateCongeStatut(selectedConge); // Appelle la méthode pour mettre à jour en DB
                congeTable.refresh(); // Rafraîchit la TableView
                showSuccess("Demande de congé refusée pour " + selectedConge.getMatriculeEmploye());
                updateStatus("Demande de congé refusée.");
            } catch (SQLException e) {
                showError("Erreur de base de données", "Impossible de refuser la demande: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            showWarning("Aucune sélection", "Veuillez sélectionner une demande de congé à refuser.");
        }
    }

    // --- Méthodes utilitaires pour les messages (comme dans vos autres contrôleurs) ---
    private void updateStatus(String message) {
        if (messageStatutRh != null) {
            messageStatutRh.setText(message);
        }
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Attention");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}