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

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;

public class MonsterPreferencesPanelController implements ResettableController
{
    @FXML
    private CheckBox ignoreMonsterHDCap;

    @Override
    public void apply()
    {
        SettingsHandler.setIgnoreMonsterHDCap(ignoreMonsterHDCap.isSelected());
    }

    @Override
    public void reset()
    {
        ignoreMonsterHDCap.setSelected(SettingsHandler.isIgnoreMonsterHDCap());
    }
}
