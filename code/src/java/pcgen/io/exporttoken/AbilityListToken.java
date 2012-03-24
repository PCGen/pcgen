/*
 * AbilityListToken.java
 * Copyright 2006 (C) James Dempsey
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on 21/11/2006
 *
 * $Id$
 */

package pcgen.io.exporttoken;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.cdom.enumeration.Nature;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.core.analysis.QualifiedName;
import pcgen.io.ExportHandler;

/**
 * <code>AbilityListToken</code> handles the output of a comma separated 
 * list of ability information.
 * 
 * The format is ABILITYLIST.y.z where
 * y is the category (FEAT, FIGHTER etc, or ALL)
 * z is an option list of TYPE=<type> - type filter - may be negated
 *
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */
public class AbilityListToken extends Token
{
	private static final String DELIM = ", ";

	//TODO: Should these be static to enable the caching?
	private List<Ability> abilityList = null;
	private PlayerCharacter lastPC = null;
	private int lastPCSerial;
	private String lastType = "";
	private AbilityCategory lastCategory = null;

	/** Token Name */
	public static final String TOKENNAME = "ABILITYLIST";

	/**
	 * Get the TOKENNAME
	 * @return TOKENNAME
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
		ExportHandler eh)
	{
		final StringTokenizer aTok = new StringTokenizer(tokenSource, ".");
		// Skip the ABILITYLIST token itself
		final String tokenString = aTok.nextToken();
		final String catString = aTok.nextToken();
		final AbilityCategory aCategory =
				SettingsHandler.getGame().getAbilityCategory(catString);

		return getTokenForCategory(pc, aTok, tokenString, aCategory);
	}

	/**
	 * Produce the ABILITY token output for a specific ability 
	 * category.
	 *  
	 * @param pc The character being processed.
	 * @param aTok The tokenised request, already past the category.
	 * @param tokenString The output token requested 
	 * @param aCategory The ability category being output.
	 * @return The token value.
	 */
	protected String getTokenForCategory(PlayerCharacter pc,
		final StringTokenizer aTok, final String tokenString,
		final AbilityCategory aCategory)
	{
		if (aCategory == null)
		{
			return "";
		}
		StringBuffer retString = new StringBuffer();
		// If we haven't cached some of the processign data, then do so, this is so that 
		// if the Output Sheet loops over this token we don't process one-off stuff more than 
		// once
		if (lastPC != pc || !aCategory.equals(lastCategory)
			|| lastPCSerial != pc.getSerial() || !tokenString.equals(lastType))
		{
			Collection<AbilityCategory> cats = SettingsHandler.getGame().getAllAbilityCatsForKey(aCategory.getKeyName());
			abilityList = new ArrayList<Ability>();
			for (AbilityCategory abilityCategory : cats)
			{
				abilityList.addAll(getAbilityList(pc, abilityCategory));
			}
			lastPC = pc;
			lastCategory = aCategory;
			lastPCSerial = pc.getSerial();
			lastType = tokenString;
		}

		// Default values
		List<String> types = new ArrayList<String>();
		List<String> negate = new ArrayList<String>();

		while (aTok.hasMoreTokens())
		{
			final String typeStr = aTok.nextToken();

			int typeInd = typeStr.indexOf("TYPE=");
			if (typeInd != -1 && typeStr.length() > 5)
			{
				if (typeInd > 0)
				{
					negate.add(typeStr.substring(typeInd + 5));
				}
				else
				{
					types.add(typeStr.substring(typeInd + 5));
				}
			}
		}

		List<Ability> aList =
				AbilityToken.buildAbilityList(types, negate, null,
					AbilityToken.ABILITY_VISIBLE, abilityList);

		boolean needComma = false;
		for (Ability ability : aList)
		{
			if (needComma)
			{
				retString.append(DELIM);
			}
			needComma = true;

			retString.append(QualifiedName.qualifiedName(pc, ability));
		}

		return retString.toString();
	}

	/**
	 * Returns the correct list of abilities of a particular category for the character.
	 * This method is overridden in subclasses if they need to change the list
	 * of abilities looked at.
	 *
	 * @param pc the character who's feats we are retrieving.
	 * @param aCategory The category of ability required.
	 * @return List of feats.
	 */
	protected List<Ability> getAbilityList(PlayerCharacter pc,
		AbilityCategory aCategory)
	{
		List<Ability> listOfAbilities = new ArrayList<Ability>();
		for (Ability ability : pc.getAbilityList(aCategory, Nature.NORMAL))
		{
			listOfAbilities.add(ability);
		}
		return listOfAbilities;
	}

}
