/*
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
 *
 *
 */

package pcgen.io.exporttoken;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.cdom.base.SortKeyRequired;
import pcgen.core.PCStat;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.core.analysis.StatAnalysis;
import pcgen.io.ExportHandler;
import pcgen.util.Delta;
import pcgen.util.Logging;

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

	@Override
	public String getTokenName()
	{
		return TOKENNAME;
	}

	@Override
	public String getToken(String tokenSource, PlayerCharacter pc, ExportHandler eh)
	{
		String retString;
		StringTokenizer aTok = new StringTokenizer(tokenSource, ".");
		if (aTok.countTokens() < 2)
		{
			Logging.errorPrint("Invalid STAT token:" + tokenSource, new Throwable());
			return "";
		}

		aTok.nextToken();
		int indexOfStat;
		indexOfStat = Integer.parseInt(aTok.nextToken());
		if ((indexOfStat < 0) || (indexOfStat >= pc.getDisplay().getStatCount()))
		{
			return "";
		}
		List<PCStat> statList = new ArrayList<>(pc.getDisplay().getStatSet());
		statList.sort(Comparator.comparing(SortKeyRequired::getSortKey));
		PCStat stat = statList.get(indexOfStat);

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
				return stat.getKeyName();
			}
			if ("LONGNAME".equals(token))
			{
				return stat.getDisplayName();
			}

			if ("ISNONABILITY".equals(token))
			{
				return pc.getDisplay().isNonAbility(stat) ? "Y" : "N";
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
				retString = getBaseModToken(pc, stat);
			}
			else
			{
				retString = getModToken(pc, stat, useTemp, useEquip, usePost, useLevel, aLevel);
			}
		}
		else
		{
			if (useBase)
			{
				retString = getBaseToken(pc, stat);
			}
			else
			{
				retString = getStatToken(pc, stat, useTemp, useEquip, usePost, useLevel, aLevel);
			}
		}

		return retString;
	}

	private static String getStatToken(PlayerCharacter pc, PCStat stat, boolean useTemp, boolean useEquip,
	                                   boolean usePost, boolean useLevel, int aLevel)
	{
		return getStatToken(pc, stat, useTemp, useEquip, usePost, useLevel, aLevel, true);
	}

	private static String getStatToken(PlayerCharacter pc, PCStat stat, boolean useTemp, boolean useEquip,
	                                   boolean usePost, boolean useLevel, int aLevel, final boolean checkGameMode)
	{
		if (pc.getDisplay().isNonAbility(stat))
		{
			return "*";
		}

		int aTotal;

		if (useLevel)
		{
			if (useEquip && useTemp)
			{
				aTotal = pc.getTotalStatAtLevel(stat, aLevel, usePost);
			}
			else
			{
				aTotal = pc.getPartialStatAtLevel(stat, aLevel, usePost, useTemp, useEquip);
			}
		}
		else if (useEquip && useTemp)
		{
			aTotal = pc.getTotalStatFor(stat);
		}
		else
		{
			aTotal = StatAnalysis.getPartialStatFor(pc, stat, useTemp, useEquip);
		}

		if (checkGameMode)
		{
			return SettingsHandler.getGameAsProperty().get().getStatDisplayText(aTotal);
		}
		return Integer.toString(aTotal);
	}

	private static String getModToken(PlayerCharacter pc, PCStat stat, boolean useTemp, boolean useEquip,
	                                  boolean usePost, boolean useLevel, int aLevel)
	{
		if (pc.getDisplay().isNonAbility(stat))
		{
			return "+0";
		}
		int aTotal = Integer.parseInt(getStatToken(pc, stat, useTemp, useEquip, usePost, useLevel, aLevel, false));

		int temp = pc.getModForNumber(aTotal, stat);
		return Delta.toString(temp);
	}

	private static String getBaseToken(PlayerCharacter pc, PCStat stat)
	{
		if (pc.getDisplay().isNonAbility(stat))
		{
			return "*";
		}
		return Integer.toString(pc.getBaseStatFor(stat));
	}

	private static String getBaseModToken(PlayerCharacter pc, PCStat stat)
	{
		if (pc.getDisplay().isNonAbility(stat))
		{
			return "+0";
		}
		int aTotal = Integer.parseInt(getBaseToken(pc, stat));
		int temp = pc.getModForNumber(aTotal, stat);

		return Delta.toString(temp);
	}
}
