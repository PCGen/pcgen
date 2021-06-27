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

import pcgen.gui3.GuiAssertions;
import pcgen.system.LanguageBundle;
import pcgen.util.Logging;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * UI Loader for the DebugDialog
 * Primary difference from {@see JFXPanelFromResource} is that
 * it tells the controller when its hidden to shut off the timers.
 * It is also a singleton and only ever shows a single stage.
 */
public final class DebugDialog
{

	private static Stage primaryStage = null;

	private DebugDialog()
	{
	}

	/**
	 * gets the stage associated with the debug dialog.
	 * must be synchronized as it inits primaryStage.
	 */
	private static synchronized Stage getStage()
	{
		GuiAssertions.assertIsJavaFXThread();
		if (primaryStage != null)
		{
			return primaryStage;
		}
		FXMLLoader loader = new FXMLLoader();
		loader.setResources(LanguageBundle.getBundle());
		loader.setLocation(DebugDialog.class.getResource("DebugDialog.fxml"));
		primaryStage = new Stage();
		final Scene scene;
		try
		{
			scene = loader.load();
		}
		catch (IOException e)
		{
			Logging.errorPrint("failed to load debugdialog", e);
			// this can only happen with invalid fxml above and can't actually happen in real life
			throw new RuntimeException(e);
		}
		primaryStage.setScene(scene);
		DebugDialogController controller = loader.getController();
		primaryStage.setOnShown(e -> controller.initTimer());
		return primaryStage;
	}

	public static void show()
	{
		GuiAssertions.assertIsJavaFXThread();
		getStage().show();
	}
}
