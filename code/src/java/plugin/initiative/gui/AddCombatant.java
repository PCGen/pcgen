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
 *  AddCombatant.java
 *
 */
package plugin.initiative.gui;

import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;

import pcgen.core.SettingsHandler;
import pcgen.gui2.dialog.AbstractDialog;
import pcgen.util.Logging;
import plugin.initiative.InitiativePlugin;
import plugin.initiative.XMLCombatant;

/**
 * <p>
 * A dialog which adds a combatant to the initiative tracker.  It
 * allows the user to enter the basic information to construct a new
 * {@code XMLCombatant}; Selecting the "Save" action constructs
 * the combatant(s) and saves them to the initiative model.
 * </p>
 *
 *
 */
public class AddCombatant extends AbstractDialog
{
	/** The initiative component */
	public Initiative initiative;

	private javax.swing.JComboBox typeCombo;

	private javax.swing.JLabel nameLabel;
	private javax.swing.JLabel conLabel;
	private javax.swing.JLabel intLabel;
	private javax.swing.JLabel wisLabel;
	private javax.swing.JLabel chaLabel;
	private javax.swing.JLabel fortLabel;
	private javax.swing.JLabel reflexLabel;
	private javax.swing.JLabel willLabel;
	private javax.swing.JLabel crLabel;
	private javax.swing.JLabel noteLabel;
	private javax.swing.JLabel playerLabel;
	private javax.swing.JLabel bonusLabel;
	private javax.swing.JLabel numberLabel;
	private javax.swing.JLabel neg20Label;
	private javax.swing.JLabel neg20Label2;
	private javax.swing.JLabel hpLabel;
	private javax.swing.JLabel typeLabel;
	private javax.swing.JLabel strLabel;
	private javax.swing.JLabel dexLabel;

	private javax.swing.JSeparator jSeparator1;

	private javax.swing.JSlider bonusSlider;
	private javax.swing.JSlider hpSlider;
	private javax.swing.JSlider numberSlider;

	private javax.swing.JTextField nameField;
	private javax.swing.JTextField playerField;

	private JFormattedTextField crField;
	private JFormattedTextField strField;
	private JFormattedTextField dexField;
	private JFormattedTextField conField;
	private JFormattedTextField intField;
	private JFormattedTextField wisField;
	private JFormattedTextField chaField;
	private JFormattedTextField fortitudeField;
	private JFormattedTextField reflexField;
	private JFormattedTextField willField;
	private JFormattedTextField bonusField;
	private JFormattedTextField hpField;
	private JFormattedTextField numberField;

	/**
	 * <p>Creates new dialog for Adding a new Combatant This contructor is used if
	 * you know what frame you are launching from</p>
	 *
	 * @param  parent      Parent frame
	 * @param  modal       Is the dialog modal
	 * @param  initiative  The initiative tracker reference.
	 */
	public AddCombatant(java.awt.Frame parent, boolean modal,
		Initiative initiative)
	{
		super(parent, "no title?", modal);
		initDropDown();
		// XXX: why those values? why not center?
		setLocation(parent.getX() + 100, parent.getY() + 100);
		this.initiative = initiative;

		boolean bHP =
				SettingsHandler.getGMGenOption(InitiativePlugin.LOG_NAME
					+ ".doHP", true);

		hpSlider.setEnabled(bHP);

		int maxHp =
				SettingsHandler.getGMGenOption(InitiativePlugin.LOG_NAME
					+ ".dbMaxHP", 100);

		int maxNum =
				SettingsHandler.getGMGenOption(InitiativePlugin.LOG_NAME
					+ ".dbMaxNum", 20);

		numberSlider.setMaximum(maxNum);
		hpSlider.setMaximum(maxHp);
		pack();
	}

	protected void applyButtonActionPerformed()
	{
		String comString = (String) typeCombo.getSelectedItem();

		if (comString.equals("Non Combatant"))
		{
			comString = "-";
		}

		// If we are creating multiple guys, loop
		if (getIntegerValue(numberField, 1) > 1)
		{
			for (int i = 1; i <= getIntegerValue(numberField, 1); i++)
			{
				XMLCombatant xmlcbt =
						new XMLCombatant(nameField.getText() + " (" + i + ")",
							playerField.getText(),
							getIntegerValue(strField, 10), getIntegerValue(
								dexField, 10), getIntegerValue(conField, 10),
							getIntegerValue(intField, 10), getIntegerValue(
								wisField, 10), getIntegerValue(chaField, 10),
							getIntegerValue(fortitudeField, 0),
							getIntegerValue(reflexField, 0), getIntegerValue(
								willField, 0), getIntegerValue(hpField, 1),
							getIntegerValue(hpField, 1), 0, getIntegerValue(
								bonusField, 0), comString, getFloatValue(
								crField, 1), 0);
				initiative.initList.add(xmlcbt);
				initiative.addTab(xmlcbt);
			}
		}

		// If if not, just add one
		else
		{
			XMLCombatant xmlcbt =
					new XMLCombatant(nameField.getText(),
						playerField.getText(), getIntegerValue(strField, 10),
						getIntegerValue(dexField, 10), getIntegerValue(
							conField, 10), getIntegerValue(intField, 10),
						getIntegerValue(wisField, 10), getIntegerValue(
							chaField, 10), getIntegerValue(fortitudeField, 0),
						getIntegerValue(reflexField, 0), getIntegerValue(
							willField, 0), getIntegerValue(hpField, 1),
						getIntegerValue(hpField, 1), 0, getIntegerValue(
							bonusField, 0), comString,
						getFloatValue(crField, 1), 0);
			initiative.initList.add(xmlcbt);
			initiative.addTab(xmlcbt);
		}

		initiative.refreshTable();
		initiative.focusRoll();
	}

	/**
	 * <p>
	 * Initializes all the components of the dialog.
	 * </p>
	 */
	protected JComponent getCenter()
	{
		JPanel center = new JPanel();

		java.awt.GridBagConstraints gridBagConstraints;

		bonusSlider = Utils.buildSlider(-20, 20);
		numberSlider = Utils.buildSlider(1, 20);
		hpSlider = Utils.buildSlider(1, 100, 5, 25);

		typeCombo = new javax.swing.JComboBox();

		jSeparator1 = new javax.swing.JSeparator();

		nameLabel = new javax.swing.JLabel();
		playerLabel = new javax.swing.JLabel();
		bonusLabel = new javax.swing.JLabel();
		numberLabel = new javax.swing.JLabel();
		neg20Label = new javax.swing.JLabel();
		hpLabel = new javax.swing.JLabel();
		typeLabel = new javax.swing.JLabel();
		strLabel = new javax.swing.JLabel();
		dexLabel = new javax.swing.JLabel();
		conLabel = new javax.swing.JLabel();
		intLabel = new javax.swing.JLabel();
		wisLabel = new javax.swing.JLabel();
		chaLabel = new javax.swing.JLabel();
		fortLabel = new javax.swing.JLabel();
		reflexLabel = new javax.swing.JLabel();
		willLabel = new javax.swing.JLabel();
		neg20Label2 = new javax.swing.JLabel();
		crLabel = new javax.swing.JLabel();
		noteLabel = new javax.swing.JLabel();

		nameField = new javax.swing.JTextField();
		playerField = new javax.swing.JTextField();

		crField = Utils.buildFloatField(-10, 50);
		conField = Utils.buildIntegerField(0, 100);
		strField = Utils.buildIntegerField(0, 100);
		dexField = Utils.buildIntegerField(0, 100);
		intField = Utils.buildIntegerField(0, 100);
		wisField = Utils.buildIntegerField(0, 100);
		chaField = Utils.buildIntegerField(0, 100);
		fortitudeField = Utils.buildIntegerField(-20, 50);
		reflexField = Utils.buildIntegerField(-20, 50);
		willField = Utils.buildIntegerField(-20, 50);

		bonusField = Utils.buildIntegerFieldWithSlider(bonusSlider);
		hpField = Utils.buildIntegerFieldWithSlider(hpSlider);
		numberField = Utils.buildIntegerFieldWithSlider(numberSlider);

		center.setLayout(new java.awt.GridBagLayout());

		nameLabel.setText("Name");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.insets = new java.awt.Insets(10, 5, 10, 0);
		center.add(nameLabel, gridBagConstraints);

		nameField.setMinimumSize(new java.awt.Dimension(100, 21));
		nameField.setPreferredSize(new java.awt.Dimension(200, 21));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridwidth = 4;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
		center.add(nameField, gridBagConstraints);

		playerLabel.setText("Player");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.insets = new java.awt.Insets(0, 5, 10, 0);
		center.add(playerLabel, gridBagConstraints);

		playerField.setPreferredSize(new java.awt.Dimension(200, 21));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.gridwidth = 4;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
		center.add(playerField, gridBagConstraints);

		bonusLabel.setText("Bonus");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.insets = new java.awt.Insets(0, 5, 10, 0);
		center.add(bonusLabel, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.gridwidth = 3;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
		center.add(bonusSlider, gridBagConstraints);

		numberLabel.setText("Number");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 4;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.insets = new java.awt.Insets(0, 5, 10, 0);
		center.add(numberLabel, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 4;
		gridBagConstraints.gridwidth = 3;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
		center.add(numberSlider, gridBagConstraints);

		neg20Label.setForeground(new java.awt.Color(204, 204, 204));
		neg20Label.setText("-20");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 3;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
		gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 10);
		center.add(neg20Label, gridBagConstraints);

		hpLabel.setText("Hit Points");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.insets = new java.awt.Insets(0, 5, 10, 0);
		center.add(hpLabel, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.gridwidth = 3;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
		center.add(hpSlider, gridBagConstraints);

		typeLabel.setText("Type");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 5;
		gridBagConstraints.insets = new java.awt.Insets(0, 5, 10, 0);
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		center.add(typeLabel, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 5;
		gridBagConstraints.gridwidth = 4;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
		center.add(typeCombo, gridBagConstraints);

		bonusField.setValue(0);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
		center.add(bonusField, gridBagConstraints);

		hpField.setValue(1);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
		center.add(hpField, gridBagConstraints);

		numberField.setValue(1);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 4;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
		center.add(numberField, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 8;
		gridBagConstraints.gridwidth = 5;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
		center.add(jSeparator1, gridBagConstraints);

		strLabel.setText("STR");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 9;
		gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
		center.add(strLabel, gridBagConstraints);

		dexLabel.setText("DEX");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 10;
		gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
		center.add(dexLabel, gridBagConstraints);

		conLabel.setText("CON");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 11;
		gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
		center.add(conLabel, gridBagConstraints);

		intLabel.setText("INT");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 12;
		gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
		center.add(intLabel, gridBagConstraints);

		wisLabel.setText("WIS");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 13;
		gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
		center.add(wisLabel, gridBagConstraints);

		chaLabel.setText("CHA");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 14;
		gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
		center.add(chaLabel, gridBagConstraints);

		conField.setValue(10);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 11;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
		center.add(conField, gridBagConstraints);

		strField.setValue(10);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 9;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
		center.add(strField, gridBagConstraints);

		dexField.setValue(10);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 10;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
		center.add(dexField, gridBagConstraints);

		intField.setValue(10);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 12;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
		center.add(intField, gridBagConstraints);

		wisField.setValue(10);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 13;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
		center.add(wisField, gridBagConstraints);

		chaField.setValue(10);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 14;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
		center.add(chaField, gridBagConstraints);

		fortLabel.setText("Fort");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 9;
		gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
		center.add(fortLabel, gridBagConstraints);

		reflexLabel.setText("Reflex");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 10;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new java.awt.Insets(0, 30, 0, 5);
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
		center.add(reflexLabel, gridBagConstraints);

		willLabel.setText("Will");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 11;
		gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
		center.add(willLabel, gridBagConstraints);

		fortitudeField.setValue(0);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 3;
		gridBagConstraints.gridy = 9;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
		center.add(fortitudeField, gridBagConstraints);

		reflexField.setValue(0);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 3;
		gridBagConstraints.gridy = 10;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
		center.add(reflexField, gridBagConstraints);

		willField.setValue(0);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 3;
		gridBagConstraints.gridy = 11;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
		center.add(willField, gridBagConstraints);

		neg20Label2.setForeground(new java.awt.Color(204, 204, 204));
		neg20Label2.setText("-20");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
		gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 10);
		center.add(neg20Label2, gridBagConstraints);

		crLabel.setText("CR");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 6;
		gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 0);
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
		center.add(crLabel, gridBagConstraints);

		noteLabel
			.setText("(Note, use decimal fractions for CR less than 1. Ex: .5 = 1/2)");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 7;
		gridBagConstraints.gridwidth = 5;
		gridBagConstraints.insets = new java.awt.Insets(0, 5, 10, 0);
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
		center.add(noteLabel, gridBagConstraints);

		crField.setText("1");

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 6;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
		center.add(crField, gridBagConstraints);

		return center;
	}

	/**
	 * <p>
	 * Initializes the list entries for the cType combo box.
	 * </p>
	 */
	private void initDropDown()
	{
		java.util.Vector<String> vType = new java.util.Vector<>();
		vType.add("Enemy");
		vType.add("Ally");
		vType.add("PC");
		vType.add("Non Combatant");

		javax.swing.DefaultComboBoxModel typeModel =
				new javax.swing.DefaultComboBoxModel(vType);
		typeCombo.setModel(typeModel);
	}

	/**
	 * <p>Returns the integer value of the given field</p>
	 * @param field A {@code JFormattedTextField} with an <code>Integer</code> value
	 * @param defaultValue
	 * @return int
	 */
	private int getIntegerValue(JFormattedTextField field, int defaultValue)
	{
		int returnValue = defaultValue;
		if (field.isValid() && field.getValue() instanceof Integer)
		{
			returnValue = ((Integer) field.getValue()).intValue();
		}
		return returnValue;
	}

	/**
	 * <p>Returns the float value of the given field</p>
	 * @param field A {@code JFormattedTextField} with an <code>Float</code> value
	 * @param defaultValue
	 * @return float
	 */
	private float getFloatValue(JFormattedTextField field, float defaultValue)
	{
		float returnValue = defaultValue;
		if (field.isValid() && field.getValue() instanceof Float)
		{
			returnValue = ((Float) field.getValue()).floatValue();
		}
		else
		{
			Logging
				.debugPrint("Was unable to read CR value, using default of ["
					+ defaultValue + "]");
		}
		return returnValue;
	}

}
