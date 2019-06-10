/*
 *  Copyright (C) 2003 Devon Jones, Emily Smirle
 * Copyright 2019 Timothy Reaves <treaves@silverfieldstech.com>
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
 */
package gmgen;

import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.EventObject;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

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
import pcgen.cdom.base.Constants;
import pcgen.core.SettingsHandler;
import pcgen.gui2.PCGenActionMap;
import pcgen.gui2.dialog.PreferencesDialog;
import pcgen.gui2.tools.CommonMenuText;
import pcgen.gui2.tools.Icons;
import pcgen.gui3.application.DesktopHandler;
import pcgen.gui3.preferences.GMGenPreferencesModel;
import pcgen.pluginmgr.PCGenMessage;
import pcgen.pluginmgr.PCGenMessageHandler;
import pcgen.pluginmgr.PluginManager;
import pcgen.pluginmgr.messages.FocusOrStateChangeOccurredMessage;
import pcgen.pluginmgr.messages.RequestFileOpenedMessageForCurrentlyOpenedPCsMessage;
import pcgen.system.LanguageBundle;
import pcgen.system.PCGenPropBundle;
import pcgen.util.Logging;

import org.apache.commons.lang3.SystemUtils;

/**
 * {@code GMGenSystem} is the main class of the GMGen application.
 *
 * It holds the controller for every tab as well as the menu bar.
 */
public final class GMGenSystem extends JFrame
		implements ChangeListener, ActionListener, PCGenMessageHandler
{

	// menu elements used with CommonMenuText.name(...)
	private static final String MNU_SAVE = "mnuSave"; //$NON-NLS-1$
	private static final String MNU_OPEN = "mnuOpen"; //$NON-NLS-1$
	private static final String MNU_EXIT = "mnuClose"; //$NON-NLS-1$
	private static final String MNU_NEW = "mnuNew"; //$NON-NLS-1$
	private static final String MNU_CUT = "mnuCut"; //$NON-NLS-1$
	private static final String MNU_COPY = "mnuCopy"; //$NON-NLS-1$
	private static final String MNU_PASTE = "mnuPaste"; //$NON-NLS-1$

	private static final String SETTING_LOGGING_ON = "Logging.On"; //$NON-NLS-1$

	/**
	 * Holds an instance of the top window, so components and windows can get
	 * their parent frame.
	 */
	public static GMGenSystem inst;

	// The main <code>JPanel</code> view for the system.
	private GMGenSystemView theView;

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

	// Tree for the preferences dialog
	private final GMGenPreferencesModel rootNode = new GMGenPreferencesModel();

	private final PCGenMessageHandler messageHandler;

	private final PluginManager pluginManager;

	/**
	 * Constructor
	 *
	 * Creates a JFrame TODO comment correctly.
	 * Starts the GMGen renderer
	 */
	public GMGenSystem()
	{
		super(LanguageBundle.getFormattedString("in_gmgen_frameTitle", APPLICATION_NAME)); //$NON-NLS-1$
		pluginManager = PluginManager.getInstance();
		messageHandler = pluginManager.getPostbox();
		this.initialize();
	}

	private void initialize()
	{
		DesktopHandler.initialize();
		inst = this;
		initLogger();
		createMenuBar();
		theView = new GMGenSystemView();
		pluginManager.addMember(this);
		PluginManager.getInstance().startAllPlugins();
		initComponents();
		messageHandler.handleMessage(new RequestFileOpenedMessageForCurrentlyOpenedPCsMessage(this));
		messageHandler.handleMessage(new FocusOrStateChangeOccurredMessage(this, editMenu));
		inst.setVisible(true);
		pack();
	}

	/**
	 * Returns the GMGen version as a human-readable string.
	 *
	 * @return The version
	 */
	public static String getVersion()
	{
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
	public void actionPerformed(ActionEvent event)
	{
		if (event.getSource() == openFileItem)
		{
			messageHandler.handleMessage(new FileMenuOpenMessage(this));
		}
		else if (event.getSource() == exitFileItem)
		{
			messageHandler.handleMessage(new GMGenBeingClosedMessage(this));
		}
		else if (event.getSource() == newFileItem)
		{
			messageHandler.handleMessage(new FileMenuNewMessage(this));
		}
		else if (event.getSource() == saveFileItem)
		{
			messageHandler.handleMessage(new FileMenuSaveMessage(this));
		}
		else if (event.getSource() == cutEditItem)
		{
			messageHandler.handleMessage(new EditMenuCutSelectionMessage(this));
		}
		else if (event.getSource() == copyEditItem)
		{
			messageHandler.handleMessage(new EditMenuCopySelectionMessage(this));
		}
		else if (event.getSource() == pasteEditItem)
		{
			messageHandler.handleMessage(new EditMenuPasteSelectionMessage(this));
		}
	}

	/**
	 * Clears the edit menu to allow a plugin to populate it.
	 */
	private void clearEditMenu()
	{
		editMenu.removeAll();

		/*
		 * Preferences on the Macintosh is in the application menu.
		 */
		editMenu.add(new JSeparator());
		CommonMenuText.name(preferencesEditItem, PCGenActionMap.MNU_TOOLS_PREFERENCES);
		editMenu.add(preferencesEditItem);
		preferencesEditItem.setEnabled(true);
		ActionListener[] listenerArray = preferencesEditItem.getActionListeners();

		for (final ActionListener aListenerArray : listenerArray)
		{
			preferencesEditItem.removeActionListener(aListenerArray);
		}
		preferencesEditItem.addActionListener(this::mPreferencesActionPerformed);
	}

	/**
	 * Message handler for the GMBus.
	 *
	 * @param message
	 *            The message passed in from the bus
	 */
	@Override
	public void handleMessage(PCGenMessage message)
	{
		// A plugin is asking for the creation of a new tab
		if (message instanceof RequestAddTabToGMGenMessage)
		{
			RequestAddTabToGMGenMessage tmessage = (RequestAddTabToGMGenMessage) message;
			Logging.debugPrint("Creating Tab " + GMGenSystemView.getTabPane().getTabCount());
			GMGenSystemView.insertPane(tmessage.getName(), tmessage.getPane(),
				GMGenSystemView.getTabPane().getTabCount());
		}
		else if (message instanceof RequestAddPreferencesPanelMessage)
		{
			RequestAddPreferencesPanelMessage pmessage = (RequestAddPreferencesPanelMessage) message;
			Logging.debugPrint("Creating Preferences Panel");
			rootNode.addPanel(pmessage.getName(), pmessage.getPrefsPanel());
		}
		// A plugin is asking for the creation of a new option in the tool menu
		else if (message instanceof AddMenuItemToGMGenToolsMenuMessage)
		{
			AddMenuItemToGMGenToolsMenuMessage mmessage = (AddMenuItemToGMGenToolsMenuMessage) message;
			toolsMenu.add(mmessage.getMenuItem());
		}
		else if (message instanceof GMGenBeingClosedMessage)
		{
			// Karianna 07/03/2008 - Added a call to exitForm passing in no
			// window event
			// TODO This sequence of calls simply hides GMGen as opposed to
			// unloading it
			exitForm(null);
		}
	}


	/**
	 * Calls the necessary methods if an item on the GUI or model has changed.
	 *
	 * @param event - The event that has happened.
	 */
	@Override
	public void stateChanged(ChangeEvent event)
	{
		stateUpdate(event);
	}

	/**
	 * Calls the necessary methods if an item on the GUI or model has changed.
	 *
	 * @param event - The event that has happened.
	 */
	private void stateUpdate(EventObject event)
	{
		newFileItem.setEnabled(false);
		openFileItem.setEnabled(false);
		saveFileItem.setEnabled(false);
		clearEditMenu();
		messageHandler.handleMessage(new FocusOrStateChangeOccurredMessage(this, editMenu));
	}

	// Sets all the panes on the GUI in the correct order.
	private void setTabbedPanes()
	{
		try
		{
			GMGenSystemView.getTabPane().setSelectedIndex(0);
			theView.showPane();
		}
		catch (RuntimeException e)
		{
			// TODO
		}
	}

	// Creates the MenuBar for the application.
	private void createMenuBar()
	{
		systemMenuBar = new JMenuBar();
		createFileMenu();
		createEditMenu();
		createToolsMenu();
		setJMenuBar(systemMenuBar);
		setDefaultEnablementOfMenuItems();
		pack();
	}

	// Enable or Disable menu items at initialization time
	private void setDefaultEnablementOfMenuItems()
	{
		openFileItem.setEnabled(true);
		saveFileItem.setEnabled(false);
		newFileItem.setEnabled(false);
		cutEditItem.setEnabled(false);
		copyEditItem.setEnabled(false);
		pasteEditItem.setEnabled(false);
		preferencesEditItem.setEnabled(true);
	}

	// Create tools menu
	private void createToolsMenu()
	{
		toolsMenu = new JMenu();
		CommonMenuText.name(toolsMenu, PCGenActionMap.MNU_TOOLS);
		systemMenuBar.add(toolsMenu);
	}

	// Create the edit menu
	private void createEditMenu()
	{
		editMenu = new JMenu();
		cutEditItem = new JMenuItem();
		copyEditItem = new JMenuItem();
		pasteEditItem = new JMenuItem();
		preferencesEditItem = new JMenuItem();

		// EDIT MENU
		CommonMenuText.name(editMenu, PCGenActionMap.MNU_EDIT);

		CommonMenuText.name(cutEditItem, MNU_CUT);
		editMenu.add(cutEditItem);

		CommonMenuText.name(copyEditItem, MNU_COPY);
		editMenu.add(copyEditItem);

		CommonMenuText.name(pasteEditItem, MNU_PASTE);
		editMenu.add(pasteEditItem);

		editMenu.add(new JSeparator());

		CommonMenuText.name(preferencesEditItem, PCGenActionMap.MNU_TOOLS_PREFERENCES);
		editMenu.add(preferencesEditItem);
		preferencesEditItem.setEnabled(true);

		ActionListener[] listenerArray = preferencesEditItem.getActionListeners();
		for (final ActionListener aListenerArray : listenerArray)
		{
			preferencesEditItem.removeActionListener(aListenerArray);
		}

		preferencesEditItem.addActionListener(this::mPreferencesActionPerformed);

		systemMenuBar.add(editMenu);
	}

	// Create the file menu
	private void createFileMenu()
	{
		fileMenu = new JMenu();
		newFileItem = new JMenuItem();
		openFileItem = new JMenuItem();
		saveFileItem = new JMenuItem();
		exitFileItem = new JMenuItem();

		CommonMenuText.name(fileMenu, PCGenActionMap.MNU_FILE);

		createFileNewMenuItem();
		createFileOpenMenuItem();
		fileMenu.add(new JSeparator());
		createFileSaveMenuItem();

		// Exit is quit on the Macintosh is in the application menu.
		if (!SystemUtils.IS_OS_MAC_OSX)
		{
			exitForMacOSX();
		}

		systemMenuBar.add(fileMenu);
	}

	private void createFileSaveMenuItem()
	{
		CommonMenuText.name(saveFileItem, MNU_SAVE);
		fileMenu.add(saveFileItem);
		saveFileItem.addActionListener(this);
	}

	private void createFileOpenMenuItem()
	{
		CommonMenuText.name(openFileItem, MNU_OPEN);
		fileMenu.add(openFileItem);
		openFileItem.addActionListener(this);
	}

	private void createFileNewMenuItem()
	{
		CommonMenuText.name(newFileItem, MNU_NEW);
		newFileItem.addActionListener(this);
		fileMenu.add(newFileItem);
	}

	private void exitForMacOSX()
	{
		fileMenu.add(new JSeparator());
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
	private void exitForm(WindowEvent event)
	{
		this.setVisible(false);
	}

	/**
	 * Initializes all the GUI components and places them in the correct place
	 * on the GUI.
	 *
	 */
	private void initComponents()
	{
		getContentPane().setLayout(new BorderLayout());
		setTabbedPanes();

		addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent evt)
			{
				exitForm(evt);
			}
		});

		addWindowFocusListener(new java.awt.event.WindowFocusListener()
		{
			@Override
			public void windowGainedFocus(java.awt.event.WindowEvent e)
			{
				stateUpdate(e);
			}

			@Override
			public void windowLostFocus(java.awt.event.WindowEvent e)
			{
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
	private static void initLogger()
	{
		boolean logging = SettingsHandler.getGMGenOption(SETTING_LOGGING_ON, false);
		LogUtilities.inst().setLogging(logging);
	}

	private void mPreferencesActionPerformed(ActionEvent event)
	{
		Window dialog = new PreferencesDialog(this, rootNode, Constants.SYSTEM_GMGEN);
		dialog.setVisible(true);
	}

}
