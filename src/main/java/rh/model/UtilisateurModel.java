package rh.model;

public class UtilisateurModel {
    private String idUtilisateur;
    private String email;
    private String password;
    private String employeId;
    private String role;

    public UtilisateurModel(String idUtilisateur, String email, String password,String employeId, String role) {
        this.idUtilisateur = idUtilisateur;
        this.email = email;
        this.password = password;
        this.employeId= employeId;
        this.role = role;
    }

    // Getters et Setters
    public String getIdUtilisateur() {
        return idUtilisateur;
    }

    public void setIdUtilisateur(String idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmployeId(String employeId){this.employeId=employeId;}

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "Utilisateur{" +
                "idUtilisateur=" + idUtilisateur +
                ", email='" + email + '\'' +
                ", employeId =" + employeId + '\''+
                ", role='" + role + '\'' +
                '}';
    }
}