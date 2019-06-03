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
 */
package plugin.experience.gui;

import java.awt.BorderLayout;

import pcgen.core.SettingsHandler;
import pcgen.gui2.prefs.PCGenPrefsPanel;
import pcgen.gui3.GuiUtility;
import pcgen.system.LanguageBundle;
import plugin.experience.ExperienceAdjusterPlugin;

import javafx.scene.control.RadioButton;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;

public final class PreferencesExperiencePanel extends PCGenPrefsPanel
{
	public static final int EXPERIENCE_3 = 1;
	public static final int EXPERIENCE_35 = 2;

	private static final String OPTION_NAME_EXP_TYPE =
			ExperienceAdjusterPlugin.LOG_NAME + ".ExperienceType"; //$NON-NLS-1$

	private RadioButton experienceRB1;
	private RadioButton experienceRB2;

	/** Creates new form PreferencesDamagePanel */
	public PreferencesExperiencePanel()
	{
		initComponents();
		this.applyOptionValuesToControls();
	}

	public void setExperience(int exp)
	{
		if (exp == EXPERIENCE_3)
		{
			experienceRB1.setSelected(true);
		}
		else if (exp == EXPERIENCE_35)
		{
			experienceRB2.setSelected(true);
		}
	}

	public int getExperience()
	{
		if (experienceRB1.isSelected())
		{
			return EXPERIENCE_3;
		}
		else if (experienceRB2.isSelected())
		{
			return EXPERIENCE_35;
		}

		return 0;
	}

	@Override
	public void setOptionsBasedOnControls()
	{
		SettingsHandler.setGMGenOption(OPTION_NAME_EXP_TYPE, getExperience());
	}

	@Override
	public void applyOptionValuesToControls()
	{
		setExperience(SettingsHandler.getGMGenOption(OPTION_NAME_EXP_TYPE, EXPERIENCE_35));
	}

	@Override
	public String toString()
	{
		return LanguageBundle.getString("in_plugin_xp"); //$NON-NLS-1$
	}

	private void initComponents()
	{
		TitledPane titlePane = new TitledPane();
		titlePane.setText(LanguageBundle.getString("in_plugin_xp_calc"));
		titlePane.setCollapsible(false);

		ToggleGroup experienceGroup = new ToggleGroup();

		experienceRB1 = new RadioButton();
		experienceRB1.setToggleGroup(experienceGroup);
		experienceRB2 = new RadioButton();
		experienceRB2.setToggleGroup(experienceGroup);

		experienceRB1.setSelected(true);
		experienceRB1.setText(LanguageBundle.getString("in_plugin_xp_byParty"));
		experienceRB2.setText(LanguageBundle.getString("in_plugin_xp_byPC"));

		VBox vbox = new VBox();
		vbox.getChildren().add(experienceRB1);
		vbox.getChildren().add(experienceRB2);

		add(GuiUtility.wrapParentAsJFXPanel(vbox), BorderLayout.CENTER);
	}

	@Override
	public String getTitle()
	{
		return LanguageBundle.getString("in_plugin_experience_name");
	}
}
