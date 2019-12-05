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

import pcgen.gui2.UIPropertyContext;
import pcgen.gui3.GuiAssertions;
import pcgen.gui3.ResettableController;

import javafx.fxml.FXML;
import javafx.scene.control.ColorPicker;

/**
 * The Colors Preferences Panel is responsible for
 * displaying color related preferences and allowing the
 * preferences to be edited by the user.
 */
public class ColorsPreferencesPanelController implements ResettableController
{
    @FXML
    private ColorPicker prereqQualifyColor;
    @FXML
    private ColorPicker prereqFailColor;
    @FXML
    private ColorPicker featAutoColor;
    @FXML
    private ColorPicker featVirtualColor;
    @FXML
    private ColorPicker sourceStatusRelease;
    @FXML
    private ColorPicker sourceStatusAlpha;
    @FXML
    private ColorPicker sourceStatusBeta;
    @FXML
    private ColorPicker sourceStatusTest;

    @Override
    public void reset()
    {
        GuiAssertions.assertIsJavaFXThread();
        prereqQualifyColor.setValue(UIPropertyContext.getQualifiedColor());
        prereqFailColor.setValue(UIPropertyContext.getNotQualifiedColor());
        featAutoColor.setValue(UIPropertyContext.getAutomaticColor());
        featVirtualColor.setValue(UIPropertyContext.getVirtualColor());

        sourceStatusRelease.setValue(UIPropertyContext.getSourceStatusReleaseColor());
        sourceStatusAlpha.setValue(UIPropertyContext.getSourceStatusAlphaColor());
        sourceStatusBeta.setValue(UIPropertyContext.getSourceStatusBetaColor());
        sourceStatusTest.setValue(UIPropertyContext.getSourceStatusTestColor());
    }

    @Override
    public void apply()
    {
        UIPropertyContext.setQualifiedColor(prereqQualifyColor.getValue());
        UIPropertyContext.setNotQualifiedColor(prereqFailColor.getValue());
        UIPropertyContext.setAutomaticColor(featAutoColor.getValue());
        UIPropertyContext.setVirtualColor(featVirtualColor.getValue());

        UIPropertyContext.setSourceStatusReleaseColor(sourceStatusRelease.getValue());
        UIPropertyContext.setSourceStatusAlphaColor(sourceStatusAlpha.getValue());
        UIPropertyContext.setSourceStatusBetaColor(sourceStatusBeta.getValue());
        UIPropertyContext.setSourceStatusTestColor(sourceStatusTest.getValue());

    }
}
