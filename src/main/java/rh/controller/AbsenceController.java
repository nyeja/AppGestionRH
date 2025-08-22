package rh.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.input.KeyEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import rh.model.Absence;
import rh.utils.ConnexionDB;

public class AbsenceController {

    @FXML private TextField txtEmployeeId;
    @FXML private TextField txtEmployeeName;
    @FXML private ChoiceBox<String> cbTypeAbsence;
    @FXML private TextField txtMotif;
    @FXML private DatePicker dpDate;
    @FXML private Button btnSave;
    @FXML private Button btnCancel;

    @FXML private TableView<Absence> tableAbsences;
    @FXML private TableColumn<Absence, String> colId;
    @FXML private TableColumn<Absence, String> colNom;
    @FXML private TableColumn<Absence, String> colType;
    @FXML private TableColumn<Absence, LocalDate> colDate;
    @FXML private TableColumn<Absence, String> colMotif;
    @FXML private TableColumn<Absence, String> colStatut;

    private ObservableList<Absence> absenceList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Colonnes
        colId.setCellValueFactory(data -> data.getValue().employeeIdProperty());
        colNom.setCellValueFactory(data -> data.getValue().employeeNameProperty());
        colType.setCellValueFactory(data -> data.getValue().typeProperty());
        colDate.setCellValueFactory(data -> data.getValue().dateProperty());
        colMotif.setCellValueFactory(data -> data.getValue().motifProperty());
        colStatut.setCellValueFactory(data -> data.getValue().statutProperty());

        // Formater la date
        colDate.setCellFactory(column -> new TableCell<>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.format(formatter));
            }
        });

        // Lier la liste au tableau
        tableAbsences.setItems(absenceList);

        // Charger les absences depuis la base
        chargerAbsences();

        // Boutons
        btnSave.setOnAction(e -> ajouterAbsence());
        btnCancel.setOnAction(e -> viderFormulaire());

        dpDate.setValue(LocalDate.now());
    }

    @FXML
    private void onNameTyped(KeyEvent event) {
        String nom = txtEmployeeName.getText().trim();
        if (nom.isEmpty()) {
            txtEmployeeId.clear();
            return;
        }
        try (Connection conn = ConnexionDB.getConnection()) {
            String sql = "SELECT ID FROM utilisateur WHERE LOWER(USERNAME) = LOWER(?)";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, nom);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) txtEmployeeId.setText(rs.getString("ID"));
            else txtEmployeeId.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void chargerAbsences() {
        absenceList.clear();
        try (Connection conn = ConnexionDB.getConnection()) {
            String sql = "SELECT ID_ABSENCE, ID_EMPLOYEE, NOM, TYPE_ABSENCE, DATE_ABSENCE, MOTIF " +
                         "FROM absence ORDER BY DATE_ABSENCE DESC";
            PreparedStatement pst = conn.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                LocalDate date = rs.getDate("DATE_ABSENCE").toLocalDate();
                Absence abs = new Absence(
                        String.valueOf(rs.getInt("ID_EMPLOYEE")),
                        rs.getString("NOM"),
                        rs.getString("TYPE_ABSENCE"),
                        date,
                        rs.getString("MOTIF"),
                        "En attente"
                );
                absenceList.add(abs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ajouterAbsence() {
        String id = txtEmployeeId.getText().trim();
        String nom = txtEmployeeName.getText().trim();
        String type = cbTypeAbsence.getValue();
        String motif = txtMotif.getText().trim();
        LocalDate date = dpDate.getValue();

        if (id.isEmpty() || nom.isEmpty() || type == null || date == null) {
            showAlert("Erreur", "Veuillez remplir tous les champs obligatoires !");
            return;
        }

        try (Connection conn = ConnexionDB.getConnection()) {
            conn.setAutoCommit(false);
            String sql = "INSERT INTO absence (ID_EMPLOYEE, NOM, TYPE_ABSENCE, DATE_ABSENCE, MOTIF)VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, Integer.parseInt(id));
            pst.setString(2, nom);
            pst.setString(3, type);
            pst.setDate(4, java.sql.Date.valueOf(date));
            pst.setString(5, motif);
            conn.commit();
            pst.executeUpdate();
             int row_inserted =  pst.executeUpdate();
             
             if (row_inserted > 0){
                 System.out.println("Succes");
             }else{
                  System.out.println("ECHEC");
             }
           

            // Recharger la liste depuis la base
            chargerAbsences();
            viderFormulaire();
            showAlert("Succès", "Absence ajoutée avec succès !");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ajouter l'absence !");
        }
    }

    private void viderFormulaire() {
        txtEmployeeId.clear();
        txtEmployeeName.clear();
        cbTypeAbsence.setValue(null);
        txtMotif.clear();
        dpDate.setValue(LocalDate.now());
    }

    private void showAlert(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
