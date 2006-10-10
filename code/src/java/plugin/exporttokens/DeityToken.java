/*
 * DeityToken.java
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

import pcgen.core.Deity;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.core.utils.CoreUtility;
import pcgen.core.utils.ListKey;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.Token;

import java.util.StringTokenizer;

/**
 * Deal with tokens:
 * DEITY (Defaults to OUTPUTNAME)
 * DEITY.NAME
 * DEITY.OUTPUTNAME
 * DEITY.DOMAINLIST
 * DEITY.FOLLOWERALIGNMENT
 * DEITY.ALIGNMENT
 * DEITY.APPEARANCE
 * DEITY.DESCRIPTION
 * DEITY.HOLYITEM
 * DEITY.FAVOREDWEAPON
 * DEITY.PANTHEONLIST
 * DEITY.SOURCE
 * DEITY.SA
 * DEITY.TITLE
 * DEITY.WORSHIPPERS
 */
public class DeityToken extends Token
{
	/** Token name */
	public static final String TOKENNAME = "DEITY";

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

		if (pc.getDeity() != null)
		{
			StringTokenizer aTok = new StringTokenizer(tokenSource, ".", false);
			String subTag = "OUTPUTNAME";
			Deity deity = pc.getDeity();

			if (aTok.countTokens() > 1)
			{
				aTok.nextToken();
				subTag = aTok.nextToken();
			}

			if ("NAME".equals(subTag))
			{
				retString = getNameToken(deity);
			}
			else if ("OUTPUTNAME".equals(subTag))
			{
				retString = getOutputNameToken(deity);
			}
			else if ("DOMAINLIST".equals(subTag))
			{
				retString = getDomainListToken(deity);
			}
			else if ("FOLLOWERALIGNMENT".equals(subTag))
			{
				retString = getFollowerAlignmentToken(deity);
			}
			else if ("ALIGNMENT".equals(subTag))
			{
				retString = getAlignmentToken(deity);
			}
			else if ("APPEARANCE".equals(subTag))
			{
				retString = getAppearanceToken(deity);
			}
			else if ("DESCRIPTION".equals(subTag))
			{
				retString = getDescriptionToken(pc, deity);
			}
			else if ("HOLYITEM".equals(subTag))
			{
				retString = getHolyItemToken(deity);
			}
			else if ("FAVOREDWEAPON".equals(subTag))
			{
				retString = getFavoredWeaponToken(deity);
			}
			else if ("PANTHEONLIST".equals(subTag))
			{
				retString = getPantheonListToken(deity);
			}
			else if ("SOURCE".equals(subTag))
			{
				retString = getSourceToken(deity);
			}
			else if ("SA".equals(subTag))
			{
				retString = getSAToken(deity);
			}
			else if ("TITLE".equals(subTag))
			{
				retString = getTitleToken(deity);
			}
			else if ("WORSHIPPERS".equals(subTag))
			{
				retString = getWorshippersToken(deity);
			}
		}

		return retString;
	}

	/**
	 * Get Alignment Sub token
	 * @param deity
	 * @return Alignment Sub token
	 */
	public static String getAlignmentToken(Deity deity)
	{
		return deity.getAlignment();
	}

	/**
	 * Get appearance sub token
	 * @param deity
	 * @return appearance sub token
	 */
	public static String getAppearanceToken(Deity deity)
	{
		return deity.getAppearance();
	}

	/**
	 * Get description sub token
	 * @param deity
	 * @return description sub token
	 */
	public static String getDescriptionToken(final PlayerCharacter aPC, Deity deity)
	{
		return deity.getDescription(aPC);
	}

	/**
	 * Get domain list sub token
	 * @param deity
	 * @return domain list sub token
	 */
	public static String getDomainListToken(Deity deity)
	{
		String retString = "";
		deity.getDomainList();
		boolean firstLine = true;

		for (int i = 0; i < deity.getDomainList().size(); i++)
		{
			if (!firstLine)
			{
				retString += ", ";
			}

			firstLine = false;

			retString += (deity.getDomainList().get(i)).getDisplayName();
		}

		return retString;
	}

	/**
	 * Get favoured weapon token
	 * @param deity
	 * @return favoured weapon token
	 */
	public static String getFavoredWeaponToken(Deity deity)
	{
		return deity.getFavoredWeapon();
	}

	/**
	 * Get follower alignment sub token
	 * @param deity
	 * @return follower alignment sub token
	 */
	public static String getFollowerAlignmentToken(Deity deity)
	{
		String retString = "";
		boolean firstLine = true;
		String fAlignment = deity.getFollowerAlignments();

		for (int i = 0; i < fAlignment.length(); i++)
		{
			if (!firstLine)
			{
				retString += ", ";
			}

			firstLine = false;

			retString += SettingsHandler.getGame().getShortAlignmentAtIndex(fAlignment.charAt(i) - 48);
		}

		return retString;
	}

	/**
	 * Get holy item sub token
	 * @param deity
	 * @return holy item sub token
	 */
	public static String getHolyItemToken(Deity deity)
	{
		return deity.getHolyItem();
	}

	/**
	 * Get the name sub token
	 * @param deity
	 * @return Get the name sub token
	 */
	public static String getNameToken(Deity deity)
	{
		return deity.getDisplayName();
	}

	/**
	 * Get the output name sub token
	 * @param deity
	 * @return output name sub token
	 */
	public static String getOutputNameToken(Deity deity)
	{
		return deity.getOutputName();
	}

	/**
	 * Get the pantheon list sub token
	 * @param deity
	 * @return the pantheon list sub token
	 */
	public static String getPantheonListToken(Deity deity)
	{
		return CoreUtility.join(deity.getPantheonList(), ", ");
	}

	/**
	 * Get the SA sub token
	 * @param deity
	 * @return the SA sub token
	 */
	public static String getSAToken(Deity deity)
	{
		if (deity.containsListFor(ListKey.SPECIAL_ABILITY))
		{
			return CoreUtility.join(deity.getListFor(ListKey.SPECIAL_ABILITY), ", ");
		}

		return "";
	}

	/**
	 * Get the source sub token
	 * @param deity
	 * @return the source sub token
	 */
	public static String getSourceToken(Deity deity)
	{
		return deity.getDefaultSourceString();
	}

	/**
	 * Get the title sub token
	 * @param deity
	 * @return the title sub token
	 */
	public static String getTitleToken(Deity deity)
	{
		return deity.getTitle();
	}

	/**
	 * Get the worshippers sub token
	 * @param deity
	 * @return the worshippers sub token
	 */
	public static String getWorshippersToken(Deity deity)
	{
		return deity.getWorshippers();
	}
}
