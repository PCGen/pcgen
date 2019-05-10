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

package pcgen.gui3.preloader;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import pcgen.system.PCGenTaskEvent;
import pcgen.system.PCGenTaskListener;
import pcgen.system.ProgressContainer;
import pcgen.util.Logging;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * This is the application logic for the "Splash Screen" when loading PCGEn
 * It isn't directly the controller for the UI, but interacts the view
 * and interacts with the controller.
 * Once we're 100% on JavaFX can possibly be replaced with the native Preloader,
 * but requires thought.
 *
 * @see pcgen.gui3.JFXPanelFromResource
 */
public class PCGenPreloader implements PCGenTaskListener
{

	private final FXMLLoader loader = new FXMLLoader();
	private Stage primaryStage;
	private final CountDownLatch waitForLoad = new CountDownLatch(1);


	public PCGenPreloader()
	{
		loader.setLocation(getClass().getResource("PCGenPreloader.fxml"));
		Platform.runLater(() -> {
			primaryStage = new Stage();
			final Scene scene;
			try
			{
				scene = loader.load();
				waitForLoad.countDown();
			} catch (IOException e)
			{
				Logging.errorPrint("failed to load preloader", e);
				return;
			}

			primaryStage.setScene(scene);
			primaryStage.show();
		});
	}

	/**
	 * @return the controller for the preloader
	 */
	public PCGenPreloaderController getController()
	{
		try
		{
			waitForLoad.await();
		} catch (InterruptedException e)
		{
			Logging.errorPrint("waiting for load was interupted");
		}
		return (PCGenPreloaderController)loader.getController();
	}

	@Override
	public void progressChanged(final PCGenTaskEvent event)
	{
		ProgressContainer task = event.getSource();
		getController().setProgress(task.getMessage(), task.getProgress() / (double)task.getMaximum());
	}

	@Override
	public void errorOccurred(final PCGenTaskEvent event)
	{
		Logging.errorPrint("ignore this for now. Eventually do something useful");
		throw new UnsupportedOperationException("Not supported yet.");

	}

	/**
	 * indicates that preloading is done. splash screen should "go away"
	 */
	public void done()
	{
		Platform.runLater(() -> primaryStage.close());
	}
}
