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
import pcgen.gui2.UIPropertyContext;
import pcgen.gui3.GuiAssertions;
import pcgen.gui3.ResettableController;
import pcgen.system.PCGenSettings;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;

/**
 * Preferences panel that controls how the GUI presents information.
 */
public class DisplayOptionsPreferencesPanelController implements ResettableController
{

    private final DisplayOptionsPreferencesPanelModel model = new DisplayOptionsPreferencesPanelModel();
    @FXML
    private CheckBox showSkillRanks;
    @FXML
    private CheckBox showSkillModifier;
    @FXML
    private CheckBox useOutputNamesEquipment;
    @FXML
    private CheckBox useOutputNamesSpells;
    @FXML
    private CheckBox useOutputNamesOther;
    @FXML
    private ComboBox<String> cmbChoiceMethods;

    @FXML
    void initialize()
    {
        cmbChoiceMethods.setItems(model.choiceOptionsAsObservableList());
    }


    @Override
    public void reset()
    {
        GuiAssertions.assertIsJavaFXThread();
        cmbChoiceMethods.getSelectionModel().select(UIPropertyContext.getSingleChoiceAction());
        showSkillModifier.setSelected(
                PCGenSettings.OPTIONS_CONTEXT.getBoolean(PCGenSettings.OPTION_SHOW_SKILL_MOD_BREAKDOWN, false));
        showSkillRanks.setSelected(
                PCGenSettings.OPTIONS_CONTEXT.getBoolean(PCGenSettings.OPTION_SHOW_SKILL_RANK_BREAKDOWN, false));
        useOutputNamesEquipment.setSelected(SettingsHandler.guiUsesOutputNameEquipment());
        useOutputNamesSpells.setSelected(SettingsHandler.guiUsesOutputNameSpells());
        useOutputNamesOther.setSelected(
                PCGenSettings.OPTIONS_CONTEXT.getBoolean(PCGenSettings.OPTION_SHOW_OUTPUT_NAME_FOR_OTHER_ITEMS, false));
    }

    @Override
    public void apply()
    {
        SettingsHandler.setGUIUsesOutputNameEquipment(useOutputNamesEquipment.isSelected());
        SettingsHandler.setGUIUsesOutputNameSpells(useOutputNamesSpells.isSelected());
        PCGenSettings.OPTIONS_CONTEXT.setBoolean(PCGenSettings.OPTION_SHOW_OUTPUT_NAME_FOR_OTHER_ITEMS,
                useOutputNamesOther.isSelected());
        UIPropertyContext.setSingleChoiceAction(cmbChoiceMethods.getSelectionModel().getSelectedIndex());
        PCGenSettings.OPTIONS_CONTEXT.setBoolean(PCGenSettings.OPTION_SHOW_SKILL_MOD_BREAKDOWN,
                showSkillModifier.isSelected());
        PCGenSettings.OPTIONS_CONTEXT.setBoolean(PCGenSettings.OPTION_SHOW_SKILL_RANK_BREAKDOWN,
                showSkillRanks.isSelected());

    }
}
