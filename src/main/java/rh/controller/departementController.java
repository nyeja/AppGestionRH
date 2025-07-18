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
    // variable de connection au base de donné
    private static Connection conn = ConnexionDB.getConnection();
    // variable utilisé pour le formulaire du departement
    @FXML
    private TextField tfNom;
    @FXML
    private TextField tfCode;
    @FXML
    private  ComboBox<String> cbResponsable;
    @FXML
    private TextField tfLocalisation;
    @FXML
    private TextField tfDescription;
    @FXML
    private TextField tfNombreEmployer;
    @FXML
    private TextField tfRechercher;

    @FXML
    private Button btnValider;

    @FXML
    private Button btnSupprimer;

    @FXML
    private Button modifValider;

    // Variable pour la model du tableaux
    @FXML
    private TableView<tableauDepartement> tableDepartement;

    @FXML
    private String id_dpm;
    // variable utilisé pour les collones du tableaux
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
    // l'observableList permet de stocker les donné avant de l'ajouter dans une liste, tableau ou autre
    // il permet de synchroniser facilement l’interface utilisateur avec les données
    @FXML
    private ObservableList<String> listDepartement = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Ajoute des options à la ChoiceBox
        String sql = "SELECT nom FROM poste";
        try (PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            cbResponsable.getItems().clear();
            while (rs.next()) {
                String nom = rs.getString("nom"); // Récupère le nom depuis la colonne
                System.out.println("Nom trouvé : " + nom);
                cbResponsable.getItems().add(nom); // Ajoute le nom dans le ComboBox

            }

        } catch (Exception e) {
            System.out.println("Erreur lors de la récupération des noms d'employés : " + e.getMessage());
        }
    /*     cbResponsable.getItems().addAll(
                "haendel",
                "abraham"
        );*/
        // recherche automatique
        tfRechercher.textProperty().addListener((observable, oldValue, newValue) -> {
            rechercherDepartement(newValue);
        });
        // Mettre le boutton valider invisible
        modifValider.setVisible(false);
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
        // Récupère les données saisies par l'utilisateur dans les champs du formulaire
        String nom = tfNom.getText();
        String code = tfCode.getText();
        String resposable = cbResponsable.getValue(); // Ce champ doit contenir l'ID ou le nom du responsable
        String localisation = tfLocalisation.getText();
        String description = tfDescription.getText();

        // Vérifie si un des champs obligatoires est vide
        if (nom.isEmpty() || code.isEmpty() || localisation.isEmpty() || description.isEmpty() || resposable == null) {
            // Affiche un message d'erreur si un champ est vide
            JOptionPane.showConfirmDialog(
                    null,
                    "Veuillez compléter tous les champs", // Message
                    "Champs manquants", // Titre
                    JOptionPane.CLOSED_OPTION // Bouton de fermeture
            );
        } else {
            // Commande SQL pour insérer un nouveau département dans la base de données
            String sql = "INSERT INTO DEPARTEMENT (NOM, CODE, ID_RESPONSABLE, LOCALISATION, DESCRIPTION) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                // Remplace les ? par les vraies valeurs saisies
                stmt.setString(1, nom);          // Nom du département
                stmt.setString(2, code);         // Code du département
                stmt.setString(3, resposable);   // ID du responsable (à adapter si nécessaire)
                stmt.setString(4, localisation); // Localisation du département
                stmt.setString(5, description);  // Description du département

                // Exécute la requête d'insertion
                int rowsInserted = stmt.executeUpdate();

                // Vérifie si l'insertion a réussi
                if (rowsInserted > 0) {
                    // Recharge les données dans le tableau des départements
                    tableDepartement.setItems(chargerProduits());

                    // Vide les champs du formulaire après l'ajout
                    tfNom.clear();
                    tfCode.clear();
                    tfDescription.clear();
                    tfLocalisation.clear();
                    tfNombreEmployer.clear(); // Champ optionnel, à supprimer s'il n'est pas utilisé
                    cbResponsable.setValue(null); // Réinitialise le choix du responsable

                    System.out.println("Département ajouté avec succès !");
                } else {
                    System.out.println("Échec de l'ajout du département.");
                }

            } catch (NumberFormatException e) {
                // Gestion d'erreur si un champ numérique contient une mauvaise valeur
                System.out.println("Erreur : nombre d'employés invalide." + e);
            } catch (Exception e) {
                // Affiche les erreurs générales de la base de données
                System.out.println("Erreur lors de l'ajout : " + e);
            }
        }
    }


    @FXML
    private void preparModificationDepartement(){
        // Selectionner les données dans le tableau
        tableauDepartement selectedDepartement = tableDepartement.getSelectionModel().getSelectedItem();
        if (selectedDepartement != null){
            tfNombreEmployer.setDisable(false);
            modifValider.setVisible(true);
            // System.out.println("Voici l'élément selectionner "+ "Id : " + selectedDepartement.getId()+ "\nnom : " + selectedDepartement.getNom());
            // recupération des données dans le tableview
            String nom = selectedDepartement.getNom();
            String code = selectedDepartement.getCode();
            String responsable = selectedDepartement.getIdResponsable();
            String localition = selectedDepartement.getLocalisation();
            String description = selectedDepartement.getDescription();
            String nombres = selectedDepartement.getNombreEmployes();
            // completer les formulaires avec les données récuperer
            tfNom.setText(nom);
            tfCode.setText(code);
            cbResponsable.setValue(responsable);
            tfLocalisation.setText(localition);
            tfDescription.setText(description);
            tfNombreEmployer.setText(nombres);
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
    private void ValiderModification(){
        // recuperation des valeurs ajoutées dans les champs de texte
        String nom = tfNom.getText();
        String code = tfCode.getText();
        String responsable = cbResponsable.getValue();
        String localition = tfLocalisation.getText();
        String description = tfDescription.getText();
        String nombres = tfNombreEmployer.getText();
        // Commande sql pour la modification des données dans une base de donnée
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
                modifValider.setVisible(false);
                tableDepartement.setItems(chargerProduits());
                tfNom.setText("");
                tfCode.setText("");
                tfDescription.setText("");
                tfLocalisation.setText("");
                tfNombreEmployer.setText("");
                cbResponsable.setValue("");
                System.out.println("Modification du département " + idDep + " réussie.");
            }else {
                System.out.println("Erreur lors de la modification");
            }
        }catch (Exception e){
            System.out.println("Voici l'erreur " + e);
        }
    }

    @FXML
    public void supprimerDepartement(ActionEvent actionEvent) {
        // ajout d'un ecouteur ou listener (sert à réagir automatiquement à un évènement ou à un changement dans un intèrface utilisateur
        tableauDepartement selectedDepartement = tableDepartement.getSelectionModel().getSelectedItem();
        if (selectedDepartement != null){
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


                        String id_dpm = selectedDepartement.getId();
                        //System.out.println("voici l'id du département selectionner " + id_dpm);
                        // Remplacement du "?" dans la requête SQL par l'ID sélectionné
                        stmt.setString(1, id_dpm);

                        // Exécution de la suppression
                        int ligneSupprimee = stmt.executeUpdate();

                        if (ligneSupprimee > 0) {
                            tableDepartement.setItems(chargerProduits());
                            // Message de succès
                            System.out.println("Suppression du département " + id_dpm + " réussie.");
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
                System.out.println("Id departement introuvable");
            }
        }else{
            int conf = JOptionPane.showConfirmDialog(
                    null,
                    "Veuillez selectionnez un élément dans le tableau",
                    "avertissement",
                    JOptionPane.CLOSED_OPTION);
        }
    }
    private void rechercherDepartement(String motCle) {
        // stocké les listes pour être bien syncronisé
        ObservableList<tableauDepartement> listeFiltrée = FXCollections.observableArrayList();
        String sql = "SELECT * FROM departement WHERE nom LIKE ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + motCle + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                // Stocké les données récuperer dans des veriables
                String id = rs.getString("ID_DEPARTEMENT");
                String nom = rs.getString("NOM");
                String code = rs.getString("CODE");
                String id_responsable = rs.getString("ID_RESPONSABLE");
                String localisation = rs.getString("LOCALISATION");
                String description = rs.getString("DESCRIPTION");
                String nombre_employes = rs.getString("NOMBRE_EMPLOYES");

                tableauDepartement dept = new tableauDepartement(id, nom, code, id_responsable, localisation, description, nombre_employes);
                listeFiltrée.add(dept);
            }

            tableDepartement.setItems(listeFiltrée);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void viderChamps(){
        int zero = 0 ;
        tfNom.clear();
        tfCode.clear();
        cbResponsable.setValue("");
        tfDescription.clear();
        tfLocalisation.clear();
        tfNombreEmployer.setText(String.valueOf(zero));
    }

}

