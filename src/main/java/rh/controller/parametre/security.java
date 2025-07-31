package rh.controller.parametre;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import rh.model.employe.employe;
import rh.model.session.userConnecter;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

import  rh.utils.ConnexionDB;

public class security implements Initializable {


    Connection conn = ConnexionDB.getConnection();
    @FXML TextField tfMdp;
    @FXML Label lbAvertissement;
    @FXML Button btnValider;

    @Override
    public void initialize(URL location , ResourceBundle resources ) {
        lbAvertissement.setVisible(false);
        String user = userConnecter.getUsername();
        completerMotDePasse();
        tfMdp.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 10) {
                lbAvertissement.setVisible(true);
                lbAvertissement.setText("Votre mot de passe ne doit pas être supérieur à 10");
                btnValider.setDisable(true);
            }else{
                lbAvertissement.setVisible(false);
                btnValider.setDisable(false);
            }
        });

    }

    @FXML
    public void completerMotDePasse(){
        String sql = "SELECT * FROM employe WHERE id = ?";
        String compte = userConnecter.getId();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, compte); // Remplace le ? par la valeur de l'utilisateur connecté

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String mdp = rs.getString("mdp");
                    employe employe = new employe(mdp);
                    tfMdp.setText(employe.getMotDePasse());

                }

            }
        } catch (Exception e) {
            System.out.println("Erreur lors du chargement du mot de passe: " + e);
        }
    }
    @FXML
    private void ValiderMotDePass(){
        String motDePass = tfMdp.getText();

        String sql_modification = "UPDATE employe SET mdp = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql_modification)){
            String id = userConnecter.getId();
            //préparation de la modification
            stmt.setString(1, motDePass);
            stmt.setString(2,id);
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
}
