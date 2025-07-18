package rh.controller;

import javafx.event.ActionEvent;
import rh.dao.employedao;
import rh.model.departement.tableauDepartement;
import rh.model.employe.employe;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import rh.utils.ConnexionDB;

import javax.swing.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
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
    @FXML private TextField tfRecherche;

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

        // recherche automatique
        tfRecherche.textProperty().addListener((observable, oldValue, newValue) -> {
            rechercherEmploye(newValue);
        });

        // Désactiver le bouton modifier et supprimer au début

        updateStatut("Application initialisée");
    }

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
        // Ajoute des options à la ChoiceBox de la departemenr
        String sql_dep = "SELECT nom FROM departement";
        Connection conn = ConnexionDB.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql_dep); ResultSet rs = stmt.executeQuery()) {
            comboDepartement.getItems().clear();
            while (rs.next()) {
                String nom = rs.getString("nom"); // Récupère le nom depuis la colonne
                comboDepartement.getItems().add(nom); // Ajoute le nom dans le ComboBox

            }

        } catch (Exception e) {
            System.out.println("Erreur : " + e.getMessage());
        }
        comboDepartement.setEditable(true); // Permet la saisie libre
        // Ajoute des options à la ChoiceBox
        String sql = "SELECT ID_POSTE FROM poste";
        try (PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            comboPoste.getItems().clear();
            while (rs.next()) {
                String poste = rs.getString("ID_POSTE"); // Récupère le nom depuis la colonne
                comboPoste.getItems().add(poste); // Ajoute le nom dans le ComboBox

            }

        } catch (Exception e) {
            System.out.println("Erreur : " + e.getMessage());
        }
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
/*
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
        } catch (SQLException e) {
            showError("Erreur lors de la modification", e.getMessage());
            e.printStackTrace();
        }
        loadEmployees();
    }
*/
@FXML
private void preparModificationDepartement(){
    // Selectionner les données dans le tableau
    employe selectedEmploye = tableEmployes.getSelectionModel().getSelectedItem();
    if (selectedEmploye != null){
        // System.out.println("Voici l'élément selectionner "+ "Id : " + selectedDepartement.getId()+ "\nnom : " + selectedDepartement.getNom());
        // recupération des données dans le tableview
        String id = selectedEmploye.getId();
        String nom = selectedEmploye.getNom();
        String prenom = selectedEmploye.getPrenom();
        String adresse = selectedEmploye.getAdresse();
        String phone = String.valueOf(selectedEmploye.getTelephone());
        String mail = selectedEmploye.getEmail();
        String date = String.valueOf(selectedEmploye.getDateEmbauche());
        String poste = selectedEmploye.getPoste();
        String departement = selectedEmploye.getDepartement();
        // completer les formulaires avec les données récuperer
        txtEmployeeId.setText(id);
        txtNom.setText(nom);
        txtPrenom.setText(prenom);
        txtEmail.setText(mail);
        txtTelephone.setText(phone);
        dateEmbauche.setValue(LocalDate.parse(date));
        comboDepartement.setValue(departement);
        comboPoste.setValue(poste);
        txtAdresse.setText(adresse);
    }else{
        JOptionPane.showConfirmDialog(
                null,
                "Veuillez selectionez l'un des éléments dans le tableau",
                "OK",
                JOptionPane.CLOSED_OPTION
        );
    }
}
@FXML
public void modifierEmploye() throws SQLException {
    // recuperation des valeurs ajoutées dans les champs de texte
    String nom = txtNom.getText();
    String prenom = txtPrenom.getText();
    String adresse = txtAdresse.getText();
    String phone = txtTelephone.getText();
    String mail = txtEmail.getText();
    LocalDate date = dateEmbauche.getValue();
    String poste = comboPoste.getValue();
    String departement = comboDepartement.getValue();
    Connection conn = ConnexionDB.getConnection();
    // Commande sql pour la modification des données dans une base de donnée
    String sql_modification = "UPDATE employe SET Nom = ?, PRENOMS = ? , ADRESSE = ?,TELEPHONE = ? , EMAIL = ?,DATE_EMBAUCHE = ?,ID_POSTE = ? , DEPARTEMENT = ? WHERE ID = ?";
    try (PreparedStatement stmt = conn.prepareStatement(sql_modification)){
        employe selectedemploye = tableEmployes.getSelectionModel().getSelectedItem();
        String idEmploye = selectedemploye.getId();
        //préparation de la modification
        stmt.setString(1, nom);
        stmt.setString(2, prenom);
        stmt.setString(3, adresse);
        stmt.setString(4, phone);
        stmt.setString(5, mail);
        stmt.setDate(6, java.sql.Date.valueOf(date));
        stmt.setString(7, poste);
        stmt.setString(8, departement);
        stmt.setString(9,idEmploye);
        // execution de la modification
        int ligneModifier = stmt.executeUpdate();
        if (ligneModifier > 0){
            tableEmployes.setItems(employeDAO.getAllEmployes());
            txtNom.clear();
            txtPrenom.clear();
            txtAdresse.clear();
            txtTelephone.clear();
            comboDepartement.setValue("");
            comboPoste.setValue("");
            txtEmail.clear();
            dateEmbauche.setValue(LocalDate.parse(""));
            System.out.println("Modification du département " + idEmploye + " réussie.");
        }else {
            System.out.println("Erreur lors de la modification");
        }
    }catch (Exception e){
        System.out.println("Voici l'erreur " + e);
    }
}
/*
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
*/
@FXML
public void supprimerEmploye(ActionEvent actionEvent) {
    // ajout d'un ecouteur ou listener (sert à réagir automatiquement à un évènement ou à un changement dans un intèrface utilisateur
    Connection conn = ConnexionDB.getConnection();
    employe selectedemploye = tableEmployes.getSelectionModel().getSelectedItem();
    if (selectedemploye != null){
        // Boîte de dialogue de confirmation
        int confirmer = JOptionPane.showConfirmDialog(
                null,
                "Voulez-vous vraiment supprimer ce département ?",
                "Confirmation",
                JOptionPane.YES_NO_OPTION);

        if (confirmer == JOptionPane.YES_OPTION) {

            // Requête SQL pour supprimer un département par son ID
            String sql_delete = "DELETE FROM employe WHERE ID = ?";
            // Bloc try-with-resources pour gérer automatiquement la fermeture du PreparedStatement
            try (PreparedStatement stmt = conn.prepareStatement(sql_delete)) {


                String id_emp = selectedemploye.getId();
                //System.out.println("voici l'id du département selectionner " + id_dpm);
                // Remplacement du "?" dans la requête SQL par l'ID sélectionné
                stmt.setString(1, id_emp);

                // Exécution de la suppression
                int ligneSupprimee = stmt.executeUpdate();

                if (ligneSupprimee > 0) {
                    tableEmployes.setItems(employeDAO.getAllEmployes());
                    // Message de succès
                    System.out.println("Suppression du département " + id_emp + " réussie.");
                } else {
                    // Aucun département supprimé (ID inexistant ?)
                    System.out.println("Aucune suppression effectuée. ID introuvable ?");
                }


            } catch (Exception e) {
                // Affichage de l'erreur en cas de problème SQL
                System.out.println("Erreur : " + e.getMessage());
                System.out.println("Voici l'erreur : " + e.getMessage());
            }
        }else {
            System.out.println("Id Employer introuvable");
        }
    }else{
        int conf = JOptionPane.showConfirmDialog(
                null,
                "Veuillez selectionnez un élément dans le tableau",
                "avertissement",
                JOptionPane.CLOSED_OPTION);
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
        tableEmployes.getSelectionModel().clearSelection();

        updateStatut("Champs vidés");
    }


    @FXML
    private void actualiserListe() {

            loadEmployees(); // tu peux simplement appeler loadEmployees ici


    }

/*
    @FXML
    private void rechercherEmploye() {
        String searchTerm = txtRecherche.getText().trim();

        if (searchTerm.isEmpty()) {
            loadEmployees();
            return;
        }

        try {
            List<employe> employees = employeDAO.rechercherEmployes(searchTerm);
          employeeList.clear();
            employeeList.addAll(employees);
            updateEmployeeCount();
            updateStatut("Recherche effectuée: " + employees.size() + " résultat(s)");
        } catch (Exception e) {
            showError("Erreur lors de la recherche", e.getMessage());
        }
    }
/*
    @FXML
    private void selectionnerEmploye(MouseEvent event) {
        employe employee = tableEmployes.getSelectionModel().getSelectedItem();
        if (employee != null) {
            selectedEmployee = employee;
            fillFormWithEmployee(employee);
            updateStatut("Employé sélectionné: " + employee.getPrenom() + " " + employee.getNom());
        }
    }
*//*
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
*/
    private void rechercherEmploye(String motCle) {
        // stocké les listes pour être bien syncronisé
        Connection conn = ConnexionDB.getConnection();
        ObservableList<employe> listeFiltrée = FXCollections.observableArrayList();
        String sql = "SELECT * FROM employe WHERE nom LIKE ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + motCle + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                // Stocké les données récuperer dans des veriables

                String id = rs.getString("id");
                String nom = rs.getString("nom");
                String prenoms  = rs.getString("prenoms");
                int telephone = rs.getInt("telephone");
                String email = rs.getString("email");
                String addresse = rs.getString("adresse");
                java.sql.Date date = rs.getDate("date_embauche"); // type java.sql.Date
                String departement = rs.getString("departement");
                String poste = rs.getString("ID_POSTE");

                employe emp = new employe(id , nom , prenoms , telephone , email , addresse , date , departement, poste );
                listeFiltrée.add(emp);
            }
            tableEmployes.setItems(listeFiltrée);

        } catch (SQLException e) {
            e.printStackTrace();
        }
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
        System.out.println(message);
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
