package rh;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TitledPane;
import javafx.stage.Stage;
import javafx.scene.Scene.*;
import javafx.scene.layout.TilePane;

public class calendrier  extends Application{

    public void start(Stage primaryStage){
        TabPane menu = new TabPane();

        menu.getTabs().add(new Tab("Année", getTilePaneAnnee()));
        menu.getTabs().add(new Tab("Mois", getTilePaneMois()));
        menu.getTabs().add(new Tab("Jour", getTilePaneJour()));
        menu.getTabs().add(new Tab("Heure", getTilePaneHeure()));
        menu.getTabs().add(new Tab("Minute", getTilePaneMinute()));

        menu.getTabs().forEach(tab -> tab.setClosable(false));

        Scene scene = new Scene(menu , 500,500);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Calendrier");
        primaryStage.show();

    }
    private TilePane getTilePaneMinute() {
        TilePane tile = new TilePane(10, 10);
        tile.setPadding(new Insets(10));
        tile.setPrefColumns(4);
        for (int i = 1 ; i <= 60 ; i++) {
            tile.getChildren().add(new Button(String.valueOf(i)));
        }
        return tile;
    }
    private TilePane getTilePaneHeure(){
        TilePane tile = new TilePane(10,10);
        tile.setPadding(new Insets(10));
        tile.setPrefColumns(4);
        for (int i = 1 ; i <= 24 ; i++) {
            tile.getChildren().add(new Button(String.valueOf(i)));
        }
        return tile;
    }
    private  TilePane getTilePaneJour(){
        TilePane tile = new TilePane(10,10);
        tile.setPadding(new Insets(10));
        tile.setPrefColumns(4);
        for (int i = 1 ; i <= 31 ; i++) {
            tile.getChildren().add(new Button(String.valueOf(i)));
        }
        return tile;
    }

    private  TilePane getTilePaneMois(){
        TilePane tile = new TilePane(10,10);
        tile.setPadding(new Insets(10));
        tile.setPrefColumns(4);
        String[] nomsMois = {
                "Janvier", "Février", "Mars", "Avril", "Mai", "Juin",
                "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre"
        };


        for (int i = 0 ; i < nomsMois.length; i++) {
            Button btn = new Button(nomsMois[i]);

            tile.getChildren().add(btn);
        }

        return tile;
    }

    private  TilePane getTilePaneAnnee(){
        TilePane tile = new TilePane(10,10);
        tile.setPadding(new Insets(10));
        tile.setPrefColumns(4);
        for (int i = 1960 ; i <= 2030 ; i++) {
            tile.getChildren().add(new Button(String.valueOf(i)));
        }
        return tile;
    }
    public static void main(String[] args) {
        launch(args);
    }
}
