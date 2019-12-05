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

import javafx.beans.property.Property;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Status bar model. Allows for a percent done and a message
 */
class PCGenStatusBarModel
{
    /**
     * must be a value between 0.0 and 1.0 or -1 (for indeterminate).
     */
    private final Property<Number> percentDone = new SimpleDoubleProperty();

    /**
     * message text shown on the left.
     */
    private final Property<String> message = new SimpleStringProperty();

    /**
     * message text shown on the progress bar itself
     */
    private final Property<String> progressText = new SimpleObjectProperty<>();

    Property<Number> percentDoneProperty()
    {
        return percentDone;
    }

    Property<String> messageProperty()
    {
        return message;
    }

    Property<String> progressText()
    {
        return progressText;
    }

}
