/*
 * Copyright 2010 (C) Connor
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
import java.awt.Graphics;

import javax.swing.Icon;

public class SignIcon implements Icon
{

    public enum Sign
    {
        Plus, Minus
    }

    private final Sign sign;

    public SignIcon(Sign sign)
    {
        this.sign = sign;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y)
    {
        g.setColor(Color.BLACK);
        g.fillRect(x, y + 3, 9, 3);
        if (sign == Sign.Plus)
        {
            g.fillRect(x + 3, y, 3, 9);
        }
    }

    @Override
    public int getIconWidth()
    {
        return 9;
    }

    @Override
    public int getIconHeight()
    {
        return 9;
    }

}
