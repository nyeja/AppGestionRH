package rh.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class LoginController {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private CheckBox rememberMeCheckBox;
    @FXML
    private ChoiceBox<String> roleChoiceBox;
    @FXML
    private Label errorLabel;

    // Simule une base de données d'utilisateurs
    private final Map<String, User> users = new HashMap<>();

    @FXML
public void initialize() {
    System.out.println("Initialisation du contrôleur...");
    System.out.println("roleChoiceBox est null ? " + (roleChoiceBox == null));
    
    // Initialisation des utilisateurs
    users.put("admin", new User("admin", "admin123", "RH"));
    users.put("jean", new User("jean", "password123", "Employer"));

    if (roleChoiceBox != null) {
        // Configuration du ChoiceBox
        roleChoiceBox.getItems().addAll("RH", "Employer");
        roleChoiceBox.setValue("Employer");
    } else {
        System.err.println("ERREUR: roleChoiceBox n'a pas été injecté par FXMLLoader !");
    }

    // Charger les identifiants enregistrés
    loadRememberedCredentials();
}

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String role = roleChoiceBox.getValue();

        if (authenticate(username, password, role)) {
            saveRememberedCredentials();
            errorLabel.setVisible(false);
            openInterface(role);
            ((Stage) usernameField.getScene().getWindow()).close();
        } else {
            errorLabel.setText("Invalid Login Please Try Again");
            errorLabel.setTextFill(Color.RED);
            errorLabel.setVisible(true);
        }
    }

    @FXML
    public void EXIT(ActionEvent event) {
        System.exit(0);
    }

    private void openInterface(String role) {
        Stage stage = new Stage();
        Label label = new Label("Bienvenue dans l'interface " + role);
        VBox root = new VBox(label);
        javafx.scene.Scene scene = new javafx.scene.Scene(root, 400, 300);
        stage.setScene(scene);
        stage.setTitle("Interface " + role);
        stage.show();
    }

    private boolean authenticate(String username, String password, String role) {
        User user = users.get(username);
        return user != null && user.getPassword().equals(password) && user.getRole().equals(role);
    }

    private void loadRememberedCredentials() {
        File file = new File("remembered_credentials.txt");
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                usernameField.setText(reader.readLine());
                passwordField.setText(reader.readLine());
                roleChoiceBox.setValue(reader.readLine());
                rememberMeCheckBox.setSelected(true);
            } catch (IOException e) {
                // Fichier corrompu ou erreur de lecture
                file.delete();
            }
        }
    }

    private void saveRememberedCredentials() {
        File file = new File("remembered_credentials.txt");
        if (rememberMeCheckBox.isSelected()) {
            try (PrintWriter writer = new PrintWriter(file)) {
                writer.println(usernameField.getText());
                writer.println(passwordField.getText());
                writer.println(roleChoiceBox.getValue());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            file.delete();
        }
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

        public String getPassword() {
            return password;
        }

        public String getRole() {
            return role;
        }
    }
}