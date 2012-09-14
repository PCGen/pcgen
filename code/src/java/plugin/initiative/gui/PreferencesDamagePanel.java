/*
 *  Initiative - A role playing utility to track turns
 *  Copyright (C) 2002 Devon D Jones
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 *  The author of this program grants you the ability to use this code
 *  in conjunction with code that is covered under the Open Gaming License
 *
 * PreferencesDamagePanel.java
 *
 * Created on July 11, 2003, 4:34 PM
 */
package plugin.initiative.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.TitledBorder;

import pcgen.core.SettingsHandler;
import pcgen.system.LanguageBundle;
import plugin.initiative.InitiativePlugin;

/**
 *
 * @author soulcatcher
 */
public class PreferencesDamagePanel extends gmgen.gui.PreferencesPanel
{

	public static final int DAMAGE_DYING_END = 1;
	public static final int DAMAGE_DYING_INITIATIVE = 2;
	public static final int DAMAGE_DISABLED_ZERO = 1;
	public static final int DAMAGE_DISABLED_CON = 2;
	public static final int DAMAGE_DEATH_NEG_TEN = 1;
	public static final int DAMAGE_DEATH_NEG_CON = 2;
	public static final int DAMAGE_STABLE_PERCENT = 1;
	public static final int DAMAGE_STABLE_SAVE = 2;
	public static final int DAMAGE_STABLE_NONE = 3;
	public static final int DAMAGE_SUBDUAL = 1;
	public static final int DAMAGE_NON_LETHAL = 2;

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.ButtonGroup deathGroup;
	private javax.swing.ButtonGroup disabledGroup;
	private javax.swing.ButtonGroup dyingGroup;
	private javax.swing.ButtonGroup nonLethalGroup;
	private javax.swing.ButtonGroup stableGroup;
	private javax.swing.JCheckBox dyingCB1;
	private javax.swing.JPanel mainPanel;
	private javax.swing.JPanel jPanel3;
	private javax.swing.JPanel jPanel4;
	private javax.swing.JPanel jPanel5;
	private javax.swing.JPanel dyingPanel;
	private javax.swing.JRadioButton deathRB1;
	private javax.swing.JRadioButton deathRB2;
	private javax.swing.JRadioButton disabledRBZero;
	private javax.swing.JRadioButton disabledRBCon;
	private javax.swing.JRadioButton dyingRB1;
	private javax.swing.JRadioButton dyingRB2;
	private javax.swing.JRadioButton nonLethalRB1;
	private javax.swing.JRadioButton nonLethalRB2;
	private javax.swing.JRadioButton stableRB1;
	private javax.swing.JRadioButton stableRB2;
	private javax.swing.JRadioButton stableRB3;
	private javax.swing.JScrollPane jScrollPane1;
	private JPanel disabledPanel;

	/** Creates new form PreferencesDamagePanel */
	public PreferencesDamagePanel()
	{
		initComponents();
		initPreferences();
	}

	public void setDeath(int death)
	{
		if (death == DAMAGE_DEATH_NEG_TEN)
		{
			deathRB1.setSelected(true);
		}
		else if (death == DAMAGE_DEATH_NEG_CON)
		{
			deathRB2.setSelected(true);
		}
	}

	public int getDeath()
	{
		int returnVal = 0;

		if (deathRB1.isSelected())
		{
			returnVal = DAMAGE_DEATH_NEG_TEN;
		}
		else if (deathRB2.isSelected())
		{
			returnVal = DAMAGE_DEATH_NEG_CON;
		}

		return returnVal;
	}

	public void setDisabled(int disabled)
	{
		if (disabled == DAMAGE_DISABLED_ZERO)
		{
			disabledRBZero.setSelected(true);
		}
		else if (disabled == DAMAGE_DISABLED_CON)
		{
			disabledRBCon.setSelected(true);
		}
	}

	public int getDisabled()
	{
		int returnVal = 0;

		if (disabledRBZero.isSelected())
		{
			returnVal = DAMAGE_DISABLED_ZERO;
		}
		else if (disabledRBCon.isSelected())
		{
			returnVal = DAMAGE_DISABLED_CON;
		}

		return returnVal;
	}

	// End of variables declaration//GEN-END:variables
	public void setDying(int dying)
	{
		if (dying == DAMAGE_DYING_END)
		{
			dyingRB1.setSelected(true);
		}
		else if (dying == DAMAGE_DYING_INITIATIVE)
		{
			dyingRB2.setSelected(true);
		}
	}

	public int getDying()
	{
		int returnVal = 0;

		if (dyingRB1.isSelected())
		{
			returnVal = DAMAGE_DYING_END;
		}
		else if (dyingRB2.isSelected())
		{
			returnVal = DAMAGE_DYING_INITIATIVE;
		}

		return returnVal;
	}

	public void setDyingStart(boolean dyingStart)
	{
		dyingCB1.setSelected(dyingStart);
	}

	public void setStable(int stable)
	{
		if (stable == DAMAGE_STABLE_PERCENT)
		{
			stableRB1.setSelected(true);
		}
		else if (stable == DAMAGE_STABLE_SAVE)
		{
			stableRB2.setSelected(true);
		}
		else if (stable == DAMAGE_STABLE_NONE)
		{
			stableRB3.setSelected(true);
		}
	}

	public int getStable()
	{
		int returnVal = 0;

		if (stableRB1.isSelected())
		{
			returnVal = DAMAGE_STABLE_PERCENT;
		}
		else if (stableRB2.isSelected())
		{
			returnVal = DAMAGE_STABLE_SAVE;
		}
		else if (stableRB3.isSelected())
		{
			returnVal = DAMAGE_STABLE_NONE;
		}

		return returnVal;
	}

	public void setSubdual(int subdual)
	{
		if (subdual == DAMAGE_SUBDUAL)
		{
			nonLethalRB1.setSelected(true);
		}
		else if (subdual == DAMAGE_NON_LETHAL)
		{
			nonLethalRB2.setSelected(true);
		}
	}

	public int getSubdual()
	{
		int returnVal = 0;

		if (nonLethalRB1.isSelected())
		{
			returnVal = DAMAGE_SUBDUAL;
		}
		else if (nonLethalRB2.isSelected())
		{
			returnVal = DAMAGE_NON_LETHAL;
		}

		return returnVal;
	}

	public void applyPreferences()
	{
		SettingsHandler.setGMGenOption(InitiativePlugin.LOG_NAME
			+ ".Damage.Dying", getDying());
		SettingsHandler.setGMGenOption(InitiativePlugin.LOG_NAME
			+ ".Damage.Dying.Start", dyingCB1.isSelected());
		SettingsHandler.setGMGenOption(InitiativePlugin.LOG_NAME
			+ ".Damage.Death", getDeath());
		SettingsHandler.setGMGenOption(InitiativePlugin.LOG_NAME
			+ ".Damage.Stable", getStable());
		SettingsHandler.setGMGenOption(InitiativePlugin.LOG_NAME
			+ ".Damage.Subdual", getSubdual());
		SettingsHandler.setGMGenOption(InitiativePlugin.LOG_NAME
			+ ".Damage.Disabled", getDisabled());
	}

	public void initPreferences()
	{
		setDying(SettingsHandler.getGMGenOption(InitiativePlugin.LOG_NAME
			+ ".Damage.Dying", DAMAGE_DYING_END));
		setDyingStart(SettingsHandler.getGMGenOption(InitiativePlugin.LOG_NAME
			+ ".Damage.Dying.Start", true));
		setDeath(SettingsHandler.getGMGenOption(InitiativePlugin.LOG_NAME
			+ ".Damage.Death", DAMAGE_DEATH_NEG_TEN));
		setStable(SettingsHandler.getGMGenOption(InitiativePlugin.LOG_NAME
			+ ".Damage.Stable", DAMAGE_STABLE_PERCENT));
		setSubdual(SettingsHandler.getGMGenOption(InitiativePlugin.LOG_NAME
			+ ".Damage.Subdual", DAMAGE_SUBDUAL));
		setDisabled(SettingsHandler.getGMGenOption(InitiativePlugin.LOG_NAME
			+ ".Damage.Disabled", DAMAGE_DISABLED_ZERO));
	}

	@Override
	public String toString()
	{
		return "Damage";
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	private void initComponents()
	{ //GEN-BEGIN:initComponents
		deathGroup = new javax.swing.ButtonGroup();
		stableGroup = new javax.swing.ButtonGroup();
		nonLethalGroup = new javax.swing.ButtonGroup();
		dyingGroup = new javax.swing.ButtonGroup();
		disabledGroup = new javax.swing.ButtonGroup();
		jScrollPane1 = new javax.swing.JScrollPane();
		mainPanel = new javax.swing.JPanel();
		dyingPanel = new javax.swing.JPanel();
		disabledPanel = new JPanel();
		dyingRB1 = new javax.swing.JRadioButton();
		dyingRB2 = new javax.swing.JRadioButton();
		dyingCB1 = new javax.swing.JCheckBox();
		jPanel3 = new javax.swing.JPanel();
		deathRB1 = new javax.swing.JRadioButton();
		deathRB2 = new javax.swing.JRadioButton();
		jPanel4 = new javax.swing.JPanel();
		stableRB1 = new javax.swing.JRadioButton();
		stableRB2 = new javax.swing.JRadioButton();
		stableRB3 = new javax.swing.JRadioButton();
		jPanel5 = new javax.swing.JPanel();
		nonLethalRB1 = new javax.swing.JRadioButton();
		nonLethalRB2 = new javax.swing.JRadioButton();
		disabledRBCon = new JRadioButton();
		disabledRBZero = new JRadioButton();

		setLayout(new java.awt.BorderLayout());

		mainPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		c.fill = GridBagConstraints.HORIZONTAL;

		dyingPanel.setLayout(new javax.swing.BoxLayout(dyingPanel,
			javax.swing.BoxLayout.Y_AXIS));

		dyingPanel.setBorder(new TitledBorder(null,
			LanguageBundle.getString("in_gmgen_init_dying"), //$NON-NLS-1$
			javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
			javax.swing.border.TitledBorder.DEFAULT_POSITION));
		dyingRB1.setSelected(true);
		dyingRB1
			.setText(LanguageBundle.getString("in_gmgen_init_dying_end")); //$NON-NLS-1$
		dyingGroup.add(dyingRB1);
		dyingPanel.add(dyingRB1);

		dyingRB2
			.setText("Dying damage happens on character's initiative (House)");
		dyingGroup.add(dyingRB2);
		dyingPanel.add(dyingRB2);

		dyingCB1.setSelected(true);
		dyingCB1
			.setText("Dying damage starts same round character started dying");
		dyingPanel.add(dyingCB1);

		mainPanel.add(dyingPanel, c);

		//DISABLED OPTION
		disabledPanel.setLayout(new javax.swing.BoxLayout(disabledPanel,
			javax.swing.BoxLayout.Y_AXIS));

		disabledPanel.setBorder(new javax.swing.border.TitledBorder(null,
			"Disabled Behavior",
			javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
			javax.swing.border.TitledBorder.DEFAULT_POSITION));
		disabledRBZero.setSelected(true);
		disabledRBZero
			.setText("Character becomes disabled at 0 HP (Standard).");
		disabledGroup.add(disabledRBZero);
		disabledPanel.add(disabledRBZero);

		disabledRBCon
			.setText("Character becomes disabled at -CON Modifier HP (House).");
		disabledGroup.add(disabledRBCon);
		disabledPanel.add(disabledRBCon);
		mainPanel.add(disabledPanel, c);
		//END DISABLED OPTION

		jPanel3.setLayout(new javax.swing.BoxLayout(jPanel3,
			javax.swing.BoxLayout.Y_AXIS));

		jPanel3.setBorder(new javax.swing.border.TitledBorder(null, "Death",
			javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
			javax.swing.border.TitledBorder.DEFAULT_POSITION));
		deathRB1.setSelected(true);
		deathRB1.setText("Death at -10 hit points (3rd Ed, Modern)");
		deathGroup.add(deathRB1);
		jPanel3.add(deathRB1);

		deathRB2.setText("Death at negative Con hit points (House)");
		deathGroup.add(deathRB2);
		jPanel3.add(deathRB2);

		mainPanel.add(jPanel3, c);

		jPanel4.setLayout(new javax.swing.BoxLayout(jPanel4,
			javax.swing.BoxLayout.Y_AXIS));

		jPanel4.setBorder(new javax.swing.border.TitledBorder(null,
			"Auto Stabilize",
			javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
			javax.swing.border.TitledBorder.DEFAULT_POSITION));
		stableRB1.setSelected(true);
		stableRB1.setText("10% chance/round (3rd Ed)");
		stableGroup.add(stableRB1);
		jPanel4.add(stableRB1);

		stableRB2.setText("Fort save DC 20/round (Modern)");
		stableGroup.add(stableRB2);
		jPanel4.add(stableRB2);

		stableRB3.setText("None (House)");
		stableGroup.add(stableRB3);
		jPanel4.add(stableRB3);

		mainPanel.add(jPanel4, c);

		jPanel5.setLayout(new javax.swing.BoxLayout(jPanel5,
			javax.swing.BoxLayout.Y_AXIS));

		jPanel5.setBorder(new javax.swing.border.TitledBorder(null,
			"Subdual/Non Lethal Damage",
			javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
			javax.swing.border.TitledBorder.DEFAULT_POSITION));
		nonLethalRB1.setSelected(true);
		nonLethalRB1.setText("Subdual Damage (3rd Ed)");
		nonLethalRB1.setToolTipText("null");
		nonLethalGroup.add(nonLethalRB1);
		jPanel5.add(nonLethalRB1);

		nonLethalRB2.setText("Non Lethal Damage (Modern)");
		nonLethalGroup.add(nonLethalRB2);
		jPanel5.add(nonLethalRB2);

		mainPanel.add(jPanel5, c);

		jScrollPane1.setViewportView(mainPanel);

		add(jScrollPane1, java.awt.BorderLayout.CENTER);
	}
}