/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rh.model;

/**
 *
 * @author USER
 */
public class Poste {
    private String id;
    private String nom;
    private String localisation;
    private String idDepartement;
    private String nomDepartement;
   

    public Poste(String id, String nom, String localisation, String idDepartement, String nomDepartement) {
        this.id = id;
        this.nom = nom;
        this.localisation = localisation;
        this.idDepartement = idDepartement;
        this.nomDepartement = nomDepartement;
        
    }

    public String getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getLocalisation() {
        return localisation;
    }

    public String getIdDepartement() {
        return idDepartement;
    }
    
    public String getNomDepartement() {
        return nomDepartement;
    }
    
   
}
