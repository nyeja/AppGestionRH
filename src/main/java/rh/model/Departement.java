
package rh.model;

/**
 *
 * @author USER
 */
public class Departement {
    private String id;
    private String nom;

    public Departement(String id, String nom) {
        this.id = id;
        this.nom = nom;
    }

    public String getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    @Override
    public String toString() {
        return nom; // C'est ce qui s'affiche dans le ChoiceBox
    }
}
