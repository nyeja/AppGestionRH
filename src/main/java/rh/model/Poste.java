package rh.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Poste {
    private final StringProperty id;
    private final StringProperty nom;
    private final StringProperty localisation;
    private final StringProperty idDepartement;
    private final StringProperty nomDepartement;

    public Poste(String id, String nom, String localisation, String idDepartement, String nomDepartement) {
        this.id = new SimpleStringProperty(id);
        this.nom = new SimpleStringProperty(nom);
        this.localisation = new SimpleStringProperty(localisation);
        this.idDepartement = new SimpleStringProperty(idDepartement);
        this.nomDepartement = new SimpleStringProperty(nomDepartement);
    }

    public String getId() {
        return id.get();
    }

    public void setId(String id) {
        this.id.set(id);
    }

    public StringProperty idProperty() {
        return id;
    }

    public String getNom() {
        return nom.get();
    }

    public void setNom(String nom) {
        this.nom.set(nom);
    }

    public StringProperty nomProperty() {
        return nom;
    }

    public String getLocalisation() {
        return localisation.get();
    }

    public void setLocalisation(String localisation) {
        this.localisation.set(localisation);
    }

    public StringProperty localisationProperty() {
        return localisation;
    }

    public String getIdDepartement() {
        return idDepartement.get();
    }

    public void setIdDepartement(String idDepartement) {
        this.idDepartement.set(idDepartement);
    }

    public StringProperty idDepartementProperty() {
        return idDepartement;
    }

    public String getNomDepartement() {
        return nomDepartement.get();
    }

    public void setNomDepartement(String nomDepartement) {
        this.nomDepartement.set(nomDepartement);
    }

    public StringProperty nomDepartementProperty() {
        return nomDepartement;
    }
}
