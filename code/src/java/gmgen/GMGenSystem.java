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
import gmgen.pluginmgr.messages.AddMenuItemToGMGenToolsMenuMessage;
import gmgen.pluginmgr.messages.EditMenuCopySelectionMessage;
import gmgen.pluginmgr.messages.EditMenuCutSelectionMessage;
import gmgen.pluginmgr.messages.EditMenuPasteSelectionMessage;
import gmgen.pluginmgr.messages.FileMenuNewMessage;
import gmgen.pluginmgr.messages.FileMenuOpenMessage;
import gmgen.pluginmgr.messages.FileMenuSaveMessage;
import gmgen.pluginmgr.messages.GMGenBeingClosedMessage;
import gmgen.pluginmgr.messages.RequestAddPreferencesPanelMessage;
import gmgen.pluginmgr.messages.RequestAddTabToGMGenMessage;
import gmgen.util.LogUtilities;

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

import org.apache.commons.lang3.SystemUtils;

import pcgen.core.SettingsHandler;
import pcgen.gui2.PCGenActionMap;
import pcgen.gui2.tools.CommonMenuText;
import pcgen.gui2.tools.Icons;
import pcgen.gui2.tools.Utility;
import pcgen.pluginmgr.PCGenMessage;
import pcgen.pluginmgr.PCGenMessageHandler;
import pcgen.pluginmgr.PluginManager;
import pcgen.pluginmgr.messages.FocusOrStateChangeOccurredMessage;
import pcgen.pluginmgr.messages.RequestFileOpenedMessageForCurrentlyOpenedPCsMessage;
import pcgen.system.LanguageBundle;
import pcgen.system.PCGenPropBundle;
import pcgen.util.Logging;
import pcgen.util.SwingWorker;


/**
 * {@code GMGenSystem} is the main class of the GMGen application.
 * 
 * It holds the controller for every tab as well as the menu bar.
 */
public final class GMGenSystem extends JFrame implements ChangeListener,
        MenuListener, ActionListener, PCGenMessageHandler {
	
	// Serial UID
    private static final long serialVersionUID = -7372446160499882872L;

    // menu elements used with CommonMenuText.name(...)
	private static final String MNU_SAVE = "mnuSave"; //$NON-NLS-1$
	private static final String MNU_OPEN = "mnuOpen"; //$NON-NLS-1$
	private static final String MNU_EXIT = "mnuClose"; //$NON-NLS-1$
	private static final String MNU_NEW = "mnuNew"; //$NON-NLS-1$
	private static final String MNU_CUT = "mnuCut"; //$NON-NLS-1$
	private static final String MNU_COPY = "mnuCopy"; //$NON-NLS-1$
	private static final String MNU_PASTE = "mnuPaste"; //$NON-NLS-1$

	// Settings keys
	private static final String SETTING_WINDOW_STATE = "WindowState"; //$NON-NLS-1$
	private static final String SETTING_WINDOW_HEIGHT = "WindowHeight"; //$NON-NLS-1$
	private static final String SETTING_WINDOW_WIDTH = "WindowWidth"; //$NON-NLS-1$
	private static final String WINDOW_Y = "WindowY"; //$NON-NLS-1$
	private static final String SETTING_WINDOW_X = "WindowX"; //$NON-NLS-1$
	private static final String SETTING_LOGGING_ON = "Logging.On"; //$NON-NLS-1$
	
    /**
     * Holds an instance of the top window, so components and windows can get
     * their parent frame.
     */
    public static GMGenSystem inst;

    // The main <code>JPanel</code> view for the system.
    private GMGenSystemView theView;

    /** {@code true} if this is a Mac OS X system. */
    private static final boolean MAC_OS_X = SystemUtils.IS_OS_MAC_OSX;
    
    /** GMGen Application name */
    public static final String APPLICATION_NAME = "GMGen"; //$NON-NLS-1$

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

    private final PCGenMessageHandler messageHandler;

	private PluginManager pluginManager;

    /**
     * Constructor
     * 
     * Creates a JFrame TODO comment correctly.
     * Starts the GMGen renderer
     */
    public GMGenSystem() {
        super(LanguageBundle.getFormattedString("in_gmgen_frameTitle", APPLICATION_NAME)); //$NON-NLS-1$
        pluginManager = PluginManager.getInstance();
        messageHandler = pluginManager.getPostbox();
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
        pluginManager.addMember(this);
        PluginManager.getInstance().startAllPlugins();
        initComponents();
        initSettings();
        messageHandler.handleMessage(new RequestFileOpenedMessageForCurrentlyOpenedPCsMessage(this));
        messageHandler.handleMessage(new FocusOrStateChangeOccurredMessage(this, editMenu));
        inst.setVisible(true);
    }

    /*
     * Fixes for Mac OS X look-and-feel menu problems.sk4p 12 Dec 2002
     */
    // TODO factorize. PCGen must be doing the same thing.
    private void initialiseMacOS() {
        System.setProperty("com.apple.mrj.application.growbox.intrudes",  //$NON-NLS-1$
                "false"); //$NON-NLS-1$
        System.setProperty("com.apple.mrj.application.live-resize", "false"); //$NON-NLS-1$ //$NON-NLS-2$
        System.setProperty("com.apple.macos.smallTabs", "true"); //$NON-NLS-1$ //$NON-NLS-2$
        System.setProperty("apple.laf.useScreenMenuBar", "true"); //$NON-NLS-1$ //$NON-NLS-2$
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", //$NON-NLS-1$
                APPLICATION_NAME);
        macOSXRegistration();
    }

    /**
     * Returns the GMGen version as a human-readable string.
     * 
     * @return The version
     */
    public static String getVersion() {
        return PCGenPropBundle.getVersionNumber();
    }

    /**
     * Calls the appropriate methods depending on the actions that happened on
     * the GUI.
     * 
     * @param event
     *            event that took place
     */
    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == openFileItem) {
            messageHandler.handleMessage(new FileMenuOpenMessage(this));
        } else if (event.getSource() == exitFileItem) {
            messageHandler.handleMessage(new GMGenBeingClosedMessage(this));
        } else if (event.getSource() == newFileItem) {
            messageHandler.handleMessage(new FileMenuNewMessage(this));
        } else if (event.getSource() == saveFileItem) {
            messageHandler.handleMessage(new FileMenuSaveMessage(this));
        } else if (event.getSource() == cutEditItem) {
            messageHandler.handleMessage(new EditMenuCutSelectionMessage(this));
        } else if (event.getSource() == copyEditItem) {
            messageHandler.handleMessage(new EditMenuCopySelectionMessage(this));
        } else if (event.getSource() == pasteEditItem) {
            messageHandler.handleMessage(new EditMenuPasteSelectionMessage(this));
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
            CommonMenuText.name(preferencesEditItem, PCGenActionMap.MNU_TOOLS_PREFERENCES);
            editMenu.add(preferencesEditItem);
            preferencesEditItem.setEnabled(true);
            ActionListener[] listenerArray = preferencesEditItem
                    .getActionListeners();

            for (final ActionListener aListenerArray : listenerArray)
            {
                preferencesEditItem.removeActionListener(aListenerArray);
            }
            preferencesEditItem
                    .addActionListener(this::mPreferencesActionPerformed);
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
     */
    @Override
    public void handleMessage(PCGenMessage message) {
        // A plugin is asking for the creation of a new tab
        if (message instanceof RequestAddTabToGMGenMessage) {
            RequestAddTabToGMGenMessage tmessage = (RequestAddTabToGMGenMessage) message;
            Logging.debugPrint("Creating Tab "
                    + GMGenSystemView.getTabPane().getTabCount());
            theView.insertPane(tmessage.getName(), tmessage.getPane(),
                    GMGenSystemView.getTabPane().getTabCount());
        } else if (message instanceof RequestAddPreferencesPanelMessage) {
        	RequestAddPreferencesPanelMessage pmessage = (RequestAddPreferencesPanelMessage) message;
            Logging.debugPrint("Creating Preferences Panel");
            rootNode.addPanel(pmessage.getName(), pmessage.getPrefsPanel());
        }
        // A plugin is asking for the creation of a new option in the tool menu
        else if (message instanceof AddMenuItemToGMGenToolsMenuMessage) {
            AddMenuItemToGMGenToolsMenuMessage mmessage = (AddMenuItemToGMGenToolsMenuMessage) message;
            toolsMenu.add(mmessage.getMenuItem());
        } else if (message instanceof GMGenBeingClosedMessage) {
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
        } catch (NoClassDefFoundError | ClassNotFoundException e) {
            // This will be thrown first if the OSXAdapter is loaded on a system
            // without the EAWT
            // because OSXAdapter extends ApplicationAdapter in its def
        	// TODO Use Logging
            System.err
                    .println("This version of Mac OS X does not support the Apple EAWT.  Application Menu handling has been disabled ("
                            + e + ")");
        } catch (Exception e) {
        	// TODO Use Logging
            System.err.println("Exception while loading the OSXAdapter = ["
                    + e.getMessage() + "]");
        }
    }

    /**
     * Handles a menu canceled event.
     * 
     * @param e
     *            menu canceled event
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
        messageHandler.handleMessage(new FocusOrStateChangeOccurredMessage(this, editMenu));
    }


    // Sets a bunch of properties based on the status of GMGen at close.
    private void setCloseSettings() {
        SettingsHandler.setGMGenOption(SETTING_WINDOW_X, this.getX());
        SettingsHandler.setGMGenOption(WINDOW_Y, this.getY());
        SettingsHandler.setGMGenOption(SETTING_WINDOW_WIDTH, this.getSize().width);
        SettingsHandler.setGMGenOption(SETTING_WINDOW_HEIGHT, this.getSize().height);

        // Maximized state of the window
        if ((getExtendedState() & Frame.MAXIMIZED_BOTH) != 0) {
            SettingsHandler.setGMGenOption(SETTING_WINDOW_STATE, Frame.MAXIMIZED_BOTH);
        } else if ((getExtendedState() & Frame.MAXIMIZED_HORIZ) != 0) {
            SettingsHandler
                    .setGMGenOption(SETTING_WINDOW_STATE, Frame.MAXIMIZED_HORIZ);
        } else if ((getExtendedState() & Frame.MAXIMIZED_VERT) != 0) {
            SettingsHandler.setGMGenOption(SETTING_WINDOW_STATE, Frame.MAXIMIZED_VERT);
        } else {
            SettingsHandler.setGMGenOption(SETTING_WINDOW_STATE, Frame.NORMAL);
        }
    }

    // Sets all the panes on the GUI in the correct order.
    private void setTabbedPanes() {
        try {
            GMGenSystemView.getTabPane().setSelectedIndex(0);
            theView.showPane();
        } catch (RuntimeException e) {
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

    // Enable or Disable menu items at initialization time
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
        
        CommonMenuText.name(toolsMenu, PCGenActionMap.MNU_TOOLS);
        toolsMenu.addMenuListener(this);

        CommonMenuText.name(versionToolsItem, "mnuGetNew"); //$NON-NLS-1$
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
		CommonMenuText.name(editMenu, PCGenActionMap.MNU_EDIT);
        editMenu.addMenuListener(this);

        CommonMenuText.name(cutEditItem, MNU_CUT);
        editMenu.add(cutEditItem);

        CommonMenuText.name(copyEditItem, MNU_COPY);
        editMenu.add(copyEditItem);

        CommonMenuText.name(pasteEditItem, MNU_PASTE);
        editMenu.add(pasteEditItem);

        // Preferences... on MAC OS X is in the application menu. See macOSXRegistration()
        if (!MAC_OS_X) {
            editMenu.add(editSeparator1);

            CommonMenuText.name(preferencesEditItem, PCGenActionMap.MNU_TOOLS_PREFERENCES);
            editMenu.add(preferencesEditItem);
            preferencesEditItem.setEnabled(true);

            ActionListener[] listenerArray = preferencesEditItem
                    .getActionListeners();
            for (final ActionListener aListenerArray : listenerArray)
            {
                preferencesEditItem.removeActionListener(aListenerArray);
            }

            preferencesEditItem.addActionListener(this::mPreferencesActionPerformed);
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
        
        CommonMenuText.name(fileMenu, PCGenActionMap.MNU_FILE);
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
        CommonMenuText.name(saveFileItem, MNU_SAVE);
        fileMenu.add(saveFileItem);
        saveFileItem.addActionListener(this);
    }

    /**
     * 
     */
    private void createFileOpenMenuItem() {
        CommonMenuText.name(openFileItem, MNU_OPEN);
        fileMenu.add(openFileItem);
        openFileItem.addActionListener(this);
    }

    /**
     * 
     */
    private void createFileNewMenuItem() {
    	CommonMenuText.name(newFileItem, MNU_NEW);
        newFileItem.addActionListener(this);
        fileMenu.add(newFileItem);
    }

    /**
     * 
     */
    private void exitForMacOSX() {
        fileMenu.add(fileSeparator2);
    	CommonMenuText.name(exitFileItem, MNU_EXIT);
        fileMenu.add(exitFileItem);
        exitFileItem.addActionListener(this);
    }

    /**
     * Closes and exits the application cleanly.
     * 
     * @param event
     *            - a window close event
     */
    private void exitForm(WindowEvent event) {
        this.setVisible(false);
    }

    /**
     * Initializes all the GUI components and places them in the correct place
     * on the GUI.
     * 
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

        setIconImage(Icons.gmgen_icon.getImageIcon().getImage());
    }

    // Initializes the Logger component.
    private void initLogger() {
        boolean logging = SettingsHandler.getGMGenOption(SETTING_LOGGING_ON, false);
        LogUtilities.inst().setLogging(logging);
    }

    // Initializes the settings, and implements their commands.
    private void initSettings() {
        int iWinX = SettingsHandler.getGMGenOption(SETTING_WINDOW_X, 0);
        int iWinY = SettingsHandler.getGMGenOption(WINDOW_Y, 0);
        setLocation(iWinX, iWinY);

        int iWinWidth = SettingsHandler.getGMGenOption(SETTING_WINDOW_WIDTH, 750);
        int iWinHeight = SettingsHandler.getGMGenOption(SETTING_WINDOW_HEIGHT, 580);
        setSize(iWinWidth, iWinHeight);

        int windowState = SettingsHandler.getGMGenOption(SETTING_WINDOW_STATE,
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
