package rh.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import rh.dao.Utilisateurdao;
import rh.model.UtilisateurModel;
import rh.utils.SessionManager;

public class LoginController implements Initializable {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label messageLabel;

    private Utilisateurdao Utilisateurdao;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Utilisateurdao = new Utilisateurdao(); // Correction du nom de la classe DAO
        updateStatus("Prêt à se connecter.");
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showWarning("Veuillez saisir un nom d'utilisateur et un mot de passe.");
            return;
        }

        try {
            UtilisateurModel utilisateurConnecte = Utilisateurdao.login(username, password);

            if (utilisateurConnecte != null) {
                showSuccess("Connexion réussie ! Bienvenue, " + utilisateurConnecte.getUsername() + ".");

                SessionManager.setCurrentUser(utilisateurConnecte);
                System.out.println("Utilisateur connecté : " + utilisateurConnecte.getUsername() +
                        ", Rôle : " + utilisateurConnecte.getRole() +
                        ", Employé ID : " + utilisateurConnecte.getEmployeId());

                redirectToDashboard(event);

            } else {
                showError("Nom d'utilisateur ou mot de passe incorrect.", "Veuillez vérifier vos identifiants.");
            }
        } catch (SQLException e) {
            showError("Erreur de base de données", "Impossible de se connecter. Veuillez réessayer plus tard.");
            e.printStackTrace();
        } catch (IOException e) {
            showError("Erreur d'interface", "Impossible de charger l'écran suivant.");
            e.printStackTrace();
        }
    }

    private void redirectToDashboard(ActionEvent event) throws IOException {
        UtilisateurModel currentUser = SessionManager.getCurrentUser();
        if (currentUser == null) {
            // Cela ne devrait normalement pas arriver juste après un login réussi
            showError("Erreur de session", "Aucun utilisateur connecté. Redirection vers l'écran de connexion.");
            // Optionnel : rediriger vers la page de login si cette situation inattendue se produit
             FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
             Parent root = loader.load();
             Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
             stage.setScene(new Scene(root));
             stage.setTitle("Connexion");
             stage.show();
            return;
        }
        //currentUser vient de SessionManager
        String role = currentUser.getRole();

        String fxmlPath = "/fxml/ErrorView.fxml"; // Valeur par défaut pour les rôles inconnus ou non gérés

        switch (role) {
            case "rh":
                fxmlPath = "/fxml/DashboardView.fxml";
                System.out.println("Redirection vers le tableau de bord RH.");
                break;
            case "employe":
                fxmlPath = "/fxml/Conge.fxml";
                System.out.println("Redirection vers le tableau de bord Employé.");
                break;
            default:
                // Le fxmlPath est déjà défini sur "/fxml/ErrorView.fxml"
                System.err.println("Rôle inconnu ou non géré: " + role + ". Redirection vers la page d'erreur.");
                break;
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();
        /**
         * Cette partie du code est essentielle pour faire passer l'id de l'employé connecté
         * instanceof:  opérateur Java qui vérifie si un objet est une instance d'une classe spécifique ou d'une sous-classe de celle-ci.
         *
         */
        Object controller = loader.getController();
        if (controller instanceof CongeController) {
            // Si le contrôleur est un CongeController (pour les employés)
            ((CongeController) controller).setEmployeMatricule(currentUser.getEmployeId());
            System.out.println("Matricule de l'employé passé au CongeController.");
        } else if (controller instanceof DashboardController) {
            //A ajuster parce que le dashboard sera different pour chaque rôle
            ((DashboardController) controller).setConnectedEmployeMatricule(currentUser.getEmployeId());
            System.out.println("Matricule de l'utilisateur passé au DashboardController.");
        } else if (fxmlPath.equals("/fxml/ErrorView.fxml") && controller instanceof ErrorController) {

            ((ErrorController) controller).setErrorMessage("Votre rôle '" + role + "' n'est pas configuré pour accéder à une interface spécifique.");
        }

        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("RHPlus - " + role.substring(0, 1).toUpperCase() + role.substring(1) + " Dashboard");
        stage.show();
    }

    private void updateStatus(String message) {
        messageLabel.setText(message);
    }

    private void showSuccess(String message) {
        messageLabel.setText(message);
    }

    private void showError(String title, String message) {
        messageLabel.setText("Erreur: " + message);
    }

    private void showWarning(String message) {
        messageLabel.setText("Attention: " + message);
    }
}