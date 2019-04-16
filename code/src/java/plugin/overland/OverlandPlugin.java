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
 */
package plugin.overland;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.stream.IntStream;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;

import gmgen.GMGenSystemView;
import gmgen.pluginmgr.messages.AddMenuItemToGMGenToolsMenuMessage;
import gmgen.pluginmgr.messages.RequestAddTabToGMGenMessage;
import pcgen.core.SettingsHandler;
import pcgen.gui2.tools.Utility;
import pcgen.pluginmgr.InteractivePlugin;
import pcgen.pluginmgr.PCGenMessage;
import pcgen.pluginmgr.PCGenMessageHandler;
import pcgen.pluginmgr.messages.FocusOrStateChangeOccurredMessage;
import pcgen.system.LanguageBundle;
import plugin.overland.gui.OverPanel;

/**
 * The {@code Overland Plugin} provides a number
 * of useful utilities that help with overland travel.
 */
public class OverlandPlugin implements InteractivePlugin
{
	/** Log name / plugin id */
	public static final String LOG_NAME = "Overland_Travel"; //$NON-NLS-1$

	/** The plugin menu item in the tools menu. */
	private JMenuItem overToolsItem = new JMenuItem();

	/** The user interface that this class will be using. */
	private OverPanel theView;

	/** The English name of the plugin. */
	private static final String NAME = "Overland Travel"; //$NON-NLS-1$
	/** Key of plugin tab. */
	private static final String IN_NAME = "in_plugin_overland_name"; //$NON-NLS-1$
	/** Mnemonic in menu for {@link #IN_NAME} */
	private static final String IN_NAME_MN = "in_mn_plugin_overland_name"; //$NON-NLS-1$

	private PCGenMessageHandler messageHandler;

	/**
	 * Starts the plugin, registering itself with the {@code TabAddMessage}.
	 */
	@Override
	public void start(PCGenMessageHandler mh)
	{
		messageHandler = mh;
		File datadir = this.getDataDirectory();
		theView = new OverPanel(datadir);
		messageHandler.handleMessage(new RequestAddTabToGMGenMessage(this, getLocalizedName(), getView()));
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
		return SettingsHandler.getGMGenOption(LOG_NAME + ".LoadOrder", 90);
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
	 * Gets the view that this class is using.
	 * @return the view.
	 */
	public Component getView()
	{
		return theView;
	}

	/**
	 * listens to messages from the GMGen system, and handles them as needed
	 * @param message the source of the event from the system
	 */
	@Override
	public void handleMessage(PCGenMessage message)
	{
		if (message instanceof FocusOrStateChangeOccurredMessage)
		{
			if (isActive())
			{
				overToolsItem.setEnabled(false);
			}
			else
			{
				overToolsItem.setEnabled(true);
			}
		}
	}

	/**
	 * Returns true if the pane is active
	 * @return true if the pane is active
	 */
	public boolean isActive()
	{
		JTabbedPane tp = Utility.getTabbedPaneFor(theView);
		return tp != null && JOptionPane.getFrameForComponent(tp).isFocused()
			&& tp.getSelectedComponent().equals(theView);
	}

	/**
	 * Initialise the menus for this plugin
	 */
	private void initMenus()
	{
		overToolsItem.setMnemonic(LanguageBundle.getMnemonic(IN_NAME_MN));
		overToolsItem.setText(getLocalizedName());
		overToolsItem.addActionListener(this::toolMenuItem);
		messageHandler.handleMessage(new AddMenuItemToGMGenToolsMenuMessage(this, overToolsItem));
	}

	/**
	 * Sets the index for the pane 
	 * @param evt
	 */
	private void toolMenuItem(ActionEvent evt)
	{
		JTabbedPane tp = GMGenSystemView.getTabPane();

		IntStream.range(0, tp.getTabCount()).filter(i -> tp.getComponentAt(i) instanceof OverPanel)
			.forEach(tp::setSelectedIndex);
	}

	@Override
	public File getDataDirectory()
	{
		return new File(SettingsHandler.getGmgenPluginDir(), getPluginName());
	}
}
