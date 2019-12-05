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

import java.util.Arrays;
import java.util.Collection;

import pcgen.system.LanguageBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Model for "Display Preferences" panel that controls how the GUI presents information.
 */
class DisplayOptionsPreferencesPanelModel
{
    private static final String IN_CM_NONE =
            LanguageBundle.getString("in_Prefs_cmNone");
    private static final String IN_CM_SELECT =
            LanguageBundle.getString("in_Prefs_cmSelect");
    private static final String IN_CM_SELECT_EXIT =
            LanguageBundle.getString("in_Prefs_cmSelectExit");

    private static final Collection<String> SINGLE_CHOICE_METHODS =
            Arrays.asList(IN_CM_NONE, IN_CM_SELECT, IN_CM_SELECT_EXIT);

    /**
     * provides the potencial options for when a Chooser has only
     * one legal option
     *
     * @return a List usable in JavaFX
     */
    ObservableList<String> choiceOptionsAsObservableList()
    {
        return FXCollections.observableArrayList(SINGLE_CHOICE_METHODS);
    }
}
