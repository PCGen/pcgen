/*
 * Copyright 2026 Vest <Vest@users.noreply.github.com>
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
 */
package pcgen.gui3.namegen;

import pcgen.gui3.JFXPanelFromResource;
import pcgen.system.LanguageBundle;

import javafx.application.Platform;

/**
 * Modal random-name dialog. Presented to Swing callers as a synchronous
 * call: construct, {@link #showAndBlock(String)}, then read back
 * {@link #getChosenName()} / {@link #getGender()}.
 */
public final class RandomNameDialog
{
	private final JFXPanelFromResource<RandomNamePanelController> panel;
	private final String initialGender;

	public RandomNameDialog(String initialGender)
	{
		this.initialGender = initialGender;
		this.panel = new JFXPanelFromResource<>(RandomNamePanelController.class, "RandomNamePanel.fxml");
	}

	/**
	 * Show the dialog and block until the user closes it. Must be called
	 * from a non-FX thread (typically the Swing EDT or the main thread).
	 */
	public void showAndBlock()
	{
		if (initialGender != null && !initialGender.isEmpty())
		{
			Platform.runLater(() -> {
				RandomNamePanelController controller = panel.getControllerFromJavaFXThread();
				if (controller != null)
				{
					controller.setInitialGender(initialGender);
				}
			});
		}
		panel.showAndBlock(LanguageBundle.getString("in_rndNameTitle"));
	}

	public String getChosenName()
	{
		RandomNamePanelController controller = panel.getController();
		return controller == null ? "" : controller.getChosenName();
	}

	public String getGender()
	{
		RandomNamePanelController controller = panel.getController();
		return controller == null ? "" : controller.getGender();
	}
}
