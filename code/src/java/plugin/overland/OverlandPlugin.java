package plugin.overland;

import gmgen.GMGenSystemView;
import gmgen.pluginmgr.GMBMessage;
import gmgen.pluginmgr.GMBPlugin;
import gmgen.pluginmgr.GMBus;
import gmgen.pluginmgr.messages.StateChangedMessage;
import gmgen.pluginmgr.messages.TabAddMessage;
import gmgen.pluginmgr.messages.ToolMenuItemAddMessage;
import pcgen.cdom.base.Constants;
import pcgen.core.SettingsHandler;
import pcgen.gui.utils.TabbedPaneUtilities;
import plugin.overland.gui.OverPanel;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.filechooser.FileFilter;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The <code>Overland Plugin</code> provides a number
 * of useful utilities that help with overland travel <br>
 * Created on February 26, 2003<br>
 * Updated on February 26, 2003
 * @author  Expires 2003
 * @version 2.10
 */
public class OverlandPlugin extends GMBPlugin
{
	/** Log name / plugin id */
	public static final String LOG_NAME = "Overland_Travel";

	/** The plugin menu item in the tools menu. */
	private JMenuItem overToolsItem = new JMenuItem();

	/** The user interface that this class will be using. */
	private OverPanel theView;

	/** The English name of the plugin. */
	private String name = "Overland Travel";

	/** The version number of the plugin. */
	private String version = "01.00.99.01.00";

	/**
	 * Creates a new instance of OverlandPlugin
	 */
	public OverlandPlugin()
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
		String datadir = this.getDataDir();
		theView = new OverPanel(datadir);
		GMBus.send(new TabAddMessage(this, name, getView(), getPluginSystem()));
		initMenus();
		getPluginSystem();
	}

	public String getPluginSystem()
	{
		return SettingsHandler.getGMGenOption(LOG_NAME + ".System",
			Constants.SYSTEM_GMGEN);
	}

	public int getPluginLoadOrder()
	{
		return SettingsHandler.getGMGenOption(LOG_NAME + ".LoadOrder", 90);
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

	/**
	 * listens to messages from the GMGen system, and handles them as needed
	 * @param message the source of the event from the system
	 * @see gmgen.pluginmgr.GMBPlugin#handleMessage(GMBMessage)
	 */
	public void handleMessage(GMBMessage message)
	{
		if (message instanceof StateChangedMessage)
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
		JTabbedPane tp = TabbedPaneUtilities.getTabbedPaneFor(theView);
		return tp != null && JOptionPane.getFrameForComponent(tp).isFocused()
			&& tp.getSelectedComponent().equals(theView);
	}

	/**
	 * Initialise the menus for this plugin
	 */
	public void initMenus()
	{
		overToolsItem.setMnemonic('O');
		overToolsItem.setText("Overland Travel");
		overToolsItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				toolMenuItem(evt);
			}
		});
		GMBus.send(new ToolMenuItemAddMessage(this, overToolsItem));
	}

	/**
	 * Sets the index for the pane 
	 * @param evt
	 */
	public void toolMenuItem(ActionEvent evt)
	{
		JTabbedPane tp = GMGenSystemView.getTabPane();

		for (int i = 0; i < tp.getTabCount(); i++)
		{
			if (tp.getComponentAt(i) instanceof OverPanel)
			{
				tp.setSelectedIndex(i);
			}
		}
	}
}
