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
package plugin.experience;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Collection;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import gmgen.GMGenSystem;
import gmgen.GMGenSystemView;
import gmgen.plugin.Combatant;
import gmgen.plugin.InitHolderList;
import gmgen.pluginmgr.messages.AddMenuItemToGMGenToolsMenuMessage;
import gmgen.pluginmgr.messages.CombatHasBeenInitiatedMessage;
import gmgen.pluginmgr.messages.FileMenuSaveMessage;
import gmgen.pluginmgr.messages.RequestAddPreferencesPanelMessage;
import gmgen.pluginmgr.messages.RequestAddTabToGMGenMessage;
import pcgen.core.SettingsHandler;
import pcgen.gui2.tools.Utility;
import pcgen.pluginmgr.InteractivePlugin;
import pcgen.pluginmgr.PCGenMessage;
import pcgen.pluginmgr.PCGenMessageHandler;
import pcgen.pluginmgr.messages.FocusOrStateChangeOccurredMessage;
import pcgen.system.LanguageBundle;
import pcgen.util.Logging;
import plugin.experience.gui.AddDefeatedCombatant;
import plugin.experience.gui.ExperienceAdjusterView;
import plugin.experience.gui.PreferencesExperiencePanel;

/**
 * The {@code ExperienceAdjusterController} handles the functionality of
 * the Adjusting of experience.  This class is called by the {@code GMGenSystem
 * } and will have it's own model and view.<br>
 */
public class ExperienceAdjusterPlugin extends KeyAdapter
		implements InteractivePlugin, ActionListener, ChangeListener /*Observer*/
{
	/** Log name */
	public static final String LOG_NAME = "Experience_Adjuster"; //$NON-NLS-1$

	/** The model that holds all the data for this section. */
	private ExperienceAdjusterModel eaModel;

	/** The user interface that this class will be using. */
	private ExperienceAdjusterView eaView;
	private InitHolderList initList;

	/** The plugin menu item in the tools menu. */
	private JMenuItem experienceToolsItem = new JMenuItem();

	/** The English name of the plugin. */
	private static final String NAME = "Experience"; //$NON-NLS-1$
	/** Key of plugin tab. */
	private static final String IN_NAME = "in_plugin_experience_name"; //$NON-NLS-1$
	/** Mnemonic in menu for {@link #IN_NAME} */
	private static final String IN_NAME_MN = "in_mn_plugin_experience_name"; //$NON-NLS-1$

	private PCGenMessageHandler messageHandler;

	/**
	 * Starts the plugin, registering itself with the {@code TabAddMessage}.
	 */
	@Override
	public void start(PCGenMessageHandler mh)
	{
		messageHandler = mh;
		eaModel = new ExperienceAdjusterModel(getDataDirectory());
		eaView = new ExperienceAdjusterView(eaModel);
		messageHandler.handleMessage(
			new RequestAddPreferencesPanelMessage(this, new PreferencesExperiencePanel()));
		initListeners();
		update();
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
		return SettingsHandler.getGMGenOption(ExperienceAdjusterPlugin.LOG_NAME + ".LoadOrder", 50);
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
	public String getPluginName()
	{
		return ExperienceAdjusterPlugin.NAME;
	}

	private String getLocalizedName()
	{
		return LanguageBundle.getString(ExperienceAdjusterPlugin.IN_NAME);
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
	@Override
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
	private void adjustCR(Combatant cbt)
	{
		String inputValue = JOptionPane.showInputDialog(GMGenSystem.inst, "CR", Float.toString(cbt.getCR()));

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
	private void handleAddEnemyButton()
	{
		AddDefeatedCombatant dialog = new AddDefeatedCombatant(GMGenSystem.inst, true, eaModel);
		dialog.setVisible(true);
		handleGroupBox();
		update();
		eaView.getEnemyList().updateUI();
	}

	/**
	 * Handles the <b>Add Experience to Character</b> button on the GUI.
	 */
	private void handleAddExperienceToCharButton()
	{
		if (eaView.getCharacterList().getSelectedIndex() != -1)
		{
			try
			{
				List list = eaView.getCharacterList().getSelectedValuesList();

				for (final Object aList : list)
				{
					eaModel.addExperienceToCharacter((ExperienceListItem) aList,
						Integer.parseInt(eaView.getExperienceField().getText()));
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
	private void handleAddExperienceToPartyButton()
	{
		eaModel.addExperienceToParty();
		eaView.getCharacterList().updateUI();
		eaModel.clearEnemies();
		handleGroupBox();
	}

	/**
	 * Handle adjust CR button
	 */
	private void handleAdjustCRButton()
	{
		if (eaView.getCharacterList().getSelectedIndex() != -1)
		{
			final Collection list = eaView.getCharacterList().getSelectedValuesList();

			for (final Object aList : list)
			{
				final ExperienceListItem item = (ExperienceListItem) aList;
				Combatant cbt = item.getCombatant();
				adjustCR(cbt);
			}
		}

		if (eaView.getEnemyList().getSelectedIndex() != -1)
		{
			final Collection list = eaView.getEnemyList().getSelectedValuesList();

			for (final Object aList : list)
			{
				ExperienceListItem item = (ExperienceListItem) aList;
				Combatant cbt = item.getCombatant();
				adjustCR(cbt);
			}
		}

		update();
	}

	/**
	 * Handles the {@code Export} button or menu option.
	 */
	private void handleExportButton()
	{
		Logging.errorPrint("unsupported operation");
	}

	/**
	 * Handles the action performed on the Group Box
	 */
	private void handleGroupBox()
	{
		eaModel.updatePartyExperience();
		eaView.setExperienceFromCombat(eaModel.getPartyExperience());
	}

	/**
	 * Handle multiplier slider
	 */
	private void handleMultiplierSlider()
	{
		// TODO the group box stuff should listen to the slider change directly
		handleGroupBox();
	}

	/**
	 * Handle remove enemey button
	 */
	private void handleRemoveEnemyButton()
	{
		if (eaView.getEnemyList().getSelectedIndex() != -1)
		{
			List list = eaView.getEnemyList().getSelectedValuesList();

			for (final Object aList : list)
			{
				eaModel.removeEnemy((ExperienceListItem) aList);
			}
		}

		handleGroupBox();
		update();
		eaView.getEnemyList().updateUI();
	}

	/**
	 * Registers all the listeners for any actions.
	 * Made it final as it is called from constructor.
	 */
	private final void initListeners()
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
	 * @param eaView the {@code JPanel} that this class uses.
	 */
	public void setView(ExperienceAdjusterView eaView)
	{
		this.eaView = eaView;
	}

	/**
	 * Initialise menus
	 */
	private void initMenus()
	{
		experienceToolsItem.setMnemonic(LanguageBundle.getMnemonic(ExperienceAdjusterPlugin.IN_NAME_MN));
		experienceToolsItem.setText(getLocalizedName());
		experienceToolsItem.addActionListener(ExperienceAdjusterPlugin::toolMenuItem);
		messageHandler.handleMessage(new AddMenuItemToGMGenToolsMenuMessage(this, experienceToolsItem));
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
		update();
	}

	@Override
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
	private static void toolMenuItem(ActionEvent evt)
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
	 */
	@Override
	public void handleMessage(PCGenMessage message)
	{
		if (message instanceof CombatHasBeenInitiatedMessage)
		{
			if (message.getSource() == this)
			{
				CombatHasBeenInitiatedMessage cmessage = (CombatHasBeenInitiatedMessage) message;

				if (initList == null)
				{
					initList = cmessage.getCombat();
				}

				eaModel.setCombat(initList);
			}

			update();
		}
		else if (message instanceof FocusOrStateChangeOccurredMessage)
		{
			if (isActive())
			{
				experienceToolsItem.setEnabled(false);

				if (initList == null)
				{
					messageHandler.handleMessage(new CombatHasBeenInitiatedMessage(this));
				}

				update();
			}
			else
			{
				experienceToolsItem.setEnabled(true);
			}
		}
		else if (message instanceof FileMenuSaveMessage)
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
		JTabbedPane tp = Utility.getTabbedPaneFor(eaView);
		return tp != null && JOptionPane.getFrameForComponent(tp).isFocused()
			&& tp.getSelectedComponent().equals(eaView);
	}

	@Override
	public File getDataDirectory()
	{
		return new File(SettingsHandler.getGmgenPluginDir(), ExperienceAdjusterPlugin.NAME);
	}
}
