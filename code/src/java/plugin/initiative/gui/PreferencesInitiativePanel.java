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
 */
package plugin.initiative.gui;

import java.awt.BorderLayout;

import pcgen.core.SettingsHandler;
import pcgen.gui3.GuiUtility;
import pcgen.system.LanguageBundle;
import plugin.initiative.InitiativePlugin;

import javafx.scene.control.CheckBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * Panel that tracks the miscellaneous preferences.
 */
public class PreferencesInitiativePanel extends gmgen.gui.PreferencesPanel
{
	private static final String SETTING_ROLL_PC_INITIATIVES = ".rollPCInitiatives"; //$NON-NLS-1$

	private CheckBox rollPCInitiatives;

	/** Creates new form PreferencesMiscPanel */
	public PreferencesInitiativePanel()
	{
		initComponents();
		initPreferences();
	}

	@Override
	public void applyPreferences()
	{
		SettingsHandler.setGMGenOption(InitiativePlugin.LOG_NAME + SETTING_ROLL_PC_INITIATIVES, getRollPCInitiatives());
	}

	@Override
	public void initPreferences()
	{
		setRollPCInitiatives(
			SettingsHandler.getGMGenOption(InitiativePlugin.LOG_NAME + SETTING_ROLL_PC_INITIATIVES, true));
	}

	/**
	 * Turns on or off refresh on state change
	 *
	 * @param b
	 */
	private void setRollPCInitiatives(final boolean b)
	{
		rollPCInitiatives.setSelected(b);
	}

	/**
	 * Gets current setting of refresh on state change
	 *
	 * @return {@code true} if the roll pc initiative is selected
	 */
	private boolean getRollPCInitiatives()
	{
		return rollPCInitiatives.isSelected();
	}

	@Override
	public String toString()
	{
		return LanguageBundle.getString("in_plugin_initiative"); //$NON-NLS-1$
	}

	private void initComponents()
	{

		rollPCInitiatives = new CheckBox();

		rollPCInitiatives.setText(LanguageBundle.getString("in_plugin_initiative_rollPcInit"));

		Pane vbox = new VBox();
		vbox.getChildren().add(rollPCInitiatives);

		setLayout(new BorderLayout());
		add(GuiUtility.wrapParentAsJFXPanel(vbox), BorderLayout.CENTER);
	}
}
