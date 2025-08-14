package rh.controller;

import rh.dao.Employedao;
import rh.model.EmployeModel;
import rh.dao.Utilisateurdao;
import rh.dao.congedao;
import rh.utils.ConnexionDB;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import rh.model.UtilisateurModel;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class EmployeeController implements Initializable {

    // Champs du formulaire
    @FXML private TextField txtEmployeeId;
    @FXML private TextField txtNom;
    @FXML private TextField txtPrenom;
    @FXML private TextField txtEmail;
    @FXML private TextField txtTelephone;
    @FXML private DatePicker dateEmbauche;
    @FXML private TextField txtAdresse;
    @FXML private ComboBox<String> comboDepartement;
    @FXML private ComboBox<String> comboPoste;

    // Boutons
    @FXML private Button btnAjouter;
    @FXML private Button btnModifier;
    @FXML private Button btnSupprimer;
    @FXML private Button btnVider;
    @FXML private Button btnActualiser;
    @FXML private Button btnRechercher;

    // Recherche
    @FXML private TextField txtRecherche;

    // Tableau
    @FXML private TableView<EmployeModel> tableEmployes;
    @FXML private TableColumn<EmployeModel, String> colId;
    @FXML private TableColumn<EmployeModel, String> colNom;
    @FXML private TableColumn<EmployeModel, String> colPrenom;
    @FXML private TableColumn<EmployeModel, String> colEmail;
    @FXML private TableColumn<EmployeModel, Integer> colTelephone;
    @FXML private TableColumn<EmployeModel, Date> colDateEmbauche;
    @FXML private TableColumn<EmployeModel, String> colDepartement;
    @FXML private TableColumn<EmployeModel, String> colPoste;
    @FXML private TableColumn<EmployeModel, String> colAdresse;

    // Barre de statut
    @FXML private Label lblStatut;
    @FXML private Label lblNombreEmployes;

    // DAO et données
    private Utilisateurdao utilisateurDAO;
    private Employedao employeDAO;
    private congedao congeDAO;

    private ObservableList<EmployeModel> employeeList;
    private EmployeModel selectedEmployee;

    /**
     * Initialise le contrôleur. Cette méthode est appelée automatiquement
     * après le chargement du fichier FXML.
     * Elle initialise les DAO, les colonnes du tableau, les ComboBox,
     * charge les employés existants et met à jour le statut.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        employeDAO = new Employedao();
        utilisateurDAO  = new Utilisateurdao();
        congeDAO = new congedao();

        employeeList = FXCollections.observableArrayList();

        initializeTableColumns();
        initializeComboBoxes();
        loadEmployees();

        // Désactiver le bouton modifier et supprimer au début
        btnModifier.setDisable(true);
        btnSupprimer.setDisable(true);

        updateStatut("Application initialisée");
    }

    /**
     * Initialise les colonnes du TableView pour qu'elles affichent les
     * données de l'objet EmployeModel.
     * Chaque colonne est liée à une propriété de la classe EmployeModel
     * via un PropertyValueFactory.
     */
    private void initializeTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colTelephone.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        colDateEmbauche.setCellValueFactory(new PropertyValueFactory<>("dateEmbauche"));
        colDepartement.setCellValueFactory(new PropertyValueFactory<>("departement"));
        colPoste.setCellValueFactory(new PropertyValueFactory<>("poste"));
        colAdresse.setCellValueFactory(new PropertyValueFactory<>("adresse"));

        // Formater la date dans le tableau
        colDateEmbauche.setCellFactory(column -> new TableCell<EmployeModel, Date>() {
            private final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

            @Override
            protected void updateItem(Date item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(format.format(item));
                }
            }
        });

        tableEmployes.setItems(employeeList);
    }

    /**
     * Initialise les ComboBox pour les départements et les postes
     * avec des listes de valeurs prédéfinies.
     * Permet également la saisie libre par l'utilisateur.
     */
    private void initializeComboBoxes() {
        // Départements prédéfinis - vous pouvez les modifier selon vos besoins
        ObservableList<String> departements = FXCollections.observableArrayList(
                "Ressources Humaines",
                "Informatique",
                "Comptabilité",
                "Marketing",
                "Ventes",
                "Production",
                "Logistique",
                "Direction"
        );
        comboDepartement.setItems(departements);
        comboDepartement.setEditable(true); // Permet la saisie libre

        // Postes prédéfinis - vous pouvez les modifier selon vos besoins
        ObservableList<String> postes = FXCollections.observableArrayList(
                "Directeur",
                "Manager",
                "Chef d'équipe",
                "Employé",
                "Stagiaire",
                "Consultant",
                "Technicien",
                "Analyste",
                "Développeur",
                "Comptable",
                "Commercial",
                "Assistant"
        );
        comboPoste.setItems(postes);
        comboPoste.setEditable(true); // Permet la saisie libre
    }

    /**
     * Charge la liste des employés depuis la base de données et
     * l'affiche dans le TableView.
     * Met à jour le compteur d'employés et le statut.
     */
    private void loadEmployees() {
        try {
            List<EmployeModel> employees = employeDAO.getAllEmployes();
            employeeList.clear();
            employeeList.addAll(employees);
            updateEmployeeCount();
            updateStatut("Employés chargés avec succès");
        } catch (Exception e) {
            showError("Erreur lors du chargement des employés", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Gère l'ajout d'un nouvel employé.
     * Valide les champs du formulaire, crée un objet EmployeModel,
     * l'insère dans la base de données, et crée automatiquement
     * un utilisateur associé (comme discuté).
     */
    @FXML
    private void ajouterEmploye() {
        if (!validateForm()) {
            return;
        }

        Connection conn = null;
        try {
            // Création de l'objet EmployeModel à partir des champs du formulaire
            EmployeModel employee = createEmployeeFromForm();
            // L'ajout de l'employé dans la DAO gère également la création de l'utilisateur.
            employeDAO.ajouterEmploye(employee);

            showSuccess("Employé et utilisateur créés avec succès pour l'ID : " + employee.getId());
            loadEmployees();
            viderChamps();
        } catch (SQLException e) {
            // En cas d'erreur, annuler la transaction
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            showError("Erreur lors de l'ajout", e.getMessage());
            e.printStackTrace();
        } finally {
            // Réactiver l'auto-commit et fermer la connexion
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Gère la modification d'un employé existant.
     * S'exécute uniquement si un employé est sélectionné.
     * Valide les champs, met à jour l'objet EmployeModel,
     * et envoie la modification à la base de données.
     */
    @FXML
    private void modifierEmploye() {
        if (selectedEmployee == null) {
            showWarning("Aucun employé sélectionné");
            return;
        }

        if (!validateForm()) {
            return;
        }

        try {
            EmployeModel employee = createEmployeeFromForm();
            employee.setId(selectedEmployee.getId());
            employeDAO.modifierEmploye(employee);
            showSuccess("Employé modifié avec succès");
            loadEmployees();
            viderChamps();
            selectedEmployee = null;
            btnModifier.setDisable(true);
            btnSupprimer.setDisable(true);
        } catch (SQLException e) {
            showError("Erreur lors de la modification", e.getMessage());
            e.printStackTrace();
        }
        loadEmployees();
    }

    /**
     * Gère la suppression d'un employé sélectionné.
     * Demande une confirmation à l'utilisateur.
     * En cas de confirmation, supprime d'abord les congés associés,
     * puis l'utilisateur, et enfin l'employé.
     */
    @FXML
    private void supprimerEmploye() {
        if (selectedEmployee == null) {
            showWarning("Aucun employé sélectionné");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer l'employé");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer " +
                selectedEmployee.getPrenom() + " " + selectedEmployee.getNom() + " ?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // Obtenez l'ID de l'employé sélectionné
                String employeId = selectedEmployee.getId();

                // Supprimer tous les congés de cet employé
                congeDAO.supprimerCongesParEmploye(employeId);

                // Supprimer l'utilisateur lié à cet employé
                utilisateurDAO.supprimerUtilisateur(employeId);


                // Enfin, supprimer l'employé de la table principale
                employeDAO.supprimerEmploye(employeId);


                showSuccess("Employé supprimé avec succès");
                loadEmployees();
                viderChamps();
                selectedEmployee = null;
                btnModifier.setDisable(true);
                btnSupprimer.setDisable(true);
            } catch (SQLException e) {
                showError("Erreur lors de la suppression", e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Réinitialise tous les champs du formulaire à leur état initial
     * (vides ou null).
     * Désactive les boutons de modification et de suppression.
     */
    @FXML
    private void viderChamps() {
        txtEmployeeId.clear();
        txtNom.clear();
        txtPrenom.clear();
        txtEmail.clear();
        txtTelephone.clear();
        dateEmbauche.setValue(null);
        txtAdresse.clear();
        comboDepartement.getSelectionModel().clearSelection();
        comboDepartement.getEditor().clear();
        comboPoste.getSelectionModel().clearSelection();
        comboPoste.getEditor().clear();

        selectedEmployee = null;
        btnModifier.setDisable(true);
        btnSupprimer.setDisable(true);
        tableEmployes.getSelectionModel().clearSelection();

        updateStatut("Champs vidés");
    }

    /**
     * Recharge la liste des employés.
     * Cette méthode est liée au bouton "Actualiser".
     */
    @FXML
    private void actualiserListe() {
        loadEmployees(); // tu peux simplement appeler loadEmployees ici
    }

    /**
     * Gère la recherche d'employés.
     * Filtre la liste des employés affichés dans le TableView
     * en fonction du terme de recherche saisi.
     * Si le champ de recherche est vide, recharge tous les employés.
     */
    @FXML
    private void rechercherEmploye() {
        String searchTerm = txtRecherche.getText().trim();

        if (searchTerm.isEmpty()) {
            loadEmployees();
            return;
        }

        try {
            List<EmployeModel> employees = employeDAO.rechercherEmployes(searchTerm);
            employeeList.clear();
            employeeList.addAll(employees);
            updateEmployeeCount();
            updateStatut("Recherche effectuée: " + employees.size() + " résultat(s)");
        } catch (Exception e) {
            showError("Erreur lors de la recherche", e.getMessage());
        }
    }

    /**
     * Gère la sélection d'un employé dans le TableView.
     * Remplit le formulaire avec les données de l'employé sélectionné
     * et active les boutons de modification et de suppression.
     *
     * @param event L'événement de la souris
     */
    @FXML
    private void selectionnerEmploye(MouseEvent event) {
        EmployeModel employee = tableEmployes.getSelectionModel().getSelectedItem();
        if (employee != null) {
            selectedEmployee = employee;
            fillFormWithEmployee(employee);
            btnModifier.setDisable(false);
            btnSupprimer.setDisable(false);
            updateStatut("Employé sélectionné: " + employee.getPrenom() + " " + employee.getNom());
        }
    }

    /**
     * Remplit les champs du formulaire avec les données de l'employé
     * passé en paramètre.
     *
     * @param employee L'employé dont les données doivent être affichées
     */
    private void fillFormWithEmployee(EmployeModel employee) {
        txtEmployeeId.setText(employee.getId());
        txtNom.setText(employee.getNom());
        txtPrenom.setText(employee.getPrenom());
        txtEmail.setText(employee.getEmail());
        txtTelephone.setText(String.valueOf(employee.getTelephone()));

        // Convertir Date vers LocalDate pour le DatePicker
        if (employee.getDateEmbauche() != null) {
            dateEmbauche.setValue(new java.sql.Date(employee.getDateEmbauche().getTime()).toLocalDate());
        }

        txtAdresse.setText(employee.getAdresse());

        // Sélectionner ou saisir le département et le poste
        comboDepartement.setValue(employee.getDepartement());
        comboPoste.setValue(employee.getPoste());
    }

    /**
     * Crée un objet EmployeModel à partir des données saisies dans le formulaire.
     *
     * @return Un objet EmployeModel rempli avec les données du formulaire
     */
    private EmployeModel createEmployeeFromForm() {
        EmployeModel employee = new EmployeModel();
        employee.setNom(txtNom.getText().trim());
        employee.setPrenom(txtPrenom.getText().trim());
        employee.setEmail(txtEmail.getText().trim());
        employee.setTelephone(Integer.parseInt(txtTelephone.getText().trim()));

        // Convertir LocalDate vers Date
        if (dateEmbauche.getValue() != null) {
            employee.setDateEmbauche(java.sql.Date.valueOf(dateEmbauche.getValue()));
        }

        employee.setAdresse(txtAdresse.getText().trim());

        // Récupérer les valeurs des ComboBox (sélectionnées ou saisies)
        String departement = comboDepartement.getValue();
        if (departement == null || departement.trim().isEmpty()) {
            departement = comboDepartement.getEditor().getText().trim();
        }
        employee.setDepartement(departement);

        String poste = comboPoste.getValue();
        if (poste == null || poste.trim().isEmpty()) {
            poste = comboPoste.getEditor().getText().trim();
        }
        employee.setPoste(poste);

        return employee;
    }

    /**
     * Valide les champs du formulaire et affiche des messages d'erreur
     * si la validation échoue.
     *
     * @return true si le formulaire est valide, false sinon
     */
    private boolean validateForm() {
        StringBuilder errors = new StringBuilder();

        if (txtNom.getText().trim().isEmpty()) {
            errors.append("- Le nom est obligatoire\n");
        }

        if (txtPrenom.getText().trim().isEmpty()) {
            errors.append("- Le prénom est obligatoire\n");
        }

        if (txtEmail.getText().trim().isEmpty()) {
            errors.append("- L'email est obligatoire\n");
        } else if (!isValidEmail(txtEmail.getText().trim())) {
            errors.append("- L'email n'est pas valide\n");
        }

        if (txtTelephone.getText().trim().isEmpty()) {
            errors.append("- Le téléphone est obligatoire\n");
        } else {
            try {
                Integer.parseInt(txtTelephone.getText().trim());
            } catch (NumberFormatException e) {
                errors.append("- Le téléphone doit être un nombre valide\n");
            }
        }

        if (dateEmbauche.getValue() == null) {
            errors.append("- La date d'embauche est obligatoire\n");
        }

        // Vérifier le département (sélectionné ou saisi)
        String departement = comboDepartement.getValue();
        if (departement == null || departement.trim().isEmpty()) {
            departement = comboDepartement.getEditor().getText().trim();
        }
        if (departement.isEmpty()) {
            errors.append("- Le département est obligatoire\n");
        }

        // Vérifier le poste (sélectionné ou saisi)
        String poste = comboPoste.getValue();
        if (poste == null || poste.trim().isEmpty()) {
            poste = comboPoste.getEditor().getText().trim();
        }
        if (poste.isEmpty()) {
            errors.append("- Le poste est obligatoire\n");
        }

        if (errors.length() > 0) {
            showError("Erreurs de validation", errors.toString());
            return false;
        }

        return true;
    }

    /**
     * Vérifie la validité d'une adresse email à l'aide d'une expression régulière.
     *
     * @param email L'adresse email à valider
     * @return true si l'email est valide, false sinon
     */
    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    /**
     * Met à jour l'étiquette affichant le nombre total d'employés.
     */
    private void updateEmployeeCount() {
        lblNombreEmployes.setText("Total: " + employeeList.size() + " employés");
    }

    /**
     * Met à jour le label de la barre de statut avec un message donné.
     *
     * @param message Le message à afficher
     */
    private void updateStatut(String message) {
        lblStatut.setText(message);
    }

    /**
     * Affiche une boîte de dialogue d'information de succès.
     *
     * @param message Le message de succès
     */
    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
        updateStatut(message);
    }

    /**
     * Affiche une boîte de dialogue d'erreur.
     *
     * @param title Le titre de l'erreur
     * @param message Le message d'erreur
     */
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
        updateStatut("Erreur: " + title);
    }

    /**
     * Affiche une boîte de dialogue d'avertissement.
     *
     * @param message Le message d'avertissement
     */
    private void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Attention");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
        updateStatut("Attention: " + message);
    }
}