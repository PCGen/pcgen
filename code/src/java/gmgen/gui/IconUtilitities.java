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

import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import java.awt.Window;
import java.net.URL;

/**
 * A Utility class to assist with the loading etc of Icons
 */
public class IconUtilitities
{
	/** The URL to the resource folder of pcgen */
	public static final String RESOURCE_URL = "/pcgen/gui/resource/";
	public static final String RESOURCE_APP_ICON = "PCGenApp.png";

	/**
	 * Fetch an <code>ImageIcon</code> relative to the calling
	 * location.
	 *
	 * @param location <code>String</code>, the path to the
	 * <code>IconImage</code> source
	 *
	 * @return <code>ImageIcon</code>, the icon or
	 * <code>null</code> on failure
	 */
	public static ImageIcon getImageIcon(String location)
	{
		if (location.startsWith(RESOURCE_URL) == false) {
			location = RESOURCE_URL + location;
		}
		final URL iconURL =
				IconUtilitities.class.getResource(location);

		if (iconURL == null)
		{
			return null;
		}

		return new ImageIcon(iconURL);
	}

	/**
	 * Fetch an <code>ImageIcon</code> relative to the calling
	 * location.
	 *
	 * @param location <code>String</code>, the path to the
	 * <code>IconImage</code> source
	 * @param description <code>String</code>, the description
	 *
	 * @return <code>ImageIcon</code>, the icon or
	 * <code>null</code> on failure
	 */
	public static ImageIcon getImageIcon(String location, String description)
	{
		if (location.startsWith(RESOURCE_URL) == false) {
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

	/**
	 * Add an icon to a menu item if the image can be loaded,
	 * otherwise return <code>false</code>.
	 *
	 * @param button AbstractButton the item
	 * @param iconName String the name of the image file (not the path)
	 *
	 * @return boolean was icon set?
	 */
	public static boolean maybeSetIcon(AbstractButton button, String iconName)
	{
		if (iconName == null)
		{
			return false;
		}

		final ImageIcon iconImage = getImageIcon(iconName);

		if (iconImage == null)
		{
			return false;
		}

		button.setIcon(iconImage);

		return true;
	}

	/**
	 * Add an icon and description to a menu item if the image can
	 * be loaded, otherwise return <code>false</code>.
	 *
	 * @param button AbstractButton the item
	 * @param iconName String the name of the image file (not the path)
	 * @param description String the description of the icon
	 *
	 * @return boolean was icon set?
	 */
	public static boolean maybeSetIcon(AbstractButton button, String iconName,
		String description)
	{
		if (iconName == null)
		{
			return false;
		}

		final ImageIcon iconImage = getImageIcon(iconName, description);

		if (iconImage == null)
		{
			return false;
		}

		button.setIcon(iconImage);

		return true;
	}

	/**
	 * Add an icon to a frame if the image can be loaded,
	 * otherwise return <code>false</code>.
	 *
	 * @param frame Frame the frame
	 * @param iconName String the name of the image file (not the path)
	 *
	 * @return boolean was icon set?
	 */
	public static boolean maybeSetIcon(Window frame, String iconName)
	{
		if (iconName == null)
		{
			return false;
		}

		final ImageIcon iconImage = getImageIcon(iconName);

		if (iconImage == null)
		{
			return false;
		}

		frame.setIconImage(iconImage.getImage());

		return true;
	}
}
