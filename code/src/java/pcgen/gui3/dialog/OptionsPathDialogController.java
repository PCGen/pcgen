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

import pcgen.system.ConfigurationSettings;
import pcgen.util.Logging;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.DirectoryChooser;
import org.apache.commons.lang3.SystemUtils;

public class OptionsPathDialogController
{
    private final OptionsPathDialogModel model = new OptionsPathDialogModel();

    @FXML
    private RadioButton freedesktop;

    @FXML
    private RadioButton macUserDir;

    @FXML
    private TextField dirSelection;

    @FXML
    private Button selectButton;

    @FXML
    private ToggleGroup directoryGroup;

    @FXML
    private ButtonBar ok;

    @FXML
    private RadioButton select;

    @FXML
    private Scene optionsPathDialogScene;

    @FXML
    void initialize()
    {
        model.directoryProperty().bindBidirectional(dirSelection.textProperty());
        select.selectedProperty().addListener((
                (observable, oldValue, newValue) -> {
                    dirSelection.setDisable(!select.isSelected());
                    dirSelection.setEditable(select.isSelected());
                    selectButton.setDisable(!select.isSelected());
                }));

        if (!SystemUtils.IS_OS_MAC_OSX)
        {
            macUserDir.setVisible(false);
        }
        if (!SystemUtils.IS_OS_UNIX)
        {
            freedesktop.setVisible(false);
        }

        directoryGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            Logging.debugPrint("toggle changed " + observable);
            if (newValue.getUserData() != null)
            {
                String userData = (String) newValue.getUserData();
                Logging.debugPrint("user data is " + userData);
                String newDir = ConfigurationSettings.getSettingsDirFromFilePath(userData);
                model.directoryProperty().setValue(newDir);
            }
        });
    }

    @FXML
    private void onConfirm(final ActionEvent actionEvent)
    {
        ConfigurationSettings.setSystemProperty(
                ConfigurationSettings.SETTINGS_FILES_PATH,
                model.directoryProperty().getValue()
        );
        optionsPathDialogScene.getWindow().hide();
    }

    @FXML
    private void doChooser(final ActionEvent actionEvent)
    {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        String modelDirectory = model.directoryProperty().getValue();
        if (!modelDirectory.isBlank())
        {
            directoryChooser.setInitialDirectory(new File(model.directoryProperty().getValue()));
        }

        File dir = directoryChooser.showDialog(optionsPathDialogScene.getWindow());

        if (dir != null)
        {
            if (dir.listFiles().length > 0)
            {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Directory Not Empty");
                alert.setContentText("The folder " + dir.getAbsolutePath() + " is not empty.\n"
                        + "All ini files in this directory may be overwritten. " + "Are you sure?");
                Optional<ButtonType> buttonType = alert.showAndWait();
                buttonType.ifPresent(option -> {
                    if (option != ButtonType.YES)
                    {
                        return;
                    }
                });
            }
            model.directoryProperty().setValue(dir.getAbsolutePath());
        }
    }
}
