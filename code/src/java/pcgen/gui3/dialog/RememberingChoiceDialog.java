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

package pcgen.gui3.dialog;

import java.util.Objects;

import pcgen.gui3.GuiAssertions;
import pcgen.gui3.GuiUtility;
import pcgen.system.LanguageBundle;
import pcgen.system.PropertyContext;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.web.WebView;

public final class RememberingChoiceDialog
{
    private RememberingChoiceDialog()
    {
    }

    // TODO: there are too many variables here. It should either be a builder
    // or something that extends Alert/Dialog. This is mainly here to keep this
    // in one place to allow us to fix later

    public static Alert create(final String title,
            final String header,
            final String htmlContent,
            final String checkboxContentKey,
            final PropertyContext context,
            final String option
    )
    {
        GuiAssertions.assertIsNotJavaFXThread();
        Objects.requireNonNull(title);
        Objects.requireNonNull(header);
        Objects.requireNonNull(htmlContent);
        Objects.requireNonNull(context);
        Objects.requireNonNull(option);

        Alert alert = GuiUtility.runOnJavaFXThreadNow(() -> new Alert(Alert.AlertType.INFORMATION));
        alert.setTitle(title);
        alert.setContentText(null);
        alert.setHeaderText(header);
        CheckBox showLicense = new CheckBox(LanguageBundle.getString(checkboxContentKey));
        showLicense.selectedProperty().addListener((observableValue, oldValue, newValue) ->
                context.setBoolean(option, newValue));
        showLicense.setSelected(context.getBoolean(option));
        Platform.runLater(() -> {
            WebView webView = new WebView();
            webView.getEngine().loadContent(htmlContent);
            alert.getDialogPane().setContent(webView);
        });
        alert.getDialogPane().setExpandableContent(showLicense);
        return alert;
    }
}
