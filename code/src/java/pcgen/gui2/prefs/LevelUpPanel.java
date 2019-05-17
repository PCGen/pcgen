/*
 * Copyright 2010(C) James Dempsey
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
package pcgen.gui2.prefs;

import pcgen.gui3.JFXPanelFromResource;
import pcgen.gui3.ResettableController;
import pcgen.gui3.preferences.LevelUpPreferencesPanelController;
import pcgen.system.LanguageBundle;

/**
 * The Class {@code LevelUpPanel} is responsible for displaying leveling up related preferences and allowing the
 * preferences to be edited by the user.
 */
@SuppressWarnings("serial")
public final class LevelUpPanel extends PCGenPrefsPanel
{
	private static final String IN_LEVEL_UP = LanguageBundle.getString("in_Prefs_levelUp");

	private final JFXPanelFromResource<ResettableController> panel;

	public LevelUpPanel()
	{

		this.panel =
				new JFXPanelFromResource<>(
						LevelUpPreferencesPanelController.class,
						"LevelUpPreferencesPanel.fxml"
				);
		this.add(panel);
	}

	@Override
	public String getTitle()
	{
		return IN_LEVEL_UP;
	}

	@Override
	public void setOptionsBasedOnControls()
	{
		panel.getController().apply();
	}

	@Override
	public void applyOptionValuesToControls()
	{
		panel.getController().reset();
	}

}
