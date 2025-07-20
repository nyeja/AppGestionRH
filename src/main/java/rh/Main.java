package rh;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    
    @Override
    public void start(Stage primaryStage) throws Exception {

        try{
            //Parent root = FXMLLoader.load(getClass().getResource("/fxml/poste/poste.fxml"));
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/poste/poste.fxml"));
            primaryStage.setScene(new Scene(root));
            primaryStage.setTitle("Gestion des Postes");
            //primaryStage.setResizable(false);
            primaryStage.show();
        } catch (Exception e) {
            System.out.println("erreur : " + e);
        }


    }

    public static void main(String[] args) {
        launch(args);
    }
}