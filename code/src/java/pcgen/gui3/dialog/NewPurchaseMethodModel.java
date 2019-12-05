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

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Model for "new purchase method"  window
 */
public class NewPurchaseMethodModel
{
    private boolean cancelled = false;
    private final StringProperty name = new SimpleStringProperty();
    private final IntegerProperty points = new SimpleIntegerProperty();

    StringProperty nameProperty()
    {
        return name;
    }

    IntegerProperty pointsProperty()
    {
        return points;
    }

    /**
     * This class is a dialog and returns its result.
     * We should probably make this a real {@code javafx.scene.control.Dialog}
     * In the meantime this returns said state.
     *
     * @return if the dialog was closed via cancel or "ok"
     */
    public boolean isCancelled()
    {
        return cancelled;
    }

    /**
     * Sets cancelled state. See isCancelled for details
     *
     * @param cancelled state to set
     */
    void setCancelled(boolean cancelled)
    {
        this.cancelled = cancelled;
    }
}
