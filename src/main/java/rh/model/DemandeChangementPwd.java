/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rh.model;

/**
 *
 * @author USER
 */

import java.sql.Date;

public class DemandeChangementPwd {
    private int id;
    private int idUtilisateur;
    private String username;
    private String nouveauMdp;
    private Date dateDemande;
    private String validee;

    // Constructeurs, getters et setters

    public DemandeChangementPwd(int id, int idUtilisateur, String username, String nouveauMdp, Date dateDemande, String validee) {
        this.id = id;
        this.idUtilisateur = idUtilisateur;
        this.username = username;
        this.nouveauMdp = nouveauMdp;
        this.dateDemande = dateDemande;
        this.validee = validee;
    }

    public int getId() { return id; }
    public int getIdUtilisateur() { return idUtilisateur; }
    public String getUsername() { return username; }
    public String getNouveauMdp() { return nouveauMdp; }
    public Date getDateDemande() { return dateDemande; }
    public String getValidee() { return validee; }

    public void setValidee(String validee) { this.validee = validee; }
}

