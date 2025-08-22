package rh.model;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Représente un objet Permission avec ses détails.
 * Cette classe agit comme le modèle de données pour les demandes de permission.
 */
public class Permission {

    private int idPermission;
    private String matriculeEmploye;
    private LocalDate dateDemande;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private int heureDebut;   // Nouveau
    private int minuteDebut;  // Nouveau
    private int heureFin;     // Nouveau
    private int minuteFin;    // Nouveau
    private String motif;
    private String statut;
    private String nomCompletEmploye; // Pour l'affichage dans l'interface RH

    // Constructeur par défaut
    public Permission() {
    }

    // Constructeur pour l'ajout d'une nouvelle demande
    public Permission(String matriculeEmploye, LocalDate dateDebut, int heureDebut, int minuteDebut, LocalDate dateFin, int heureFin, int minuteFin, String motif, String statut) {
        this.matriculeEmploye = matriculeEmploye;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.heureDebut = heureDebut;
        this.heureFin = heureFin;
        this.motif = motif;
        this.statut = statut;
    }

    // Getters et Setters
    public int getIdPermission() {
        return idPermission;
    }

    public void setIdPermission(int idPermission) {
        this.idPermission = idPermission;
    }

    public String getMatriculeEmploye() {
        return matriculeEmploye;
    }

    public void setMatriculeEmploye(String matriculeEmploye) {
        this.matriculeEmploye = matriculeEmploye;
    }

    public LocalDate getDateDemande() {
        return dateDemande;
    }

    public void setDateDemande(LocalDate dateDemande) {
        this.dateDemande = dateDemande;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }

    // Nouveaux getters et setters
    public int getHeureDebut() { return heureDebut; }
    public void setHeureDebut(int heureDebut) { this.heureDebut = heureDebut; }

    public int getMinuteDebut() { return minuteDebut; }
    public void setMinuteDebut(int minuteDebut) { this.minuteDebut = minuteDebut; }

    public int getHeureFin() { return heureFin; }
    public void setHeureFin(int heureFin) { this.heureFin = heureFin; }

    public int getMinuteFin() { return minuteFin; }
    public void setMinuteFin(int minuteFin) { this.minuteFin = minuteFin; }

    public String getMotif() {
        return motif;
    }

    public void setMotif(String motif) {
        this.motif = motif;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public String getNomCompletEmploye() {
        return nomCompletEmploye;
    }

    public void setNomCompletEmploye(String nomCompletEmploye) {
        this.nomCompletEmploye = nomCompletEmploye;
    }
}
