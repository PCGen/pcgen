/*
 * HPToken.java
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
 * Current Ver: $Revision: 1.5 $
 * Last Editor: $Author: karianna $
 * Last Edited: $Date: 2006/02/02 13:06:00 $
 *
 */
package pcgen.io.exporttoken;

import pcgen.core.PlayerCharacter;
import pcgen.io.ExportHandler;

/**
 * Deals with HP token
 */
public class HPToken extends Token
{
	/** Token name */
	public static final String TOKENNAME = "HP";

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
		return getHPToken(pc) + "";
	}

	/**
	 * Get the HP Token
	 * @param pc
	 * @return the HP Token
	 */
	public static int getHPToken(PlayerCharacter pc)
	{
		return pc.hitPoints();
	}
}
