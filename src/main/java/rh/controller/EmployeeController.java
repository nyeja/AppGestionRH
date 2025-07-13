package rh.controller;

import rh.dao.employedao;
import rh.model.employe;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

import java.net.URL;
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
    @FXML private TableView<employe> tableEmployes;
    @FXML private TableColumn<employe, String> colId;
    @FXML private TableColumn<employe, String> colNom;
    @FXML private TableColumn<employe, String> colPrenom;
    @FXML private TableColumn<employe, String> colEmail;
    @FXML private TableColumn<employe, Integer> colTelephone;
    @FXML private TableColumn<employe, Date> colDateEmbauche;
    @FXML private TableColumn<employe, String> colDepartement;
    @FXML private TableColumn<employe, String> colPoste;
    @FXML private TableColumn<employe, String> colAdresse;

    // Barre de statut
    @FXML private Label lblStatut;
    @FXML private Label lblNombreEmployes;

    // DAO et données
    private employedao employeDAO;
    private ObservableList<employe> employeeList;
    private employe selectedEmployee;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        employeDAO = new employedao();
        employeeList = FXCollections.observableArrayList();

        initializeTableColumns();
        initializeComboBoxes();
        loadEmployees();

        // Désactiver le bouton modifier et supprimer au début
        btnModifier.setDisable(true);
        btnSupprimer.setDisable(true);

        updateStatut("Application initialisée");
    }

    private void initializeTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colTelephone.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        colDateEmbauche.setCellValueFactory(new PropertyValueFactory<>("dateEmbauche"));
//        colDepartement.setCellValueFactory(new PropertyValueFactory<>("departement"));
//        colPoste.setCellValueFactory(new PropertyValueFactory<>("poste"));
        colAdresse.setCellValueFactory(new PropertyValueFactory<>("adresse"));

        // Formater la date dans le tableau
        colDateEmbauche.setCellFactory(column -> new TableCell<employe, Date>() {
            private SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

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

    private void loadEmployees() {
        try {
            List<employe> employees = employeDAO.getAllEmployes();
            employeeList.clear();
            employeeList.addAll(employees);
            updateEmployeeCount();
            updateStatut("Employés chargés avec succès");
        } catch (Exception e) {
            showError("Erreur lors du chargement des employés", e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void ajouterEmploye() {
        if (!validateForm()) {
            return;
        }

        try {
            employe employee = createEmployeeFromForm();
            employeDAO.ajouterEmploye(employee);
            showSuccess("Employé ajouté avec succès avec l'ID : " + employee.getId());
            loadEmployees();
            viderChamps();
        } catch (SQLException e) {
            showError("Erreur lors de l'ajout", e.getMessage());
            e.printStackTrace();
        }
    }

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
            employe employee = createEmployeeFromForm();
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
                employeDAO.supprimerEmploye(selectedEmployee.getId());
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


    @FXML
    private void actualiserListe() {

            loadEmployees(); // tu peux simplement appeler loadEmployees ici


    }


//    @FXML
//    private void rechercherEmploye() {
//        String searchTerm = txtRecherche.getText().trim();
//
//        if (searchTerm.isEmpty()) {
//            loadEmployees();
//            return;
//        }
//
//        try {
//            List<employe> employees = employeDAO.rechercherEmployes(searchTerm);
//            employeeList.clear();
//            employeeList.addAll(employees);
//            updateEmployeeCount();
//            updateStatut("Recherche effectuée: " + employees.size() + " résultat(s)");
//        } catch (Exception e) {
//            showError("Erreur lors de la recherche", e.getMessage());
//        }
//    }

    @FXML
    private void selectionnerEmploye(MouseEvent event) {
        employe employee = tableEmployes.getSelectionModel().getSelectedItem();
        if (employee != null) {
            selectedEmployee = employee;
            fillFormWithEmployee(employee);
            btnModifier.setDisable(false);
            btnSupprimer.setDisable(false);
            updateStatut("Employé sélectionné: " + employee.getPrenom() + " " + employee.getNom());
        }
    }

    private void fillFormWithEmployee(employe employee) {
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

    private employe createEmployeeFromForm() {
        employe employee = new employe();
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

    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    private void updateEmployeeCount() {
        lblNombreEmployes.setText("Total: " + employeeList.size() + " employés");
    }

    private void updateStatut(String message) {
        lblStatut.setText(message);
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
        updateStatut(message);
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
        updateStatut("Erreur: " + title);
    }

    private void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Attention");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
        updateStatut("Attention: " + message);
    }
}
