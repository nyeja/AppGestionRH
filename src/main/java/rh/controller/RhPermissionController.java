package rh.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.event.ActionEvent;

import rh.dao.Permissiondao;
import rh.model.Permission;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Contrôleur FXML pour la gestion des demandes de permission côté RH.
 * Affiche les nouvelles demandes à traiter et l'historique.
 */
public class RhPermissionController implements Initializable {


    @FXML private TableView<Permission> newPermissionTable;
    @FXML private TableColumn<Permission, Integer> colIdPermissionNew;
    @FXML private TableColumn<Permission, String> colMatriculeEmployeNew;
    @FXML private TableColumn<Permission, String> colNomCompletNew;
    @FXML private TableColumn<Permission, LocalDate> colDateDebutNew;
    @FXML private TableColumn<Permission, String> colHeureDebutNew;
    @FXML private TableColumn<Permission, LocalDate> colDateFinNew;
    @FXML private TableColumn<Permission, String> colHeureFinNew;
    @FXML private TableColumn<Permission, String> colMotifNew;
    @FXML private TableColumn<Permission, String> colStatutNew;

    @FXML private TableView<Permission> historyPermissionTable;
    @FXML private TableColumn<Permission, Integer> colIdPermissionHistory;
    @FXML private TableColumn<Permission, String> colMatriculeEmployeHistory;
    @FXML private TableColumn<Permission, String> colNomCompletHistory;
    @FXML private TableColumn<Permission, LocalDate> colDateDebutHistory;
    @FXML private TableColumn<Permission, String> colHeureDebutHistory;
    @FXML private TableColumn<Permission, LocalDate> colDateFinHistory;
    @FXML private TableColumn<Permission, String> colHeureFinHistory;
    @FXML private TableColumn<Permission, String> colMotifHistory;
    @FXML private TableColumn<Permission, String> colStatutHistory;

    @FXML private Label messageStatutRh;

    // --- Variables d'état et dépendances ---
    private Permissiondao permissionDAO;
    private ObservableList<Permission> newPermissionList;
    private ObservableList<Permission> historyPermissionList;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        permissionDAO = new Permissiondao();
        newPermissionList = FXCollections.observableArrayList();
        historyPermissionList = FXCollections.observableArrayList();

        // Initialisation des colonnes du tableau des nouvelles demandes
        colIdPermissionNew.setCellValueFactory(new PropertyValueFactory<>("idPermission"));
        colMatriculeEmployeNew.setCellValueFactory(new PropertyValueFactory<>("matriculeEmploye"));
        colNomCompletNew.setCellValueFactory(new PropertyValueFactory<>("nomCompletEmploye"));
        colMotifNew.setCellValueFactory(new PropertyValueFactory<>("motif"));
        colStatutNew.setCellValueFactory(new PropertyValueFactory<>("statut"));

        // Formatage des colonnes de date pour le tableau "Nouvelles Demandes"
        colDateDebutNew.setCellValueFactory(new PropertyValueFactory<>("dateDebut"));
        colDateDebutNew.setCellFactory(this::createDateCell);
        colDateFinNew.setCellValueFactory(new PropertyValueFactory<>("dateFin"));
        colDateFinNew.setCellFactory(this::createDateCell);

        // Liaison et formatage des colonnes d'heures pour "Nouvelles Demandes"
        colHeureDebutNew.setCellValueFactory(cellData -> {
            Permission permission = cellData.getValue();
            String heureMinute = String.format("%02d:%02d", permission.getHeureDebut(), permission.getMinuteDebut());
            return new SimpleStringProperty(heureMinute);
        });
        colHeureFinNew.setCellValueFactory(cellData -> {
            Permission permission = cellData.getValue();
            String heureMinute = String.format("%02d:%02d", permission.getHeureFin(), permission.getMinuteFin());
            return new SimpleStringProperty(heureMinute);
        });

        // Initialisation des colonnes du tableau de l'historique
        colIdPermissionHistory.setCellValueFactory(new PropertyValueFactory<>("idPermission"));
        colMatriculeEmployeHistory.setCellValueFactory(new PropertyValueFactory<>("matriculeEmploye"));
        colNomCompletHistory.setCellValueFactory(new PropertyValueFactory<>("nomCompletEmploye"));
        colMotifHistory.setCellValueFactory(new PropertyValueFactory<>("motif"));
        colStatutHistory.setCellValueFactory(new PropertyValueFactory<>("statut"));

        // Formatage des colonnes de date pour le tableau "Historique"
        colDateDebutHistory.setCellValueFactory(new PropertyValueFactory<>("dateDebut"));
        colDateDebutHistory.setCellFactory(this::createDateCell);
        colDateFinHistory.setCellValueFactory(new PropertyValueFactory<>("dateFin"));
        colDateFinHistory.setCellFactory(this::createDateCell);

        // Liaison et formatage des colonnes d'heures pour "Historique"
        colHeureDebutHistory.setCellValueFactory(cellData -> {
            Permission permission = cellData.getValue();
            String heureMinute = String.format("%02d:%02d", permission.getHeureDebut(), permission.getMinuteDebut());
            return new SimpleStringProperty(heureMinute);
        });
        colHeureFinHistory.setCellValueFactory(cellData -> {
            Permission permission = cellData.getValue();
            String heureMinute = String.format("%02d:%02d", permission.getHeureFin(), permission.getMinuteFin());
            return new SimpleStringProperty(heureMinute);
        });

        newPermissionTable.setItems(newPermissionList);
        historyPermissionTable.setItems(historyPermissionList);

        loadPermissionRequests();
    }

    /**
     * Crée une cellule de table pour formatter l'affichage d'une date.
     */
    private TableCell<Permission, LocalDate> createDateCell(TableColumn<Permission, LocalDate> column) {
        return new TableCell<Permission, LocalDate>() {
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

    /**
     * Charge toutes les demandes de permission et les distribue dans les deux tableaux.
     */
    @FXML
    private void loadPermissionRequests() {
        messageStatutRh.setText("Chargement des demandes...");
        try {
            List<Permission> allPermissions = permissionDAO.getAllPermissions();

            newPermissionList.clear();
            historyPermissionList.clear();

            for (Permission p : allPermissions) {
                if ("En attente".equals(p.getStatut())) {
                    newPermissionList.add(p);
                } else {
                    historyPermissionList.add(p);
                }
            }
            messageStatutRh.setText("Chargement terminé.");
        } catch (SQLException e) {
            messageStatutRh.setText("Erreur de chargement : " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Gère l'action d'approbation d'une demande de permission.
     */
    @FXML
    private void handleApprouver(ActionEvent event) {
        Permission selectedPermission = newPermissionTable.getSelectionModel().getSelectedItem();
        if (selectedPermission != null) {
            try {
                permissionDAO.updatePermissionStatut(selectedPermission.getIdPermission(), "Approuvée");
                messageStatutRh.setText("Demande " + selectedPermission.getIdPermission() + " approuvée.");
                loadPermissionRequests();
            } catch (SQLException e) {
                messageStatutRh.setText("Erreur d'approbation : " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            showError("Aucune sélection", "Veuillez sélectionner une demande à approuver.");
        }
    }

    /**
     * Gère l'action de refus d'une demande de permission.
     */
    @FXML
    private void handleRefuser(ActionEvent event) {
        Permission selectedPermission = newPermissionTable.getSelectionModel().getSelectedItem();
        if (selectedPermission != null) {
            try {
                permissionDAO.updatePermissionStatut(selectedPermission.getIdPermission(), "Refusée");
                messageStatutRh.setText("Demande " + selectedPermission.getIdPermission() + " refusée.");
                loadPermissionRequests();
            } catch (SQLException e) {
                messageStatutRh.setText("Erreur de refus : " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            showError("Aucune sélection", "Veuillez sélectionner une demande à refuser.");
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}