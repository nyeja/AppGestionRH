package rh.model;

// Dans le package model
public class UtilisateurModel {
    private int idUtilisateur;
    private String username;
    private String password; // Ce sera le hachage du mot de passe
    private String employeId; // Pour lier à l'employé
    private String role;


    public UtilisateurModel(int idUtilisateur, String username, String password, String employeId ,String role) {
        this.idUtilisateur = idUtilisateur;
        this.username = username;
        this.password = password;
        this.employeId = employeId;
        this.role=role;
    }

    // Getters et Setters
    public int getIdUtilisateur() {
        return idUtilisateur;
    }

    public void setIdUtilisateur(int idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
    }

    public String getUsername() {
        return username;
    }



    public String getPassword() {
        return password;
    }


    public String getEmployeId() {
        return employeId;
    }

    public String getRole(){
        return role;
    }


    @Override
    public String toString() {
        return "Utilisateur{" +
                "idUtilisateur=" + idUtilisateur +
                ", username='" + username + '\'' +
                ", employeId=" + employeId +
                ", role='" + role + '\'' + // Ajout du rôle dans toString
                '}';
    }
}