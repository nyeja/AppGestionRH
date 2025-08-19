package rh.model;

import java.util.Date;

public class EmployeModel {
    private String id;
    private String nom;
    private String prenom;
    private Integer telephone;
    private String email;
    private String adresse;
    private Date dateEmbauche;
    private String departement;
    private String poste;

    // Changement : soldeConge est maintenant de type primitif 'int'
    private int soldeConge;

    // Constructeur par défaut
    public EmployeModel() {}

    // Constructeur sans département et poste (pour compatibilité)
    public EmployeModel(String id, String nom, String prenom, int telephone, String email,
                        String adresse, Date dateEmbauche) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.telephone = telephone;
        this.email = email;
        this.adresse = adresse;
        this.dateEmbauche = dateEmbauche;
    }

    // Getters et setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Integer getTelephone() { return telephone; }
    public void setTelephone(Integer telephone) { this.telephone = telephone; }

    public Date getDateEmbauche() { return dateEmbauche; }
    public void setDateEmbauche(Date dateEmbauche) { this.dateEmbauche = dateEmbauche; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    public String getDepartement() { return departement; }
    public void setDepartement(String departement) { this.departement = departement; }

    public String getPoste() { return poste; }
    public void setPoste(String poste) { this.poste = poste; }

    @Override
    public String toString() {
        return prenom + " " + nom + " (" + poste + ")";
    }

    // --- Méthodes corrigées ---

    // La méthode setSoldeConge doit affecter la valeur au champ
    public void setSoldeConge(int soldeConge) {
        this.soldeConge = soldeConge;
    }

    // La méthode getSoldeConge doit retourner le champ
    public int getSoldeConge() {
        return this.soldeConge;
    }
}