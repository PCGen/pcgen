/*
 * AbilityToken.java
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
 * Created on 20/11/2006
 *
 * $Id: $
 */

package pcgen.io.exporttoken;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.io.ExportHandler;
import pcgen.util.enumeration.Visibility;

/**
 * <code>AbilityToken</code> handles the output of ability information.
 * The format is ABILITY.w.x.y.z where
 * w is the category (FEAT, FIGHTER etc, or ALL)
 * x is the ability's position in the list of abilities - 0-based index.
 * y is the required ability type - default is ALL).
 * z is what is to be output DESC, TYPE, SOURCE, default is name, or TYPE=<type> - type filter
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision: $
 */
public class AbilityToken extends Token
{

	/** Token Name */
	public static final String TOKENNAME = "ABILITY";

	/** Default Ability = 0**/
	public static final int ABILITY_DEFAULT = 0;
	/** Visible Ability = 1 */
	public static final int ABILITY_VISIBLE = 1;
	/** Hidden Ability = 2 */
	public static final int ABILITY_HIDDEN = 2;
	/** All Abilitys = 3 */
	public static final int ABILITY_ALL = 3;

	//private int visibility = ABILITY_DEFAULT;
	private List<Ability> abilityList = new ArrayList<Ability>();
	//TODO: Should these be static? They probably never get used if not.
	private PlayerCharacter cachedPC = null;
	private int cachedPcSerial = 0;
	private String lastToken = null;
	private AbilityCategory lastCategory = null;
	private int visibility = ABILITY_DEFAULT;

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
	 * @see pcgen.io.exporttoken.Token#getToken(java.lang.String,
	 *      pcgen.core.PlayerCharacter, pcgen.io.ExportHandler)
	 */
	@Override
	public String getToken(String tokenSource, PlayerCharacter pc,
		ExportHandler eh)
	{
		final StringTokenizer aTok = new StringTokenizer(tokenSource, ".");
		// Skip the ABILITY token itself
		final String tokenString = aTok.nextToken();
		final String catString = aTok.nextToken();
		final AbilityCategory aCategory =
				SettingsHandler.getGame().getAbilityCategory(catString);

		
		return getTokenForCategory(tokenSource, pc, eh, aTok, tokenString,
			aCategory);
	}

	/**
	 * Produce the ABILITY token output for a specific ability 
	 * category.
	 *  
	 * @param tokenSource The token being processed. 
	 * @param pc The character being processed.
	 * @param eh The export handler in use for the export.
	 * @param aTok The tokenised request, already past the category.
	 * @param tokenString The output token requested 
	 * @param aCategory The ability category being output.
	 * @return The token value.
	 */
	protected String getTokenForCategory(String tokenSource,
		PlayerCharacter pc, ExportHandler eh, final StringTokenizer aTok,
		final String tokenString, final AbilityCategory aCategory)
	{
		if (cachedPC != pc || !aCategory.equals(lastCategory)
			|| cachedPcSerial != pc.getSerial()
			|| !tokenString.equals(lastToken))
		{
			// Overridden by subclasses to return the right list.
			abilityList = getAbilityList(pc, aCategory);
			cachedPC = pc;
			lastCategory = aCategory;
			cachedPcSerial = pc.getSerial();
			lastToken = tokenString;
		}

		// Default values
		List<String> types = new ArrayList<String>();
		List<String> negate = new ArrayList<String>();
		String abilityType = null;

		// abilityIndex holds the number of the ability we want, is 
		// decremented as we iterate through the list. It is only 
		// decremented if the current ability matches the desired ability
		int abilityIndex = -1;

		while (aTok.hasMoreTokens())
		{
			final String bString = aTok.nextToken();

			try
			{
				abilityIndex = Integer.parseInt(bString);

				break;
			}
			catch (NumberFormatException exc)
			{
				if (bString.equals("VISIBLE"))
				{
					visibility = ABILITY_VISIBLE;
					continue;
				}
				else if (bString.equals("HIDDEN"))
				{
					visibility = ABILITY_HIDDEN;
					continue;
				}
				else if (bString.equals("ALL"))
				{
					visibility = ABILITY_ALL;
					continue;
				}
				else
				{
					abilityType = bString;
				}
			}

		}

		while (aTok.hasMoreTokens())
		{
			final String typeStr = aTok.nextToken();

			int typeInd = typeStr.indexOf("TYPE");
			if (typeInd != -1 && typeStr.length() > 4)
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
				AbilityToken.buildAbilityList(types, negate, abilityType,
					visibility, abilityList);

		String retString =
				getRetString(tokenSource, pc, eh, abilityIndex, aList);

		return retString;
	}

	/**
	 * Build up the list of abilities of interest based on the type selection.
	 * 
	 * @param types The list of types which it must match at least one of.
	 * @param negate The list of types it must not match any of.
	 * @param abilityType The type definition it must match.
	 * @return
	 */
	static List<Ability> buildAbilityList(List<String> types,
		List<String> negate, String abilityType, int visibility,
		List<Ability> abilityList)
	{
		List<Ability> aList = new ArrayList<Ability>();

		Globals.sortPObjectListByName(abilityList);

		for (Ability aAbility : abilityList)
		{
			boolean matchTypeDef = false;
			boolean matchVisibilityDef = false;

			if (abilityType != null)
			{
				if (aAbility.isType(abilityType))
				{
					matchTypeDef = true;
				}
			}
			else
			{
				matchTypeDef = true;
			}

			boolean istype = false;
			boolean isnttype = true;

			// is at leas one of the types we've asked for
			if (types.size() > 0)
			{
				for (String typeStr : types)
				{
					istype |= aAbility.isType(typeStr);
				}
			}
			else
			{
				istype = true;
			}

			// isn't all the types we've said it's not
			for (String typeStr : negate)
			{
				isnttype &= !aAbility.isType(typeStr);
			}

			matchTypeDef = matchTypeDef && istype && isnttype;

			switch (visibility)
			{
				case ABILITY_ALL:
					matchVisibilityDef = true;
					break;
				case ABILITY_HIDDEN:
					if (aAbility.getSafe(ObjectKey.VISIBILITY) == Visibility.HIDDEN
						|| aAbility.getSafe(ObjectKey.VISIBILITY) == Visibility.DISPLAY_ONLY)
					{
						matchVisibilityDef = true;
					}
					break;
				case ABILITY_VISIBLE: // Fall thru intentional
				default:
					if (aAbility.getSafe(ObjectKey.VISIBILITY) == Visibility.DEFAULT
						|| aAbility.getSafe(ObjectKey.VISIBILITY) == Visibility.OUTPUT_ONLY)
					{
						matchVisibilityDef = true;
					}
					break;
			}

			if (matchTypeDef && matchVisibilityDef)
			{
				aList.add(aAbility);
			}
		}
		return aList;
	}

	/**
	 * Calculate the token value for the ability token.
	 * 
	 * @param tokenSource The text of the export token.
	 * @param pc The character ebign exported.
	 * @param eh The export handler.
	 * @param abilityIndex The location f the ability in the list.
	 * @param aList The list of abilities.
	 * @return The token value.
	 */
	private String getRetString(String tokenSource, PlayerCharacter pc,
		ExportHandler eh, int abilityIndex, List<Ability> aList)
	{
		String retString = "";
		Ability aAbility;
		if (abilityIndex >= 0 && abilityIndex < aList.size())
		{
			aAbility = aList.get(abilityIndex);

			if (abilityIndex == aList.size() - 1 && eh != null
				&& eh.getExistsOnly())
			{
				eh.setNoMoreItems(true);
			}

			if (tokenSource.endsWith(".DESC"))
			{
				retString += aAbility.getDescription(pc);
			}
			else if (tokenSource.endsWith(".BENEFIT"))
			{
				retString += aAbility.getBenefits(pc);
			}
			else if (tokenSource.endsWith(".TYPE"))
			{
				retString += aAbility.getType();
			}
			else if (tokenSource.endsWith(".ASSOCIATED"))
			{
				StringBuffer buf = new StringBuffer();

				for (int j = 0; j < aAbility.getAssociatedCount(); j++)
				{
					if (j != 0)
					{
						buf.append(",");
					}
					buf.append(aAbility.getAssociated(j));
				}

				retString += buf.toString();
			}
			else if (tokenSource.endsWith(".ASSOCIATEDCOUNT"))
			{
				retString += Integer.toString(aAbility.getAssociatedCount());
			}
			else if (tokenSource.endsWith(".SOURCE"))
			{
				retString += aAbility.getDefaultSourceString();
			}
//			else if (tokenSource.indexOf(".IS=") != -1)
//			{
//				final String type = tokenSource.substring(tokenSource.indexOf(".IS=")+4);
//				retString += aAbility.isType(type) == true ? "1" : "0";
//			}
//			else if (tokenSource.endsWith(".NATURE"))
//			{
//				retString += aAbility.getFeatType();
//			}
			else
			{
				retString += aAbility.qualifiedName();
			}
		}
		else if (eh != null && eh.getExistsOnly())
		{
			eh.setNoMoreItems(true);
		}

		return retString;
	}

	/**
	 * Returns the correct list of abilities for the character.
	 * This method is overridden in subclasses if they need to change the list
	 * of abilities looked at.
	 *
	 * @param pc the character who's abilities we are retrieving.
	 * @param aCategory The category of ability being reported. 
	 * @return List of abilities.
	 */
	protected List<Ability> getAbilityList(PlayerCharacter pc,
		final AbilityCategory aCategory)
	{
		final List<Ability> abilityList = new ArrayList<Ability>();
		Collection<AbilityCategory> allCats =
				SettingsHandler.getGame().getAllAbilityCategories();
		for (AbilityCategory aCat : allCats)
		{
			if (aCat.getAbilityCategory().equals(aCategory.getKeyName()))
			{
				abilityList.addAll(pc.getRealAbilitiesList(aCat));
			}
		}
		return abilityList;
	}

	/**
	 * @return the visibility
	 */
	protected int getVisibility()
	{
		return visibility;
	}

	/**
	 * @param visibility the visibility to set
	 */
	protected void setVisibility(int visibility)
	{
		this.visibility = visibility;
	}

}
