package rh;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Mainexemple extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            BorderPane root = new BorderPane();
            Scene scene = new Scene(root, 400, 400);
            scene.getStylesheets().add(getClass().getResource("/fxml/dashboard/application.css").toExternalForm());


            primaryStage.setScene(scene);
            primaryStage.setTitle("Premiere Fenetre");
            //primaryStage.setResizable(false);

            primaryStage.setX(300);
            primaryStage.setY(10);

            primaryStage.setWidth(500);
            primaryStage.setHeight(600);
            primaryStage.show();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}