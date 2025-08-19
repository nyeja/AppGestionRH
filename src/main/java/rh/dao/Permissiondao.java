package rh.dao;

import rh.model.Permission;
import rh.utils.ConnexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.Date;

/**
 * Data Access Object (DAO) pour la gestion des demandes de permission dans la base de données.
 */
public class Permissiondao {

    /**
     * Ajoute une nouvelle demande de permission dans la base de données.
     * Le statut initial est "En attente".
     *
     * @param permission L'objet Permission à insérer.
     * @throws SQLException Si une erreur de base de données se produit.
     */
    public void ajouterPermission(Permission permission) throws SQLException {
        // La requête SQL doit inclure explicitement toutes les colonnes NOT NULL, y compris les heures et minutes.
        String sql = "INSERT INTO PERMISSION (MATRICULE_EMPLOYE, DATE_DEBUT, HEURE_DEBUT, MINUTE_DEBUT, DATE_FIN, HEURE_FIN, MINUTE_FIN, MOTIF, STATUT, DATE_DEMANDE) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, SYSDATE)";
        try (Connection conn = ConnexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, permission.getMatriculeEmploye());
            ps.setDate(2, Date.valueOf(permission.getDateDebut()));
            ps.setInt(3, permission.getHeureDebut());
            ps.setInt(4, permission.getMinuteDebut());
            ps.setDate(5, Date.valueOf(permission.getDateFin()));
            ps.setInt(6, permission.getHeureFin());
            ps.setInt(7, permission.getMinuteFin());
            ps.setString(8, permission.getMotif());
            ps.setString(9, permission.getStatut());

            ps.executeUpdate();
        }
    }

    /**
     * Récupère toutes les demandes de permission pour un employé spécifique.
     *
     * @param matriculeEmploye Le matricule de l'employé.
     * @return Une liste des demandes de permission.
     * @throws SQLException Si une erreur de base de données se produit.
     */
    public List<Permission> getPermissionsByEmployeMatricule(String matriculeEmploye) throws SQLException {
        List<Permission> permissions = new ArrayList<>();
        // La requête doit maintenant sélectionner les heures et minutes
        String sql = "SELECT ID_PERMISSION, DATE_DEMANDE, DATE_DEBUT, HEURE_DEBUT, MINUTE_DEBUT, DATE_FIN, HEURE_FIN, MINUTE_FIN, MOTIF, STATUT FROM PERMISSION WHERE MATRICULE_EMPLOYE = ? ORDER BY DATE_DEMANDE DESC";

        try (Connection conn = ConnexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, matriculeEmploye);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    permissions.add(mapResultSetToPermission(rs));
                }
            }
        }
        return permissions;
    }

    /**
     * Récupère toutes les demandes de permission pour le tableau de bord RH.
     *
     * @return Une liste de toutes les demandes de permission.
     * @throws SQLException Si une erreur de base de données se produit.
     */
    public List<Permission> getAllPermissions() throws SQLException {
        List<Permission> permissions = new ArrayList<>();
        // La requête doit sélectionner les heures et minutes
        String sql = "SELECT p.ID_PERMISSION, p.MATRICULE_EMPLOYE, p.DATE_DEMANDE, p.DATE_DEBUT, p.HEURE_DEBUT, p.MINUTE_DEBUT, p.DATE_FIN, p.HEURE_FIN, p.MINUTE_FIN, p.MOTIF, p.STATUT, e.NOM, e.PRENOMS " +
                "FROM PERMISSION p INNER JOIN EMPLOYE e ON p.MATRICULE_EMPLOYE = e.ID " +
                "ORDER BY p.STATUT ASC, p.DATE_DEMANDE DESC";

        try (Connection conn = ConnexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                permissions.add(mapResultSetToPermissionWithEmploye(rs));
            }
        }
        return permissions;
    }

    /**
     * Met à jour le statut d'une demande de permission.
     *
     * @param idPermission L'ID de la demande à mettre à jour.
     * @param statut Le nouveau statut.
     * @throws SQLException Si une erreur de base de données se produit.
     */
    public void updatePermissionStatut(int idPermission, String statut) throws SQLException {
        String sql = "UPDATE PERMISSION SET STATUT = ? WHERE ID_PERMISSION = ?";
        try (Connection conn = ConnexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, statut);
            ps.setInt(2, idPermission);
            ps.executeUpdate();
        }
    }

    /**
     * Mappe un ResultSet à un objet Permission.
     */
    private Permission mapResultSetToPermission(ResultSet rs) throws SQLException {
        Permission permission = new Permission();
        permission.setIdPermission(rs.getInt("ID_PERMISSION"));
        permission.setDateDemande(rs.getDate("DATE_DEMANDE").toLocalDate());
        permission.setDateDebut(rs.getDate("DATE_DEBUT").toLocalDate());
        permission.setHeureDebut(rs.getInt("HEURE_DEBUT"));
        permission.setMinuteDebut(rs.getInt("MINUTE_DEBUT"));
        permission.setDateFin(rs.getDate("DATE_FIN").toLocalDate());
        permission.setHeureFin(rs.getInt("HEURE_FIN"));
        permission.setMinuteFin(rs.getInt("MINUTE_FIN"));
        permission.setMotif(rs.getString("MOTIF"));
        permission.setStatut(rs.getString("STATUT"));
        // Le matricule de l'employé est déjà connu si la requête est spécifique à un employé
        return permission;
    }

    /**
     * Mappe un ResultSet à un objet Permission avec les infos de l'employé.
     */
    private Permission mapResultSetToPermissionWithEmploye(ResultSet rs) throws SQLException {
        Permission permission = new Permission();
        permission.setIdPermission(rs.getInt("ID_PERMISSION"));
        permission.setMatriculeEmploye(rs.getString("MATRICULE_EMPLOYE"));
        permission.setDateDemande(rs.getDate("DATE_DEMANDE").toLocalDate());
        permission.setDateDebut(rs.getDate("DATE_DEBUT").toLocalDate());
        permission.setHeureDebut(rs.getInt("HEURE_DEBUT"));
        permission.setMinuteDebut(rs.getInt("MINUTE_DEBUT"));
        permission.setDateFin(rs.getDate("DATE_FIN").toLocalDate());
        permission.setHeureFin(rs.getInt("HEURE_FIN"));
        permission.setMinuteFin(rs.getInt("MINUTE_FIN"));
        permission.setMotif(rs.getString("MOTIF"));
        permission.setStatut(rs.getString("STATUT"));
        permission.setNomCompletEmploye(rs.getString("NOM") + " " + rs.getString("PRENOMS"));
        return permission;
    }
}