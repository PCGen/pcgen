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
 */
 package plugin.encounter.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;

import javax.swing.AbstractListModel;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import pcgen.gui2.tools.Icons;
import pcgen.system.LanguageBundle;

/**
 * View part (in MVC model) of the encounter plugin, a JPanel.
 * 
 */
public class EncounterView extends JPanel
{
	private static final long serialVersionUID = 6127095703012846620L;

	private JButton addCreature;
	private JButton generateEncounter;
	private JButton removeCreature;
	private JButton transferToTracker;
	private JComboBox environment;
	private JLabel encounterLevel;
	private JLabel jLabel3;

	private JLabel jLabel4;
	private JLabel numberLabel;
	private JLabel targetLabel;
	private JList encounterCreatures;
	private JList libraryCreatures;
	private JPanel jPanel1;
	private JPanel jPanel2;
	private JPanel jPanel3;
	private JPanel jPanel4;
	private JPanel jPanel5;
	private JPanel jPanel6;
	private JScrollPane jScrollPane1;
	private JScrollPane jScrollPane2;
	private JTextField numberOfCreatures;
	private JTextField targetEncounterLevel;

	/** Creates new form EnvounterView */
	public EncounterView()
	{
		initComponents();
	}

	/**
	 * Get add creature
	 * @return addCreature
	 */
	public JButton getAddCreature()
	{
		return addCreature;
	}

	/**
	 * getEncounterCreatures
	 * @return encounterCreatures
	 */
	public JList getEncounterCreatures()
	{
		return encounterCreatures;
	}

	/**
	 * Set encounter Model
	 * @param model
	 */
	public void setEncounterModel(DefaultListModel model)
	{
		encounterCreatures.setModel(model);
	}

	/**
	 * Get environment
	 * @return environment
	 */
	public JComboBox getEnvironment()
	{
		return environment;
	}

	/**
	 * Get generate encounter
	 * @return generate encounter
	 */
	public JButton getGenerateEncounter()
	{
		return generateEncounter;
	}

	/**
	 * Get library creatures
	 * @return library creatures
	 */
	public JList getLibraryCreatures()
	{
		return libraryCreatures;
	}

	/**
	 * Set library model
	 * @param model
	 */
	public void setLibraryModel(AbstractListModel model)
	{
		libraryCreatures.setModel(model);
	}

	/**
	 * Get number label
	 * @return number label
	 */
	public JLabel getNumberLabel()
	{
		return numberLabel;
	}

	/**
	 * Get number of creatures
	 * @return number of creatures
	 */
	public JTextField getNumberOfCreatures()
	{
		return numberOfCreatures;
	}

	/**
	 * Get number of creatures text
	 * @return number of creatures text
	 */
	public String getNumberOfCreaturesText()
	{
		return numberOfCreatures.getText();
	}

	/**
	 * Get remove creature
	 * @return remove creature
	 */
	public JButton getRemoveCreature()
	{
		return removeCreature;
	}

	/**
	 * Get target EL
	 * @return target EL
	 */
	public String getTargetEL()
	{
		return targetEncounterLevel.getText();
	}

	/**
	 * Get target encounter level
	 * @return target encounter level
	 */
	public JTextField getTargetEncounterLevel()
	{
		return targetEncounterLevel;
	}

	/**
	 * Get target label
	 * @return target label
	 */
	public JLabel getTargetLabel()
	{
		return targetLabel;
	}

	/**
	 * Sets the totalEncounterLevel.
	 * @param totalEncounterLevel The totalEncounterLevel to set
	 */
	public void setTotalEncounterLevel(String totalEncounterLevel)
	{
		this.encounterLevel.setText(totalEncounterLevel);
	}

	/**
	 * Get transfer to tracker
	 * @return transfer to tracker
	 */
	public JButton getTransferToTracker()
	{
		return transferToTracker;
	}

	private void generateEncounterActionPerformed(ActionEvent evt)
	{
		// Add your handling code here:
	}


	/** This method is called from within the constructor to
	 * initialize the form.
	 */
	private void initComponents()
	{
		GridBagConstraints gridBagConstraints;

		jPanel1 = new JPanel();
		targetLabel = new JLabel();
		targetEncounterLevel = new JTextField();
		numberLabel = new JLabel();
		numberOfCreatures = new JTextField();
		jLabel3 = new JLabel();
		environment = new JComboBox();
		generateEncounter = new JButton();
		transferToTracker = new JButton();
		jPanel4 = new JPanel();
		jPanel2 = new JPanel();
		jScrollPane1 = new JScrollPane();
		libraryCreatures = new JList();
		jPanel6 = new JPanel();
		jPanel3 = new JPanel();
		addCreature = new JButton();
		removeCreature = new JButton();
		jScrollPane2 = new JScrollPane();
		encounterCreatures = new JList();
		jPanel5 = new JPanel();
		jLabel4 = new JLabel();
		encounterLevel = new JLabel();

		setLayout(new BorderLayout());

		jPanel1.setLayout(new GridBagLayout());

		targetLabel.setText(LanguageBundle.getString("in_plugin_encounter_targetEL")); //$NON-NLS-1$
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		jPanel1.add(targetLabel, gridBagConstraints);

		targetEncounterLevel.setText(Integer.toString(1));
		targetEncounterLevel
			.addActionListener(this::targetEncounterLevelActionPerformed);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints.insets = new Insets(0, 0, 5, 0);
		jPanel1.add(targetEncounterLevel, gridBagConstraints);

		numberLabel.setText(LanguageBundle.getString("in_plugin_encounter_numbercreature")); //$NON-NLS-1$
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		jPanel1.add(numberLabel, gridBagConstraints);

		numberOfCreatures.setText(Integer.toString(0));
		numberOfCreatures.addActionListener(this::numberOfCreaturesActionPerformed);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints.insets = new Insets(0, 0, 5, 0);
		jPanel1.add(numberOfCreatures, gridBagConstraints);

		jLabel3.setText(LanguageBundle.getString("in_plugin_encounter_environment")); //$NON-NLS-1$
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 4;
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		jPanel1.add(jLabel3, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 5;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints.insets = new Insets(0, 0, 5, 0);
		jPanel1.add(environment, gridBagConstraints);

		generateEncounter.setText(LanguageBundle.getString("in_plugin_encounter_newEncounter")); //$NON-NLS-1$
		generateEncounter.addActionListener(this::generateEncounterActionPerformed);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 6;
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints.insets = new Insets(0, 0, 5, 0);
		jPanel1.add(generateEncounter, gridBagConstraints);

		transferToTracker.setText(LanguageBundle.getString("in_plugin_encounter_beginCombat")); //$NON-NLS-1$
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 7;
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		jPanel1.add(transferToTracker, gridBagConstraints);

		add(jPanel1, BorderLayout.EAST);

		jPanel4.setLayout(new BorderLayout());

		jPanel2.setLayout(new GridLayout(1, 0));

		jScrollPane1.setViewportView(libraryCreatures);

		jPanel2.add(jScrollPane1);

		jPanel6.setLayout(new BoxLayout(jPanel6,
			BoxLayout.X_AXIS));

		jPanel3.setLayout(new BoxLayout(jPanel3,
			BoxLayout.Y_AXIS));

		addCreature.setIcon(Icons.Forward16.getImageIcon());
		addCreature.setMaximumSize(new Dimension(50, 50));
		addCreature.setMinimumSize(new Dimension(50, 50));
		jPanel3.add(addCreature);

		removeCreature.setIcon(Icons.Back16.getImageIcon());
		removeCreature.setMaximumSize(new Dimension(50, 50));
		removeCreature.setMinimumSize(new Dimension(50, 50));
		jPanel3.add(removeCreature);

		jPanel6.add(jPanel3);

		jScrollPane2.setViewportView(encounterCreatures);

		jPanel6.add(jScrollPane2);

		jPanel2.add(jPanel6);

		jPanel4.add(jPanel2, BorderLayout.CENTER);

		jPanel5.setLayout(new FlowLayout(FlowLayout.RIGHT));

		jLabel4.setText(LanguageBundle.getString("in_plugin_encounter_totalEL")); //$NON-NLS-1$
		jPanel5.add(jLabel4);

		encounterLevel.setText(Integer.toString(0));
		jPanel5.add(encounterLevel);

		jPanel4.add(jPanel5, BorderLayout.SOUTH);

		add(jPanel4, BorderLayout.CENTER);
	}

	private void numberOfCreaturesActionPerformed(ActionEvent evt) {
		// Add your handling code here:
	}

	private void targetEncounterLevelActionPerformed(ActionEvent evt) {
		// Add your handling code here:
	}
}
