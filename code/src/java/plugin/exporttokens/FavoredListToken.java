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
 *
 * Created on December 15, 2003, 12:21 PM
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package plugin.exporttokens;

import pcgen.core.PlayerCharacter;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.Token;

import java.util.Iterator;

/**
 * Handle the FAVOREDLIST token which produces a list of a character's
 * favored classes.
 *
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 * @version $Revision$
 */
public class FavoredListToken extends Token
{
	/** The token processed by this class. */
	public static final String TOKENNAME = "FAVOREDLIST";

	/**
	 * @see pcgen.io.exporttoken.Token#getTokenName()
	 */
	public String getTokenName()
	{
		return TOKENNAME;
	}

	/**
	 * @see pcgen.io.exporttoken.Token#getToken(java.lang.String, pcgen.core.PlayerCharacter, pcgen.io.ExportHandler)
	 */
	public String getToken(String tokenSource, PlayerCharacter pc, ExportHandler eh)
	{
		return getFavoredListToken(pc);
	}

	/**
	 * Retrieve the list of favored classes for the PC.
	 * @param pc The character to be queried.
	 * @return The text comma seperated list of favored classes.
	 */
	public static String getFavoredListToken(PlayerCharacter pc)
	{
		String retString = "";
		boolean firstLine = true;

		for (Iterator e = pc.getFavoredClasses().iterator(); e.hasNext();)
		{
			// separator only on second and beyond iterations
			if (!firstLine)
			{
				retString += ", ";
			}

			firstLine = false;

			retString += (String) e.next();
		}

		return retString;
	}
}
