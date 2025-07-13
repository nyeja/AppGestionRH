package rh.model;

import java.util.Date;

public class employe {
    private String id;
    private String nom;
    private String prenom;
    private Integer telephone;
    private String email;
    private String adresse;
    private Date dateEmbauche;
    private String departement;
    private String poste;

    // Constructeur par défaut
    public employe() {}

    // Constructeur avec tous les arguments
    public employe(String id, String nom, String prenom, int telephone, String email,
                   String adresse, Date dateEmbauche, String departement, String poste) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.telephone = telephone;
        this.email = email;
        this.adresse = adresse;
        this.dateEmbauche = dateEmbauche;
        this.departement = departement;
        this.poste = poste;
    }

    // Constructeur sans département et poste (pour compatibilité)
    public employe(String id, String nom, String prenom, int telephone, String email,
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
}
