module trondance {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;

    requires javax.inject;
    requires java.sql;

    requires spring.core;
    requires spring.context;
    requires spring.web;
    requires spring.beans;

    requires com.fasterxml.jackson.databind;

    exports trondance;
    exports trondance.persistence;
    exports trondance.provider;

    opens trondance.provider;
    opens trondance.config;
    opens trondance;
}