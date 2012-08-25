/*
 * PortraitPane.java
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
 * Created on Jul 8, 2011, 5:17:22 PM
 */
package pcgen.gui2.tabs.bio;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import pcgen.core.facade.CharacterFacade;
import pcgen.gui2.tools.Utility;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
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

	public MouseHandler createMouseHandler(CharacterFacade character)
	{
		return new MouseHandler(character);
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
			int width, height;
			width = (int) (scale * portrait.getWidth());
			height = (int) (scale * portrait.getHeight());
			g.drawImage(portrait, insets.left, insets.top, width, height, this);
		}
		else
		{
			g.drawImage(portrait, insets.left, insets.top, this);
		}
		g.setColor(Color.BLACK);
		g.setXORMode(Color.WHITE);
		if (cropRect != null)
		{
			if (scale < 1)
			{
				g.drawRect(((int) (cropRect.x * scale)) + insets.left,
						   ((int) (cropRect.y * scale)) + insets.top,
						   (int) (cropRect.width * scale), (int) (cropRect.height * scale));
			}
			else
			{
				g.drawRect(cropRect.x + insets.left, cropRect.y + insets.top,
						   cropRect.width, cropRect.height);
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
			}
			else
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

	public class MouseHandler extends MouseAdapter implements MouseMotionListener
	{

		private CharacterFacade character;

		public MouseHandler(CharacterFacade character)
		{
			this.character = character;
		}

		private boolean movingRect = false;
		private Point cropOffset = null;

		@Override
		public void mouseDragged(MouseEvent e)
		{
			if (movingRect)
			{
				int x, y;
				if (scale < 1)
				{
					x = (int) (e.getX() / scale - cropOffset.x);
					y = (int) (e.getY() / scale - cropOffset.y);
				}
				else
				{
					x = e.getX() - cropOffset.x;
					y = e.getY() - cropOffset.y;
				}
				x = Math.max(x, 0);
				y = Math.max(y, 0);
				cropRect.setLocation(x, y);
				Utility.adjustRectToFitImage(portrait, cropRect);
				character.setThumbnailCrop(cropRect);
			}
		}

		@Override
		public void mousePressed(MouseEvent e)
		{
			if (e.getButton() != MouseEvent.BUTTON1)
			{
				return;
			}
			Point mousepoint = e.getPoint();
			if (scale < 1)
			{
				mousepoint.x = (int) (mousepoint.x / scale);
				mousepoint.y = (int) (mousepoint.y / scale);
			}
			movingRect = cropRect.contains(mousepoint);
			cropOffset = new Point(mousepoint.x - cropRect.x, mousepoint.y - cropRect.y);
		}

		@Override
		public void mouseReleased(MouseEvent e)
		{
			movingRect = false;
		}

		@Override
		public void mouseMoved(MouseEvent e)
		{
		}

		@Override
		public void mouseWheelMoved(MouseWheelEvent e)
		{
			if (e.getScrollType() != MouseWheelEvent.WHEEL_UNIT_SCROLL)
			{
				return;
			}
			Point mousepoint = e.getPoint();
			if (scale < 1)
			{
				mousepoint.x = (int) (mousepoint.x / scale);
				mousepoint.y = (int) (mousepoint.y / scale);
			}
			if (!cropRect.contains(mousepoint))
			{
				return;
			}
			int units = e.getUnitsToScroll();
			int size = cropRect.width + units;
			size = Math.max(size, 100);
			size = Math.min(size, portrait.getWidth() - 1);
			size = Math.min(size, portrait.getHeight() - 1);

			cropRect.width = size;
			cropRect.height = size;

			int x = cropRect.x;
			int y = cropRect.y;
			x = Math.max(x, 0);
			y = Math.max(y, 0);
			cropRect.setLocation(x, y);
			Utility.adjustRectToFitImage(portrait, cropRect);
			character.setThumbnailCrop(cropRect);
		}

	}

}
