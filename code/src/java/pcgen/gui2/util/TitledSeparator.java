/*
 * TitledSeparator.java
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
 * Created on Aug 23, 2008, 3:26:37 PM
 */
package pcgen.gui2.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.JComponent;
import javax.swing.border.TitledBorder;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class TitledSeparator extends JComponent
{

    /**
     * Use the default vertical orientation for the title text.
     */
    static public final int DEFAULT_POSITION = TitledBorder.DEFAULT_POSITION;
    /** Position the title above the border's top line. */
    static public final int ABOVE = TitledBorder.ABOVE_TOP;
    /** Position the title in the middle of the border's top line. */
    static public final int MIDDLE = TitledBorder.TOP;
    /** Position the title below the border's top line. */
    static public final int BELOW = TitledBorder.BELOW_TOP;
    private TitledBorder titledBorder;

    public TitledSeparator()
    {
        this(null, TitledBorder.DEFAULT_JUSTIFICATION, MIDDLE, null, null);
    }

    public TitledSeparator(String title)
    {
        this(title, TitledBorder.DEFAULT_JUSTIFICATION, MIDDLE, null, null);
    }

    public TitledSeparator(String title, int titleJustification,
                            int titlePosition)
    {
        this(title, titleJustification, titlePosition, null, null);
    }

    public TitledSeparator(String title, int titleJustification,
                            int titlePosition, Font titleFont)
    {
        this(title, titleJustification, titlePosition, titleFont, null);
    }

    public TitledSeparator(String title, int titleJustification,
                            int titlePosition, Font titleFont, Color titleColor)
    {
        this.titledBorder = new TitledBorder(null, title);
        setTitleJustification(titleJustification);
        setTitlePosition(titlePosition);
        setTitleFont(titleFont);
        setTitleColor(titleColor);
    }

    public int getTitlePosition()
    {
        return titledBorder.getTitlePosition();
    }

    public int getTitleJustification()
    {
        return titledBorder.getTitleJustification();
    }

    public Color getTitleColor()
    {
        return titledBorder.getTitleColor();
    }

    public String getTitle()
    {
        return titledBorder.getTitle();
    }

    public Font getTitleFont()
    {
        return titledBorder.getTitleFont();
    }

    public void setTitle(String title)
    {
        titledBorder.setTitle(title);
    }

    public void setTitleJustification(int titleJustification)
    {
        titledBorder.setTitleJustification(titleJustification);
    }

    public void setTitleColor(Color titleColor)
    {
        titledBorder.setTitleColor(titleColor);
    }

    public void setTitleFont(Font font)
    {
        titledBorder.setTitleFont(font);
    }

    public void setTitlePosition(int titlePosition)
    {
        switch (titlePosition)
        {
            default:
                throw new IllegalArgumentException();
            case DEFAULT_POSITION:
            case ABOVE:
            case MIDDLE:
            case BELOW:
                titledBorder.setTitlePosition(titlePosition);
        }

    }

    @Override
    public void paint(Graphics g)
    {
        Dimension size = getSize();
        titledBorder.paintBorder(this, g, -4, 0, size.width + 8, size.height + 4);
    }

    @Override
    public Dimension getPreferredSize()
    {
        Insets insets = titledBorder.getBorderInsets(this);

        return new Dimension(0, insets.top);
    }

}
