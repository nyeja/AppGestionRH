package rh; // Assurez-vous que le package correspond à celui de votre Main.java

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Charge la vue de connexion comme première interface
            // Assurez-vous que le chemin vers Login.fxml est correct
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);

            primaryStage.setScene(scene);
            primaryStage.setTitle("RHPlus - Connexion"); // Titre de la fenêtre de connexion
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement de la vue de connexion : " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args); // Lance l'application JavaFX
    }
}