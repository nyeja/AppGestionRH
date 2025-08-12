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

import rh.dao.congedao;
import rh.model.Conge;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

/**
 * Contrôleur FXML pour l'interface de gestion des congés destinée aux ressources humaines (RH).
 * Cette classe permet de visualiser toutes les demandes de congé soumises et d'approuver ou
 * de refuser les demandes sélectionnées.
 *
 * @author Votre Nom
 * @version 1.0
 * @since 2025-08-12
 */
public class RhCongeController implements Initializable {

    // --- Composants FXML de l'interface utilisateur ---
    @FXML private TableView<Conge> congeTable;
    @FXML private TableColumn<Conge, String> colIdConge;
    @FXML private TableColumn<Conge, String> colMatriculeEmploye;
    @FXML private TableColumn<Conge, String> colNomComplet;
    @FXML private TableColumn<Conge, LocalDate> colDateDebut;
    @FXML private TableColumn<Conge, LocalDate> colDateFin;
    @FXML private TableColumn<Conge, String> colTypeConge;
    @FXML private TableColumn<Conge, String> colJustificatif;
    @FXML private TableColumn<Conge, String> colStatut;
    @FXML private Button btnApprouver;
    @FXML private Button btnRefuser;
    @FXML private Button btnActualiser;
    @FXML private Label messageStatutRh;

    // --- Dépendances et variables d'état ---
    private congedao congeDAO;
    /**
     * Liste observable des congés pour la TableView, permettant de synchroniser les données de l'UI
     * avec les modifications apportées à la liste.
     */
    private ObservableList<Conge> congeList;

    /**
     * Méthode d'initialisation du contrôleur. Appelée automatiquement après le chargement du fichier FXML.
     * Configure les colonnes de la TableView, initialise l'écouteur de sélection et charge les données initiales.
     *
     * @param url L'emplacement utilisé pour résoudre les chemins relatifs pour l'objet racine.
     * @param resourceBundle Le ResourceBundle qui gère les textes localisés.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        congeDAO = new congedao();

        // Initialisation des colonnes de la TableView. Chaque colonne est liée à une propriété de l'objet Conge.
        colIdConge.setCellValueFactory(new PropertyValueFactory<>("idConge"));
        colMatriculeEmploye.setCellValueFactory(new PropertyValueFactory<>("matriculeEmploye"));
        colNomComplet.setCellValueFactory(new PropertyValueFactory<>("nomComplet"));
        colDateDebut.setCellValueFactory(new PropertyValueFactory<>("dateDebut"));
        colDateFin.setCellValueFactory(new PropertyValueFactory<>("dateFin"));
        colTypeConge.setCellValueFactory(new PropertyValueFactory<>("typeConge"));
        colJustificatif.setCellValueFactory(new PropertyValueFactory<>("justificatif"));
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));

        congeList = FXCollections.observableArrayList();
        congeTable.setItems(congeList);

        // Désactiver les boutons d'action tant qu'aucune ligne n'est sélectionnée.
        btnApprouver.setDisable(true);
        btnRefuser.setDisable(true);

        // Ajout d'un écouteur pour détecter la sélection d'une ligne dans la TableView.
        congeTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    boolean isSelected = newSelection != null;
                    btnApprouver.setDisable(!isSelected);
                    btnRefuser.setDisable(!isSelected);
                }
        );

        // Charge les demandes de congé au démarrage de l'interface.
        loadCongeRequests();
    }

    // --- Méthodes de gestion des actions FXML ---

    /**
     * Méthode appelée pour rafraîchir la liste des demandes de congé affichées dans la TableView.
     */
    @FXML
    private void loadCongeRequests() {
        try {
            congeList.clear();
            // Récupère toutes les demandes de congé de la base de données via le DAO.
            congeList.addAll(congeDAO.getAllConges());
            updateStatus("Demandes de congé chargées avec succès.");
        } catch (SQLException e) {
            showError("Erreur de base de données", "Impossible de charger les demandes de congé : " + e.getMessage());
            e.printStackTrace();
            updateStatus("Erreur de chargement des demandes.");
        }
    }

    /**
     * Gère l'événement de clic sur le bouton "Approuver".
     * Met à jour le statut de la demande de congé sélectionnée à "Approuve".
     *
     * @param event L'événement d'action.
     */
    @FXML
    private void handleApprouver(ActionEvent event) {
        Conge selectedConge = congeTable.getSelectionModel().getSelectedItem();
        if (selectedConge != null) {
            try {
                selectedConge.setStatut("Approuve");
                congeDAO.updateCongeStatut(selectedConge);
                congeTable.refresh();
                showSuccess("Demande de congé approuvée pour " + selectedConge.getNomComplet() + " (" + selectedConge.getMatriculeEmploye() + ")");
                updateStatus("Demande de congé approuvée.");
            } catch (SQLException e) {
                showError("Erreur de base de données", "Impossible d'approuver la demande : " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            showWarning("Aucune sélection", "Veuillez sélectionner une demande de congé à approuver.");
        }
    }

    /**
     * Gère l'événement de clic sur le bouton "Refuser".
     * Met à jour le statut de la demande de congé sélectionnée à "Refuse".
     *
     * @param event L'événement d'action.
     */
    @FXML
    private void handleRefuser(ActionEvent event) {
        Conge selectedConge = congeTable.getSelectionModel().getSelectedItem();
        if (selectedConge != null) {
            try {
                selectedConge.setStatut("Refuse");
                congeDAO.updateCongeStatut(selectedConge);
                congeTable.refresh();
                showSuccess("Demande de congé refusée pour " + selectedConge.getNomComplet() + " (" + selectedConge.getMatriculeEmploye() + ")");
                updateStatus("Demande de congé refusée.");
            } catch (SQLException e) {
                showError("Erreur de base de données", "Impossible de refuser la demande : " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            showWarning("Aucune sélection", "Veuillez sélectionner une demande de congé à refuser.");
        }
    }

    // --- Méthodes utilitaires pour la communication avec l'utilisateur ---

    /**
     * Met à jour le message de statut affiché dans la barre de statut de l'application.
     *
     * @param message Le message à afficher.
     */
    private void updateStatus(String message) {
        if (messageStatutRh != null) {
            messageStatutRh.setText(message);
        }
    }

    /**
     * Affiche une boîte de dialogue de type information (succès).
     *
     * @param message Le message à afficher.
     */
    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Affiche une boîte de dialogue de type erreur.
     *
     * @param title Le titre de la boîte de dialogue.
     * @param message Le message d'erreur à afficher.
     */
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Affiche une boîte de dialogue de type avertissement.
     *
     * @param title Le titre de la boîte de dialogue.
     * @param message Le message d'avertissement à afficher.
     */
    private void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Attention");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}