module com.rhplus.rhplus {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires java.sql;


    exports rh;
    opens rh to javafx.fxml;
    opens rh.controller to javafx.fxml;
    opens rh.model to javafx.base, javafx.fxml;
    exports rh.controller;

}