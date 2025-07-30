package rh.model.employe;

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
    private String motDePasse;
    private String image; // peut contenir un chemin ou juste le nom de fichier
    private String role;
    // Constructeur par défaut
    public employe() {}

    // Constructeur avec tous les arguments
    public employe(String id, String nom, String prenom, int telephone, String email,
                   String adresse, Date dateEmbauche, String departement, String poste,
                   String motDePasse, String image , String role) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.telephone = telephone;
        this.email = email;
        this.adresse = adresse;
        this.dateEmbauche = dateEmbauche;
        this.departement = departement;
        this.poste = poste;
        this.motDePasse = motDePasse;
        this.image = image;
        this.role = role;
    }

    // constructeur sans img et mot de pass
    public employe(String id, String nom, String prenom, int telephone, String email,
                   String adresse, Date dateEmbauche, String departement, String poste , String role) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.telephone = telephone;
        this.email = email;
        this.adresse = adresse;
        this.dateEmbauche = dateEmbauche;
        this.departement = departement;
        this.poste = poste;
        this.role = role ;
    }

    public employe(String id , String nom, String motDePasse, String role) {
        this.id = id;
        this.nom = nom;
        this.motDePasse = motDePasse;
        this.role = role;
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

    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public String getRole(){return role; }
    public void setRole(String role) { this.role = role; }
    @Override
    public String toString() {
        return prenom + " " + nom + " (" + poste + ")";
    }
}
