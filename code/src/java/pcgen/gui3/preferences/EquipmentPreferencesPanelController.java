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

import javafx.fxml.FXML;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;

/**
 * A panel for showing preferences relating to how to use equipment.
 * Long term this might get controlled by data, but keeping for now
 * due to legacy settings (and to get some experience with settings
 * as a javafx panel)
 */
public class EquipmentPreferencesPanelController implements ResettableController
{
    @FXML
    private Spinner<Integer> wandSpinner;

    @FXML
    private Spinner<Integer> potionSpinner;

    private final EquipmentPreferencesModel model = new EquipmentPreferencesModel();

    @FXML
    void initialize()
    {
        var potionValueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(model.getMaxPotionLevelBounds().min,
                        model.getMaxWandLevelBounds().max,
                        SettingsHandler.maxPotionSpellLevel().getValue(), 1
                );
        potionSpinner.setValueFactory(potionValueFactory);
        var wandValueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(model.getMaxWandLevelBounds().min,
                        model.getMaxWandLevelBounds().max,
                        SettingsHandler.maxWandSpellLevel().getValue(), 1
                );
        wandSpinner.setValueFactory(wandValueFactory);

        // TODO: consider adding an apply button that sets values rather than using binding directly
        model.maxPotionLevelProperty().bind(potionSpinner.valueProperty());
        model.maxWandLevelProperty().bind(wandSpinner.valueProperty());
    }

    // TOOD: eventually apply/reset ought to be composed behavior
    // and really ought to be implemented as "copy initial model to safe space; and
    // copy it back from the saved model when we reset.
    // for now, do this to make some progress.

    @Override
    public void reset()
    {
        potionSpinner.getValueFactory().setValue(SettingsHandler.maxPotionSpellLevel().getValue());
        wandSpinner.getValueFactory().setValue(SettingsHandler.maxWandSpellLevel().getValue());
    }

    @Override
    public void apply()
    {
        SettingsHandler.maxPotionSpellLevel().set(model.maxPotionLevelProperty().get());
        SettingsHandler.maxWandSpellLevel().set(model.maxWandLevelProperty().get());
    }
}
