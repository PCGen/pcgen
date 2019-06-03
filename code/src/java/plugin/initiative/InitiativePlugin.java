/*
 *  Initiative - A role playing utility to track turns
 *  Copyright (C) 2002 Devon D Jones
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
package plugin.initiative;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Collection;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;

import gmgen.GMGenSystem;
import gmgen.GMGenSystemView;
import gmgen.plugin.InitHolder;
import gmgen.plugin.InitHolderList;
import gmgen.plugin.PcgCombatant;
import gmgen.pluginmgr.messages.AddMenuItemToGMGenToolsMenuMessage;
import gmgen.pluginmgr.messages.CombatHasBeenInitiatedMessage;
import gmgen.pluginmgr.messages.FileMenuOpenMessage;
import gmgen.pluginmgr.messages.FileMenuSaveMessage;
import gmgen.pluginmgr.messages.RequestAddPreferencesPanelMessage;
import gmgen.pluginmgr.messages.RequestAddTabToGMGenMessage;
import gmgen.util.LogUtilities;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.gui2.tools.Utility;
import pcgen.io.PCGFile;
import pcgen.pluginmgr.InteractivePlugin;
import pcgen.pluginmgr.PCGenMessage;
import pcgen.pluginmgr.PCGenMessageHandler;
import pcgen.pluginmgr.messages.FocusOrStateChangeOccurredMessage;
import pcgen.pluginmgr.messages.PlayerCharacterWasClosedMessage;
import pcgen.pluginmgr.messages.PlayerCharacterWasLoadedMessage;
import pcgen.pluginmgr.messages.RequestOpenPlayerCharacterMessage;
import pcgen.pluginmgr.messages.RequestToSavePlayerCharacterMessage;
import pcgen.pluginmgr.messages.TransmitInitiativeValuesBetweenComponentsMessage;
import pcgen.system.PCGenSettings;
import plugin.initiative.gui.Initiative;
import plugin.initiative.gui.PreferencesDamagePanel;
import plugin.initiative.gui.PreferencesInitiativePanel;
import plugin.initiative.gui.PreferencesMassiveDamagePanel;
import plugin.initiative.gui.PreferencesPerformancePanel;

import javafx.stage.FileChooser;

/**
 * The {@code ExperienceAdjusterController} handles the functionality of
 * the Adjusting of experience. This class is called by the {@code GMGenSystem}
 * and will have it's own model and view. <br>
 */
public class InitiativePlugin implements InteractivePlugin
{

	/** Name used for initiative logging. */
	public static final String LOG_NAME = "Initiative"; //$NON-NLS-1$

	/** The user interface that this class will be using. */
	private Initiative theView;

	/** The plugin menu item in the tools menu. */
	private final JMenuItem initToolsItem = new JMenuItem();

	/** The English name of the plugin. */
	private static final String name = "Initiative";

	private PCGenMessageHandler messageHandler;

	/**
	 * Starts the plugin, registering itself with the {@code TabAddMessage}.
	 */
	@Override
	public void start(PCGenMessageHandler mh)
	{
		messageHandler = mh;
		theView = new Initiative();
		messageHandler.handleMessage(new RequestAddPreferencesPanelMessage(this, new PreferencesDamagePanel()));
		messageHandler
			.handleMessage(new RequestAddPreferencesPanelMessage(this, new PreferencesMassiveDamagePanel()));
		messageHandler
			.handleMessage(new RequestAddPreferencesPanelMessage(this, new PreferencesInitiativePanel()));
		messageHandler
			.handleMessage(new RequestAddPreferencesPanelMessage(this, new PreferencesPerformancePanel()));

		theView.setLog(LogUtilities.inst());
		messageHandler.handleMessage(new RequestAddTabToGMGenMessage(this, name, getView()));
		initMenus();
	}

	@Override
	public void stop()
	{
		messageHandler = null;
	}

	@Override
	public int getPriority()
	{
		return SettingsHandler.getGMGenOption(LOG_NAME + ".LoadOrder", 40);
	}

	/**
	 * Accessor for name
	 *
	 * @return name
	 */
	@Override
	public String getPluginName()
	{
		return name;
	}

	/**
	 * Gets the view that this class is using.
	 *
	 * @return the view.
	 */
	public Component getView()
	{
		return theView;
	}

	/**
	 * Handles the clicking of the <b>Add </b> button on the GUI.
	 */
	private void fileOpen()
	{
		FileChooser fileChooser = new FileChooser();
		// TODO: i18n
		fileChooser.setTitle("Initiative Export");
		FileChooser.ExtensionFilter pcgenFilter = new FileChooser.ExtensionFilter(
				"PCGen File", "*.pcp", "*.pcg"
		);
		FileChooser.ExtensionFilter initFilter = new FileChooser.ExtensionFilter(
				"PCGen File", "*.gmi", "*.init"
		);
		fileChooser.getExtensionFilters().add(pcgenFilter);
		fileChooser.getExtensionFilters().add(initFilter);
		fileChooser.setSelectedExtensionFilter(initFilter);
		File defaultFile = new File(PCGenSettings.getPcgDir());
		fileChooser.setInitialDirectory(defaultFile);

		// TODO: set to parent once converted to JavaFX
		Collection<File> pcFiles = fileChooser.showOpenMultipleDialog(null);
		if (pcFiles == null)
		{
			return;
		}

		for (File pcFile : pcFiles)
		{
			if (PCGFile.isPCGenCharacterOrPartyFile(pcFile))
			{
				messageHandler.handleMessage(new RequestOpenPlayerCharacterMessage(this, pcFile, false));

				//loadPCG(pcFiles[i]);
			}
			else if (pcFile.toString().endsWith(".init") || pcFile.toString().endsWith(".gmi"))
			{
				loadINIT(pcFile);
			}
		}

		theView.refreshTable();
	}

	/**
	 * <p>
	 * Gets the internal view's {@code InitHolderList}
	 * </p>
	 *
	 * @param message
	 */
	private void handleCombatRequestMessage(CombatHasBeenInitiatedMessage message)
	{
		message.setCombat(theView.initList);
	}

	/**
	 * <p>
	 * Delegates to {@code handleAddButton()}
	 * </p>
	 *
	 * @param message
	 */
	private void handleFileOpenMessage(FileMenuOpenMessage message)
	{
		if (GMGenSystemView.getTabPane().getSelectedComponent() instanceof Initiative)
		{
			fileOpen();
		}
	}

	/**
	 * <p>
	 * Handles an {@code InitHolderListSendMessage} by addomg all new
	 * combatants to the views list.
	 * </p>
	 *
	 * @param message
	 */
	private void handleInitHolderListSendMessage(TransmitInitiativeValuesBetweenComponentsMessage message)
	{
		if (message.getSource() != this)
		{
			InitHolderList cl = message.getInitHolderList();

			for (InitHolder iH : cl)
			{
				theView.addInitHolder(iH);
			}

			theView.refreshTable();
		}
	}

	/**
	 * <p>
	 * Listens to messages from the GMGen system, and handles them as needed
	 * </p>
	 *
	 * @param message
	 *          the source of the event from the system
	 */
	@Override
	public void handleMessage(PCGenMessage message)
	{
		if (message instanceof FileMenuOpenMessage)
		{
			handleFileOpenMessage((FileMenuOpenMessage) message);
		}
		else if (message instanceof FileMenuSaveMessage)
		{
			handleSaveMessage((FileMenuSaveMessage) message);
		}
		else if (message instanceof TransmitInitiativeValuesBetweenComponentsMessage)
		{
			handleInitHolderListSendMessage((TransmitInitiativeValuesBetweenComponentsMessage) message);
		}
		else if (message instanceof PlayerCharacterWasLoadedMessage)
		{
			handlePCLoadedMessage((PlayerCharacterWasLoadedMessage) message);
		}
		else if (message instanceof PlayerCharacterWasClosedMessage)
		{
			handlePCClosedMessage((PlayerCharacterWasClosedMessage) message);
		}
		else if (message instanceof FocusOrStateChangeOccurredMessage)
		{
			handleStateChangedMessage((FocusOrStateChangeOccurredMessage) message);
		}
		else if (message instanceof CombatHasBeenInitiatedMessage)
		{
			handleCombatRequestMessage((CombatHasBeenInitiatedMessage) message);
		}
	}

	/**
	 * <p>
	 * Removes the closed PC from the combat.
	 * </p>
	 *
	 * @param message
	 */
	private void handlePCClosedMessage(PlayerCharacterWasClosedMessage message)
	{
		theView.removePcgCombatant(message.getPC());
		theView.refreshTable();
	}

	/**
	 * <p>
	 * Adds the specified pc to the combat.
	 * </p>
	 *
	 * @param message
	 */
	private void handlePCLoadedMessage(PlayerCharacterWasLoadedMessage message)
	{
		PlayerCharacter pc = message.getPc();
		String type = "PC";
		String player = pc.getDisplay().getPlayersName();

		//Based on the Player's name, auto set the combatant's type
		if (player.equalsIgnoreCase("Ally"))
		{
			type = "Ally";
		}
		else if (player.equalsIgnoreCase("GM") || player.equalsIgnoreCase("DM") || player.equalsIgnoreCase("Enemy"))
		{
			type = "Enemy";
		}
		else if (player.equals("-"))
		{
			type = "-";
		}

		theView.addPcgCombatant(pc, type);
		theView.refreshTable();
	}

	/**
	 * <p>
	 * Saves the combatants to a file
	 * </p>
	 */
	private void fileSave()
	{
		for (int i = 0; i < theView.initList.size(); i++)
		{
			InitHolder iH = theView.initList.get(i);

			if (iH instanceof PcgCombatant)
			{
				PcgCombatant pcgcbt = (PcgCombatant) iH;
				messageHandler.handleMessage(new RequestToSavePlayerCharacterMessage(this, pcgcbt.getPC()));
			}
		}

		theView.saveToFile();
	}

	/**
	 * <p>
	 * Handles save messages; delegates to fileSave();
	 * </p>
	 *
	 * @param message
	 */
	private void handleSaveMessage(FileMenuSaveMessage message)
	{
		if (isActive())
		{
			fileSave();
			message.consume();
		}
	}

	/**
	 * <p>
	 * Handles focus/tab focus events, enables and disables components in the GUI
	 * and refreshes the initiative table (and tabs, if auto refreshing is on).
	 * </p>
	 *
	 * @param message
	 */
	private void handleStateChangedMessage(FocusOrStateChangeOccurredMessage message)
	{
		if (isActive())
		{
			initToolsItem.setEnabled(false);
			if (GMGenSystem.inst != null)
			{
				GMGenSystem.inst.openFileItem.setEnabled(true);
				GMGenSystem.inst.saveFileItem.setEnabled(true);
			}
			theView.refreshTable();
			if (SettingsHandler.getGMGenOption(InitiativePlugin.LOG_NAME + ".refreshOnStateChange", true))
			{
				theView.refreshTabs();
			}
		}
		else
		{
			initToolsItem.setEnabled(true);
		}
	}

	/**
	 * Returns true if this plugin is active
	 * @return true if this plugin is active
	 */
	public boolean isActive()
	{
		JTabbedPane tp = Utility.getTabbedPaneFor(theView);
		return tp != null && JOptionPane.getFrameForComponent(tp).isFocused()
			&& tp.getSelectedComponent().equals(theView);
	}

	/**
	 * <p>
	 * Handles the initiative menu item by selecting the initiative tab.
	 * </p>
	 *
	 * @param evt
	 */
	private void initMenuItem(ActionEvent evt)
	{
		JTabbedPane tp = GMGenSystemView.getTabPane();

		for (int i = 0; i < tp.getTabCount(); i++)
		{
			if (tp.getComponentAt(i) instanceof Initiative)
			{
				tp.setSelectedIndex(i);
			}
		}
	}

	/**
	 * <p>
	 * Initializes the menus.
	 * </p>
	 */
	private void initMenus()
	{
		initToolsItem.setMnemonic('I');
		initToolsItem.setText("Initiative");
		initToolsItem.addActionListener(this::initMenuItem);
		messageHandler.handleMessage(new AddMenuItemToGMGenToolsMenuMessage(this, initToolsItem));
	}

	/**
	 * <p>
	 * Loads an initiative file
	 * </p>
	 *
	 * @param initFile
	 */
	private void loadINIT(File initFile)
	{
		theView.loadINIT(initFile, this);
	}

	/**
	 *  Gets the name of the data directory for Plugin object
	 *
	 *@return    The data directory name
	 */
	@Override
	public File getDataDirectory()
	{
		return new File(SettingsHandler.getGmgenPluginDir(), name);
	}
}
