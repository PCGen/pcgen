/*
 * TabbedPane.java
 *
 * Copyright 2003 (C) Bryan McRoberts <merton_monk@yahoo.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA.
 *
 * Created on April 1st, 2003.
 */
package gmgen.gui;

import java.net.URL;
import javax.swing.ImageIcon;

/**
 * A Utility class to assist with the loading etc of Icons
 */
final class IconUtilitities
{
	/** The URL to the resource folder of pcgen */
	private static final String RESOURCE_URL = "/pcgen/gui/resource/";

	private IconUtilitities() {}

	/**
	 * Fetch an {@code ImageIcon} relative to the calling
	 * location.
	 *
	 * @param location {@code String}, the path to the
	 * {@code IconImage} source
	 * @param description {@code String}, the description
	 *
	 * @return {@code ImageIcon}, the icon or
	 * {@code null} on failure
	 */
	public static ImageIcon getImageIcon(String location, String description)
	{
		if (!location.startsWith(RESOURCE_URL)) {
			location = RESOURCE_URL + location;
		}
		final URL iconURL =
				IconUtilitities.class.getResource(location);

		if (iconURL == null)
		{
			return null;
		}

		return new ImageIcon(iconURL, description);
	}

}
