package plugin.experience;

import gmgen.GMGenSystem;
import gmgen.GMGenSystemView;
import gmgen.plugin.Combatant;
import gmgen.plugin.InitHolderList;
import gmgen.pluginmgr.GMBMessage;
import gmgen.pluginmgr.GMBPlugin;
import gmgen.pluginmgr.GMBus;
import gmgen.pluginmgr.messages.CombatRequestMessage;
import gmgen.pluginmgr.messages.PreferencesPanelAddMessage;
import gmgen.pluginmgr.messages.SaveMessage;
import gmgen.pluginmgr.messages.StateChangedMessage;
import gmgen.pluginmgr.messages.TabAddMessage;
import gmgen.pluginmgr.messages.ToolMenuItemAddMessage;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.NumberFormat;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;

import pcgen.cdom.base.Constants;
import pcgen.core.SettingsHandler;
import pcgen.core.utils.CoreUtility;
import pcgen.gui.utils.TabbedPaneUtilities;
import pcgen.util.Logging;
import plugin.experience.gui.AddDefeatedCombatant;
import plugin.experience.gui.ExperienceAdjusterView;
import plugin.experience.gui.PreferencesExperiencePanel;

/**
 * The <code>ExperienceAdjusterController</code> handles the functionality of
 * the Adjusting of experience.  This class is called by the <code>GMGenSystem
 * </code> and will have it's own model and view.<br>
 * Created on February 26, 2003<br>
 * Updated on February 26, 2003
 * @author  Expires 2003
 * @version 2.10
 */
public class ExperienceAdjusterPlugin extends GMBPlugin implements
		ActionListener, ChangeListener, KeyListener /*Observer*/
{
	/** Log name */
	public static final String LOG_NAME = "Experience_Adjuster";

	/** The model that holds all the data for this section. */
	protected ExperienceAdjusterModel eaModel;

	/** The user interface that this class will be using. */
	protected ExperienceAdjusterView eaView;
	protected InitHolderList initList;

	/** The plugin menu item in the tools menu. */
	protected JMenuItem experienceToolsItem = new JMenuItem();

	/** The English name of the plugin. */
	protected String name = "Experience";

	/** The version number of the plugin. */
	protected String version = "01.00.99.01.00";

	/**
	 * Creates a new instance of ExperienceAdjusterModel
	 */
	public ExperienceAdjusterPlugin()
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
		eaModel = new ExperienceAdjusterModel(getDataDir());
		eaView = new ExperienceAdjusterView();
		GMBus.send(new PreferencesPanelAddMessage(this, name,
			new PreferencesExperiencePanel()));
		initListeners();
		update();
		GMBus.send(new TabAddMessage(this, name, getView(), getPluginSystem()));
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
		return SettingsHandler.getGMGenOption(LOG_NAME + ".LoadOrder", 50);
	}

	/**
	 * Gets the model that this class is using.
	 * @return the model.
	 */
	public ExperienceAdjusterModel getModel()
	{
		return eaModel;
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
	 * Gets the view that this class is using.
	 * @return the view.
	 */
	public JPanel getView()
	{
		return eaView;
	}

	/**
	 * Calls the appropriate methods depending on the source of the action.
	 * @param e the action even that happened.
	 */
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == eaView.getAddExperienceToCharButton())
		{
			handleAddExperienceToCharButton();
		}

		if (e.getSource() == eaView.getAddExperienceToPartyButton())
		{
			handleAddExperienceToPartyButton();
		}

		if (e.getSource() == eaView.getAddEnemyButton())
		{
			handleAddEnemyButton();
		}

		if (e.getSource() == eaView.getRemoveEnemyButton())
		{
			handleRemoveEnemyButton();
		}

		if (e.getSource() == eaView.getAdjustCRButton())
		{
			handleAdjustCRButton();
		}
	}

	/**
	 * Adjust the CR
	 * @param cbt
	 */
	public void adjustCR(Combatant cbt)
	{
		String inputValue =
				JOptionPane.showInputDialog(GMGenSystem.inst, "CR", Float
					.toString(cbt.getCR()));

		if (inputValue != null)
		{
			try
			{
				cbt.setCR(Float.parseFloat(inputValue));
			}
			catch (NumberFormatException e)
			{
				Logging.errorPrint("Value could not be parsed into a number.");
				adjustCR(cbt);
			}
		}
	}

	/**
	 * Handle enemy button 
	 */
	public void handleAddEnemyButton()
	{
		AddDefeatedCombatant dialog =
				new AddDefeatedCombatant(GMGenSystem.inst, true, eaModel);
		dialog.setVisible(true);
		handleGroupBox();
		update();
		this.eaView.getEnemyList().updateUI();
	}

	/**
	 * Handles the <b>Add Experience to Character</code> button on the GUI.
	 */
	public void handleAddExperienceToCharButton()
	{
		if (eaView.getCharacterList().getSelectedIndex() != -1)
		{
			try
			{
				Object[] list = eaView.getCharacterList().getSelectedValues();

				for (int i = 0; i < list.length; i++)
				{
					eaModel.addExperienceToCharacter(
						(ExperienceListItem) list[i], Integer.parseInt(eaView
							.getExperienceField().getText()));
				}
			}
			catch (NumberFormatException e)
			{
				eaView.setExperienceToAdd("");
			}
		}

		eaView.getCharacterList().updateUI();
		eaView.getExperienceField().setText("0");
	}

	/**
	 * Handles the <b>Add Experience to Group</b> button on the GUI.
	 */
	public void handleAddExperienceToPartyButton()
	{
		eaModel.addExperienceToParty();
		this.eaView.getCharacterList().updateUI();
		eaModel.clearEnemies();
		handleGroupBox();
	}

	/**
	 * Handle adjust CR button
	 */
	public void handleAdjustCRButton()
	{
		if (eaView.getCharacterList().getSelectedIndex() != -1)
		{
			Object[] list = eaView.getCharacterList().getSelectedValues();

			for (int i = 0; i < list.length; i++)
			{
				ExperienceListItem item = (ExperienceListItem) list[i];
				Combatant cbt = item.getCombatant();
				adjustCR(cbt);
			}
		}

		if (eaView.getEnemyList().getSelectedIndex() != -1)
		{
			Object[] list = eaView.getEnemyList().getSelectedValues();

			for (int i = 0; i < list.length; i++)
			{
				ExperienceListItem item = (ExperienceListItem) list[i];
				Combatant cbt = item.getCombatant();
				adjustCR(cbt);
			}
		}

		update();
	}

	/**
	 * Handles the <code>Export</code> button or menu option.
	 */
	public void handleExportButton()
	{
		/*if(c.size() != 0) {
		 JFileChooser chooser = new JFileChooser();
		 String[] txts = new String[] {"txt"};
		 chooser.addChoosableFileFilter(new SimpleFileFilter(txts, "Text Format (*.txt)"));
		 chooser.removeChoosableFileFilter(chooser.getAcceptAllFileFilter());
		 int option = chooser.showSaveDialog(eaView);
		 if(option == JFileChooser.APPROVE_OPTION) {
		 eaModel.export( chooser.getSelectedFile() );
		 } else {
		 // this means the file is invalid
		 }
		 }*/
	}

	/**
	 * Handles the action performed on the Group Box
	 */
	public void handleGroupBox()
	{
		eaModel.updatePartyExperience();
		eaView.setExperienceFromCombat(eaModel.getPartyExperience());
	}

	/**
	 * Handle multiplier slider
	 */
	public void handleMultiplierSlider()
	{
		int value = eaView.getExperienceMultSlider().getValue();
		double realValue = 1.0 + (value * 0.1);
		eaModel.setMultiplier(realValue);

		if (CoreUtility.doublesEqual(realValue, 0.5))
		{
			eaView.getExperienceMultNameLabel().setText("Half as Hard");
		}
		else if (realValue <= .7)
		{
			eaView.getExperienceMultNameLabel().setText("Much Easier");
		}
		else if ((realValue > .7) && (realValue < 1.5))
		{
			eaView.getExperienceMultNameLabel().setText("Normal");
		}
		else if (realValue >= 1.5)
		{
			eaView.getExperienceMultNameLabel().setText("Much Harder");
		}

		if (CoreUtility.doublesEqual(realValue, 2))
		{
			eaView.getExperienceMultNameLabel().setText("Twice as Hard");
		}

		NumberFormat nf = java.text.NumberFormat.getNumberInstance();
		nf.setMaximumFractionDigits(1);

		eaView.getExperienceMultLabel().setText(nf.format(realValue) + "×");
		handleGroupBox();
	}

	/**
	 * Handle remove enemey button
	 */
	public void handleRemoveEnemyButton()
	{
		if (eaView.getEnemyList().getSelectedIndex() != -1)
		{
			Object[] list = eaView.getEnemyList().getSelectedValues();

			for (int i = 0; i < list.length; i++)
			{
				eaModel.removeEnemy((ExperienceListItem) list[i]);
			}
		}

		handleGroupBox();
		update();
		this.eaView.getEnemyList().updateUI();
	}

	/**
	 * Registers all the listeners for any actions.
	 * Made it final as it is called from constructor.
	 */
	public final void initListeners()
	{
		eaView.getAddExperienceToCharButton().addActionListener(this);
		eaView.getAddExperienceToPartyButton().addActionListener(this);
		eaView.getAdjustCRButton().addActionListener(this);
		eaView.getAddEnemyButton().addActionListener(this);
		eaView.getRemoveEnemyButton().addActionListener(this);
		eaView.getExperienceMultSlider().addChangeListener(this);
	}

	/**
	 * Sets the instance of the model for this class to use.
	 * @param eaModel the model for this class to use.
	 */
	public void setModel(ExperienceAdjusterModel eaModel)
	{
		this.eaModel = eaModel;
	}

	/**
	 * Sets the instance of the view for this class to use.
	 * @param eaView the <code>JPanel</code> that this class uses.
	 */
	public void setView(ExperienceAdjusterView eaView)
	{
		this.eaView = eaView;
	}

	/**
	 * Initialise menus
	 */
	public void initMenus()
	{
		experienceToolsItem.setMnemonic('E');
		experienceToolsItem.setText("Experience Adjuster");
		experienceToolsItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				toolMenuItem(evt);
			}
		});
		GMBus.send(new ToolMenuItemAddMessage(this, experienceToolsItem));
	}

	public void keyPressed(KeyEvent e)
	{
		// TODO:  Method doesn't do anything?
	}

	public void keyReleased(KeyEvent e)
	{
		this.update();
	}

	public void keyTyped(KeyEvent e)
	{
		// TODO:  Method doesn't do anything?
	}

	public void stateChanged(ChangeEvent e)
	{
		if (e.getSource() == eaView.getExperienceMultSlider())
		{
			handleMultiplierSlider();
		}
	}

	/**
	 * Tool menu item
	 * @param evt
	 */
	public void toolMenuItem(ActionEvent evt)
	{
		JTabbedPane tp = GMGenSystemView.getTabPane();

		for (int i = 0; i < tp.getTabCount(); i++)
		{
			if (tp.getComponentAt(i) instanceof ExperienceAdjusterView)
			{
				tp.setSelectedIndex(i);
			}
		}
	}

	/**
	 * Calls all the necessary update functions for the GUI components.
	 * Made it final as it is called from constructor.
	 */
	public final void update()
	{
		eaModel.populateLists();
		eaView.setParty(eaModel.getParty());
		eaView.setEnemies(eaModel.getEnemies());
		handleGroupBox();
	}

	/**
	 * listens to messages from the GMGen system, and handles them as needed
	 * @param message the source of the event from the system
	 * @see GMBPlugin#handleMessage(GMBMessage)
	 */
	@Override
	public void handleMessage(GMBMessage message)
	{
		if (message instanceof CombatRequestMessage)
		{
			if (message.getSource() == this)
			{
				CombatRequestMessage cmessage = (CombatRequestMessage) message;

				if (initList == null)
				{
					initList = cmessage.getCombat();
				}

				eaModel.setCombat(initList);
			}

			update();
		}
		else if (message instanceof StateChangedMessage)
		{
			if (isActive())
			{
				experienceToolsItem.setEnabled(false);

				if (initList == null)
				{
					GMBus.send(new CombatRequestMessage(this));
				}

				update();
			}
			else
			{
				experienceToolsItem.setEnabled(true);
			}
		}
		else if (message instanceof SaveMessage)
		{
			if (isActive())
			{
				handleExportButton();
			}
		}
	}

	/**
	 * Return TRUE if active
	 * @return TRUE if active
	 */
	public boolean isActive()
	{
		JTabbedPane tp = TabbedPaneUtilities.getTabbedPaneFor(eaView);
		return tp != null && JOptionPane.getFrameForComponent(tp).isFocused()
			&& tp.getSelectedComponent().equals(eaView);
	}
}
