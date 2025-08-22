package rh.controller;

import rh.model.session.userConnecter;

import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import rh.utils.ConnexionDB;

import javafx.scene.image.Image;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import rh.model.session.userConnecter;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class dashboardController {
    private String connectedEmployeMatricule;

    Connection conn = ConnexionDB.getConnection();

    @FXML private StackPane mainContentDepartement;

    @FXML private MenuItem itemPoste;

    @FXML private MenuItem itemEmployer;

    @FXML private MenuItem itemDepartement;

    @FXML private ImageView ImageProfile;

    @FXML private StackPane stackPaneHome;

    @FXML private StackPane stackPaneDepartement;

    @FXML private Button btnEmployer;
    @FXML private Button btnDepartement;
    @FXML private Button btnPoste;

    @FXML
    private void initialize(){
        stackPaneDepartement.getChildren().setAll(stackPaneHome);
        stackPaneHome.setVisible(true);
        imageConnecter();

        String role = userConnecter.getRole();
        System.out.println("Role :  " + role );
        if (role.equals("Employer")){
            btnEmployer.setVisible(false);
            btnDepartement.setVisible(false);
            btnPoste.setVisible(false);
        }


    }
    @FXML
    private void afficherAcceuil(){
        stackPaneDepartement.getChildren().setAll(stackPaneHome);
        stackPaneHome.setVisible(true);
    }
    //Définit le matricule de l'employé connecté
    private void setConnectedEmployeMatricule(String id){
        this.connectedEmployeMatricule = id;
    }
    @FXML
    private void loadDepartementView() throws IOException {
        // Charge le fichier FXML du module "Département"
        Parent node = FXMLLoader.load(getClass().getResource("/fxml/departement/dpm.fxml"));
        // Affiche ce module dans le StackPane principal
        stackPaneDepartement.getChildren().setAll(node);

    }

    @FXML
    private  void loadEmployer() throws IOException{
        Parent node = FXMLLoader.load(getClass().getResource("/fxml/employe/employe.fxml"));
        stackPaneDepartement.getChildren().setAll(node);
    }

    @FXML
    private  void loadPoste() throws IOException{
        Parent node = FXMLLoader.load(getClass().getResource("/fxml/poste/poste.fxml"));
        stackPaneDepartement.getChildren().setAll(node);
    }

    @FXML
    private void loadPresence() throws IOException{
        Parent node = FXMLLoader.load(getClass().getResource("/fxml/presence/presence.fxml"));
        stackPaneDepartement.getChildren().setAll(node);
    }

    @FXML
    private void loadParametre() throws IOException{
        Parent node = FXMLLoader.load(getClass().getResource("/fxml/parametre/parametre.fxml"));
        stackPaneDepartement.getChildren().setAll(node);
    }

    @FXML
    private void deconnexion(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/Login/Login.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Connexion");
            stage.setScene(new Scene(root));
            stage.show();

            // Fermer la fenêtre actuelle
            ((Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow()).close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private  void loadConge() throws IOException{
        Parent node = FXMLLoader.load(getClass().getResource("/fxml/Conge.fxml"));
        stackPaneDepartement.getChildren().setAll(node);
    }
    @FXML
    private  void loadCongeAdmin() throws IOException{
        Parent node = FXMLLoader.load(getClass().getResource("/fxml/rh_conge_view.fxml"));
        stackPaneDepartement.getChildren().setAll(node);
    }
    @FXML
    private  void loadDemandePermission() throws IOException{
        Parent node = FXMLLoader.load(getClass().getResource("/fxml/employe_permission_view.fxml"));
        stackPaneDepartement.getChildren().setAll(node);
    }
    @FXML
    private  void loadPermissionAdmin() throws IOException{
        Parent node = FXMLLoader.load(getClass().getResource("/fxml/rh_permission_view.fxml"));
        stackPaneDepartement.getChildren().setAll(node);
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
                                ImageProfile.setImage(image);

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

}
