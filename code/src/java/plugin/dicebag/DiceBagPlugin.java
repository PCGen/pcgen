/*
 * Copyright 2003 (C) Ross M. Lodge
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
package plugin.dicebag;

import java.awt.event.ActionEvent;
import java.io.File;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
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
import plugin.dicebag.gui.DiceBagPluginController;

import gmgen.GMGenSystem;
import gmgen.GMGenSystemView;
import gmgen.pluginmgr.messages.AddMenuItemToGMGenToolsMenuMessage;
import gmgen.pluginmgr.messages.FileMenuNewMessage;
import gmgen.pluginmgr.messages.FileMenuOpenMessage;
import gmgen.pluginmgr.messages.FileMenuSaveMessage;
import gmgen.pluginmgr.messages.GMGenBeingClosedMessage;
import gmgen.pluginmgr.messages.RequestAddTabToGMGenMessage;

/**
 *
 * <p>
 * The base plugin class for the DiceBag plugin. This class handles mediation
 * between the GUI components of in {@code dicebag.gui} and the plugin
 * framework. This class should <b>not </b> pass framework events directly on to
 * the {@code dicebag.gui} classes, nor should those classes call the
 * framework directly.
 * </p>
 */
public class DiceBagPlugin implements InteractivePlugin
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

	private static final String NAME = "DiceBag"; //$NON-NLS-1$
	/** Key of dice bag tab. */
	private static final String IN_NAME = "in_plugin_dicebag_name"; //$NON-NLS-1$

	private PCGenMessageHandler messageHandler;

	/**
	 * <p>
	 * Adds view panel via TabAddMessage and initializes the menu items.
	 * </p>
	 */
    @Override
	public void start(PCGenMessageHandler mh)
	{
    	messageHandler = mh;
		theController = new DiceBagPluginController();
		messageHandler.handleMessage(new RequestAddTabToGMGenMessage(this, getLocalizedName(), theController.getComponent()));
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
		return SettingsHandler.getGMGenOption(DiceBagPlugin.LOG_NAME + ".LoadOrder", 20);
	}

    @Override
	public String getPluginName()
	{
		return DiceBagPlugin.NAME;
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
	 * Delegates all these messages to {@code theController}.
	 * </p>
	 * @param message
	 *
	 * @see pcgen.pluginmgr.PCGenMessageHandler#handleMessage
	 */
    @Override
	public void handleMessage(PCGenMessage message)
	{
		if (message instanceof FocusOrStateChangeOccurredMessage)
		{
			handleStateChangedMessage();
		}
		else if (message instanceof GMGenBeingClosedMessage)
		{
			handleWindowClosedMessage();
		}
		else if (message instanceof FileMenuOpenMessage)
		{
			handleFileOpenMessage((FileMenuOpenMessage) message);
		}
		else if (message instanceof FileMenuSaveMessage)
		{
			handleSaveMessage((FileMenuSaveMessage) message);
		}
		else if (message instanceof FileMenuNewMessage)
		{
			handleLoadMessage((FileMenuNewMessage) message);
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
	 * {@code fileOpen()} method. We veto the message so it is not passed
	 * on to other plugins.
	 * </p>
	 *
	 * @param message
	 *          {@code FileOpenMessage}
	 */
	private void handleFileOpenMessage(FileMenuOpenMessage message)
	{
		if (GMGenSystemView.getTabPane().getSelectedComponent().equals(
			theController.getComponent()))
		{
			theController.fileOpen();
			message.consume();
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
	private void handleLoadMessage(FileMenuNewMessage message)
	{
		if (GMGenSystemView.getTabPane().getSelectedComponent().equals(
			theController.getComponent()))
		{
			theController.fileNew();
			message.consume();
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
	private void handleSaveMessage(FileMenuSaveMessage message)
	{
		if (isActive())
		{
			theController.fileSave();
			message.consume();
		}
	}

	/**
	 * <p>
	 * Handles a state changed message. Basically enables or disables applicable
	 * menu items, refreshes data.
	 * </p>
	 */
	private void handleStateChangedMessage()
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
	 * Calls the {@code windowClosed()} method of the controller.
	 * </p>
	 */
	private void handleWindowClosedMessage()
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
				Utility.getTabbedPaneFor(theController
					.getComponent());
		return (tp != null) && JOptionPane.getFrameForComponent(tp).isFocused()
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
				makeMenuItem(getLocalizedName(), DiceBagPlugin.DICEBAG_TOOLS_COMMAND, null,
					LanguageBundle.getString("in_plugin_dicebag_desc"), //$NON-NLS-1$
					LanguageBundle.getMnemonic("in_mn_plugin_dicebag_name")); //$NON-NLS-1$
		messageHandler.handleMessage(new AddMenuItemToGMGenToolsMenuMessage(this, notesToolsItem));
	}

	private String getLocalizedName()
	{
		return LanguageBundle.getString(DiceBagPlugin.IN_NAME);
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

		URL imageURL = null;

		if ((iconPath != null) && (!iconPath.isEmpty()))
		{
			imageURL = getClass().getResource(iconPath);
		}

		Action action;
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

		return new JMenuItem(action);
	}

	/**
	 *
	 * <p>
	 * Action class to handle all menu item actions.
	 * </p>
	 */
	private final class ActionDelegate extends AbstractAction
	{

		/**
		 * @see javax.swing.AbstractAction#AbstractAction(String)
		 */
		private ActionDelegate(String name)
		{
			super(name);
		}

		/**
		 * @see javax.swing.AbstractAction#AbstractAction(String, Icon)
		 */
		private ActionDelegate(String name, Icon icon)
		{
			super(name, icon);
		}

        @Override
		public void actionPerformed(ActionEvent e)
		{
			String command = e.getActionCommand();

			if (DiceBagPlugin.DICEBAG_TOOLS_COMMAND.equals(command))
			{
				toolMenuItem(e);
			}
		}
	}

	/**
	 *  Gets the name of the data directory for Plugin object
	 *
	 *@return    The data directory name
	 */
	@Override
	public File getDataDirectory()
	{
		return new File(SettingsHandler.getGmgenPluginDir(), getPluginName());
	}
}
