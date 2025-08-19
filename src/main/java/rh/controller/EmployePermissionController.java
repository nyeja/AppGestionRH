package rh.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.event.ActionEvent;
import javafx.beans.property.SimpleStringProperty; // Importation nécessaire

import rh.dao.Permissiondao;
import rh.model.Permission;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime; // Importation de LocalTime
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.stream.IntStream;

/**
 * Contrôleur FXML pour la gestion des demandes de permission côté employé.
 * Permet à l'employé de soumettre une nouvelle demande et de voir l'historique de ses permissions.
 */
public class EmployePermissionController implements Initializable {

    // --- Composants FXML de l'interface utilisateur ---
    @FXML private DatePicker dateDebutPicker;
    @FXML private DatePicker dateFinPicker;
    @FXML private ComboBox<String> heureDebutBox;
    @FXML private ComboBox<String> minuteDebutBox;
    @FXML private ComboBox<String> heureFinBox;
    @FXML private ComboBox<String> minuteFinBox;
    @FXML private TextArea motifTextArea;
    @FXML private Button soumettreBtn;
    @FXML private TableView<Permission> permissionTable;
    @FXML private TableColumn<Permission, Integer> colId;
    @FXML private TableColumn<Permission, LocalDate> colDateDemande;
    @FXML private TableColumn<Permission, LocalDate> colDateDebut;
    @FXML private TableColumn<Permission, LocalDate> colDateFin;
    @FXML private TableColumn<Permission, String> colHeureDebut; // Correction: type String pour l'affichage "HH:mm"
    @FXML private TableColumn<Permission, String> colHeureFin;   // Correction: type String pour l'affichage "HH:mm"
    @FXML private TableColumn<Permission, String> colMotif;
    @FXML private TableColumn<Permission, String> colStatut;
    @FXML private Label statusLabel;

    // --- Variables d'état et dépendances ---
    private Permissiondao permissionDAO;
    private ObservableList<Permission> permissionList;
    private String matriculeEmploye;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Définit le matricule de l'employé après le chargement du contrôleur.
     * @param matricule Le matricule de l'employé connecté.
     */
    public void setMatriculeEmploye(String matricule) {
        this.matriculeEmploye = matricule;
        loadPermissions();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        permissionDAO = new Permissiondao();
        permissionList = FXCollections.observableArrayList();
        permissionTable.setItems(permissionList);

        // Peupler les ComboBox d'heures et de minutes
        ObservableList<String> heures = FXCollections.observableArrayList();
        IntStream.rangeClosed(0, 23).forEach(i -> heures.add(String.format("%02d", i)));
        heureDebutBox.setItems(heures);
        heureFinBox.setItems(heures);

        ObservableList<String> minutes = FXCollections.observableArrayList();
        IntStream.rangeClosed(0, 59).forEach(i -> minutes.add(String.format("%02d", i)));
        minuteDebutBox.setItems(minutes);
        minuteFinBox.setItems(minutes);

        // Initialisation des colonnes du tableau
        colId.setCellValueFactory(new PropertyValueFactory<>("idPermission"));
        colDateDemande.setCellValueFactory(new PropertyValueFactory<>("dateDemande"));

        // Liaison des colonnes de date avec un formateur personnalisé
        colDateDebut.setCellValueFactory(new PropertyValueFactory<>("dateDebut"));
        colDateFin.setCellValueFactory(new PropertyValueFactory<>("dateFin"));
        colDateDemande.setCellFactory(this::createDateCell);
        colDateDebut.setCellFactory(this::createDateCell);
        colDateFin.setCellFactory(this::createDateCell);

        // Liaison des colonnes d'heure en combinant heure et minute
        colHeureDebut.setCellValueFactory(cellData -> {
            Permission permission = cellData.getValue();
            String heureMinute = String.format("%02d:%02d", permission.getHeureDebut(), permission.getMinuteDebut());
            return new SimpleStringProperty(heureMinute);
        });

        colHeureFin.setCellValueFactory(cellData -> {
            Permission permission = cellData.getValue();
            String heureMinute = String.format("%02d:%02d", permission.getHeureFin(), permission.getMinuteFin());
            return new SimpleStringProperty(heureMinute);
        });

        colMotif.setCellValueFactory(new PropertyValueFactory<>("motif"));
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));
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
     * Méthode pour soumettre une demande de permission
     */
    @FXML
    private void handleSoumettre(ActionEvent event) {
        try {
            LocalDate dateDebut = dateDebutPicker.getValue();
            LocalDate dateFin = dateFinPicker.getValue();
            String motif = motifTextArea.getText();

            // Validation simple
            if (dateDebut == null || dateFin == null || motif.trim().isEmpty() ||
                    heureDebutBox.getValue() == null || minuteDebutBox.getValue() == null ||
                    heureFinBox.getValue() == null || minuteFinBox.getValue() == null) {
                showError("Validation échouée", "Veuillez remplir tous les champs.");
                return;
            }

            // Récupération et conversion des heures et minutes en int
            int heureDebut = Integer.parseInt(heureDebutBox.getValue());
            int minuteDebut = Integer.parseInt(minuteDebutBox.getValue());
            int heureFin = Integer.parseInt(heureFinBox.getValue());
            int minuteFin = Integer.parseInt(minuteFinBox.getValue());

            // Validation des dates et heures
            if (dateDebut.isAfter(dateFin) || (dateDebut.isEqual(dateFin) && LocalTime.of(heureDebut, minuteDebut).isAfter(LocalTime.of(heureFin, minuteFin)))) {
                showError("Validation échouée", "La date et l'heure de fin doivent être postérieures ou égales à la date et l'heure de début.");
                return;
            }

            // Création de l'objet Permission avec les champs corrigés
            Permission nouvellePermission = new Permission(matriculeEmploye, dateDebut, heureDebut, minuteDebut, dateFin, heureFin, minuteFin, motif, "En attente");
            permissionDAO.ajouterPermission(nouvellePermission);

            showSuccess("Demande de permission envoyée", "Votre demande a été soumise avec succès.");
            clearForm();
            loadPermissions(); // Recharger le tableau
        } catch (SQLException | NumberFormatException e) {
            showError("Erreur de données", "Impossible de soumettre la demande : " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Méthode pour charger toutes les permissions dans la bd
     */
    private void loadPermissions() {
        if (matriculeEmploye == null) {
            return; // Attendre que le matricule soit défini
        }
        try {
            permissionList.clear();
            permissionList.addAll(permissionDAO.getPermissionsByEmployeMatricule(matriculeEmploye));
        } catch (SQLException e) {
            showError("Erreur de base de données", "Impossible de charger les demandes de permission.");
            e.printStackTrace();
        }
    }

    /**
    * Méthode pour réinitialiser le formulaire
     **/
    private void clearForm() {
        dateDebutPicker.setValue(null);
        dateFinPicker.setValue(null);
        heureDebutBox.setValue(null);
        minuteDebutBox.setValue(null);
        heureFinBox.setValue(null);
        minuteFinBox.setValue(null);
        motifTextArea.clear();
    }

    // --- Méthodes utilitaires pour les alertes ---

    private void showSuccess(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(title);
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
}