/*
 *  Copyright (C) 2002 Devon Jones
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
 *  AddDefeatedCombatant.java
 */
package plugin.experience.gui;

import java.awt.Insets;
import java.text.MessageFormat;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import pcgen.core.SettingsHandler;
import pcgen.gui2.dialog.AbstractDialog;
import plugin.experience.DefeatedCombatant;
import plugin.experience.ExperienceAdjusterModel;
import plugin.experience.ExperienceAdjusterPlugin;
import plugin.experience.ExperienceListItem;


public class AddDefeatedCombatant extends AbstractDialog
{
	// End of variables declaration//GEN-END:variables

	private static final String OPTION_NAME_DBMAXNUM = ExperienceAdjusterPlugin.LOG_NAME + ".dbMaxNum"; //$NON-NLS-1$
	
	/**  Description of the Field */
	public ExperienceAdjusterModel model;
	private javax.swing.JLabel lCR;
	private javax.swing.JLabel lCR2;
	private javax.swing.JLabel lName;

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private JLabel lNumber;
	private SpinnerNumberModel sNumber;
	private javax.swing.JTextField tCR;
	private javax.swing.JTextField tName;
	private JSpinner tNumber;

	/**
	 *  Creates new dialog for Adding a new Combatant This constructor is used if
	 *  you know what frame you are launching from
	 *
	 *@param  parent      Description of the Parameter
	 *@param  modal       Description of the Parameter
	 *@param  model  Description of the Parameter
	 */
	public AddDefeatedCombatant(java.awt.Frame parent, boolean modal,
		ExperienceAdjusterModel model)
	{
		super(parent, "Add defeated combatant", modal);
		pack();
		setLocation(parent.getX() + 100, parent.getY() + 100);
		this.model = model;

		sNumber.setMinimum(1);
		int maxNum =
				SettingsHandler.getGMGenOption(
					OPTION_NAME_DBMAXNUM, 20);
		sNumber.setMaximum(maxNum);
	}

	@Override
	public void applyButtonActionPerformed()
	{
		// If we are creating multiple guys, loop
		for (int i = 1; i <= sNumber.getNumber().intValue(); i++)
		{
			String enemyName;
			if (tName.getText().isEmpty())
			{
				enemyName = "Defeated Enemy";
			}
			else
			{
				enemyName = tName.getText();
			}

			if (sNumber.getNumber().intValue() > 1)
			{
				enemyName = MessageFormat.format("{0} ({1})", enemyName, i);
			}
			model.addEnemy(new ExperienceListItem(new DefeatedCombatant(
				enemyName, checkCRField(tCR, 0))));
		}
	}

	private float checkCRField(javax.swing.JTextField tf, float def)
	{
		float value;

		try
		{
			value = Float.parseFloat(tf.getText());

			/*
			 * CONSIDER What is this trying to do, and is there a clear way 
			 * to perform that action?  Is this a Math.floor? - thpr 10/21/06
			 */
			if (value > 1.0)
			{
				value = ((int) value / 1);
			}

			return value;
		}
		catch (NumberFormatException e)
		{
			tf.setText(Float.toString(def));

			return def;
		}
	}

	//GEN-LAST:event_bSaveActionPerformed

	/**
	 *  This method is called from within the constructor to initialize the form.
	 */
    @Override
	protected JComponent getCenter()
	{
		JPanel panel = new JPanel();

		java.awt.GridBagConstraints gridBagConstraints;

		lName = new javax.swing.JLabel();
		tName = new javax.swing.JTextField();
		lNumber = new javax.swing.JLabel();
		sNumber = new SpinnerNumberModel(1, 1, 20, 1);
		tNumber = new JSpinner(sNumber);
		lCR = new javax.swing.JLabel();
		lCR2 = new javax.swing.JLabel();
		tCR = new javax.swing.JTextField();

		panel.setLayout(new java.awt.GridBagLayout());

		addWindowListener(new java.awt.event.WindowAdapter()
		{
            @Override
			public void windowClosing(java.awt.event.WindowEvent evt)
			{
				close();
			}
		});

		lCR.setText("CR: ");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
		gridBagConstraints.insets = new Insets(GAP, GAP, 0, 0);
		panel.add(lCR, gridBagConstraints);

		lNumber.setText("Number: ");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
		gridBagConstraints.insets = new Insets(GAP, GAP, 0, 0);
		panel.add(lNumber, gridBagConstraints);

		lName.setText("Name: ");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.insets = new Insets(GAP, GAP, 0, 0);
		gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
		panel.add(lName, gridBagConstraints);

		lCR2.setText("(Note, use decimal fractions for CR. Ex: .5 = 1/2)");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.insets = new Insets(0, GAP, 0, GAP);
		gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
		panel.add(lCR2, gridBagConstraints);

		// TODO tCR should be internationalized
		tCR.setText("1.0");
		tCR.addActionListener(this::tCRActionPerformed);

		tCR.addFocusListener(new java.awt.event.FocusAdapter()
		{
            @Override
			public void focusLost(java.awt.event.FocusEvent evt)
			{
				tCRFocusLost(evt);
			}
		});

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		tCR.setColumns(5);
		gridBagConstraints.insets = new Insets(GAP, 0, 0, GAP);
		gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
		panel.add(tCR, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.insets = new Insets(GAP, 0, 0, GAP);
		gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
		panel.add(tNumber, gridBagConstraints);
		
		tName.setColumns(20);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.insets = new Insets(GAP, 0, 0, GAP);
		gridBagConstraints.weightx = 1;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
		panel.add(tName, gridBagConstraints);
		
		return panel;
	}

	//GEN-END:initComponents


	private void tCRActionPerformed(java.awt.event.ActionEvent evt)
	{ //GEN-FIRST:event_tCRActionPerformed
		tCR.setText(Float.toString(checkCRField(tCR, 1)));
	}

	//GEN-LAST:event_tCRActionPerformed

	private void tCRFocusLost(java.awt.event.FocusEvent evt)
	{ //GEN-FIRST:event_tCRFocusLost
		tCR.setText(Float.toString(checkCRField(tCR, 1)));
	}

    @Override
	protected String getOkMnKey()
	{
		return "in_mn_add"; //$NON-NLS-1$
	}

    @Override
	protected String getOkKey()
	{
		return "in_add"; //$NON-NLS-1$
	}
}
