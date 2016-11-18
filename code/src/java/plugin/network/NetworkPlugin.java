/*
 * Copyright 2003 (C) Devon Jones
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * $Id$
 */
 package plugin.network;

import gmgen.GMGenSystemView;
import gmgen.pluginmgr.messages.AddMenuItemToGMGenToolsMenuMessage;
import gmgen.pluginmgr.messages.CombatHasBeenInitiatedMessage;
import gmgen.pluginmgr.messages.CombatantHasBeenUpdatedMessage;
import gmgen.pluginmgr.messages.RequestAddPreferencesPanelMessage;
import gmgen.pluginmgr.messages.RequestAddTabToGMGenMessage;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;

import pcgen.core.SettingsHandler;
import pcgen.gui2.tools.Utility;
import pcgen.pluginmgr.InteractivePlugin;
import pcgen.pluginmgr.PCGenMessage;
import pcgen.pluginmgr.PCGenMessageHandler;
import pcgen.pluginmgr.messages.FocusOrStateChangeOccurredMessage;
import pcgen.system.LanguageBundle;
import plugin.network.gui.NetworkView;
import plugin.network.gui.PreferencesNetworkingPanel;

/**
 * The {@code ExperienceAdjusterController} handles the functionality of
 * the Adjusting of experience.  This class is called by the {@code GMGenSystem
 * } and will have it's own model and view.<br>
 * Created on February 26, 2003<br>
 * Updated on February 26, 2003
 * @author  Expires 2003
 */
public class NetworkPlugin implements InteractivePlugin
{
	public static final String LOG_NAME = "Network"; //$NON-NLS-1$

	/** The English name of the plugin. */
	private static final String NAME = "Network"; //$NON-NLS-1$
	/** Key of plugin tab. */
	private static final String IN_NAME = "in_plugin_network_name"; //$NON-NLS-1$
	/** Mnemonic in menu for {@link #IN_NAME} */
	private static final String IN_NAME_MN = "in_mn_plugin_network_name"; //$NON-NLS-1$

	private NetworkModel model;

	private JMenuItem netToolsItem = new JMenuItem();

	private PCGenMessageHandler messageHandler;

	/**
	 * Creates a new instance of NetworkPlugin
	 */
	public NetworkPlugin()
	{
		// Do Nothing
	}

	/**
	 * Starts the plugin, registering itself with the {@code TabAddMessage}.
	 */
    @Override
	public void start(PCGenMessageHandler mh)
	{
    	messageHandler = mh;
		model = new NetworkModel();
		messageHandler.handleMessage(new RequestAddTabToGMGenMessage(this, getLocalizedName(), model.getView()));
		initMenus();
		messageHandler.handleMessage(new RequestAddPreferencesPanelMessage(this, getLocalizedName(),
			new PreferencesNetworkingPanel(model)));
	}

	@Override
	public void stop()
	{
		messageHandler = null;
	}

    @Override
	public int getPriority()
	{
		return SettingsHandler.getGMGenOption(LOG_NAME + ".LoadOrder", 60);
	}

	/**
	 * Accessor for name
	 * @return name
	 */
    @Override
	public String getPluginName()
	{
		return NAME;
	}
	
	private String getLocalizedName()
	{
		return LanguageBundle.getString(IN_NAME);
	}

	/**
	 * listens to messages from the GMGen system, and handles them as needed
	 * @param message the source of the event from the system
	 */
    @Override
	public void handleMessage(PCGenMessage message)
	{
		if (message instanceof CombatHasBeenInitiatedMessage)
		{
			handleCombatRequestMessage((CombatHasBeenInitiatedMessage) message);
		}
		else if (message instanceof FocusOrStateChangeOccurredMessage)
		{
			handleStateChangedMessage((FocusOrStateChangeOccurredMessage) message);
		}
		else if (message instanceof CombatantHasBeenUpdatedMessage)
		{
			handleCombatantUpdatedMessage((CombatantHasBeenUpdatedMessage) message);
		}
	}

	private void handleStateChangedMessage(FocusOrStateChangeOccurredMessage message)
	{
		if (isActive())
		{
			netToolsItem.setEnabled(false);
			if (model.getCombat() == null)
			{
				messageHandler.handleMessage(new CombatHasBeenInitiatedMessage(this));
			}
			try
			{
				GMGenSystemView.getTabPane().setIconAt(
					GMGenSystemView.getTabPane().indexOfTab(getLocalizedName()), null);
			}
			catch (Exception e)
			{
				// TODO Handle this?
			}
			model.clearIcon();
			model.refresh();
		}
		else
		{
			netToolsItem.setEnabled(true);
		}
	}

	private void handleCombatRequestMessage(CombatHasBeenInitiatedMessage message)
	{
		if (message.getSource() == this)
		{
			model.setCombat(message.getCombat());
		}
		model.refresh();
	}

	private void handleCombatantUpdatedMessage(CombatantHasBeenUpdatedMessage message)
	{
		model.combatantUpdated(message.getCombatant());
	}

	public boolean isActive()
	{
		JTabbedPane tp = Utility.getTabbedPaneFor(model.getView());
		return tp != null && JOptionPane.getFrameForComponent(tp).isFocused()
			&& tp.getSelectedComponent().equals(model.getView());
	}

	public void toolMenuItem(ActionEvent evt)
	{
		JTabbedPane tp = GMGenSystemView.getTabPane();

		for (int i = 0; i < tp.getTabCount(); i++)
		{
			if (tp.getComponentAt(i) instanceof NetworkView)
			{
				tp.setSelectedIndex(i);
			}
		}
	}

	private void initMenus()
	{
		netToolsItem.setMnemonic(LanguageBundle.getMnemonic(IN_NAME_MN));
		netToolsItem.setText(getLocalizedName());
		netToolsItem.addActionListener(this::toolMenuItem);
		messageHandler.handleMessage(new AddMenuItemToGMGenToolsMenuMessage(this, netToolsItem));
	}

	/**
	 * Gets the name of the data directory for Plugin object
	 *
	 * @return    The data directory name
	 */
	public File getDataDirectory()
	{
		File dataDir =
				new File(SettingsHandler.getGmgenPluginDir(), getPluginName());
		return dataDir;
	}
}
