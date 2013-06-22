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
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.enumeration.AspectName;
import pcgen.cdom.enumeration.MapKey;
import pcgen.cdom.enumeration.Nature;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.SourceFormat;
import pcgen.cdom.helper.Aspect;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.BenefitFormatting;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.core.analysis.QualifiedName;
import pcgen.io.ExportHandler;
import pcgen.util.enumeration.Visibility;

/**
 * <code>AbilityToken</code> handles the output of ability information.
 * 
 * The format is ABILITY.u.v.w.x.y.z where:
 * <ul>
 * <li>u is the AbilityCategory (FEAT, FIGHTER etc, or ALL) - Mandatory</li>
 * <li>v is the visibility (DEFAULT, ALL, VISIBLE, HIDDEN) - Optional</li>
 * <li>w is the ability type filtering via strings - Optional</li>
 * <li>x is the ability's position in the list of abilities, 0-based index -
 * Optional</li>
 * <li>y is the ability type filtering via AbilityType - default is ALL).</li>
 * <li>z is what is to be output DESC, TYPE, SOURCE, default is name, or
 * TYPE=&lt;type&gt; - type filter</li>
 * </ul>
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision: $
 */
public class AbilityToken extends Token
{

	/** Token Name */
	public static final String TOKENNAME = "ABILITY";

	/** Filter Definition :: Default Ability = 0 */
	public static final int ABILITY_DEFAULT = 0;

	/** Filter Definition :: Visible Ability = 1 */
	public static final int ABILITY_VISIBLE = 1;

	/** Filter Definition :: Hidden Ability = 2 */
	public static final int ABILITY_HIDDEN = 2;

	/** Filter Definition :: All Abilities = 3 */
	public static final int ABILITY_ALL = 3;

	/** The list of abilities to get the ability from */
	private List<Ability> abilityList = new ArrayList<Ability>();

	/** The current visibility filtering to apply */
	private int visibility = ABILITY_DEFAULT;

	/** The cached PC */
	private PlayerCharacter cachedPC = null;

	/** The cached PC serial (serial holds whether a PC has been changed) */
	private int cachedPcSerial = 0;

	/** The last token in the list of abilities */
	private String lastToken = null;

	/** The last ability category in the list of abilities */
	private AbilityCategory lastCategory = null;

	/**
	 * Get the TOKENNAME
	 * 
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
		// Skip the ABILITY token itself
		final StringTokenizer aTok = new StringTokenizer(tokenSource, ".");
		final String tokenString = aTok.nextToken();

		// Get the Ability Category from the Gamemode given the key
		final String categoryString = aTok.nextToken();
		final AbilityCategory aCategory =
				SettingsHandler.getGame().getAbilityCategory(categoryString);

		// Get the ABILITY token for the category
		return getTokenForCategory(tokenSource, pc, eh, aTok, tokenString,
			aCategory);
	}

	/**
	 * Produce the ABILITY token output for a specific ability category.
	 * 
	 * @param tokenSource
	 *            The token being processed.
	 * @param pc
	 *            The character being processed.
	 * @param eh
	 *            The export handler in use for the export.
	 * @param aTok
	 *            The tokenised request, already past the category.
	 * @param tokenString
	 *            The output token requested
	 * @param aCategory
	 *            The ability category being output.
	 * @return The token value.
	 */
	protected String getTokenForCategory(String tokenSource,
		PlayerCharacter pc, ExportHandler eh, final StringTokenizer aTok,
		final String tokenString, final AbilityCategory aCategory)
	{
		boolean cacheAbilityProcessingData =
				(cachedPC != pc || !aCategory.equals(lastCategory)
					|| cachedPcSerial != pc.getSerial() || !tokenString
					.equals(lastToken));

		// As this method can effectively be called by an OS FOR token, there 
		// is a performance saving in caching some of the one-off processing data 
		if (cacheAbilityProcessingData)
		{
			// Overridden by subclasses to return the right list.
			abilityList = getAbilityList(pc, aCategory);
			cachedPC = pc;
			lastCategory = aCategory;
			cachedPcSerial = pc.getSerial();
			lastToken = tokenString;
		}

		// Ability Types Filter List
		List<String> types = new ArrayList<String>();
		// Negated Ability Types Filter List (excludes from types)
		List<String> negate = new ArrayList<String>();
		// Ability Type
		String abilityType = null;
		// Ability Types Filter List
		String key = null;
		// Ability List
		List<Ability> aList = null;

		/*
		 * abilityIndex holds the number of the ability we want, is decremented
		 * as we iterate through the list. It is only decremented if the current
		 * ability matches the desired ability
		 */
		int abilityIndex = -1;

		/* 
		 * Grab the next token which will either be be:
		 * visibility (v), type (w) or index (x), stop processing 
		 * once you hit the index token 
		 */
		while (aTok.hasMoreTokens())
		{
			final String bString = aTok.nextToken();
			try
			{
				// Get the mandatory ability index
				abilityIndex = Integer.parseInt(bString);
				break;
			}
			// The optional visibility (v) or type (w) has been provided, so deal with those 
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

		/* 
		 * Grab the next token which will either be be:
		 * TYPE (y) or property (z), stop processing 
		 * once you hit the last token 
		 */
		while (aTok.hasMoreTokens())
		{
			final String typeStr = aTok.nextToken();
			int typeInd = typeStr.indexOf("TYPE=");
			// If it's TYPE and it actually has a value attached then process it 
			if (typeInd != -1 && typeStr.length() > 5)
			{
				// It's a type to be excluded from the filter list 
				if (typeStr.startsWith("!"))
				{
					negate.add(typeStr.substring(typeInd + 5));
				}
				else
				{
					types.add(typeStr.substring(typeInd + 5));
				}
			}
			
			int keyInd = typeStr.indexOf("KEY=");
			// If it's KEY and it actually has a value attached then process it 
			if (keyInd != -1 && typeStr.length() > 4)
			{
				key = typeStr.substring(keyInd + 4);
			}
		}

		// Build the list of abilities that we should display
		if (key == null)
		{
			aList = AbilityToken.buildAbilityList(types, negate, abilityType,
					visibility, abilityList);
		}
		else
		{
			aList = AbilityToken.buildAbilityList(key, visibility, abilityList);
		}

		// Build the return string to give to the OutputSheet
		String retString =
				getRetString(tokenSource, pc, eh, abilityIndex, aList);

		return retString;
	}

	/**
	 * Build up the list of abilities of interest based on the type and visibility selection.
	 * 
	 * @param types
	 *            The list of types which it must match at least one of.
	 * @param negate
	 *            The list of types it must not match any of.
	 * @param abilityType
	 *            The type definition it must match.
	 * @return List of abilities based on the type and visibility selection.
	 */
	static List<Ability> buildAbilityList(List<String> types,
		List<String> negate, String abilityType, int visibility,
		List<Ability> listOfAbilities)
	{
		// List to build up
		List<Ability> aList = new ArrayList<Ability>();

		// Sort the ability list passed in
		Globals.sortPObjectListByName(listOfAbilities);

		boolean matchTypeDef = false;
		boolean matchVisibilityDef = false;

		// For each ability figure out whether it should be displayed depending
		// on its visibility filtering and its ability type filtering 
		for (Ability aAbility : listOfAbilities)
		{
			matchTypeDef =
					abilityMatchesType(abilityType, aAbility, types, negate);
			matchVisibilityDef = abilityMatchesVisibility(visibility, aAbility);
			if (matchTypeDef && matchVisibilityDef)
			{
				aList.add(aAbility);
			}
		}
		return aList;
	}

	/**
	 * Build up the list of abilities of interest based on the key and visibility selection.
	 * 
	 * @param key
	 *            The key of the wanted ability.
	 * @return List of abilities based on the type and visibility selection.
	 */
	static List<Ability> buildAbilityList(String key, int visibility,
		List<Ability> listOfAbilities)
	{
		// List to build up
		List<Ability> aList = new ArrayList<Ability>();

		// Sort the ability list passed in
		Globals.sortPObjectListByName(listOfAbilities);

		boolean matchKeyDef = false;
		boolean matchVisibilityDef = false;

		// For each ability figure out whether it should be displayed depending
		// on its visibility filtering and its ability type filtering 
		for (Ability aAbility : listOfAbilities)
		{
			matchKeyDef = aAbility.getKeyName().equalsIgnoreCase(key);
			matchVisibilityDef = abilityMatchesVisibility(visibility, aAbility);
			if (matchKeyDef && matchVisibilityDef)
			{
				aList.add(aAbility);
			}
		}
		return aList;
	}

	/**
	 * Helper method, returns true if the ability has one of the ability types that 
	 * we are matching on.
	 * 
	 * @param abilityType The ability Type to test
	 * @param aAbility The ability
	 * @param types The list of types we're trying to match on
	 * @param negate The exclusion list of types
	 * @return True if it matches one of the types else false
	 */
	private static boolean abilityMatchesType(String abilityType,
		Ability aAbility, List<String> types, List<String> negate)
	{
		boolean matchTypeDef = false;

		// If the ability type is an actual properly registered type or its null
		// then match the type definition
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

		// If the types contains at least one of the types we've asked for
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

		// It isn't all the types we've said
		for (String typeStr : negate)
		{
			isnttype &= !aAbility.isType(typeStr);
		}

		matchTypeDef = matchTypeDef && istype && isnttype;
		return matchTypeDef;
	}

	/**
	 * Helper method, returns true if the ability meets the visibility requirements.
	 * 
	 * @param visibility The ability Type to test
	 * @param aAbility The ability
	 * @return true if it meets the visibility requirements
	 */
	private static boolean abilityMatchesVisibility(int visibility,
		Ability aAbility)
	{
		boolean matchVisibilityDef = false;
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
			case ABILITY_VISIBLE: // Fall through intentional
			default:
				if (aAbility.getSafe(ObjectKey.VISIBILITY) == Visibility.DEFAULT
					|| aAbility.getSafe(ObjectKey.VISIBILITY) == Visibility.OUTPUT_ONLY)
				{
					matchVisibilityDef = true;
				}
				break;
		}
		return matchVisibilityDef;
	}

	/**
	 * Calculate the token value (return string) for the ability token.
	 * 
	 * @param tokenSource
	 *            The text of the export token.
	 * @param pc
	 *            The character being exported.
	 * @param eh
	 *            The export handler.
	 * @param abilityIndex
	 *            The location of the ability in the list.
	 * @param aList
	 *            The list of abilities to get the ability from.
	 * @return The token value.
	 */
	private String getRetString(String tokenSource, PlayerCharacter pc,
		ExportHandler eh, int abilityIndex, List<Ability> aList)
	{
		String retString = "";
		Ability aAbility;
		// If the ability index given is within a valid range
		if (abilityIndex >= 0 && abilityIndex < aList.size())
		{
			aAbility = aList.get(abilityIndex);

			// If it is the last item and there's a valid export handler and ??? TODO
			// Then tell the ExportHandler that there is no more processing needed
			if (abilityIndex == aList.size() - 1 && eh != null
				&& eh.getExistsOnly())
			{
				eh.setNoMoreItems(true);
			}

			if (tokenSource.endsWith(".DESC"))
			{
				retString = pc.getDescription(aAbility);
			}
			else if (tokenSource.endsWith(".BENEFIT"))
			{
				retString = BenefitFormatting.getBenefits(pc, aAbility);
			}
			else if (tokenSource.endsWith(".TYPE"))
			{
				retString = aAbility.getType().toUpperCase();
			}
			else if (tokenSource.endsWith(".ASSOCIATED"))
			{
				retString =
						StringUtil.join(pc.getAssociationList(aAbility), ",");
			}
			else if (tokenSource.endsWith(".ASSOCIATEDCOUNT"))
			{
				retString =
						Integer.toString(pc
							.getDetailedAssociationCount(aAbility));
			}
			else if (tokenSource.endsWith(".SOURCE"))
			{
				retString =
						SourceFormat.getFormattedString(aAbility, Globals
							.getSourceDisplay(), true);
			}
			else if (tokenSource.endsWith(".ASPECT"))
			{
				retString = getAspectString(pc, aAbility);
			}
			else if (tokenSource.indexOf(".ASPECT.") > -1)
			{
				final String key =
						tokenSource
							.substring(tokenSource.indexOf(".ASPECT.") + 8);
				retString = getAspectString(pc, aAbility, key);
			}
			else if (tokenSource.endsWith(".ASPECTCOUNT"))
			{
				retString =
						Integer.toString(aAbility
							.getSafeSizeOfMapFor(MapKey.ASPECT));
			}
			else if (tokenSource.indexOf(".HASASPECT.") > -1)
			{
				final String key =
						tokenSource.substring(tokenSource
							.indexOf(".HASASPECT.") + 11);
				retString = getHasAspectString(aAbility, key);
			}
			else
			{
				retString = QualifiedName.qualifiedName(pc, aAbility);
			}
		}
		// If the ability index is not in a valid range then tell the 
		// ExportHandler that there are no more items to process
		else if (eh != null && eh.getExistsOnly())
		{
			eh.setNoMoreItems(true);
		}

		return retString;
	}

	/**
	 * Gets the aspect string.
	 * 
	 * @param pc
	 *            The character being exported.
	 * @param ability
	 *            The ability
	 * 
	 * @return the aspect string
	 */
	private String getAspectString(PlayerCharacter pc, Ability ability)
	{
		Set<AspectName> aspectKeys = ability.getKeysFor(MapKey.ASPECT);
		SortedSet<AspectName> sortedKeys = new TreeSet<AspectName>(aspectKeys);
		StringBuilder buff = new StringBuilder();
		for (AspectName key : sortedKeys)
		{
			if (buff.length() > 0)
			{
				buff.append(", ");
			}
			buff.append(ability.printAspect(pc, key));
		}
		return buff.toString();
	}

	/**
	 * Gets the aspect string for an aspect identified by position or name.
	 * 
	 * @param pc
	 *            The character being exported.
	 * @param ability
	 *            The ability being queried.
	 * @param key
	 *            The key (number or name) of the aspect to retrieve
	 * 
	 * @return the aspect string
	 */
	private String getAspectString(PlayerCharacter pc, Ability ability,
		String key)
	{
		if (key == null)
		{
			return "";
		}

		int index = -1;
		try
		{
			index = Integer.parseInt(key);
		}
		catch (NumberFormatException e)
		{
			// Ignore exception - expect this as we can get a String at this point
		}
		List<Aspect> aspects = null;
		if (index > -1)
		{
			if (index < ability.getSafeSizeOfMapFor(MapKey.ASPECT))
			{
				Set<AspectName> aspectKeys = ability.getKeysFor(MapKey.ASPECT);
				List<AspectName> sortedKeys =
						new ArrayList<AspectName>(aspectKeys);
				Collections.sort(sortedKeys);
				aspects = ability.get(MapKey.ASPECT, sortedKeys.get(index));
			}
		}
		else
		{
			aspects = getAspectsByName(ability, key);
		}

		StringBuilder buff = new StringBuilder();
		if (aspects != null)
		{
			for (int i = 0; i < aspects.size(); i++)
			{
				Aspect aspect = aspects.get(i);
				if (index > -1 & i == 0)
				{
					buff.append(aspect.getName()).append(": ");
				}
				buff.append(aspect.getAspectText(pc, ability));
			}
		}
		return buff.toString();
	}

	/**
	 * Gets the boolean (Y/N) string for the presence of the named aspect.
	 * 
	 * @param ability
	 *            The ability being queried.
	 * @param key
	 *            The key (name only) of the aspect to check
	 * 
	 * @return Y if the aspect is present, N if not.
	 */
	private String getHasAspectString(Ability ability, String key)
	{
		List<Aspect> target = getAspectsByName(ability, key);
		if (target == null)
		{
			return "N";
		}
		return "Y";
	}

	/**
	 * Retrieve a named aspect from the ability.
	 * 
	 * @param ability
	 *            The ability to query
	 * @param key
	 *            The name of the aspect
	 * @return The aspect, or null if not present.
	 */
	private List<Aspect> getAspectsByName(Ability ability, String key)
	{
		if (key == null)
		{
			return null;
		}

		return ability.get(MapKey.ASPECT, AspectName.getConstant(key));
	}

	/**
	 * Returns the correct list of abilities for the character. This method is
	 * overridden in subclasses if they need to change the list of abilities
	 * looked at.
	 * 
	 * @param pc
	 *            The character who's abilities we are retrieving.
	 * @param aCategory
	 *            The category of ability being reported.
	 * @return List of abilities.
	 */
	protected List<Ability> getAbilityList(PlayerCharacter pc,
		final AbilityCategory aCategory)
	{
		final List<Ability> listOfAbilities = new ArrayList<Ability>();
		Collection<AbilityCategory> allCats =
				SettingsHandler.getGame().getAllAbilityCategories();
		for (AbilityCategory aCat : allCats)
		{
			if (aCat.getParentCategory().equals(aCategory))
			{
				listOfAbilities.addAll(pc.getAbilityList(aCat, Nature.NORMAL));
			}
		}
		return listOfAbilities;
	}

	/**
	 * @return the visibility
	 */
	protected int getVisibility()
	{
		return visibility;
	}

	/**
	 * @param visibility
	 *            the visibility to set
	 */
	protected void setVisibility(int visibility)
	{
		this.visibility = visibility;
	}

}
