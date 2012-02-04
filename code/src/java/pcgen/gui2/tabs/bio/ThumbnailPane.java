/*
 * ThumbnailPane.java
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
 * Created on Jul 9, 2011, 3:11:44 PM
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

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
class ThumbnailPane extends JComponent
{

	private static final int THUMBNAIL_SIZE = 100;
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
		this.cropRect = cropRect;
		refreshImage();
	}

	private void refreshImage()
	{
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
		width += THUMBNAIL_SIZE;
		height += THUMBNAIL_SIZE;
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
		g.drawImage(portrait.getSubimage(cropRect.x, cropRect.y, cropRect.width, cropRect.height),
					insets.left, insets.top, THUMBNAIL_SIZE, THUMBNAIL_SIZE, this);
	}

}
