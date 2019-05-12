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

import java.io.File;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import pcgen.system.ConfigurationSettings;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import org.apache.commons.lang3.SystemUtils;

/**
 *
 */
public class OptionsPathDialogController
{
	// TODO: consider extending Dialog since this has native "return to user" features

	private final OptionsPathModel model = new OptionsPathModel();

	private final Executor DIALOG_CREATION_EXECUTOR = Executors.newSingleThreadExecutor();

	@FXML
	private RadioButton osSpecific;
	@FXML
	private Label selectedDirectoryLabel;
	@FXML
	private Scene scene;

	@FXML
	private void initialize()
	{
		model.getSelectedDirectory().addListener((observable, oldValue, newValue) ->
				selectedDirectoryLabel.setText(newValue));
		if (SystemUtils.IS_OS_MAC_OSX)
		{
			// TODO: i18n
			osSpecific.setText("Mac User Dir");
		}
		else if (SystemUtils.IS_OS_UNIX)
		{
			// TODO: i18n
			osSpecific.setText("Freedesktop configuration sub-directory: Use for most Linux/BSD");
		}
		else
		{
			osSpecific.setDisable(true);
			osSpecific.setVisible(false);
		}

	}

	@FXML
	private void selectCustomDir(final ActionEvent actionEvent)
	{
		/*
		 This is weird. We need to execute off of the UI thread, but don't really have way to return control
		 to the master thread. Instead we fork off into the nether for a bit.
		 However, we need to get data _from_ the UI so use a Future to return the UI thread.
		 A pattern we possibly want is a centralized application equvelent of Platform.runLater
		 rather than having every class implement their own executor.
		 */
		DIALOG_CREATION_EXECUTOR.execute(() -> {
			DirectoryChooser directoryChooser = new DirectoryChooser();
			directoryChooser.setInitialDirectory(new File(model.getSelectedDirectory().get()));

			File dir = CompletableFuture.supplyAsync(() ->
					directoryChooser.showDialog(scene.getWindow()), Platform::runLater).join();

			if (dir != null)
			{
				if (dir.listFiles().length > 0)
				{
					Dialog<ButtonType> alert = new Alert(Alert.AlertType.CONFIRMATION);
					// TODO: i18n
					alert.setTitle("Confirmation");
					alert.setHeaderText(null);
					alert.setContentText("The folder " + dir.getAbsolutePath() + " is not empty.\n"
							+ "All ini files in this directory may be overwritten. " + "Are you sure?");
					alert.initModality(Modality.APPLICATION_MODAL);
					alert.initOwner(scene.getWindow());

					Optional<ButtonType> result = alert.showAndWait();
					if (result.isPresent() && result.get() == ButtonType.OK)
					{
						model.getSelectedDirectory().set(dir.getPath());
					}
				}
			}
		});
	}

	@FXML
	private void selectOK(final ActionEvent actionEvent)
	{
		ConfigurationSettings.setSystemProperty(ConfigurationSettings.SETTINGS_FILES_PATH,
				model.getSelectedDirectory().get());
		scene.getWindow().hide();
	}

	@FXML
	private void onHomeDir(final ActionEvent actionEvent)
	{
		model.getSelectedDirectory().set(ConfigurationSettings.SettingsFilesPath.user.getSettingsDir());
	}

	@FXML
	private void onInstallDir(final ActionEvent actionEvent)
	{
		model.getSelectedDirectory().set(
				ConfigurationSettings.SettingsFilesPath.pcgen.getSettingsDir());
	}

	@FXML
	private void onOSSpecific(final ActionEvent actionEvent)
	{
		if (SystemUtils.IS_OS_MAC_OSX)
		{
			model.getSelectedDirectory().set(
					ConfigurationSettings.SettingsFilesPath.mac_user    .getSettingsDir());
		}
		else if (SystemUtils.IS_OS_UNIX)
		{
			model.getSelectedDirectory().set(
					ConfigurationSettings.SettingsFilesPath.FD_USER.getSettingsDir());
		}

	}
}
