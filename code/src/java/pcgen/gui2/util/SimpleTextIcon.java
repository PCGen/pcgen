/*
 * Copyright 2008 Connor Petty <cpmeister@users.sourceforge.net>
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
 *
 */
package pcgen.gui2.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Graphics;

import javax.swing.Icon;

/**
 * This class creates an icon out of a string of text. A SimpleTextIcon is particularly useful
 * in situations where using text alone does not delivered the desired UI effect. Because icons
 * have special treatment in the swing UI the SimpleTextIcon can be employed for several unique
 * cases. For instance a SimpleTextIcon can be used to display different texts for a button's
 * rollover event.
 * *Note* The SimpleTextIcon does not treat html text specially so it will be displayed verbatim.
 */
public class SimpleTextIcon implements Icon
{

    private final String text;
    private final FontMetrics metrics;
    private final Color color;

    /**
     * Creates a new SimpleTextIcon that displays the text as the given color.
     * The Component argument is used to retrive the font and font metrics that will be used to
     * display the string.
     *
     * @param c     the Component used to render the text
     * @param text  the string to be displayed
     * @param color the color to display the text
     */
    public SimpleTextIcon(Component c, String text, Color color)
    {
        this.text = text;
        this.metrics = c.getFontMetrics(c.getFont());
        this.color = color;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y)
    {
        g.setColor(color);
        g.setFont(metrics.getFont());
        g.drawString(text, x, y + metrics.getAscent());
    }

    @Override
    public int getIconWidth()
    {
        return metrics.stringWidth(text);
    }

    @Override
    public int getIconHeight()
    {
        return metrics.getHeight();
    }

}
