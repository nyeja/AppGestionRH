package rh.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.io.*;
import javafx.fxml.FXMLLoader;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;


import rh.dao.EmployerDAO;
import rh.model.Employer;

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
    private Label AllertMessage;

    @FXML
    public void initialize() {
        System.out.println("Initialisation du contrôleur...");
        System.out.println("roleChoiceBox est null ? " + (roleChoiceBox == null));

        if (roleChoiceBox != null) {
            roleChoiceBox.getItems().addAll("RH", "Employer");
            roleChoiceBox.setValue("Employer");
        } else {
            System.err.println("ERREUR: roleChoiceBox n'a pas été injecté par FXMLLoader !");
        }

        loadRememberedCredentials();
    }

    @FXML
private void handleLogin(ActionEvent event) {
    String username = usernameField.getText();
    String password = passwordField.getText();
    String role = roleChoiceBox.getValue();

    System.out.println("Saisie utilisateur : " + username + ", " + password + ", " + role);

    EmployerDAO employerDAO = new EmployerDAO();
    Employer user = employerDAO.trouverParUsername(username);

    if (user != null) {
        System.out.println("Utilisateur DB : " + user.getUsername() + ", " + user.getPassword() + ", " + user.getRole());
    } else {
        System.out.println("Aucun utilisateur trouvé avec ce username.");
    }

    if (user != null && user.getUsername().equals(username)
            && user.getPassword().equals(password)
            && user.getRole().equalsIgnoreCase(role)) {

        saveRememberedCredentials();
        AllertMessage.setVisible(false);
        openInterface(role);
        ((Stage) usernameField.getScene().getWindow()).close();

    } else {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur de connexion");
        alert.setHeaderText("Identifiants incorrects");
        alert.setContentText("Nom d'utilisateur, mot de passe ou rôle invalide.");
        alert.showAndWait();

        AllertMessage.setTextFill(Color.RED);
        AllertMessage.setVisible(true);
    }
}

    @FXML
    public void EXIT(ActionEvent event) {
        System.exit(0);
    }

    private void openInterface(String role) {
    try {
        String fxmlPath = "";

        if (role.equalsIgnoreCase("RH")) {
            fxmlPath = "/rh/fxml/RH/RH.fxml";
        } else if (role.equalsIgnoreCase("Employer")) {
            fxmlPath = "/rh/fxml/Employer/Employer.fxml"; // à créer plus tard si besoin
        }
       if (!fxmlPath.isEmpty()) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Interface " + role);
            stage.show();
        } else {
            System.err.println("Aucune interface définie pour le rôle : " + role);
        }

    } catch (IOException e) {
        e.printStackTrace();
    }
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
                file.delete();
            }
        }
    }

    private void saveRememberedCredentials() {
        if (rememberMeCheckBox == null || usernameField == null || passwordField == null || roleChoiceBox == null) {
            System.err.println("Un ou plusieurs champs ne sont pas initialisés !");
            return;
        }

        File file = new File("remembered_credentials.txt");
        try {
            if (rememberMeCheckBox.isSelected()) {
                try (PrintWriter writer = new PrintWriter(file)) {
                    writer.println(usernameField.getText());
                    writer.println(passwordField.getText());
                    writer.println(roleChoiceBox.getValue());
                }
            } else {
                if (file.exists()) {
                    file.delete();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleForgotPassword() {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/rh/fxml/Password/Forgot_password.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setTitle("Forgot Password");
        stage.setScene(new Scene(root));
        stage.show();
    } catch (IOException e) {
        e.printStackTrace();
    }
}

}
