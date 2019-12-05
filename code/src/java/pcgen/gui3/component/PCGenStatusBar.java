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

package pcgen.gui3.component;

import java.io.IOException;

import pcgen.gui3.core.IORuntimeException;
import pcgen.system.LanguageBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

/**
 * Reusuable status bar. Displays a message + Progress Bar.
 */
public final class PCGenStatusBar extends HBox
{
    private final PCGenStatusBarModel model = new PCGenStatusBarModel();

    @FXML
    private Text loadingLabel;

    @FXML
    private ProgressBar loadProgress;

    @FXML
    private Label progressText;

    public PCGenStatusBar()
    {
        try
        {
            FXMLLoader loader = new FXMLLoader();
            loader.setResources(LanguageBundle.getBundle());
            loader.setController(this);
            loader.setRoot(this);
            loader.setLocation(getClass().getResource("PCGenStatusBar.fxml"));
            loader.load();
        } catch (IOException e)
        {
            throw new IORuntimeException(e);
        }
    }

    @FXML
    void initialize()
    {
        loadingLabel.textProperty().bind(model.messageProperty());
        loadProgress.progressProperty().bind(model.percentDoneProperty());
        progressText.textProperty().bind(model.progressText());
    }

    public void setProgress(String message, double progress)
    {
        setProgress(message, progress, String.format("%.0f%%", progress * 100));
    }

    public void setProgress(String message, double progress, String progressText)
    {
        Platform.runLater(() -> {
            model.messageProperty().setValue(message);
            model.percentDoneProperty().setValue(progress);
            model.progressText().setValue(progressText);
        });
    }

}
