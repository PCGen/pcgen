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
package plugin.pcgtracker;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import gmgen.GMGenSystem;
import gmgen.GMGenSystemView;
import gmgen.gui.ImagePreview;
import gmgen.pluginmgr.messages.AddMenuItemToGMGenToolsMenuMessage;
import gmgen.pluginmgr.messages.FileMenuOpenMessage;
import gmgen.pluginmgr.messages.GMGenBeingClosedMessage;
import gmgen.pluginmgr.messages.RequestAddTabToGMGenMessage;
import pcgen.cdom.base.Constants;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.gui2.tools.Utility;
import pcgen.io.PCGFile;
import pcgen.io.PCGIOHandler;
import pcgen.pluginmgr.InteractivePlugin;
import pcgen.pluginmgr.PCGenMessage;
import pcgen.pluginmgr.PCGenMessageHandler;
import pcgen.pluginmgr.messages.FocusOrStateChangeOccurredMessage;
import pcgen.pluginmgr.messages.PlayerCharacterWasClosedMessage;
import pcgen.pluginmgr.messages.PlayerCharacterWasLoadedMessage;
import pcgen.pluginmgr.messages.RequestOpenPlayerCharacterMessage;
import pcgen.system.LanguageBundle;
import pcgen.system.PCGenSettings;
import pcgen.util.FileHelper;
import pcgen.util.Logging;
import plugin.pcgtracker.gui.PCGTrackerView;

/**
 * The {@code ExperienceAdjusterController} handles the functionality of
 * the Adjusting of experience.  This class is called by the {@code GMGenSystem}
 * and will have it's own model and view.
 */
public class PCGTrackerPlugin implements InteractivePlugin, java.awt.event.ActionListener
{
	public static final String LOG_NAME = "PCG_Tracker"; //$NON-NLS-1$

	private static final String OPTION_NAME_SYSTEM = LOG_NAME + ".System"; //$NON-NLS-1$
	private static final String OPTION_NAME_LOADORDER = LOG_NAME + ".LoadOrder"; //$NON-NLS-1$

	private static final String FILENAME_PCP = "pcp"; //$NON-NLS-1$
	private static final String FILENAME_PCG = "pcg"; //$NON-NLS-1$

	/** The plugin menu item in the tools menu. */
	private JMenuItem charToolsItem = new JMenuItem();
	private PCGTrackerModel model = new PCGTrackerModel();
	private PCGTrackerView theView;

	/** The English name of the plugin. */
	private static final String NAME = "Character Tracker"; //$NON-NLS-1$
	/** Key of plugin tab. */
	private static final String IN_NAME = "in_plugin_pcgtracker_name"; //$NON-NLS-1$

	private PCGenMessageHandler messageHandler;

	/**
	 * Starts the plugin, registering itself with the {@code TabAddMessage}.
	 */
	@Override
	public void start(PCGenMessageHandler mh)
	{
		messageHandler = mh;
		theView = new PCGTrackerView();
		theView.getLoadedList().setModel(model);
		initListeners();
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
		return SettingsHandler.getGMGenOption(OPTION_NAME_LOADORDER, 1000);
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

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == theView.getRemoveButton())
		{
			removeSelected();
		}

		if (e.getSource() == theView.getSaveButton())
		{
			for (Object obj : theView.getLoadedList().getSelectedValuesList())
			{
				PlayerCharacter pc = model.get(obj);
				savePC(pc, false);
			}
		}

		if (e.getSource() == theView.getSaveAsButton())
		{
			for (Object obj : theView.getLoadedList().getSelectedValuesList())
			{
				PlayerCharacter pc = model.get(obj);
				savePC(pc, true);
			}
		}

		if (e.getSource() == theView.getLoadButton())
		{
			handleOpen();
		}

		theView.getLoadedList().repaint();
	}

	public void handleClose()
	{
		/*
		 * TODO This method seems like a "dead" chain of events - the PCs are
		 * fetched, but nothing happens. As best I can tell, none of these
		 * methods have side effects (that is good), but that means this method
		 * does nothing. - thpr 10/26/06
		 */
		if (!model.isEmpty())
		{
			GMGenSystemView.getTabPane().setSelectedComponent(theView);
		}

		for (int i = 0; i < model.size(); i++)
		{
			model.get(i);
		}
	}

	/**
	 * listens to messages from the GMGen system, and handles them as needed
	 * @param message the source of the event from the system
	 */
	@Override
	public void handleMessage(PCGenMessage message)
	{
		if (message instanceof FileMenuOpenMessage)
		{
			if (isActive())
			{
				handleOpen();
			}
		}
		else if (message instanceof PlayerCharacterWasLoadedMessage)
		{
			PlayerCharacterWasLoadedMessage cmessage = (PlayerCharacterWasLoadedMessage) message;
			model.add(cmessage.getPc());
		}
		else if (message instanceof FocusOrStateChangeOccurredMessage)
		{
			if (isActive())
			{
				charToolsItem.setEnabled(false);

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
				charToolsItem.setEnabled(true);
			}
		}
		else if (message instanceof GMGenBeingClosedMessage)
		{
			handleClose();
		}
		/*else if (message instanceof SavePCGRequestMessage)
		 {
		 SavePCGRequestMessage smessage = (SavePCGRequestMessage) message;
		 savePC(smessage.getPC(), false);
		 }*/
		else if (message instanceof PlayerCharacterWasClosedMessage)
		{
			PlayerCharacterWasClosedMessage cmessage = (PlayerCharacterWasClosedMessage) message;
			model.remove(cmessage.getPC());
		}
	}

	public boolean isActive()
	{
		JTabbedPane tp = Utility.getTabbedPaneFor(theView);
		return tp != null && JOptionPane.getFrameForComponent(tp).isFocused()
			&& tp.getSelectedComponent().equals(theView);
	}

	/**
	 * Handles the clicking of the <b>Add</b> button on the GUI.
	 */
	public void handleOpen()
	{
		File defaultFile = new File(PCGenSettings.getPcgDir());
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(defaultFile);

		String[] pcgs = {FILENAME_PCG, FILENAME_PCP};
		FileFilter ff = new FileNameExtensionFilter(LanguageBundle.getString("in_pcgen_file"),

			pcgs);
		chooser.addChoosableFileFilter(ff);
		chooser.setFileFilter(ff);
		chooser.setMultiSelectionEnabled(true);
		Component component = GMGenSystem.inst;
		Cursor originalCursor = component.getCursor();
		component.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

		int option = chooser.showOpenDialog(GMGenSystem.inst);

		if (option == JFileChooser.APPROVE_OPTION)
		{
			for (File selectedFile : chooser.getSelectedFiles())
			{
				if (PCGFile.isPCGenCharacterOrPartyFile(selectedFile))
				{
					messageHandler.handleMessage(new RequestOpenPlayerCharacterMessage(this, selectedFile, false));
				}
			}
		}
		else
		{
			/* this means the file is invalid */
		}

		GMGenSystem.inst.setCursor(originalCursor);
	}

	/**
	 * Registers all the listeners for any actions.
	 */
	private void initListeners()
	{
		theView.getRemoveButton().addActionListener(this);
		theView.getSaveButton().addActionListener(this);
		theView.getSaveAsButton().addActionListener(this);
		theView.getLoadButton().addActionListener(this);
	}

	private void removeSelected()
	{
		for (Object obj : theView.getLoadedList().getSelectedValuesList())
		{
			PlayerCharacter pc = model.get(obj);
			model.removeElement(obj);
			messageHandler.handleMessage(new PlayerCharacterWasClosedMessage(this, pc));
		}
	}

	/**
	 * Checks whether a character can be saved, and if so, calls
	 * it's {@code save} method.
	 *
	 * @param aPC The PlayerCharacter to save
	 * @param saveas boolean if {@code true}, ask for file name
	 *
	 * @return {@code true} if saved; {@code false} if save as cancelled
	 */
	// TODO use pcgen save methods rather than implementing it again
	private boolean savePC(PlayerCharacter aPC, boolean saveas)
	{
		boolean newPC = false;
		File prevFile;
		File file = null;
		String aPCFileName = aPC.getFileName();

		if (aPCFileName.isEmpty())
		{
			String characterName = FileHelper.sanitizeFilename(aPC.getDisplay().getDisplayName());
			prevFile = new File(PCGenSettings.getPcgDir(),
				characterName + Constants.EXTENSION_CHARACTER_FILE);
			aPCFileName = prevFile.getAbsolutePath();
			newPC = true;
		}
		else
		{
			prevFile = new File(aPCFileName);
		}

		if (saveas || newPC)
		{
			JFileChooser fc = ImagePreview.decorateWithImagePreview(new JFileChooser());
			String[] pcgs = {FILENAME_PCG};
			FileFilter ff = new FileNameExtensionFilter(LanguageBundle.getString("in_pcgen_file_char"), pcgs);
			fc.setFileFilter(ff);
			fc.setSelectedFile(prevFile);

			PropertyChangeListener listener = new FilenameChangeListener(aPCFileName, fc);

			fc.addPropertyChangeListener(listener);

			int returnVal = fc.showSaveDialog(GMGenSystem.inst);
			fc.removePropertyChangeListener(listener);

			if (returnVal == JFileChooser.APPROVE_OPTION)
			{
				file = fc.getSelectedFile();

				if (!PCGFile.isPCGenCharacterFile(file))
				{
					file = new File(file.getParent(), file.getName() + Constants.EXTENSION_CHARACTER_FILE);
				}

				if (file.isDirectory())
				{
					JOptionPane.showMessageDialog(null, LanguageBundle.getString("in_savePcDirOverwrite"), //$NON-NLS-1$
						Constants.APPLICATION_NAME, JOptionPane.ERROR_MESSAGE);

					return false;
				}

				if (file.exists() && (newPC || !file.getName().equals(prevFile.getName())))
				{
					int reallyClose = JOptionPane.showConfirmDialog(GMGenSystem.inst,
						LanguageBundle.getFormattedString("in_savePcConfirmOverMsg", //$NON-NLS-1$
							file.getName()),
						LanguageBundle.getFormattedString("in_savePcConfirmOverTitle", file.getName()), //$NON-NLS-1$
						JOptionPane.YES_NO_OPTION);

					if (reallyClose != JOptionPane.YES_OPTION)
					{
						return false;
					}
				}

				aPC.setFileName(file.getAbsolutePath());
			}
			else
			{ // not saving

				return false;
			}
		}

		else
		{ // simple save
			file = prevFile;
		}

		try
		{
			(new PCGIOHandler()).write(aPC, null, null, file);
		}
		catch (Exception ex)
		{
			String formattedString =
					LanguageBundle.getFormattedString(
						"in_saveFailMsg", aPC.getDisplay().getDisplayName()); //$NON-NLS-1$
			JOptionPane.showMessageDialog(
				null, formattedString, Constants.APPLICATION_NAME, JOptionPane.ERROR_MESSAGE);
			Logging.errorPrint(formattedString);
			Logging.errorPrint(ex.getMessage(), ex);

			return false;
		}

		return true;
	}

	private static void toolMenuItem(ActionEvent evt)
	{
		JTabbedPane tp = GMGenSystemView.getTabPane();

		for (int i = 0; i < tp.getTabCount(); i++)
		{
			if (tp.getComponentAt(i) instanceof PCGTrackerView)
			{
				tp.setSelectedIndex(i);
			}
		}
	}

	private void initMenus()
	{
		charToolsItem.setMnemonic(LanguageBundle.getMnemonic("in_mn_plugin_pcgtracker_name")); //$NON-NLS-1$
		charToolsItem.setText(LanguageBundle.getString("in_plugin_pcgtracker_name")); //$NON-NLS-1$
		charToolsItem.addActionListener(PCGTrackerPlugin::toolMenuItem);
		messageHandler.handleMessage(new AddMenuItemToGMGenToolsMenuMessage(this, charToolsItem));
	}

	/**
	 * Property change listener for the event "selected file
	 * changed".  Ensures that the filename doesn't get changed
	 * when a directory is selected.
	 *
	 * @author Dmitry Jemerov &lt;yole@spb.cityline.ru&gt;
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

		@Override
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
			fileChooser.setSelectedFile(new File(fileChooser.getCurrentDirectory(), lastSelName));
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
	 *  Gets the name of the data directory for Plugin object
	 *
	 *@return    The data directory name
	 */
	@Override
	public File getDataDirectory()
	{
		return new File(SettingsHandler.getGmgenPluginDir(), NAME);
	}

}
