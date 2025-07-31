package rh;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class    LoginMain extends Application {
    //OK
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Charge le FXML depuis le bon chemin
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/Login/Login.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Login System");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}