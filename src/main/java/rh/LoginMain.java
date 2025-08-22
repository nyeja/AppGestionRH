package rh;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Dialog;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class    LoginMain extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login/loginPro.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);

        // Enlever les boutons natifs
        stage.initStyle(StageStyle.UNDECORATED);

        // Charger le CSS
        scene.getStylesheets().add(getClass().getResource("/css/login.css").toExternalForm());

        stage.setScene(scene);
        stage.setTitle("Formulaire de connexion");
        stage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}