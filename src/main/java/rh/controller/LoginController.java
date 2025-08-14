package rh.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import rh.utils.ConnexionDB;
import rh.model.User;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label messageLabel;

    /**
     * Gère l'action de connexion de l'utilisateur.
     * Cette méthode est déclenchée lors du clic sur le bouton de connexion.
     * Elle vérifie les identifiants saisis par l'utilisateur par rapport à la base de données.
     * En cas de succès, elle redirige l'utilisateur vers la vue appropriée en fonction de son rôle.
     * En cas d'échec, elle affiche un message d'erreur.
     *
     * @param event L'événement d'action qui a déclenché cette méthode.
     */
    @FXML
    public void handleLogin(ActionEvent event) {
        // Enlève les espaces de début et de fin
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        // Validation basique des champs
        if (email.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Veuillez saisir votre email et votre mot de passe.");
            return;
        }

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = ConnexionDB.getConnection();
            if (conn == null) {
                messageLabel.setText("Erreur de connexion à la base de données.");
                System.err.println("Le code de connexion a renvoyé null.");
                return;
            }

            // Requête SQL pour vérifier les informations de connexion
            // Dans ce cas, nous assumons que le mot de passe est stocké en clair.
            String sql = "SELECT idUtilisateur, email, password, employe_id, role FROM UTILISATEUR WHERE email = ?";
            System.out.println("Exécution de la requête : " + sql);
            System.out.println("Avec email saisi: '" + email + "'");

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                // Un utilisateur avec cet email a été trouvé
                String storedPassword = rs.getString("password").trim();
                String storedRole = rs.getString("role").trim(); // Trim le rôle aussi
                String employeId = rs.getString("employe_id");


                if (password.equals(storedPassword)) {
                    // Connexion réussie
                    int id = rs.getInt("idUtilisateur");
                    User user = new User(id, email, employeId, storedRole);
                    messageLabel.setText("Connexion réussie ! Redirection en cours...");
                    redirectToView(event, user.getRole(), user.getEmployeId());
                } else {
                    // Mot de passe incorrect
                    messageLabel.setText("Email ou mot de passe incorrect.");
                    System.err.println("Le mot de passe saisi ne correspond pas à celui de la BDD.");
                }

            } else {
                // Connexion échouée
                messageLabel.setText("Email ou mot de passe incorrect.");
                System.err.println("Échec de la connexion. Aucun utilisateur trouvé pour cet email.");
            }

        } catch (SQLException e) {
            messageLabel.setText("Une erreur s'est produite lors de la connexion.");
            System.err.println("Erreur SQL : " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de la vue FXML : " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Fermeture de la connexion
            ConnexionDB.closeConnection();
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Charge et affiche une nouvelle vue FXML en fonction du rôle de l'utilisateur connecté.
     * Cette méthode gère la logique de navigation post-connexion.
     * Le matricule de l'employé est passé au contrôleur de la nouvelle vue si nécessaire.
     *
     * @param event L'événement d'action pour obtenir la fenêtre courante.
     * @param role Le rôle de l'utilisateur (par exemple "employe", "rh", "admin").
     * @param matriculeEmploye Le matricule de l'employé associé à l'utilisateur connecté.
     */
    private void redirectToView(ActionEvent event, String role, String matriculeEmploye) throws IOException {
        String fxmlFile = "";
        String title = "";

        // Logique de redirection
        switch (role.toLowerCase()) {
            case "employe":
                fxmlFile = "/fxml/Conge.fxml";
                title = "Tableau de bord Employé";
                break;
            case "rh":
                fxmlFile = "/fxml/DashboardView.fxml";
                title = "Tableau de bord Manager";
                break;
            case "admin":
                fxmlFile = "/fxml/Conge.fxml";
                title = "Tableau de bord Admin";
                break;
            default:
                fxmlFile = "/fxml/DashboardView.fxml";
                title = "Tableau de bord";
                break;
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
        Parent root = loader.load();

        // Récupérer le contrôleur de la nouvelle scène
        Object controller = loader.getController();

        // Si le contrôleur est de type DashboardController ou CongeController, on lui passe le matricule.
        if (controller instanceof DashboardController) {
            DashboardController dashboardController = (DashboardController) controller;
            dashboardController.setConnectedEmployeMatricule(matriculeEmploye);
        } else if (controller instanceof CongeController) {
            CongeController congeController = (CongeController) controller;
            congeController.setEmployeMatricule(matriculeEmploye);
        }

        // Obtenir la scène et la fenêtre actuelle
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // Créer une nouvelle scène avec la nouvelle vue
        Scene scene = new Scene(root);

        // Mettre à jour la fenêtre
        stage.setScene(scene);
        stage.setTitle(title);
        stage.show();
    }

    /**
     * Gère la redirection vers la page de création d'utilisateur.
     * Cette méthode est liée à un bouton "Créer un compte" ou similaire.
     * Elle charge la vue FXML pour la création d'un nouvel utilisateur et l'affiche.
     *
     * @param event L'événement d'action.
     */
    @FXML
    public void handleCreateUserRedirect(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/UserCreationView.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Créer un nouvel utilisateur");
            stage.show();
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de la vue de création d'utilisateur : " + e.getMessage());
            e.printStackTrace();
            messageLabel.setText("Impossible de charger la page de création d'utilisateur.");
        }
    }
}