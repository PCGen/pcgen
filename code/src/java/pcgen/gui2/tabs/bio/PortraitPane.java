/*
 * Copyright 2011 Connor Petty <cpmeister@users.sourceforge.net>
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
package pcgen.gui2.tabs.bio;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.JComponent;

class PortraitPane extends JComponent
{

    private static final int MAX_PORTRAIT_HEIGHT = 400;
    private float scale = 1;
    private BufferedImage portrait;
    private Rectangle cropRect;

    public PortraitPane()
    {
        this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
    }

    public void setPortraitImage(BufferedImage portrait)
    {
        this.portrait = portrait;
        scale = MAX_PORTRAIT_HEIGHT / (float) portrait.getHeight();
        repaint();
    }

    public void setCropRectangle(Rectangle newCropRect)
    {
        this.cropRect = new Rectangle(newCropRect);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        Insets insets = getInsets();
        if (scale < 1)
        {
            int width = (int) (scale * portrait.getWidth());
            int height = (int) (scale * portrait.getHeight());
            g.drawImage(portrait, insets.left, insets.top, width, height, this);
        } else
        {
            g.drawImage(portrait, insets.left, insets.top, this);
        }
        g.setColor(Color.BLACK);
        g.setXORMode(Color.WHITE);
        if (cropRect != null)
        {
            if (scale < 1)
            {
                g.drawRect(((int) (cropRect.x * scale)) + insets.left, ((int) (cropRect.y * scale)) + insets.top,
                        (int) (cropRect.width * scale), (int) (cropRect.height * scale));
            } else
            {
                g.drawRect(cropRect.x + insets.left, cropRect.y + insets.top, cropRect.width, cropRect.height);
            }
        }
    }

    @Override
    public Dimension getPreferredSize()
    {
        Insets insets = getInsets();
        int width = 0;
        int height = 0;

        if (insets != null)
        {
            width += insets.left + insets.right;
            height += insets.top + insets.bottom;
        }
        if (portrait != null)
        {
            if (scale < 1)
            {
                width += (int) (scale * portrait.getWidth());
                height += (int) (scale * portrait.getHeight());
            } else
            {
                width += portrait.getWidth();
                height += portrait.getHeight();
            }
        }
        return new Dimension(width, height);
    }

    @Override
    public Dimension getMaximumSize()
    {
        return getPreferredSize();
    }

}
