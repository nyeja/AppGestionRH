package rh.controller.parametre;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import rh.model.employe.employe;
import rh.model.session.userConnecter;
import rh.utils.ConnexionDB;

import java.io.File;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class profile implements Initializable {
    @FXML
    Label lbNomPrenom;
    @FXML Label lbMail;
    @FXML TextField tfNom;
    @FXML TextField tfPrenom;
    @FXML TextField tfEmail;
    @FXML TextField tfTel;
    @FXML TextField tfAdresse;
    @FXML TextField tfDtns;
    @FXML ImageView imageViewProfile;
    Connection conn = ConnexionDB.getConnection();

    @Override
    public void initialize(URL location , ResourceBundle resources ) {
        String user = userConnecter.getUsername();
        initializeEmploye();
        imageConnecter();
        System.out.println("Id connecter " + userConnecter.getId() );
        System.out.println("Utilisateur connecté : " + user );
    }
    public void initializeEmploye() {
        String sql = "SELECT * FROM employe WHERE id = ?";
        String compte = userConnecter.getId();

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
                    System.out.println("MDP 1 : " + mdp);
                    // Créer l'objet employe correctement
                    employe employeur = new employe(
                            id, nom, prenoms, telephone, email, adresse, date,
                            departement, poste , mdp , img , role
                    );
                    System.out.println("MDP 2 : " + employeur.getMotDePasse());
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
    public void imageConnecter(){
        String imagePath = null; // Variable pour stocker le chemin de l'image
        String sql_image = "SELECT img FROM employe WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql_image)) {
            String idConnecter = userConnecter.getId();
            stmt.setString(1, idConnecter);
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
                                imageViewProfile.setImage(image);

                            } catch (Exception e) {
                                // En cas d'erreur, affiche la pile d'exception pour le débogage
                                e.printStackTrace();
                            }

                        } catch (Exception e) {
                            // En cas d'erreur (ex: chemin invalide), on affiche l'image par défaut
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération de l'image : " + e.getMessage());
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
}
