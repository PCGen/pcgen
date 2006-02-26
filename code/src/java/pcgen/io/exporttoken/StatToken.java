/*
 * StatToken.java
 * Copyright 2003 (C) Devon Jones <soulcatcher@evilsoft.org>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	 See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on December 15, 2003, 12:21 PM
 *
 * Current Ver: $Revision: 1.10 $
 * Last Editor: $Author: binkley $
 * Last Edited: $Date: 2005/10/22 11:38:45 $
 *
 */

package pcgen.io.exporttoken;

import pcgen.core.PCStat;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.io.ExportHandler;
import pcgen.util.Delta;
import pcgen.util.Logging;

import java.util.StringTokenizer;

//STAT.x
//STAT.x.STAT
//STAT.x.BASE
//STAT.x.MOD
//STAT.x.BASEMOD
//STAT.x.NOTEMP
//STAT.x.NOTEMPMOD
//STAT.x.NAME
//STAT.x.LONGNAME
//STAT.x.LEVEL.x[.NOEQUIP][.NOPOST]

// Changed so that you can use any of the following arguments to
// get what you want:
// NOTEMP
// NOEQUIP
// NOPOST
// LEVEL.x
//
// You must supply either STAT, BASE or MOD somewhere in the line
// If neither is found, it will assume you want the STAT

public class StatToken extends Token
{
	public static final String TOKENNAME = "STAT";

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
		String retString = "";
		StringTokenizer aTok = new StringTokenizer(tokenSource, ".");
		aTok.nextToken();
		int indexOfStat;
		indexOfStat = Integer.parseInt(aTok.nextToken());
		if ((indexOfStat < 0) || (indexOfStat >= pc.getStatList().getStats().size()))
		{
			return "";
		}

		String findType = "STAT";

		boolean useBase = false;
		boolean useLevel = false;
		int aLevel = 0;

		boolean useEquip = true;
		boolean useTemp = true;
		boolean usePost = true;

		while (aTok.hasMoreTokens())
		{
			String token = aTok.nextToken();

			if ("NAME".equals(token))
			{
				return getNameToken(pc, indexOfStat);
			}
			if ("LONGNAME".equals(token))
			{
				return getLongNameToken(pc, indexOfStat);
			}

			if ("STAT".equals(token))
			{
				findType = "STAT";
			}
			else if ("MOD".equals(token))
			{
				findType = "MOD";
			}
			else if ("BASE".equals(token))
			{
				useBase = true;
			}
			else if ("BASEMOD".equals(token))
			{
				findType = "MOD";
				useBase = true;
			}
			else if ("NOTEMP".equals(token))
			{
				useTemp = false;
			}
			else if ("NOTEMPMOD".equals(token))
			{
				findType = "MOD";
				useTemp = false;
			}
			else if ("NOEQUIP".equals(token))
			{
				useEquip = false;
			}
			else if ("NOPOST".equals(token))
			{
				usePost = false;
			}
			else if ("LEVEL".equals(token))
			{
				try
				{
					aLevel = Integer.parseInt(aTok.nextToken());
				}
				catch (NumberFormatException nfe)
				{
					Logging.errorPrint("Malformed LEVEL.x tag");
				}
				useLevel = true;
			}
		}

		if (findType.equals("MOD"))
		{
			if (useBase)
			{
				retString = getBaseModToken(pc, indexOfStat);
			}
			else
			{
				retString = getModToken(pc, indexOfStat, useTemp, useEquip, usePost, useLevel, aLevel);
			}
		}
		else
		{
			if (useBase)
			{
				retString = getBaseToken(pc, indexOfStat);
			}
			else
			{
				retString = getStatToken(pc, indexOfStat, useTemp, useEquip, usePost, useLevel, aLevel);
			}
		}

		return retString;
	}

	public static String getStatToken(PlayerCharacter pc, int index, boolean useTemp, boolean useEquip, boolean usePost, boolean useLevel, int aLevel)
	{
		return getStatToken(pc, index, useTemp, useEquip, usePost, useLevel, aLevel, true);
	}

	public static String getStatToken(PlayerCharacter pc, int index, boolean useTemp, boolean useEquip, boolean usePost, boolean useLevel, int aLevel, final boolean checkGameMode)
	{
		PCStat stat = (PCStat) pc.getStatList().getStats().get(index);
		if (pc.isNonAbility(index))
		{
			return "*";
		}

		int aTotal = 0;

		if (useLevel)
		{
			if (useEquip && useTemp)
			{
				aTotal = pc.getTotalStatAtLevel(stat.getAbb(), aLevel, usePost);
			}
			else
			{
				aTotal = pc.getPartialStatAtLevel(stat.getAbb(), aLevel, usePost, useTemp, useEquip);
			}
		}
		else if (useEquip && useTemp)
		{
			aTotal = pc.getStatList().getTotalStatFor(stat.getAbb());
		}
		else
		{
			aTotal = pc.getStatList().getPartialStatFor(stat.getAbb(), useTemp, useEquip);
		}

		if (checkGameMode)
		{
			return SettingsHandler.getGame().getStatDisplayText(aTotal);
		}
		return Integer.toString(aTotal);
	}

	public static String getModToken(PlayerCharacter pc, int index, boolean useTemp, boolean useEquip, boolean usePost, boolean useLevel, int aLevel)
	{
		if (pc.isNonAbility(index))
		{
			return "+0";
		}
		int aTotal = Integer.parseInt(getStatToken(pc, index, useTemp, useEquip, usePost, useLevel, aLevel, false));

		int temp = pc.getStatList().getModForNumber(aTotal);
		return Delta.toString(temp);
	}

	public static String getBaseToken(PlayerCharacter pc, int index)
	{
		PCStat stat = (PCStat) pc.getStatList().getStats().get(index);
		if (pc.isNonAbility(index))
		{
			return "*";
		}
		return pc.getStatList().getBaseStatFor(stat.getAbb()) + "";
	}

	public static String getBaseModToken(PlayerCharacter pc, int index)
	{
		pc.getStatList().getStats().get(index);
		if (pc.isNonAbility(index))
		{
			return "+0";
		}
		int aTotal = Integer.parseInt(getBaseToken(pc, index));
		int temp = pc.getStatList().getModForNumber(aTotal);

		return Delta.toString(temp);
	}

	public static String getNameToken(PlayerCharacter pc, int index)
	{
		return SettingsHandler.getGame().s_ATTRIBSHORT[index];
	}

	public static String getLongNameToken(PlayerCharacter pc, int index)
	{
		return SettingsHandler.getGame().s_ATTRIBLONG[index];
	}


	/*
	 * Wrapper functions for calls with old arguments
	 */

	public static String getModToken(PlayerCharacter pc, int index)
	{
		return getModToken(pc, index, true, true, true, false, 0);
	}

}

