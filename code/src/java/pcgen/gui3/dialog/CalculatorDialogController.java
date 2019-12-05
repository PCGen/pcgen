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

import pcgen.core.Globals;
import pcgen.core.VariableProcessor;
import pcgen.facade.core.CharacterFacade;
import pcgen.facade.util.ReferenceFacade;
import pcgen.gui2.PCGenFrame;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 * A debug utility that ones to directly write a formula expression
 * and get a result.
 */
public class CalculatorDialogController
{
    @FXML
    private TextField formulaText;
    @FXML
    private TextArea outputText;

    @FXML
    private void onCalculate(final ActionEvent actionEvent)
    {
        String formula = formulaText.getText();
        // This is really temporary until everything is converted
        // to JavaFX and we could actually legally access our parent.
        PCGenFrame rootFrame = (PCGenFrame) Globals.getRootFrame();
        ReferenceFacade<CharacterFacade> selectedCharacterRef = rootFrame.getSelectedCharacterRef();
        CharacterFacade currentPC = selectedCharacterRef.get();

        if (currentPC != null)
        {
            VariableProcessor vp = currentPC.getVariableProcessor();
            vp.pauseCache();
            outputText.setText(outputText.getText()
                    + currentPC.getNameRef() + ": " + formula + " = " + currentPC.getVariable(formula, true) + '\n');
            vp.restartCache();
        } else
        {
            outputText.setText(outputText.getText() + "No character currently selected.\n");
        }
        formulaText.requestFocus();

    }

    @FXML
    private void onClear(final ActionEvent actionEvent)
    {
        outputText.clear();
    }
}
