package rh;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class Mainlayoutavance extends Application {

    @Override
    public void start(Stage primaryStage) {
        TabPane mainTabs = new TabPane();

        mainTabs.getTabs().add(new Tab("TabPane", createTabPaneDemo()));
        mainTabs.getTabs().add(new Tab("Accordion", createAccordionDemo()));
        mainTabs.getTabs().add(new Tab("TitledPane", createTitledPaneDemo()));
        mainTabs.getTabs().add(new Tab("SplitPane", createSplitPaneDemo()));
        mainTabs.getTabs().add(new Tab("StackPane", createStackPaneDemo()));
        mainTabs.getTabs().add(new Tab("MenuBar", createMenuBarDemo()));
        mainTabs.getTabs().add(new Tab("ListView", createListViewDemo()));

        mainTabs.getTabs().forEach(tab -> tab.setClosable(false));

        Scene scene = new Scene(mainTabs, 600, 400);
        primaryStage.setTitle("JavaFX - Composants Similaires à TabPane");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private TabPane createTabPaneDemo() {
        TabPane tabPane = new TabPane();
        tabPane.getTabs().addAll(
                new Tab("Vue 1", new Label("Contenu 1")),
                new Tab("Vue 2", new Label("Contenu 2"))
        );
        return tabPane;
    }

    private Accordion createAccordionDemo() {
        TitledPane p1 = new TitledPane("Section 1", new Label("Contenu 1"));
        TitledPane p2 = new TitledPane("Section 2", new Label("Contenu 2"));
        Accordion accordion = new Accordion(p1, p2);
        return accordion;
    }

    private TitledPane createTitledPaneDemo() {
        VBox content = new VBox(new Label("Option A"), new CheckBox("Activer"));
        return new TitledPane("Options avancées", content);
    }

    private SplitPane createSplitPaneDemo() {
        Label gauche = new Label("Zone de gauche");
        Label droite = new Label("Zone de droite");

        SplitPane splitPane = new SplitPane();
        splitPane.getItems().addAll(gauche, droite);
        splitPane.setDividerPositions(0.5);
        return splitPane;
    }

    private StackPane createStackPaneDemo() {
        StackPane stack = new StackPane();
        Rectangle fond = new Rectangle(300, 200, Color.LIGHTGRAY);
        Label label = new Label("Vue actuelle");

        stack.getChildren().addAll(fond, label);
        return stack;
    }

    private VBox createMenuBarDemo() {
        MenuBar menuBar = new MenuBar();
        Menu fichier = new Menu("Fichier");
        MenuItem nouveau = new MenuItem("Nouveau");
        fichier.getItems().add(nouveau);

        Menu aide = new Menu("Aide");
        MenuItem apropos = new MenuItem("À propos");
        aide.getItems().add(apropos);

        menuBar.getMenus().addAll(fichier, aide);

        Label info = new Label("Sélectionnez un menu...");

        nouveau.setOnAction(e -> info.setText("Nouveau sélectionné"));
        apropos.setOnAction(e -> info.setText("À propos sélectionné"));

        VBox layout = new VBox(menuBar, info);
        return layout;
    }

    private VBox createListViewDemo() {
        ListView<String> listView = new ListView<>();
        listView.getItems().addAll("Vue A", "Vue B", "Vue C");

        Label label = new Label("Aucune sélection");
        listView.getSelectionModel().selectedItemProperty().addListener(
                (ChangeListener<String>) (obs, oldVal, newVal) -> label.setText("Contenu de : " + newVal)
        );

        VBox vbox = new VBox(10, listView, label);
        vbox.setPrefHeight(300);
        return vbox;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
