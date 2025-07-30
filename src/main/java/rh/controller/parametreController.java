package rh.controller;

import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import rh.model.departement.tableauDepartement;
import rh.model.employe.employe;
import rh.model.session.userConnecter;
import rh.utils.ConnexionDB;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;


public class parametreController implements Initializable {

    @FXML StackPane StackpaneMain;

    @FXML Label  lbNomPrenom;
    @FXML Label lbMail;
    @FXML TextField tfNom;
    @FXML TextField tfPrenom;
    @FXML TextField tfEmail;
    @FXML TextField tfTel;
    @FXML TextField tfAdresse;
    @FXML TextField tfDtns;
    Connection conn = ConnexionDB.getConnection();

    @Override
    public void initialize(URL location , ResourceBundle resources ) {
        String user = userConnecter.getUsername();
        initializeEmploye();
        System.out.println("Id connecter " + userConnecter.getId() );
        System.out.println("Utilisateur connecté : " + user );
    }
    public void initializeEmploye() {
        String sql = "SELECT * FROM employe WHERE nom = ?";
        String compte = userConnecter.getUsername();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, compte); // Remplace le ? par la valeur de l'utilisateur connecté

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String id = rs.getString("id");
                    String nom = rs.getString("nom");
                    String prenoms = rs.getString("prenoms");
                    int telephone = rs.getInt("telephone");
                    String email = rs.getString("email");
                    String adresse = rs.getString("adresse");
                    Date date = rs.getDate("date_embauche");
                    String departement = rs.getString("departement");
                    String poste = rs.getString("id_poste");
                    String role = rs.getString("role");
                    String img = rs.getString("img");
                    String mdp = rs.getString("mdp");

                    // Créer l'objet employe correctement
                    employe employeur = new employe(
                            id, nom, prenoms, telephone, email, adresse, date,
                            departement, poste ,role , img , mdp
                    );
                    lbNomPrenom.setText(employeur.getNom() + " " + employeur.getPrenom());
                    lbMail.setText(employeur.getEmail());
                    tfNom.setText(employeur.getNom());
                    tfPrenom.setText(employeur.getPrenom());
                    tfEmail.setText(employeur.getEmail());
                    tfTel.setText(String.valueOf(employeur.getTelephone()));
                    tfAdresse.setText(employeur.getAdresse());
                    // Exemple d'utilisation
                    System.out.println("Nom : " + employeur.getNom());
                    // tu peux ensuite faire : Session.setUtilisateur(employeur);
                }
            }

        } catch (Exception e) {
            System.out.println("Erreur lors de l'initialisation des employé : " + e);
        }
    }


    @FXML
    private void ValiderInfoProfil(){
        String nom = tfNom.getText();
        String prenom = tfPrenom.getText();
        String adresse = tfAdresse.getText();
        String phone = tfTel.getText();
        String mail = tfEmail.getText();

        String sql_modification = "UPDATE employe SET nom = ? , prenoms = ? , adresse = ? , telephone = ? , Email = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql_modification)){
            String id = userConnecter.getId();
            //préparation de la modification
            stmt.setString(1, nom);
            stmt.setString(2, prenom);
            stmt.setString(3, adresse);
            stmt.setString(4, phone);
            stmt.setString(5, mail);
            stmt.setString(6,id);
            // execution de la modification
            int ligneModifier = stmt.executeUpdate();
            if (ligneModifier > 0){
                System.out.println("Modification du département " + id + " réussie.");
            }else {
                System.out.println("Erreur lors de la modification");
            }
        }catch (Exception e){
            System.out.println("Voici l'erreur " + e);
        }
    }
    @FXML
    private void loadProfil() throws IOException {
        Parent node = FXMLLoader.load(getClass().getResource("/fxml/parametre/profil.fxml"));
        StackpaneMain.getChildren().setAll(node);
    }

    @FXML
    private void loadSecurity() throws IOException {
        Parent node = FXMLLoader.load(getClass().getResource("/fxml/parametre/security.fxml"));
        StackpaneMain.getChildren().setAll(node);
    }
}
