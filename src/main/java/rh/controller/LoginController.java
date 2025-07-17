package rh.controller;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class LoginController extends Application {  // Doit étendre Application
    private TextField usernameField;
    private PasswordField passwordField;
    private CheckBox rememberMeCheckBox;
    private ChoiceBox<String> roleChoiceBox;
    private Label errorLabel;
    
    // Simule une base de données d'utilisateurs
    private Map<String, User> users = new HashMap<>();
    
    @Override
    public void start(Stage primaryStage) {
        // Initialiser quelques utilisateurs de test
        initializeUsers();
        
        // Création des composants
        Label titleLabel = new Label("Login");
        titleLabel.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");
        
        usernameField = new TextField();
        usernameField.setPromptText("Ex: Jean");
        
        passwordField = new PasswordField();
        
        rememberMeCheckBox = new CheckBox("Remember Me");
        
        roleChoiceBox = new ChoiceBox<>();
        roleChoiceBox.getItems().addAll("RH", "Employer");
        roleChoiceBox.setValue("Employer"); // Valeur par défaut
        
        errorLabel = new Label("Invalid Login Please Try Again");
        errorLabel.setTextFill(Color.RED);
        errorLabel.setVisible(false);
        
        Button loginButton = new Button("Login");
        loginButton.setDefaultButton(true);
        loginButton.setOnAction(e -> handleLogin(primaryStage));
        
        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(e -> primaryStage.close());
        
        Hyperlink registerLink = new Hyperlink("Don't have an account? Register");
        
        // Organisation de la mise en page
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);
        root.getChildren().addAll(
            titleLabel,
            new Label("Username:"), usernameField,
            new Label("Password:"), passwordField,
            new Label("Role:"), roleChoiceBox,
            rememberMeCheckBox,
            errorLabel,
            loginButton, cancelButton,
            registerLink
        );
        
        // Charger les identifiants après avoir initialisé les champs
        loadRememberedCredentials();
        
        // Configuration de la scène
        Scene scene = new Scene(root, 350, 450);
        primaryStage.setTitle("Login System");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
    
    private void initializeUsers() {
        users.put("admin", new User("admin", "admin123", "RH"));
        users.put("jean", new User("jean", "password123", "Employer"));
    }
    
    private void loadRememberedCredentials() {
        try (BufferedReader reader = new BufferedReader(new FileReader("remembered_credentials.txt"))) {
            String username = reader.readLine();
            String password = reader.readLine();
            String role = reader.readLine();
            
            if (username != null && password != null && role != null) {
                usernameField.setText(username);
                passwordField.setText(password);
                roleChoiceBox.setValue(role);
                rememberMeCheckBox.setSelected(true);
            }
        } catch (IOException e) {
            // Fichier non trouvé ou erreur de lecture
        }
    }
    
    private void saveRememberedCredentials() {
        if (rememberMeCheckBox.isSelected()) {
            try (PrintWriter writer = new PrintWriter(new FileWriter("remembered_credentials.txt"))) {
                writer.println(usernameField.getText());
                writer.println(passwordField.getText());
                writer.println(roleChoiceBox.getValue());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            new File("remembered_credentials.txt").delete();
        }
    }
    
    private void handleLogin(Stage primaryStage) {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String role = roleChoiceBox.getValue();
        
        if (authenticate(username, password, role)) {
            saveRememberedCredentials();
            errorLabel.setVisible(false);
            
            if ("RH".equals(role)) {
                openRHInterface();
            } else {
                openEmployerInterface();
            }
            primaryStage.close();
        } else {
            errorLabel.setVisible(true);
        }
    }
    
    private boolean authenticate(String username, String password, String role) {
        User user = users.get(username);
        return user != null && user.getPassword().equals(password) && user.getRole().equals(role);
    }
    
    private void openRHInterface() {
        Stage rhStage = new Stage();
        Label label = new Label("Bienvenue dans l'interface RH");
        VBox root = new VBox(label);
        Scene scene = new Scene(root, 400, 300);
        rhStage.setScene(scene);
        rhStage.setTitle("Interface RH");
        rhStage.show();
    }
    
    private void openEmployerInterface() {
        Stage empStage = new Stage();
        Label label = new Label("Bienvenue dans l'interface Employé");
        VBox root = new VBox(label);
        Scene scene = new Scene(root, 400, 300);
        empStage.setScene(scene);
        empStage.setTitle("Interface Employé");
        empStage.show();
    }
    
    private void showRegistrationForm() {
        // Implémentation à compléter
    }
    
    public static void main(String[] args) {
        launch(args);
    }
    
    private static class User {
        private final String username;
        private final String password;
        private final String role;
        
        public User(String username, String password, String role) {
            this.username = username;
            this.password = password;
            this.role = role;
        }
        
        public String getPassword() { return password; }
        public String getRole() { return role; }
    }
}