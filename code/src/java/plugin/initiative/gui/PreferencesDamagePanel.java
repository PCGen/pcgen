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
 * PreferencesDamagePanel.java
 */
package plugin.initiative.gui;

import java.awt.BorderLayout;

import gmgen.gui.PreferencesPanel;
import pcgen.core.SettingsHandler;
import pcgen.gui3.GuiUtility;
import pcgen.system.LanguageBundle;
import plugin.initiative.InitiativePlugin;

import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * Preference panel for damage related settings.
 */
public final class PreferencesDamagePanel extends PreferencesPanel
{
	private static final String SETTING_DAMAGE_DYING = ".Damage.Dying"; //$NON-NLS-1$
	private static final String SETTING_DAMAGE_DYING_START = ".Damage.Dying.Start"; //$NON-NLS-1$
	private static final String SETTING_DAMAGE_DEATH = ".Damage.Death"; //$NON-NLS-1$
	private static final String SETTING_DAMAGE_STABLE = ".Damage.Stable"; //$NON-NLS-1$
	private static final String SETTING_DAMAGE_SUBDUAL2 = ".Damage.Subdual"; //$NON-NLS-1$
	private static final String SETTING_DAMAGE_DISABLED = ".Damage.Disabled"; //$NON-NLS-1$

	// XXX Can replace those constants with enums that have a value for saving purpose
	static final int DAMAGE_DYING_END = 1;
	static final int DAMAGE_DYING_INITIATIVE = 2;

	private static final int DAMAGE_DISABLED_ZERO = 1;
	private static final int DAMAGE_DISABLED_CON = 2;

	private static final int DAMAGE_DEATH_NEG_TEN = 1;
	private static final int DAMAGE_DEATH_NEG_CON = 2;

	static final int DAMAGE_STABLE_PERCENT = 1;
	static final int DAMAGE_STABLE_SAVE = 2;
	private static final int DAMAGE_STABLE_NONE = 3;

	static final int DAMAGE_SUBDUAL = 1;
	static final int DAMAGE_NON_LETHAL = 2;

	private CheckBox dyingCB1;
	private RadioButton deathRB1;
	private RadioButton deathRB2;
	private RadioButton disabledRBZero;
	private RadioButton disabledRBCon;
	private RadioButton dyingRB1;
	private RadioButton dyingRB2;
	private RadioButton nonLethalRB1;
	private RadioButton nonLethalRB2;
	private RadioButton stableRB1;
	private RadioButton stableRB2;
	private RadioButton stableRB3;

	/**
	 * Creates new form PreferencesDamagePanel
	 */
	public PreferencesDamagePanel()
	{
		initComponents();
		initPreferences();
	}

	private void setDeath(int death)
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

	private int getDeath()
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
	private void setDying(int dying)
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

	private int getDying()
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

	private void setDyingStart(boolean dyingStart)
	{
		dyingCB1.setSelected(dyingStart);
	}

	private void setStable(int stable)
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

	private int getStable()
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

	@Override
	public void applyPreferences()
	{
		SettingsHandler.setGMGenOption(InitiativePlugin.LOG_NAME + SETTING_DAMAGE_DYING, getDying());
		SettingsHandler.setGMGenOption(InitiativePlugin.LOG_NAME + SETTING_DAMAGE_DYING_START, dyingCB1.isSelected());
		SettingsHandler.setGMGenOption(InitiativePlugin.LOG_NAME + SETTING_DAMAGE_DEATH, getDeath());
		SettingsHandler.setGMGenOption(InitiativePlugin.LOG_NAME + SETTING_DAMAGE_STABLE, getStable());
		SettingsHandler.setGMGenOption(InitiativePlugin.LOG_NAME + SETTING_DAMAGE_SUBDUAL2, getSubdual());
		SettingsHandler.setGMGenOption(InitiativePlugin.LOG_NAME + SETTING_DAMAGE_DISABLED, getDisabled());
	}

	@Override
	public void initPreferences()
	{
		setDying(SettingsHandler.getGMGenOption(InitiativePlugin.LOG_NAME + SETTING_DAMAGE_DYING, DAMAGE_DYING_END));
		setDyingStart(SettingsHandler.getGMGenOption(InitiativePlugin.LOG_NAME + SETTING_DAMAGE_DYING_START, true));
		setDeath(
				SettingsHandler.getGMGenOption(InitiativePlugin.LOG_NAME + SETTING_DAMAGE_DEATH,
						DAMAGE_DEATH_NEG_TEN));
		setStable(
				SettingsHandler.getGMGenOption(
						InitiativePlugin.LOG_NAME + SETTING_DAMAGE_STABLE,
						DAMAGE_STABLE_PERCENT
				));
		setSubdual(SettingsHandler.getGMGenOption(InitiativePlugin.LOG_NAME + SETTING_DAMAGE_SUBDUAL2,
				DAMAGE_SUBDUAL));
		setDisabled(
				SettingsHandler.getGMGenOption(
						InitiativePlugin.LOG_NAME + SETTING_DAMAGE_DISABLED,
						DAMAGE_DISABLED_ZERO
				));
	}

	@Override
	public String toString()
	{
		return LanguageBundle.getString("in_plugin_initiative_damage");
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 */
	private void initComponents()
	{
		ToggleGroup deathGroup = new ToggleGroup();
		ToggleGroup stableGroup = new ToggleGroup();
		ToggleGroup nonLethalGroup = new ToggleGroup();
		ToggleGroup dyingGroup = new ToggleGroup();
		ToggleGroup disabledGroup = new ToggleGroup();
		VBox dyingPanel = new VBox();
		VBox disabledPanel = new VBox();
		dyingRB1 = new RadioButton();
		dyingRB2 = new RadioButton();
		dyingCB1 = new CheckBox();
		VBox jPanel3 = new VBox();
		deathRB1 = new RadioButton();
		deathRB2 = new RadioButton();
		VBox jPanel4 = new VBox();
		stableRB1 = new RadioButton();
		stableRB2 = new RadioButton();
		stableRB3 = new RadioButton();
		VBox jPanel5 = new VBox();
		nonLethalRB1 = new RadioButton();
		nonLethalRB2 = new RadioButton();
		disabledRBCon = new RadioButton();
		disabledRBZero = new RadioButton();

		setLayout(new BorderLayout());

		dyingRB1.setSelected(true);
		dyingRB1.setText(LanguageBundle.getString("in_plugin_initiative_dying_end")); //$NON-NLS-1$
		dyingRB1.setToggleGroup(dyingGroup);
		dyingPanel.getChildren().add(dyingRB1);
		Node titledDyingPanel = new TitledPane(
				LanguageBundle.getString("in_plugin_initiative_dying"),
				dyingPanel
		);

		dyingRB2.setText(LanguageBundle.getString("in_plugin_initiative_dying_own")); //$NON-NLS-1$
		dyingRB2.setToggleGroup(dyingGroup);
		dyingPanel.getChildren().add(dyingRB2);

		dyingCB1.setSelected(true);
		dyingCB1.setText(LanguageBundle.getString("in_plugin_initiative_dying_start")); //$NON-NLS-1$
		dyingPanel.getChildren().add(dyingCB1);

		disabledRBZero.setSelected(true);
		disabledRBZero.setText(LanguageBundle.getString("in_plugin_initiative_disabled_zero")); //$NON-NLS-1$
		disabledRBZero.setToggleGroup(disabledGroup);
		disabledPanel.getChildren().add(disabledRBZero);
		Node titledDisabledPanel =
				new TitledPane(LanguageBundle.getString("in_plugin_initiative_disabled"), disabledPanel);

		disabledRBCon.setText(LanguageBundle.getString("in_plugin_initiative_disabled_mincon")); //$NON-NLS-1$
		disabledRBCon.setToggleGroup(disabledGroup);
		disabledPanel.getChildren().add(disabledRBCon);

		deathRB1.setSelected(true);
		deathRB1.setText(LanguageBundle.getString("in_plugin_initiative_death_minten")); //$NON-NLS-1$
		deathRB1.setToggleGroup(deathGroup);
		jPanel3.getChildren().add(deathRB1);
		Node titledInitPanel =
				new TitledPane(
						LanguageBundle.getString("in_plugin_initiative_disabled"),
						jPanel3
				);

		deathRB2.setText(LanguageBundle.getString("in_plugin_initiative_death_negcon")); //$NON-NLS-1$
		deathRB2.setToggleGroup(deathGroup);
		jPanel3.getChildren().add(deathRB2);

		stableRB1.setSelected(true);
		stableRB1.setText(LanguageBundle.getString("in_plugin_initiative_stabilize_tenpercent")); //$NON-NLS-1$
		stableRB1.setToggleGroup(stableGroup);
		jPanel4.getChildren().add(stableRB1);

		stableRB2.setText(LanguageBundle.getString("in_plugin_initiative_stabilize_fort")); //$NON-NLS-1$
		stableRB2.setToggleGroup(stableGroup);
		jPanel4.getChildren().add(stableRB2);

		stableRB3.setText(LanguageBundle.getString("in_plugin_initiative_stabilize_none")); //$NON-NLS-1$
		stableRB3.setToggleGroup(stableGroup);
		jPanel4.getChildren().add(stableRB3);

		Node titledStablePanel = new TitledPane(
				LanguageBundle.getString("in_plugin_initiative_stabilize_auto"),
				jPanel4
		);

		nonLethalRB1.setSelected(true);
		nonLethalRB1.setText(LanguageBundle.getString("in_plugin_initiative_subdual")); //$NON-NLS-1$
		nonLethalRB1.setToggleGroup(nonLethalGroup);
		jPanel5.getChildren().add(nonLethalRB1);

		nonLethalRB2.setText(LanguageBundle.getString("in_plugin_initiative_nonlethal")); //$NON-NLS-1$
		nonLethalRB2.setToggleGroup(nonLethalGroup);
		jPanel5.getChildren().add(nonLethalRB2);

		Node titledSubDualPanel = new TitledPane(
				LanguageBundle.getString("in_plugin_initiative_subdualnl"),
				jPanel5
		);

		Pane mainPanel = new VBox();
		mainPanel.getChildren().add(titledDyingPanel);
		mainPanel.getChildren().add(titledDisabledPanel);
		mainPanel.getChildren().add(titledInitPanel);
		mainPanel.getChildren().add(titledStablePanel);
		mainPanel.getChildren().add(titledSubDualPanel);

		ScrollPane scrollPane = new ScrollPane();
		scrollPane.setContent(mainPanel);

		add(GuiUtility.wrapParentAsJFXPanel(scrollPane), BorderLayout.CENTER);
	}
}
