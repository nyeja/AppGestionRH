module rh {
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
    requires java.desktop;

    opens rh.model.departement to java.base;
    opens rh.model.employe to java.base;
    opens rh.model to java.base;
    opens rh.dao to java.base;

    opens rh.controller.parametre to javafx.fxml;
    opens rh.controller to javafx.fxml;


    exports rh.model.departement;
    exports rh.model.employe;
    exports rh.dao;
    exports rh.model;
    exports rh.controller;
    exports rh;

}
