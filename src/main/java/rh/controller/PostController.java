package rh.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import rh.model.Departement;
import rh.model.Poste;

import java.sql.*;
import java.util.Optional;

public class PostController {

    @FXML private TextField txtNom;
    @FXML private TextField txtLocalisation;
    @FXML private ChoiceBox<Departement> choiceDepartement;
    @FXML private TableView<Poste> tablePoste;
    @FXML private TableColumn<Poste, String> colNom;
    @FXML private TableColumn<Poste, String> colLocalisation;
    @FXML private TableColumn<Poste, String> colNomDepartement;
    @FXML private TableColumn<Poste, String> colIdPoste;
    @FXML private TableColumn<Poste, String> colIdDepartement;
    @FXML private TextField txtRecherche;
    @FXML private Button btnAjouter;
    @FXML private Button btnModifier;
    @FXML private Button btnSupprimer;
    @FXML private Button btnValider;

    private ObservableList<Poste> posteList = FXCollections.observableArrayList();
    private ObservableList<Departement> departementList = FXCollections.observableArrayList();
    private FilteredList<Poste> filteredPostes;
    private Poste posteSelectionne = null;

    private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521:XE";
    private static final String DB_USER = "Yola";
    private static final String DB_PASSWORD = "Yolabd";

    @FXML
    public void initialize() {
        configureTableColumns();
        setupListeners();
        loadData();
        setupSearchFilter();
    }

    private void configureTableColumns() {
        colIdPoste.setCellValueFactory(new PropertyValueFactory<>("id"));
        colIdDepartement.setCellValueFactory(new PropertyValueFactory<>("idDepartement"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colLocalisation.setCellValueFactory(new PropertyValueFactory<>("localisation"));
        colNomDepartement.setCellValueFactory(new PropertyValueFactory<>("nomDepartement"));
        tablePoste.setItems(posteList);
    }

    private void setupListeners() {
        tablePoste.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            boolean selected = newVal != null;
            btnModifier.setDisable(!selected);
            btnSupprimer.setDisable(!selected);
        });
        btnValider.setDisable(true);
    }

    private void loadData() {
        loadDepartements();
        loadPostes();
    }

    private Connection getConnection() throws SQLException {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver Oracle non trouvé", e);
        }
    }

    private void loadDepartements() {
        departementList.clear();
        String sql = "SELECT ID_DEPARTEMENT, NOM FROM DEPARTEMENT";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                departementList.add(new Departement(
                    rs.getString("ID_DEPARTEMENT"),
                    rs.getString("NOM")
                ));
            }
            choiceDepartement.setItems(departementList);
        } catch (SQLException e) {
            showDatabaseError("Erreur chargement départements", e);
        }
    }

    private void loadPostes() {
        posteList.clear();
        String sql = "SELECT ID_POSTE, NOM, LOCALISATION, ID_DEPARTEMENT FROM POSTE ORDER BY ID_POSTE DESC";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                posteList.add(new Poste(
                    rs.getString("ID_POSTE"),
                    rs.getString("NOM"),
                    rs.getString("LOCALISATION"),
                    rs.getString("ID_DEPARTEMENT"),
                    rs.getString("NOM_DEPARTEMENT")
                ));
            }
        } catch (SQLException e) {
            showDatabaseError("Erreur chargement postes", e);
        }
    }

    private void setupSearchFilter() {
        filteredPostes = new FilteredList<>(posteList, p -> true);

        txtRecherche.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredPostes.setPredicate(poste -> {
                if (newVal == null || newVal.isEmpty()) return true;
                String filter = newVal.toLowerCase();
                return poste.getNom().toLowerCase().contains(filter) ||
                       poste.getLocalisation().toLowerCase().contains(filter) ||
                       poste.getId().toLowerCase().contains(filter) ||
                       poste.getIdDepartement().toLowerCase().contains(filter) ||
                       poste.getNomDepartement().toLowerCase().contains(filter);
            });
        });

        SortedList<Poste> sortedList = new SortedList<>(filteredPostes);
        sortedList.comparatorProperty().bind(tablePoste.comparatorProperty());
        tablePoste.setItems(sortedList);
    }

    @FXML
    private void ajouterPoste() {
        if (!validateInputs()) return;

        String sql = "INSERT INTO POSTE (NOM, LOCALISATION, ID_DEPARTEMENT, NOM_DEPARTEMENT) VALUES (?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            Departement dep = choiceDepartement.getValue();

            stmt.setString(1, txtNom.getText().trim());
            stmt.setString(2, txtLocalisation.getText().trim());
            stmt.setString(3, dep.getId());
            stmt.setString(4, dep.getNom());

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                showAlert("Succès", "Poste ajouté avec succès");
                resetForm();
                loadPostes();
            }
        } catch (SQLException e) {
            showDatabaseError("Erreur ajout poste", e);
        }
    }

    @FXML
    private void preparerModification() {
        posteSelectionne = tablePoste.getSelectionModel().getSelectedItem();
        if (posteSelectionne == null) {
            showAlert("Aucune sélection", "Veuillez sélectionner un poste à modifier");
            return;
        }

        txtNom.setText(posteSelectionne.getNom());
        txtLocalisation.setText(posteSelectionne.getLocalisation());

        choiceDepartement.getItems().stream()
                .filter(d -> d.getId().equals(posteSelectionne.getIdDepartement()))
                .findFirst()
                .ifPresent(choiceDepartement::setValue);

        btnAjouter.setDisable(true);
        btnModifier.setDisable(true);
        btnValider.setDisable(false);
    }

    @FXML
    private void validerModification() {
        if (!validateInputs() || posteSelectionne == null) return;

        String sql = "UPDATE POSTE SET NOM = ?, LOCALISATION = ?, ID_DEPARTEMENT = ?, NOM_DEPARTEMENT = ? WHERE ID_POSTE = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            Departement dep = choiceDepartement.getValue();

            stmt.setString(1, txtNom.getText().trim());
            stmt.setString(2, txtLocalisation.getText().trim());
            stmt.setString(3, dep.getId());
            stmt.setString(4, dep.getNom());
            stmt.setString(5, posteSelectionne.getId());

            if (stmt.executeUpdate() > 0) {
                showAlert("Succès", "Poste modifié avec succès");
                resetForm();
                loadPostes();
            }
        } catch (SQLException e) {
            showDatabaseError("Erreur modification poste", e);
        }
    }

    @FXML
    private void supprimerPoste() {
        Poste poste = tablePoste.getSelectionModel().getSelectedItem();
        if (poste == null) return;

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText("Supprimer le poste " + poste.getNom() + " ?");
        confirmation.setContentText("Cette action est irréversible.");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            String sql = "DELETE FROM POSTE WHERE ID_POSTE = ?";
            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, poste.getId());
                if (stmt.executeUpdate() > 0) {
                    showAlert("Succès", "Poste supprimé avec succès");
                    resetForm();
                    loadPostes();
                }
            } catch (SQLException e) {
                showDatabaseError("Erreur suppression poste", e);
            }
        }
    }

    private boolean validateInputs() {
        return !(txtNom.getText().trim().isEmpty() ||
                 txtLocalisation.getText().trim().isEmpty() ||
                 choiceDepartement.getValue() == null);
    }

    private void resetForm() {
        txtNom.clear();
        txtLocalisation.clear();
        choiceDepartement.getSelectionModel().clearSelection();
        posteSelectionne = null;
        btnAjouter.setDisable(false);
        btnValider.setDisable(true);
        btnModifier.setDisable(true);
        btnSupprimer.setDisable(true);
        tablePoste.getSelectionModel().clearSelection();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showDatabaseError(String context, SQLException e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur Base de Données");
        alert.setHeaderText(context);
        alert.setContentText("Erreur: " + e.getMessage());
        alert.showAndWait();
        e.printStackTrace();
    }
}