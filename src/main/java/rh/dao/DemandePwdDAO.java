package rh.dao;

import rh.model.DemandeChangementPwd;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DemandePwdDAO {
    private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521:xe";
    private static final String DB_USER = "Yola";
    private static final String DB_PASSWORD = "Yolabd";

    private Connection conn;

    public DemandePwdDAO() {
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<DemandeChangementPwd> getDemandesNonValidees() {
        List<DemandeChangementPwd> demandes = new ArrayList<>();
        String sql = "SELECT d.id, d.id_utilisateur, u.username, d.nouveau_mdp, d.date_demande, d.validee " +
                     "FROM demande_changement_pwd d JOIN utilisateur u ON d.id_utilisateur = u.id " +
                     "WHERE d.validee = 'N'";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                demandes.add(new DemandeChangementPwd(
                        rs.getInt("id"),
                        rs.getInt("id_utilisateur"),
                        rs.getString("username"),
                        rs.getString("nouveau_mdp"),
                        rs.getDate("date_demande"),
                        rs.getString("validee")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return demandes;
    }

    public void validerDemande(int idDemande, int idUtilisateur, String nouveauMdp) {
        try {
            conn.setAutoCommit(false);

            // 1. Mettre à jour mot de passe dans `utilisateur`
            String updateUser = "UPDATE utilisateur SET password = ? WHERE id = ?";
            try (PreparedStatement stmtUser = conn.prepareStatement(updateUser)) {
                stmtUser.setString(1, nouveauMdp);
                stmtUser.setInt(2, idUtilisateur);
                stmtUser.executeUpdate();
            }

            // 2. Marquer la demande comme validée
            String updateDemande = "UPDATE demande_changement_pwd SET validee = 'Y' WHERE id = ?";
            try (PreparedStatement stmtDemande = conn.prepareStatement(updateDemande)) {
                stmtDemande.setInt(1, idDemande);
                stmtDemande.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
    }
    public boolean ajouterDemande(int idUtilisateur, String nouveauMdp) {
    String sql = "INSERT INTO demande_changement_pwd (id_utilisateur, nouveau_mdp, date_demande, validee) " +
                 "VALUES (?, ?, SYSDATE, 'N')";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, idUtilisateur);
        stmt.setString(2, nouveauMdp);
        int rows = stmt.executeUpdate();
        return rows > 0;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}

}
