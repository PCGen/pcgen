module pcgen {
    requires java.desktop;
    requires java.logging;
    requires java.naming;
    requires java.net.http;
    requires java.prefs;
    requires java.scripting;
    requires java.sql;
    requires java.xml;
    requires java.management;
    requires java.rmi;
    requires jdk.httpserver;
    requires jdk.unsupported;
    requires jdk.xml.dom;
    requires jdk.jsobject;

    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.swing;
    requires javafx.web;

    requires static org.jetbrains.annotations;
    requires static com.github.spotbugs.annotations;

    exports pcgen.core;
    exports pcgen.gui2;
    exports pcgen.gui3;
    exports pcgen.io;
    exports pcgen.persistence;
    exports pcgen.pluginmgr;
    exports pcgen.system;
    exports pcgen.util;

    opens pcgen.system;
    opens pcgen.pluginmgr;
    opens pcgen.core.bonus;
    opens pcgen.core.prereq;
    opens pcgen.cdom.facet;
    opens pcgen.cdom.reference;
    opens pcgen.cdom.enumeration;
    opens pcgen.persistence.lst;
    opens pcgen.rules.persistence;
    opens pcgen.rules.context;
    opens pcgen.gui2.converter;
    opens pcgen.gui3;
    opens pcgen.gui3.dialog;
    opens pcgen.gui3.preferences;
    opens pcgen.gui3.preloader;
    opens pcgen.io;
    opens pcgen.util;
}
