package rh.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;

import javafx.scene.control.cell.PropertyValueFactory;
import rh.dao.Employedao;
import rh.dao.Congedao;
import rh.model.Conge;
import rh.model.EmployeModel;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.time.temporal.ChronoUnit;
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
//    @FXML private TextField JustificatifField;
    @FXML private Button EnvoyerBtn;
    @FXML private Button AnnulerBtn;
    @FXML private Label lblEmployeMatricule;
    @FXML private Label messageStatut;
    @FXML private TableView<Conge> congeHistoryTable; // Nouveau tableau
    @FXML private TableColumn<Conge, String> colIdConge;
    @FXML private TextArea JustificatifField;
    @FXML private Label lblSoldeConge; // Nouveau Label pour le solde
    @FXML private TableColumn<Conge, LocalDate> colDateDebut;
    @FXML private TableColumn<Conge, LocalDate> colDateFin;
    @FXML private TableColumn<Conge, String> colTypeConge;
    @FXML private TableColumn<Conge, String> colJustificatif;
    @FXML private TableColumn<Conge, String> colStatut;

    // --- Dépendances et variables d'état ---
    private Congedao congeDAO;
    private Employedao employeDAO;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private ObservableList<Conge> congeHistoryList;
    private String matriculeEmployeConnecte;

    /**
     * Initialise le contrôleur après le chargement du fichier FXML.
     * Cette méthode configure les éléments de l'UI et prépare les dépendances.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        congeDAO = new Congedao();
        employeDAO = new Employedao(); // Initialisez le DAO

        // Configuration des colonnes du tableau
        colIdConge.setCellValueFactory(new PropertyValueFactory<>("idConge"));
        colDateDebut.setCellValueFactory(new PropertyValueFactory<>("dateDebut"));
        colDateDebut.setCellFactory(this::createDateCell);
        colDateFin.setCellValueFactory(new PropertyValueFactory<>("dateFin"));
        colDateFin.setCellFactory(this::createDateCell);
        colTypeConge.setCellValueFactory(new PropertyValueFactory<>("typeConge"));
        colJustificatif.setCellValueFactory(new PropertyValueFactory<>("justificatif"));
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));

        congeHistoryList = FXCollections.observableArrayList();
        congeHistoryTable.setItems(congeHistoryList);

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
     * Crée une cellule de table pour formatter l'affichage d'une date.
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


    /**
     * Méthode appelée pour définir le matricule de l'employé connecté.
     * Cette méthode est typiquement invoquée par un autre contrôleur (ex: LoginController).
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

        loadEmployeData();
    }


    /**
    * Méthode pour charger les données de l'employé connecté
    * */
    private void loadEmployeData() {
        try {
            EmployeModel employe = employeDAO.getEmployeByMatricule(this.matriculeEmployeConnecte);
            if (employe != null && lblSoldeConge != null) {
                lblSoldeConge.setText(String.valueOf(employe.getSoldeConge()));
            } else if (lblSoldeConge != null) {
                lblSoldeConge.setText("Non disponible");
            }

            List<Conge> historique = congeDAO.getCongeByEmployeMatricule(this.matriculeEmployeConnecte);
            congeHistoryList.setAll(historique);

        } catch (SQLException e) {
            showError("Erreur de chargement", "Impossible de charger les données de l'employé: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // --- Gestionnaire d'événements FXML ---

    /**
     * Gère l'événement de clic sur le bouton "Envoyer".
     * Valide le formulaire et, en cas de succès, soumet la demande de congé à la base de données.
     * Si le solde n'arrive pas encore à 0, on peut envoyer la demande
     */
    @FXML
    private void handleEnvoyerDemande(ActionEvent event) {
        if (!validateForm()) {
            return;
        }

        try {
            long joursDemandes = calculateNombreJours(DebutDateConge.getValue(), FinDateConge.getValue());
            EmployeModel employe = employeDAO.getEmployeByMatricule(this.matriculeEmployeConnecte);

            if (employe == null) {
                showError("Erreur", "Employé non trouvé.");
                return;
            }

            // Vérifier le solde de congé avant de soumettre
            if (employe.getSoldeConge() < joursDemandes) {
                showWarning("Solde insuffisant", "Le solde de congé restant (" + employe.getSoldeConge() + " jours) est insuffisant pour cette demande (" + joursDemandes + " jours).");
                return;
            }

            Conge nouvelleDemande = new Conge();
            nouvelleDemande.setIdConge(generateCongeId());
            nouvelleDemande.setMatriculeEmploye(this.matriculeEmployeConnecte);
            nouvelleDemande.setDateDebut(DebutDateConge.getValue());
            nouvelleDemande.setDateFin(FinDateConge.getValue());
            nouvelleDemande.setTypeConge(TypeConge.getValue());
            nouvelleDemande.setJustificatif(JustificatifField.getText().trim());
            nouvelleDemande.setStatut("En attente"); // Statut initial
            nouvelleDemande.setNbJours((int) joursDemandes);

            congeDAO.ajouterConge(nouvelleDemande);
            int nouveauSolde = employe.getSoldeConge() - (int) joursDemandes;
            employeDAO.updateSoldeConge(this.matriculeEmployeConnecte, nouveauSolde);

            // Recharger les données pour mettre à jour l'UI avec le nouveau solde

            loadEmployeData();
            showSuccess("Demande de congé soumise avec succès pour " + joursDemandes + " jours !");
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
     */
    @FXML
    private void handleAnnulerDemande(ActionEvent event) {
        clearForm();
        updateStatus("Formulaire de demande de congé annulé.");
    }


    /**
     * Valide les données saisies dans le formulaire.
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
     */
    private String generateCongeId() {
        String timestampPart = String.valueOf(System.currentTimeMillis());
        timestampPart = timestampPart.substring(Math.max(0, timestampPart.length() - 5));
        return "CGE-" + timestampPart;
    }

    /**
     * Met à jour le message de statut affiché à l'utilisateur.
     */
    private void updateStatus(String message) {
        if (messageStatut != null) {
            messageStatut.setText(message);
        }
    }

    /**
     * Calcule le nombre de jours entre deux dates, en incluant la date de fin.
     * @param debut La date de début du congé.
     * @param fin La date de fin du congé.
     * @return Le nombre de jours de congé demandés.
     */
    private long calculateNombreJours(LocalDate debut, LocalDate fin) {
        if (debut == null || fin == null || debut.isAfter(fin)) {
            return 0;
        }
        return ChronoUnit.DAYS.between(debut, fin) + 1;
    }

    /**
     * Affiche une boîte de dialogue de type Succès.
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