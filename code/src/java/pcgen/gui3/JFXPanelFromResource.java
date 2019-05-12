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

package pcgen.gui3;

import java.io.IOException;
import java.util.Objects;

import pcgen.system.LanguageBundle;
import pcgen.util.Logging;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Displays HTML content as a "panel".
 * @param <T> The class of the controller
 */
public final class JFXPanelFromResource<T> extends JFXPanel
{

	private final FXMLLoader fxmlLoader = new FXMLLoader();

	/**
	 * @param klass the class that contains the resource load
	 * @param resourceName the relative filename of the FXML file to load.
	 */
	public JFXPanelFromResource(Class<T> klass, String resourceName)
	{
		fxmlLoader.setLocation(klass.getResource(resourceName));
		fxmlLoader.setResources(LanguageBundle.getBundle());
		Platform.runLater(() -> {
			try
			{
				Scene scene = fxmlLoader.load();
				this.setScene(scene);
			} catch (IOException e)
			{
				Logging.errorPrint("failed to load stream fxml", e);
			}
		});
	}

	/**
	 * @return controller for the loaded FXML file
	 */
	public T getController()
	{
		return fxmlLoader.getController();
	}

	// instead of living in JFXPanelFromResource, we should either have StageLoaders,
	// a utility class, or something else
	public void showAsStage()
	{
		Platform.runLater(() -> {
			Stage dialog = new Stage();
			dialog.setScene(getScene());
			dialog.sizeToScene();
			dialog.show();
		});
	}
}
