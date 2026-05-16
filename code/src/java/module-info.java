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

    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.swing;
    requires javafx.web;

    requires pcgen.base;
    requires pcgen.formula;

    requires org.apache.commons.lang3;
    requires org.apache.commons.io;
    requires freemarker;
    requires org.jdom2;
    requires net.sourceforge.argparse4j;
    requires org.controlsfx.controls;
    requires org.xmlunit;
    requires org.apache.xmlgraphics.fop.core;
    requires org.apache.xmlgraphics.fop.events;
    requires org.apache.xmlgraphics.commons;
    requires spring.web;
    requires spring.beans;
    requires spring.core;
    requires Saxon.HE;
    requires jep;

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
    opens pcgen.cdom.facet.analysis;
    opens pcgen.cdom.facet.fact;
    opens pcgen.cdom.facet.input;
    opens pcgen.cdom.facet.model;
    opens pcgen.cdom.formula.scope;
    opens pcgen.cdom.reference;
    opens pcgen.cdom.enumeration;
    opens pcgen.persistence.lst;
    opens pcgen.rules.persistence;
    opens pcgen.rules.context;
    opens pcgen.gui2.converter;
    opens pcgen.gui3;
    opens pcgen.gui3.application;
    opens pcgen.gui3.behavior;
    opens pcgen.gui3.component;
    opens pcgen.gui3.core;
    opens pcgen.gui3.dialog;
    opens pcgen.gui3.preferences;
    opens pcgen.gui3.preloader;
    opens pcgen.gui3.utilty;
    opens pcgen.io;
    opens pcgen.util;
}
