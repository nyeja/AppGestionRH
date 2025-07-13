package rh;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.net.URL;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        URL fxmlLocation = getClass().getResource("/fxml/employee-management.fxml");
        if (fxmlLocation == null) {
            throw new IllegalStateException("Le fichier FXML est introuvable !");
        }

        Parent root = FXMLLoader.load(fxmlLocation);
        Scene scene = new Scene(root);

        // Charger le CSS
        URL cssLocation = getClass().getResource("/employee-styles.css");
        if (cssLocation != null) {
            scene.getStylesheets().add(cssLocation.toExternalForm());
        }

        stage.setScene(scene);
        stage.setTitle("RHPlus - Gestion des employés");
        stage.setMinWidth(1100);
        stage.setMinHeight(700);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
