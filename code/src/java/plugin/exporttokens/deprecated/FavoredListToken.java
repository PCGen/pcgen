/*
 * FavoredListToken.java
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
package plugin.exporttokens.deprecated;

import pcgen.core.PCClass;
import pcgen.core.display.CharacterDisplay;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.AbstractExportToken;

/**
 * Handle the FAVOREDLIST token which produces a list of a character's
 * favored classes.
 */
public class FavoredListToken extends AbstractExportToken
{
	@Override
	public String getTokenName()
	{
		return "FAVOREDLIST";
	}

	@Override
	public String getToken(String tokenSource, CharacterDisplay display, ExportHandler eh)
	{
		return getFavoredListToken(display);
	}

	/**
	 * Retrieve the list of favored classes for the PC.
	 * @param display The character to be queried.
	 * @return The text comma seperated list of favored classes.
	 */
	public static String getFavoredListToken(CharacterDisplay display)
	{
		if (display.hasAnyFavoredClass())
		{
			return "Any";
		}
		StringBuilder sb = new StringBuilder();
		for (PCClass pcc : display.getFavoredClasses())
		{
            sb.append(pcc.getFullKey());
		}
		return sb.toString();
	}
}
