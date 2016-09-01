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
 *  PreferencesTrackingPanel.java
 *
 *  Created on August 29, 2002, 2:41 PM
 */
package plugin.initiative.gui;

import javax.swing.SwingConstants;
import pcgen.core.SettingsHandler;
import plugin.initiative.InitiativePlugin;

/**
 *  Dialog for editing preferences.
 *
 *@author     devon
 *@since    April 7, 2003
 */
class PreferencesTrackingPanel extends gmgen.gui.PreferencesPanel
{
	// End of variables declaration//GEN-END:variables
	private Initiative initiative;
	private javax.swing.JCheckBox deathCheckBox;
	private javax.swing.JCheckBox hpCheckBox;
	private javax.swing.JCheckBox spellCheckBox;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JLabel jLabel3;

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JLabel jLabel4;
	private javax.swing.JSeparator jSeparator1;
	private javax.swing.JSeparator jSeparator2;

	/**  Creates new form PreferencesTrackingPanel
	 * @param initiative
	 */
	public PreferencesTrackingPanel(Initiative initiative)
	{
		initComponents();
		this.initiative = initiative;
	}

	/**
	 *  Sets the "Track Death" box checked status
	 *
	 *@param  Selected  The new deathCheckBoxChecked value
	 */
	private void setDeathCheckBoxChecked(boolean Selected)
	{
		deathCheckBox.setSelected(Selected);
	}

	/**
	 *  Determines if the "Track Death" box is checked
	 *
	 *@return    The deathCheckBoxChecked value
	 */
	private boolean isDeathCheckBoxChecked()
	{
		return deathCheckBox.isSelected();
	}

	/**
	 *  Sets the "Track Hit Points" box checked status
	 *
	 *@param  Selected  The new hPCheckBoxChecked value
	 */
	private void setHPCheckBoxChecked(boolean Selected)
	{
		hpCheckBox.setSelected(Selected);
	}

	/**
	 *  Determines if the "Track Hit Points" box is checked
	 *
	 *@return    The hPCheckBoxChecked value
	 */
	private boolean isHPCheckBoxChecked()
	{
		return hpCheckBox.isSelected();
	}

	/**
	 *  Sets the "Enable Spells" box checked status
	 *
	 *@param  Selected  The new spellCheckBoxChecked value
	 */
	private void setSpellCheckBoxChecked(boolean Selected)
	{
		spellCheckBox.setSelected(Selected);
	}

	/**
	 *  Determines if the "Enable Spells" box is checked
	 *
	 *@return    The spellCheckBoxChecked value
	 */
	private boolean isSpellCheckBoxChecked()
	{
		return spellCheckBox.isSelected();
	}

    @Override
	public void applyPreferences()
	{
		SettingsHandler.setGMGenOption(InitiativePlugin.LOG_NAME + ".doSpells",
			isSpellCheckBoxChecked());
		SettingsHandler.setGMGenOption(InitiativePlugin.LOG_NAME + ".doDeath",
			isDeathCheckBoxChecked());
		SettingsHandler.setGMGenOption(InitiativePlugin.LOG_NAME + ".doHP",
			isHPCheckBoxChecked());
		initiative.applyPrefs();
		initiative.refreshTable();
	}

    @Override
	public void initPreferences()
	{
		setSpellCheckBoxChecked(SettingsHandler.getGMGenOption(
			InitiativePlugin.LOG_NAME + ".doSpells", true));
		setDeathCheckBoxChecked(SettingsHandler.getGMGenOption(
			InitiativePlugin.LOG_NAME + ".doDeath", true));
		setHPCheckBoxChecked(SettingsHandler.getGMGenOption(
			InitiativePlugin.LOG_NAME + ".doHP", true));
	}

	@Override
	public String toString()
	{
		return "Tracking";
	}

	/**
	 *  This method is called from within the constructor to initialize the form.
	 *  WARNING: Do NOT modify this code. The content of this method is always
	 *  regenerated by the Form Editor.
	 */
	private void initComponents()
	{ //GEN-BEGIN:initComponents

		java.awt.GridBagConstraints gridBagConstraints;

		jLabel1 = new javax.swing.JLabel();
		spellCheckBox = new javax.swing.JCheckBox();
		jSeparator1 = new javax.swing.JSeparator();
		jLabel2 = new javax.swing.JLabel();
		jSeparator2 = new javax.swing.JSeparator();
		jLabel3 = new javax.swing.JLabel();
		deathCheckBox = new javax.swing.JCheckBox();
		jLabel4 = new javax.swing.JLabel();
		hpCheckBox = new javax.swing.JCheckBox();

		setLayout(new java.awt.GridBagLayout());

		jLabel1.setText("Spells");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
		add(jLabel1, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.ipady = -1;
		gridBagConstraints.insets = new java.awt.Insets(0, 25, 0, 0);
		add(spellCheckBox, gridBagConstraints);

		jSeparator1.setOrientation(SwingConstants.VERTICAL);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.gridheight = 5;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.ipadx = 8;
		gridBagConstraints.ipady = 260;
		gridBagConstraints.insets = new java.awt.Insets(8, 17, 0, 0);
		add(jSeparator1, gridBagConstraints);

		jLabel2.setFont(new java.awt.Font("Dialog", 1, 18));
		jLabel2.setText("Pick what is tracked");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridwidth = 4;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
		add(jLabel2, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.gridwidth = 5;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.ipadx = 400;
		gridBagConstraints.ipady = 8;
		gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
		add(jSeparator2, gridBagConstraints);

		jLabel3.setText("Death");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
		add(jLabel3, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new java.awt.Insets(0, 25, 0, 0);
		add(deathCheckBox, gridBagConstraints);

		jLabel4.setText("Hit Points");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 4;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
		add(jLabel4, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 4;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new java.awt.Insets(0, 25, 0, 0);
		add(hpCheckBox, gridBagConstraints);
	}
	//GEN-END:initComponents
}
