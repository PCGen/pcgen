package plugin.network;

import gmgen.GMGenSystemView;
import gmgen.pluginmgr.GMBMessage;
import gmgen.pluginmgr.GMBPlugin;
import gmgen.pluginmgr.GMBus;
import gmgen.pluginmgr.messages.*;
import pcgen.cdom.base.Constants;
import pcgen.core.SettingsHandler;
import pcgen.gui.utils.TabbedPaneUtilities;
import plugin.network.gui.NetworkView;
import plugin.network.gui.PreferencesNetworkingPanel;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.filechooser.FileFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The <code>ExperienceAdjusterController</code> handles the functionality of
 * the Adjusting of experience.  This class is called by the <code>GMGenSystem
 * </code> and will have it's own model and view.<br>
 * Created on February 26, 2003<br>
 * Updated on February 26, 2003
 * @author  Expires 2003
 * @version 2.10
 */
public class NetworkPlugin extends GMBPlugin
{
	public static final String LOG_NAME = "Network";

	/** The English name of the plugin. */
	private String name = "Network";

	/** The version number of the plugin. */
	private String version = "01.00.99.01.00";

	private NetworkModel model;

	private JMenuItem netToolsItem = new JMenuItem();

	/**
	 * Creates a new instance of NetworkPlugin
	 */
	public NetworkPlugin()
	{
		// Do Nothing
	}

    @Override
	public FileFilter[] getFileTypes()
	{
		return null;
	}

	/**
	 * Starts the plugin, registering itself with the <code>TabAddMessage</code>.
	 */
    @Override
	public void start()
	{
		model = new NetworkModel();
		GMBus.send(new TabAddMessage(this, name, model.getView(),
			getPluginSystem()));
		initMenus();
		GMBus.send(new PreferencesPanelAddMessage(this, name,
			new PreferencesNetworkingPanel(model)));
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
		return SettingsHandler.getGMGenOption(LOG_NAME + ".LoadOrder", 60);
	}

	/**
	 * Accessor for name
	 * @return name
	 */
    @Override
	public String getName()
	{
		return name;
	}

	/**
	 * Accessor for version
	 * @return version
	 */
    @Override
	public String getVersion()
	{
		return version;
	}

	/**
	 * listens to messages from the GMGen system, and handles them as needed
	 * @param message the source of the event from the system
	 * @see gmgen.pluginmgr.GMBPlugin#handleMessage(GMBMessage)
	 */
    @Override
	public void handleMessage(GMBMessage message)
	{
		if (message instanceof CombatRequestMessage)
		{
			handleCombatRequestMessage((CombatRequestMessage) message);
		}
		else if (message instanceof StateChangedMessage)
		{
			handleStateChangedMessage((StateChangedMessage) message);
		}
		else if (message instanceof CombatantUpdatedMessage)
		{
			handleCombatantUpdatedMessage((CombatantUpdatedMessage) message);
		}
	}

	private void handleStateChangedMessage(StateChangedMessage message)
	{
		if (isActive())
		{
			netToolsItem.setEnabled(false);
			if (model.getCombat() == null)
			{
				GMBus.send(new CombatRequestMessage(this));
			}
			try
			{
				GMGenSystemView.getTabPane().setIconAt(
					GMGenSystemView.getTabPane().indexOfTab(name), null);
			}
			catch (Exception e)
			{
				// TODO Handle this?
			}
			model.clearIcon();
			model.refresh();
		}
		else
		{
			netToolsItem.setEnabled(true);
		}
	}

	private void handleCombatRequestMessage(CombatRequestMessage message)
	{
		if (message.getSource() == this)
		{
			model.setCombat(message.getCombat());
		}
		model.refresh();
	}

	private void handleCombatantUpdatedMessage(CombatantUpdatedMessage message)
	{
		model.combatantUpdated(message.getCombatant());
	}

	public boolean isActive()
	{
		JTabbedPane tp = TabbedPaneUtilities.getTabbedPaneFor(model.getView());
		return tp != null && JOptionPane.getFrameForComponent(tp).isFocused()
			&& tp.getSelectedComponent().equals(model.getView());
	}

	public void toolMenuItem(ActionEvent evt)
	{
		JTabbedPane tp = GMGenSystemView.getTabPane();

		for (int i = 0; i < tp.getTabCount(); i++)
		{
			if (tp.getComponentAt(i) instanceof NetworkView)
			{
				tp.setSelectedIndex(i);
			}
		}
	}

	private void initMenus()
	{
		netToolsItem.setMnemonic('N');
		netToolsItem.setText("Network");
		netToolsItem.addActionListener(new ActionListener()
		{
            @Override
			public void actionPerformed(ActionEvent evt)
			{
				toolMenuItem(evt);
			}
		});
		GMBus.send(new ToolMenuItemAddMessage(this, netToolsItem));
	}
}
