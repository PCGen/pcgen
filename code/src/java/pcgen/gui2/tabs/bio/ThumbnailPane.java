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

import pcgen.cdom.base.Constants;

class ThumbnailPane extends JComponent
{

    private BufferedImage portrait;
    private Rectangle cropRect;

    public ThumbnailPane()
    {
        this.setDoubleBuffered(true);
        this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
    }

    public void setPortraitImage(BufferedImage portrait)
    {
        this.portrait = portrait;
        refreshImage();
    }

    public void setCropRectangle(Rectangle cropRect)
    {
        this.cropRect = new Rectangle(cropRect);
        refreshImage();
    }

    private void refreshImage()
    {
        if (cropRect != null)
        {
            if ((this.cropRect.x + this.cropRect.width) > portrait.getWidth())
            {
                this.cropRect.x = portrait.getWidth() - cropRect.width;
            }
            if ((this.cropRect.y + this.cropRect.height) > portrait.getHeight())
            {
                this.cropRect.y = portrait.getHeight() - cropRect.height;
            }
            if (this.cropRect.x < 0 || this.cropRect.y < 0)
            {
                this.cropRect = null;
            }
        }
        if (cropRect != null)
        {
            repaint();
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
        width += Constants.THUMBNAIL_SIZE;
        height += Constants.THUMBNAIL_SIZE;
        return new Dimension(width, height);
    }

    @Override
    public Dimension getMaximumSize()
    {
        return getPreferredSize();
    }

    @Override
    public Dimension getMinimumSize()
    {
        return getPreferredSize();
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        if (portrait == null || cropRect == null)
        {
            return;
        }
        Insets insets = getInsets();
        g.drawImage(portrait.getSubimage(cropRect.x, cropRect.y, cropRect.width, cropRect.height), insets.left,
                insets.top, Constants.THUMBNAIL_SIZE, Constants.THUMBNAIL_SIZE, this);
    }

}
