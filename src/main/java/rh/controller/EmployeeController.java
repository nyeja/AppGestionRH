package rh.controller;

import javafx.event.ActionEvent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import rh.dao.employedao;
import rh.model.employe.employe;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import rh.utils.ConnexionDB;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

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
    @FXML private ChoiceBox<String> cbRole;

    // Boutons
    @FXML private Button btnAjouter;
    @FXML private Button btnModifier;
    @FXML private Button btnSupprimer;
    @FXML private Button btnValiderModification;
    @FXML private Button btnViderChamps;
    @FXML private Button btnRechercher;
    @FXML private Button btnAnulerModification;

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
    @FXML private TableColumn<employe, String> colRole;

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

        ajouterListeners();

        btnImport.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                Window window = btnImport.getScene().getWindow();
                setupImportButton(window);
            }
        });

        cbRole.getItems().addAll(
          "Admin", "Employer"
        );
        cbRole.setValue("Employer");

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
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));

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

    public void loadEmployees() {
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

    /**
     * Configure l'action d'importation d'image
     */
    @FXML
    private Button btnImport;

    @FXML
    private ImageView imageView;
    private void setupImportButton(Window window) {
        btnImport.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Importer une image");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Fichiers image", "*.png", "*.jpg", "*.jpeg")
            );

            File fichier = fileChooser.showOpenDialog(null);
            if (fichier != null) {
                imagePathSelectionnee = fichier.getAbsolutePath(); // Enregistre le chemin de l'image
                imageView.setImage(new Image(fichier.toURI().toString())); // Affiche l'image dans le ImageView
            }
        });
    }


public void chargerImageParDefaut() {
    try {
        // Chargement depuis le dossier resources (accessible via le classpath)
        Image image = new Image(getClass().getResource("/img/utilisateur.png").toExternalForm());
        imageView.setImage(image);
    } catch (Exception e) {
        e.printStackTrace();
        imageView.setImage(null);
        System.out.println("Image par défaut introuvable dans resources/img/");
    }
}
    public static String getCheminImageParDefaut() {
        return "resources/img/utilisateur.png"; // Adapte selon ton projet
    }

    // Méthode utilitaire pour générer une chaîne alphanumérique aléatoire
    private String getRandomAlphaNumeric(int longueur) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < longueur; i++) {
            int index = random.nextInt(chars.length());
            sb.append(chars.charAt(index));
        }
        return sb.toString();
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
private void preparModificationEmploye(){

    Connection conn = ConnexionDB.getConnection();
    // Selectionner les données dans le tableau
    employe selectedEmploye = tableEmployes.getSelectionModel().getSelectedItem();

    if (selectedEmploye != null){
        btnAjouter.setDisable(true);
        btnAnulerModification.setDisable(false);
        btnModifier.setDisable(true);
        btnValiderModification.setDisable(false);

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
        String role = selectedEmploye.getRole();
        String imagePath = null; // Variable pour stocker le chemin de l'image
        String sql_image = "SELECT img FROM employe WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql_image)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    imagePath = rs.getString("img"); // Récupère le chemin de l'image
                    // Afficher l'image dans le ImageView
                    if (imagePath != null && !imagePath.isEmpty()) {
                        try {
                            try {
                                Image image;

                                // Vérifie si le chemin de l'image commence par "/img/"
                                // Cela signifie que l'image est dans le dossier de ressources
                                if (imagePath.startsWith("/img/")) {
                                    // Construit le chemin absolu du fichier image à partir du dossier ressources
                                    File imageFile = new File("src/main/resources" + imagePath);

                                    // Si le fichier existe physiquement, on le charge à l’aide de son URI
                                    if (imageFile.exists()) {
                                        image = new Image(imageFile.toURI().toString()); // Charge le fichier local
                                    } else {
                                        // Si le fichier n'existe pas, on charge l'image par défaut
                                        image = new Image(getClass().getResource("/img/utilisateur.png").toExternalForm());
                                    }

                                } else {
                                    // Si le chemin ne commence pas par "/img/", on tente de le charger via le classloader
                                    URL imageUrl = getClass().getResource(imagePath);

                                    if (imageUrl != null) {
                                        image = new Image(imageUrl.toExternalForm()); // Charge depuis les ressources intégrées
                                    } else {
                                        // Si aucun chemin valide n'est trouvé, on utilise l'image par défaut
                                        image = new Image(getClass().getResource("/img/utilisateur.png").toExternalForm());
                                    }
                                }

                                // Affiche l'image dans l'ImageView
                                imageView.setImage(image);

                            } catch (Exception e) {
                                // En cas d'erreur, affiche la pile d'exception pour le débogage
                                e.printStackTrace();

                                // Charge une image par défaut en cas d'erreur
                                chargerImageParDefaut();
                            }

                        } catch (Exception e) {
                            // En cas d'erreur (ex: chemin invalide), on affiche l'image par défaut
                            e.printStackTrace();
                            chargerImageParDefaut();
                        }
                    } else {
                        // Aucun chemin fourni, on affiche l'image par défaut
                        chargerImageParDefaut();
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération de l'image : " + e.getMessage());
        }

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
        cbRole.setValue(role);


    }else{
        JOptionPane.showConfirmDialog(
                null,
                "Veuillez selectionez l'un des éléments dans le tableau",
                "OK",
                JOptionPane.CLOSED_OPTION
        );
    }
}
    public String copierImageVersRessource(String imagePathSelectionnee, String nomEmploye) {
        if (imagePathSelectionnee != null && !imagePathSelectionnee.isEmpty()) {
            try {
                File sourceFile = new File(imagePathSelectionnee);
                String extension = imagePathSelectionnee.substring(imagePathSelectionnee.lastIndexOf("."));
                String nomFichier = nomEmploye.toLowerCase().replaceAll("[^a-zA-Z0-9]", "_") + "_img" + System.currentTimeMillis() + extension;

                String cheminDestination = "src/main/resources/img/" + nomFichier;
                File destinationFile = new File(cheminDestination);
                destinationFile.getParentFile().mkdirs();

                Files.copy(sourceFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                // Retourne le chemin relatif utilisable par getClass().getResource(...)
                return "/img/" + nomFichier;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // En cas d’erreur ou image vide, retourne l’image par défaut
        return "/img/utilisateur.png";
    }

@FXML
public void modifierEmploye() throws SQLException {
    String nom = txtNom.getText();
    String prenom = txtPrenom.getText();
    String adresse = txtAdresse.getText();
    String phone = txtTelephone.getText();
    String mail = txtEmail.getText();
    LocalDate date = dateEmbauche.getValue();
    String poste = comboPoste.getValue();
    String departement = comboDepartement.getValue();
    String role = cbRole.getValue();

    // Récupérer l'image actuelle affichée dans l'ImageView
    Image image = imageView.getImage();
    String imagePath = null;

    if (image != null && image.getUrl() != null) {
        // Chemin absolu de l'image affichée
        String imageUrl = image.getUrl();

        // Convertir en chemin système s’il s’agit d’un fichier local
        if (imageUrl.startsWith("file:/")) {
            try {
                File file = new File(new URI(imageUrl));
                String cheminCopie = copierImageVersRessource(file.getAbsolutePath(), nom);
                imagePath = cheminCopie;
            } catch (Exception e) {
                e.printStackTrace();
                imagePath = "/img/utilisateur.png";
            }
        } else {
            // Si image déjà dans ressources, on laisse le chemin tel quel
            imagePath = imageUrl.replace(getClass().getResource("/").toExternalForm(), "/");
        }
    } else {
        imagePath = "/img/utilisateur.png";
    }
    System.out.println("img path : " + imagePath);
    Connection conn = ConnexionDB.getConnection();
    String sql_modification = "UPDATE employe SET Nom = ?, PRENOMS = ?, ADRESSE = ?, TELEPHONE = ?, EMAIL = ?, DATE_EMBAUCHE = ?, ID_POSTE = ?, DEPARTEMENT = ?, IMG = ? , ROLE = ? WHERE ID = ?";

    try (PreparedStatement stmt = conn.prepareStatement(sql_modification)) {
        employe selectedemploye = tableEmployes.getSelectionModel().getSelectedItem();
        String idEmploye = selectedemploye.getId();

        stmt.setString(1, nom);
        stmt.setString(2, prenom);
        stmt.setString(3, adresse);
        stmt.setString(4, phone);
        stmt.setString(5, mail);
        stmt.setDate(6, java.sql.Date.valueOf(date));
        stmt.setString(7, poste);
        stmt.setString(8, departement);
        stmt.setString(9, imagePath);
        stmt.setString(10, role);
        stmt.setString(11, idEmploye);

        int ligneModifier = stmt.executeUpdate();
        if (ligneModifier > 0) {
            tableEmployes.setItems(employeDAO.getAllEmployes());
            txtNom.clear();
            txtEmployeeId.clear();
            txtPrenom.clear();
            txtAdresse.clear();
            txtTelephone.clear();
            comboDepartement.setValue(null);
            comboPoste.setValue(null);
            txtEmail.clear();
            dateEmbauche.setValue(null);
            cbRole.setValue(null);
            imageView.setImage(null);
            System.out.println("Modification de l'employé " + idEmploye + " réussie.");
            btnModifier.setDisable(false);
            btnValiderModification.setDisable(true);
            btnAjouter.setDisable(false);
            btnAnulerModification.setDisable(true);
            viderChamps();
        } else {
            System.out.println("Erreur lors de la modification");
        }
    } catch (Exception e) {
        System.out.println("Voici l'erreur : " + e);
    }

    imageView.setImage(new Image(getClass().getResource("/img/utilisateur.png").toExternalForm()));

}

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

            btnViderChamps.setDisable(true);
            btnAjouter.setDisable(false);
            btnAnulerModification.setDisable(true);
            btnValiderModification.setDisable(true);
            btnModifier.setDisable(false);
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
            cbRole.setValue(null);
        selectedEmployee = null;
        tableEmployes.getSelectionModel().clearSelection();

        updateStatut("Champs vidés");
    }
    private void ajouterListeners() {
        // Écouteurs pour les TextField
        txtNom.textProperty().addListener((obs, oldVal, newVal) -> activerBtnVider());
        txtPrenom.textProperty().addListener((obs, oldVal, newVal) -> activerBtnVider());
        txtTelephone.textProperty().addListener((obs, oldVal, newVal) -> activerBtnVider());
        txtEmail.textProperty().addListener((obs, oldVal, newVal) -> activerBtnVider());
        txtAdresse.textProperty().addListener((obs, oldVal, newVal) -> activerBtnVider());
        txtEmployeeId.textProperty().addListener((obs, oldVal, newVal) -> activerBtnVider());

        // Écouteurs pour les ComboBox
        comboDepartement.valueProperty().addListener((obs, oldVal, newVal) -> activerBtnVider());
        comboPoste.valueProperty().addListener((obs, oldVal, newVal) -> activerBtnVider());
        cbRole.valueProperty().addListener((obs, oldVal, newVal) -> activerBtnVider());
        // Écouteur pour la date
        dateEmbauche.valueProperty().addListener((obs, oldVal, newVal) -> activerBtnVider());
    }

    private void activerBtnVider() {
        boolean auMoinsUnRempli =
                !txtNom.getText().isEmpty() ||
                        !txtPrenom.getText().isEmpty() ||
                        !txtTelephone.getText().isEmpty() ||
                        !txtEmail.getText().isEmpty() ||
                        !txtAdresse.getText().isEmpty() ||
                        !txtEmployeeId.getText().isEmpty() ||
                        comboDepartement.getValue() != null ||
                        comboPoste.getValue() != null ||
                        dateEmbauche.getValue() != null ||
                        cbRole.getValue() != null;

        btnViderChamps.setDisable(!auMoinsUnRempli);
    }

    @FXML
    private void actualiserListe() {

            loadEmployees(); // tu peux simplement appeler loadEmployees ici


    }

    private void rechercherEmploye(String motCle) {
        // stocké les listes pour être bien syncronisé
        Connection conn = ConnexionDB.getConnection();
        ObservableList<employe> listeFiltrée = FXCollections.observableArrayList();
        String sql = "SELECT * FROM employe WHERE nom LIKE ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + motCle + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                // Stocké les données récuperer dans des variables

                String id = rs.getString("id");
                String nom = rs.getString("nom");
                String prenoms = rs.getString("prenoms");
                int telephone = rs.getInt("telephone");
                String email = rs.getString("email");
                String addresse = rs.getString("adresse");
                java.sql.Date date = rs.getDate("date_embauche");
                String departement = rs.getString("departement");
                String poste = rs.getString("id_poste");
                String role = rs.getString("role");

                employe emp = new employe(id , nom , prenoms , telephone , email , addresse , date , departement, poste ,role);
                listeFiltrée.add(emp);
            }
            tableEmployes.setItems(listeFiltrée);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }




    private static String imagePathSelectionnee = null;
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

        String role = cbRole.getValue();
        employee.setRole(role);
        // Copier l'image importée dans src/main/resources/img/ avec un nom basé sur le nom de l'employé
        if (imagePathSelectionnee != null && !imagePathSelectionnee.isEmpty()) {
            try {
                File sourceFile = new File(imagePathSelectionnee);
                String extension = imagePathSelectionnee.substring(imagePathSelectionnee.lastIndexOf("."));
                String nomFichier = employee.getNom().toLowerCase().replaceAll("[^a-zA-Z0-9]", "_") + "_img" + extension;

                // Dossier où on veut copier l’image dans le projet
                String cheminDestination = "src/main/resources/img/" + nomFichier;
                File destinationFile = new File(cheminDestination);
                destinationFile.getParentFile().mkdirs();

                Files.copy(sourceFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                // Enregistrer le chemin relatif depuis les classes Java
                employee.setImage("/img/" + nomFichier);

            } catch (IOException e) {
                e.printStackTrace();
                employee.setImage("/img/utilisateur.png"); // chemin relatif depuis les ressources
            }
        } else {
            employee.setImage("/img/utilisateur.png");
        }
        // Génération automatique du mot de passe
        String randomPart = getRandomAlphaNumeric(5); // Ex : "X2f9p"

        employee.setMotDePasse(randomPart); // Assigne le mot de passe généré

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
        String role = cbRole.getValue();
        if (role == null ) {
            errors.append("- Le rôle est obligatoire\n");
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

    public void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
        updateStatut(message);
    }

    public void showError(String title, String message) {
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
