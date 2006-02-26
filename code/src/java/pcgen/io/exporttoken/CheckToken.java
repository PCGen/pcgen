/*
 * CheckToken.java
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
 * Current Ver: $Revision: 1.7 $
 * Last Editor: $Author: soulcatcher $
 * Last Edited: $Date: 2006/02/14 02:46:17 $
 *
 */
package pcgen.io.exporttoken;

import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.io.ExportHandler;
import pcgen.util.Delta;

import java.util.StringTokenizer;

/**
 * Deal with token:
 *
 * CHECK.x.y.y..
 * x = FORTITUDE|WILL|REFLEX|0|1|2
 * y = TOTAL|BASE|MISC|EPIC|MAGIC|RACE|FEATS|STATMOD|NOEPIC|NOMAGIC|NORACE|NOFEATS|NOSTAT|NOSTATMOD
 */
public class CheckToken extends Token
{
	/** Token name */
	public static final String TOKENNAME = "CHECK";

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
		StringTokenizer aTok = new StringTokenizer(tokenSource, ".", false);
		aTok.nextToken();

		String saveType = aTok.nextToken();

		String saveMods = "";
		while (aTok.hasMoreTokens())
		{
			if (saveMods.length() > 0)
			{
				saveMods += ".";
			}
			saveMods += aTok.nextToken();
		}

		if ("NAME".equals(saveMods))
		{
			return getNameToken(saveType);
		}
		return Delta.toString(getCheckToken(pc, saveType, saveMods));
	}

	/**
	 * Get the token
	 * @param pc
	 * @param saveType
	 * @param saveMods
	 * @return int
	 */
	public static int getCheckToken(PlayerCharacter pc, String saveType, String saveMods)
	{
		saveType = getNameToken(saveType);

		if ("".equals(saveMods))
		{
			saveMods = "TOTAL";
		}
		return pc.calculateSaveBonus(1, saveType, saveMods);
	}

	/**
	 * Get the token name
	 * @param saveType
	 * @return token name
	 */
	public static String getNameToken(String saveType)
	{
		try
		{
			int i = Integer.parseInt(saveType);

			if ((i >= 0) && (i < SettingsHandler.getGame().getUnmodifiableCheckList().size()))
			{
				saveType = SettingsHandler.getGame().getUnmodifiableCheckList().get(i).toString();
			}
		}
		catch (NumberFormatException e)
		{
			// just means it's a name, not a number
		}
		return saveType;
	}
}

