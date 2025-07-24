/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rh.controller;

/**
 *
 * @author USER
 */
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import rh.dao.DemandePwdDAO;
import rh.model.DemandeChangementPwd;

import java.sql.Date;

public class ValiderDemandeController {

    @FXML
    private TableView<DemandeChangementPwd> tableDemandes;
    @FXML
    private TableColumn<DemandeChangementPwd, String> colUsername;
    @FXML
    private TableColumn<DemandeChangementPwd, String> colNouveauMdp;
    @FXML
    private TableColumn<DemandeChangementPwd, Date> colDate;

    private final DemandePwdDAO demandeDAO = new DemandePwdDAO();

    @FXML
    public void initialize() {
        colUsername.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getUsername()));
        colNouveauMdp.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNouveauMdp()));
        colDate.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getDateDemande()));

        tableDemandes.setItems(FXCollections.observableArrayList(demandeDAO.getDemandesNonValidees()));
    }

    @FXML
    public void handleValiderDemande() {
        DemandeChangementPwd selected = tableDemandes.getSelectionModel().getSelectedItem();
        if (selected != null) {
            demandeDAO.validerDemande(selected.getId(), selected.getIdUtilisateur(), selected.getNouveauMdp());
            tableDemandes.getItems().remove(selected);
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Veuillez sélectionner une demande à valider.");
            alert.showAndWait();
        }
    }
}
