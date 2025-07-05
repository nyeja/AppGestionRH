package rh;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class Mainlayoutexemple extends Application {

    @Override
    public void start(Stage primaryStage) {
        TabPane tabPane = new TabPane();

        tabPane.getTabs().add(new Tab("HBox", getHBox()));
        tabPane.getTabs().add(new Tab("VBox", getVBox()));
        tabPane.getTabs().add(new Tab("GridPane", getGridPane()));
        tabPane.getTabs().add(new Tab("BorderPane", getBorderPane()));
        tabPane.getTabs().add(new Tab("StackPane", getStackPane()));
        tabPane.getTabs().add(new Tab("FlowPane", getFlowPane()));
        tabPane.getTabs().add(new Tab("TilePane", getTilePane()));
        tabPane.getTabs().add(new Tab("AnchorPane", getAnchorPane()));
        tabPane.getTabs().add(new Tab("Pane", getPane()));

        tabPane.getTabs().forEach(tab -> tab.setClosable(false));

        Scene scene = new Scene(tabPane, 700, 400);
        scene.getStylesheets().add(getClass().getResource("/fxml/dashboard/application.css").toExternalForm());
        primaryStage.setTitle("JavaFX Layouts Demo");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private HBox getHBox() {
        HBox hbox = new HBox(10);
        hbox.setId("hboxId");
        hbox.setPadding(new Insets(15));
        hbox.setAlignment(Pos.CENTER);
        Button btnGauche = new Button("Gauche");
        Button btnMillieu = new Button("Milieu");
        Button btnDroite = new Button("Droite");
        hbox.getChildren().addAll(btnGauche, btnMillieu ,btnDroite );
        btnGauche.setId("btnGauche");
        btnMillieu.setId("btnMillieu");
        btnDroite.setId("btnDroite");
        btnGauche.setOnAction(e->{
            btnGauche.setText("OK Gauche");
            btnMillieu.setText("Millieu");
            btnDroite.setText("Droite");
        });
        btnMillieu.setOnAction(e-> {
            btnGauche.setText("Gauche");
            btnMillieu.setText("OK Millieu");
            btnDroite.setText("Droite");
        });
        btnDroite.setOnAction(e-> {
            btnGauche.setText("Gauche");
            btnMillieu.setText("Millieu");
            btnDroite.setText("OK Droite");
        });
        return hbox;
    }

    private VBox getVBox() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(15));
        vbox.setAlignment(Pos.CENTER);
        Label nom = new Label("Nom");
        TextField tfNom = new TextField();
        Label Email = new Label("Email");
        TextField tfEmail = new TextField();
        vbox.getChildren().addAll(nom , tfNom , Email,  tfEmail);

        nom.setId("nom");
        Email.setId("email");
        tfNom.setId("tfNom");
        tfEmail.setId("tfEmail");

        return vbox;
    }

    private GridPane getGridPane() {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(15));
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);

        grid.add(new Label("Nom:"), 0, 0);
        grid.add(new TextField(), 1, 0);
        grid.add(new Label("Mot de passe:"), 0, 1);
        grid.add(new PasswordField(), 1, 1);

        return grid;
    }

    private BorderPane getBorderPane() {
        BorderPane border = new BorderPane();
        border.setPadding(new Insets(10));
        border.setTop(new Label("Haut"));
        border.setLeft(new Button("Gauche"));
        border.setRight(new Button("Droite"));
        border.setBottom(new Label("Bas"));
        border.setCenter(new TextArea("Centre"));
        return border;
    }

    private StackPane getStackPane() {
        StackPane stack = new StackPane();
        stack.setPadding(new Insets(10));
        Rectangle fond = new Rectangle(200, 150, Color.LIGHTBLUE);
        Label label = new Label("Superposé");
        stack.getChildren().addAll(fond, label);
        return stack;
    }

    private FlowPane getFlowPane() {
        FlowPane flow = new FlowPane(10, 10);
        flow.setPadding(new Insets(10));
        flow.setAlignment(Pos.CENTER);
        for (int i = 1; i <= 10; i++) {
            flow.getChildren().add(new Button("Bouton " + i));
        }
        return flow;
    }

    private TilePane getTilePane() {
        TilePane tile = new TilePane(10, 10);
        tile.setPadding(new Insets(10));
        tile.setPrefColumns(4);
        for (char c = 'A'; c <= 'H'; c++) {
            tile.getChildren().add(new Button(String.valueOf(c)));
        }
        return tile;
    }

    private AnchorPane getAnchorPane() {
        AnchorPane anchor = new AnchorPane();
        Button btn = new Button("Ancré");
        AnchorPane.setTopAnchor(btn, 20.0);
        AnchorPane.setRightAnchor(btn, 20.0);
        anchor.getChildren().add(btn);
        return anchor;
    }

    private Pane getPane() {
        Pane pane = new Pane();
        Button btn = new Button("Positionné");
        btn.setLayoutX(120);
        btn.setLayoutY(80);
        pane.getChildren().add(btn);
        return pane;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
