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
import gmgen.pluginmgr.PluginLoader;
import gmgen.pluginmgr.messages.ClipboardMessage;
import gmgen.pluginmgr.messages.FetchOpenPCGRequestMessage;
import gmgen.pluginmgr.messages.FileOpenMessage;
import gmgen.pluginmgr.messages.LoadMessage;
import gmgen.pluginmgr.messages.PCLoadedMessage;
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
import java.awt.Image;
import java.awt.Toolkit;
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

import pcgen.cdom.base.Constants;
import pcgen.core.SettingsHandler;
import pcgen.util.Logging;
import pcgen.util.SwingWorker;

/**
 *  <code>GMGenSystem</code> is the main class of this application. This class
 *  holds the contoller of every section, and the menu bar.
 *
 *@author     Expires 2003
 *@since    May 30, 2003
 *@version    3.3
 *@since      GMGen 3.3
 */
public final class GMGenSystem extends JFrame implements ChangeListener,
		MenuListener, ActionListener, GMBComponent
{
	/**
	 *  Holds an instance of the top window, so components and windows can get
	 *  their parent frame.
	 */
	public static GMGenSystem inst;

	/**
	 *  Boolean true if this is a Macintosh system.
	 */
	public static final boolean MAC_OS_X =
			(System.getProperty("os.name").equals("Mac OS X"));

	/**
	 *  The copy menu item in the edit menu.
	 */
	public JMenuItem copyEditItem;

	/**
	 *  The cut menu item in the edit menu.
	 */
	public JMenuItem cutEditItem;

	/**
	 *  The new menu item in the file menu.
	 */
	public JMenuItem newFileItem;

	/**
	 *  The open menu item in the file menu.
	 */
	public JMenuItem openFileItem;

	/**
	 *  The paste menu item in the edit menu.
	 */
	public JMenuItem pasteEditItem;

	/**
	 *  The preferences menu item in the edit menu.
	 */
	public JMenuItem preferencesEditItem;

	/**
	 *  The save menu item in the file menu.
	 */
	public JMenuItem saveFileItem;

	/**
	 *  The source loader section.
	 */

	//private SourceView sourceView;
	/**
	 *  The main <code>JPanel</code> view for the system.
	 */
	private GMGenSystemView theView;

	/**
	 *  The edit menu.
	 */
	private JMenu editMenu;

	/**
	 *  The file menu.
	 */
	private JMenu fileMenu;

	/**
	 *  The tools menu.
	 */
	private JMenu toolsMenu;

	/**
	 *  The main menu bar.
	 */
	private JMenuBar systemMenu;

	/**
	 *  The exit menu item in the file menu.
	 */
	private JMenuItem exitFileItem;

	/**
	 *  The version menu item in the tools menu.
	 */
	private JMenuItem versionToolsItem;

	/**
	 *  The file menu separator.
	 */
	private JSeparator editSeparator1;

	/**
	 *  The file menu separator.
	 */
	private JSeparator fileSeparator1;

	/**
	 *  The file menu separator.
	 */
	private JSeparator fileSeparator2;

	/**
	 *  The tools menu separator.
	 */
	private JSeparator toolsSeparator1;

	/**
	 *  Tree for the prefereneces dialog
	 */
	private PreferencesRootTreeNode rootNode = new PreferencesRootTreeNode();

	/**
	 *  Creates an instance of the main application. Does all the core
	 *  initialization
	 *
	 *@since    GMGen 3.3
	 */
	public GMGenSystem()
	{
		super("GMGen System");

		new Renderer().start();
	}

	private void initialize()
	{
		// Fixes for Mac OS X look-and-feel menu problems.
		// sk4p 12 Dec 2002
		if (MAC_OS_X)
		{
			System.setProperty("com.apple.mrj.application.growbox.intrudes",
				"false");
			System
				.setProperty("com.apple.mrj.application.live-resize", "false");
			System.setProperty("com.apple.macos.smallTabs", "true");
			System.setProperty("apple.laf.useScreenMenuBar", "true");
			System.setProperty(
				"com.apple.mrj.application.apple.menu.about.name", "GMGen");
			macOSXRegistration();

			// Set up our application to respond to the Mac OS X application menu
		}

		inst = this;
		initLogger();
		createMenuBar();
		theView = new GMGenSystemView();
		GMBus.addToBus(this);
		PluginLoader ploader = PluginLoader.inst();
		ploader.startSystemPlugins(Constants.SYSTEM_GMGEN);
		initComponents();
		initSettings();
		GMBus.send(new FetchOpenPCGRequestMessage(this));
		GMBus.send(new StateChangedMessage(this, editMenu));
		inst.setVisible(true);
	}

	/**
	 *  Gets the build number of GMGen.
	 *
	 *@return    The build number
	 *@since     GMGen 3.3
	 */
	public static String getBuild()
	{
		return "03.03.99.01.00";
	}

	/**
	 *  Returns the GMGen version as a human-readable string.
	 *
	 *@return    The version
	 *@since     GMGen 3.3
	 */
	public static String getVersion()
	{
		return MiscUtilities.buildToVersion(getBuild());
	}

	/**
	 *  Calls the appropriate methods depending on the actions that happened on the
	 *  GUI.
	 *
	 *@param  e  event that took place
	 *@since     GMGen 3.3
	 */
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == openFileItem)
		{
			GMBus.send(new FileOpenMessage(this));
		}
		else if (e.getSource() == exitFileItem)
		{
			GMBus.send(new WindowClosedMessage(this));
			//System.exit(0);
		}
		else if (e.getSource() == newFileItem)
		{
			GMBus.send(new LoadMessage(this));
		}
		else if (e.getSource() == saveFileItem)
		{
			GMBus.send(new SaveMessage(this));
		}
		else if (e.getSource() == cutEditItem)
		{
			GMBus.send(new ClipboardMessage(this, ClipboardMessage.CUT));
		}
		else if (e.getSource() == copyEditItem)
		{
			GMBus.send(new ClipboardMessage(this, ClipboardMessage.COPY));
		}
		else if (e.getSource() == pasteEditItem)
		{
			GMBus.send(new ClipboardMessage(this, ClipboardMessage.PASTE));

			/*} else if (e.getSource() == sourceView.getLoadButton()) {
			   setTabsEnabled();
			   } else if ((e.getSource() == sourceView.getUnloadAllButton()) || (e.getSource() == sourceView.getRemoveAllButton())) {
			       setTabsDisabled();*/
		}
	}

	/**
	 *  Clears the edit menu to allow a plugin to populate it.
	 */
	public void clearEditMenu()
	{
		editMenu.removeAll();

		/**
		 *  Preferences on the Macintosh is in the application menu. See
		 *  macOSXRegistration()
		 */
		if (!MAC_OS_X)
		{
			editMenu.add(editSeparator1);

			preferencesEditItem.setText("Preferences");
			editMenu.add(preferencesEditItem);
			preferencesEditItem.setEnabled(true);

			ActionListener[] listenerArray =
					preferencesEditItem.getActionListeners();
			for (int i = 0; i < listenerArray.length; i++)
			{
				preferencesEditItem.removeActionListener(listenerArray[i]);
			}
			preferencesEditItem
				.addActionListener(new java.awt.event.ActionListener()
				{
					public void actionPerformed(java.awt.event.ActionEvent evt)
					{
						mPreferencesActionPerformed(evt);
					}
				});
		}
	}

	/**
	 *  Exits GMGen, the mac way.
	 */
	public void exitFormMac()
	{
		this.setVisible(false);

		//exit();
	}

	/**
	 *  Handles the clicking on the file menu.
	 *
	 *@since    GMGen 3.3
	 */
	public void handleFileMenu()
	{
		// TODO
	}

	/**
	 *  Message handler for the GMBus.
	 *
	 *@param  message  The messge passed in from the bus
	 *@since           GMGen 3.3
	 */
	public void handleMessage(GMBMessage message)
	{
		//A plugin is asking for the creation of a new tab
		if (message instanceof TabAddMessage)
		{
			TabAddMessage tmessage = (TabAddMessage) message;
			if (tmessage.getSystem().equals(Constants.SYSTEM_GMGEN))
			{
				Logging.debugPrint("Creating Tab "
					+ GMGenSystemView.getTabPane().getTabCount());
				theView.insertPane(tmessage.getName(), tmessage.getPane(),
					GMGenSystemView.getTabPane().getTabCount());
			}
		}
		else if (message instanceof PreferencesPanelAddMessage)
		{
			PreferencesPanelAddMessage pmessage =
					(PreferencesPanelAddMessage) message;
			Logging.debugPrint("Creating Preferences Panel");
			rootNode.addPanel(pmessage.getName(), pmessage.getPane());
		}
		// A plugin is asking for the creation of a new option in the tool menu
		else if (message instanceof ToolMenuItemAddMessage)
		{
			ToolMenuItemAddMessage mmessage = (ToolMenuItemAddMessage) message;
			toolsMenu.add(mmessage.getMenuItem());
		}
		else if (message instanceof WindowClosedMessage)
		{
			setCloseSettings();
			// Karianna 07/03/2008 - Added a call to exitForm passing in no window event
			// TODO This sequence of calls simply hides GMGen as opposed to unloading it
			exitForm(null);
		}
		else if (message instanceof PCLoadedMessage)
		{
			// tell source view to refresh
			//sourceView.updateLoadedCampaignsUI();
		}
	}

	/**
	 *  Handles the clicking on the tool menu.
	 *
	 *@since    GMGen 3.3
	 */
	public void handleToolsMenu()
	{
		// TODO
	}

	/**
	 * launches the preferences dialog on a mac.
	 */
	public void mPreferencesActionPerformedMac()
	{
		PreferencesDialog dialog = new PreferencesDialog(this, true, rootNode);
		dialog.setVisible(true);
	}

	/**
	 * generic registration with the Mac OS X application menu.  Checks the platform, then attempts
	 * to register with the Apple EAWT.
	 * This method calls OSXAdapter.registerMacOSXApplication() and OSXAdapter.enablePrefs().
	 * See OSXAdapter.java for the signatures of these methods.
	 */
	public void macOSXRegistration()
	{
		if (MAC_OS_X)
		{
			try
			{
				Class<?> osxAdapter = Class.forName("gmgen.util.OSXAdapter");
				Class[] defArgs = {GMGenSystem.class};
				Method registerMethod =
						osxAdapter.getDeclaredMethod(
							"registerMacOSXApplication", defArgs);

				if (registerMethod != null)
				{
					Object[] args = {this};
					registerMethod.invoke(osxAdapter, args);
				}

				// This is slightly gross.  to reflectively access methods with boolean args,
				// use "boolean.class", then pass a Boolean object in as the arg, which apparently
				// gets converted for you by the reflection system.
				defArgs[0] = boolean.class;

				Method prefsEnableMethod =
						osxAdapter.getDeclaredMethod("enablePrefs", defArgs);

				if (prefsEnableMethod != null)
				{
					Object[] args = {Boolean.TRUE};
					prefsEnableMethod.invoke(osxAdapter, args);
				}
			}
			catch (NoClassDefFoundError e)
			{
				// This will be thrown first if the OSXAdapter is loaded on a system without the EAWT
				// because OSXAdapter extends ApplicationAdapter in its def
				System.err
					.println("This version of Mac OS X does not support the Apple EAWT.  Application Menu handling has been disabled ("
						+ e + ")");
			}
			catch (ClassNotFoundException e)
			{
				// This shouldn't be reached; if there's a problem with the OSXAdapter we should get the
				// above NoClassDefFoundError first.
				System.err
					.println("This version of Mac OS X does not support the Apple EAWT.  Application Menu handling has been disabled ("
						+ e + ")");
			}
			catch (Exception e)
			{
				System.err.println("Exception while loading the OSXAdapter:");
				e.printStackTrace();
			}
		}
	}

	/**
	 *  Handles a menu canceled event.
	 *
	 *@param  e  menu canceled event
	 *@since     GMGen 3.3
	 */
	public void menuCanceled(MenuEvent e)
	{
		// TODO
	}

	/**
	 *  Handles a menu de-selected event.
	 *
	 *@param  e  Menu Deselected event
	 *@since     GMGen 3.3
	 */
	public void menuDeselected(MenuEvent e)
	{
		// TODO
	}

	/**
	 *  Listens for menus to be clicked and calls the appropriate handlers.
	 *
	 *@param  e  the menu event that happened.
	 *@since     GMGen 3.3
	 */
	public void menuSelected(MenuEvent e)
	{
		if (e.getSource() == fileMenu)
		{
			handleFileMenu();
		}
		else if (e.getSource() == toolsMenu)
		{
			handleToolsMenu();
		}
	}

	/**
	 *  Calls the necessary methods if an item on the GUI or model has changed.
	 *
	 *@param  e  the event that has happened.
	 *@since     GMGen 3.3
	 */
	public void stateChanged(ChangeEvent e)
	{
		stateUpdate(e);
	}

	/**
	 *  Calls the necessary methods if an item on the GUI or model has changed.
	 *
	 *@param  e  the event that has happened.
	 *@since     GMGen 3.3
	 */
	public void stateUpdate(EventObject e)
	{
		newFileItem.setEnabled(false);
		openFileItem.setEnabled(false);
		saveFileItem.setEnabled(false);
		clearEditMenu();
		GMBus.send(new StateChangedMessage(this, editMenu));
	}

	/**
	 *  Sets a bunch of properties based on the status of GMGen at close.
	 *
	 *@since    GMGen 3.3
	 */
	private void setCloseSettings()
	{
		SettingsHandler.setGMGenOption("WindowX", this.getX());
		SettingsHandler.setGMGenOption("WindowY", this.getY());
		SettingsHandler.setGMGenOption("WindowWidth", this.getSize().width);
		SettingsHandler.setGMGenOption("WindowHeight", this.getSize().height);

		//Maximized state of the window
		if ((getExtendedState() & Frame.MAXIMIZED_BOTH) != 0)
		{
			SettingsHandler.setGMGenOption("WindowState", Frame.MAXIMIZED_BOTH);
		}
		else if ((getExtendedState() & Frame.MAXIMIZED_HORIZ) != 0)
		{
			SettingsHandler
				.setGMGenOption("WindowState", Frame.MAXIMIZED_HORIZ);
		}
		else if ((getExtendedState() & Frame.MAXIMIZED_VERT) != 0)
		{
			SettingsHandler.setGMGenOption("WindowState", Frame.MAXIMIZED_VERT);
		}
		else
		{
			SettingsHandler.setGMGenOption("WindowState", Frame.NORMAL);
		}
	}

	/* *
	 *  Sets certain needed settings to PCGen.
	 *
	 *@since    GMGen 3.3
	 */

	/*public static void setPCGenSettings() {
	   String pcgenLocation = SettingsHandler.expandRelativePath(MiscUtilities.getGMGenOption("pcgenDir", System.getProperty("user.dir")));
	   System.setProperty("pcgen.options", pcgenLocation);
	   System.setProperty("user.dir", pcgenLocation);
	   //SettingsHandler.setIsGMGen(true);
	   SettingsHandler.readOptionsProperties();
	   SettingsHandler.setOptionsProperties();
	   SettingsHandler.setRollMethod(pcgen.core.Constants.ROLLINGMETHOD_ALLSAME);
	   SettingsHandler.setHPRollMethod(2);
	   SettingsHandler.setPcgenFilesDir(new File(pcgenLocation));
	   SettingsHandler.setPcgenSystemDir(new File(pcgenLocation + "/system"));
	   SettingsHandler.getOptions().setProperty("pcgen.files.pcgenSystemDir", SettingsHandler.getPcgenSystemDir().getAbsolutePath());
	   //SettingsHandler.setMonsterDefault(true);
	   //setPCGenOption("unlimitedStatPool", isUnlimitedStatPool());
	   //SettingsHandler.setUseExperimentalCursor(false);
	   SettingsHandler.setLoadCampaignsAtStart(true);
	   SettingsHandler.setLoadCampaignsWithPC(true);
	   SettingsHandler.setOptionAllowedInSources(true);
	   SettingsHandler.setPCGenOption("userdir", pcgenLocation);
	   SettingsHandler.setPccFilesLocation(new File(pcgenLocation + File.separator + "data"));
	   // initialize selected campaign sources
	   String sourceFiles = SettingsHandler.getGMGenOption("chosenCampaignSourcefiles", "");
	   PersistenceManager.setChosenCampaignSourcefiles(CoreUtility.split(sourceFiles, ','));
	   }*/

	/**
	 *  Sets all the panes on the GUI in the correct order.
	 *
	 *@since    GMGen 3.3
	 */
	private void setTabbedPanes()
	{
		//setTabsDisabled();
		try
		{
			GMGenSystemView.getTabPane().setSelectedIndex(0);
			theView.showPane();
		}
		catch (Exception e)
		{
			// TODO
		}
	}

	/**
	 *  Creates the MenuBar for the application.
	 *
	 *@since    GMGen 3.3
	 */
	private void createMenuBar()
	{
		systemMenu = new JMenuBar();

		fileMenu = new JMenu();
		newFileItem = new JMenuItem();
		openFileItem = new JMenuItem();
		fileSeparator1 = new JSeparator();
		saveFileItem = new JMenuItem();
		fileSeparator2 = new JSeparator();
		exitFileItem = new JMenuItem();
		editMenu = new JMenu();
		cutEditItem = new JMenuItem();
		copyEditItem = new JMenuItem();
		pasteEditItem = new JMenuItem();
		editSeparator1 = new JSeparator();
		preferencesEditItem = new JMenuItem();

		toolsMenu = new JMenu();
		toolsSeparator1 = new JSeparator();
		versionToolsItem = new JMenuItem();

		// FILE MENU
		fileMenu.setText("File");
		fileMenu.setMnemonic('F');
		fileMenu.addMenuListener(this);

		newFileItem.setMnemonic('N');
		newFileItem.setText("New");
		newFileItem.addActionListener(this);
		fileMenu.add(newFileItem);

		openFileItem.setMnemonic('O');
		openFileItem.setText("Open");
		fileMenu.add(openFileItem);
		openFileItem.addActionListener(this);

		fileMenu.add(fileSeparator1);

		saveFileItem.setMnemonic('S');
		saveFileItem.setText("Save");
		fileMenu.add(saveFileItem);
		saveFileItem.addActionListener(this);

		/**
		 *  Exit is quit on the Macintosh is in the application menu. See
		 *  macOSXRegistration()
		 */
		if (!MAC_OS_X)
		{
			fileMenu.add(fileSeparator2);

			exitFileItem.setMnemonic('x');
			exitFileItem.setText("Exit");
			fileMenu.add(exitFileItem);
			exitFileItem.addActionListener(this);
		}

		systemMenu.add(fileMenu);

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

		/**
		 *  Preferences... on the Macintosh is in the application menu. See
		 *  macOSXRegistration()
		 */
		if (!MAC_OS_X)
		{
			editMenu.add(editSeparator1);

			preferencesEditItem.setText("Preferences");
			editMenu.add(preferencesEditItem);
			preferencesEditItem.setEnabled(true);

			ActionListener[] listenerArray =
					preferencesEditItem.getActionListeners();
			for (int i = 0; i < listenerArray.length; i++)
			{
				preferencesEditItem.removeActionListener(listenerArray[i]);
			}

			preferencesEditItem.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					mPreferencesActionPerformed(evt);
				}
			});
		}

		systemMenu.add(editMenu);

		// TOOLS MENU
		toolsMenu.setText("Tools");
		toolsMenu.setMnemonic('T');
		toolsMenu.addMenuListener(this);

		versionToolsItem.setMnemonic('G');
		versionToolsItem.setText("Get Newest Version");
		toolsMenu.add(versionToolsItem);

		toolsMenu.add(toolsSeparator1);

		systemMenu.add(toolsMenu);

		setJMenuBar(systemMenu);
		openFileItem.setEnabled(true);
		saveFileItem.setEnabled(false);
		newFileItem.setEnabled(false);
		cutEditItem.setEnabled(false);
		copyEditItem.setEnabled(false);
		pasteEditItem.setEnabled(false);
		preferencesEditItem.setEnabled(true);
		versionToolsItem.setEnabled(false);

		pack();
	}

	/**
	 *  Closes and exits the application cleanly.
	 *
	 *@param  evt  a window close event
	 *@since       GMGen 3.3
	 */
	private void exitForm(WindowEvent evt)
	{
		this.setVisible(false);

		//exit();
	}

	/**
	 *  Initializes all the GUI components and places them in the correct place on
	 *  the GUI.
	 *
	 *@since    GMGen 3.3
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
			public void windowGainedFocus(java.awt.event.WindowEvent e)
			{
				stateUpdate(e);
			}

			public void windowLostFocus(java.awt.event.WindowEvent e)
			{
				//Intentionally left blank because WindowFocusListener requires
				//the method to be implemented.
			}
		});

		//sourceView.getLoadButton().addActionListener(this);
		//sourceView.getUnloadAllButton().addActionListener(this);
		//sourceView.getRemoveAllButton().addActionListener(this);
		GMGenSystemView.getTabPane().addChangeListener(this);
		getContentPane().add(theView, BorderLayout.CENTER);

		Toolkit kit = Toolkit.getDefaultToolkit();
		Image img =
				kit.getImage(getClass().getResource(
					"/pcgen/gui/resource/gmgen_icon.png"));
		setIconImage(img);
	}

	// end initPcgenLocation

	/**
	 *  Initializes the Logger component.
	 *
	 *@since    GMGen 3.3
	 */
	private void initLogger()
	{
		LogUtilities.inst().setLoggingOn(
			SettingsHandler.getGMGenOption("Logging.On", false));
	}

	/**
	 *  Initializes the settings, and implements their commands.
	 *
	 *@since    GMGen 3.3
	 */
	private void initSettings()
	{
		int iWinX = SettingsHandler.getGMGenOption("WindowX", 0);
		int iWinY = SettingsHandler.getGMGenOption("WindowY", 0);
		setLocation(iWinX, iWinY);

		int iWinWidth = SettingsHandler.getGMGenOption("WindowWidth", 750);
		int iWinHeight = SettingsHandler.getGMGenOption("WindowHeight", 580);
		setSize(iWinWidth, iWinHeight);

		int windowState =
				SettingsHandler.getGMGenOption("WindowState", Frame.NORMAL);

		if (windowState != Frame.NORMAL)
		{
			setExtendedState(windowState);
		}

	}

	// end handleOpenFile

	private void mPreferencesActionPerformed(ActionEvent evt)
	{
		PreferencesDialog dialog = new PreferencesDialog(this, true, rootNode);
		dialog.setVisible(true);
	}

	private class Renderer extends SwingWorker
	{

		@Override
		public Object construct()
		{
			return "";
		}

		@Override
		public void finished()
		{
			GMGenSystem.this.initialize();
		}
	}

}
