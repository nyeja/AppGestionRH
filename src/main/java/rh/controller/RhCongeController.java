package rh.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.event.ActionEvent;

import rh.dao.Congedao;
import rh.model.Conge;
import rh.utils.ConnexionDB;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Contrôleur FXML pour l'interface de gestion des congés destinée aux ressources humaines (RH).
 * Cette classe permet de visualiser toutes les demandes de congé soumises et d'approuver ou
 * de refuser les demandes sélectionnées.
 *
 * @author Rajerisoa Ny Eja Manoa
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
    @FXML private TableColumn<Conge, Integer> colSoldeConge;

    // --- Composants FXML pour l'HISTORIQUE ---
    @FXML private TableView<Conge> congeHistoryTable;
    @FXML private TableColumn<Conge, Integer> colIdCongeHistory;
    @FXML private TableColumn<Conge, String> colMatriculeEmployeHistory;
    @FXML private TableColumn<Conge, String> colNomCompletHistory;
    @FXML private TableColumn<Conge, LocalDate> colDateDebutHistory;
    @FXML private TableColumn<Conge, LocalDate> colDateFinHistory;
    @FXML private TableColumn<Conge, String> colTypeCongeHistory;
    @FXML private TableColumn<Conge, String> colJustificatifHistory;
    @FXML private TableColumn<Conge, String> colStatutHistory;

    @FXML private Button btnApprouver;
    @FXML private Button btnRefuser;
    @FXML private Button btnActualiser;
    @FXML private Label messageStatutRh;




    // --- Dépendances et variables d'état ---
    private Congedao congeDAO;
    private ObservableList<Conge> congeList;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy"); // Formatteur de date
    private ObservableList<Conge> historyCongeList; // Liste pour l'historique

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        congeDAO = new Congedao();
        historyCongeList = FXCollections.observableArrayList();

        // Initialisation des colonnes de la TableView.
        colIdConge.setCellValueFactory(new PropertyValueFactory<>("idConge"));
        colMatriculeEmploye.setCellValueFactory(new PropertyValueFactory<>("matriculeEmploye"));
        // Assurez-vous d'avoir une propriété 'nomComplet' dans votre classe Conge
        colNomComplet.setCellValueFactory(new PropertyValueFactory<>("nomComplet"));
        colDateDebut.setCellValueFactory(new PropertyValueFactory<>("dateDebut"));
        colDateFin.setCellValueFactory(new PropertyValueFactory<>("dateFin"));
        colTypeConge.setCellValueFactory(new PropertyValueFactory<>("typeConge"));
        colJustificatif.setCellValueFactory(new PropertyValueFactory<>("justificatif"));
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));
        colSoldeConge.setCellValueFactory(new PropertyValueFactory<>("soldeConge")); // Liaison de la nouvelle colonne

        // Initialisation des colonnes pour le tableau "Historique"
        colIdCongeHistory.setCellValueFactory(new PropertyValueFactory<>("idConge"));
        colMatriculeEmployeHistory.setCellValueFactory(new PropertyValueFactory<>("matriculeEmploye"));
        colNomCompletHistory.setCellValueFactory(new PropertyValueFactory<>("nomComplet"));
        colDateDebutHistory.setCellValueFactory(new PropertyValueFactory<>("dateDebut"));
        colDateDebutHistory.setCellFactory(this::createDateCell);
        colDateFinHistory.setCellValueFactory(new PropertyValueFactory<>("dateFin"));
        colDateFinHistory.setCellFactory(this::createDateCell);
        colTypeCongeHistory.setCellValueFactory(new PropertyValueFactory<>("typeConge"));
        colJustificatifHistory.setCellValueFactory(new PropertyValueFactory<>("justificatif"));
        colStatutHistory.setCellValueFactory(new PropertyValueFactory<>("statut"));

        congeHistoryTable.setItems(historyCongeList);

        // Configuration de l'affichage des dates
        colDateDebut.setCellValueFactory(new PropertyValueFactory<>("dateDebut"));
        colDateDebut.setCellFactory(column -> {
            return new javafx.scene.control.TableCell<Conge, LocalDate>() {
                @Override
                protected void updateItem(LocalDate item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(dateFormatter.format(item));
                    }
                }
            };
        });

        colDateFin.setCellValueFactory(new PropertyValueFactory<>("dateDebut"));
        colDateFin.setCellFactory(column -> {
            return new javafx.scene.control.TableCell<Conge, LocalDate>() {
                @Override
                protected void updateItem(LocalDate item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(dateFormatter.format(item));
                    }
                }
            };
        });

        congeList = FXCollections.observableArrayList();
        congeTable.setItems(congeList);

        btnApprouver.setDisable(true);
        btnRefuser.setDisable(true);

        congeTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    boolean isSelected = newSelection != null;
                    btnApprouver.setDisable(!isSelected);
                    btnRefuser.setDisable(!isSelected);
                }
        );

        loadCongeRequests();
    }

    /**
     * Crée une cellule de table pour formater l'affichage d'une date.
     */
    private TableCell<Conge, LocalDate> createDateCell(TableColumn<Conge, LocalDate> column) {
        return new TableCell<Conge, LocalDate>() {
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(dateFormatter.format(item));
                }
            }
        };
    }

    // --- Méthodes de gestion des actions FXML ---

    /**
     * Méthode pour charger les demandes de congés
     * */
    @FXML
    private void loadCongeRequests() {
        try {
            List<Conge> allConges = congeDAO.getAllConges();

            congeList.clear();
            historyCongeList.clear();
            for (Conge c : allConges) {
                if ("En attente".equals(c.getStatut())) { // Assurez-vous que le statut par défaut est bien "En attente"
                    congeList.add(c);
                } else {
                    historyCongeList.add(c);
                }
            }
        } catch (SQLException e) {
            showError("Erreur de base de données", "Impossible de charger les demandes de congé : " + e.getMessage());
            e.printStackTrace();
            updateStatus("Erreur de chargement des demandes.");
        }
    }

    /**
     *Méthode pour le changement de statut en approuvé ou refusé
     */

    @FXML
    private void handleApprouver(ActionEvent event) {
        Conge selectedConge = congeTable.getSelectionModel().getSelectedItem();
        if (selectedConge != null) {
            try {
                // 1. Mettre à jour le statut du congé
                selectedConge.setStatut("Approuve");
                congeDAO.updateCongeStatut(selectedConge);

                // 2. Calculer la durée du congé
                long dureeConge = ChronoUnit.DAYS.between(selectedConge.getDateDebut(), selectedConge.getDateFin());

                // 3. Mettre à jour le solde de l'employé
                mettreAJourSoldeEmploye(selectedConge.getMatriculeEmploye(), (int) dureeConge);

                loadCongeRequests();

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

    @FXML
    private void handleRefuser(ActionEvent event) {
        Conge selectedConge = congeTable.getSelectionModel().getSelectedItem();
        if (selectedConge != null) {
            try {
                selectedConge.setStatut("Refuse");
                congeDAO.updateCongeStatut(selectedConge);

                loadCongeRequests();

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


    /**
     * Méthode interne pour mettre à jour le solde de congé d'un employé.
     * Cette méthode remplace la méthode `validerConge` qui était dans le contrôleur.
     * @param employeId L'ID de l'employé concerné.
     * @param dureeConge La durée du congé en jours.
     */
    private void mettreAJourSoldeEmploye(String employeId, int dureeConge) throws SQLException {
        String updateSoldeSql = "UPDATE EMPLOYE SET SOLDE_CONGE = SOLDE_CONGE - ? WHERE id = ?";

        try (Connection conn = ConnexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(updateSoldeSql)) {

            pstmt.setInt(1, dureeConge);
            pstmt.setString(2, employeId);
            pstmt.executeUpdate();
            System.out.println("Solde de l'employé " + employeId + " mis à jour. Décrémenté de " + dureeConge + " jours.");
        }
    }

    // --- Méthodes utilitaires pour la communication avec l'utilisateur ---

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
