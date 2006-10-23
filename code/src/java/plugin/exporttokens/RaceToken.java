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
 *
 * Created on December 15, 2003, 12:21 PM
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package plugin.exporttokens;

import pcgen.core.*;
import pcgen.core.utils.ListKey;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.Token;
import pcgen.util.PropertyFactory;

import java.util.List;

/**
 * This class implements support for the RACE set of tokens.
 * RACE - Displays characters race.
 * RACE.ABILITYLIST - Displays a comma delimited list of the characters racial 
 * special abilities.
 */
public class RaceToken extends Token
{
	private static final String TOKENNAME = "RACE"; //$NON-NLS-1$
	private static final String[] SUBTOKENLIST = { "ABILITYLIST" }; //$NON-NLS-1$

	/**
	 * @see pcgen.io.exporttoken.Token#getTokenName()
	 */
	@Override
	public String getTokenName()
	{
		return TOKENNAME;
	}

	/**
	 * @see pcgen.io.exporttoken.Token#getToken(java.lang.String, pcgen.core.PlayerCharacter, pcgen.io.ExportHandler)
	 */
	@Override
	public String getToken(String tokenSource, PlayerCharacter pc, 
			@SuppressWarnings("unused")	ExportHandler eh)
	{
		String retString = Constants.EMPTY_STRING;

		if (TOKENNAME.equals(tokenSource))
		{
			retString = getRaceToken(pc);
		}
		else
		{
			final String preString = TOKENNAME + SUBTOKENSEP;
			for ( int i = 0; i < SUBTOKENLIST.length; i++ )
			{
				final String subToken = preString + SUBTOKENLIST[i];
				if ( subToken.equals( tokenSource ) )
				{
					retString = getSubToken( SUBTOKENLIST[i], pc );
				}
			}
		}

		return retString;
	}

	private static String getSubToken( final String subToken, final PlayerCharacter pc)
	{
		if ( !subToken.equals( SUBTOKENLIST[0] ) )
		{
			return Constants.EMPTY_STRING;
		}
		final List<SpecialAbility> saList = pc.getRace().getListFor(ListKey.SPECIAL_ABILITY);

		if ((saList == null) || saList.isEmpty())
		{
			return Constants.EMPTY_STRING;
		}

		StringBuffer returnString = new StringBuffer();
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

//		String tempRaceName = pc.getRace().getDisplayName();

//		if (tempRaceName.equals(Constants.s_NONE))
//		{
//			tempRaceName = pc.getRace().getOutputName();
//		}

		String tempRaceName = pc.getRace().getOutputName();

		if ( tempRaceName == null || tempRaceName.length() == 0 )
		{
			tempRaceName = pc.getRace().getDisplayName();
		}
		
		StringBuffer extraRaceInfo = new StringBuffer(40);

		if (!pc.getSubRace().equals(Constants.s_NONE))
		{
			extraRaceInfo.append(pc.getSubRace());
		}

		if (SettingsHandler.hideMonsterClasses())
		{
			final String monsterClass = pc.getRace().getMonsterClass(pc, false);

			if (monsterClass != null)
			{
				final PCClass aClass = pc.getClassKeyed(monsterClass);

				if (aClass != null)
				{
					int minHD = pc.getRace().hitDice(pc) + pc.getRace().getMonsterClassLevels(pc);
					int monsterHD = pc.getRace().hitDice(pc) + aClass.getLevel();

					if (monsterHD != minHD)
					{
						if (extraRaceInfo.length() != 0)
						{
							extraRaceInfo.append(' ');
						}

						extraRaceInfo.append(monsterHD).append(PropertyFactory.getString("in_hdLabel")); //$NON-NLS-1$
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
