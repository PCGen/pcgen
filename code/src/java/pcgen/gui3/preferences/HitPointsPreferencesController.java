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

import pcgen.core.SettingsHandler;
import pcgen.gui3.ResettableController;

import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;

/**
 * This class is responsible for displaying hit points related preferences
 * and allowing the preferences to be edited by the user.
 */
public class HitPointsPreferencesController implements ResettableController
{
    @FXML
    private ToggleGroup hpModeGroup;
    @FXML
    private Spinner<Integer> hpPercentSpinner;
    @FXML
    private CheckBox maxHpAtFirstLevel;
    @FXML
    private CheckBox maxHpAtFirstClassLevel;

    @Override
    public void reset()
    {
        int hpRollMethod = SettingsHandler.getHPRollMethod();
        FilteredList<Toggle> filtered = hpModeGroup.getToggles().filtered(e -> (int) e.getUserData() == hpRollMethod);
        if (!filtered.isEmpty())
        {
            assert filtered.size() == 1;
            hpModeGroup.selectToggle(filtered.get(0));
        }

        hpPercentSpinner.getValueFactory().setValue(SettingsHandler.getHPPercent());
        maxHpAtFirstLevel.setSelected(SettingsHandler.isHPMaxAtFirstLevel());
        maxHpAtFirstClassLevel.setSelected(SettingsHandler.isHPMaxAtFirstClassLevel());

    }

    @Override
    public void apply()
    {
        SettingsHandler.setHPRollMethod((int) hpModeGroup.getSelectedToggle().getUserData());
        SettingsHandler.setHPPercent(hpPercentSpinner.getValue());
        SettingsHandler.setHPMaxAtFirstLevel(maxHpAtFirstLevel.isSelected());
        SettingsHandler.setHPMaxAtFirstClassLevel(maxHpAtFirstClassLevel.isSelected());

    }
}
