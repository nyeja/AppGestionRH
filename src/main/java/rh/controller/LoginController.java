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

/**
 * Contrôleur FXML pour l'interface de connexion.
 * Gère la validation des identifiants utilisateur et la redirection vers le tableau de bord
 * approprié en fonction du rôle de l'utilisateur.
 *
 * @author Votre Nom
 * @version 1.0
 * @since 2025-08-12
 */
public class LoginController implements Initializable {

    // --- Composants FXML ---
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label messageLabel;

    // --- Dépendances ---
    private Utilisateurdao utilisateurDao;

    /**
     * Méthode d'initialisation du contrôleur.
     * Configure le DAO pour l'accès aux données des utilisateurs.
     *
     * @param location L'emplacement utilisé pour résoudre les chemins relatifs.
     * @param resources Les ressources utilisées pour la localisation.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        utilisateurDao = new Utilisateurdao();
        updateStatus("Prêt à se connecter.");
    }

    /**
     * Gère l'événement de connexion (clic sur le bouton de connexion).
     * Valide les champs, tente de connecter l'utilisateur et gère la redirection.
     *
     * @param event L'événement d'action.
     */
    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showWarning("Veuillez saisir un nom d'utilisateur et un mot de passe.");
            return;
        }

        try {
            UtilisateurModel utilisateurConnecte = utilisateurDao.login(username, password);

            if (utilisateurConnecte != null) {
                showSuccess("Connexion réussie ! Bienvenue, " + utilisateurConnecte.getUsername() + ".");

                // Enregistrement de l'utilisateur dans la session
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

    /**
     * Redirige l'utilisateur vers le tableau de bord approprié en fonction de son rôle.
     *
     * @param event L'événement d'action pour accéder à l'objet Stage.
     * @throws IOException Si le fichier FXML de la vue de destination n'est pas trouvé.
     */
    private void redirectToDashboard(ActionEvent event) throws IOException {
        UtilisateurModel currentUser = SessionManager.getCurrentUser();
        // Vérification de la session
        if (currentUser == null) {
            showError("Erreur de session", "Aucun utilisateur connecté. Redirection vers l'écran de connexion.");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Connexion");
            stage.show();
            return;
        }

        String role = currentUser.getRole();
        String fxmlPath = null;
        String windowTitle = "RHPlus - ";

        // Détermination du chemin FXML et du titre en fonction du rôle
        switch (role) {
            case "rh":
                fxmlPath = "/fxml/DashboardView.fxml";
                windowTitle += "Tableau de bord RH";
                System.out.println("Redirection vers le tableau de bord RH.");
                break;
            case "employe":
                // Utilisation d'une vue de tableau de bord dédiée pour l'employé serait une meilleure pratique
                fxmlPath = "/fxml/DashboardEmployeView.fxml"; // Exemple de nom pour une vue employe dédiée
                windowTitle += "Tableau de bord Employé";
                System.out.println("Redirection vers le tableau de bord Employé.");
                break;
            default:
                // Pour les rôles non gérés, on pourrait rediriger vers une page d'erreur
                fxmlPath = "/fxml/ErrorView.fxml";
                windowTitle += "Erreur";
                System.err.println("Rôle inconnu ou non géré: " + role + ". Redirection vers la page d'erreur.");
                break;
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();
        Object controller = loader.getController();

        // Passage de l'identifiant de l'employé au contrôleur de destination
        if (controller instanceof DashboardController) {
            ((DashboardController) controller).setConnectedEmployeMatricule(currentUser.getEmployeId());
            System.out.println("Matricule de l'utilisateur passé au DashboardController.");
        } else if (controller instanceof CongeController) {
            // Si la redirection mène directement à la vue de congé, passer le matricule directement
            ((CongeController) controller).setEmployeMatricule(currentUser.getEmployeId());
            System.out.println("Matricule de l'employé passé au CongeController.");
        } else if (controller instanceof ErrorController) {
            ((ErrorController) controller).setErrorMessage("Votre rôle '" + role + "' n'est pas configuré pour accéder à une interface spécifique.");
        }


        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle(windowTitle);
        stage.show();
    }

    // --- Méthodes utilitaires pour l'affichage de l'état ---

    private void updateStatus(String message) {
        if (messageLabel != null) {
            messageLabel.setText(message);
        }
    }

    private void showSuccess(String message) {
        updateStatus(message);
    }

    private void showError(String title, String message) {
        updateStatus("Erreur: " + message);
    }

    private void showWarning(String message) {
        updateStatus("Attention: " + message);
    }
}