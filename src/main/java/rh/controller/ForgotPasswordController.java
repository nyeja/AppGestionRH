package rh.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import rh.dao.DemandePwdDAO;
import rh.dao.EmployerDAO;


public class ForgotPasswordController {

    @FXML private TextField usernameField;
    @FXML private PasswordField newPasswordField;
    @FXML private Label infoLabel;

    @FXML
    public void handlePasswordResetRequest() {
        String username = usernameField.getText();
        String newPassword = newPasswordField.getText();

        if (username.isEmpty() || newPassword.isEmpty()) {
            infoLabel.setText("Tous les champs sont requis.");
            return;
        }

        EmployerDAO utilisateurDAO = new EmployerDAO();
        int userId = utilisateurDAO.getIdByUsername(username);

        if (userId == -1) {
            infoLabel.setText("Utilisateur introuvable.");
            return;
        }

        DemandePwdDAO demandeDAO = new DemandePwdDAO();
        boolean success = demandeDAO.ajouterDemande(userId, newPassword);

        if (success) {
            infoLabel.setText("Demande envoyée à l'administrateur.");
        } else {
            infoLabel.setText("Erreur lors de l'envoi de la demande.");
        }
    }
}
