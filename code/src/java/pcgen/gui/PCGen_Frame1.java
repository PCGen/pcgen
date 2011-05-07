/*
 * PCGen_Frame1.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 *
 * Created on April 21, 2001, 2:15 PM
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package pcgen.gui;

import gmgen.GMGenSystem;
import gmgen.plugin.InitHolder;
import gmgen.plugin.InitHolderList;
import gmgen.plugin.PcgCombatant;
import gmgen.pluginmgr.GMBComponent;
import gmgen.pluginmgr.GMBMessage;
import gmgen.pluginmgr.GMBus;
import gmgen.pluginmgr.PluginLoader;
import gmgen.pluginmgr.messages.FetchOpenPCGRequestMessage;
import gmgen.pluginmgr.messages.FileTypeMessage;
import gmgen.pluginmgr.messages.InitHolderListSendMessage;
import gmgen.pluginmgr.messages.NewMessage;
import gmgen.pluginmgr.messages.OpenMessage;
import gmgen.pluginmgr.messages.OpenPCGRequestMessage;
import gmgen.pluginmgr.messages.PCClosedMessage;
import gmgen.pluginmgr.messages.PCLoadedMessage;
import gmgen.pluginmgr.messages.PauseRefreshMessage;
import gmgen.pluginmgr.messages.ResumeRefreshMessage;
import gmgen.pluginmgr.messages.SaveMessage;
import gmgen.pluginmgr.messages.SavePCGNotificationMessage;
import gmgen.pluginmgr.messages.SavePCGRequestMessage;
import gmgen.pluginmgr.messages.StateChangedMessage;
import gmgen.pluginmgr.messages.TabAddMessage;
import gmgen.pluginmgr.messages.WindowClosedMessage;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.MenuComponent;
import java.awt.MenuContainer;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.EventObject;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.TabbedPaneUI;

import pcgen.cdom.base.Constants;
import pcgen.core.CustomData;
import pcgen.core.Equipment;
import pcgen.core.GameMode;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.core.character.Follower;
import pcgen.core.npcgen.NPCGenerator;
import pcgen.core.party.PCLoader;
import pcgen.core.party.Party;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.gui.filter.FilterFactory;
import pcgen.gui.filter.Filterable;
import pcgen.gui.sources.InfoPanel;
import pcgen.gui.sources.MainSource;
import pcgen.gui.sources.SourceSelectionDialog;
import pcgen.gui.utils.IconUtilitities;
import pcgen.gui.utils.LinkableHtmlMessage;
import pcgen.gui.utils.Utility;
import pcgen.io.PCGFile;
import pcgen.io.PCGIOHandler;
import pcgen.util.FOPResourceChecker;
import pcgen.util.JEPResourceChecker;
import pcgen.util.Logging;
import pcgen.util.PropertyFactory;
import pcgen.util.enumeration.Tab;

/**
 * Main screen of the application. Some of the custom JPanels created
 * here also help intialise, for example {@link pcgen.gui.sources.MainSource} 
 * also loads any default campaigns.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision$
 */
public class PCGen_Frame1 extends JFrame implements GMBComponent, Observer,
		PCLoader
{
	static final long serialVersionUID = 1042236188732008819L;

	/** The number of the first character tab */
	public static int FIRST_CHAR_TAB = 1;
	private static PCGen_Frame1 inst;

	// Our automagic mouse wait cursor when waiting for > 3/4 second
	private static WaitCursorEventQueue waitQueue =
			new WaitCursorEventQueue(750);
	// Use the automagic wait cursor flag, defauls to false
	private static boolean usingWaitCursor = false;

	private static CharacterInfo characterPane = null;

	private static StatusBar statusBar = new StatusBar();

	/**
	 * Main tabbed panel of the application.
	 * The first tab contains the {@link #mainSource}
	 * ("Campaign") panel.
	 * Additional {@link CharacterInfo} panel tabs are added
	 * for each created character.
	 */
	static JTabbedPane baseTabbedPane = new JTabbedPane();

	/** Menubar and toolbar actions. */
	FrameActionListener frameActionListener;
	private final PcgFilter filter = new PcgFilter();
	private final PcpFilter partyFilter = new PcpFilter();
	private BorderLayout borderLayout1 = new BorderLayout();
	private BorderLayout borderLayout2 = new BorderLayout();
	private FlowLayout flowLayout1 = new FlowLayout();
	private FlowLayout flowLayout2 = new FlowLayout();

	// the panel that contains all but the status bar,
	// this allows the tool bar to be floated and not
	// overlap with the status bar
	private JPanel panelMain = new JPanel();

	// GUI stuff
	private JPanel panelSouth = new JPanel();
	private JPanel panelSouthCenter = new JPanel();
	private JPanel panelSouthEast = new JPanel();
	private KitSelector kitSelector = null;
	private MainPopupMenu mainPopupMenu;
	private List<Component> tempTabList = new ArrayList<Component>(12);

	/**
	 * Contains the source screen.
	 *
	 * @see MainSource
	 */
	private MainSource mainSource;
	private InfoPanel infoPanel;

	/** Menubar for the main application. */
	private PCGenMenuBar pcgenMenuBar;
	private PCPopupMenu pcPopupMenu;

	/** ToolBar for the main application. */
	private PToolBar toolBar;
	private PopupListener popupListener;
	private String partyFileName = ""; //used to keep track of last .pcp file used

	// Characters / core stuff
	private int newPCNameCount = 0;
	private pcGenGUI mainClass;

	/**
	 * Screen initialization. Override close.
	 * 
	 * Calls private <code>jbInit()</code> which does real screen
	 * initialization: Sets up all the window properties (icon,
	 * title, size);
	 * and creates the campaign and DM tools tabs, along with all
	 * the sub-panes of DM tools.
	 */
	public PCGen_Frame1()
	{
		inst = this;

		enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		frameActionListener = new FrameActionListener(this);
		mainPopupMenu = new MainPopupMenu(frameActionListener);
		pcPopupMenu = new PCPopupMenu(frameActionListener);
		popupListener =
				new PopupListener(baseTabbedPane, mainPopupMenu, pcPopupMenu);
		toolBar = PToolBar.createToolBar(this);
		pcgenMenuBar = new PCGenMenuBar(this, frameActionListener);

		try
		{
			jbInit();
		}
		catch (Exception e) //This is what jbInit throws...
		{
			Logging.errorPrint("jbInit", e);
		}

		Globals.setRootFrame(this);
		GMBus.addToBus(this);
		GMBus.addToBus(PreferencesDialog.getPreferencesComponent());
		PluginLoader ploader = PluginLoader.inst();
		ploader.startSystemPlugins(Constants.s_SYSTEM_PCGEN);
	}

	/**
	 * Get the baseTabbedPane (the base pane for PCGen)
	 * @return baseTabbedPane
	 */
	public static JTabbedPane getBaseTabbedPane()
	{
		return baseTabbedPane;
	}

	/**
	 * Get the characterPane (the Pane that holds the Character Tabs)
	 * @return characterPane
	 */
	public static CharacterInfo getCharacterPane()
	{
		return characterPane;
	}

	/**
	 * Accessor to get CharacterInfo component of active tab.
	 * This is especially needed to change/update a character's name.
	 * @return CharacterInfo
	 */
	public static CharacterInfo getCurrentCharacterInfo()
	{
		int index = getBaseTabbedPane().getSelectedIndex();

		if (index >= FIRST_CHAR_TAB)
		{
			/**
			 * I would have preferred code like this
			 *    return (CharacterInfo) characterList.elementAt(index - FIRST_CHAR_TAB);
			 * but characterList is no class field (i.e. not static),
			 * so I use the static JTabbedPane.
			 * Maybe this whole method should not be static,
			 * but then we will be in need of a method to access the actual
			 * instance of PCGen_Frame1, so that NameGUI can access the
			 * current CharacterInfo for setting a new (random) name.
			 *
			 * author: Thomas Behr 20-12-01
			 */
			return characterPane;
		}

		/**
		 * hope this will not cause any NullPointerExceptions!
		 *
		 * author: Thomas Behr 20-12-01
		 */
		return null;
	}

	/**
	 * Accessor to get selected Filterable component of active tab
	 * <p/>
	 * <br>author: Thomas Behr
	 * @return Filterable
	 */
	public static Filterable getCurrentFilterable()
	{
		int index = getBaseTabbedPane().getSelectedIndex();

		if (index == 0)
		{
			Object tab = getBaseTabbedPane().getComponentAt(index);
			if (tab instanceof Filterable)
			{
				return (Filterable) tab;
			}
		}
		else if (index >= FIRST_CHAR_TAB)
		{
			return characterPane.getSelectedFilterable();
		}

		return null;
	}

	/**
	 * @return the infoPanel
	 */
	public InfoPanel getInfoPanel()
	{
		return infoPanel;
	}

	/**
	 * Get an instance of this class
	 * @return instance of PCGen_Frame1 
	 */
	public static PCGen_Frame1 getInst()
	{
		return inst;
	}

	/**
	 * Set the main class for the application
	 * @param owner
	 */
	public void setMainClass(pcGenGUI owner)
	{
		mainClass = owner;
	}

	/** 
	 * Get the source screen
	 * @return mainSource
	 */
	public MainSource getMainSource()
	{
		return mainSource;
	}

	/**
	 * Gets the status bar message area text.
	 *
	 * @return the status bar message area text
	 *
	 * @see StatusBar#getMessageAreaText()
	 */
	public static String getMessageAreaText()
	{
		return statusBar.getMessageAreaText();
	}

	/**
	 * Sets the status bar message area text and saves the previous message.
	 * Repeated calls stack up the previous messages.  Nest calls to
	 * <code>setMessageAreaText</code> and <code>restoreMessageAreaText</code> in
	 * pairs.
	 *
	 * @param message the new status bar message area text
	 *
	 * @see #restoreMessageAreaText()
	 */
	public static void setMessageAreaText(final String message)
	{
		statusBar.setMessageAreaText(message);
	}

	/**
	 * Sets the status bar message area text discarding the previous message.
	 *
	 * @param message
	 */
	public static void setMessageAreaTextWithoutSaving(final String message)
	{
		statusBar.setMessageAreaTextWithoutSaving(message);
	}

	/**
	 * Restores the previous status bar message area text.  Repeated calls restore
	 * older messages.  Nest calls to <code>setMessageAreaText</code> and
	 * <code>restoreMessageAreaText</code> in pairs.
	 *
	 * @see #setMessageAreaText(String)
	 */
	public static void restoreMessageAreaText()
	{
		statusBar.restoreMessageAreaText();
	}

	/**
	 * Adds or removes the memory area from the status bar.
	 *
	 * @param showMemoryArea <code>true</code> to add the memory area
	 */
	public static void showMemoryArea(final boolean showMemoryArea)
	{
		statusBar.setShowMemoryArea(showMemoryArea);
	}

	/**
	 * Set the title of the frame, sets the game mode name and 
	 * the version number 
	 */
	public void setGameModeTitle()
	{
		String modeName;
		GameMode gameMode = SettingsHandler.getGame();

		if (gameMode == null)
		{
			modeName = "???";
		}
		else
		{
			modeName = gameMode.getDisplayName();
		}

		setTitle("PCGen v. " + PCGenProp.getVersionNumber() + " - " + modeName
			+ " Campaign");
	}

	/**
	 * Enable/disable all items intelligently.
	 * This method probably does too much.
	 */
	public static void enableDisableMenuItems()
	{
		if (!Globals.getUseGUI())
		{
			return;
		}

		PCGen_Frame1 frame = getInst();

		frame.enableNew(true);
		frame.enableOpen(false);
		frame.enableClose(false);
		frame.enableCloseAll(false);
		frame.enableSave(false);
		frame.enableSaveAs(false);
		frame.enableSaveAll(false);
		frame.enableRevertToSaved(false);
		frame.enableSave(false);
		frame.enablePrintPreview(false);
		frame.enablePrint(false);
		frame.enablePartySave(false);
		frame.enablePartySaveAs(false);
		frame.enableExport(false);
		frame.enableKit(false);
		frame.enableLstEditors(false);

		/* No campaigns open */
		if (!Globals.checkListsHappy())
		{
			// If you can autoload a campaign, you can open a PC
			if (SettingsHandler.isExpertGUI())
			{
				frame.enableNew(false);
			}

			// If you can autoload a campaign, you can open a PC
			if (SettingsHandler.isLoadCampaignsWithPC())
			{
				frame.enableOpen(true);
			}

			frame.pcgenMenuBar.treasureItem.setEnabled(false);
			return;
		}

		frame.enableOpen(true);
		frame.enableLstEditors(true);
		frame.pcgenMenuBar.treasureItem.setEnabled(true);

		final int currTab = baseTabbedPane.getSelectedIndex();
		if (currTab < FIRST_CHAR_TAB && currTab > 0)
		{
			//In a plugin's view
			frame.enableSave(true);
			return;
		}

		PlayerCharacter aPC = frame.getCurrentPC();
		/* No PCs open */
		if (aPC == null)
		{
			return;
		}

		frame.enableClose(true);
		frame.enableSaveAs(true);
		frame.enablePrintPreview(true);
		frame.enablePrint(true);

		// How can you tell if a party file is clean?  XXX
		//frame.enablePartyClose(true);
		frame.enablePartySave(true);
		frame.enablePartySaveAs(true);
		frame.enableExport(true);
		frame.enableKit(true);

		List<PlayerCharacter> allPCs = Globals.getPCList();
		int pcCount = allPCs.size();

		if (pcCount > 1)
		{
			frame.enableCloseAll(true);
		}

		/* Changes to any PC? */
		for (int i = 0; i < pcCount; ++i)
		{
			if ((allPCs.get(i)).isDirty())
			{
				frame.enableSaveAll(true);

				break;
			}
		}

		/* No changes to current PC */
		if (!aPC.isDirty())
		{
			return;
		}

		frame.enableSave(true);

		/* No saved file yet */
		if (!aPC.wasEverSaved())
		{
			return;
		}

		frame.enableRevertToSaved(true);
	}

	/**
	 * Set the recently opened PCs list
	 * @param entries
	 */
	public void setOpenRecentPCs(String[] entries)
	{
		pcgenMenuBar.openRecentPCMenu.setEntriesAsStrings(entries);
	}

	/**
	 * Get the list of recently opened PCs
	 * @return recently opened PCs
	 */
	public String[] getOpenRecentPCs()
	{
		return pcgenMenuBar.openRecentPCMenu.getEntriesAsStrings();
	}

	/**
	 * Set recently opened Parties
	 * @param strings
	 */
	public void setOpenRecentParties(String[] strings)
	{
		pcgenMenuBar.openRecentPartyMenu.setEntriesAsStrings(strings);
	}

	/**
	 * Get recently opened parties
	 * @return recently opened parties
	 */
	public String[] getOpenRecentParties()
	{
		return pcgenMenuBar.openRecentPartyMenu.getEntriesAsStrings();
	}

	/**
	 * get the root frame that the given panel resides within
	 * @param child the panel to find the root frame of
	 * @return the root frame, or <code>null</code> if the given
	 * panel is not within a frame
	 */
	public static PCGen_Frame1 getRealParentFrame(JPanel child)
	{
		return (PCGen_Frame1) child.getTopLevelAncestor();
	}

	/**
	 * Add monster HD to the summary tab, the direction indicates 
	 * whether we are actually adding or subtracting
	 * 
	 * @param direction
	 */
	public static void addMonsterHD(int direction)
	{
		if (characterPane != null)
		{
			characterPane.infoSummary().addMonsterHD(direction);
		}
	}

	/**
	 * Close all of the open PCs
	 */
	public void closeAllPCs()
	{
		int tabCount = baseTabbedPane.getTabCount();

		while (tabCount > FIRST_CHAR_TAB)
		{
			if (closePCTabAt(tabCount - 1, true))
			{
				tabCount = baseTabbedPane.getTabCount();
			}
			else
			{
				//Stop closing if user wants to save an unsaved tab.
				return;
			}
		}

		enableClose(false);
		enableCloseAll(false);
		enableSave(false);
		enableSaveAs(false);
		enableRevertToSaved(false);
		enablePartySave(false);
		enablePartySaveAs(false);
		enablePartyClose(false);
		enablePrintPreview(false);
		enablePrint(false);
		enableExport(false);
		enableKit(false);
		newPCNameCount = 0;
	}

	/**
	 * Sets the LST editors to be enabled/disabled
	 * @param itemState
	 */
	public void enableLstEditors(boolean itemState)
	{
		pcgenMenuBar.listEditor.setEnabled(itemState);
	}

	/**
	 * Enable/disable the new item
	 * @param itemState
	 */
	public void enableNew(boolean itemState)
	{
		pcgenMenuBar.newItem.setEnabled(itemState);
		toolBar.newItem.setEnabled(itemState);
		mainPopupMenu.newItem.setEnabled(itemState);
		pcPopupMenu.getNewItem().setEnabled(itemState);

		// New Random NPC
		pcgenMenuBar.newNPCItem.setEnabled(itemState);
		toolBar.newNPCItem.setEnabled(itemState);
		mainPopupMenu.newNPCItem.setEnabled(itemState);
		pcPopupMenu.getNewNPCItem().setEnabled(itemState);
	}

	/**
	 * Enable/disable the open item
	 * @param itemState
	 */
	public void enableOpen(boolean itemState)
	{
		pcgenMenuBar.openItem.setEnabled(itemState);
		toolBar.openItem.setEnabled(itemState);
	}

	/**
	 * Update the available equipment list for all of the currently loaded PCs, 
	 * but only purchase for the active PC.
	 * 
	 * @param newEq The new equipment
	 * @param purchase 
	 */
	public void eqList_Changed(Equipment newEq, boolean purchase)
	{
		if (characterPane != null)
		{
			characterPane.infoInventory().getInfoGear().refreshAvailableList(
				newEq, purchase, true);
		}
	}

	/**
	 * Export to PDF/XML/XSL output sheets
	 */
	public void exportToPDFItem_actionPerformed()
	{
		if (!pcgenMenuBar.enablePDF)
		{
			warnAboutMissingResource();
			return;
		}

		if (pcgenMenuBar.exportPDFPopup == null)
		{
			pcgenMenuBar.exportPDFPopup = new ExportPDFPopup(baseTabbedPane);
		}

		pcgenMenuBar.exportPDFPopup.setCurrentPCSelectionByTab();
	}

	/**
	 * Export to standard html/xhtml output sheets
	 */
	public void exportToStandardItem_actionPerformed()
	{
		if (pcgenMenuBar.exportPopup == null)
		{
			pcgenMenuBar.exportPopup = new ExportPopup(baseTabbedPane);
		}

		pcgenMenuBar.exportPopup.setCurrentPCSelectionByTab();
	}

	/**
	 * Export to text output sheets
	 */
	public void exportToTextItem_actionPerformed()
	{
		if (pcgenMenuBar.exportTextPopup == null)
		{
			pcgenMenuBar.exportTextPopup = new ExportTextPopup(baseTabbedPane);
		}

		pcgenMenuBar.exportTextPopup.setCurrentPCSelectionByTab();
	}

	public PlayerCharacter loadPCFromFile(File file)
	{
		return loadPCFromFile(file, false, false);
	}

	/**
	 * Set the current PC and load their companions (if that option is set)
	 * @param aPC The PC to set
	 */
	public void setPC(final PlayerCharacter aPC)
	{
		if (mainClass == null || aPC == null)
		{
			return;
		}

		addPCTab(aPC);

		if (aPC.getLoadCompanion() && aPC.hasFollowers())
		{
			for (Follower nPC : aPC.getFollowerList())
			{
				boolean aLoaded = false;

				// is this companion already loaded?
				for (PlayerCharacter testPC : Globals.getPCList())
				{
					if (nPC.getFileName().equals(testPC.getFileName()))
					{
						aLoaded = true;
					}
				}

				if (!aLoaded)
				{
					// not loaded, so load this file
					final File aFile = new File(nPC.getFileName());
					Party followerParty = Party.makeSingleCharacterParty(aFile);
					final PlayerCharacter follower = followerParty.load(null);
					if ((mainClass != null) && follower != null)
					{
						addPCTab(follower);
					}
					else
					{
						//todo: i18n these messages
						ShowMessageDelegate
							.showMessageDialog(
								"Unrecoverable problems occurred while loading a companion or follower.",
								"Error", MessageType.ERROR);
					}
				}
			}
		}
	}

	/**
	 * Load the PC from a PCG file
	 * 
	 * @param file The PCG file containing the PC
	 * @param blockLoadedMessage Whether we block the message that the PC is loaded 
	 * to the GMGen plugin system
	 * @param allowDuplicates Should we allow the user to load the same character multiple times?
	 * @return the PC
	 */
	public PlayerCharacter loadPCFromFile(File file, boolean blockLoadedMessage, boolean allowDuplicates)
	{
		PlayerCharacter aPC;

		// Fix for bug 1082786 - loading duplicate pcg files
		if (!allowDuplicates)
		{
			for (PlayerCharacter possibleDuplicate : Globals.getPCList())
			{
				if (file.getAbsolutePath().equals(possibleDuplicate.getFileName()))
				{
					// TODO Internationalise
					ShowMessageDelegate.showMessageDialog(
						"This character has already been loaded from: "
							+ file.getAbsolutePath(), "Error", MessageType.ERROR);
					Logging.errorPrint("The character was already loaded");
					return null;
				}
			}
		}

		Party party = Party.makeSingleCharacterParty(file);

		if (!file.exists())
		{
			JOptionPane.showMessageDialog(this, "File does not exist", "Error",
				JOptionPane.ERROR_MESSAGE);
			return null;
		}

		try
		{
			GMBus.send(new PauseRefreshMessage(this));
			aPC = party.load(null);

			if (mainClass == null || aPC == null)
			{
				//TODO: i18n these messages
				ShowMessageDelegate
					.showMessageDialog(
						"Unrecoverable problems occurred while loading the character.",
						"Error", MessageType.ERROR);
				Logging.errorPrint("Error in loadPCFromFile");

				return null;
			}
			pcgenMenuBar.openRecentPCMenu.add(aPC.getDisplayName(), file);
			setPC(aPC);

			if (!blockLoadedMessage)
			{
				GMBus.send(new PCLoadedMessage(this, aPC));
			}
		}
		finally
		{
			GMBus.send(new ResumeRefreshMessage(this));
		}

		return aPC;
	}

	/**
	 * update/restore filter settings from globally saved settings
	 * <p/>
	 * <br>author: Thomas Behr 24-02-02
	 *
	 * @param filterableName the name of the Filterable;<br>
	 *                       if <code>null</code> then filters for all
	 *                       Filterables will be updated
	 */
	public static void restoreFilterSettings(String filterableName)
	{
		if (characterPane != null)
		{
			characterPane.restoreFilterSettings(filterableName);
		}
		else
		{
			FilterFactory.clearFilterCache();
		}
	}

	/**
	 * Update the list of feats that have changed, e.g.  Update the abilities tab
	 */
	public void featList_Changed()
	{
		if (characterPane != null)
		{
			// TODO deprecated  - Previous code updated the old FEAT tab, remove commented 
			// out code?
			//characterPane.setPaneForUpdate(characterPane.infoFeats());
			characterPane.setPaneForUpdate(characterPane.infoAbilities());
			characterPane.refresh();
		}
	}

	/**
	 * Force an update to all of the currently loaded PCs
	 */
	public static void forceUpdate_PlayerTabs()
	{
		boolean tips = SettingsHandler.isToolTipTextShown();

		for (int i = FIRST_CHAR_TAB, x = baseTabbedPane.getTabCount(); i < x; ++i)
		{
			PlayerCharacter aPC = getPCForTabAt(i);
			setTabName(i, aPC.getDisplayName());
			baseTabbedPane.setToolTipTextAt(i, tips ? aPC.getFullDisplayName()
				: null);
		}
	}

	/**
	 * Update the HP for the character in the Classes and Summary 
	 * screens.
	 */
	public void hpTotal_Changed()
	{
		if (characterPane != null)
		{
			characterPane.setPaneForUpdate(characterPane.infoClasses());
			characterPane.setPaneForUpdate(characterPane.infoSummary());
			characterPane.refresh();
		}
	}

	/**
	 * Reverts to the previous version of this PC's saved file.
	 * @param e
	 */
	public void revertToSavedItem_actionPerformed(ActionEvent e)
	{
		PlayerCharacter aPC = getCurrentPC();

		if (aPC == null)
		{
			return;
		}

		if (!aPC.isDirty())
		{
			// do nothing if clean
			return;
		}

		int reallyClose =
				JOptionPane.showConfirmDialog(this, aPC.getDisplayName()
					+ " changed.	 Discard changes?", "Revert "
					+ aPC.getDisplayName() + "?", JOptionPane.YES_NO_OPTION);

		if (reallyClose != JOptionPane.YES_OPTION)
		{
			return;
		}

		// TODO: I18N
		setMessageAreaText("Reverting character to saved...");

		try
		{
			// seize the focus to cause focus listeners to fire
			pcgenMenuBar.revertToSavedItem.requestFocus();

			String fileName = aPC.getFileName(); // full path

			if (fileName.equals(""))
			{
				return;
			}

			closeItem_actionPerformed(e);

			File pcFile = new File(fileName);

			if (pcFile.exists())
			{
				loadPCFromFile(pcFile);
			}
		}
		finally
		{
			restoreMessageAreaText();
		}
	}

	/**
	 * Checks whether a character can be saved, and if so, calls
	 * it's <code>save</code> method.
	 *
	 * @param aPC    The PlayerCharacter to save
	 * @param saveas boolean if <code>true</code>, ask for file name
	 * @return <code>true</code> if saved; <code>false</code> if saveas cancelled
	 */
	public boolean savePC(PlayerCharacter aPC, boolean saveas)
	{
		boolean newPC = false;
		File prevFile;
		File file;
		String aPCFileName = aPC.getFileName();
		GMBus.send(new SavePCGNotificationMessage(this, aPC));

		if (aPCFileName.equals(""))
		{
			prevFile =
					new File(SettingsHandler.getPcgPath(), aPC.getDisplayName()
						+ Constants.s_PCGEN_CHARACTER_EXTENSION);
			aPCFileName = prevFile.getAbsolutePath();
			newPC = true;
		}
		else
		{
			prevFile = new File(aPCFileName);
		}

		if (saveas || newPC)
		{
			final JFileChooser fc =
					SettingsHandler.isShowImagePreview() ? ImagePreview
						.decorateWithImagePreview(new JFileChooser())
						: new JFileChooser();
			fc.setFileFilter(filter);
			fc.setSelectedFile(prevFile);

			FilenameChangeListener listener =
					new FilenameChangeListener(aPCFileName, fc);

			fc.addPropertyChangeListener(listener);

			int returnVal = fc.showSaveDialog(PCGen_Frame1.this);
			fc.removePropertyChangeListener(listener);

			if (returnVal == JFileChooser.APPROVE_OPTION)
			{
				file = fc.getSelectedFile();

				if (!PCGFile.isPCGenCharacterFile(file))
				{
					file =
							new File(file.getParent(), file.getName()
								+ Constants.s_PCGEN_CHARACTER_EXTENSION);
				}

				if (file.isDirectory())
				{
					ShowMessageDelegate.showMessageDialog(
						"You cannot overwrite a directory with a character.",
						Constants.APPLICATION_NAME, MessageType.ERROR);

					return false;
				}

				// Do't worry about checking for prevFile == null as we know it can't be from above
				if (file.exists()
					&& (newPC || !file.getName().equals(prevFile.getName())))
				{
					int reallyClose =
							JOptionPane
								.showConfirmDialog(
									this,
									"The file "
										+ file.getName()
										+ " already exists, are you sure you want to overwrite it?",
									"Confirm overwriting " + file.getName(),
									JOptionPane.YES_NO_OPTION);

					if (reallyClose != JOptionPane.YES_OPTION)
					{
						return false;
					}
				}

				aPC.setFileName(file.getAbsolutePath());
			}
			else
			// not saving
			{
				return false;
			}
		}

		else
		// simple save
		{
			file = prevFile;
		}

		try
		{
			(new PCGIOHandler()).write(aPC, file.getAbsolutePath());
			SettingsHandler.setLastUsedPcgPath(file.getParentFile());
		}
		catch (Exception ex)
		{
			Logging.errorPrint("Could not save " + aPC.getDisplayName(), ex);
			ShowMessageDelegate.showMessageDialog("Could not save "
				+ aPC.getDisplayName(), Constants.APPLICATION_NAME, MessageType.ERROR);

			return false;
		}

		pcgenMenuBar.openRecentPCMenu.add(aPC.getDisplayName(), file);

		return true;
	}

	/**
	 * Use the wait mouse cursor.  If b is true then set the usingWaitCursor boolean 
	 * to true (if not already set) else if b is false and the usingWaitCursoe is 
	 * true then switch it to off.
	 * 
	 * @param b 
	 */
	public static void useWaitCursor(boolean b)
	{
		if (b)
		{
			if (!usingWaitCursor)
			{
				usingWaitCursor = true;
				Toolkit.getDefaultToolkit().getSystemEventQueue().push(
					waitQueue);
			}
		}
		else
		{
			if (usingWaitCursor)
			{
				try
				{
					waitQueue.doPop();
				}
				catch (EmptyStackException e)
				{
					//TODO: Should we really ignore this?
				}

				usingWaitCursor = false;
			}
		}
	}

	/**
	 * Set the PCGen menu Bar
	 * @param thePcgenMenuBar
	 */
	public void setPcgenMenuBar(PCGenMenuBar thePcgenMenuBar)
	{
		this.pcgenMenuBar = thePcgenMenuBar;
	}

	/**
	 * Get the PCGen menu bar
	 * @return pcgenMenuBar
	 */
	public PCGenMenuBar getPcgenMenuBar()
	{
		return pcgenMenuBar;
	}

	public void handleMessage(GMBMessage message)
	{
		if (message instanceof OpenPCGRequestMessage)
		{
			handleOpenPCGRequestMessage((OpenPCGRequestMessage) message);
		}
		else if (message instanceof OpenMessage)
		{
			handleOpenMessage((OpenMessage) message);
		}
		else if (message instanceof SaveMessage)
		{
			handleSaveMessage((SaveMessage) message);
		}
		else if (message instanceof NewMessage)
		{
			handleNewMessage();
		}
		else if (message instanceof FetchOpenPCGRequestMessage)
		{
			handleFetchOpenPCGRequestMessage();
		}
		else if (message instanceof SavePCGRequestMessage)
		{
			SavePCGRequestMessage smessage = (SavePCGRequestMessage) message;
			savePC(smessage.getPC(), false);
		}

		// This should only be used until GMGen can use PCGen to generate it's
		// Random encounter beasties.
		else if (message instanceof InitHolderListSendMessage)
		{
			handleInitHolderListSendMessage((InitHolderListSendMessage) message);
		}
		else if (message instanceof StateChangedMessage)
		{
			handleStateChangedMessage();
		}
		else if (message instanceof TabAddMessage)
		{
			handleTabAddMessage((TabAddMessage) message);
		}
		else if (message instanceof PauseRefreshMessage)
		{
			handlePauseRefreshMessage();
		}
		else if (message instanceof ResumeRefreshMessage)
		{
			handleResumeRefreshMessage();
		}
	}

	/**
	 * @param message
	 */
	private void handleOpenMessage(OpenMessage message)
	{
		final File[] pcFiles = message.getFile();
		for (int i = 0; i < pcFiles.length; i++)
		{
			if (filter.accept(pcFiles[i]))
			{
				SettingsHandler.setLastUsedPcgPath(pcFiles[i].getParentFile());
				loadPCFromFile(pcFiles[i]);
			}
		}
		message.veto();
	}

	private void handleSaveMessage(SaveMessage message)
	{
		final int currTab = baseTabbedPane.getSelectedIndex();
		if (this.isFocused() && currTab >= FIRST_CHAR_TAB)
		{
			// seize the focus to cause focus listeners to fire
			pcgenMenuBar.saveItem.requestFocus();

			final PlayerCharacter aPC = getCurrentPC();

			if (aPC == null)
			{
				return;
			}

			savePC(aPC, false);
			message.veto();
		}
	}

	private void handleNewMessage()
	{
		final int currTab = baseTabbedPane.getSelectedIndex();
		if (currTab < 0 || currTab >= FIRST_CHAR_TAB
			|| baseTabbedPane.getSelectedComponent() == mainSource
			|| baseTabbedPane.getSelectedComponent() instanceof InfoPanel)
		{
			// seize the focus to cause focus listeners to fire
			// How does this work with the toolbar button?? --bko XXX
			toolBar.newItem.requestFocus();
			pcgenMenuBar.newItem.requestFocus();

			doNewItem(); // selects new tab for us
		}
	}

	/**
	 * Send a state changed message to the GMGen plugin system
	 * @param e The event that has changed
	 */
	public void stateUpdate(EventObject e)
	{
		GMBus.send(new StateChangedMessage(this, null));
	}

	/**
	 * Overridden so we can handle exit on System Close
	 * by calling <code>handleQuit</code>.
	 * @param e The window event
	 */
	@Override
	protected void processWindowEvent(WindowEvent e)
	{
		super.processWindowEvent(e);

		if (e.getID() == WindowEvent.WINDOW_CLOSING)
		{
			handleQuit();
		}
	}

	/**
	 * Add a Kit to the PC
	 */
	public void addKit_actionPerformed()
	{
		toolBar.addKit.requestFocus();
		pcgenMenuBar.addKit.requestFocus();

		PlayerCharacter aPC = getCurrentPC();

		if (aPC == null)
		{
			return;
		}

		final int currTab = baseTabbedPane.getSelectedIndex();

		if (currTab >= FIRST_CHAR_TAB)
		{
			//			final String kitFilter = getCharacterPane().getKitFilter();

			if (kitSelector == null)
			{
				kitSelector = new KitSelector(aPC);
			}

			kitSelector.setVisible(true);
			//			kitSelector.setFilter(kitFilter);
			kitSelector = null;
		}
	}

	/**
	 * Close all open character tabs
	 */
	void closeAllItem_actionPerformed()
	{
		// seize the focus to cause focus listeners to fire
		pcgenMenuBar.closeAllItem.requestFocus();
		closeAllPCs();
	}

	/**
	 * Closes the currently selected character tab.
	 * @param e
	 */
	void closeItem_actionPerformed(ActionEvent e)
	{
		// seize the focus to cause focus listeners to fire
		pcgenMenuBar.closeItem.requestFocus();

		final int currTab = baseTabbedPane.getSelectedIndex();

		if (currTab >= FIRST_CHAR_TAB)
		{
			// Check if reverting instead
			final String command = e.getActionCommand();

			if (!closePCTabAt(currTab, command.equals("file.close")))
			{
				return;
			}

			// Reset the "New1" counter if you close all tabs
			if (baseTabbedPane.getTabCount() <= FIRST_CHAR_TAB)
			{
				newPCNameCount = 0;
			}
		}
	}

	void closePopupItem_actionPerformed()
	{
		int index = popupListener.getTabIndex();
		closePCTabAt(index, true);

		// Try not to jump the tab focus around
		baseTabbedPane.setSelectedIndex((index < baseTabbedPane.getTabCount())
			? index : (index - 1));
	}

	boolean loadPartyFromFile(File file)
	{
		Party party = Party.makePartyFromFile(file);

		if (!file.exists())
		{
			JOptionPane.showMessageDialog(this, "File does not exist", "Error",
				JOptionPane.ERROR_MESSAGE);
			return false;
		}

		PlayerCharacter pc = party.load(this);
		if ((mainClass != null) && pc != null)
		{
			//if everything loaded successfully, then this file becomes the "current" party file
			partyFileName = file.getAbsolutePath();

			String displayName = party.getDisplayName();

			pcgenMenuBar.openRecentPartyMenu.add(displayName, file);
			enablePartyClose(true);
		}
		else
		{
			//todo: i18n these messages
			ShowMessageDelegate.showMessageDialog(
				"Problems occurred while loading the party.", "Error",
				MessageType.ERROR);
			Logging.errorPrint("PCGen_Frame1: Error in loadPartyFromFile");

			return false;
		}

		return true;
	}

	/**
	 * Creates a new {@link PlayerCharacter} model, and a corresponding
	 * {@link CharacterInfo} panel. The <code>PlayerCharacter</code> is
	 * added to the <code>Globals.getPCList()</code>. The <code>CharacterInfo</code>
	 * is added to both the <code>characterList</code>, and adds it to the main
	 * program frame, <code>baseTabbedPane</code> as a new tab.
	 */
	void newItem_actionPerformed()
	{
		GMBus.send(new NewMessage(this));
	}

	// WIP please leave boomer70
	void doNewNPC()
	{
		if (!Globals.displayListsHappy())
		{
			ShowMessageDelegate.showMessageDialog(PropertyFactory
				.getString("in_newCharNoSources"), Constants.APPLICATION_NAME,
				MessageType.ERROR);

			return;
		}

		final PlayerCharacter pc = new PlayerCharacter();

		final NPCGeneratorDlg genDlg = new NPCGeneratorDlg();
		genDlg.pack();
		genDlg.setVisible(true);

		if (genDlg.getValue() == NPCGeneratorDlg.OK_BUTTON)
		{
			// Add a waiting cursor as this can take some time
			final Cursor oldCursor = getCursor();
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

			final NPCGenerator npcgen = NPCGenerator.getInst();
			npcgen.generate(pc, genDlg.getAlignment(), genDlg.getRace(), genDlg
				.getGender(), genDlg.getClassList(), genDlg.getLevels(), genDlg
				.getRollMethod(), genDlg.getNameChoice());

			Globals.getPCList().add(pc);
			pc.setDirty(true);

			setPC(pc);

			// Put the cursor back to what it was
			setCursor(oldCursor);
		}
	}

	void newNPCItem_actionPerformed()
	{
		doNewNPC();
	}

	void newPopupItem_actionPerformed()
	{
		doNewItem();
	}

	// WIP please leave boomer70
	void newNPCPopupItem_actionPerformed()
	{
		//		doNewItem();
		doNewNPC();
	}

	/**
	 * Launches GMGen.
	 */
	void openGMGen_actionPerformed()
	{
		if (GMGenSystem.inst == null)
		{
			new GMGenSystem();
		}
		else
		{
			GMGenSystem.inst.setVisible(true);
		}
	}

	/**
	 * Load a character into a new <code>PlayerCharacter</code> model, and
	 * create a corresponding <code>CharacterInfo</code> panel.
	 *
	 * @see #newItem_actionPerformed
	 */
	void openItem_actionPerformed()
	{
		toolBar.openItem.requestFocus();
		pcgenMenuBar.openItem.requestFocus();

		// TODO: I18N
		setMessageAreaText("Opening file.  Please wait...");

		// seize the focus to cause focus listeners to fire
		pcgenMenuBar.openItem.requestFocus();

		final JFileChooser fc =
				SettingsHandler.isShowImagePreview() ? ImagePreview
					.decorateWithImagePreview(new JFileChooser())
					: new JFileChooser();
		fc.setCurrentDirectory(SettingsHandler.getLastUsedPcgPath());
		FileTypeMessage ftMessage = new FileTypeMessage(this);
		GMBus.send(ftMessage);
		FileFilter[] ffs = ftMessage.getFileypes();
		for (int i = 0; i < ffs.length; i++)
		{
			fc.addChoosableFileFilter(ffs[i]);
		}

		fc.setFileFilter(filter);
		fc.setMultiSelectionEnabled(true);

		int returnVal = fc.showOpenDialog(PCGen_Frame1.this);

		if (returnVal == JFileChooser.APPROVE_OPTION)
		{
			GMBus.send(new OpenMessage(this, fc.getSelectedFiles()));
		}

		restoreMessageAreaText();
	}

	/**
	 * Close a party metafile, including campaign info and characters.
	 */
	void partyCloseItem_actionPerformed()
	{
		// TODO: I18N
		setMessageAreaText("Closing party...");

		// close all PCs
		closeAllPCs();

		// was closing all PCs successful?
		int tabCount = baseTabbedPane.getTabCount();

		if (tabCount == FIRST_CHAR_TAB)
		{
			// seize the focus to cause focus listeners to fire
			pcgenMenuBar.partyCloseItem.requestFocus();
			enablePartyClose(false);
		}

		restoreMessageAreaText();
	}

	/**
	 * Load a party metafile, including campaign info and characters.
	 * Campaigns are loaded as from the Campaign tab.
	 * Characters are loaded into a new <code>PlayerCharacter</code> model
	 * and a corresponding <code>CharacterInfo</code> panel is created.
	 */
	void partyOpenItem_actionPerformed()
	{
		// TODO: I18N
		setMessageAreaText("Opening party...");

		// seize the focus to cause focus listeners to fire
		pcgenMenuBar.partyOpenItem.requestFocus();

		JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(SettingsHandler.getPcgPath());
		fc.setFileFilter(partyFilter);

		int returnVal = fc.showOpenDialog(PCGen_Frame1.this);

		if (returnVal == JFileChooser.APPROVE_OPTION)
		{
			File file = fc.getSelectedFile();
			SettingsHandler.setLastUsedPcgPath(file.getParentFile());
			loadPartyFromFile(file);
		}

		restoreMessageAreaText();
	}

	void partySaveAsItem_actionPerformed()
	{
		// seize the focus to cause focus listeners to fire
		pcgenMenuBar.partySaveAsItem.requestFocus();

		// Save PCs first so that if you change their names, the party has a chance to know about that.	 --bko
		saveAllPCs();
		partySaveItem(true);
	}

	/**
	 * Saves a party metafile, including campaign info and characters.
	 */
	void partySaveItem_actionPerformed()
	{
		// seize the focus to cause focus listeners to fire
		pcgenMenuBar.partySaveItem.requestFocus();

		// Save PCs first so that if you change their names, the party has a chance to know about that.	 --bko
		saveAllPCs();
		partySaveItem(false);
	}

	/**
	 * Show the preferences pane.
	 */
	public void preferencesItem_actionPerformed()
	{
		// TODO: I18N
		setMessageAreaText("Preferences...");

		//statusBar.updateUI();
		statusBar.revalidate();

		PreferencesDialog.show(this);

		restoreMessageAreaText();
	}

	void printItem_actionPerformed()
	{
		/*
		 * changed this, so a warning will popopup, if user
		 * tries to print without having the needed libraries
		 * installed
		 *
		 * author: Thomas Behr 03-01-02
		 */
		if (!pcgenMenuBar.enablePDF)
		{
			warnAboutMissingResource();

			return;
		}

		pcgenMenuBar.checkPrintFrame();
		pcgenMenuBar.printFrame.setCurrentPCSelectionByTab();
		pcgenMenuBar.printFrame.pack();
		pcgenMenuBar.printFrame.setVisible(true);
	}

	void printPreviewItem_actionPerformed()
	{
		// show the preview in the browser
		Utility.previewInBrowser(getCurrentPC());
	}

	/**
	 * Saves all open characters. Iterates through
	 * <code>Globals.getPCList()</code> (not the tabs) and runs
	 * <code>savePC</code> for each one.
	 */
	void saveAllItem_actionPerformed()
	{
		// seize the focus to cause focus listeners to fire
		pcgenMenuBar.saveAllItem.requestFocus();
		saveAllPCs();
	}

	/**
	 * Saves the character corresponding to the
	 * currently selected tab. The current character is
	 * worked out by taking the <code>(tab position - FIRST_CHAR_TAB)</code>,
	 * and taking the corresponding character from the
	 * <code>Globals.getPCList()</code>.
	 */
	void saveAsItem_actionPerformed()
	{
		// seize the focus to cause focus listeners to fire
		pcgenMenuBar.saveAsItem.requestFocus();

		final PlayerCharacter aPC = getCurrentPC();

		if (aPC == null)
		{
			return;
		}

		savePC(aPC, true);
	}

	void saveAsPopupItem_actionPerformed()
	{
		savePC(getPCForTabAt(popupListener.getTabIndex()), true);
	}

	/**
	 * Saves the character corresponding to the
	 * currently selected tab. The current character is
	 * worked out by taking the <code>(tab position - FIRST_CHAR_TAB)</code>,
	 * and taking the corresponding character from the
	 * <code>Globals.getPCList()</code>.
	 */
	void saveItem_actionPerformed()
	{
		GMBus.send(new SaveMessage(this));
	}

	void savePopupItem_actionPerformed()
	{
		savePC(getPCForTabAt(popupListener.getTabIndex()), false);
	}

	void shiftLeftPopupItem_actionPerformed()
	{
		int index = popupListener.getTabIndex();
		moveTab(index, (index == FIRST_CHAR_TAB) ? (baseTabbedPane
			.getTabCount() - 1) : (index - 1));
	}

	void shiftRightPopupItem_actionPerformed()
	{
		int index = popupListener.getTabIndex();
		moveTab(index, (index == (baseTabbedPane.getTabCount() - 1))
			? FIRST_CHAR_TAB : (index + 1));
	}

	void updateByKludge()
	{
		// What is this bit of oddness?	 XXX
		final KitSelector ks = kitSelector;
		kitSelector = null;

		final int idx = baseTabbedPane.getSelectedIndex();
		baseTabbedPane.setSelectedIndex(0);
		baseTabbedPane.setSelectedIndex(idx);
		kitSelector = ks;
	}

	private static PlayerCharacter getPCForTabAt(int index)
	{
		final int idx = index - FIRST_CHAR_TAB;

		if ((idx >= 0) && (idx < Globals.getPCList().size()))
		{
			return Globals.getPCList().get(idx);
		}
		return null;
	}

	/**
	 * What PC's tab is on top?
	 * @return PlayerCharacter
	 */
	PlayerCharacter getCurrentPC()
	{
		final int currTab = baseTabbedPane.getSelectedIndex();

		if (currTab < FIRST_CHAR_TAB)
		{
			return null;
		}

		return getPCForTabAt(currTab);
	}

	private static void setTabName(int index, String aName)
	{
		getBaseTabbedPane().setTitleAt(index, aName);
	}

	private boolean getUserChoice()
	{
		try
		{
			final AskUserPopup dlg =
					new AskUserPopup(this, "Remove temporary files?", true);
			dlg.setVisible(true);

			return dlg.getDelete();
		}
		catch (Exception e)
		{
			Logging.errorPrint("Error in PCGen_Frame1::getUserChoice", e);
		}

		return false;
	}

	private void addPCTab(PlayerCharacter aPC)
	{
		aPC.addObserver(this);

		if (characterPane == null)
		{
			characterPane = new CharacterInfo(aPC, tempTabList);
		}
		else
		{
			resetCharacterTabs();
		}

		characterPane.resetToSummaryTab();

		baseTabbedPane.addTab(aPC.getDisplayName(), null, characterPane,
			SettingsHandler.isToolTipTextShown() ? aPC.getFullDisplayName()
				: null);
		baseTabbedPane.setSelectedIndex(baseTabbedPane.getTabCount() - 1);
	}

	/**
	 * Shows different menus for different main tabs.
	 * When one of the first two panes (Campaign or GM) is shown,
	 * there are different menu items enabled (such as Save),
	 * than for character tabs.
	 */
	private void baseTabbedPane_changePanel()
	{
		// call requestFocus to prevent open edits
		// from applying to the wrong PC
		baseTabbedPane.requestFocus();

		enableDisableMenuItems();

		killKitSelector();

		final int currentPanel = baseTabbedPane.getSelectedIndex();

		if (currentPanel < FIRST_CHAR_TAB)
		{
			PToolBar.displayHelpPanel(false);
		}
		else
		{
			characterPane.setPc(getCurrentPC());

			if (Globals.getPCList().size() > 1)
			{
				resetCharacterTabs();
			}

			//TODO: DJ This is a memory leak
			Component c = baseTabbedPane.getComponent(currentPanel);
			FocusListener[] f = c.getFocusListeners();
			for (int i = 0; i < f.length; i++)
			{
				c.removeFocusListener(f[i]);
			}
			baseTabbedPane.setComponentAt(currentPanel, characterPane);

			featList_Changed();

			final JTabbedPane aPane =
					(JTabbedPane) characterPane.getComponent(0);
			final int si = aPane.getSelectedIndex();

			if (si >= 0)
			{
				aPane.getComponent(si).requestFocus();

				// force component to get componentShown message
				final ComponentEvent ce =
						new ComponentEvent(aPane.getComponent(si),
							ComponentEvent.COMPONENT_SHOWN);
				aPane.getComponent(si).dispatchEvent(ce);
			}
		}

		// change focus to force new focus listeners to fire
		baseTabbedPane.requestFocus();
	}

	void checkResources()
	{
		if ((JEPResourceChecker.getMissingResourceCount() != 0))
		{
			new LinkableHtmlMessage(this, JEPResourceChecker
				.getMissingResourceMessage(), Constants.APPLICATION_NAME)
				.setVisible(true);
		}
	}

	/**
	 * Close a tab by tab number, not pc number.
	 *
	 * @param index     Tab the character is on (not PC number)
	 * @param isClosing boolean <code>true</code> if closing, <code>false</code> if reverting
	 * @return <code>true</code> if closed; <code>false</code> if still open
	 */
	private boolean closePCTabAt(int index, boolean isClosing)
	{
		boolean bSave = true;
		final PlayerCharacter aPC = getPCForTabAt(index);

		if ((aPC != null) && (aPC.isDirty()))
		{
			int reallyClose = JOptionPane.YES_OPTION;

			if (isClosing)
			{
				reallyClose =
						JOptionPane
							.showConfirmDialog(this, aPC.getDisplayName()
								+ " changed.  Save changes before closing?",
								"Save " + aPC.getDisplayName()
									+ " before closing?",
								JOptionPane.YES_NO_CANCEL_OPTION);
			}
			else
			// reverting
			{
				bSave = false;
			}

			if ((reallyClose == JOptionPane.CANCEL_OPTION)
				|| (reallyClose == JOptionPane.CLOSED_OPTION))
			{
				return false; // don't quit/close
			}

			else if (reallyClose == JOptionPane.NO_OPTION)
			{
				bSave = false;
			}
		}
		else
		{
			bSave = false;
		}

		// make sure that all the spell tables are reset
		// for when the next character get's loaded/viewed
		characterPane.setPaneForUpdate(characterPane.infoSpells());
		characterPane.refresh();

		if (index >= FIRST_CHAR_TAB)
		{
			// save filter settings
			characterPane.storeFilterSettings();

			// bSave has already checked the aPC != null condition, but we 
			// repeat it due to paranoia :)
			if (bSave && aPC != null)
			{
				// Quick hack: blank filename means never saved before
				final String fileName = aPC.getFileName();

				if (!savePC(aPC, fileName.equals("")))
				{
					return false;
				}
			}
		}

		GMBus.send(new PCClosedMessage(this, aPC));

		disposePCTabAt(index);

		return true;
	}

	private void disposePCTabAt(int index)
	{
		int newIndex =
				((index == (baseTabbedPane.getTabCount() - 1)) ? (index - 1)
					: index);

		//This should dispose of the character objects.
		ChangeListener[] cl = baseTabbedPane.getChangeListeners();
		for (int i = 0; i < cl.length; i++)
		{
			baseTabbedPane.removeChangeListener(cl[i]);
		}
		baseTabbedPane.removeTabAt(index);
		for (int i = cl.length - 1; i >= 0; i--)
		{
			baseTabbedPane.addChangeListener(cl[i]);
		}
		Globals.getPCList().remove(index - FIRST_CHAR_TAB);

		// Go to the source tab, not the dm tools, if no pc tabs
		baseTabbedPane.setSelectedIndex((newIndex == (FIRST_CHAR_TAB - 1)) ? 0
			: newIndex);

		// Need to fire this manually
		baseTabbedPane_changePanel();

		// This will free up resources, which are locked
		//PlayerCharacter.dispose();
		killKitSelector();

		// now set the PC to something else
		final PlayerCharacter aPC = getPCForTabAt(newIndex);
	}

	private void doNewItem()
	{
		if (!Globals.displayListsHappy())
		{
			ShowMessageDelegate.showMessageDialog(PropertyFactory
				.getString("in_newCharNoSources"), Constants.APPLICATION_NAME,
				MessageType.ERROR);

			return;
		}

		if (Globals.getGameModeHasPointPool()
			&& !SettingsHandler.getGame().isPurchaseStatMode())
		{
			ShowMessageDelegate.showMessageDialog(
				"In order for this game mode to work properly you "
					+ "need to select a Purchase Mode in "
					+ "Settings->Preferences.\nThere should be a "
					+ "selection for user-rolled abilities as well.",
				Constants.APPLICATION_NAME, MessageType.ERROR);
			return;
		}

		final PlayerCharacter aPC = new PlayerCharacter();
		Globals.getPCList().add(aPC);
		++newPCNameCount;
		aPC.setName("New" + newPCNameCount);
		aPC.setDirty(true);

		addPCTab(aPC);
		GMBus.send(new PCLoadedMessage(this, aPC));
	}

	/**
	 * Enable/disable the close item
	 * @param itemState
	 */
	private void enableClose(boolean itemState)
	{
		pcgenMenuBar.closeItem.setEnabled(itemState);
		toolBar.closeItem.setEnabled(itemState);
		pcPopupMenu.getCloseItem().setEnabled(itemState);
	}

	/**
	 * Enable/disable the closeAll item
	 * @param itemState
	 */
	private void enableCloseAll(boolean itemState)
	{
		pcgenMenuBar.closeAllItem.setEnabled(itemState);
	}

	/**
	 * Enable/disable the export menu
	 * @param itemState
	 */
	private void enableExport(boolean itemState)
	{
		pcgenMenuBar.exportMenu.setEnabled(itemState);
	}

	private void enableKit(boolean itemState)
	{
		pcgenMenuBar.addKit.setEnabled(itemState);
		toolBar.addKit.setEnabled(itemState);
	}

	/**
	 * Enable/disable the partyClose item
	 * @param itemState
	 */
	private void enablePartyClose(boolean itemState)
	{
		pcgenMenuBar.partyCloseItem.setEnabled(itemState);
	}

	/**
	 * Enable/disable the partySave item
	 * @param itemState
	 */
	private void enablePartySave(boolean itemState)
	{
		pcgenMenuBar.partySaveItem.setEnabled(itemState);
	}

	/**
	 * Enable/disable the partySaveAs item
	 * @param itemState
	 */
	private void enablePartySaveAs(boolean itemState)
	{
		pcgenMenuBar.partySaveAsItem.setEnabled(itemState);
	}

	/**
	 * Enable/disable the print item
	 * @param itemState
	 */
	private void enablePrint(boolean itemState)
	{
		pcgenMenuBar.printItem.setEnabled(itemState);
		toolBar.printItem.setEnabled(itemState);
	}

	/**
	 * Enable/disable the printPreview item
	 * @param itemState
	 */
	private void enablePrintPreview(boolean itemState)
	{
		pcgenMenuBar.printPreviewItem.setEnabled(itemState);
		toolBar.printPreviewItem.setEnabled(itemState);
	}

	/**
	 * Enable/disable the revertToSaved item
	 * @param itemState
	 */
	private void enableRevertToSaved(boolean itemState)
	{
		pcgenMenuBar.revertToSavedItem.setEnabled(itemState);
		pcPopupMenu.getRevertToSavedItem().setEnabled(itemState);
	}

	/**
	 * Enable/disable the save item
	 * @param itemState
	 */
	private void enableSave(boolean itemState)
	{
		pcgenMenuBar.saveItem.setEnabled(itemState);
		toolBar.saveItem.setEnabled(itemState);
		pcPopupMenu.getSaveItem().setEnabled(itemState);
	}

	/**
	 * Enable/disable the saveAll item
	 * @param itemState
	 */
	private void enableSaveAll(boolean itemState)
	{
		pcgenMenuBar.saveAllItem.setEnabled(itemState);
	}

	/**
	 * Enable/disable the saveAs item
	 * @param itemState
	 */
	private void enableSaveAs(boolean itemState)
	{
		pcgenMenuBar.saveAsItem.setEnabled(itemState);
		pcPopupMenu.getSaveAsItem().setEnabled(itemState);
	}

	/**
	 * Closes the program by calling <code>handleQuit</code>
	 */
	public void exitItem_actionPerformed()
	{
		// seize the focus to cause focus listeners to fire
		pcgenMenuBar.exitItem.requestFocus();
		handleQuit();
	}

	private void handleInitHolderListSendMessage(
		InitHolderListSendMessage message)
	{
		InitHolderList list = message.getInitHolderList();

		for (int i = 0; i < list.size(); i++)
		{
			InitHolder iH = list.get(i);

			if (iH instanceof PcgCombatant)
			{
				PcgCombatant pcg = (PcgCombatant) iH;
				PlayerCharacter aPC = pcg.getPC();
				Globals.getPCList().add(aPC);
				aPC.setDirty(true);
				addPCTab(aPC);
			}
		}
	}

	private void handleOpenPCGRequestMessage(OpenPCGRequestMessage message)
	{
		File pcFile = message.getFile();

		if (PCGFile.isPCGenCharacterFile(pcFile))
		{
			message.setPlayerCharacter(loadPCFromFile(pcFile, message
				.blockLoadedMessage(), false));
		}
		else if (PCGFile.isPCGenPartyFile(pcFile))
		{
			loadPartyFromFile(pcFile);
		}
	}

	private void handleFetchOpenPCGRequestMessage()
	{
		for (int i = 0; i < Globals.getPCList().size(); i++)
		{
			GMBus.send(new PCLoadedMessage(this, Globals.getPCList().get(i)));
		}
	}

	/**
	 * Does the real work in closing the program.
	 * Closes each character tab, giving user a chance to save.
	 * Saves options to file, then cleans up and exits.
	 */
	private void handleQuit()
	{
		if (SettingsHandler.getLeftUpperCorner() == null)
		{
			SettingsHandler.setLeftUpperCorner(new Point(0, 0));
		}

		if (getState() != Frame.ICONIFIED)
		{
			SettingsHandler.getLeftUpperCorner().setLocation(
				getLocationOnScreen().getX(), getLocationOnScreen().getY());
		}

		//Get the maximized state of the window (1.4 ONLY!!!!)
		//Make sure we're not getting iconified state.
		if ((getExtendedState() & Frame.MAXIMIZED_BOTH) != 0)
		{
			SettingsHandler.setWindowState(Frame.MAXIMIZED_BOTH);
		}
		else if ((getExtendedState() & Frame.MAXIMIZED_HORIZ) != 0)
		{
			SettingsHandler.setWindowState(Frame.MAXIMIZED_HORIZ);
		}
		else if ((getExtendedState() & Frame.MAXIMIZED_VERT) != 0)
		{
			SettingsHandler.setWindowState(Frame.MAXIMIZED_VERT);
		}
		else
		{
			SettingsHandler.setWindowState(Frame.NORMAL);
		}

		int tabCount = baseTabbedPane.getTabCount();

		while (tabCount > FIRST_CHAR_TAB)
		{
			if (closePCTabAt(tabCount - 1, true))
			{
				tabCount = baseTabbedPane.getTabCount();
			}
			else
			{
				//Stop closing if user wants to save an unsaved tab.
				return;
			}
		}

		GMBus.send(new WindowClosedMessage(this));

		SettingsHandler.storeFilterSettings(mainSource);

		// Need to (possibly) write customEquipment.lst
		if (SettingsHandler.getSaveCustomEquipment())
		{
			CustomData.writeCustomItems();
		}

		//
		// Clean up our temporary files
		//
		removeTemporaryFiles();

		SettingsHandler.writeOptionsProperties(getCurrentPC());

		this.dispose();
		System.exit(0);
	}

	private void handleStateChangedMessage()
	{
		if (this.isFocused() && characterPane != null)
		{
			PlayerCharacter aPC =
					getPCForTabAt(baseTabbedPane.getSelectedIndex());
			// What could possibly have changed on focus that would
			// require a forceUpdate of all the panels?
			// JSC -- 03/27/2004
			//
			// The answer to this question is this: GMGen can update characters, for
			// example updating the experience.  This message is only ever really
			// called when the user switches from gmgen to pcgen - and it needs to be
			// called to ensure that all pcgen screens are updated based on any
			// changes to the PlayerCharacter object.  Without this, what the user
			// sees on the screen is stale.
			// DJ -- 05/23/2004
			characterPane
				.setPaneForUpdate(characterPane.infoSpecialAbilities());
			characterPane.setPaneForUpdate(characterPane.infoSummary());
			characterPane.setPaneForUpdate(characterPane.infoRace());
			characterPane.setPaneForUpdate(characterPane.infoClasses());
			characterPane.setPaneForUpdate(characterPane.infoDomain());
			//			characterPane.setPaneForUpdate(characterPane.infoFeats());
			characterPane.setPaneForUpdate(characterPane.infoAbilities());
			characterPane.setPaneForUpdate(characterPane.infoSkills());
			characterPane.setPaneForUpdate(characterPane.infoSpells());
			characterPane.setPaneForUpdate(characterPane.infoInventory());
			characterPane.setPaneForUpdate(characterPane.infoDesc());
			characterPane.refresh();
			forceUpdate_PlayerTabs();
		}
	}

	private void handleTabAddMessage(TabAddMessage message)
	{
		if (message.getSystem().equals(Constants.s_SYSTEM_PCGEN))
		{
			if (message.getPane() instanceof CharacterInfoTab)
			{
				if (characterPane == null)
				{
					tempTabList.add(message.getPane());
				}
				else
				{
					characterPane.addTab((CharacterInfoTab) message.getPane());
				}
			}
			else
			{
				FIRST_CHAR_TAB++;
				baseTabbedPane.addTab(message.getName(), message.getPane());
			}
		}
	}

	private void handlePauseRefreshMessage()
	{
		if (characterPane != null)
		{
			characterPane.setRefresh(false);
		}
	}

	private void handleResumeRefreshMessage()
	{
		if (characterPane != null)
		{
			characterPane.setRefresh(true);
			characterPane.refresh();
		}
	}

	/**
	 * Real screen initialization is done here. Sets up all
	 * the window properties (icon, title, size).
	 * <p/>
	 * Creates the campaign and DM tools tabs, along with all
	 * the sub-panes of DM tools.
	 *
	 * @throws Exception Any Exception
	 */
	private void jbInit() throws Exception
	{
		IconUtilitities.maybeSetIcon(this, IconUtilitities.RESOURCE_APP_ICON);

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(panelMain, BorderLayout.CENTER);
		panelMain.setLayout(borderLayout1);
		setSize(new Dimension(700, 600));
		setGameModeTitle();

		setJMenuBar(pcgenMenuBar);

		panelMain.add(toolBar, BorderLayout.NORTH);
		showToolBar();

		baseTabbedPane.setTabPlacement(SettingsHandler.getTabPlacement());
		baseTabbedPane.setDoubleBuffered(true);
		baseTabbedPane.setMinimumSize(new Dimension(620, 350));
		baseTabbedPane.setPreferredSize(new Dimension(620, 350));
		baseTabbedPane.addMouseListener(popupListener);

		panelSouth.setLayout(borderLayout2);
		panelSouthEast.setLayout(flowLayout1);
		flowLayout1.setAlignment(FlowLayout.RIGHT);
		panelSouthCenter.setLayout(flowLayout2);
		flowLayout2.setAlignment(FlowLayout.LEFT);

		panelMain.add(baseTabbedPane, BorderLayout.CENTER);

		GameMode game = SettingsHandler.getGame();

		mainSource = new MainSource(false);
		if ((game != null) && (game.getTabShown(Tab.SOURCES)) && SettingsHandler.useAdvancedSourceSelect())
		{
			baseTabbedPane.addTab(game.getTabName(Tab.SOURCES), mainSource);
			baseTabbedPane.setToolTipTextAt(0, SettingsHandler
				.isToolTipTextShown() ? MainSource.SOURCE_MATERIALS_TAB : null);
		}
		else
		{
			infoPanel = new InfoPanel();
			baseTabbedPane.addTab(game.getTabName(Tab.INFO), infoPanel);
			baseTabbedPane.setToolTipTextAt(0, SettingsHandler
				.isToolTipTextShown() ? MainSource.SOURCE_MATERIALS_TAB : null);
		}

		this.getContentPane().add(panelSouth, BorderLayout.SOUTH);
		panelSouth.add(statusBar, BorderLayout.SOUTH);
		baseTabbedPane.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent c)
			{
				baseTabbedPane_changePanel();
			}
		});

		mainSource.addComponentListener(toolBar.getComponentListener());

		/* Disabled under Windows PLAF as it was breaking drop-down navigation
		 * via the mouse on a number of pages (e.g. Alignment, race, class on
		 * the summary page)
		 * James Dempsey jdempsey@users.sourceforge.net 01 Feb 2004
		 */
		if (!(UIFactory.isWindowsUI() && System.getProperty("java.version")
			.startsWith("1.4.0")))
		{
			addWindowFocusListener(new java.awt.event.WindowFocusListener()
			{
				public void windowGainedFocus(java.awt.event.WindowEvent e)
				{
					stateUpdate(e);
				}

				public void windowLostFocus(java.awt.event.WindowEvent e)
				{
					// TODO This method currently does nothing?
				}
			});
		}
	}

	private void killKitSelector()
	{
		if (kitSelector != null)
		{
			if (kitSelector.isVisible())
			{
				kitSelector.closeDialog();
			}

			kitSelector = null;
		}
	}

	private void moveTab(int oldIndex, int newIndex)
	{
		// Because the tabs are "fake", we need to reorder the
		// PCList in Globals, then simply refresh.
		List<PlayerCharacter> pcList = Globals.getPCList();
		PlayerCharacter aPC = pcList.get(oldIndex - FIRST_CHAR_TAB);
		pcList.remove(oldIndex - FIRST_CHAR_TAB);
		pcList.add(newIndex - FIRST_CHAR_TAB, aPC);
		Globals.setPCList(pcList);

		forceUpdate_PlayerTabs();
		baseTabbedPane.setSelectedIndex(newIndex);
	}

	private void partySaveItem(boolean saveas)
	{
		boolean newParty = false;
		File prevFile;
		File file;

		if (partyFileName.length() == 0)
		{
			prevFile =
					new File(SettingsHandler.getPcgPath(), "Party"
						+ Constants.s_PCGEN_PARTY_EXTENSION);
			partyFileName = prevFile.getAbsolutePath();
			newParty = true;
		}
		else
		{
			prevFile = new File(partyFileName);
		}

		file = prevFile;

		if (saveas || newParty)
		{
			JFileChooser fc = new JFileChooser();
			fc.setFileFilter(partyFilter);
			fc.setSelectedFile(prevFile);

			FilenameChangeListener listener =
					new FilenameChangeListener(partyFileName, fc);
			fc.addPropertyChangeListener(listener);

			int returnVal = fc.showSaveDialog(PCGen_Frame1.this);
			fc.removePropertyChangeListener(listener);

			if (returnVal == JFileChooser.APPROVE_OPTION)
			{
				file = fc.getSelectedFile();

				if (!PCGFile.isPCGenPartyFile(file))
				{
					file =
							new File(file.getParent(), file.getName()
								+ Constants.s_PCGEN_PARTY_EXTENSION);
				}

				if (file.isDirectory())
				{
					ShowMessageDelegate.showMessageDialog(
						"You cannot overwrite a directory with a party.",
						Constants.APPLICATION_NAME, MessageType.ERROR);

					return;
				}

				// No need to check that prevFile == null as we know it's not from above
				if (file.exists()
					&& (newParty || !file.getName().equals(prevFile.getName())))
				{
					int reallyClose =
							JOptionPane
								.showConfirmDialog(
									this,
									"The file "
										+ file.getName()
										+ " already exists, are you sure you want to overwrite it?",
									"Confirm overwriting " + file.getName(),
									JOptionPane.YES_NO_OPTION);

					if (reallyClose != JOptionPane.YES_OPTION)
					{
						return;
					}
				}
			}
			else
			{
				return;
			}
		}
		try
		{
			partyFileName = file.getAbsolutePath();

			Party party = Party.makePartyFromFile(file);
			party.addAllOpenCharacters();
			party.save();
			enablePartyClose(true);
		}
		catch (IOException ex)
		{
			ShowMessageDelegate.showMessageDialog("Could not save "
				+ partyFileName, Constants.APPLICATION_NAME, MessageType.ERROR);
			Logging.errorPrint("Could not save" + partyFileName, ex);

			return;
		}

		pcgenMenuBar.openRecentPartyMenu.add(partyFileName, file);
	}

	protected void resetUI()
	{
		mainSource.resetUI();
	}

	private void removeTemporaryFiles()
	{
		final boolean cleanUp = SettingsHandler.getCleanupTempFiles();

		if (!cleanUp)
		{
			return;
		}

		final String aDirectory =
				SettingsHandler.getTempPath() + File.separator;
		new File(aDirectory).list(new FilenameFilter()
		{
			boolean myCleanUp = cleanUp;

			public boolean accept(File aFile, String aString)
			{
				try
				{
					if (aString.startsWith(Constants.s_TempFileName))
					{
						if (!myCleanUp)
						{
							myCleanUp = getUserChoice();
						}

						if (myCleanUp)
						{
							final File tf = new File(aFile, aString);
							tf.delete();
						}
					}
				}
				catch (Exception e)
				{
					Logging.errorPrint("removeTemporaryFiles", e);
				}

				return false;
			}
		});
	}

	private void resetCharacterTabs()
	{
		if (characterPane == null)
		{
			return;
		}

		for (int i = FIRST_CHAR_TAB; i < baseTabbedPane.getTabCount(); ++i)
		{
			baseTabbedPane.setComponentAt(i, new JPanel());
		}
	}

	private void saveAllPCs()
	{
		for (int i = 0, x = Globals.getPCList().size(); i < x; ++i)
		{
			PlayerCharacter aPC = Globals.getPCList().get(i);

			if (aPC.isDirty())
			{
				savePC(aPC, false);
			}
		}
	}

	private void showToolBar()
	{
		toolBar.setVisible(SettingsHandler.isToolBarShown());
	}

	void warnAboutMissingResource()
	{
		new LinkableHtmlMessage(this, FOPResourceChecker
			.getMissingResourceMessage(), Constants.APPLICATION_NAME).setVisible(true);
	}

	/**
	 * Property change listener for the event "selected file
	 * changed".  Ensures that the filename doesn't get changed
	 * when a directory is selected.
	 *
	 * @author Dmitry Jemerov <yole@spb.cityline.ru>
	 */
	static final class FilenameChangeListener implements PropertyChangeListener
	{
		private JFileChooser fileChooser;
		private String lastSelName;

		FilenameChangeListener(String aFileName, JFileChooser aFileChooser)
		{
			lastSelName = aFileName;
			fileChooser = aFileChooser;
		}

		public void propertyChange(PropertyChangeEvent evt)
		{
			String propName = evt.getPropertyName();
			if (propName.equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY))
			{
				onSelectedFileChange(evt);
			}
			else if (propName.equals(JFileChooser.DIRECTORY_CHANGED_PROPERTY))
			{
				onDirectoryChange();
			}
		}

		private void onDirectoryChange()
		{
			fileChooser.setSelectedFile(new File(fileChooser
				.getCurrentDirectory(), lastSelName));
		}

		private void onSelectedFileChange(PropertyChangeEvent evt)
		{
			File newSelFile = (File) evt.getNewValue();

			if ((newSelFile != null) && !newSelFile.isDirectory())
			{
				lastSelName = newSelFile.getName();
			}
		}
	}

	/**
	 * Support for popup menus on player tabs.  This is too easy.
	 */
	static final class PopupListener extends MouseAdapter
	{
		JTabbedPane tabbedPane;
		MainPopupMenu mainPopupMenu;
		PCPopupMenu pcPopupMenu;
		int index;

		/**
		 * Constructor for the pop up menus listener
		 * @param aTabbedPane
		 * @param theMainPopupMenu
		 * @param thePcPopupMenu
		 */
		PopupListener(JTabbedPane aTabbedPane, MainPopupMenu theMainPopupMenu,
			PCPopupMenu thePcPopupMenu)
		{
			this.tabbedPane = aTabbedPane;
			this.mainPopupMenu = theMainPopupMenu;
			this.pcPopupMenu = thePcPopupMenu;
		}

		/**
		 * Get the index of the current tab
		 * @return index of current tab
		 */
		public int getTabIndex()
		{
			return index;
		}

		/**
		 * Fire off the mouse pressed event
		 * @param e The event
		 */
		@Override
		public void mousePressed(MouseEvent e)
		{
			// Work-around: W32 returns false even on
			// right-mouse clicks
			if (!(e.isPopupTrigger() || SwingUtilities.isRightMouseButton(e)))
			{
				return;
			}

			index = indexAtLocation(e.getX(), e.getY());

			// Clicked somewhere besides a tab
			if (index < 0)
			{
				return;
			}

			enableDisableMenuItems();

			if (index < FIRST_CHAR_TAB)
			{
				mainPopupMenu.show(e.getComponent(), e.getX(), e.getY());

				return;
			}

			int tabCount = tabbedPane.getTabCount();

			if (tabCount == FIRST_CHAR_TAB + 1)
			{ // one PC tab only -- no shifting
				pcPopupMenu.setShiftType(PCPopupMenu.SHIFT_NONE);
			}
			else if (tabCount == FIRST_CHAR_TAB + 2)
			{ // two PC tabs -- support swapping
				pcPopupMenu.setShiftType((index == 1) ? PCPopupMenu.SHIFT_RIGHT
					: PCPopupMenu.SHIFT_LEFT);
			}
			else
			{ // many PC tabs -- support cycling
				if (index == FIRST_CHAR_TAB)
				{
					pcPopupMenu.setShiftType(PCPopupMenu.SHIFT_END_RIGHT);
				}
				else if (index == (tabCount - 1))
				{
					pcPopupMenu.setShiftType(PCPopupMenu.SHIFT_LEFT_BEGINNING);
				}
				else
				{
					pcPopupMenu.setShiftType(PCPopupMenu.SHIFT_LEFT_RIGHT);
				}
			}

			pcPopupMenu.show(e.getComponent(), e.getX(), e.getY());
		}

		// Missing from JTabbedPane < 1.4
		private int indexAtLocation(int x, int y)
		{
			TabbedPaneUI ui = tabbedPane.getUI();

			if (ui != null)
			{
				return ui.tabForCoordinate(tabbedPane, x, y);
			}

			return -1;
		}
	}

	private class AskUserPopup extends JDialog
	{
		static final long serialVersionUID = 1042236188732008819L;
		private boolean doDelete = false;

		/**
		 * Constructor for the ask a user pop up
		 * @param owner
		 * @param title
		 * @param modal
		 */
		public AskUserPopup(JFrame owner, String title, boolean modal)
		{
			super(owner, title, modal);
			initComponents();
			setLocationRelativeTo(owner);
		}

		/**
		 * Get the doDelete flag
		 * @return true if we delete, else false if we don't
		 */
		public boolean getDelete()
		{
			return doDelete;
		}

		private void setDelete(boolean argDoDelete)
		{
			doDelete = argDoDelete;
		}

		private void initComponents()
		{
			final JButton btnYes = new JButton("Yes");
			final JButton btnNo = new JButton("No");
			final JPanel jPanel = new JPanel();
			final JCheckBox chkDontAsk = new JCheckBox("Don't ask again");
			jPanel.add(chkDontAsk);
			jPanel.add(btnYes);
			jPanel.add(btnNo);
			btnYes.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					if (chkDontAsk.isSelected())
					{
						SettingsHandler.setCleanupTempFiles(true);
					}

					setDelete(true);
					dispose();
				}
			});
			btnNo.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					if (chkDontAsk.isSelected())
					{
						SettingsHandler.setCleanupTempFiles(false);
					}

					dispose();
				}
			});
			getContentPane().setLayout(new BorderLayout());
			getContentPane().add(jPanel, BorderLayout.SOUTH);
			pack();
		}
	}

	/**
	 * From http://www.javaworld.com/javaworld/javatips/jw-javatip87.html
	 */
	private static final class WaitCursorEventQueue extends EventQueue
	{
		private WaitCursorTimer waitTimer;
		private int delay;

		/**
		 * The constructor for the wait cursor event queue
		 * @param theDelay
		 */
		public WaitCursorEventQueue(int theDelay)
		{
			this.delay = theDelay;
			waitTimer = new WaitCursorTimer();
			waitTimer.setDaemon(true);
			waitTimer.start();
		}

		/**
		 * Pop an event off the queue
		 */
		public void doPop()
		{
			pop();
		}

		/**
		 * Dispatch and AWT event
		 * @param event The event to dispatch
		 */
		@Override
		protected void dispatchEvent(AWTEvent event)
		{
			waitTimer.startTimer(event.getSource());

			try
			{
				super.dispatchEvent(event);
			}
			catch (Exception e)
			{
				//Logging.reportAllThreads();
				Logging.errorPrint("Uncaught error - ignoring", e);
			}
			finally
			{
				waitTimer.stopTimer();
			}
		}

		private final class WaitCursorTimer extends Thread
		{
			private Component parent;
			private Object source;

			/**
			 * The standard run method for threaded code, in this 
			 * case deals with the waitMouseCursor thread
			 */
			@Override
			public synchronized void run()
			{
				while (true)
				{
					try
					{
						//wait for notification from startTimer()
						this.wait();

						//wait for event processing to
						//reach the threshold, or
						//interruption from
						//stopTimer()
						this.wait(delay);

						if (source instanceof Component)
						{
							parent = SwingUtilities.getRoot((Component) source);
						}
						else if (source instanceof MenuComponent)
						{
							MenuContainer mParent =
									((MenuComponent) source).getParent();

							if (mParent instanceof Component)
							{
								parent =
										SwingUtilities
											.getRoot((Component) mParent);
							}
						}

						if ((parent != null) && parent.isShowing())
						{
							parent.setCursor(Cursor
								.getPredefinedCursor(Cursor.WAIT_CURSOR));
						}
					}
					catch (InterruptedException ie)
					{
						// TODO - Handle Exception
					}
				}
			}

			synchronized void startTimer(Object argSource)
			{
				this.source = argSource;
				this.notify();
			}

			synchronized void stopTimer()
			{
				if (parent == null)
				{
					interrupt();
				}
				else
				{
					parent.setCursor(null);
					parent = null;
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update(Observable o, Object arg)
	{
		// A character has been updated.
		if (arg != null)
		{
			if ("TabName".equals(arg.toString()))
			{
				forceUpdate_PlayerTabs();
			}
		}
		PCGen_Frame1.enableDisableMenuItems();
	}

	/**
	 * Set the character pane on the frame
	 * @param theCharacterPane to set
	 */
	public static void setCharacterPane(CharacterInfo theCharacterPane)
	{
		PCGen_Frame1.characterPane = theCharacterPane;
	}

	/**
	 * Bring up the About dialog box.
	 */
	public void aboutItem_actionPerformed()
	{
		pcgenMenuBar.handleAbout();
	}

	/**
	 * Refresh the names of the character tabs - they may be changed when 
	 * the game mode is changed. 
	 */
	public void refreshCharInfoTabs()
	{
		if (characterPane != null)
		{
			characterPane.refreshCharInfoTabs();
		}
	}
	
	/**
	 * Switch the source selection method in use between the basic (quick 
	 * source dialog) and advanced (MainSource). Also sets the preference 
	 * to match for next time.
	 * @param advanced Should the advanced page be used (true) or the basic page (false).
	 */
	public void switchSourceSelectMeans(boolean advanced)
	{
		GameMode game = SettingsHandler.getGame();
		if (advanced)
		{
			baseTabbedPane.setComponentAt(0, mainSource);
			baseTabbedPane.setTitleAt(0, game.getTabName(Tab.SOURCES));
			baseTabbedPane.setToolTipTextAt(0, SettingsHandler
				.isToolTipTextShown() ? MainSource.SOURCE_MATERIALS_TAB : null);
		}
		else
		{
			if (infoPanel == null)
			{
				infoPanel = new InfoPanel();
			}
			baseTabbedPane.setComponentAt(0, infoPanel);
			baseTabbedPane.setTitleAt(0, game.getTabName(Tab.INFO));
			baseTabbedPane.setToolTipTextAt(0, SettingsHandler
				.isToolTipTextShown() ? MainSource.SOURCE_MATERIALS_TAB : null);

			PCGen_Frame1.setMessageAreaText(PropertyFactory
				.getString("in_qsrc_messageText"));

			SourceSelectionDialog dialog =
					new SourceSelectionDialog(PCGen_Frame1.getInst(), true);
			dialog.setVisible(true);

			PCGen_Frame1.restoreMessageAreaText();
		}

		SettingsHandler.setUseAdvancedSourceSelect(advanced);
	}
}
