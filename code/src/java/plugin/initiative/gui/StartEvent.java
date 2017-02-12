/*
 *  Initiative - A role playing utility to track turns
 *  Copyright (C) 2002 Devon D Jones
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 *  StartEvent.java
 *
 *  Created on January 16, 2002, 3:08 PM
 */
package plugin.initiative.gui;

import gmgen.plugin.Event;
import pcgen.core.SettingsHandler;
import plugin.initiative.InitiativePlugin;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 *@author     devon
 */
public class StartEvent extends JDialog
{

	/**  The initiative panel */
	protected JPanel mainPanel;
	/** initiative */
	public Initiative initiative;
	protected JButton bCancel;
	protected JButton bSave;
	protected JCheckBox cbAlert;
	protected JLabel nameLabel;
	protected JLabel playerLabel;
	protected JLabel durationLabel;

	protected JLabel effectLabel;
	protected JLabel initiativeLabel;
	protected JSlider sDuration;
	protected JSlider sInit;
	protected JFormattedTextField lDuration;
	protected JFormattedTextField lInit;
	protected JTextField tEffect;
	protected JTextField tName;
	protected JTextField tPlayer;

	protected String sTitle = "Start Event Timer";
	protected String sAlertLabel = "Alert when event Completes/Occurs";

	protected int gridBagRow = 0;

	/**
	 *  Creates new form CastSpell - used when you do know who your frame is
	 *
	 *@param  parent      Parent form
	 *@param  modal       is modal?
	 *@param  initiative  Initiative panel
	 */
	public StartEvent(Frame parent, boolean modal,
		Initiative initiative)
	{
		super(parent, modal);
		initComponents();
		this.initiative = initiative;
		initCheckBox();
	}

	/**
	 *  Constructor for the CastSpell object - used when you know who your frame is
	 *  and you want to cast the spell for a particular player
	 *
	 *@param  parent      Parent form
	 *@param  modal       is modal
	 *@param  initiative  Initiative panel
	 *@param  player      player name
	 *@param  init        player's initiative
	 */
	public StartEvent(Frame parent, boolean modal,
		Initiative initiative, String player, int init)
	{
		super(parent, modal);
		initComponents();
		this.initiative = initiative;
		tName.grabFocus();
		tPlayer.setText(player);
		lInit.setValue(init);
		initCheckBox();
	}

	/**
	 *
	 * <p>Initializes the alert checkbox based on the options.</p>
	 *
	 */
	public void initCheckBox()
	{
		boolean box =
				SettingsHandler.getGMGenOption(InitiativePlugin.LOG_NAME
					+ ".ShowEvents", true);

		if (box)
		{
			cbAlert.setSelected(false);
		}
		else
		{
			cbAlert.setSelected(true);
		}
	}

	/**
	 *
	 * <p>Handles a cancel-button press.</p>
	 * @param e
	 */
	protected void cancelAndClose(ActionEvent e)
	{
		setVisible(false);
		dispose();
	}

	/**
	 *
	 * <p>
	 * Handles a save button press; closes dialog and saves the event
	 * to the log and initiative list.
	 * </p>
	 * @param e
	 */
	protected void saveAndClose(ActionEvent e)
	{
		save();
	}

	protected void save()
	{
		initiative.initList.add(new Event(tName.getText(), tPlayer.getText(),
			tEffect.getText(), ((Integer) lDuration.getValue()).intValue(),
			((Integer) lInit.getValue()).intValue(), cbAlert.isSelected()));
		initiative.writeToCombatTabWithRound(" Event Timer " + tName.getText()
			+ " Started");
		initiative.refreshTable();
		initiative.grabFocus();
		initiative.focusNextInit();
		setVisible(false);
		dispose();
	}

	/**
	 *  Closes the dialog
	 *
	 *@param  evt  close event
	 */
	protected void closeDialog(WindowEvent evt)
	{
		setVisible(false);
		dispose();
	}

	/**
	 *
	 * <p>Initializes the components.</p>
	 *
	 */
	protected void initComponents()
	{
		basicSetup();

		initAllDefaultComponents();

		addButtons();

		finalizeSetup();
	}

	protected void initAllDefaultComponents()
	{
		addName();
		addPlayer();
		addEffect();
		addDuration();
		addInitiative();
		addAlert();
	}

	protected void finalizeSetup()
	{
		pack();
		setLocationRelativeTo(getOwner());
	}

	protected void addButtons()
	{
		bSave = new JButton();
		bCancel = new JButton();

		GridBagConstraints gridBagConstraints;
		//Buttons
		bSave.setText("Save");
		bSave.addActionListener(this::saveAndClose);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = gridBagRow;
		//gridBagConstraints.insets = new java.awt.Insets(0, 5, 10, 0);
		mainPanel.add(bSave, gridBagConstraints);

		bCancel.setText("Cancel");
		bCancel.addActionListener(this::cancelAndClose);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = gridBagRow;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.anchor = GridBagConstraints.NORTHEAST;
		mainPanel.add(bCancel, gridBagConstraints);
		gridBagRow++;
	}

	protected void addEffect()
	{
		effectLabel = new JLabel();
		tEffect = new JTextField();

		GridBagConstraints gridBagConstraints;
		effectLabel.setText("Effect");
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = gridBagRow;
		gridBagConstraints.insets = new Insets(0, 0, 10, 0);
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		mainPanel.add(effectLabel, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = gridBagRow;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		mainPanel.add(tEffect, gridBagConstraints);

		tEffect.addKeyListener(new EnterKeyAdapter());
		gridBagRow++;
	}

	protected void addAlert()
	{
		cbAlert = new JCheckBox();

		GridBagConstraints gridBagConstraints;
		cbAlert.setText(sAlertLabel);
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = gridBagRow;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.insets = new Insets(0, 0, 10, 0);
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		mainPanel.add(cbAlert, gridBagConstraints);

		cbAlert.addKeyListener(new EnterKeyAdapter());
		gridBagRow++;
	}

	protected void addInitiative()
	{
		initiativeLabel = new JLabel();
		sInit = Utils.buildSlider(1, 50);
		lInit = Utils.buildIntegerFieldWithSlider(sInit);
		lInit.setValue(1);

		GridBagConstraints gridBagConstraints;
		initiativeLabel.setText("Initiative");
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = gridBagRow;
		gridBagConstraints.insets = new Insets(0, 0, 5, 0);
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		mainPanel.add(initiativeLabel, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = gridBagRow;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = GridBagConstraints.NORTH;
		mainPanel.add(sInit, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = gridBagRow;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		mainPanel.add(lInit, gridBagConstraints);
		lInit.setMinimumSize(new Dimension(lInit.getPreferredSize().width,
			lInit.getPreferredSize().height));

		lInit.addKeyListener(new EnterKeyAdapter());
		gridBagRow++;
	}

	protected void addDuration()
	{
		durationLabel = new JLabel();
		sDuration = Utils.buildSlider(1, 50);
		lDuration = Utils.buildIntegerFieldWithSlider(sDuration);
		lDuration.setValue(1);

		GridBagConstraints gridBagConstraints;
		durationLabel.setText("Duration");
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = gridBagRow;
		gridBagConstraints.insets = new Insets(0, 0, 10, 0);
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		mainPanel.add(durationLabel, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = gridBagRow;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.ipadx = -80;
		gridBagConstraints.ipady = 4;
		gridBagConstraints.anchor = GridBagConstraints.NORTH;
		mainPanel.add(sDuration, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = gridBagRow;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		mainPanel.add(lDuration, gridBagConstraints);
		lDuration.setMinimumSize(new Dimension(
			lDuration.getPreferredSize().width,
			lDuration.getPreferredSize().height));

		lDuration.addKeyListener(new EnterKeyAdapter());
		gridBagRow++;
	}

	protected void addPlayer()
	{
		playerLabel = new JLabel();
		tPlayer = new JTextField();

		GridBagConstraints gridBagConstraints;
		playerLabel.setText("Player");
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = gridBagRow;
		gridBagConstraints.insets = new Insets(0, 0, 10, 0);
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		mainPanel.add(playerLabel, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = gridBagRow;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.ipadx = 37;
		gridBagConstraints.anchor = GridBagConstraints.NORTH;
		mainPanel.add(tPlayer, gridBagConstraints);

		tPlayer.addKeyListener(new EnterKeyAdapter());
		gridBagRow++;
	}

	protected void basicSetup()
	{
		mainPanel = new JPanel(new GridBagLayout());
		mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(mainPanel, BorderLayout.CENTER);

		setTitle(sTitle);

		addWindowListener(new WindowAdapter()
		{
            @Override
			public void windowClosing(WindowEvent evt)
			{
				closeDialog(evt);
			}
		});
	}

	protected void addName()
	{
		nameLabel = new JLabel();
		tName = new JTextField();

		GridBagConstraints gridBagConstraints;
		nameLabel.setText("Name");
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = gridBagRow;
		gridBagConstraints.insets = new Insets(0, 0, 10, 0);
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		mainPanel.add(nameLabel, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = gridBagRow;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.ipadx = 37;
		gridBagConstraints.insets = new Insets(0, 0, 0, 0);
		gridBagConstraints.anchor = GridBagConstraints.NORTH;
		mainPanel.add(tName, gridBagConstraints);

		tName.addKeyListener(new EnterKeyAdapter());

		gridBagRow++;
	}

	protected class EnterKeyAdapter extends KeyAdapter
	{
        @Override
		public void keyReleased(KeyEvent evt)
		{
			if (evt.getKeyCode() == KeyEvent.VK_ENTER)
			{
				save();
			}
		}
	}
}
