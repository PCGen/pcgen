/*
 * PortraitToken.java
 * Copyright 2003 (C) Devon Jones <soulcatcher@evilsoft.org>
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
package plugin.exporttokens;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import pcgen.cdom.base.Constants;
import pcgen.core.display.CharacterDisplay;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.AbstractExportToken;
import pcgen.util.Logging;

import org.apache.commons.lang3.StringUtils;

/**
 * The Class {@code PortraitToken} supports the PORTRAIT
 * token and its and PORTRAIT.THUMB variant.
 *
 *
 */
public class PortraitToken extends AbstractExportToken
{
	@Override
	public String getTokenName()
	{
		return "PORTRAIT";
	}

	/**
	 * True if the token should be encoded during the export
	 * @return False because the Portrait path must be unchanged
	 */
	@Override
	public boolean isEncoded()
	{
		return false;
	}

	//TODO: Move this to a token that has all of the descriptive stuff about a character
	@Override
	public String getToken(String tokenSource, CharacterDisplay display, ExportHandler eh)
	{
		if ("PORTRAIT.THUMB".equals(tokenSource))
		{
			return getThumbnailToken(display);
		}

		return display.getPortraitPath();
	}

	private String getThumbnailToken(CharacterDisplay display)
	{
		// Generate thumbnail
		BufferedImage thumb = generateThumb(display);
		if (thumb == null)
		{
			return null;
		}

		// Save to a temporary file
		String pcgFilename = display.getFileName();
		String baseName;
		if (StringUtils.isNotBlank(pcgFilename))
		{
			baseName = new File(pcgFilename).getName();
			if (baseName.indexOf(Constants.EXTENSION_CHARACTER_FILE) > 0)
			{
				baseName = baseName.substring(0, baseName.indexOf(Constants.EXTENSION_CHARACTER_FILE));
			}
		}
		else
		{
			baseName = display.getName();
		}

		File thumbFile;
		try
		{
			thumbFile = File.createTempFile("pcgentmb_", ".png");
		}
		catch (IOException e1)
		{
			Logging.errorPrint("PortraitToken.getThumbnailToken failed", e1);
			return null;

		}
		try
		{
			ImageIO.write(thumb, "PNG", thumbFile);
		}
		catch (IOException e)
		{
			Logging.errorPrint("PortraitToken.getThumbnailToken failed", e);
			return null;
		}

		// Return the path
		return thumbFile.getAbsolutePath();
	}

	/**
	 * Generate a thumbnail image based on the character's portrait and
	 * the thumnbnail rectangle.
	 *
	 * @param display The character being output.
	 * @return The thumbnail image, or null if not defined.
	 */
	private BufferedImage generateThumb(CharacterDisplay display)
	{
		Rectangle cropRect = display.getPortraitThumbnailRect();
		BufferedImage portrait = null;
		try
		{
			File file = new File(display.getPortraitPath());
			if (file.isFile())
			{
				portrait = ImageIO.read(file);
			}
		}
		catch (IOException ex)
		{
			Logging.errorPrint("Could not load image", ex);
		}
		if (portrait == null || cropRect == null)
		{
			return null;
		}

		BufferedImage thumb = portrait.getSubimage(cropRect.x, cropRect.y, cropRect.width, cropRect.height);
		thumb = getScaledInstance(thumb, Constants.THUMBNAIL_SIZE, Constants.THUMBNAIL_SIZE,
			RenderingHints.VALUE_INTERPOLATION_BILINEAR, true);
		return thumb;
	}

	/**
	 * Convenience method that returns a scaled instance of the
	 * provided {@code BufferedImage}.
	 *
	 * @param img the original image to be scaled
	 * @param targetWidth the desired width of the scaled instance,
	 *    in pixels
	 * @param targetHeight the desired height of the scaled instance,
	 *    in pixels
	 * @param hint one of the rendering hints that corresponds to
	 *    {@code RenderingHints.KEY_INTERPOLATION} (e.g.
	 *    {@code RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR},
	 *    {@code RenderingHints.VALUE_INTERPOLATION_BILINEAR},
	 *    {@code RenderingHints.VALUE_INTERPOLATION_BICUBIC})
	 * @param higherQuality if true, this method will use a multi-step
	 *    scaling technique that provides higher quality than the usual
	 *    one-step technique (only useful in down scaling cases, where
	 *    {@code targetWidth} or {@code targetHeight} is
	 *    smaller than the original dimensions, and generally only when
	 *    the {@code BILINEAR} hint is specified)
	 * @return a scaled version of the original {@code BufferedImage}
	 */
	public BufferedImage getScaledInstance(BufferedImage img, int targetWidth, int targetHeight, Object hint,
		boolean higherQuality)
	{
		int type = (img.getTransparency() == Transparency.OPAQUE) ? BufferedImage.TYPE_INT_RGB
			: BufferedImage.TYPE_INT_ARGB;
		BufferedImage ret = img;
		int w;
		int h;
		if (higherQuality)
		{
			// Use multi-step technique: start with original size, then
			// scale down in multiple passes with drawImage()
			// until the target size is reached
			w = img.getWidth();
			h = img.getHeight();
		}
		else
		{
			// Use one-step technique: scale directly from original
			// size to target size with a single drawImage() call
			w = targetWidth;
			h = targetHeight;
		}

		// If we are scaling up, just do the one pass.
		if (w < targetWidth || h < targetWidth)
		{
			// Use one-step technique: scale directly from original
			// size to target size with a single drawImage() call
			w = targetWidth;
			h = targetHeight;
		}

		do
		{
			if (higherQuality && w > targetWidth)
			{
				w /= 2;
				if (w < targetWidth)
				{
					w = targetWidth;
				}
			}

			if (higherQuality && h > targetHeight)
			{
				h /= 2;
				if (h < targetHeight)
				{
					h = targetHeight;
				}
			}

			BufferedImage tmp = new BufferedImage(w, h, type);
			Graphics2D g2 = tmp.createGraphics();
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint);
			g2.drawImage(ret, 0, 0, w, h, null);
			g2.dispose();

			ret = tmp;
		}
		while (w != targetWidth || h != targetHeight);

		return ret;
	}
}
