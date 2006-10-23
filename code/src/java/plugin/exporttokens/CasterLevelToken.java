/*
 * CasterLevelToken.java
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

import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.Token;

import java.util.StringTokenizer;


/**
 * Deals with following tokens
 * CASTERLEVEL
 * CASTERLEVEL.x
 * CASTERLEVEL.TOTAL
 */
public class CasterLevelToken extends Token
{
	/** The token name */
	public static final String TOKENNAME = "CASTERLEVEL";

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
		StringTokenizer aTok = new StringTokenizer(tokenSource, ".");
		aTok.nextToken(); // burn off CASTERLEVEL

		String varName = "";
		int i = 0;

		if (aTok.hasMoreTokens())
		{
			varName = aTok.nextToken();
			try
			{
				i = Integer.parseInt(varName);
			}
			catch (NumberFormatException nfe)
			{
				// Must be TOTAL
			}
		}

		if (varName.equals("TOTAL") || varName.equals(""))
		{
			return Integer.toString(pc.getVariableValue(tokenSource, "TOTAL").intValue());
		}
		return getClassToken(pc, i);
	}

	/**
	 * Get the class token
	 * @param pc
	 * @param classNumber
	 * @return token
	 */
	public String getClassToken(PlayerCharacter pc, int classNumber)
	{
		String cString = "";

		if (pc.getClassList().size() > classNumber)
		{
			PCClass pcClass = pc.getClassList().get(classNumber);
			cString = "CLASS:" + pcClass.getKeyName();
		}
		return Float.toString(pc.getVariableValue(TOKENNAME, cString).intValue());
	}

}
