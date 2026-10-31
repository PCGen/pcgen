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

import java.awt.BorderLayout;

import pcgen.gui3.GuiAssertions;
import pcgen.gui3.GuiUtility;
import pcgen.gui3.JFXPanelFromResource;
import pcgen.gui3.preferences.LocationPreferencesPanelController;
import pcgen.system.LanguageBundle;

import javafx.application.Platform;

/** Embeds the FXML location preferences in the Swing preferences dialog. */
public final class LocationPanel extends PCGenPrefsPanel
{
	private static final String IN_LOCATION = LanguageBundle.getString("in_Prefs_location");

	private final JFXPanelFromResource<LocationPreferencesPanelController> panel;

	/** Creates the Swing host and schedules loading of the location form on the FX thread. */
	public LocationPanel()
	{
		GuiAssertions.assertIsNotJavaFXThread();
		setLayout(new BorderLayout());
		panel = new JFXPanelFromResource<>(LocationPreferencesPanelController.class, "LocationPreferencesPanel.fxml");
		add(panel, BorderLayout.CENTER);
	}

	@Override
	public String getTitle()
	{
		return IN_LOCATION;
	}

	@Override
	public void setOptionsBasedOnControls()
	{
		if (Platform.isFxApplicationThread())
		{
			panel.getControllerFromJavaFXThread().apply();
		}
		else
		{
			GuiUtility.runOnJavaFXThreadNow(() -> {
				panel.getControllerFromJavaFXThread().apply();
				return null;
			});
		}
	}

	@Override
	public void applyOptionValuesToControls()
	{
		Platform.runLater(() -> panel.getControllerFromJavaFXThread().reset());
	}
}
