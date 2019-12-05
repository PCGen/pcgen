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

package pcgen.gui3.utilty;

import javafx.scene.paint.Color;
import org.jetbrains.annotations.Nullable;

/**
 * Utility functions for color related things.
 */
public final class ColorUtilty
{
    private ColorUtilty()
    {
    }

    /**
     * Provides string from a Color which can be passed to {@code Color.valueOf}
     *
     * @param color color to convert
     */
    public static String colorToRGBString(final Color color)
    {
        return String.format(
                "#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255)
        );
    }

    /**
     * @param color JavaFX color
     * @return AWT color
     */
    @Nullable
    public static java.awt.Color colorToAWTColor(final Color color)
    {
        if (color == null)
        {
            return null;
        }
        return new java.awt.Color(
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255)
        );
    }

}
