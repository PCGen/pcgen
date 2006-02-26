/*
 * GoldToken.java
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
 * Last Editor: $Author: binkley $
 * Last Edited: $Date: 2005/10/18 20:23:58 $
 *
 */
package plugin.exporttokens;

import pcgen.core.PlayerCharacter;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.Token;

import java.math.BigDecimal;

/**
 * Handle the GOLD token which outputs the amount of unallocated wealth
 * that the character has.
 *
 * Last Editor: $Author: binkley $
 * Last Edited: $Date: 2005/10/18 20:23:58 $
 *
 * @version $Revision: 1.3 $
 */
public class GoldToken extends Token
{
	/** The token handled by this class. */
	public static final String TOKENNAME = "GOLD";

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
		return getGoldToken(pc) + "";
	}

	/**
	 * Retrieve the amount of money that the character has.
	 * @param pc The character to be queried.
	 * @return The amount of gold
	 */
	public static BigDecimal getGoldToken(PlayerCharacter pc)
	{
		return pc.getGold();
	}
}
