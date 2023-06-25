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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.     See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package pcgen.gui3.dialog;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import pcgen.gui3.Controllable;
import pcgen.gui3.GuiAssertions;
import pcgen.system.LanguageBundle;
import pcgen.util.Logging;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

/**
 * This is the application logic for the "About Dialog"
 *
 * @see pcgen.gui3.JFXPanelFromResource
 */
public class AboutDialog implements Controllable<AboutDialogController>
{
	private final FXMLLoader loader = new FXMLLoader();
	private final Stage primaryStage;

	public AboutDialog(Stage parentStage)
	{
		this.primaryStage = parentStage;

		loader.setResources(LanguageBundle.getBundle());
		loader.setLocation(getClass().getResource("AboutDialog.fxml"));

		final Scene scene;
		try
		{
			scene = loader.load();
			scene.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
				if (keyEvent.getCode()== KeyCode.ESCAPE)
				{
					primaryStage.hide();
				}
			});
		} catch (IOException e)
		{
			Logging.errorPrint("failed to load preloader", e);
			return;
		}

		primaryStage.setScene(scene);
	}

	/**
	 * @return the controller for the preloader
	 */
	@Override
	public AboutDialogController getController()
	{
		GuiAssertions.assertIsNotJavaFXThread();
		return CompletableFuture
				.supplyAsync(loader::<AboutDialogController>getController)
				.join();
	}

	/**
	 * Shows the About dialog window
	 */
	public void show()
	{
		// Fix for Mac, when the window is maximized
		if (primaryStage.isFullScreen())
		{
			primaryStage.setFullScreen(false);
		}
		// Fix for other platforms
		if (primaryStage.isMaximized())
		{
			primaryStage.setMaximized(false);
		}
		primaryStage.sizeToScene();
		primaryStage.show();

		// Don't allow to resize the window less, than its current size. It is possible to get stage's dimensions
		// only, when the stage becomes visible
		primaryStage.setMinWidth(primaryStage.getWidth());
		primaryStage.setMinHeight(primaryStage.getHeight());
	}
}
