/*
 * MaxDexToken.java
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
 * Current Ver: $Revision: 1.3 $
 * Last Editor: $Author: karianna $
 * Last Edited: $Date: 2006/02/14 16:39:18 $
 *
 */
package plugin.exporttokens;

import pcgen.core.Constants;
import pcgen.core.PlayerCharacter;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.Token;
import pcgen.util.Delta;

/**
 * Deals with retunring value from MAXDEX token
 */
public class MaxDexToken extends Token
{
	/** Token name */
	public static final String TOKENNAME = "MAXDEX";

	/**
	 * @see pcgen.io.exporttoken.Token#getTokenName()
	 */
	public String getTokenName()
	{
		return TOKENNAME;
	}

	//TODO: Rip the processing of this token out of PlayerCharacter
	/**
	 * @see pcgen.io.exporttoken.Token#getToken(java.lang.String, pcgen.core.PlayerCharacter, pcgen.io.ExportHandler)
	 */
	public String getToken(String tokenSource, PlayerCharacter pc, ExportHandler eh)
	{
		return getMaxDexToken(tokenSource, pc);
	}

	/**
	 * Get the value for the MAXDEX token
	 * @param tokenSource
	 * @param pc
	 * @return the value for the MAXDEX token
	 */
	public static String getMaxDexToken(String tokenSource, PlayerCharacter pc)
	{
		String retString = "";
		int mod = pc.modToFromEquipment(tokenSource);

		if (mod != Constants.MAX_MAXDEX)
		{
			retString = Delta.toString(mod);
		}

		return retString;
	}
}
