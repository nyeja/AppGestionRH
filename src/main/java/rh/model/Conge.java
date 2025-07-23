package rh.model;

import java.time.LocalDate; // Use LocalDate for DatePicker compatibility

public class Conge {
    private String idConge; // Matches VARCHAR2(10) from DB
    private String matriculeEmploye; // Matches VARCHAR2(20) from DB
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private String typeConge;
    private String justificatif;
    private String statut; // Default to 'En attente' in DB

    // Constructor
    public Conge() {}

    // Constructor with fields (optional, useful for creating objects from DB)
    public Conge(String idConge, String matriculeEmploye, LocalDate dateDebut, LocalDate dateFin, String typeConge, String justificatif, String statut) {
        this.idConge = idConge;
        this.matriculeEmploye = matriculeEmploye;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.typeConge = typeConge;
        this.justificatif = justificatif;
        this.statut = statut;
    }

    // Getters and Setters
    public String getIdConge() { return idConge; }
    public void setIdConge(String idConge) { this.idConge = idConge; }

    public String getMatriculeEmploye() { return matriculeEmploye; }
    public void setMatriculeEmploye(String matriculeEmploye) { this.matriculeEmploye = matriculeEmploye; }

    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }

    public LocalDate getDateFin() { return dateFin; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }

    public String getTypeConge() { return typeConge; }
    public void setTypeConge(String typeConge) { this.typeConge = typeConge; }

    public String getJustificatif() { return justificatif; }
    public void setJustificatif(String justificatif) { this.justificatif = justificatif; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

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