/*
 * SkinLFResourceChecker.java
 * Copyright 2001 (C) Jason Buchanan
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
package pcgen.util;

/**
 * Title:        SkinLFResourceChecker.java
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 */
public final class SkinLFResourceChecker
{
	private static int missingResourceCount;
	private static StringBuilder resourceBuffer;
	private static final String whereToGetIt =
			"<a href=\"http://prdownloads.sourceforge.net/pcgen/skin.zip\">skin.zip</a>";

	static
	{
		missingResourceCount = 0;

		//optimize StringBuilder initial size (0 should be right length. Hopefully we don't get an error. :)
		resourceBuffer = new StringBuilder(0);
		checkResource();
	}

	/**
	 * Get the missing resource count
	 * @return the missing resource count
	 */
	public static int getMissingResourceCount()
	{
		return missingResourceCount;
	}

	/**
	 * Get the missing resource message
	 * @return the missing resource message
	 */
	public static String getMissingResourceMessage()
	{
		if (missingResourceCount != 0)
		{
			return resourceBuffer + "\n"
				+ ResourceChecker.getItHereMsg + whereToGetIt + "\n"
				+ ResourceChecker.missingLibMsg;//TODO Why does this have hardcoded file separators? JK070115
		}

		return "";
	}

	private static void checkResource()
	{
		if (!ResourceChecker.hasResource(
				"com.l2fprod.gui.plaf.skin.SkinLookAndFeel", "skinlf.jar",
				resourceBuffer))
		{
			++missingResourceCount;
		}
	}
}
