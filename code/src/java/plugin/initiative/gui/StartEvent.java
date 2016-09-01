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
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *@author     devon
 *@since    April 7, 2003
 */
class StartEvent extends javax.swing.JDialog
{

	/**  The initiative panel */
	JPanel mainPanel;
	/** initiative */
	Initiative initiative;
	private javax.swing.JButton bCancel;
	private javax.swing.JButton bSave;
	javax.swing.JCheckBox cbAlert;
	private javax.swing.JLabel nameLabel;
	private javax.swing.JLabel playerLabel;
	private javax.swing.JLabel durationLabel;

	private javax.swing.JLabel effectLabel;
	private javax.swing.JLabel initiativeLabel;
	private javax.swing.JSlider sDuration;
	private javax.swing.JSlider sInit;
	JFormattedTextField lDuration;
	JFormattedTextField lInit;
	javax.swing.JTextField tEffect;
	javax.swing.JTextField tName;
	javax.swing.JTextField tPlayer;

	String sTitle = "Start Event Timer";
	String sAlertLabel = "Alert when event Completes/Occurs";

	int gridBagRow = 0;

	/**
	 *  Creates new form CastSpell - used when you do know who your frame is
	 *
	 *@param  parent      Parent form
	 *@param  modal       is modal?
	 *@param  initiative  Initiative panel
	 */
	public StartEvent(java.awt.Frame parent, boolean modal,
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
	public StartEvent(java.awt.Frame parent, boolean modal,
		Initiative initiative, String player, int init)
	{
		super(parent, modal);
		initComponents();
		this.initiative = initiative;
		tName.grabFocus();
		tPlayer.setText(player);
		lInit.setValue(Integer.valueOf(init));
		initCheckBox();
	}

	/**
	 *
	 * <p>Initializes the alert checkbox based on the options.</p>
	 *
	 */
	private void initCheckBox()
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
	private void cancelAndClose(ActionEvent e)
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
	private void saveAndClose(ActionEvent e)
	{
		save();
	}

	void save()
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
	private void closeDialog(java.awt.event.WindowEvent evt)
	{
		setVisible(false);
		dispose();
	}

	/**
	 *
	 * <p>Initializes the components.</p>
	 *
	 */
	void initComponents()
	{
		basicSetup();

		initAllDefaultComponents();

		addButtons();

		finalizeSetup();
	}

	void initAllDefaultComponents()
	{
		addName();
		addPlayer();
		addEffect();
		addDuration();
		addInitiative();
		addAlert();
	}

	void finalizeSetup()
	{
		pack();
		setLocationRelativeTo(getOwner());
	}

	void addButtons()
	{
		bSave = new javax.swing.JButton();
		bCancel = new javax.swing.JButton();

		java.awt.GridBagConstraints gridBagConstraints;
		//Buttons
		bSave.setText("Save");
		bSave.addActionListener(new ActionListener()
		{
            @Override
			public void actionPerformed(ActionEvent e)
			{
				saveAndClose(e);
			}
		});

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = gridBagRow;
		//gridBagConstraints.insets = new java.awt.Insets(0, 5, 10, 0);
		mainPanel.add(bSave, gridBagConstraints);

		bCancel.setText("Cancel");
		bCancel.addActionListener(new ActionListener()
		{
            @Override
			public void actionPerformed(ActionEvent e)
			{
				cancelAndClose(e);
			}
		});

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = gridBagRow;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
		mainPanel.add(bCancel, gridBagConstraints);
		gridBagRow++;
	}

	private void addEffect()
	{
		effectLabel = new javax.swing.JLabel();
		tEffect = new javax.swing.JTextField();

		java.awt.GridBagConstraints gridBagConstraints;
		effectLabel.setText("Effect");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = gridBagRow;
		gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
		mainPanel.add(effectLabel, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = gridBagRow;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
		mainPanel.add(tEffect, gridBagConstraints);

		tEffect.addKeyListener(new EnterKeyAdapter());
		gridBagRow++;
	}

	private void addAlert()
	{
		cbAlert = new javax.swing.JCheckBox();

		java.awt.GridBagConstraints gridBagConstraints;
		cbAlert.setText(sAlertLabel);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = gridBagRow;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
		mainPanel.add(cbAlert, gridBagConstraints);

		cbAlert.addKeyListener(new EnterKeyAdapter());
		gridBagRow++;
	}

	private void addInitiative()
	{
		initiativeLabel = new javax.swing.JLabel();
		sInit = Utils.buildSlider(1, 50);
		lInit = Utils.buildIntegerFieldWithSlider(sInit);
		lInit.setValue(Integer.valueOf(1));

		java.awt.GridBagConstraints gridBagConstraints;
		initiativeLabel.setText("Initiative");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = gridBagRow;
		gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		mainPanel.add(initiativeLabel, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = gridBagRow;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
		mainPanel.add(sInit, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = gridBagRow;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
		mainPanel.add(lInit, gridBagConstraints);
		lInit.setMinimumSize(new Dimension(lInit.getPreferredSize().width,
			lInit.getPreferredSize().height));

		lInit.addKeyListener(new EnterKeyAdapter());
		gridBagRow++;
	}

	private void addDuration()
	{
		durationLabel = new javax.swing.JLabel();
		sDuration = Utils.buildSlider(1, 50);
		lDuration = Utils.buildIntegerFieldWithSlider(sDuration);
		lDuration.setValue(Integer.valueOf(1));

		java.awt.GridBagConstraints gridBagConstraints;
		durationLabel.setText("Duration");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = gridBagRow;
		gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		mainPanel.add(durationLabel, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = gridBagRow;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.ipadx = -80;
		gridBagConstraints.ipady = 4;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
		mainPanel.add(sDuration, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = gridBagRow;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
		mainPanel.add(lDuration, gridBagConstraints);
		lDuration.setMinimumSize(new Dimension(
			lDuration.getPreferredSize().width,
			lDuration.getPreferredSize().height));

		lDuration.addKeyListener(new EnterKeyAdapter());
		gridBagRow++;
	}

	private void addPlayer()
	{
		playerLabel = new javax.swing.JLabel();
		tPlayer = new javax.swing.JTextField();

		java.awt.GridBagConstraints gridBagConstraints;
		playerLabel.setText("Player");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = gridBagRow;
		gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		mainPanel.add(playerLabel, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = gridBagRow;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.ipadx = 37;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
		mainPanel.add(tPlayer, gridBagConstraints);

		tPlayer.addKeyListener(new EnterKeyAdapter());
		gridBagRow++;
	}

	void basicSetup()
	{
		mainPanel = new JPanel(new java.awt.GridBagLayout());
		mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(mainPanel, BorderLayout.CENTER);

		setTitle(sTitle);

		addWindowListener(new java.awt.event.WindowAdapter()
		{
            @Override
			public void windowClosing(java.awt.event.WindowEvent evt)
			{
				closeDialog(evt);
			}
		});
	}

	private void addName()
	{
		nameLabel = new javax.swing.JLabel();
		tName = new javax.swing.JTextField();

		java.awt.GridBagConstraints gridBagConstraints;
		nameLabel.setText("Name");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = gridBagRow;
		gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		mainPanel.add(nameLabel, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = gridBagRow;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.ipadx = 37;
		gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 0);
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
		mainPanel.add(tName, gridBagConstraints);

		tName.addKeyListener(new EnterKeyAdapter());

		gridBagRow++;
	}

	private class EnterKeyAdapter extends java.awt.event.KeyAdapter
	{
        @Override
		public void keyReleased(java.awt.event.KeyEvent evt)
		{
			if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER)
			{
				save();
			}
		}
	}
}
