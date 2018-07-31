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
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.UIManager;

import pcgen.system.LanguageBundle;
import pcgen.util.Logging;

class ImagePreviewer extends JComponent
{

	private static final int SIZE = 200;
	private static final String IN_NOT_AN_IMAGE = LanguageBundle.getString("in_ImagePreview_notAnImage");
	private BufferedImage image;

	public ImagePreviewer()
	{
		setPreferredSize(new Dimension(SIZE, SIZE));
	}

	public void setImage(File file)
	{
		if (file == null || !file.exists())
		{
			image = null;
			return;
		}
		try
		{
			image = ImageIO.read(file);
		}
		catch (IOException ex)
		{
			Logging.errorPrint("Could not read image", ex);
		}
		repaint();
	}

	public BufferedImage getImage()
	{
		return image;
	}

	@Override
	protected void paintComponent(Graphics g)
	{
		g.setColor(UIManager.getColor("Panel.background"));
		g.fillRect(0, 0, getWidth(), getHeight());

		final int textX = getFontHeightHint(g);
		final int textY = SIZE - getFontHeightHint(g);

		if (image != null)
		{
			final int width = image.getWidth(null);
			final int height = image.getHeight(null);
			final int side = Math.max(width, height);
			final double scale = (double) SIZE / (double) side;

			g.drawImage(image, 0, 0, (int) (scale * width), (int) (scale * height), null);

			// Annotate with original dimensions.  Overlay black on white so
			// the values are visible against most possible image backgrounds.
			final String dim = width + " x " + height;

			g.setColor(Color.black);
			g.drawString(dim, textX, textY);
			g.setColor(Color.white);
			g.drawString(dim, textX - 1, textX - 1);
		}
		else
		{
			g.setColor(UIManager.getColor("Panel.foreground"));
			// TODO: I18N
			g.drawString(IN_NOT_AN_IMAGE, textX, textY);
		}
	}

	private static int getFontHeightHint(final Graphics g)
	{
		return g.getFontMetrics().getHeight();
	}

}
