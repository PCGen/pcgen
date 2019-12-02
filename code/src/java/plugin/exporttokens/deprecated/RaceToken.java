/*
 * RaceToken.java
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
 */
package plugin.exporttokens.deprecated;

import java.util.ArrayList;
import java.util.List;

import pcgen.cdom.base.Constants;
import pcgen.cdom.content.LevelCommandFactory;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.core.SettingsHandler;
import pcgen.core.SpecialAbility;
import pcgen.core.analysis.OutputNameFormatting;
import pcgen.core.display.CharacterDisplay;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.Token;
import pcgen.system.LanguageBundle;

/**
 * This class implements support for the RACE set of tokens.
 * RACE - Displays characters race.
 * RACE.ABILITYLIST - Displays a comma delimited list of the characters racial 
 * special abilities.
 */
public class RaceToken extends Token
{
	private static final String TOKENNAME = "RACE"; //$NON-NLS-1$
	private static final String[] SUBTOKENLIST = {"ABILITYLIST"}; //$NON-NLS-1$

	@Override
	public String getTokenName()
	{
		return TOKENNAME;
	}

	@Override
	public String getToken(String tokenSource, PlayerCharacter pc, ExportHandler eh)
	{
		String retString = Constants.EMPTY_STRING;

		if (TOKENNAME.equals(tokenSource))
		{
			retString = getRaceToken(pc);
		}
		else
		{
			final String preString = TOKENNAME + SUBTOKENSEP;
            for (String s : SUBTOKENLIST) {
                final String subToken = preString + s;
                if (subToken.equals(tokenSource)) {
                    retString = getSubToken(s, pc.getDisplay());
                }
            }
		}

		return retString;
	}

	private static String getSubToken(final String subToken, CharacterDisplay display)
	{
		if (!subToken.equals(SUBTOKENLIST[0]))
		{
			return Constants.EMPTY_STRING;
		}

		final List<SpecialAbility> saList = new ArrayList<>();
		Race race = display.getRace();
		saList.addAll(display.getResolvedUserSpecialAbilities(race));
		saList.addAll(display.getResolvedSpecialAbilities(race));

		if (saList.isEmpty())
		{
			return Constants.EMPTY_STRING;
		}

		StringBuilder returnString = new StringBuilder();
		boolean firstLine = true;
		for (SpecialAbility sa : saList)
		{
			if (!firstLine)
			{
				returnString.append(", "); //$NON-NLS-1$
			}

			firstLine = false;

			returnString.append(sa.getDisplayName());
		}

		return returnString.toString();
	}

	private static String getRaceToken(PlayerCharacter pc)
	{
		String retString = Constants.EMPTY_STRING;

		Race race = pc.getDisplay().getRace();
		String tempRaceName = OutputNameFormatting.getOutputName(race);

		if (tempRaceName == null || tempRaceName.isEmpty())
		{
			tempRaceName = race.getDisplayName();
		}

		StringBuilder extraRaceInfo = new StringBuilder(40);

		String subRace = pc.getDisplay().getSubRace();
		if (subRace != null)
		{
			extraRaceInfo.append(subRace);
		}

		if (SettingsHandler.hideMonsterClasses())
		{
			LevelCommandFactory lcf = race.get(ObjectKey.MONSTER_CLASS);

			if (lcf != null)
			{
				PCClass monsterClass = lcf.getPCClass();
				final PCClass aClass = pc.getClassKeyed(monsterClass.getKeyName());

				if (aClass != null)
				{
					int minHD = lcf.getLevelCount().resolve(pc, "").intValue();
					int monsterHD = pc.getDisplay().getLevel(aClass);

					if (monsterHD != minHD)
					{
						if (extraRaceInfo.length() != 0)
						{
							extraRaceInfo.append(' ');
						}

						extraRaceInfo.append(monsterHD).append(LanguageBundle.getString("in_hdLabel")); //$NON-NLS-1$
					}
				}
			}
		}

		retString = tempRaceName;

		if (extraRaceInfo.length() != 0)
		{
			retString += " (" + extraRaceInfo + ')'; //$NON-NLS-1$
		}

		return retString;
	}
}
