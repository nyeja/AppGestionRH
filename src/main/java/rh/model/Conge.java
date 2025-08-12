package rh.model;

import java.time.LocalDate;

public class Conge {
    private String idConge;
    private String matriculeEmploye;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private String typeConge;
    private String justificatif;
    private String statut;
    private String nom;
    private String prenoms;

    // Constructor
    public Conge() {}

    // Getters and Setters
    public String getIdConge() { return idConge; }
    public void setIdConge(String idConge) {this.idConge= idConge;}

    public String getMatriculeEmploye() { return matriculeEmploye; }
    public void setMatriculeEmploye(String matriculeEmploye) {this.matriculeEmploye = matriculeEmploye; }

    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }

    public LocalDate getDateFin() { return dateFin; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin ;}

    public String getTypeConge() { return typeConge; }
    public void setTypeConge(String typeConge) { this.typeConge = typeConge;}

    public String getJustificatif() { return justificatif; }
    public void setJustificatif(String justificatif) { this.justificatif=justificatif;}

    public String getStatut() { return statut; }
    public void setStatut(String statut) {this.statut=statut; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenoms() { return prenoms; }
    public void setPrenoms(String prenoms) { this.prenoms = prenoms; }

    //A ne pas effacer, cela permet d'utiliser un nom complet pour la liste des congés
    public String getNomComplet() {
        if (nom != null && prenoms != null) {
            return prenoms + " " + nom;
        }
        return "N/A"; // Retourne une valeur par défaut si les noms sont manquants
    }







    @Override
    public String toString() {
        return "Conge{" +
                "idConge='" + idConge + '\'' +
                ", matriculeEmploye='" + matriculeEmploye + '\'' +
                ", dateDebut=" + dateDebut +
                ", dateFin=" + dateFin +
                ", typeConge='" + typeConge + '\'' +
                ", justificatif='" + justificatif + '\'' +
                ", statut='" + statut + '\'' +
                '}';
    }



}