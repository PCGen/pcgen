package plugin.pcgtracker;

import gmgen.GMGenSystem;
import gmgen.GMGenSystemView;
import gmgen.io.SimpleFileFilter;
import gmgen.pluginmgr.GMBMessage;
import gmgen.pluginmgr.GMBPlugin;
import gmgen.pluginmgr.GMBus;
import gmgen.pluginmgr.messages.*;
import gmgen.util.MiscUtilities;
import pcgen.cdom.base.Constants;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.gui.utils.TabbedPaneUtilities;
import pcgen.gui.ImagePreview;
import pcgen.io.PCGIOHandler;
import pcgen.io.PCGFile;
import pcgen.system.LanguageBundle;
import pcgen.system.PCGenSettings;
import pcgen.util.Logging;
import plugin.pcgtracker.gui.PCGTrackerView;

import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.filechooser.FileFilter;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

/**
 * The <code>ExperienceAdjusterController</code> handles the functionality of
 * the Adjusting of experience.  This class is called by the <code>GMGenSystem
 * </code> and will have it's own model and view.<br>
 * Created on February 26, 2003<br>
 * Updated on February 26, 2003
 * @author  Expires 2003
 * @version 2.10
 */
public class PCGTrackerPlugin extends GMBPlugin implements
		java.awt.event.ActionListener
{
	public static final String LOG_NAME = "PCG_Tracker";

	private static final String OPTION_NAME_SYSTEM = LOG_NAME + ".System"; //$NON-NLS-1$
	private static final String OPTION_NAME_LOADORDER = LOG_NAME + ".LoadOrder"; //$NON-NLS-1$

	private static final String FILENAME_PCP = "pcp"; //$NON-NLS-1$
	private static final String FILENAME_PCG = "pcg"; //$NON-NLS-1$

	/** The plugin menu item in the tools menu. */
	private JMenuItem charToolsItem = new JMenuItem();
	private PCGTrackerModel model = new PCGTrackerModel();
	private PCGTrackerView theView;

	/** The English name of the plugin. */
	// TODO add static final?
	private String name = "Character Tracker";

	/** The version number of the plugin. */
	// TODO add static final?
	private String version = "01.00.99.01.00";

	/**
	 * Creates a new instance of PCGTrackerPlugin
	 */
	public PCGTrackerPlugin()
	{
		// Do Nothing
	}

	public FileFilter[] getFileTypes()
	{
		return null;
	}

	/**
	 * Starts the plugin, registering itself with the <code>TabAddMessage</code>.
	 */
	public void start()
	{
		theView = new PCGTrackerView();
		theView.getLoadedList().setModel(model);
		initListeners();
		GMBus.send(new TabAddMessage(this, name, getView(), getPluginSystem()));
		initMenus();
		getPluginSystem();
	}

	public String getPluginSystem()
	{
		return SettingsHandler.getGMGenOption(OPTION_NAME_SYSTEM,
			Constants.SYSTEM_GMGEN);
	}

	public int getPluginLoadOrder()
	{
		return SettingsHandler.getGMGenOption(OPTION_NAME_LOADORDER, 1000);
	}

	/**
	 * Accessor for name
	 * @return name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Accessor for version
	 * @return version
	 */
	public String getVersion()
	{
		return version;
	}

	/**
	 * Gets the view that this class is using.
	 * @return the view.
	 */
	public Component getView()
	{
		return theView;
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == theView.getRemoveButton())
		{
			removeSelected();
		}

		if (e.getSource() == theView.getSaveButton())
		{
			for (Object obj : theView.getLoadedList().getSelectedValues())
			{
				PlayerCharacter pc = model.get(obj);
				savePC(pc, false);
			}
		}

		if (e.getSource() == theView.getSaveAsButton())
		{
			for (Object obj : theView.getLoadedList().getSelectedValues())
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
		if (model.size() > 0)
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
	 * @see gmgen.pluginmgr.GMBPlugin#handleMessage(GMBMessage)
	 */
	public void handleMessage(GMBMessage message)
	{
		if (message instanceof FileOpenMessage)
		{
			if (isActive())
			{
				handleOpen();
			}
		}
		else if (message instanceof PCLoadedMessage)
		{
			PCLoadedMessage cmessage = (PCLoadedMessage) message;

			if (!cmessage.isIgnored(this))
			{
				model.add(cmessage.getPC());
			}
		}
		else if (message instanceof StateChangedMessage)
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
		else if (message instanceof WindowClosedMessage)
		{
			handleClose();
		}
		/*else if (message instanceof SavePCGRequestMessage)
		 {
		 SavePCGRequestMessage smessage = (SavePCGRequestMessage) message;
		 savePC(smessage.getPC(), false);
		 }*/
		else if (message instanceof PCClosedMessage)
		{
			PCClosedMessage cmessage = (PCClosedMessage) message;
			for (Object obj : theView.getLoadedList().getSelectedValues())
			{
				PlayerCharacter pc = model.get(obj);
				if (pc == cmessage.getPC())
				{
					model.removeElement(obj);
				}
			}
		}
	}

	public boolean isActive()
	{
		JTabbedPane tp = TabbedPaneUtilities.getTabbedPaneFor(theView);
		return tp != null && JOptionPane.getFrameForComponent(tp).isFocused()
			&& tp.getSelectedComponent().equals(theView);
	}

	/**
	 * Handles the clicking of the <b>Add</b> button on the GUI.
	 */
	public void handleOpen()
	{
		File defaultFile = new File(PCGenSettings.getPcgDir());
		JFileChooser chooser =
				ImagePreview.decorateWithImagePreview(new JFileChooser());
		chooser.setCurrentDirectory(defaultFile);

		String[] pcgs = new String[]{FILENAME_PCG, FILENAME_PCP};
		SimpleFileFilter ff = new SimpleFileFilter(pcgs, LanguageBundle.getString("in_pcgen_file")); //$NON-NLS-1$
		chooser.addChoosableFileFilter(ff);
		chooser.setFileFilter(ff);
		chooser.setMultiSelectionEnabled(true);

		java.awt.Cursor saveCursor =
				MiscUtilities.setBusyCursor(GMGenSystem.inst);
		int option = chooser.showOpenDialog(GMGenSystem.inst);

		if (option == JFileChooser.APPROVE_OPTION)
		{
			for (File selectedFile : chooser.getSelectedFiles())
			{
				if (PCGFile.isPCGenCharacterOrPartyFile(selectedFile))
				{
					GMBus.send(new OpenPCGRequestMessage(this, selectedFile,
						false));
				}
			}
		}
		else
		{
			/* this means the file is invalid */
		}

		MiscUtilities.setCursor(GMGenSystem.inst, saveCursor);
	}

	/**
	 * Registers all the listeners for any actions.
	 */
	public void initListeners()
	{
		theView.getRemoveButton().addActionListener(this);
		theView.getSaveButton().addActionListener(this);
		theView.getSaveAsButton().addActionListener(this);
		theView.getLoadButton().addActionListener(this);
	}

	public void removeSelected()
	{
		for (Object obj : theView.getLoadedList().getSelectedValues())
		{
			PlayerCharacter pc = model.get(obj);
			model.removeElement(obj);
			GMBus.send(new PCClosedMessage(this, pc));
		}
	}

	/**
	 * Checks whether a character can be saved, and if so, calls
	 * it's <code>save</code> method.
	 *
	 * @param aPC The PlayerCharacter to save
	 * @param saveas boolean if <code>true</code>, ask for file name
	 *
	 * @return <code>true</code> if saved; <code>false</code> if save as cancelled
	 */
	// TODO use pcgen save methods rather than implementing it again
	public boolean savePC(PlayerCharacter aPC, boolean saveas)
	{
		boolean newPC = false;
		File prevFile;
		File file = null;
		String aPCFileName = aPC.getFileName();

		if (aPCFileName.isEmpty())
		{
			prevFile =
					new File(PCGenSettings.getPcgDir(), aPC.getDisplayName()
						+ Constants.EXTENSION_CHARACTER_FILE);
			aPCFileName = prevFile.getAbsolutePath();
			newPC = true;
		}
		else
		{
			prevFile = new File(aPCFileName);
		}

		if (saveas || newPC)
		{
			JFileChooser fc =
					ImagePreview.decorateWithImagePreview(new JFileChooser());
			String[] pcgs = new String[]{FILENAME_PCG};
			SimpleFileFilter ff = new SimpleFileFilter(pcgs, LanguageBundle.getString("in_pcgen_file_char")); //$NON-NLS-1$
			fc.setFileFilter(ff);
			fc.setSelectedFile(prevFile);

			FilenameChangeListener listener =
					new FilenameChangeListener(aPCFileName, fc);

			fc.addPropertyChangeListener(listener);

			int returnVal = fc.showSaveDialog(GMGenSystem.inst);
			fc.removePropertyChangeListener(listener);

			if (returnVal == JFileChooser.APPROVE_OPTION)
			{
				file = fc.getSelectedFile();

				if (!PCGFile.isPCGenCharacterFile(file))
				{
					file =
							new File(file.getParent(), file.getName()
								+ Constants.EXTENSION_CHARACTER_FILE);
				}

				if (file.isDirectory())
				{
					JOptionPane.showMessageDialog(null,
						LanguageBundle.getString("in_savePcDirOverwrite"), //$NON-NLS-1$
						Constants.APPLICATION_NAME, JOptionPane.ERROR_MESSAGE);

					return false;
				}

				if (file.exists()
					&& (newPC || !file.getName().equals(prevFile.getName())))
				{
					int reallyClose =
							JOptionPane
								.showConfirmDialog(
									GMGenSystem.inst,
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
			(new PCGIOHandler()).write(aPC, file.getAbsolutePath());
		}
		catch (Exception ex)
		{
			String formattedString = LanguageBundle.getFormattedString("in_saveFailMsg", aPC.getDisplayName()); //$NON-NLS-1$
			JOptionPane.showMessageDialog(null, formattedString, Constants.APPLICATION_NAME,
				JOptionPane.ERROR_MESSAGE);
			Logging.errorPrint(formattedString);
			Logging.errorPrint(ex.getMessage(), ex);

			return false;
		}

		return true;
	}

	public void toolMenuItem(ActionEvent evt)
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
		charToolsItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				toolMenuItem(evt);
			}
		});
		GMBus.send(new ToolMenuItemAddMessage(this, charToolsItem));
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
}
