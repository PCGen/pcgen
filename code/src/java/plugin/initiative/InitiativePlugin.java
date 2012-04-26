package plugin.initiative;

import gmgen.GMGenSystem;
import gmgen.GMGenSystemView;
import gmgen.io.SimpleFileFilter;
import gmgen.plugin.InitHolder;
import gmgen.plugin.InitHolderList;
import gmgen.plugin.PcgCombatant;
import gmgen.pluginmgr.GMBMessage;
import gmgen.pluginmgr.GMBPlugin;
import gmgen.pluginmgr.GMBus;
import gmgen.pluginmgr.messages.*;
import gmgen.util.LogUtilities;
import gmgen.util.MiscUtilities;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.filechooser.FileFilter;

import pcgen.cdom.base.Constants;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.gui.ImagePreview;
import pcgen.gui.utils.TabbedPaneUtilities;
import pcgen.io.PCGFile;
import pcgen.system.PCGenSettings;
import plugin.initiative.gui.Initiative;
import plugin.initiative.gui.PreferencesDamagePanel;
import plugin.initiative.gui.PreferencesInitiativePanel;
import plugin.initiative.gui.PreferencesMassiveDamagePanel;
import plugin.initiative.gui.PreferencesPerformancePanel;

/**
 * The <code>ExperienceAdjusterController</code> handles the functionality of
 * the Adjusting of experience. This class is called by the <code>GMGenSystem
 * </code>
 * and will have it's own model and view. <br>
 * Created on February 26, 2003 <br>
 * Updated on February 26, 2003
 *
 * @author Expires 2003
 * @version 2.10
 */
public class InitiativePlugin extends GMBPlugin
{

	/** Name used for initiative logging. */
	public static final String LOG_NAME = "Initiative";

	/** The user interface that this class will be using. */
	private Initiative theView;

	/** The plugin menu item in the tools menu. */
	private JMenuItem initToolsItem = new JMenuItem();

	/** The English name of the plugin. */
	private String name = "Initiative";

	/** The version number of the plugin. */
	private String version = "01.00.99.01.00";

	/**
	 * Creates a new instance of InitiativePlugin
	 */
	public InitiativePlugin()
	{
		// Do Nothing
	}

	public FileFilter[] getFileTypes()
	{
		FileFilter[] ff = {getFileType()};

		return ff;
	}

	/**
	 * Get the file type
	 * @return the file type
	 */
	public FileFilter getFileType()
	{
		String[] init = new String[]{"gmi", "init"};
		return new SimpleFileFilter(init, "Initiative Export");
	}

	/**
	 * Starts the plugin, registering itself with the <code>TabAddMessage</code>.
	 */
	public void start()
	{
		theView = new Initiative();
		GMBus.send(new PreferencesPanelAddMessage(this, name,
			new PreferencesDamagePanel()));
		GMBus.send(new PreferencesPanelAddMessage(this, name,
			new PreferencesMassiveDamagePanel()));
		GMBus.send(new PreferencesPanelAddMessage(this, name,
			new PreferencesInitiativePanel()));
		GMBus.send(new PreferencesPanelAddMessage(this, name,
			new PreferencesPerformancePanel()));

		theView.setLog(LogUtilities.inst());
		GMBus.send(new TabAddMessage(this, name, getView(), getPluginSystem()));
		initMenus();
	}

	public String getPluginSystem()
	{
		return SettingsHandler.getGMGenOption(LOG_NAME + ".System",
			Constants.SYSTEM_GMGEN);
	}

	public int getPluginLoadOrder()
	{
		return SettingsHandler.getGMGenOption(LOG_NAME + ".LoadOrder", 40);
	}

	/**
	 * Accessor for name
	 *
	 * @return name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Accessor for version
	 *
	 * @return version
	 */
	public String getVersion()
	{
		return version;
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
	public void fileOpen()
	{
		JFileChooser chooser =
				ImagePreview.decorateWithImagePreview(new JFileChooser());
		File defaultFile = new File(PCGenSettings.getPcgDir());

		if (defaultFile.exists())
		{
			chooser.setCurrentDirectory(defaultFile);
		}

		String[] pcgs = new String[]{"pcg", "pcp"};
		String[] init = new String[]{"gmi", "init"};
		SimpleFileFilter ff = new SimpleFileFilter(init, "Initiative Export");
		chooser.addChoosableFileFilter(ff);
		chooser
			.addChoosableFileFilter(new SimpleFileFilter(pcgs, "PCGen File"));
		chooser.removeChoosableFileFilter(chooser.getAcceptAllFileFilter());
		chooser.setFileFilter(ff);
		chooser.setMultiSelectionEnabled(true);

		Cursor saveCursor = MiscUtilities.setBusyCursor(theView);
		int option = chooser.showOpenDialog(theView);

		if (option == JFileChooser.APPROVE_OPTION)
		{
			File[] pcFiles = chooser.getSelectedFiles();

			for (int i = 0; i < pcFiles.length; i++)
			{
				if (PCGFile.isPCGenCharacterOrPartyFile(pcFiles[i]))
				{
					GMBus.send(new OpenPCGRequestMessage(this, pcFiles[i],
						false));

					//loadPCG(pcFiles[i]);
				}
				else if (pcFiles[i].toString().endsWith(".init")
					|| pcFiles[i].toString().endsWith(".gmi"))
				{
					loadINIT(pcFiles[i]);
				}
			}
			/* loop through selected files */

			theView.refreshTable();
		}
		else
		{
			/* this means the file is invalid */
		}

		MiscUtilities.setCursor(theView, saveCursor);
	}

	/**
	 * <p>
	 * Gets the internal view's <code>InitHolderList</code>
	 * </p>
	 *
	 * @param message
	 */
	public void handleCombatRequestMessage(CombatRequestMessage message)
	{
		message.setCombat(theView.initList);
	}

	/**
	 * <p>
	 * Delegates to <code>handleAddButton()</code>
	 * </p>
	 *
	 * @param message
	 */
	public void handleFileOpenMessage(FileOpenMessage message)
	{
		if (GMGenSystemView.getTabPane().getSelectedComponent() instanceof Initiative)
		{
			fileOpen();
		}
	}

	/**
	 * <p>
	 * Handles an <code>InitHolderListSendMessage</code> by addomg all new
	 * combatants to the views list.
	 * </p>
	 *
	 * @param message
	 */
	public void handleInitHolderListSendMessage(
		InitHolderListSendMessage message)
	{
		if (message.getSource() != this)
		{
			InitHolderList cl = message.getInitHolderList();

			for (int i = 0; i < cl.size(); i++)
			{
				InitHolder iH = cl.get(i);
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
	 * @see gmgen.pluginmgr.GMBPlugin#handleMessage(GMBMessage)
	 */
	public void handleMessage(GMBMessage message)
	{
		if (message instanceof FileOpenMessage)
		{
			handleFileOpenMessage((FileOpenMessage) message);
		}
		else if (message instanceof SaveMessage)
		{
			handleSaveMessage((SaveMessage) message);
		}
		else if (message instanceof InitHolderListSendMessage)
		{
			handleInitHolderListSendMessage((InitHolderListSendMessage) message);
		}
		else if (message instanceof PCLoadedMessage)
		{
			handlePCLoadedMessage((PCLoadedMessage) message);
		}
		else if (message instanceof PCClosedMessage)
		{
			handlePCClosedMessage((PCClosedMessage) message);
		}
		else if (message instanceof WindowClosedMessage)
		{
			handleWindowClosedMessage((WindowClosedMessage) message);
		}
		else if (message instanceof StateChangedMessage)
		{
			handleStateChangedMessage((StateChangedMessage) message);
		}
		else if (message instanceof CombatRequestMessage)
		{
			handleCombatRequestMessage((CombatRequestMessage) message);
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
				loadINIT(files[i]);
			}
		}
	}

	/**
	 * <p>
	 * Removes the closed PC from the combat.
	 * </p>
	 *
	 * @param message
	 */
	public void handlePCClosedMessage(PCClosedMessage message)
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
	public void handlePCLoadedMessage(PCLoadedMessage message)
	{
		if (!message.isIgnored(this))
		{
			PlayerCharacter pc = message.getPC();
			String type = "PC";
			String player = pc.getPlayersName();

			//Based on the Player's name, auto set the combatant's type
			if (player.equalsIgnoreCase("Ally"))
			{
				type = "Ally";
			}
			else if (player.equalsIgnoreCase("GM")
				|| player.equalsIgnoreCase("DM")
				|| player.equalsIgnoreCase("Enemy"))
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
	}

	/**
	 * <p>
	 * Saves the combatants to a file
	 * </p>
	 */
	public void fileSave()
	{
		for (int i = 0; i < theView.initList.size(); i++)
		{
			InitHolder iH = theView.initList.get(i);

			if (iH instanceof PcgCombatant)
			{
				PcgCombatant pcgcbt = (PcgCombatant) iH;
				GMBus.send(new SavePCGRequestMessage(this, pcgcbt.getPC()));
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
	public void handleSaveMessage(SaveMessage message)
	{
		if (isActive())
		{
			fileSave();
			message.veto();
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
	public void handleStateChangedMessage(StateChangedMessage message)
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
			if (SettingsHandler.getGMGenOption(InitiativePlugin.LOG_NAME
				+ ".refreshOnStateChange", true))
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
	 * <p>
	 * Handles window closing by saving preferences.
	 * </p>
	 *
	 * @param message
	 */
	public void handleWindowClosedMessage(WindowClosedMessage message)
	{
		theView.setExitPrefs();
	}

	/**
	 * Returns true if this plugin is active
	 * @return true if this plugin is active
	 */
	public boolean isActive()
	{
		JTabbedPane tp = TabbedPaneUtilities.getTabbedPaneFor(theView);
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
	public void initMenuItem(ActionEvent evt)
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
	public void initMenus()
	{
		initToolsItem.setMnemonic('I');
		initToolsItem.setText("Initiative");
		initToolsItem.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent evt)
			{
				initMenuItem(evt);
			}
		});
		GMBus.send(new ToolMenuItemAddMessage(this, initToolsItem));
	}

	/**
	 * <p>
	 * Loads an initiative file
	 * </p>
	 *
	 * @param initFile
	 */
	public void loadINIT(File initFile)
	{
		theView.loadINIT(initFile, this);
	}
}