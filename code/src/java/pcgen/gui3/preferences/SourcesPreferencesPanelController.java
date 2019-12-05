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

import java.util.List;

import pcgen.cdom.enumeration.SourceFormat;
import pcgen.core.Globals;
import pcgen.core.SettingsHandler;
import pcgen.gui2.UIPropertyContext;
import pcgen.gui3.GuiAssertions;
import pcgen.gui3.ResettableController;
import pcgen.system.LanguageBundle;
import pcgen.system.PCGenSettings;
import pcgen.util.Logging;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;

public class SourcesPreferencesPanelController implements ResettableController
{
    @FXML
    private CheckBox campLoad;
    @FXML
    private CheckBox charCampLoad;
    @FXML
    private CheckBox allowOptsInSource;
    @FXML
    private CheckBox saveCustom;
    @FXML
    private CheckBox showOGL;
    @FXML
    private CheckBox showMature;
    @FXML
    private ChoiceBox<String> sourceOptions;
    @FXML
    private CheckBox loadURL;
    @FXML
    private CheckBox allowOverride;
    @FXML
    private CheckBox skipSourceSelect;
    @FXML
    private CheckBox useAdvancedSourceSelect;
    @FXML
    private CheckBox allowMultiLineObjectsSelect;

    @FXML
    private void initialize()
    {
        var choices = FXCollections.observableArrayList(List.of(
                LanguageBundle.getString("in_Prefs_sdLong"),
                LanguageBundle.getString("in_Prefs_sdMedium"),
                LanguageBundle.getString("in_Prefs_sdShort"),
                LanguageBundle.getString("in_Prefs_sdPage"),
                LanguageBundle.getString("in_Prefs_sdWeb")));
        sourceOptions.setItems(choices);
    }


    @Override
    public void apply()
    {
        PCGenSettings.OPTIONS_CONTEXT.setBoolean(PCGenSettings.OPTION_AUTOLOAD_SOURCES_AT_START, campLoad.isSelected());
        PCGenSettings.OPTIONS_CONTEXT.setBoolean(PCGenSettings.OPTION_AUTOLOAD_SOURCES_WITH_PC,
                charCampLoad.isSelected());
        PCGenSettings.OPTIONS_CONTEXT.setBoolean(PCGenSettings.OPTION_ALLOWED_IN_SOURCES,
                allowOptsInSource.isSelected());
        PCGenSettings.OPTIONS_CONTEXT.setBoolean(PCGenSettings.OPTION_SAVE_CUSTOM_EQUIPMENT, saveCustom.isSelected());
        PCGenSettings.OPTIONS_CONTEXT.setBoolean(PCGenSettings.OPTION_SHOW_LICENSE, showOGL.isSelected());
        PCGenSettings.OPTIONS_CONTEXT.setBoolean(PCGenSettings.OPTION_SHOW_MATURE_ON_LOAD, showMature.isSelected());
        SettingsHandler.setLoadURLs(loadURL.isSelected());
        PCGenSettings.OPTIONS_CONTEXT.setBoolean(PCGenSettings.OPTION_ALLOW_OVERRIDE_DUPLICATES,
                allowOverride.isSelected());

        UIPropertyContext.getInstance().setBoolean(UIPropertyContext.SKIP_SOURCE_SELECTION,
                skipSourceSelect.isSelected());
        UIPropertyContext.getInstance().setBoolean(UIPropertyContext.SOURCE_USE_BASIC_KEY,
                !useAdvancedSourceSelect.isSelected());
        PCGenSettings.OPTIONS_CONTEXT.setBoolean(PCGenSettings.OPTION_SOURCES_ALLOW_MULTI_LINE,
                allowMultiLineObjectsSelect.isSelected());

        switch (sourceOptions.getSelectionModel().getSelectedIndex())
        {
            case 0:
                Globals.setSourceDisplay(SourceFormat.LONG);
                break;

            case 1:
                Globals.setSourceDisplay(SourceFormat.MEDIUM);
                break;

            case 2:
                Globals.setSourceDisplay(SourceFormat.SHORT);
                break;

            case 3:
                Globals.setSourceDisplay(SourceFormat.PAGE);
                break;

            case 4:
                Globals.setSourceDisplay(SourceFormat.WEB);
                break;

            default:
                Logging.errorPrint("In PreferencesDialog.setOptionsBasedOnControls " + "(sourceOptions) the index "
                        + sourceOptions.getSelectionModel().getSelectedIndex() + " is unsupported.");

                break;
        }
    }

    @Override
    public void reset()
    {
        GuiAssertions.assertIsJavaFXThread();
        campLoad.setSelected(
                PCGenSettings.OPTIONS_CONTEXT.initBoolean(PCGenSettings.OPTION_AUTOLOAD_SOURCES_AT_START, false));
        charCampLoad.setSelected(
                PCGenSettings.OPTIONS_CONTEXT.initBoolean(PCGenSettings.OPTION_AUTOLOAD_SOURCES_WITH_PC, true));
        allowOptsInSource
                .setSelected(PCGenSettings.OPTIONS_CONTEXT.initBoolean(PCGenSettings.OPTION_ALLOWED_IN_SOURCES, true));

        saveCustom.setSelected(PCGenSettings.OPTIONS_CONTEXT.getBoolean(PCGenSettings.OPTION_SAVE_CUSTOM_EQUIPMENT));
        showOGL.setSelected(PCGenSettings.OPTIONS_CONTEXT.getBoolean(PCGenSettings.OPTION_SHOW_LICENSE));
        showMature.setSelected(PCGenSettings.OPTIONS_CONTEXT.getBoolean(PCGenSettings.OPTION_SHOW_MATURE_ON_LOAD));
        loadURL.setSelected(SettingsHandler.isLoadURLs());
        allowOverride.setSelected(
                PCGenSettings.OPTIONS_CONTEXT.initBoolean(PCGenSettings.OPTION_ALLOW_OVERRIDE_DUPLICATES, true));
        skipSourceSelect
                .setSelected(UIPropertyContext.getInstance().getBoolean(UIPropertyContext.SKIP_SOURCE_SELECTION));
        useAdvancedSourceSelect
                .setSelected(!UIPropertyContext.getInstance().getBoolean(UIPropertyContext.SOURCE_USE_BASIC_KEY));
        allowMultiLineObjectsSelect
                .setSelected(PCGenSettings.OPTIONS_CONTEXT.getBoolean(PCGenSettings.OPTION_SOURCES_ALLOW_MULTI_LINE));

        switch (Globals.getSourceDisplay())
        {
            case LONG:
                sourceOptions.getSelectionModel().select(0);

                break;

            case MEDIUM:
                sourceOptions.getSelectionModel().select(1);

                break;

            case SHORT:
                sourceOptions.getSelectionModel().select(2);

                break;

            case PAGE:
                sourceOptions.getSelectionModel().select(3);

                break;

            case WEB:
                sourceOptions.getSelectionModel().select(4);

                break;

            default:
                Logging.errorPrint(
                        "In PreferencesDialog.applyOptionValuesToControls " + "(source display) the option "
                                + Globals.getSourceDisplay() + " is unsupported.");

                break;
        }
    }
}
