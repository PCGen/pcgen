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
import pcgen.system.LanguageBundle;
import plugin.initiative.InitiativePlugin;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.VBox;

/**
 * Panel that tracks the misc preferences
 */
public final class PreferencesPerformancePanel extends gmgen.gui.PreferencesPanel
{
	private static final String OPTION_NAME = InitiativePlugin.LOG_NAME + ".refreshOnStateChange"; //$NON-NLS-1$

	private CheckBox refreshOnStateChange;

	/** Creates new form PreferencesMiscPanel */
	public PreferencesPerformancePanel()
	{
		initComponents();
		initPreferences();
	}

	@Override
	public void applyPreferences()
	{
		SettingsHandler.setGMGenOption(OPTION_NAME, getRefreshOnStateChange());
	}

	@Override
	public void initPreferences()
	{
		setRefreshOnStateChange(SettingsHandler.getGMGenOption(OPTION_NAME, true));
	}

	/**
	 * <p>
	 * Turns on or off refresh on state cange
	 * </p>
	 *
	 * @param b
	 */
	private void setRefreshOnStateChange(boolean b)
	{
		refreshOnStateChange.setSelected(b);
	}

	/**
	 * <p>
	 * Gets current setting of refresh on state change
	 * </p>
	 *
	 * @return true or false
	 */
	private boolean getRefreshOnStateChange()
	{
		return refreshOnStateChange.isSelected();
	}

	@Override
	public String toString()
	{
		return LanguageBundle.getString("in_plugin_init_performance"); //$NON-NLS-1$
	}

	private void initComponents()
	{
		VBox vbox = new VBox();
		JFXPanel jfxPanel = new JFXPanel();

		refreshOnStateChange = new CheckBox();
		refreshOnStateChange.setText(LanguageBundle.getString("in_plugin_init_refreshOnChange")); //$NON-NLS-1$

		vbox.getChildren().add(refreshOnStateChange);

		Platform.runLater(() -> {
			Scene scene = new Scene(vbox);
			jfxPanel.setScene(scene);
		});

		add(jfxPanel, BorderLayout.CENTER);
		setLayout(new BorderLayout());
	}
}
