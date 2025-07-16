package rh.model.departement;

public class tableauDepartement {
    // Déclaration
    private String id;
    private String nom;
    private String code;
    private String idResponsable;
    private String localisation;
    private String description;
    private String nombreEmployes;
    // constructeur pour création du tableau
    public tableauDepartement(String id, String nom , String code, String idResponsable, String localisation, String description, String nombreEmployes) {
        this.id = id;
        this.nom = nom;
        this.code = code;
        this.idResponsable = idResponsable;
        this.localisation = localisation;
        this.description = description;
        this.nombreEmployes = nombreEmployes;
    }
    // Getter
    public String getId() { return id; }
    public String getNom(){return nom;}
    public String getCode() { return code; }
    public String getIdResponsable() { return idResponsable; }
    public String getLocalisation() { return localisation; }
    public String getDescription() { return description; }
    public String getNombreEmployes() { return nombreEmployes; }
}
