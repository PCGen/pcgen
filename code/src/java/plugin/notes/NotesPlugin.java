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
import gmgen.pluginmgr.GMBMessage;
import gmgen.pluginmgr.GMBPlugin;
import gmgen.pluginmgr.GMBus;
import gmgen.pluginmgr.messages.*;
import pcgen.cdom.base.Constants;
import pcgen.core.SettingsHandler;
import pcgen.gui.utils.TabbedPaneUtilities;
import pcgen.io.PCGFile;
import pcgen.system.LanguageBundle;
import plugin.notes.gui.NotesView;
import plugin.notes.gui.PreferencesNotesPanel;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * The <code>NotesPlugin</code> controls the various classes that are involved
 * in the functionality of the Notes System. This <code>class
 * </code> is a
 * plugin for the <code>GMGenSystem</code>, is called by the
 * <code>PluginLoader</code> and will create a model and a view for this
 * plugin.
 *
 * @author Expires 2003
 * @since August 27, 2003
 * @version 2.10
 */
public class NotesPlugin extends GMBPlugin
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

	/** Key for the name of the plugin. */
	private final static String IN_NAME = "in_plugin_notes_name"; //$NON-NLS-1$

	/** The version number of the plugin. */
	private String version = "01.00.99.01.00"; //$NON-NLS-1$

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
	 * Starts the plugin, registering itself with the <code>TabAddMessage</code>.
	 */
	public void start()
	{
		String name = getName();
		GMBus.send(new PreferencesPanelAddMessage(this, name,
			new PreferencesNotesPanel()));
		theView = new NotesView(getDataDir(), this);
		GMBus.send(new TabAddMessage(this, name, getView(), getPluginSystem()));
		initMenus();
	}

	public String getPluginSystem()
	{
		return SettingsHandler.getGMGenOption(OPTION_NAME_SYSTEM,
			Constants.SYSTEM_GMGEN);
	}

	public int getPluginLoadOrder()
	{
		return SettingsHandler.getGMGenOption(OPTION_NAME_LOADORDER, 70);
	}

	/**
	 * Accessor for name
	 *
	 * @return The name value
	 */
	public String getName()
	{
		return LanguageBundle.getString(IN_NAME);
	}

	public boolean isRecognizedFileType(File launch)
	{
		return PCGFile.isPCGenCharacterOrPartyFile(launch);
	}

	/**
	 * Accessor for version
	 *
	 * @return The version value
	 */
	public String getVersion()
	{
		return version;
	}

	/**
	 * Gets the <code>JPanel</code> view for the notes plugin
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
	 * @param message
	 *          GMBus Message
	 * @see gmgen.pluginmgr.GMBPlugin#handleMessage(GMBMessage)
	 */
	public void handleMessage(GMBMessage message)
	{
		if (message instanceof StateChangedMessage)
		{
			handleStateChangedMessage((StateChangedMessage) message);
		}
		else if (message instanceof WindowClosedMessage)
		{
			handleWindowClosedMessage();
		}
		else if (message instanceof FileOpenMessage)
		{
			handleFileOpenMessage();
		}
		else if (message instanceof OpenMessage)
		{
			handleOpenMessage((OpenMessage) message);
		}
		else if (message instanceof FileTypeMessage)
		{
			handleFileTypeMessage((FileTypeMessage) message);
		}
	}

	/**
	 * @param message
	 */
	private void handleFileTypeMessage(FileTypeMessage message)
	{
		message.addFileTypes(getFileTypes());
	}

	/**
	 * @param message
	 */
	private void handleOpenMessage(OpenMessage message)
	{
		final File[] files = message.getFile();
		final FileFilter filter = getFileType();
		for (int i = 0; i < files.length; i++)
		{
			if (filter.accept(files[i]))
			{
				theView.openGMN(files[i]);
			}
		}
	}

	public void loadRecognizedFileType(File launch)
	{
		GMBus.send(new OpenPCGRequestMessage(this, launch, false));
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
	 * @param message
	 *          GMBus StateChangedMessage
	 */
	private void handleStateChangedMessage(StateChangedMessage message)
	{
		StateChangedMessage smessage = message;
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
		JTabbedPane tp = TabbedPaneUtilities.getTabbedPaneFor(theView);
		return tp != null && JOptionPane.getFrameForComponent(tp).isFocused()
			&& tp.getSelectedComponent().equals(theView);
	}

	/** Initializes the Menus on the menu bar */
	private void initMenus()
	{
		notesToolsItem.setMnemonic(LanguageBundle.getMnemonic("in_mn_plugin_notes_name")); //$NON-NLS-1$
		notesToolsItem.setText(getName());
		notesToolsItem.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent evt)
			{
				toolMenuItem(evt);
			}
		});
		GMBus.send(new ToolMenuItemAddMessage(this, notesToolsItem));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see gmgen.pluginmgr.Plugin#getDataDir()
	 */
	public String getDataDir()
	{
		return SettingsHandler.getGMGenOption(
			OPTION_NAME_DATADIR, super.getDataDir());
	}
}