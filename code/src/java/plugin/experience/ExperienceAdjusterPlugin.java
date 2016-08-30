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
 *
 * $Id$
 */
 package plugin.experience;

import gmgen.GMGenSystem;
import gmgen.GMGenSystemView;
import gmgen.plugin.Combatant;
import gmgen.plugin.InitHolderList;
import gmgen.pluginmgr.messages.AddMenuItemToGMGenToolsMenuMessage;
import gmgen.pluginmgr.messages.CombatHasBeenInitiatedMessage;
import gmgen.pluginmgr.messages.FileMenuSaveMessage;
import gmgen.pluginmgr.messages.RequestAddPreferencesPanelMessage;
import gmgen.pluginmgr.messages.RequestAddTabToGMGenMessage;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

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
 * The <code>ExperienceAdjusterController</code> handles the functionality of
 * the Adjusting of experience.  This class is called by the <code>GMGenSystem
 * </code> and will have it's own model and view.<br>
 * Created on February 26, 2003<br>
 * Updated on February 26, 2003
 * @author  Expires 2003
 * @version 2.10
 */
public class ExperienceAdjusterPlugin implements InteractivePlugin,
		ActionListener, ChangeListener, KeyListener /*Observer*/
{
	/** Log name */
	public static final String LOG_NAME = "Experience_Adjuster"; //$NON-NLS-1$

	/** The model that holds all the data for this section. */
	protected ExperienceAdjusterModel eaModel;

	/** The user interface that this class will be using. */
	protected ExperienceAdjusterView eaView;
	protected InitHolderList initList;

	/** The plugin menu item in the tools menu. */
	protected JMenuItem experienceToolsItem = new JMenuItem();

	/** The English name of the plugin. */
	private static final String NAME = "Experience"; //$NON-NLS-1$
	/** Key of plugin tab. */
	private static final String IN_NAME = "in_plugin_experience_name"; //$NON-NLS-1$
	/** Mnemonic in menu for {@link #IN_NAME} */
	private static final String IN_NAME_MN = "in_mn_plugin_experience_name"; //$NON-NLS-1$

	/** The version number of the plugin. */
	protected String version = "01.00.99.01.00";

	private PCGenMessageHandler messageHandler;

	/**
	 * Creates a new instance of ExperienceAdjusterModel
	 */
	public ExperienceAdjusterPlugin()
	{
		// Do Nothing
	}

	/**
	 * Starts the plugin, registering itself with the <code>TabAddMessage</code>.
	 */
	@Override
	public void start(PCGenMessageHandler mh)
	{
    	messageHandler = mh;
		eaModel = new ExperienceAdjusterModel(getDataDirectory());
		eaView = new ExperienceAdjusterView(eaModel);
		messageHandler.handleMessage(new RequestAddPreferencesPanelMessage(this, getLocalizedName(),
			new PreferencesExperiencePanel()));
		initListeners();
		update();
		messageHandler.handleMessage(new RequestAddTabToGMGenMessage(this, getLocalizedName(), getView()));
		initMenus();
	}

	/**
	 * {@inheritDoc}
	 */
    @Override
	public void stop()
	{
		messageHandler = null;
	}

	@Override
	public int getPriority()
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
	 * Handles the <b>Add Experience to Character</b> button on the GUI.
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
		// TODO the group box stuff should listen to the slider change directly
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
		experienceToolsItem.setMnemonic(LanguageBundle.getMnemonic(IN_NAME_MN));
		experienceToolsItem.setText(getLocalizedName());
		experienceToolsItem.addActionListener(new ActionListener()
		{
            @Override
			public void actionPerformed(ActionEvent evt)
			{
				toolMenuItem(evt);
			}
		});
		messageHandler.handleMessage(new AddMenuItemToGMGenToolsMenuMessage(this, experienceToolsItem));
	}

    @Override
	public void keyPressed(KeyEvent e)
	{
		// TODO:  Method doesn't do anything?
	}

    @Override
	public void keyReleased(KeyEvent e)
	{
		this.update();
	}

    @Override
	public void keyTyped(KeyEvent e)
	{
		// TODO:  Method doesn't do anything?
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public File getDataDirectory()
	{
		File dataDir =
				new File(SettingsHandler.getGmgenPluginDir(), getPluginName());
		return dataDir;
	}
}
