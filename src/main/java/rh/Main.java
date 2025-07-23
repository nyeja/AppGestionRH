package rh;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        
        
         // Charge le FXML depuis le bon chemin
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/rh/fxml/Login/Login.fxml"));
        Parent root = loader.load();
        
        Scene scene = new Scene(root);
        primaryStage.setTitle("Login");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false); // Pour respecter tes dimensions fixes
        primaryStage.show();/**/
        
        
        /* Charge le FXML depuis resources/rh/fxml/post/post.fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/rh/fxml/post/post.fxml"));
        Parent root = loader.load();
        
        Scene scene = new Scene(root);
        primaryStage.setTitle("Gestion des Postes");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false); // Pour respecter tes dimensions fixes
        primaryStage.show();*/
    }

    public static void main(String[] args) {
        launch(args);
    }
}