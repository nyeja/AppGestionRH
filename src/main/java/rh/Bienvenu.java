package rh;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Bienvenu extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("My First JavaFX App");

        BorderPane root = new BorderPane();
        Button btnHello = new Button("Hello World");

        root.setCenter(btnHello);

        Scene scene = new Scene(root, 250, 100);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}