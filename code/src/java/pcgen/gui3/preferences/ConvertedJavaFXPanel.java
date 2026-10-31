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

package pcgen.gui3.preferences;

import java.awt.BorderLayout;

import pcgen.gui2.prefs.PCGenPrefsPanel;
import pcgen.gui3.GuiAssertions;
import pcgen.gui3.GuiUtility;
import pcgen.gui3.JFXPanelFromResource;
import pcgen.gui3.ResettableController;
import pcgen.system.LanguageBundle;

import javafx.application.Platform;

/**
 * Embeds an FXML preferences form in Swing and runs its controller callbacks on FX.
 * @param <T> the preferences controller type
 */
public final class ConvertedJavaFXPanel<T extends ResettableController> extends PCGenPrefsPanel
{
	private final String title;
	private final JFXPanelFromResource<T> panel;

	/**
	 * Creates a form that fills its Swing container and initializes localization before loading FXML.
	 * @param klass the controller class used to locate the resource
	 * @param resource the FXML resource relative to the controller class
	 * @param titleTextKey the language-bundle key for the panel title
	 */
	public ConvertedJavaFXPanel(Class<T> klass, String resource, String titleTextKey)
	{
		GuiAssertions.assertIsNotJavaFXThread();
		title = LanguageBundle.getString(titleTextKey);
		setLayout(new BorderLayout());
		panel = new JFXPanelFromResource<>(klass, resource);
		add(panel, BorderLayout.CENTER);
	}

	@Override
	public String getTitle()
	{
		return title;
	}

	@Override
	public void applyOptionValuesToControls()
	{
		Platform.runLater(() -> panel.getControllerFromJavaFXThread().reset());
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

	/**
	 * Returns the loaded controller, waiting for FXML loading when called from Swing.
	 * @return the controller whose UI methods must be called on FX
	 */
	public T getController()
	{
		return panel.getController();
	}

	@Override
	public String toString()
	{
		return getTitle();
	}
}
