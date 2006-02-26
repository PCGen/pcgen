/*
 * WeaponProfsToken.java
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
 * Current Ver: $Revision: 1.4 $
 * Last Editor: $Author: darkenedroom $
 * Last Edited: $Date: 2006/01/17 19:29:19 $
 *
 */
package plugin.exporttokens;

import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.Token;

import java.util.SortedSet;

/**
 * <code>WeaponProfsToken</code>.
 * 
 * @author	binkley
 * @version	$Revision: 1.4 $
 */
public class WeaponProfsToken extends Token
{
	/** WeaponProfsToken */
	public static final String TOKENNAME = "WEAPONPROFS";

	/**
	 * Gets the token name
	 * 
	 * @return The token name.
	 * @see pcgen.io.exporttoken.Token#getTokenName()
	 */
	public String getTokenName()
	{
		return TOKENNAME;
	}

	/**
	 * Get the value of the supplied output token. Simply calls getWeaponProfsToken.
	 *
	 * @param tokenSource The full source of the token
	 * @param pc The character to retrieve the value for.
	 * @param eh The ExportHandler that is managing the export
	 * @return The value of the token.
	 * @see pcgen.io.exporttoken.Token#getToken(java.lang.String, pcgen.core.PlayerCharacter, pcgen.io.ExportHandler)
	 */
	public String getToken(String tokenSource, PlayerCharacter pc, ExportHandler eh)
	{
		return getWeaponProfsToken(pc);
	}

	/**
	 * Returns the weapons profs for a player character as a comma delimited string, may be empty.
	 * 
	 * @param pc The character to retrieve the weapons profs for.
	 * @return The weapon profs string, may be empty.
	 */
	public static String getWeaponProfsToken(PlayerCharacter pc)
	{
		StringBuffer sb = new StringBuffer();

		if (SettingsHandler.getWeaponProfPrintout())
		{
			SortedSet stringList = pc.getWeaponProfList();

			for (int i = 0; i < stringList.size(); i++)
			{
				if(i > 0) {
					sb.append(", ");
				}
				sb.append((String) stringList.toArray()[i]);
			}
		}
		return sb.toString();
	}
}


