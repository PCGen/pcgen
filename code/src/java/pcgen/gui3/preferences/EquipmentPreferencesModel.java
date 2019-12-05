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

package pcgen.gui3.preferences;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * model for equipment preferences
 */
final class EquipmentPreferencesModel
{
    static final class Bounds
    {
        private Bounds(final int min, final int max)
        {
            this.min = min;
            this.max = max;
        }

        public final int min;
        public final int max;
    }

    private final IntegerProperty maxPotionLevel = new SimpleIntegerProperty();
    private final Bounds maxPotionLevelBounds = new Bounds(1, 9);
    private final IntegerProperty maxWandLevel = new SimpleIntegerProperty();
    private final Bounds maxWandLevelBounds = new Bounds(1, 9);

    IntegerProperty maxPotionLevelProperty()
    {
        return maxPotionLevel;
    }

    Bounds getMaxPotionLevelBounds()
    {
        return maxPotionLevelBounds;
    }

    IntegerProperty maxWandLevelProperty()
    {
        return maxWandLevel;
    }

    Bounds getMaxWandLevelBounds()
    {
        return maxWandLevelBounds;
    }


}
