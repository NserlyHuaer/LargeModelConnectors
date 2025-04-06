module com.nserly.largemodelconnectors {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires okhttp3;
    requires java.net.http;
    requires okio;
    requires static lombok;
    requires org.slf4j;
    requires com.google.gson;
    requires org.commonmark.ext.gfm.tables;
    requires org.commonmark.ext.heading.anchor;
    requires org.commonmark;
    requires jlatexmath;
    requires flexmark;
    requires flexmark.util.ast;
    requires javafx.swing;
    requires java.prefs;

    opens com.nserly.Graphics to javafx.fxml;
    opens com.nserly.Tools.Connection.MessageCollections to com.google.gson;
    exports com.nserly.Graphics;
    opens com.nserly.Tools.Connection.MessageCollections.DeepSeek to com.google.gson;
    exports com.nserly;
    opens com.nserly to javafx.fxml;
    opens com.nserly.Controller to javafx.fxml;
    exports com.nserly.Controller;
}