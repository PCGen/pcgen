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

import pcgen.cdom.base.Constants;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.stage.Window;

// consider using Dialog rather than implementing this ourselves

/**
 * Controller for the "new purchase method" window. This is rather specific window
 * that allows one to add a new "point value" that can be used to purchase skills.
 */
public class NewPurchaseMethodDialogController
{
    private final NewPurchaseMethodModel model = new NewPurchaseMethodModel();
    @FXML
    private Scene newPurchaseDialog;
    @FXML
    private TextField nameEdit;

    // should this be a slider or other numeric thing?
    @FXML
    private Slider pointsEdit;
    @FXML
    private Label shownCount;

    @FXML
    void initialize()
    {
        model.nameProperty().bind(nameEdit.textProperty());
        model.pointsProperty().bind(pointsEdit.valueProperty());
    }

    @FXML
    private void onOk(final ActionEvent actionEvent)
    {
        // possibly replace with ControlsFX validation framework

        if (getEnteredName().isEmpty())
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(Constants.APPLICATION_NAME);
            // todo: i18n
            alert.setContentText("Please enter a name for this method.");
            alert.initOwner(newPurchaseDialog.getWindow());
            alert.showAndWait();
            return;
        }

        model.setCancelled(false);
        Platform.runLater(() -> {
            Window window = newPurchaseDialog.getWindow();
            window.hide();
        });
    }

    @FXML
    private void onCancel(final ActionEvent actionEvent)
    {
        model.setCancelled(true);
        Platform.runLater(() -> {
            Window window = newPurchaseDialog.getWindow();
            window.hide();
        });
    }

    public boolean isCancelled()
    {
        return model.isCancelled();
    }

    public String getEnteredName()
    {
        // shouldn't this be part of the model - rather than have the controller modify
        // and pass through
        // should we just directly return the model and let the caller get it from
        // the property ?
        return model.nameProperty().get().trim();
    }

    public int getEnteredPoints()
    {
        return model.pointsProperty().get();
    }

}
