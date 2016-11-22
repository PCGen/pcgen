/*
 *  NotesPlugin.java - plugin handler for the "Notes" plugin for GMGen
 *  Copyright (C) 2003 Devon Jones
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
package plugin.notes;

import gmgen.GMGenSystem;
import gmgen.GMGenSystemView;
import gmgen.io.SimpleFileFilter;
import gmgen.pluginmgr.messages.AddMenuItemToGMGenToolsMenuMessage;
import gmgen.pluginmgr.messages.FileMenuOpenMessage;
import gmgen.pluginmgr.messages.GMGenBeingClosedMessage;
import gmgen.pluginmgr.messages.RequestAddPreferencesPanelMessage;
import gmgen.pluginmgr.messages.RequestAddTabToGMGenMessage;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.filechooser.FileFilter;

import org.apache.commons.lang3.StringUtils;

import pcgen.core.SettingsHandler;
import pcgen.gui2.tools.Utility;
import pcgen.io.PCGFile;
import pcgen.pluginmgr.InteractivePlugin;
import pcgen.pluginmgr.PCGenMessage;
import pcgen.pluginmgr.PCGenMessageHandler;
import pcgen.pluginmgr.messages.FocusOrStateChangeOccurredMessage;
import pcgen.pluginmgr.messages.RequestOpenPlayerCharacterMessage;
import pcgen.system.LanguageBundle;
import plugin.notes.gui.NotesView;
import plugin.notes.gui.PreferencesNotesPanel;

/**
 * The {@code NotesPlugin} controls the various classes that are involved
 * in the functionality of the Notes System. This {@code class
 * } is a
 * plugin for the {@code GMGenSystem}, is called by the
 * {@code PluginLoader} and will create a model and a view for this
 * plugin.
 *
 * @author Devon Jones
 */
public class NotesPlugin implements InteractivePlugin
{

	public static final String EXTENSION_NOTES = "gmn"; //$NON-NLS-1$

	/** The Log Name for the Logging system */
	public static final String LOG_NAME = "Notes"; //$NON-NLS-1$

	private static final String OPTION_NAME_SYSTEM = LOG_NAME + ".System"; //$NON-NLS-1$
	private static final String OPTION_NAME_LOADORDER = LOG_NAME + ".LoadOrder"; //$NON-NLS-1$
	private static final String OPTION_NAME_DATADIR = LOG_NAME + ".DataDir"; //$NON-NLS-1$

	/** The plugin menu item in the tools menu. */
	private JMenuItem notesToolsItem = new JMenuItem();

	/** The user interface for the encounter generator. */
	private NotesView theView;

	/** The English name of the plugin. */
	private final static String NAME = "Notes"; //$NON-NLS-1$
	/** Key for the name of the plugin. */
	private final static String IN_NAME = "in_plugin_notes_name"; //$NON-NLS-1$

	/** The version number of the plugin. */
	private String version = "01.00.99.01.00"; //$NON-NLS-1$

	private PCGenMessageHandler messageHandler;

	/** Constructor for the NotesPlugin object */
	public NotesPlugin()
	{
		// Do Nothing
	}

	public static FileFilter getFileType()
	{
		String[] fileExt = new String[]{EXTENSION_NOTES};
		return new SimpleFileFilter(fileExt, LanguageBundle.getString("in_plugin_notes_file")); //$NON-NLS-1$
	}

	public FileFilter[] getFileTypes()
	{
		FileFilter[] ff = {getFileType()};

		return ff;
	}

	/**
	 * Starts the plugin, registering itself with the {@code TabAddMessage}.
	 */
    @Override
	public void start(PCGenMessageHandler mh)
	{
    	messageHandler = mh;
		String name = getPluginName();
		messageHandler.handleMessage(new RequestAddPreferencesPanelMessage(this, name,
			new PreferencesNotesPanel()));
		theView = new NotesView(getDataDirectory(), this);
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
		return SettingsHandler.getGMGenOption(OPTION_NAME_LOADORDER, 70);
	}

	/**
	 * Accessor for name
	 *
	 * @return The name value
	 */
    @Override
	public String getPluginName()
	{
		return NAME;
	}

	public String getLocalizedName()
	{
		return LanguageBundle.getString(IN_NAME);
	}

	public boolean isRecognizedFileType(File launch)
	{
		return PCGFile.isPCGenCharacterOrPartyFile(launch);
	}

	/**
	 * Gets the {@code JPanel} view for the notes plugin
	 *
	 * @return the view.
	 */
	public JPanel getView()
	{
		return theView;
	}

	/**
	 * listens to messages from the GMGen system, and handles them as needed
	 *
	 * @param message Message
	 */
    @Override
	public void handleMessage(PCGenMessage message)
	{
		if (message instanceof FocusOrStateChangeOccurredMessage)
		{
			handleStateChangedMessage((FocusOrStateChangeOccurredMessage) message);
		}
		else if (message instanceof GMGenBeingClosedMessage)
		{
			handleWindowClosedMessage();
		}
		else if (message instanceof FileMenuOpenMessage)
		{
			handleFileOpenMessage();
		}
	}

	public void loadRecognizedFileType(File launch)
	{
		messageHandler.handleMessage(new RequestOpenPlayerCharacterMessage(this, launch, false));
	}

	/**
	 * Changes to the notes plugin as the active tab
	 *
	 * @param evt
	 *          Action Event of a click on the tool menu item
	 */
	public void toolMenuItem(ActionEvent evt)
	{
		JTabbedPane tp = GMGenSystemView.getTabPane();

		for (int i = 0; i < tp.getTabCount(); i++)
		{
			if (tp.getComponentAt(i) instanceof NotesView)
			{
				tp.setSelectedIndex(i);
			}
		}
	}

	/**
	 * Handles the FileOpenMessage
	 *
	 */
	private void handleFileOpenMessage()
	{
		if (isActive())
		{
			theView.handleOpen();
		}
	}

	/**
	 * Handles the StateChangedMessage
	 *
	 * @param message StateChangedMessage
	 */
	private void handleStateChangedMessage(FocusOrStateChangeOccurredMessage message)
	{
		FocusOrStateChangeOccurredMessage smessage = message;
		if (isActive())
		{
			notesToolsItem.setEnabled(false);

			JMenu editMenu = smessage.getEditMenu();
			if (editMenu != null)
			{
				theView.initEditMenu(editMenu);
			}
			theView.refreshTree();
			try
			{
				GMGenSystem.inst.openFileItem.setEnabled(true);
			}
			catch (Exception e)
			{
				// TODO Handle this?
			}
		}
		else
		{
			notesToolsItem.setEnabled(true);
			theView.refreshTree();
		}
	}

	/**
	 * Handles the WindowClosedMessage
	 *
	 */
	private void handleWindowClosedMessage()
	{
		theView.windowClosed();
	}

	public boolean isActive()
	{
		JTabbedPane tp = Utility.getTabbedPaneFor(theView);
		return tp != null && JOptionPane.getFrameForComponent(tp).isFocused()
			&& tp.getSelectedComponent().equals(theView);
	}

	/** Initializes the Menus on the menu bar */
	private void initMenus()
	{
		notesToolsItem.setMnemonic(LanguageBundle.getMnemonic("in_mn_plugin_notes_name")); //$NON-NLS-1$
		notesToolsItem.setText(getLocalizedName());
		notesToolsItem.addActionListener(this::toolMenuItem);
		messageHandler.handleMessage(new AddMenuItemToGMGenToolsMenuMessage(this, notesToolsItem));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see gmgen.pluginmgr.Plugin#getDataDir()
	 */
    @Override
	public File getDataDirectory()
	{
    	String notesDataDir = SettingsHandler.getGMGenOption(
			OPTION_NAME_DATADIR, "");
    	if (StringUtils.isEmpty(notesDataDir))
    	{
    		return defaultDataDir();
    	}
		return new File(notesDataDir);
	}

	public File defaultDataDir()
	{
		File dataDir =
				new File(SettingsHandler.getGmgenPluginDir(), getPluginName());
		return dataDir;
	}
}
