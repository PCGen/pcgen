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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.     See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package pcgen.gui3.preloader;

import pcgen.gui3.component.PCGenStatusBar;

import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;

/**
 * Displays HTML content as a "panel".
 */
public final class PCGenPreloaderController
{
	/** Corner radius, in pixels, matching {@code .splash-card} in the CSS. */
	private static final double CORNER_RADIUS = 12.0;

	@FXML
	private ImageView splashImage;

	@FXML
	private PCGenStatusBar pcGenStatusBar;

	@FXML
	void initialize()
	{
		// Clip the image to rounded top corners so it doesn't overhang the card frame.
		Rectangle clip = new Rectangle();
		clip.setArcWidth(CORNER_RADIUS * 2);
		clip.setArcHeight(CORNER_RADIUS * 2);
		splashImage.layoutBoundsProperty().addListener((obs, oldBounds, bounds) -> resizeClip(clip, bounds));
		resizeClip(clip, splashImage.getLayoutBounds());
		splashImage.setClip(clip);
	}

	private static void resizeClip(Rectangle clip, Bounds bounds)
	{
		clip.setWidth(bounds.getWidth());
		// Overshoot the height so only the top corners round; the bottom pair falls out of view.
		clip.setHeight(bounds.getHeight() + CORNER_RADIUS);
	}

	public void setProgress(String message, double progress)
	{
		pcGenStatusBar.setProgress(message, progress);
	}
}
