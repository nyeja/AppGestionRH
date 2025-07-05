package rh.controller;

import java.sql.*;

import javafx.beans.property.Property;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import rh.model.departement.tableauDepartement;
import rh.utils.ConnexionDB;

import javax.swing.*;

public class departementController {

    private static Connection conn = ConnexionDB.getConnection();

    @FXML
    private TextField tfNom;

    @FXML
    private TextField tfCode;

    @FXML
    private  ChoiceBox<String> cbResponsable;

    @FXML
    private TextField tfLocalisation;

    @FXML
    private TextField tfDescription;

    @FXML
    private TextField tfNombreEmployer;

    @FXML
    private Button btnValider;

    @FXML
    private Button btnSupprimer;

    @FXML
    private Label labelMessage;

    @FXML
    private TableView<tableauDepartement> tableDepartement;

    @FXML
    private String id_dpm;

    @FXML
    private TableColumn<tableauDepartement,String> colId;
    @FXML
    private TableColumn<tableauDepartement,String> colNom;
    @FXML
    private TableColumn<tableauDepartement,String> colCode;
    @FXML
    private TableColumn<tableauDepartement,String> colResponsable;
    @FXML
    private TableColumn<tableauDepartement,String> colLocalisation;
    @FXML
    private TableColumn<tableauDepartement,String> colDescription;
    @FXML
    private TableColumn<tableauDepartement,String> colNombreEmployer;

    @FXML
    private ListView listViewDepartement;

    @FXML
    private ObservableList<String> listDepartement = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Ajoute des options à la ChoiceBox
        cbResponsable.getItems().addAll(
                "RH",
                "IT",
                "Finance",
                "Marketing"
        );

       try {
           // Liaison des colonnes aux propriétés de la classe tableauDepartement
           colId.setCellValueFactory(new PropertyValueFactory<>("id"));
           colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
           colCode.setCellValueFactory(new PropertyValueFactory<>("code"));
           colResponsable.setCellValueFactory(new PropertyValueFactory<>("idResponsable"));
           colLocalisation.setCellValueFactory(new PropertyValueFactory<>("localisation"));
           colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
           colNombreEmployer.setCellValueFactory(new PropertyValueFactory<>("nombreEmployes"));

           // Chargement des données depuis la base de données et affichage dans la TableView
           tableDepartement.setItems(chargerProduits());

       }catch (Exception e){
           System.out.println("voici l'erreur" + e);
       }
    }
/* aaffichage avec listview
    @FXML
    public void AfficherDepartement(){
        String sqlAfficher = "select * from departement";


        try (PreparedStatement stmt = conn.prepareStatement(sqlAfficher)) {
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                String id =   rs.getString("id_departement");
                String nom =   rs.getString("code");
                String id_responsable =   rs.getString("id_responsable");
                String localisation =   rs.getString("LOCALISATION");
                String description =   rs.getString("DESCRIPTION");
                String nombre_employes =   rs.getString("NOMBRE_EMPLOYES");
                for (int i= 1; i < rs.getRow(); i++){

                    listDepartement.add("Voici le " + i + " département " + "\nid : " + id);
                    listDepartement.add("Département de " + nom);
                    listDepartement.add("Mr/Mme " + id_responsable);
                    listDepartement.add("Localisé au " + localisation);
                    listDepartement.add("description : " + description);
                    listDepartement.add("Effectif : " + nombre_employes);
                    listViewDepartement.setItems(listDepartement);
                }

            }

        } catch (Exception e) {
            labelMessage.setText("Erreur : Echec de suppression" + e.getMessage());
            e.printStackTrace();
        }
    }*/
    // Méthode statique qui charge les départements depuis la base et les retourne sous forme de liste observable
    private static ObservableList<tableauDepartement> chargerProduits() {
        // Liste observable qui contiendra les départements récupérés
        ObservableList<tableauDepartement> listDepartement = FXCollections.observableArrayList();

        // Requête SQL pour récupérer tous les départements
        String sql_afficher = "SELECT * from departement";

        // Bloc try-with-resources pour exécuter la requête en toute sécurité
        try (PreparedStatement stmt = conn.prepareStatement(sql_afficher)) {
            ResultSet rs = stmt.executeQuery(); // Exécution de la requête

            // Boucle sur chaque ligne retournée
            while(rs.next()) {
                // Récupération des champs de la table
                String id = rs.getString("ID_DEPARTEMENT");
                String nom = rs.getString("NOM");
                String code = rs.getString("CODE");
                String id_responsable = rs.getString("ID_RESPONSABLE");
                String localisation = rs.getString("LOCALISATION");
                String description = rs.getString("DESCRIPTION");
                String nombre_employes = rs.getString("NOMBRE_EMPLOYES");

                // Création d’un objet tableauDepartement et ajout à la liste observable
                listDepartement.add(new tableauDepartement(id, nom , code , id_responsable, localisation, description, nombre_employes));
            }

        } catch (SQLException e) {
            // En cas d’erreur, afficher les détails dans la console
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("voici l'erreur de charger produits : " + e);
        }

        // Retourner la liste pour l'afficher dans la TableView
        return listDepartement;
    }


    @FXML
    private void ajouterDepartement() {

        String nom = tfNom.getText();
        String code = tfCode.getText();
        String resposable = cbResponsable.getValue();
        String localisation = tfLocalisation.getText();
        String description = tfDescription.getText();
        String nbrEmployer = tfNombreEmployer.getText();

        String sql = "insert into DEPARTEMENT (NOM,CODE,ID_RESPONSABLE,LOCALISATION,DESCRIPTION,NOMBRE_EMPLOYES) VALUES (?,?,?,?,?,?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nom);
            stmt.setString(2, code);
            stmt.setString(3, resposable);
            stmt.setString(4, localisation);
            stmt.setString(5, description);
            stmt.setInt(6, Integer.parseInt(nbrEmployer));

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                tfNom.setText("");
                tfCode.setText("");
                tfDescription.setText("");
                tfLocalisation.setText("");
                tfNombreEmployer.setText("");
                cbResponsable.setValue("");
                labelMessage.setText("Département ajouté avec succès !");
            } else {
                labelMessage.setText("Échec de l'ajout du département.");
            }

        } catch (NumberFormatException e) {
            labelMessage.setText("Erreur : nombre d'employés invalide.");
            e.printStackTrace();
        } catch (Exception e) {
            labelMessage.setText("Erreur lors de l'ajout : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void preparModificationDepartement(){
        tableauDepartement selectedDepartement = tableDepartement.getSelectionModel().getSelectedItem();
        if (selectedDepartement != null){
            // System.out.println("Voici l'élément selectionner "+ "Id : " + selectedDepartement.getId()+ "\nnom : " + selectedDepartement.getNom());
            // recupération des données dans le tableview
            String nom = selectedDepartement.getNom();
            String code = selectedDepartement.getCode();
            String responsable = selectedDepartement.getIdResponsable();
            String localition = selectedDepartement.getLocalisation();
            String description = selectedDepartement.getDescription();
            String nombres = selectedDepartement.getNombreEmployes();
            // completer les formulaire avec les données récuperer
            tfNom.setText(nom);
            tfCode.setText(code);
            cbResponsable.setValue(responsable);
            tfLocalisation.setText(localition);
            tfDescription.setText(description);
            tfNombreEmployer.setText(nombres);
        }
    }

    @FXML
    private void ValiderModification(){
        // recuperation les valeur ajouter dans le champs de texte
        String nom = tfNom.getText();
        String code = tfCode.getText();
        String responsable = cbResponsable.getValue();
        String localition = tfLocalisation.getText();
        String description = tfDescription.getText();
        String nombres = tfNombreEmployer.getText();

        String sql_modification = "UPDATE departement SET nom = ?, code = ?,id_responsable = ?,localisation = ?,description = ?,nombre_employes = ? WHERE id_departement = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql_modification)){
            tableauDepartement selectedDepartement = tableDepartement.getSelectionModel().getSelectedItem();
            String idDep = selectedDepartement.getId();
            //préparation de la modification
            stmt.setString(1, nom);
            stmt.setString(2, code);
            stmt.setString(3, responsable);
            stmt.setString(4, localition);
            stmt.setString(5, description);
            stmt.setInt(6, Integer.parseInt(nombres));
            stmt.setString(7,idDep);
            // execution de la modification
            int ligneModifier = stmt.executeUpdate();
            if (ligneModifier > 0){
                tfNom.setText("");
                tfCode.setText("");
                tfDescription.setText("");
                tfLocalisation.setText("");
                tfNombreEmployer.setText("");
                cbResponsable.setValue("");
                labelMessage.setText("Modification du département " + idDep + " réussie.");
            }else {
                labelMessage.setText("Erreur lors de la modification");
            }
        }catch (Exception e){
            System.out.println("Voici l'erreur " + e);
        }
    }

    @FXML
    public void supprimerDepartement(ActionEvent actionEvent) {
        // Boîte de dialogue de confirmation
        int confirmer = JOptionPane.showConfirmDialog(
                null,
                "Voulez-vous vraiment supprimer ce département ?",
                "Confirmation",
                JOptionPane.YES_NO_OPTION);

        if (confirmer == JOptionPane.YES_OPTION) {

            // Requête SQL pour supprimer un département par son ID
            String sql_delete = "DELETE FROM departement WHERE id_departement = ?";
            // Bloc try-with-resources pour gérer automatiquement la fermeture du PreparedStatement
            try (PreparedStatement stmt = conn.prepareStatement(sql_delete)) {
                // ajout d'un ecouteur ou listener (sert à réagir automatiquement à un évènement ou à un changement dans un intèrface utilisateur
                tableauDepartement selectedDepartement = tableDepartement.getSelectionModel().getSelectedItem();
                if (selectedDepartement != null){
                    String id_dpm = selectedDepartement.getId();
                    //System.out.println("voici l'id du département selectionner " + id_dpm);
                    // Remplacement du "?" dans la requête SQL par l'ID sélectionné
                    stmt.setString(1, id_dpm);

                    // Exécution de la suppression
                    int ligneSupprimee = stmt.executeUpdate();

                    if (ligneSupprimee > 0) {
                        // Message de succès
                        labelMessage.setText("Suppression du département " + id_dpm + " réussie.");
                    } else {
                        // Aucun département supprimé (ID inexistant ?)
                        labelMessage.setText("Aucune suppression effectuée. ID introuvable ?");
                    }
                }else {
                    System.out.println("Id departement introuvable");
                }

            } catch (Exception e) {
                // Affichage de l'erreur en cas de problème SQL
                labelMessage.setText("Erreur : " + e.getMessage());
                System.out.println("Voici l'erreur : " + e.getMessage());
            }
        }
    }

}
