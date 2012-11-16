/*
 * Copyright 2008 (C) Tom Parker <thpr@users.sourceforge.net>
 * Derived from PObject.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.core.analysis;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;

public class DescriptionFormatting
{

	private static String piDescString(PlayerCharacter aPC, PObject cdo,
		boolean useHeader)
	{
		final String desc = aPC.getDescription(cdo);

		if (cdo.getSafe(ObjectKey.DESC_PI))
		{
			final StringBuilder sb = new StringBuilder(desc.length() + 30);

			if (useHeader)
			{
				sb.append("<html>");
			}

			sb.append("<b><i>").append(desc).append("</i></b>");

			if (useHeader)
			{
				sb.append("</html>");
			}

			return sb.toString();
		}

		return desc;
	}

	/**
	 * Get the Product Identity description String
	 * @return the Product Identity description String
	 */
	public static String piDescString(PlayerCharacter aPC, PObject po)
	{
		return piDescString(aPC, po, true);
	}

	/**
	 * In some cases, we need a PI-formatted string to place within a
	 * pre-existing <html> tag
	 * @return PI description
	 */
	public static String piDescSubString(PlayerCharacter aPC, PObject po)
	{
		return piDescString(aPC, po, false);
	}

}
