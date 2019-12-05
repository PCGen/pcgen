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

package pcgen.gui3.component;

import java.io.IOException;

import pcgen.gui3.core.IORuntimeException;
import pcgen.system.LanguageBundle;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;

/**
 * This is a straightforward panel with an "ok"
 * and "close" button.
 */
public final class OKCloseButtonBar extends ButtonBar
{
    private final EventHandler<ActionEvent> okAction;
    private final EventHandler<ActionEvent> cancelAction;

    @FXML
    private Button okButton;

    @FXML
    private Button cancelButton;

    /**
     * @param okAction     what happens when OK is called
     * @param cancelAction what happens when cancel is called
     */
    public OKCloseButtonBar(EventHandler<ActionEvent> okAction,
            EventHandler<ActionEvent> cancelAction)
    {
        try
        {
            this.okAction = okAction;
            this.cancelAction = cancelAction;
            FXMLLoader loader = new FXMLLoader();
            loader.setResources(LanguageBundle.getBundle());
            loader.setController(this);
            loader.setRoot(this);
            loader.setLocation(getClass().getResource("OKCloseButtonBar.fxml"));
            loader.load();
        } catch (IOException e)
        {
            throw new IORuntimeException(e);
        }
    }

    @FXML
    void initialize()
    {
        okButton.setOnAction(okAction);
        cancelButton.setOnAction(cancelAction);
    }

    public Button getOkButton()
    {
        return okButton;
    }

    public Button getCancelButton()
    {
        return cancelButton;
    }
}
