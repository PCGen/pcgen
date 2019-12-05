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

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import pcgen.core.GameMode;
import pcgen.core.SettingsHandler;
import pcgen.gui2.UIPropertyContext;
import pcgen.gui3.ResettableController;
import pcgen.system.ConfigurationSettings;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

/**
 * The Defaults Panel is responsible for
 * setting various defaults for characters that can be changed
 * on a per character basis, such as experience table, character
 * type, and so on.
 */
public class DefaultsPreferencesPanelController implements ResettableController
{
    private static final String DEFAULT_PREVIEW_SHEET_KEY = "CharacterSheetInfoTab.defaultPreviewSheet.";
    @FXML
    private ComboBox<String> xpTableCombo;
    @FXML
    private ComboBox<String> characterTypeCombo;
    @FXML
    private ComboBox<String> previewSheetCombo;

    private static List<String> getPreviewDirecoryOptions(GameMode gameMode)
    {
        String previewDir = ConfigurationSettings.getPreviewDir();
        File sheetDir = new File(previewDir, gameMode.getCharSheetDir());
        if (sheetDir.exists() && sheetDir.isDirectory())
        {
            File[] files = sheetDir.listFiles();
            if (files == null)
            {
                return Collections.emptyList();
            } else
            {
                return Arrays.stream(files)
                        .map(File::toString)
                        .collect(Collectors.toList());
            }

        } else
        {
            return Collections.emptyList();
        }

    }

    @Override
    public void reset()
    {
        /*
         * much of this data should be driven by a
         * model and the controller should actually be getting
         * a observableList to attach.
         */

        final GameMode gameMode = SettingsHandler.getGame();

        final String xpTableName = gameMode.getDefaultXPTableName();
        List<String> xpTableNames = gameMode.getXPTableNames();
        ObservableList<String> xpSheetItems =
                FXCollections.observableArrayList(xpTableNames);
        xpTableCombo.setItems(xpSheetItems);
        xpTableCombo.getSelectionModel().select(xpTableName);

        final String characterType = gameMode.getDefaultCharacterType();
        List<String> characterTypes = gameMode.getCharacterTypeList();
        ObservableList<String> characterTypeItems =
                FXCollections.observableArrayList(characterTypes);
        characterTypeCombo.setItems(characterTypeItems);
        characterTypeCombo.getSelectionModel().select(characterType);

        final String previewSheet = UIPropertyContext.getInstance().initProperty(DEFAULT_PREVIEW_SHEET_KEY + gameMode,
                gameMode.getDefaultPreviewSheet());
        ObservableList<String> previewSheetItems =
                FXCollections.observableArrayList(getPreviewDirecoryOptions(gameMode));
        previewSheetCombo.setItems(previewSheetItems);
        previewSheetCombo.getSelectionModel().select(previewSheet);
    }

    @Override
    public void apply()
    {
        final GameMode gameMode = SettingsHandler.getGame();
        gameMode.setDefaultXPTableName(xpTableCombo.getSelectionModel().getSelectedItem());
        gameMode.setDefaultCharacterType(characterTypeCombo.getSelectionModel().getSelectedItem());
        gameMode.setDefaultPreviewSheet(previewSheetCombo.getSelectionModel().getSelectedItem());

        UIPropertyContext.getInstance().setProperty(DEFAULT_PREVIEW_SHEET_KEY + gameMode.getName(),
                previewSheetCombo.getSelectionModel().getSelectedItem()
        );
    }
}
