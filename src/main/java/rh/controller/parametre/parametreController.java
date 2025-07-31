package rh.controller.parametre;

import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import rh.model.employe.employe;
import rh.model.session.userConnecter;
import rh.utils.ConnexionDB;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;


public class parametreController {

    @FXML StackPane StackpaneMain;


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
