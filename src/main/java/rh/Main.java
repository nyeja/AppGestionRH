package rh;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.scene.image.Image;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/dashboard/dashboard.fxml"));
        //Parent root = FXMLLoader.load(getClass().getResource("/fxml/poste/poste.fxml"));
        //Parent root = FXMLLoader.load(getClass().("/fxml/employe/employe.fxml"));
        //Parent root = FXMLLoader.load(getClass().getResource("/fxml/presence/presence.fxml"));
        //Parent root = FXMLLoader.load(getClass().getResource("/fxml/departement/dpm.fxml"));
        primaryStage.setScene(new Scene(root));
        // Changer le logo de l'application
        // primaryStage.getIcons().add(new Image(getClass().getgetResourceResourceAsStream("D:/Local Disk D_112020241629/Etude/L3/projet/projet tuto 24-25/AppGestionRH/")));

        primaryStage.setTitle("Département/dashboard");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
