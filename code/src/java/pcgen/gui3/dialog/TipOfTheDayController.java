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

import pcgen.gui2.UIPropertyContext;
import pcgen.gui2.tools.TipOfTheDayHandler;
import pcgen.gui3.GuiAssertions;
import pcgen.system.LanguageBundle;
import pcgen.system.PropertyContext;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.web.WebView;

/**
 * This provides a popup that shows users some simple ways to improve their experience
 * The tips themselves are provided by a properties file and controlled by
 * {@see pcgen.gui2.tools.TipOfTheDayHandler}
 */
public class TipOfTheDayController
{

    private static final PropertyContext PROPERTY_CONTEXT = UIPropertyContext.createContext("TipOfTheDay");

    @FXML
    private CheckBox showTips;

    private static final String HTML_START =
            "<html><body style=\"margin-left: 5px;margin-right: 5px;margin-top: 5px\">";
    private static final String HTML_END = "</body></html>";
    private final TipOfTheDayHandler tipHandler = TipOfTheDayHandler.getInstance();
    @FXML
    private WebView browser;

    @FXML
    void initialize()
    {
        // ideally, this should be become a bindable "BeanProperty" rather than
        // a changeListener based construct.
        showTips.setSelected(
                PROPERTY_CONTEXT.initBoolean("showTipOfTheDay", true)
        );
        showTips.selectedProperty().addListener((ov, old_val, new_val) -> PROPERTY_CONTEXT.setBoolean("showTipOfTheDay", showTips.isSelected()));
        showNextTip();
    }

    @FXML
    private void onClose(final ActionEvent actionEvent)
    {
        var button = (Button) actionEvent.getSource();
        button.getScene().getWindow().hide();
    }

    @FXML
    private void onNextTip(final ActionEvent actionEvent)
    {
        showNextTip();
    }

    @FXML
    private void onPrevTip(final ActionEvent actionEvent)
    {
        showPrevTip();
    }

    private void showNextTip()
    {
        if (tipHandler.hasTips())
        {
            showTip(tipHandler.getNextTip());
        }
    }

    private void showPrevTip()
    {
        if (tipHandler.hasTips())
        {
            showTip(tipHandler.getPrevTip());
        }
    }

    private void showTip(final String tip)
    {
        GuiAssertions.assertIsJavaFXThread();
        browser.getEngine().loadContent(buildTipText(tip));
    }

    private String buildTipText(String tip)
    {
        return String.format("%s%s%s", HTML_START,
                LanguageBundle.getFormattedString("in_tod_tipDisplay",
                        Integer.toString(tipHandler.getLastNumber() + 1), tip),
                HTML_END);
    }
}
