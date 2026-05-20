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

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import pcgen.gui3.GuiAssertions;
import pcgen.system.LanguageBundle;
import pcgen.util.Logging;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Modal random-name dialog. Presented to Swing callers as a synchronous
 * call: construct, {@link #showAndBlock()}, then read back
 * {@link #getChosenName()} / {@link #getGender()}.
 *
 * <p>Loads the FXML directly into a JavaFX {@link Stage}; we don't need
 * a {@code JFXPanel} because the dialog isn't embedded in Swing.
 */
public final class RandomNameDialog
{
	private static final String FXML_RESOURCE = "RandomNamePanel.fxml";

	private final String initialGender;
	private RandomNamePanelController controller;

	public RandomNameDialog(String initialGender)
	{
		this.initialGender = initialGender;
	}

	/**
	 * Show the dialog and block until the user closes it. Must be called
	 * from a non-FX thread (typically the Swing EDT or the main thread).
	 */
	public void showAndBlock()
	{
		GuiAssertions.assertIsNotJavaFXThread();
		CompletableFuture<Void> closed = new CompletableFuture<>();
		Platform.runLater(() -> {
			try
			{
				FXMLLoader loader = new FXMLLoader();
				URL location = RandomNameDialog.class.getResource(FXML_RESOURCE);
				Objects.requireNonNull(location, FXML_RESOURCE);
				loader.setLocation(location);
				loader.setResources(LanguageBundle.getBundle());
				Scene scene = loader.load();
				controller = loader.getController();
				if (initialGender != null && !initialGender.isEmpty() && controller != null)
				{
					controller.setInitialGender(initialGender);
				}

				Stage stage = new Stage();
				stage.setTitle(LanguageBundle.getString("in_rndNameTitle"));
				stage.setScene(scene);
				stage.initModality(Modality.APPLICATION_MODAL);
				stage.sizeToScene();
				stage.showAndWait();
			}
			catch (IOException e)
			{
				Logging.errorPrint("failed to load random-name dialog FXML", e);
			}
			finally
			{
				closed.complete(null);
			}
		});
		closed.join();
	}

	public String getChosenName()
	{
		return controller == null ? "" : controller.getChosenName();
	}

	public String getGender()
	{
		return controller == null ? "" : controller.getGender();
	}
}
