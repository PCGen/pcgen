/*
 *  $Id$
 *
 *  plugin.dicebag - DESCRIPTION OF PACKAGE
 *  Copyright (C) 2003 RossLodge
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
 *  DiceBagPlugin.java
 *
 *  Created on Oct 17, 2003, 2:54:09 PM
 */
package plugin.dicebag;

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
import pcgen.system.LanguageBundle;
import plugin.dicebag.gui.DiceBagPluginController;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.event.ActionEvent;
import java.io.File;
import java.net.URL;

/**
 * @author RossLodge
 *
 * <p>
 * The base plugin class for the DiceBag plugin. This class handles mediation
 * between the GUI components of in <code>dicebag.gui</code> and the plugin
 * framework. This class should <b>not </b> pass framework events directly on to
 * the <code>dicebag.gui</code> classes, nor should those classes call the
 * framework directly.
 * </p>
 *
 */
public class DiceBagPlugin extends GMBPlugin
{

	/** Menu item command string for tools menu item */
	private static final String DICEBAG_TOOLS_COMMAND = "TOOLS_MENU_ENTRY"; //$NON-NLS-1$

	/** Name for logger.*/
	public static final String LOG_NAME = "DiceBag"; //$NON-NLS-1$

	/**
	 * The controller object. Should handle only interface between this object and
	 * the gui components/data.
	 */
	private DiceBagPluginController theController = null;

	/** Menu item for tools menu. Selects this tab. */
	private JMenuItem notesToolsItem;

	/** Key of dice bag tab. */
	private static final String IN_NAME = "in_plugin_dicebag_name"; //$NON-NLS-1$

	/** Version number. (NOTE: does this mean anything?) */
	private String version = "00.00.00.01";

	/**
	 * <p>
	 * Default (and only) constructure. Initializes the plugin.
	 * </p>
	 */
	public DiceBagPlugin()
	{
		// Do Nothing
	}

    @Override
	public FileFilter[] getFileTypes()
	{
		FileFilter[] ff = {getFileType()};

		return ff;
	}

	/**
	 * Get File type
	 * @return FileFilter
	 */
	public FileFilter getFileType()
	{
		String[] fileExt = new String[]{"dbg"}; //$NON-NLS-1$
		return new SimpleFileFilter(fileExt, LanguageBundle.getString("in_plugin_dicebag_filter")); //$NON-NLS-1$
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
		theController = new DiceBagPluginController();
		GMBus.send(new TabAddMessage(this, getName(), theController.getComponent(),
			getPluginSystem()));
		initMenus();
	}

    @Override
	public String getPluginSystem()
	{
		return SettingsHandler.getGMGenOption(LOG_NAME + ".System",
			Constants.SYSTEM_GMGEN);
	}

    @Override
	public int getPluginLoadOrder()
	{
		return SettingsHandler.getGMGenOption(LOG_NAME + ".LoadOrder", 20);
	}

	/*
	 * @see gmgen.pluginmgr.GMBPlugin#getName()
	 */
    @Override
	public String getName()
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

	/**
	 * <p>
	 * Listens to messages on the GMGen Bus. Handles the following:
	 * </p>
	 * <ul>
	 * <li>StateChangedMessage</li>
	 * <li>WindowClosedMessage</li>
	 * <li>FileOpenMessage</li>
	 * <li>SaveMessage</li>
	 * <li>LoadMessage</li>
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
		if (message instanceof StateChangedMessage)
		{
			handleStateChangedMessage((StateChangedMessage) message);
		}
		else if (message instanceof WindowClosedMessage)
		{
			handleWindowClosedMessage((WindowClosedMessage) message);
		}
		else if (message instanceof FileOpenMessage)
		{
			handleFileOpenMessage((FileOpenMessage) message);
		}
		else if (message instanceof SaveMessage)
		{
			handleSaveMessage((SaveMessage) message);
		}
		else if (message instanceof LoadMessage)
		{
			handleLoadMessage((LoadMessage) message);
		}
		else if (message instanceof OpenMessage)
		{
			handleOpenMessage((OpenMessage) message);
		}
		else if (message instanceof NewMessage)
		{
			handleNewMessage((NewMessage) message);
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
				theController.openFile(files[i]);
			}
		}
	}

	/**
	 * @param message
	 */
	private void handleNewMessage(NewMessage message)
	{
		if (isActive())
		{
			theController.fileNew();
			message.veto();
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
			if (tp.getComponentAt(i).equals(theController.getComponent()))
			{
				tp.setSelectedIndex(i);
			}
		}
	}

	/**
	 * <p>
	 * Delegates this message to the controller by calling the controller's
	 * <code>fileOpen()</code> method. We veto the message so it is not passed
	 * on to other plugins.
	 * </p>
	 *
	 * @param message
	 *          <code>FileOpenMessage</code>
	 */
	private void handleFileOpenMessage(FileOpenMessage message)
	{
		if (GMGenSystemView.getTabPane().getSelectedComponent().equals(
			theController.getComponent()))
		{
			theController.fileOpen();
			message.veto();
		}
	}

	/**
	 * <p>
	 * Handles a LoadMessage. We interpret this as a fileNew command, and pass the
	 * command on to the controller object. We also veto the message so that it is
	 * not passed on to other plugins.
	 * </p>
	 *
	 * @param message
	 */
	private void handleLoadMessage(LoadMessage message)
	{
		if (GMGenSystemView.getTabPane().getSelectedComponent().equals(
			theController.getComponent()))
		{
			theController.fileNew();
			message.veto();
		}
	}

	/**
	 * <p>
	 * Handles a SaveMessage. We pass this on to the controller, and veto the
	 * message so that it is not passed on to other plugins.
	 * </p>
	 *
	 * @param message
	 */
	private void handleSaveMessage(SaveMessage message)
	{
		if (isActive())
		{
			theController.fileSave();
			message.veto();
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
		if (GMGenSystemView.getTabPane() != null)
		{
			if (isActive())
			{
				notesToolsItem.setEnabled(false);
				GMGenSystem.inst.openFileItem.setEnabled(true);
				GMGenSystem.inst.saveFileItem.setEnabled(true);
				GMGenSystem.inst.newFileItem.setEnabled(true);
			}
			else
			{
				notesToolsItem.setEnabled(true);
			}
		}
	}

	/**
	 * <p>
	 * Calls the <code>windowClosed()</code> method of the controller.
	 * </p>
	 *
	 * @param message
	 */
	private void handleWindowClosedMessage(WindowClosedMessage message)
	{
		theController.windowClosed();
	}

	/**
	 * isActive
	 * @return TRUE if active
	 */
	public boolean isActive()
	{
		JTabbedPane tp =
				TabbedPaneUtilities.getTabbedPaneFor(theController
					.getComponent());
		return tp != null && JOptionPane.getFrameForComponent(tp).isFocused()
			&& tp.getSelectedComponent().equals(theController.getComponent());
	}

	/**
	 * <p>
	 * Adds DiceBag menu items to tools and menu.
	 * </p>
	 */
	private void initMenus()
	{
		notesToolsItem =
				makeMenuItem(LanguageBundle.getString(IN_NAME), DICEBAG_TOOLS_COMMAND, null,
					LanguageBundle.getString("in_plugin_dicebag_desc"), //$NON-NLS-1$
					LanguageBundle.getMnemonic("in_mn_plugin_dicebag_name")); //$NON-NLS-1$
		GMBus.send(new ToolMenuItemAddMessage(this, notesToolsItem));
	}

	/**
	 * <p>
	 * Creates a menu item by constructing an action based on the specified
	 * parameters.
	 * </p>
	 *
	 * @param text
	 *          Text to appear in label.
	 * @param key
	 *          Command string.
	 * @param iconPath
	 *          Path to icon resource; may be null. Resolved with
	 *          getClass().getResource()
	 * @param desc
	 *          Description (tool tip text)
	 * @param mnemonic
	 *          Mnemonic integer. Should be from KeyEvents
	 * @return A new JMenuItem
	 */
	private JMenuItem makeMenuItem(String text, String key, String iconPath,
		String desc, Integer mnemonic)
	{
		JMenuItem menuItem;

		Action action;
		URL imageURL = null;

		if ((iconPath != null) && (iconPath.length() > 0))
		{
			imageURL = getClass().getResource(iconPath);
		}

		if (imageURL != null)
		{
			action = new ActionDelegate(text, new ImageIcon(imageURL));
		}
		else
		{
			action = new ActionDelegate(text);
		}

		action.putValue(Action.SHORT_DESCRIPTION, desc);
		action.putValue(Action.MNEMONIC_KEY, mnemonic);
		action.putValue(Action.ACTION_COMMAND_KEY, key);

		menuItem = new JMenuItem(action);

		return menuItem;
	}

	/**
	 * @author Ross Lodge
	 *
	 * <p>
	 * Action class to handle all menu item actions.
	 * </p>
	 */
	private class ActionDelegate extends AbstractAction
	{

		/**
		 * @see javax.swing.AbstractAction#AbstractAction()
		 */
		public ActionDelegate()
		{
			super();
		}

		/**
		 * @see javax.swing.AbstractAction#AbstractAction(String)
		 */
		public ActionDelegate(String name)
		{
			super(name);
		}

		/**
		 * @see javax.swing.AbstractAction#AbstractAction(String, Icon)
		 */
		public ActionDelegate(String name, Icon icon)
		{
			super(name, icon);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
        @Override
		public void actionPerformed(ActionEvent e)
		{
			String command = e.getActionCommand();

			if (DICEBAG_TOOLS_COMMAND.equals(command))
			{
				toolMenuItem(e);
			}
		}
	}
}