package rh.model;

import javafx.beans.property.*;
import java.time.LocalDate;

public class Absence {
    private StringProperty employeeId;
    private StringProperty employeeName;
    private StringProperty type;
    private ObjectProperty<LocalDate> date;
    private StringProperty motif;
    private StringProperty statut;

    public Absence(String employeeId, String employeeName, String type, LocalDate date, String motif, String statut) {
        this.employeeId = new SimpleStringProperty(employeeId);
        this.employeeName = new SimpleStringProperty(employeeName);
        this.type = new SimpleStringProperty(type);
        this.date = new SimpleObjectProperty<>(date);
        this.motif = new SimpleStringProperty(motif);
        this.statut = new SimpleStringProperty(statut);
    }

    // Getters (Property pour TableView)
    public StringProperty employeeIdProperty() { return employeeId; }
    public StringProperty employeeNameProperty() { return employeeName; }
    public StringProperty typeProperty() { return type; }
    public ObjectProperty<LocalDate> dateProperty() { return date; }
    public StringProperty motifProperty() { return motif; }
    public StringProperty statutProperty() { return statut; }
}
