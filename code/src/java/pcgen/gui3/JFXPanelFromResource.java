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
import java.net.URL;
import java.util.concurrent.CompletableFuture;

import pcgen.system.LanguageBundle;
import pcgen.util.Logging;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Displays HTML content as a "panel".
 *
 * @param <T> The class of the controller
 */
public final class JFXPanelFromResource<T> extends JFXPanel implements Controllable<T>
{

	private final FXMLLoader fxmlLoader = new FXMLLoader();

	/**
	 * @param klass        the class that contains the resource load
	 * @param resourceName the relative filename of the FXML file to load.
	 */
	public JFXPanelFromResource(Class<? extends T> klass, String resourceName)
	{
		URL resource = klass.getResource(resourceName);
		Logging.debugPrint(String.format("location for %s (%s) is %s", resourceName, klass, resource));
		fxmlLoader.setLocation(resource);
		fxmlLoader.setResources(LanguageBundle.getBundle());
		Platform.runLater(() -> {
			try
			{
				Scene scene = fxmlLoader.load();
				this.setScene(scene);
			} catch (IOException e)
			{
				Logging.errorPrint(String.format("failed to load stream fxml (%s/%s/%s)",
						resourceName, klass, resource), e);
			}
		});
	}

	@Override
	public T getController()
	{
		if (Platform.isFxApplicationThread())
		{
			return fxmlLoader.getController();
		}
		else
		{
			return GuiUtility.runOnJavaFXThreadNow(fxmlLoader::getController);
		}
	}

	public T getControllerFromJavaFXThread()
	{
		GuiAssertions.assertIsJavaFXThread();
		return fxmlLoader.getController();
	}

	public void showAsStage(String title)
	{
		Platform.runLater(() -> {
			Stage stage = new Stage();
			stage.setTitle(title);
			stage.setScene(getScene());
			stage.sizeToScene();
			stage.show();
		});
	}

	public void showAndBlock(String title)
	{
		GuiAssertions.assertIsNotJavaFXThread();
		CompletableFuture<Integer> lock = new CompletableFuture<>();
		Platform.runLater(() -> {
			Stage stage = new Stage();
			stage.setTitle(title);
			stage.setScene(getScene());
			stage.sizeToScene();
			stage.showAndWait();
			Logging.errorPrint("passed wait");
			lock.completeAsync(() -> 0);

		});
		lock.join();
	}

}
