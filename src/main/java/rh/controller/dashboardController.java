package rh.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.StackPane;

import java.io.IOException;


public class dashboardController {
    @FXML private StackPane mainContentDepartement;

    @FXML private MenuItem itemPoste;

    @FXML private MenuItem itemEmployer;

    @FXML private MenuItem itemDepartement;

    @FXML private StackPane stackPaneHome;

    @FXML private StackPane stackPaneDepartement;

    @FXML
    private void afficherAcceuil(){
        stackPaneHome.setVisible(true);
    }

    @FXML
    private  void infoPoste(){
        try {
            System.out.println("information sur poste");
        }catch (Exception e){
            System.out.println("voici l'erreur : " + e);
        }
    }
    @FXML
    private  void infoEmployer(){
        try {
            System.out.println("information sur EMployer");
        }catch (Exception e){
            System.out.println("voici l'erreur : " + e);
        }
    }
    @FXML
    private  void infoDepartement(){
        try {
            stackPaneDepartement.setVisible(true);
        }catch (Exception e){
            System.out.println("voici l'erreur : " + e);
        }
    }

    @FXML
    private void loadDepartementView() throws IOException {
        // Charge le fichier FXML du module "Département"
        Parent node = FXMLLoader.load(getClass().getResource("/fxml/departement/dpm.fxml"));
        // Affiche ce module dans le StackPane principal
        mainContentDepartement.getChildren().setAll(node);

    }

}
