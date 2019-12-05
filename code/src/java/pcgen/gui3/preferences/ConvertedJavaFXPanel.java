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

import pcgen.gui2.prefs.PCGenPrefsPanel;
import pcgen.gui3.GuiAssertions;
import pcgen.gui3.JFXPanelFromResource;
import pcgen.gui3.ResettableController;
import pcgen.system.LanguageBundle;

import javafx.application.Platform;

public final class ConvertedJavaFXPanel<T extends ResettableController> extends PCGenPrefsPanel
{
    private final String titleTextKey;
    private final JFXPanelFromResource<T> panel;

    public ConvertedJavaFXPanel(Class<T> klass, String resource, String titleTextKey)
    {
        this.titleTextKey = titleTextKey;
        this.panel =
                new JFXPanelFromResource<>(
                        klass,
                        resource
                );
        this.add(panel);

    }

    @Override
    public String getTitle()
    {
        return LanguageBundle.getString(titleTextKey);
    }

    @Override
    public void applyOptionValuesToControls()
    {
        GuiAssertions.assertIsNotJavaFXThread();
        Platform.runLater(() ->
                panel.getControllerFromJavaFXThread().reset()
        );
    }

    @Override
    public void setOptionsBasedOnControls()
    {
        panel.getController().apply();
    }

    public T getController()
    {
        return this.panel.getController();
    }

    @Override
    public String toString()
    {
        return getTitle();
    }
}
