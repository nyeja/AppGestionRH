package rh.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;

import rh.dao.congedao;
import rh.model.Conge;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class CongeController implements Initializable {
    @FXML private DatePicker DebutDateConge;
    @FXML private DatePicker FinDateConge;
    @FXML private ComboBox<String> TypeConge;
    @FXML private TextField JustificatifField;
    @FXML private Button EnvoyerBtn;
    @FXML private Button AnnulerBtn;
    @FXML private Label lblEmployeMatricule;
    @FXML private Label messageStatut;

    private congedao congeDAO;
    private String matriculeEmployeConnecte; //Variable pour stocker le matricule de l'employé

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        congeDAO = new congedao();


        ObservableList<String> types = FXCollections.observableArrayList(
                "Annuel", "Maladie", "Maternite", "Exceptionnel"
        );
        TypeConge.setItems(types);


        EnvoyerBtn.setDisable(true);
        AnnulerBtn.setDisable(true);

        updateStatus("Prêt à soumettre une demande de congé.");
    }

    /**
     * Method called by LoginController to pass the connected employee's matricule.
     * @param matriculeEmploye The matricule of the employee who just logged in.
     */
    public void setEmployeMatricule(String matriculeEmploye) {
        this.matriculeEmployeConnecte = matriculeEmploye;
        System.out.println("Matricule employé reçu dans CongeController : " + this.matriculeEmployeConnecte);

        if (lblEmployeMatricule != null) {
            lblEmployeMatricule.setText("Matricule Employé: " + this.matriculeEmployeConnecte);
        }

        // Enable buttons once the employee is identified
        EnvoyerBtn.setDisable(false);
        AnnulerBtn.setDisable(false);
    }

    @FXML
    private void handleEnvoyerDemande(ActionEvent event) {
        if (!validateForm()) {
            return;
        }

        try {
            Conge nouvelleDemande = new Conge();

            nouvelleDemande.setIdConge(generateCongeId());
            nouvelleDemande.setMatriculeEmploye(this.matriculeEmployeConnecte);
            nouvelleDemande.setDateDebut(DebutDateConge.getValue());
            nouvelleDemande.setDateFin(FinDateConge.getValue());
            nouvelleDemande.setTypeConge(TypeConge.getValue());
            nouvelleDemande.setJustificatif(JustificatifField.getText().trim());
            nouvelleDemande.setStatut("En attente"); // Initial status

            congeDAO.ajouterConge(nouvelleDemande);
            showSuccess("Demande de congé soumise avec succès ! ID: " + nouvelleDemande.getIdConge());
            clearForm();
        } catch (SQLException e) {
            showError("Erreur de base de données", "Impossible de soumettre la demande de congé: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            showError("Erreur", "Une erreur inattendue est survenue: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAnnulerDemande(ActionEvent event) {
        clearForm();
        updateStatus("Formulaire de demande de congé annulé.");
    }

    private boolean validateForm() {
        StringBuilder errors = new StringBuilder();

        if (DebutDateConge.getValue() == null) {
            errors.append("- La date de début est obligatoire.\n");
        }
        if (FinDateConge.getValue() == null) {
            errors.append("- La date de fin est obligatoire.\n");
        }
        if (DebutDateConge.getValue() != null && FinDateConge.getValue() != null &&
                DebutDateConge.getValue().isAfter(FinDateConge.getValue())) {
            errors.append("- La date de début ne peut pas être après la date de fin.\n");
        }
        if (TypeConge.getValue() == null || TypeConge.getValue().isEmpty()) {
            errors.append("- Le type de congé est obligatoire.\n");
        }


        if (errors.length() > 0) {
            showWarning("Validation échouée", errors.toString());
            return false;
        }
        return true;
    }

    private void clearForm() {
        DebutDateConge.setValue(null);
        FinDateConge.setValue(null);
        TypeConge.getSelectionModel().clearSelection();
        JustificatifField.clear();
        updateStatus("Champs vidés.");
    }


    private String generateCongeId() {
        // Utilise une partie du timestamp et un préfixe plus court
        // Par exemple, les 5 derniers chiffres du timestamp + un préfixe de 4 caractères
        String timestampPart = String.valueOf(System.currentTimeMillis());
        // Prend les 5 derniers chiffres pour garder l'ID court
        timestampPart = timestampPart.substring(Math.max(0, timestampPart.length() - 5));

        // Préfixe de 4 caractères max, par exemple "CGE-"
        return "CGE-" + timestampPart; // Ex: "CGE-12345" (longueur 9)
    }


    private void updateStatus(String message) {
        if (messageStatut != null) { // Add null check for messageStatut in initialize too
            messageStatut.setText(message);
        }
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
        updateStatus(message);
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
        updateStatus("Erreur: " + title);
    }

    private void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Attention");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
        updateStatus("Attention: " + title);
    }
}