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
package plugin.experience.gui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import pcgen.core.utils.CoreUtility;
import pcgen.system.LanguageBundle;
import plugin.experience.ExperienceAdjusterModel;

/**
 * The View for the Experience Adjuster.  This view is independant and will be
 * created by the {@code ExperienceAdjusterController}.  The view is
 * implemented as a {@code JPanel} so it can be added to the
 * {@code JTabbedPane} of the {@code GMGenSystemView}.<br>
 */
// TODO use multi column list rather than a single line
public class ExperienceAdjusterView extends JPanel
{
	// TODO make this l&f / UIManager value dependent
	private static final int BORDER_SIZE = 6;

	/** Button to add an enemy to the experience calculation. */
	private JButton addEnemyButton;

	/** The <b>Add Experience to Selected Character</b> button. */
	private JButton addExperienceToCharButton;

	/** The <b>Add Experience to Selected Group</b> button. */
	private JButton addExperienceToPartyButton;

	/** The <b>Add Experience to Selected Character</b> button. */
	private JButton adjustCRButton;

	/** Button to delete an enemy from the experience calculation. */
	private JButton removeEnemyButton;

	/** The <b>Total Experience from Combat:</b> label. */
	private JLabel experienceFromCombat;

	/** The multiplier label. */
	private JLabel experienceMultLabel;

	/** The Name for the experience multiplier label. */
	private JLabel experienceMultNameLabel;

	//Various components to shape the form properly
	private JLabel jLabel4;
	private JLabel jLabel5;

	/** The GUI component that holds the list of PC combatants. */
	private JList characterList;

	/** The GUI component that holds the list of enemy combatants. */
	private JList enemyList;
	private JPanel jPanel1;
	private JPanel panelChar;
	private JPanel jPanel5;
	private JPanel jPanel6;
	private JPanel jPanel7;
	private JPanel jPanel8;
	private JScrollPane scrollPaneChar;

	/** The <b>Experience Multiplier</b> slider. */
	private JSlider experienceMultSlider;

	/** The user editable field that takes in experience to add. */
	private JTextField experienceToAdd;

	private ExperienceAdjusterModel model;

	/**
	 * Creates an instance of {@code ExperienceAdjusterView}
	 * {@code ExperienceAdjusterController}.
	 */
	public ExperienceAdjusterView(ExperienceAdjusterModel model)
	{
		this.model = model;
		initComponents();
	}

	/**
	 * Gets the add enemy button
	 * @return the add enemy button
	 */
	public JButton getAddEnemyButton()
	{
		return addEnemyButton;
	}

	/**
	 * Gets the <b>Add Experience to Selected Character</b> button.
	 * @return the {@code addExperienceToCharButton}.
	 */
	public JButton getAddExperienceToCharButton()
	{
		return addExperienceToCharButton;
	}

	/**
	 * Gets the <b>Add Experience to Selected Party</b> button.
	 * @return the {@code addExperienceToPartyButton}.
	 */
	public JButton getAddExperienceToPartyButton()
	{
		return addExperienceToPartyButton;
	}

	/**
	 * Gets the <b>Adjust CR</b> button.
	 * @return the {@code adjustCRButton}.
	 */
	public JButton getAdjustCRButton()
	{
		return adjustCRButton;
	}

	/**
	 * Gets the list of characters from the GUI.
	 * @return the {@code characterList}.
	 */
	public JList getCharacterList()
	{
		return characterList;
	}

	/**
	 * Set enemies
	 * @param enemies
	 */
	public void setEnemies(DefaultListModel enemies)
	{
		enemyList.setModel(enemies);
	}

	/**
	 * Get enemy list
	 * @return enemy list
	 */
	public JList getEnemyList()
	{
		return enemyList;
	}

	/**
	 * Gets the field for experience to add.
	 * @return the {@code experienceToAdd} field.
	 */
	public JTextField getExperienceField()
	{
		return experienceToAdd;
	}

	/**
	 * Sets the experience from combat value on the GUI.
	 * @param experience the value of experience that has come from the combat.
	 */
	public void setExperienceFromCombat(int experience)
	{
		experienceFromCombat.setText(Integer.toString(experience));
	}

	/**
	 * Sets the experience from combat value on the GUI.
	 * @param experience the value of experience that has come from the combat.
	 */
	public void setExperienceFromCombat(String experience)
	{
		experienceFromCombat.setText(experience);
	}

	/**
	 * Gets the value from the experience from combat label.
	 * @return the value for experience from combat.
	 */
	public int getExperienceFromCombat()
	{
		return Integer.parseInt(experienceFromCombat.getText());
	}

	/** The multiplier label.
	 * @return JLabel*/
	public JLabel getExperienceMultLabel()
	{
		return experienceMultLabel;
	}

	/** The Name for the experience multiplier label.
	 * @return JLabel*/
	public JLabel getExperienceMultNameLabel()
	{
		return experienceMultNameLabel;
	}

	/** The <b>Experience Multiplier</b> slider.
	 * @return JSlider*/
	public JSlider getExperienceMultSlider()
	{
		return experienceMultSlider;
	}

	/**
	 * Sets the experience to add field if needed.
	 * @param experience the value for the experience to add to the character.
	 */
	public void setExperienceToAdd(int experience)
	{
		experienceToAdd.setText(Integer.toString(experience));
	}

	/**
	 * Sets the experience to add field if needed.
	 * @param experience the value for the experience to add to the character.
	 */
	public void setExperienceToAdd(String experience)
	{
		experienceToAdd.setText(experience);
	}

	/**
	 * Gets the experience to add that the user has input.
	 * @return the experience to add as an {@code int}.
	 */
	public int getExperienceToAdd()
	{
		return Integer.parseInt(experienceToAdd.getText());
	}

	/**
	 * sets the party in the main list display
	 * @param party
	 */
	public void setParty(DefaultListModel party)
	{
		characterList.setModel(party);
	}

	/**
	 * Get the remove enemy button
	 * @return the remove enemy button
	 */
	public JButton getRemoveEnemyButton()
	{
		return removeEnemyButton;
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	private void initComponents()
	{
		java.awt.GridBagConstraints gridBagConstraints;

		jPanel5 = new JPanel();
		panelChar = new JPanel();
		characterList = new JList();
		JLabel spCharLabel = new JLabel();
		jPanel1 = new JPanel();
		jLabel4 = new JLabel();
		enemyList = new JList();
		jPanel6 = new JPanel();
		jPanel7 = new JPanel();
		JLabel jLabel7 = new JLabel();
		experienceToAdd = new JTextField(6);
		addExperienceToCharButton = new JButton();
		jPanel8 = new JPanel();
		jLabel5 = new JLabel();
		experienceFromCombat = new JLabel();
		experienceMultNameLabel = new JLabel();
		experienceMultSlider = new JSlider();
		addExperienceToPartyButton = new JButton();
		experienceMultLabel = new JLabel();
		adjustCRButton = new JButton();
		addEnemyButton = new JButton();
		removeEnemyButton = new JButton();
		scrollPaneChar = new JScrollPane(characterList);
		JScrollPane scrollPaneEnemy = new JScrollPane(enemyList);

		setLayout(new GridLayout(0, 1));

		jPanel5.setBorder(new TitledBorder(LanguageBundle.getString("in_plugin_xp_char"))); //$NON-NLS-1$
		jPanel5.setLayout(new java.awt.GridLayout(1, 0));

		panelChar.setLayout(new java.awt.BorderLayout());

		spCharLabel.setText(LanguageBundle.getString("in_plugin_xp_nameLvlXp")); //$NON-NLS-1$
		panelChar.add(spCharLabel, BorderLayout.NORTH);
		panelChar.add(scrollPaneChar, java.awt.BorderLayout.CENTER);
		jPanel5.add(panelChar);

		jPanel1.setLayout(new java.awt.BorderLayout());

		jLabel4.setText(LanguageBundle.getString("in_plugin_xp_nameCr")); //$NON-NLS-1$
		jPanel1.add(jLabel4, java.awt.BorderLayout.NORTH);
		jPanel1.add(scrollPaneEnemy, java.awt.BorderLayout.CENTER);

		add(jPanel5);

		jPanel6.setLayout(new java.awt.GridLayout(1, 0));
		jPanel6.setBorder(new TitledBorder(LanguageBundle.getString("in_plugin_xp_enemies"))); //$NON-NLS-1$
		jPanel6.add(jPanel1);

		jPanel7.setLayout(new java.awt.GridBagLayout());

		// the button is after to allow the use of Tab after entering a value then pressing the button
		jLabel7.setText(LanguageBundle.getString("in_plugin_xp_xpTo")); //$NON-NLS-1$
		addExperienceToCharButton.setText(LanguageBundle.getString("in_plugin_xp_selectedChar")); //$NON-NLS-1$
		addExperienceToCharButton.setEnabled(false);
		gridBagConstraints = new java.awt.GridBagConstraints();
		jPanel7.add(new JLabel(LanguageBundle.getString("in_plugin_xp_add")), gridBagConstraints); //$NON-NLS-1$
		jPanel7.add(experienceToAdd, gridBagConstraints);
		jPanel7.add(jLabel7, gridBagConstraints);
		jPanel7.add(addExperienceToCharButton, gridBagConstraints);
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		// add an empty horizontal glue like panel
		jPanel7.add(new JPanel(), gridBagConstraints);
		// Updates the button if there is a selected character
		characterList.addListSelectionListener(new ListSelectionListener()
		{

			@Override
			public void valueChanged(ListSelectionEvent e)
			{
				if (!e.getValueIsAdjusting())
				{
					addExperienceToCharButton.setEnabled(!characterList.isSelectionEmpty());
				}
			}
		});

		jPanel7.setBorder(BorderFactory.createEmptyBorder(0, BORDER_SIZE, 0, 0));
		jPanel5.add(jPanel7);

		jPanel8.setLayout(new java.awt.GridBagLayout());

		jLabel5.setText(LanguageBundle.getString("in_plugin_xp_xpFromCombat")); //$NON-NLS-1$
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridwidth = 2;
		jPanel8.add(jLabel5, gridBagConstraints);

		experienceFromCombat.setText(Integer.toString(0));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		jPanel8.add(experienceFromCombat, gridBagConstraints);

		experienceMultNameLabel.setText(LanguageBundle.getString("in_plugin_xp_normal")); //$NON-NLS-1$
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		jPanel8.add(experienceMultNameLabel, gridBagConstraints);

		experienceMultSlider.setMaximum(10);
		experienceMultSlider.setMinimum(-5);
		experienceMultSlider.setValue(0);
		// TODO the false value (the slider's) should not be visible, only the real one should
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.gridheight = 2;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		jPanel8.add(experienceMultSlider, gridBagConstraints);
		experienceMultSlider.addChangeListener(new ChangeListener()
		{

			@Override
			public void stateChanged(ChangeEvent e)
			{
				double realValue = getSliderRealValue();

				if (CoreUtility.doublesEqual(realValue, 0.5))
				{
					getExperienceMultNameLabel().setText(LanguageBundle.getString("in_plugin_xp_half")); //$NON-NLS-1$
				}
				else if (realValue <= 0.7)
				{
					getExperienceMultNameLabel().setText(LanguageBundle.getString("in_plugin_xp_easier")); //$NON-NLS-1$
				}
				else if ((realValue > 0.7) && (realValue < 1.5))
				{
					getExperienceMultNameLabel().setText(LanguageBundle.getString("in_plugin_xp_normal")); //$NON-NLS-1$
				}
				else if (realValue >= 1.5)
				{
					getExperienceMultNameLabel().setText(LanguageBundle.getString("in_plugin_xp_harder")); //$NON-NLS-1$
				}

				if (CoreUtility.doublesEqual(realValue, 2))
				{
					getExperienceMultNameLabel().setText(LanguageBundle.getString("in_plugin_xp_twice")); //$NON-NLS-1$
				}

				getExperienceMultLabel().setText(LanguageBundle.getPrettyMultiplier(realValue));

				model.setMultiplier(realValue);
			}
		});

		addExperienceToPartyButton.setText(LanguageBundle.getString("in_plugin_xp_addXpToParty")); //$NON-NLS-1$
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		jPanel8.add(addExperienceToPartyButton, gridBagConstraints);

		experienceMultLabel.setText(LanguageBundle.getPrettyMultiplier(1.0d));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		jPanel8.add(experienceMultLabel, gridBagConstraints);

		adjustCRButton.setText(LanguageBundle.getString("in_plugin_xp_adjustCr")); //$NON-NLS-1$
		adjustCRButton.setEnabled(false);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 5;
		gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 0);
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		jPanel8.add(adjustCRButton, gridBagConstraints);

		addEnemyButton.setText(LanguageBundle.getString("in_plugin_xp_addEnemy")); //$NON-NLS-1$
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 6;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 0);
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		jPanel8.add(addEnemyButton, gridBagConstraints);

		removeEnemyButton.setText(LanguageBundle.getString("in_plugin_xp_removeEnemy")); //$NON-NLS-1$
		removeEnemyButton.setEnabled(false);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 5;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.insets = new java.awt.Insets(12, BORDER_SIZE, 0, 0);
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		jPanel8.add(removeEnemyButton, gridBagConstraints);
		// Update buttons on selection change
		enemyList.addListSelectionListener(new ListSelectionListener()
		{

			@Override
			public void valueChanged(ListSelectionEvent e)
			{
				if (!e.getValueIsAdjusting())
				{
					adjustCRButton.setEnabled(!enemyList.isSelectionEmpty());
					removeEnemyButton.setEnabled(!enemyList.isSelectionEmpty());
				}
			}
		});

		jPanel8.setBorder(BorderFactory.createEmptyBorder(0, BORDER_SIZE, 0, 0));
		jPanel6.add(jPanel8);

		add(jPanel6);
	}

	private double calculateRealValue(int i)
	{
		return 1.0 + (i * 0.1);
	}

	public double getSliderRealValue()
	{
		return calculateRealValue(getExperienceMultSlider().getValue());
	}
}
