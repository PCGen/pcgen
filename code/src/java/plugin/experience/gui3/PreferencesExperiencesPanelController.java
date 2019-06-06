/*
 * Copyright 2019 (C) Eitan Adler <lists@eitanadler.com>
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
 */

package plugin.experience.gui3;

import pcgen.core.SettingsHandler;
import pcgen.gui3.ResettableController;
import plugin.experience.ExperienceAdjusterPlugin;

import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;

public class PreferencesExperiencesPanelController implements ResettableController
{
	@FXML
	private RadioButton experienceRB1;
	@FXML
	private RadioButton experienceRB2;
	@FXML
	private ToggleGroup experienceGroup;

	private static final String OPTION_NAME_EXP_TYPE =
			ExperienceAdjusterPlugin.LOG_NAME + ".ExperienceType";

	// TODO: move to a "model" class
	public static final int EXPERIENCE_3 = 1;
	public static final int EXPERIENCE_35 = 2;

	public void setExperience(int exp)
	{
		experienceGroup.getToggles().stream()
		               .filter(toggle -> (int)toggle.getUserData() == exp)
		               .findFirst()
		               .ifPresent(toggle -> toggle.setSelected(true));
	}

	public int getExperience()
	{
		return (int)experienceGroup.getSelectedToggle().getUserData();
	}

	@Override
	public void apply()
	{
		SettingsHandler.setGMGenOption(OPTION_NAME_EXP_TYPE, getExperience());

	}

	@Override
	public void reset()
	{
		setExperience(SettingsHandler.getGMGenOption(OPTION_NAME_EXP_TYPE, EXPERIENCE_35));
	}
}
