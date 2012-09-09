/*
 *  GMGenSystem.java - main class for GMGen
 *  Copyright (C) 2003 Devon Jones, Emily Smirle
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 *  Created on May 24, 2003
 */
package gmgen;

import gmgen.gui.PreferencesDialog;
import gmgen.gui.PreferencesRootTreeNode;
import gmgen.pluginmgr.GMBComponent;
import gmgen.pluginmgr.GMBMessage;
import gmgen.pluginmgr.GMBus;
import gmgen.pluginmgr.PluginManager;
import gmgen.pluginmgr.messages.ClipboardMessage;
import gmgen.pluginmgr.messages.FetchOpenPCGRequestMessage;
import gmgen.pluginmgr.messages.FileOpenMessage;
import gmgen.pluginmgr.messages.LoadMessage;
import gmgen.pluginmgr.messages.PreferencesPanelAddMessage;
import gmgen.pluginmgr.messages.SaveMessage;
import gmgen.pluginmgr.messages.StateChangedMessage;
import gmgen.pluginmgr.messages.TabAddMessage;
import gmgen.pluginmgr.messages.ToolMenuItemAddMessage;
import gmgen.pluginmgr.messages.WindowClosedMessage;
import gmgen.util.LogUtilities;
import gmgen.util.MiscUtilities;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.Method;
import java.util.EventObject;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import org.apache.commons.lang.SystemUtils;

import pcgen.cdom.base.Constants;
import pcgen.core.SettingsHandler;
import pcgen.gui2.tools.Icons;
import pcgen.gui2.tools.Utility;
import pcgen.util.Logging;
import pcgen.util.SwingWorker;

/**
 * <code>GMGenSystem</code> is the main class of the GMGen application.
 * 
 * It holds the controller for every tab as well as the menu bar.
 */
public final class GMGenSystem extends JFrame implements ChangeListener,
        MenuListener, ActionListener, GMBComponent {

    // Serial UID
    private static final long serialVersionUID = -7372446160499882872L;

    /**
     * Holds an instance of the top window, so components and windows can get
     * their parent frame.
     */
    public static GMGenSystem inst;

    // The main <code>JPanel</code> view for the system.
    private GMGenSystemView theView;

    // Boolean true if this is a Mac OS X system.
    private static final boolean MAC_OS_X = SystemUtils.IS_OS_MAC_OSX;
    
    /** GMGen Application name */
    public static final String APPLICATION_NAME = "GMGen";

    private JMenuBar systemMenuBar;
    
    // Menus
    private JMenu editMenu;
    private JMenu fileMenu;
    private JMenu toolsMenu;

    // File menu items
    private JMenuItem copyEditItem;
    private JMenuItem cutEditItem;
    private JMenuItem pasteEditItem;
    private JMenuItem preferencesEditItem;
    private JMenuItem exitFileItem;
    private JMenuItem versionToolsItem;

    /**
     * The new menu item in the file menu.
     */
    public JMenuItem newFileItem;

    /**
     * The open menu item in the file menu.
     */
    public JMenuItem openFileItem;

    /**
     * The save menu item in the file menu.
     */
    public JMenuItem saveFileItem;

    // Separators
    private JSeparator editSeparator1;
    private JSeparator fileSeparator1;
    private JSeparator fileSeparator2;
    private JSeparator toolsSeparator1;

    // Tree for the preferences dialog
    private PreferencesRootTreeNode rootNode = new PreferencesRootTreeNode();

    /**
     * Constructor
     * 
     * Creates a JFrame TODO comment correctly.
     * Starts the GMGen renderer
     */
    public GMGenSystem() {
        super("GMGen System");
        new Renderer().start();
    }

    private void initialize() {
        if (MAC_OS_X) {
            initialiseMacOS();
        }
        Utility.setApplicationTitle(APPLICATION_NAME);

        inst = this;
        initLogger();
        createMenuBar();
        theView = new GMGenSystemView();
        GMBus.addToBus(this);
        PluginManager.getInstance().startAllPlugins();
        initComponents();
        initSettings();
        GMBus.send(new FetchOpenPCGRequestMessage(this));
        GMBus.send(new StateChangedMessage(this, editMenu));
        inst.setVisible(true);
    }

    /*
     * Fixes for Mac OS X look-and-feel menu problems.sk4p 12 Dec 2002
     */
    private void initialiseMacOS() {
        System.setProperty("com.apple.mrj.application.growbox.intrudes",
                "false");
        System.setProperty("com.apple.mrj.application.live-resize", "false");
        System.setProperty("com.apple.macos.smallTabs", "true");
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("com.apple.mrj.application.apple.menu.about.name",
                "GMGen");
        macOSXRegistration();
    }

    /**
     * Gets the build number of GMGen.
     * 
     * @return The build number
     * @since GMGen 3.3
     */
    public static String getBuild() {
        return "03.03.99.01.00";
    }

    /**
     * Returns the GMGen version as a human-readable string.
     * 
     * @return The version
     * @since GMGen 3.3
     */
    public static String getVersion() {
        return MiscUtilities.buildToVersion(getBuild());
    }

    /**
     * Calls the appropriate methods depending on the actions that happened on
     * the GUI.
     * 
     * @param event
     *            event that took place
     * @since GMGen 3.3
     */
    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == openFileItem) {
            GMBus.send(new FileOpenMessage(this));
        } else if (event.getSource() == exitFileItem) {
            GMBus.send(new WindowClosedMessage(this));
        } else if (event.getSource() == newFileItem) {
            GMBus.send(new LoadMessage(this));
        } else if (event.getSource() == saveFileItem) {
            GMBus.send(new SaveMessage(this));
        } else if (event.getSource() == cutEditItem) {
            GMBus.send(new ClipboardMessage(this, ClipboardMessage.CUT));
        } else if (event.getSource() == copyEditItem) {
            GMBus.send(new ClipboardMessage(this, ClipboardMessage.COPY));
        } else if (event.getSource() == pasteEditItem) {
            GMBus.send(new ClipboardMessage(this, ClipboardMessage.PASTE));
        }
    }

    /**
     * Clears the edit menu to allow a plugin to populate it.
     */
    public void clearEditMenu() {
        editMenu.removeAll();

        /**
         * Preferences on the Macintosh is in the application menu. See
         * macOSXRegistration()
         */
        if (!MAC_OS_X) {
            editMenu.add(editSeparator1);
            preferencesEditItem.setText("Preferences");
            editMenu.add(preferencesEditItem);
            preferencesEditItem.setEnabled(true);
            ActionListener[] listenerArray = preferencesEditItem
                    .getActionListeners();

            for (int i = 0; i < listenerArray.length; i++) {
                preferencesEditItem.removeActionListener(listenerArray[i]);
            }
            preferencesEditItem
                    .addActionListener(new java.awt.event.ActionListener() {
                        @Override
                        public void actionPerformed(
                                java.awt.event.ActionEvent evt) {
                            mPreferencesActionPerformed(evt);
                        }
                    });
        }
    }

    /**
     * Exits GMGen, the Mac way.
     */
    public void exitFormMac() {
        this.setVisible(false);
    }

    /**
     * Message handler for the GMBus.
     * 
     * @param message
     *            The message passed in from the bus
     * @since GMGen 3.3
     */
    @Override
    public void handleMessage(GMBMessage message) {
        // A plugin is asking for the creation of a new tab
        if (message instanceof TabAddMessage) {
            TabAddMessage tmessage = (TabAddMessage) message;
            if (tmessage.getSystem().equals(Constants.SYSTEM_GMGEN)) {
                Logging.debugPrint("Creating Tab "
                        + GMGenSystemView.getTabPane().getTabCount());
                theView.insertPane(tmessage.getName(), tmessage.getPane(),
                        GMGenSystemView.getTabPane().getTabCount());
            }
        } else if (message instanceof PreferencesPanelAddMessage) {
            PreferencesPanelAddMessage pmessage = (PreferencesPanelAddMessage) message;
            Logging.debugPrint("Creating Preferences Panel");
            rootNode.addPanel(pmessage.getName(), pmessage.getPane());
        }
        // A plugin is asking for the creation of a new option in the tool menu
        else if (message instanceof ToolMenuItemAddMessage) {
            ToolMenuItemAddMessage mmessage = (ToolMenuItemAddMessage) message;
            toolsMenu.add(mmessage.getMenuItem());
        } else if (message instanceof WindowClosedMessage) {
            setCloseSettings();
            // Karianna 07/03/2008 - Added a call to exitForm passing in no
            // window event
            // TODO This sequence of calls simply hides GMGen as opposed to
            // unloading it
            exitForm(null);
        }
    }

    /**
     * Handles the clicking on the tool menu.
     * 
     * @since GMGen 3.3
     */
    public void handleToolsMenu() {
        // TODO
    }

    /**
     * launches the preferences dialog on a mac.
     */
    public void mPreferencesActionPerformedMac() {
        PreferencesDialog dialog = new PreferencesDialog(this, true, rootNode);
        dialog.setVisible(true);
    }

    /**
     * Generic registration with the Mac OS X application menu. Checks the
     * platform, then attempts to register with the Apple EAWT.
     * 
     * This method calls OSXAdapter.registerMacOSXApplication() and
     * OSXAdapter.enablePrefs(). See OSXAdapter.java for the signatures of these
     * methods.
     */
    private void macOSXRegistration() {
        try {
            Class<?> osxAdapter = Class.forName("gmgen.util.OSXAdapter");
            Class<?>[] defArgs = { GMGenSystem.class };
            Method registerMethod = osxAdapter.getDeclaredMethod(
                    "registerMacOSXApplication", defArgs);

            if (registerMethod != null) {
                Object[] args = { this };
                registerMethod.invoke(osxAdapter, args);
            }

            // This is slightly gross. to reflectively access methods with
            // boolean args,
            // use "boolean.class", then pass a Boolean object in as the arg,
            // which apparently
            // gets converted for you by the reflection system.
            defArgs[0] = boolean.class;

            Method prefsEnableMethod = osxAdapter.getDeclaredMethod(
                    "enablePrefs", defArgs);

            if (prefsEnableMethod != null) {
                Object[] args = { Boolean.TRUE };
                prefsEnableMethod.invoke(osxAdapter, args);
            }
        } catch (NoClassDefFoundError e) {
            // This will be thrown first if the OSXAdapter is loaded on a system
            // without the EAWT
            // because OSXAdapter extends ApplicationAdapter in its def
            System.err
                    .println("This version of Mac OS X does not support the Apple EAWT.  Application Menu handling has been disabled ("
                            + e + ")");
        } catch (ClassNotFoundException e) {
            // This shouldn't be reached; if there's a problem with the
            // OSXAdapter we should get the
            // above NoClassDefFoundError first.
            System.err
                    .println("This version of Mac OS X does not support the Apple EAWT.  Application Menu handling has been disabled ("
                            + e + ")");
        } catch (Exception e) {
            System.err.println("Exception while loading the OSXAdapter = ["
                    + e.getMessage() + "]");
        }
    }

    /**
     * Handles a menu canceled event.
     * 
     * @param e
     *            menu canceled event
     * @since GMGen 3.3
     */
    @Override
    public void menuCanceled(MenuEvent e) {
        // TODO
    }

    /**
     * Handles a menu de-selected event.
     * 
     * @param e
     *            Menu Deselected event
     * @since GMGen 3.3
     */
    @Override
    public void menuDeselected(MenuEvent e) {
        // TODO
    }

    /**
     * Listens for menus to be clicked and calls the appropriate handlers.
     * 
     * @param e
     *            the menu event that happened.
     * @since GMGen 3.3
     */
    @Override
    public void menuSelected(MenuEvent e) {
        if (e.getSource() == toolsMenu) {
            handleToolsMenu();
        }
    }

    /**
     * Calls the necessary methods if an item on the GUI or model has changed.
     * 
     * @param event - The event that has happened.
     */
    @Override
    public void stateChanged(ChangeEvent event) {
        stateUpdate(event);
    }

    /**
     * Calls the necessary methods if an item on the GUI or model has changed.
     * 
     * @param event - The event that has happened.
     */
    private void stateUpdate(EventObject event) {
        newFileItem.setEnabled(false);
        openFileItem.setEnabled(false);
        saveFileItem.setEnabled(false);
        clearEditMenu();
        GMBus.send(new StateChangedMessage(this, editMenu));
    }


    // Sets a bunch of properties based on the status of GMGen at close.
    private void setCloseSettings() {
        SettingsHandler.setGMGenOption("WindowX", this.getX());
        SettingsHandler.setGMGenOption("WindowY", this.getY());
        SettingsHandler.setGMGenOption("WindowWidth", this.getSize().width);
        SettingsHandler.setGMGenOption("WindowHeight", this.getSize().height);

        // Maximized state of the window
        if ((getExtendedState() & Frame.MAXIMIZED_BOTH) != 0) {
            SettingsHandler.setGMGenOption("WindowState", Frame.MAXIMIZED_BOTH);
        } else if ((getExtendedState() & Frame.MAXIMIZED_HORIZ) != 0) {
            SettingsHandler
                    .setGMGenOption("WindowState", Frame.MAXIMIZED_HORIZ);
        } else if ((getExtendedState() & Frame.MAXIMIZED_VERT) != 0) {
            SettingsHandler.setGMGenOption("WindowState", Frame.MAXIMIZED_VERT);
        } else {
            SettingsHandler.setGMGenOption("WindowState", Frame.NORMAL);
        }
    }

    // Sets all the panes on the GUI in the correct order.
    private void setTabbedPanes() {
        try {
            GMGenSystemView.getTabPane().setSelectedIndex(0);
            theView.showPane();
        } catch (Exception e) {
            // TODO
        }
    }

    // Creates the MenuBar for the application.
    private void createMenuBar() {
        systemMenuBar = new JMenuBar();
        createFileMenu();
        createEditMenu();
        createToolsMenu();
        setJMenuBar(systemMenuBar);
        setDefaultEnablementOfMenuItems();
        pack();
    }

    // Enable or Disable menu items at initialisation time
    private void setDefaultEnablementOfMenuItems() {
        openFileItem.setEnabled(true);
        saveFileItem.setEnabled(false);
        newFileItem.setEnabled(false);
        cutEditItem.setEnabled(false);
        copyEditItem.setEnabled(false);
        pasteEditItem.setEnabled(false);
        preferencesEditItem.setEnabled(true);
        versionToolsItem.setEnabled(false);
    }

    // Create tools menu
    private void createToolsMenu() {
        toolsMenu = new JMenu();
        toolsSeparator1 = new JSeparator();
        versionToolsItem = new JMenuItem();
        
        toolsMenu.setText("Tools");
        toolsMenu.setMnemonic('T');
        toolsMenu.addMenuListener(this);

        versionToolsItem.setMnemonic('G');
        versionToolsItem.setText("Get Newest Version");
        toolsMenu.add(versionToolsItem);

        toolsMenu.add(toolsSeparator1);

        systemMenuBar.add(toolsMenu);
    }

    // Create the edit menu
    private void createEditMenu() {
        editMenu = new JMenu();
        cutEditItem = new JMenuItem();
        copyEditItem = new JMenuItem();
        pasteEditItem = new JMenuItem();
        editSeparator1 = new JSeparator();
        preferencesEditItem = new JMenuItem();
        
        // EDIT MENU
        editMenu.setText("Edit");
        editMenu.setMnemonic('E');
        editMenu.addMenuListener(this);

        cutEditItem.setText("Cut");
        editMenu.add(cutEditItem);

        copyEditItem.setText("Copy");
        editMenu.add(copyEditItem);

        pasteEditItem.setText("Paste");
        editMenu.add(pasteEditItem);

        // Preferences... on MAC OS X is in the application menu. See macOSXRegistration()
        if (!MAC_OS_X) {
            editMenu.add(editSeparator1);

            preferencesEditItem.setText("Preferences");
            editMenu.add(preferencesEditItem);
            preferencesEditItem.setEnabled(true);

            ActionListener[] listenerArray = preferencesEditItem
                    .getActionListeners();
            for (int i = 0; i < listenerArray.length; i++) {
                preferencesEditItem.removeActionListener(listenerArray[i]);
            }

            preferencesEditItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    mPreferencesActionPerformed(evt);
                }
            });
        }

        systemMenuBar.add(editMenu);
    }

    // Create the file menu
    private void createFileMenu() {
        fileMenu = new JMenu();
        newFileItem = new JMenuItem();
        openFileItem = new JMenuItem();
        fileSeparator1 = new JSeparator();
        saveFileItem = new JMenuItem();
        fileSeparator2 = new JSeparator();
        exitFileItem = new JMenuItem();
        
        fileMenu.setText("File");
        fileMenu.setMnemonic('F');
        fileMenu.addMenuListener(this);

        createFileNewMenuItem();
        createFileOpenMenuItem();
        fileMenu.add(fileSeparator1);
        createFileSaveMenuItem();

        // Exit is quit on the Macintosh is in the application menu. See macOSXRegistration()
        if (!MAC_OS_X) {
            exitForMacOSX();
        }

        systemMenuBar.add(fileMenu);
    }

    /**
     * 
     */
    private void createFileSaveMenuItem() {
        saveFileItem.setMnemonic('S');
        saveFileItem.setText("Save");
        fileMenu.add(saveFileItem);
        saveFileItem.addActionListener(this);
    }

    /**
     * 
     */
    private void createFileOpenMenuItem() {
        openFileItem.setMnemonic('O');
        openFileItem.setText("Open");
        fileMenu.add(openFileItem);
        openFileItem.addActionListener(this);
    }

    /**
     * 
     */
    private void createFileNewMenuItem() {
        newFileItem.setMnemonic('N');
        newFileItem.setText("New");
        newFileItem.addActionListener(this);
        fileMenu.add(newFileItem);
    }

    /**
     * 
     */
    private void exitForMacOSX() {
        fileMenu.add(fileSeparator2);
        exitFileItem.setMnemonic('x');
        exitFileItem.setText("Exit");
        fileMenu.add(exitFileItem);
        exitFileItem.addActionListener(this);
    }

    /**
     * Closes and exits the application cleanly.
     * 
     * @param event
     *            - a window close event
     * @since GMGen 3.3
     */
    private void exitForm(WindowEvent event) {
        this.setVisible(false);
    }

    /**
     * Initializes all the GUI components and places them in the correct place
     * on the GUI.
     * 
     * @since GMGen 3.3
     */
    private void initComponents() {
        getContentPane().setLayout(new BorderLayout());
        setTabbedPanes();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent evt) {
                exitForm(evt);
            }
        });

        addWindowFocusListener(new java.awt.event.WindowFocusListener() {
            @Override
            public void windowGainedFocus(java.awt.event.WindowEvent e) {
                stateUpdate(e);
            }

            @Override
            public void windowLostFocus(java.awt.event.WindowEvent e) {
                // Intentionally left blank because WindowFocusListener requires
                // the method to be implemented.
            }
        });

        // sourceView.getLoadButton().addActionListener(this);
        // sourceView.getUnloadAllButton().addActionListener(this);
        // sourceView.getRemoveAllButton().addActionListener(this);
        GMGenSystemView.getTabPane().addChangeListener(this);
        getContentPane().add(theView, BorderLayout.CENTER);

        setIconImage(Icons.createImageIcon("gmgen_icon.png").getImage());
    }

    // Initializes the Logger component.
    private void initLogger() {
        boolean logging = SettingsHandler.getGMGenOption("Logging.On", false);
        LogUtilities.inst().setLogging(logging);
    }

    // Initializes the settings, and implements their commands.
    private void initSettings() {
        int iWinX = SettingsHandler.getGMGenOption("WindowX", 0);
        int iWinY = SettingsHandler.getGMGenOption("WindowY", 0);
        setLocation(iWinX, iWinY);

        int iWinWidth = SettingsHandler.getGMGenOption("WindowWidth", 750);
        int iWinHeight = SettingsHandler.getGMGenOption("WindowHeight", 580);
        setSize(iWinWidth, iWinHeight);

        int windowState = SettingsHandler.getGMGenOption("WindowState",
                Frame.NORMAL);

        if (windowState != Frame.NORMAL) {
            setExtendedState(windowState);
        }

    }

    private void mPreferencesActionPerformed(ActionEvent event) {
        PreferencesDialog dialog = new PreferencesDialog(this, true, rootNode);
        dialog.setVisible(true);
    }

    private class Renderer extends SwingWorker {

        @Override
        public Object construct() {
            return "";
        }

        @Override
        public void finished() {
            GMGenSystem.this.initialize();
        }
    }

}
