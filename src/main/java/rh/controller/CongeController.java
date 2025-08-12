package rh.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;

import rh.dao.congedao;
import rh.model.Conge;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

/**
 * Contrôleur FXML pour la gestion des demandes de congé.
 * Cette classe gère l'interface utilisateur pour soumettre, annuler et valider les demandes de congé.
 * Elle interagit avec le DAO pour persister les données en base de données.
 *
 * @author Rajerisoa Ny Eja Manoa
 * @version 1.0
 * @since 2025-08-12
 */
public class CongeController implements Initializable {

    // --- Composants FXML ---
    @FXML private DatePicker DebutDateConge;
    @FXML private DatePicker FinDateConge;
    @FXML private ComboBox<String> TypeConge;
    @FXML private TextField JustificatifField;
    @FXML private Button EnvoyerBtn;
    @FXML private Button AnnulerBtn;
    @FXML private Label lblEmployeMatricule;
    @FXML private Label messageStatut;

    // --- Dépendances et variables d'état ---
    private congedao congeDAO;
    private String matriculeEmployeConnecte;

    /**
     * Initialise le contrôleur après le chargement du fichier FXML.
     * Cette méthode configure les éléments de l'UI et prépare les dépendances.
     *
     * @param url L'emplacement utilisé pour résoudre les chemins relatifs pour l'objet racine.
     * @param resourceBundle Le ResourceBundle qui gère les textes localisés.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        congeDAO = new congedao();

        // Remplir le ComboBox avec les types de congé disponibles.
        ObservableList<String> types = FXCollections.observableArrayList(
                "Annuel", "Maladie", "Maternite", "Exceptionnel"
        );
        TypeConge.setItems(types);

        // Désactiver les boutons de soumission initialement, ils seront activés après la connexion de l'employé.
        EnvoyerBtn.setDisable(true);
        AnnulerBtn.setDisable(true);

        updateStatus("Prêt à soumettre une demande de congé.");
    }

    /**
     * Méthode appelée pour définir le matricule de l'employé connecté.
     * Cette méthode est typiquement invoquée par un autre contrôleur (ex: LoginController).
     *
     * @param matriculeEmploye Le matricule de l'employé connecté.
     */
    public void setEmployeMatricule(String matriculeEmploye) {
        this.matriculeEmployeConnecte = matriculeEmploye;
        System.out.println("Matricule employé reçu dans CongeController : " + this.matriculeEmployeConnecte);

        if (lblEmployeMatricule != null) {
            lblEmployeMatricule.setText("Matricule Employé: " + this.matriculeEmployeConnecte);
        }

        // Activer les boutons une fois que le matricule de l'employé est défini.
        EnvoyerBtn.setDisable(false);
        AnnulerBtn.setDisable(false);
    }

    // --- Gestionnaire d'événements FXML ---

    /**
     * Gère l'événement de clic sur le bouton "Envoyer".
     * Valide le formulaire et, en cas de succès, soumet la demande de congé à la base de données.
     *
     * @param event L'événement d'action.
     */
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
            nouvelleDemande.setStatut("En attente"); // Statut initial

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

    /**
     * Gère l'événement de clic sur le bouton "Annuler".
     * Réinitialise les champs du formulaire.
     *
     * @param event L'événement d'action.
     */
    @FXML
    private void handleAnnulerDemande(ActionEvent event) {
        clearForm();
        updateStatus("Formulaire de demande de congé annulé.");
    }

    // --- Méthodes utilitaires ---

    /**
     * Valide les données saisies dans le formulaire.
     *
     * @return {@code true} si le formulaire est valide, sinon {@code false}.
     */
    private boolean validateForm() {
        StringBuilder errors = new StringBuilder();

        LocalDate debut = DebutDateConge.getValue();
        LocalDate fin = FinDateConge.getValue();

        if (debut == null) {
            errors.append("- La date de début est obligatoire.\n");
        }
        if (fin == null) {
            errors.append("- La date de fin est obligatoire.\n");
        }
        if (debut != null && fin != null && debut.isAfter(fin)) {
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

    /**
     * Efface tous les champs du formulaire.
     */
    private void clearForm() {
        DebutDateConge.setValue(null);
        FinDateConge.setValue(null);
        TypeConge.getSelectionModel().clearSelection();
        JustificatifField.clear();
        updateStatus("Champs vidés.");
    }

    /**
     * Génère un identifiant unique pour une demande de congé.
     * Utilise une partie du timestamp du système pour créer un ID simple.
     *
     * @return Une chaîne de caractères représentant l'identifiant du congé.
     */
    private String generateCongeId() {
        String timestampPart = String.valueOf(System.currentTimeMillis());
        timestampPart = timestampPart.substring(Math.max(0, timestampPart.length() - 5));
        return "CGE-" + timestampPart;
    }

    /**
     * Met à jour le message de statut affiché à l'utilisateur.
     *
     * @param message Le message à afficher.
     */
    private void updateStatus(String message) {
        if (messageStatut != null) {
            messageStatut.setText(message);
        }
    }

    /**
     * Affiche une boîte de dialogue de type Succès.
     *
     * @param message Le message à afficher dans la boîte de dialogue.
     */
    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
        updateStatus(message);
    }

    /**
     * Affiche une boîte de dialogue de type Erreur.
     *
     * @param title Le titre de la boîte de dialogue.
     * @param message Le message à afficher.
     */
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
        updateStatus("Erreur: " + title);
    }

    /**
     * Affiche une boîte de dialogue de type Avertissement.
     *
     * @param title Le titre de la boîte de dialogue.
     * @param message Le message à afficher.
     */
    private void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Attention");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
        updateStatus("Attention: " + title);
    }
}