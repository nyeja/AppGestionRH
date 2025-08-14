package rh.model;

/**
 * Classe de modèle pour l'utilisateur.
 */
public class User {

    private int id;
    private String email;
    private String employeId;
    private String role;

    public User(int id, String email, String employeId, String role) {
        this.id = id;
        this.email = email;
        this.employeId = employeId;
        this.role = role;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getEmployeId() {
        return employeId;
    }

    public String getRole() {
        return role;
    }
}
