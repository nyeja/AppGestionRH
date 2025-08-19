package rh.dao;

import rh.model.Conge;
import rh.utils.ConnexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.Date;

public class Congedao {

    /**
     * Ajoute une nouvelle demande de congé dans la base de données.
     * La date de soumission est automatiquement générée par le SGBD.
     *
     * @param conge L'objet Conge contenant les informations de la demande.
     * @throws SQLException Si une erreur de base de données se produit.
     */
    public void ajouterConge(Conge conge) throws SQLException {
        String sql = "INSERT INTO Conge (ID_CONGE, MATRICULE_EMPLOYE, DATE_DEBUT, DATE_FIN, TYPE_CONGE, JUSTIFICATIF, STATUT, NB_JOURS, DATE_SOUMISSION) VALUES (?, ?, ?, ?, ?, ?, ?, ?, SYSDATE)";

        try (Connection conn = ConnexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, conge.getIdConge()); //
            ps.setString(2, conge.getMatriculeEmploye());
            ps.setDate(3, Date.valueOf(conge.getDateDebut()));
            ps.setDate(4, Date.valueOf(conge.getDateFin()));
            ps.setString(5, conge.getTypeConge());
            ps.setString(6, conge.getJustificatif());
            ps.setString(7, conge.getStatut());
            ps.setInt(8, conge.getNbJours());

            ps.executeUpdate();
        }
    }

    /**
     * Récupère toutes les demandes de congé depuis la base de données.
     * Effectue une jointure avec la table EMPLOYE pour inclure le nom et le prénom de l'employé.
     *
     * @return Une liste d'objets Conge.
     * @throws SQLException Si une erreur de base de données se produit.
     */
    public List<Conge> getAllConges() throws SQLException {
        List<Conge> conges = new ArrayList<>();

        String sql = "SELECT c.ID_CONGE, c.MATRICULE_EMPLOYE, c.DATE_SOUMISSION, c.JUSTIFICATIF, " +
                "c.DATE_DEBUT, c.DATE_FIN, c.TYPE_CONGE, c.STATUT, " +
                "e.NOM, e.PRENOMS, e.SOLDE_CONGE " +
                "FROM CONGE c " +
                "INNER JOIN EMPLOYE e ON c.MATRICULE_EMPLOYE = e.ID " +
                "ORDER BY c.STATUT ASC, c.DATE_SOUMISSION DESC";

        try (Connection conn = ConnexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                conges.add(mapResultSetToConge(rs));
            }
        }
        return conges;
    }

    /**
     * Met à jour le statut d'une demande de congé spécifique.
     * Cette méthode est utilisée pour approuver ou refuser une demande.
     *
     * @param conge L'objet Conge avec le statut et l'ID mis à jour.
     * @throws SQLException Si une erreur de base de données se produit.
     */
    public void updateCongeStatut(Conge conge) throws SQLException {
        // Met à jour uniquement le statut basé sur l'ID du congé
        String sql = "UPDATE Conge SET STATUT = ? WHERE ID_CONGE = ?";
        try (Connection conn = ConnexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, conge.getStatut()); // Le nouveau statut ('Approuvé', 'Refusé')
            ps.setString(2, conge.getIdConge()); // Identifie la demande de congé spécifique par son ID
            ps.executeUpdate();
        }
    }

    /**
     * Mappe un `ResultSet` à un objet `Conge`.
     * Cette méthode utilitaire simplifie la conversion des données de la base
     * de données en un objet Java.
     *
     * @param rs Le ResultSet contenant les données d'une demande de congé.
     * @return Un objet Conge rempli avec les données du ResultSet.
     * @throws SQLException Si une erreur de lecture du ResultSet se produit.
     */
    private Conge mapResultSetToConge(ResultSet rs) throws SQLException {
        Conge conge = new Conge();
        conge.setIdConge(rs.getString("ID_CONGE"));
        conge.setMatriculeEmploye(rs.getString("MATRICULE_EMPLOYE"));
        conge.setDateDebut(rs.getDate("DATE_DEBUT").toLocalDate());
        conge.setDateFin(rs.getDate("DATE_FIN").toLocalDate());
        conge.setTypeConge(rs.getString("TYPE_CONGE"));
        conge.setJustificatif(rs.getString("JUSTIFICATIF"));
        conge.setStatut(rs.getString("STATUT"));
        conge.setNom(rs.getString("NOM"));
        conge.setPrenoms(rs.getString("PRENOMS"));
        conge.setSoldeConge(rs.getInt("SOLDE_CONGE"));

        return conge;
    }

    /**
     * Génère un identifiant unique pour une demande de congé.
     * C'est un simple exemple basé sur l'horodatage.
     *
     * @return Une chaîne de caractères représentant l'ID unique.
     */
    private String generateUniqueCongeId() {
        return "CON-" + System.currentTimeMillis() % 10000; // Simple example
    }

    /**
     * Supprime toutes les demandes de congé associées à un employé spécifique.
     * Cette méthode est utilisée, par exemple, lors de la suppression d'un employé.
     *
     * @param employeId L'identifiant de l'employé dont les congés doivent être supprimés.
     * @throws SQLException Si une erreur de base de données se produit.
     */
    public void supprimerCongesParEmploye(String employeId) throws SQLException {
        String sql = "DELETE FROM Conge WHERE matricule_employe = ?";
        try (Connection connection = ConnexionDB.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, employeId);
            ps.executeUpdate();
        }
    }

    /**
     * Récupère toutes les demandes de congé pour un employé spécifique.
     * @param matriculeEmploye Le matricule de l'employé.
     * @return Une liste des demandes de congé de l'employé.
     * @throws SQLException Si une erreur de base de données se produit.
     */
    public List<Conge> getCongeByEmployeMatricule(String matriculeEmploye) throws SQLException {
        List<Conge> conges = new ArrayList<>();
        String query = "SELECT * FROM conge WHERE matricule_employe = ?";
        try (Connection conn = ConnexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, matriculeEmploye);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Conge conge = new Conge();
                conge.setIdConge(rs.getString("id_conge"));
                conge.setMatriculeEmploye(rs.getString("matricule_employe"));
                conge.setDateDebut(rs.getDate("date_debut").toLocalDate());
                conge.setDateFin(rs.getDate("date_fin").toLocalDate());
                conge.setTypeConge(rs.getString("type_conge"));
                conge.setJustificatif(rs.getString("justificatif")); // Assurez-vous que le nom de la colonne est correct
                conge.setStatut(rs.getString("statut"));
                conges.add(conge);
            }
        }
        return conges;
    }

}