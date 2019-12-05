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

package pcgen.gui3.preferences;

import pcgen.core.SettingsHandler;
import pcgen.gui3.ResettableController;
import pcgen.system.PCGenSettings;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;

public class LevelUpPreferencesPanelController implements ResettableController
{

    private final LevelUpPreferencesModel model = new LevelUpPreferencesModel();
    @FXML
    private CheckBox statWindow;
    @FXML
    private CheckBox warnFirstLevelUp;

    @FXML
    void initialize()
    {
        model.statWindowProperty().bind(statWindow.selectedProperty());
        model.warnFirstLevelUpProperty().bind(warnFirstLevelUp.selectedProperty());
    }

    @Override
    public void reset()
    {
        statWindow.selectedProperty().set(SettingsHandler.getShowStatDialogAtLevelUp());
        warnFirstLevelUp.selectedProperty().set(
                PCGenSettings.OPTIONS_CONTEXT.getBoolean(
                        PCGenSettings.OPTION_SHOW_WARNING_AT_FIRST_LEVEL_UP, true));
    }

    @Override
    public void apply()
    {
        SettingsHandler.setShowStatDialogAtLevelUp(statWindow.isSelected());
        PCGenSettings.OPTIONS_CONTEXT.setBoolean(PCGenSettings.OPTION_SHOW_WARNING_AT_FIRST_LEVEL_UP,
                warnFirstLevelUp.isSelected());
    }
}
