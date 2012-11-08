/*
 *  plugin.charactersheet - DESCRIPTION OF PACKAGE
 *  Copyright (C) 2003 Devon Jones
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 *  The author of this program grants you the ability to use this code
 *  in conjunction with code that is covered under the Open Gaming License
 *
 *  CharacterSheetPlugin.java
 *
 *  Created on Oct 17, 2003, 2:54:09 PM
 */
package plugin.charactersheet;

import gmgen.GMGenSystemView;
import gmgen.pluginmgr.GMBMessage;
import gmgen.pluginmgr.GMBPlugin;
import gmgen.pluginmgr.GMBus;
import gmgen.pluginmgr.messages.*;
import pcgen.cdom.base.Constants;
import pcgen.core.SettingsHandler;
import pcgen.gui.utils.TabbedPaneUtilities;
import pcgen.system.LanguageBundle;
import plugin.charactersheet.gui.PreferencesDisplayPanel;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.filechooser.FileFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Devon Jones
 *
 * <p>
 * The base plugin class for the DiceBag plugin. This class handles mediation
 * between the GUI components of in <code>charactersheet.gui</code> and the
 * plugin framework. This class should <b>not </b> pass framework events
 * directly on to the <code>charactersheet.gui</code> classes, nor should
 * those classes call the framework directly.
 * </p>
 *
 */
public class CharacterSheetPlugin extends GMBPlugin
{

	/** Name for logger. Do not internationalize, used as beginning of options. */
	public static final String LOG_NAME = "CharacterSheet"; //$NON-NLS-1$

	private static final String OPTION_NAME_LOADORDER = LOG_NAME + ".LoadOrder"; //$NON-NLS-1$
	private static final String OPTION_NAME_SYSTEM = LOG_NAME + ".System"; //$NON-NLS-1$

	/** Menu item for tools menu. Selects this tab. */
	private JMenuItem csToolsItem = new JMenuItem();

	private static final String NAME = "Character Sheet"; //$NON-NLS-1$
	/** Name of tab. */
	private static final String IN_NAME = "in_plugin_charactersheet_name"; //$NON-NLS-1$

	/** Version number. (NOTE: does this mean anything?) */
	private String version = "00.00.00.01"; //$NON-NLS-1$

	private CharacterSheetModel model;

	/**
	 * <p>
	 * Default (and only) constructure. Initializes the plugin.
	 * </p>
	 */
	public CharacterSheetPlugin()
	{
		// Do Nothing
	}

	/**
	 * <p>
	 * Adds view panel via TabAddMessage and initializes the menu items.
	 * </p>
	 *
	 * @see gmgen.pluginmgr.GMBPlugin#start()
	 */
    @Override
	public void start()
	{
		model = new CharacterSheetModel();
		String name = getLocalizedName();
		GMBus.send(new PreferencesPanelAddMessage(this, name,
			new PreferencesDisplayPanel(model)));
		if (getPluginSystem().equals(Constants.SYSTEM_PCGEN))
		{
			GMBus.send(new TabAddMessage(this, name, model.getInfoPanel(),
				getPluginSystem()));
		}
		else
		{
			GMBus.send(new TabAddMessage(this, name, model.getComponent(),
				getPluginSystem()));
		}
		initMenus();
	}

    @Override
	public FileFilter[] getFileTypes()
	{
		return null;
	}

	/*
	 * @see gmgen.pluginmgr.GMBPlugin#getName()
	 */
    @Override
	public String getName()
	{
		return NAME;
	}

	private String getLocalizedName()
	{
		return LanguageBundle.getString(IN_NAME);
	}

	/*
	 * @see gmgen.pluginmgr.GMBPlugin#getVersion()
	 */
    @Override
	public String getVersion()
	{
		return version;
	}

    @Override
	public String getPluginSystem()
	{
		return SettingsHandler.getGMGenOption(OPTION_NAME_SYSTEM,
			Constants.SYSTEM_GMGEN);
	}

    @Override
	public int getPluginLoadOrder()
	{
		return SettingsHandler.getGMGenOption(OPTION_NAME_LOADORDER, 10);
	}

	/**
	 * <p>
	 * Listens to messages on the GMGen Bus. Handles the following:
	 * </p>
	 * <ul>
	 * <li>StateChangedMessage</li>
	 * <li>PCLoadedMessage</li>
	 * </ul>
	 * <p>
	 * Delegates all these messages to <code>theController</code>.
	 * </p>
	 * @param message
	 *
	 * @see gmgen.pluginmgr.GMBPlugin#handleMessage
	 */
    @Override
	public void handleMessage(GMBMessage message)
	{
		if (getPluginSystem().equals(Constants.SYSTEM_GMGEN))
		{
			if (message instanceof StateChangedMessage)
			{
				handleStateChangedMessage((StateChangedMessage) message);
			}
			else if (message instanceof PCLoadedMessage)
			{
				handlePCLoadedMessage((PCLoadedMessage) message);
			}
			else if (message instanceof PCClosedMessage)
			{
				handlePCClosedMessage((PCClosedMessage) message);
			}
			else if (message instanceof SavePCGNotificationMessage)
			{
				handleSavePCGNotificationMessage((SavePCGNotificationMessage) message);
			}
			else if (message instanceof WindowClosedMessage)
			{
				handleWindowClosedMessage((WindowClosedMessage) message);
			}
		}
		else if (message instanceof PauseRefreshMessage)
		{
			handlePauseRefreshMessage((PauseRefreshMessage) message);
		}
		else if (message instanceof ResumeRefreshMessage)
		{
			handleResumeRefreshMessage((ResumeRefreshMessage) message);
		}
	}

	/**
	 * <p>
	 * Selects the DiceBag component if in the tab list.
	 * </p>
	 *
	 * @param evt
	 *          ActionEvent that fired this method.
	 */
	public void toolMenuItem(ActionEvent evt)
	{
		JTabbedPane tp = GMGenSystemView.getTabPane();

		for (int i = 0; i < tp.getTabCount(); i++)
		{
			if (tp.getComponentAt(i).equals(model.getComponent()))
			{
				tp.setSelectedIndex(i);
			}
		}
	}

	/**
	 * <p>
	 * Handles a state changed message. Basically enables or disables applicable
	 * menu items, refreshes data.
	 * </p>
	 *
	 * @param message
	 *          The message
	 */
	private void handleStateChangedMessage(StateChangedMessage message)
	{
		if (isActive())
		{
			csToolsItem.setEnabled(false);
			model.refresh();
		}
		else
		{
			csToolsItem.setEnabled(true);
		}
	}

	/**
	 * Handle the pc loaded message
	 * @param message
	 */
	public void handlePCLoadedMessage(PCLoadedMessage message)
	{
		if (!message.isIgnored(this))
		{
			model.addPc(message.getPC());
		}
	}

	/**
	 * Handle the pc closed message
	 * @param message
	 */
	public void handlePCClosedMessage(PCClosedMessage message)
	{
		model.removePc(message.getPC());
	}

	/**
	 * Handle the save pcg notification message
	 * @param message
	 */
	public void handleSavePCGNotificationMessage(
		SavePCGNotificationMessage message)
	{
		model.savePc(message.getPC());
	}

	/**
	 * Handle the window closed message
	 * @param message
	 */
	public void handleWindowClosedMessage(WindowClosedMessage message)
	{
		model.closeWindow();
	}

	private void handlePauseRefreshMessage(PauseRefreshMessage message)
	{
		model.setRefresh(false);
	}

	private void handleResumeRefreshMessage(ResumeRefreshMessage message)
	{
		model.setRefresh(true);
	}

	/**
	 * is Active
	 * @return TRUE if active else FALSE
	 */
	public boolean isActive()
	{
		JTabbedPane tp =
				TabbedPaneUtilities.getTabbedPaneFor(model.getComponent());
		return tp != null && tp.getSelectedComponent() != null
			&& JOptionPane.getFrameForComponent(tp).isFocused()
			&& tp.getSelectedComponent().equals(model.getComponent());
	}

	/**
	 * <p>
	 * Adds menu items to tools and menu.
	 * </p>
	 */
	public void initMenus()
	{
		csToolsItem.setMnemonic(LanguageBundle.getMnemonic("in_mn_plugin_charactersheet_name")); //$NON-NLS-1$
		csToolsItem.setText(getLocalizedName());
		csToolsItem.addActionListener(new ActionListener()
		{

            @Override
			public void actionPerformed(ActionEvent evt)
			{
				toolMenuItem(evt);
			}
		});
		GMBus.send(new ToolMenuItemAddMessage(this, csToolsItem));
	}
}