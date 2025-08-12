package rh.dao;



import rh.model.Conge;

import rh.utils.ConnexionDB;



import java.sql.Connection;

import java.sql.PreparedStatement;

import java.sql.ResultSet;

import java.sql.SQLException;

import java.sql.Statement;

import java.time.LocalDate;

import java.util.ArrayList;

import java.util.List;

import java.sql.Date;



public class congedao {



    public void ajouterConge(Conge conge) throws SQLException {

        String sql = "INSERT INTO Conge (ID_CONGE, MATRICULE_EMPLOYE, DATE_DEBUT, DATE_FIN, TYPE_CONGE, JUSTIFICATIF, STATUT, DATE_SOUMISSION) VALUES (?, ?, ?, ?, ?, ?, ?, SYSDATE)";



        try (Connection conn = ConnexionDB.getConnection();

             PreparedStatement ps = conn.prepareStatement(sql)) {



            ps.setString(1, conge.getIdConge()); //

            ps.setString(2, conge.getMatriculeEmploye());

            ps.setDate(3, Date.valueOf(conge.getDateDebut()));

            ps.setDate(4, Date.valueOf(conge.getDateFin()));

            ps.setString(5, conge.getTypeConge());

            ps.setString(6, conge.getJustificatif());

            ps.setString(7, conge.getStatut());



            ps.executeUpdate();

        }

    }



// Récupérer toutes les demandes de congé



    public List<Conge> getAllConges() throws SQLException {

        List<Conge> conges = new ArrayList<>();



// Requête SQL corrigée pour correspondre à la nouvelle structure

        String sql = "SELECT c.ID_CONGE, c.MATRICULE_EMPLOYE, c.DATE_SOUMISSION, c.JUSTIFICATIF, "+

                "c.DATE_DEBUT, c.DATE_FIN, c.TYPE_CONGE, c.STATUT, c.NB_JOURS, " +

                "e.NOM, e.PRENOMS " +

                "FROM CONGE c " +

                "INNER JOIN EMPLOYE e ON c.MATRICULE_EMPLOYE = e.ID " + // Jointure sur MATRICULE_EMPLOYE

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

//Mettre à jour le statut d'une demande de congé

// Cette méthode sera utilisée lorsque les RH approuvent ou refusent une demande.



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









    private Conge mapResultSetToConge(ResultSet rs) throws SQLException {

        Conge conge = new Conge();

        conge.setIdConge(rs.getString("ID_CONGE"));

        conge.setMatriculeEmploye(rs.getString("MATRICULE_EMPLOYE")); // Corrigé de MATRICULE_EMPLOYE à ID_EMPLOYE

        conge.setDateDebut(rs.getDate("DATE_DEBUT").toLocalDate());

        conge.setDateFin(rs.getDate("DATE_FIN").toLocalDate());



        conge.setTypeConge(rs.getString("TYPE_CONGE"));

        conge.setJustificatif(rs.getString("JUSTIFICATIF"));

        conge.setStatut(rs.getString("STATUT"));



// Récupération des données jointes avec les bons noms de colonnes

        conge.setNom(rs.getString("NOM"));

        conge.setPrenoms(rs.getString("PRENOMS")); // Corrigé de PRENOM à PRENOMS



        return conge;

    }



    private String generateUniqueCongeId() {

        return "CON-" + System.currentTimeMillis() % 10000; // Simple example

    }

}