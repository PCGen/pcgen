/*
 * RaceSubTypeToken.java
 * Copyright 2005 (C) Aaron Divinsky <boomer70@yahoo.com>
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
 * Created on November 2, 2005
 *
 * Current Ver: $Revision: 1.1 $
 * Last Editor: $Author: boomer70 $
 * Last Edited: $Date: 2005/11/04 05:56:16 $
 *
 */
package plugin.exporttokens;

import java.util.List;
import java.util.StringTokenizer;

import pcgen.core.PlayerCharacter;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.Token;

//RACESUBTYPE.x
public class RaceSubTypeToken extends Token
{
	public static final String TOKENNAME = "RACESUBTYPE";

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
		aTok.nextToken();

		int i = 0;
		if (aTok.hasMoreTokens())
		{
			try
			{
				i = Integer.parseInt(aTok.nextToken());
			}
			catch (NumberFormatException notUsed)
			{
				// This is an error. We will return the first item
			}
		}

		return getRaceSubTypeToken(pc, i);
	}

	private static String getRaceSubTypeToken(PlayerCharacter pc, int index)
	{
		List subTypes = pc.getRacialSubTypes();
		if (index >= 0 && index < subTypes.size())
		{
			return (String)subTypes.get(index);
		}
		return "";
	}
}
