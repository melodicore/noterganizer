/**
 * @author datafox
 */
module noterganizer.client {
    exports me.datafox.noterganizer.client;

    requires static lombok;

    requires noterganizer.api;

    requires java.net.http;
    requires java.prefs;

    requires org.apache.logging.log4j;
    requires org.apache.logging.log4j.core;

    requires com.google.gson;

    requires io.github.classgraph;

    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    requires com.sandec.mdfx;

    opens me.datafox.noterganizer.client to javafx.fxml;
    opens me.datafox.noterganizer.client.controller to javafx.fxml;

    opens me.datafox.noterganizer.client.model to com.google.gson;
}