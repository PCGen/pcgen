/*
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
 *
 */

package pcgen.io.exporttoken;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.base.lang.StringUtil;
import pcgen.base.lang.UnreachableError;
import pcgen.base.util.GenericMapToList;
import pcgen.base.util.HashMapToList;
import pcgen.base.util.MapToList;
import pcgen.cdom.base.Constants;
import pcgen.cdom.content.CNAbility;
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
import pcgen.util.Logging;
import pcgen.util.enumeration.View;

/**
 * {@code AbilityToken} handles the output of ability information.
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
 */
public class AbilityToken extends Token
{

	/** Token Name */
	public static final String TOKENNAME = "ABILITY";

	/** The list of abilities to get the ability from */
	private MapToList<Ability, CNAbility> abilityList = new HashMapToList<>();

	/** The current visibility filtering to apply */
	private View view = View.VISIBLE_EXPORT;

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

	@Override
	public String getToken(String tokenSource, PlayerCharacter pc, ExportHandler eh)
	{
		// Skip the ABILITY token itself
		final StringTokenizer aTok = new StringTokenizer(tokenSource, ".");
		final String tokenString = aTok.nextToken();

		// Get the Ability Category from the Gamemode given the key
		final String categoryString = aTok.nextToken();
		final AbilityCategory aCategory = "ANY".equals(categoryString) ? AbilityCategory.ANY
			: SettingsHandler.getGameAsProperty().get().getAbilityCategory(categoryString);

		// Get the ABILITY token for the category
		return getTokenForCategory(tokenSource, pc, eh, aTok, tokenString, aCategory);
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
	protected String getTokenForCategory(String tokenSource, PlayerCharacter pc, ExportHandler eh,
		final StringTokenizer aTok, final String tokenString, final AbilityCategory aCategory)
	{
		boolean cacheAbilityProcessingData = (cachedPC != pc || !aCategory.equals(lastCategory)
			|| cachedPcSerial != pc.getSerial() || !tokenString.equals(lastToken));

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
		List<String> types = new ArrayList<>();
		// Negated Ability Types Filter List (excludes from types)
		List<String> negate = new ArrayList<>();
		// Ability Type
		String abilityType = null;
		// Ability Types Filter List
		String key = null;
		// Ability Aspect Filter
		String aspect = null;

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
				switch (bString)
				{
					case "VISIBLE" -> {
						view = View.VISIBLE_EXPORT;
						continue;
					}
					case "HIDDEN" -> {
						view = View.HIDDEN_EXPORT;
						continue;
					}
					case "ALL" -> {
						view = View.ALL;
						continue;
					}
					default -> abilityType = bString;
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
			int extypeInd = typeStr.indexOf("EXCLUDETYPE=");

			// If it's TYPE and it actually has a value attached then process it 
			if (typeInd != -1 && extypeInd == -1 && typeStr.length() > 5)
			{
				// It's a type to be excluded from the filter list 
				if (typeStr.startsWith("!"))
				{
					Logging.deprecationPrint(
						"The use of !TYPE with ABILITY output tokens is deprecated. Please use EXCLUDETYPE.");
					negate.add(typeStr.substring(typeInd + 5));
				}
				else
				{
					StringTokenizer incTok = new StringTokenizer(typeStr.substring(typeInd + 5), Constants.SEMICOLON);
					while (incTok.hasMoreTokens())
					{
						types.add(incTok.nextToken());
					}
				}
			}

			// If it's EXCLUDETYPE and it actually has a value attached then process it 
			if (extypeInd != -1 && typeStr.length() > 12)
			{
				// exclude TYPEs from comma-separated list
				StringTokenizer exTok = new StringTokenizer(typeStr.substring(extypeInd + 12), Constants.SEMICOLON);
				while (exTok.hasMoreTokens())
				{
					negate.add(exTok.nextToken());
				}
			}

			int keyInd = typeStr.indexOf("KEY=");
			// If it's KEY and it actually has a value attached then process it 
			if (keyInd != -1 && typeStr.length() > 4)
			{
				key = typeStr.substring(keyInd + 4);
			}

			int aspectInd = typeStr.indexOf("ASPECT=");
			// If it's ASPECT and it actually has a value attached then process it
			if (aspectInd != -1 && typeStr.length() > 7)
			{
				aspect = typeStr.substring(aspectInd + 7);
			}

		}

		// Ability List
		MapToList<Ability, CNAbility> aList;
		// Build the list of abilities that we should display
		if (key == null)
		{
			aList = AbilityToken.buildAbilityList(types, negate, abilityType, view, aspect, abilityList);
		}
		else
		{
			aList = AbilityToken.buildAbilityList(key, view, abilityList);
		}

		// Build the return string to give to the OutputSheet

		return getRetString(tokenSource, pc, eh, abilityIndex, aList);
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
	 * @param aspect
	 *            The aspect which it must match.
	 * @return List of abilities based on the type, visibility, and aspect selection.
	 */
	static MapToList<Ability, CNAbility> buildAbilityList(List<String> types, List<String> negate, String abilityType,
		View view, String aspect, MapToList<Ability, CNAbility> listOfAbilities)
	{
		List<Ability> aList = new ArrayList<>(listOfAbilities.getKeySet());

		// Sort the ability list passed in
		Globals.sortPObjectListByName(aList);

		boolean matchTypeDef;
		boolean matchVisibilityDef;
		boolean matchAspectDef;

		// List to build up
		List<Ability> bList = new ArrayList<>();

		// For each ability figure out whether it should be displayed depending
		// on its visibility filtering and its ability type filtering 
		for (Ability aAbility : aList)
		{
			matchTypeDef = abilityMatchesType(abilityType, aAbility, types, negate);
			matchVisibilityDef = abilityVisibleTo(view, aAbility);
			matchAspectDef = abilityMatchesAspect(aspect, aAbility);
			if (matchTypeDef && matchVisibilityDef && matchAspectDef)
			{
				bList.add(aAbility);
			}
		}
		try
		{
			MapToList<Ability, CNAbility> mtl = new GenericMapToList<>(LinkedHashMap.class);
			for (Ability a : bList)
			{
				mtl.addAllToListFor(a, listOfAbilities.getListFor(a));
			}
			return mtl;
		}
		catch (ReflectiveOperationException e)
		{
			throw new UnreachableError(e);
		}
	}

	/**
	 * Build up the list of abilities of interest based on the key and visibility selection.
	 * 
	 * @param key
	 *            The key of the wanted ability.
	 * @return List of abilities based on the type and visibility selection.
	 */
	static MapToList<Ability, CNAbility> buildAbilityList(String key, View view,
		MapToList<Ability, CNAbility> listOfAbilities)
	{
		List<Ability> aList = new ArrayList<>(listOfAbilities.getKeySet());

		// Sort the ability list passed in
		Globals.sortPObjectListByName(aList);

		boolean matchKeyDef;
		boolean matchVisibilityDef;

		// List to build up
		List<Ability> bList = new ArrayList<>();

		// For each ability figure out whether it should be displayed depending
		// on its visibility filtering and its ability type filtering 
		for (Ability aAbility : aList)
		{
			matchKeyDef = aAbility.getKeyName().equalsIgnoreCase(key);
			matchVisibilityDef = abilityVisibleTo(view, aAbility);
			if (matchKeyDef && matchVisibilityDef)
			{
				bList.add(aAbility);
			}
		}
		try
		{
			MapToList<Ability, CNAbility> mtl = new GenericMapToList<>(LinkedHashMap.class);
			for (Ability a : bList)
			{
				mtl.addAllToListFor(a, listOfAbilities.getListFor(a));
			}
			return mtl;
		}
		catch (ReflectiveOperationException e)
		{
			throw new UnreachableError(e);
		}
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
	static boolean abilityMatchesType(String abilityType, Ability aAbility, List<String> types, List<String> negate)
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
		if (!types.isEmpty())
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
	 * @param v         The ability Type to test
	 * @param aAbility  The ability
	 * @return true if it meets the visibility requirements
	 */
	static boolean abilityVisibleTo(View v, Ability aAbility)
	{
		return aAbility.getSafe(ObjectKey.VISIBILITY).isVisibleTo(v);
	}

	/**
	 * Helper method, returns true if the ability has the aspect we are matching on.
	 * 
	 * @param aspect The aspecte we're trying to match on
	 * @param aAbility The ability
	 * @return True if it matches the aspect else false
	 */
	static boolean abilityMatchesAspect(String aspect, Ability aAbility)
	{
		return (aspect == null) || (aAbility.get(MapKey.ASPECT, AspectName.getConstant(aspect)) != null);
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
	 * @param aMapToList
	 *            The list of abilities to get the ability from.
	 * @return The token value.
	 */
	private String getRetString(String tokenSource, PlayerCharacter pc, ExportHandler eh, int abilityIndex,
		MapToList<Ability, CNAbility> aMapToList)
	{
		String retString = "";
		Ability aAbility;
		List<Ability> aList = new ArrayList<>(aMapToList.getKeySet());
		// If the ability index given is within a valid range
		if (abilityIndex >= 0 && abilityIndex < aList.size())
		{
			aAbility = aList.get(abilityIndex);
			List<CNAbility> abilities = aMapToList.getListFor(aAbility);
			if (abilities.isEmpty())
			{
				return "";
			}

			// If it is the last item and there's a valid export handler and ??? TODO
			// Then tell the ExportHandler that there is no more processing needed
			if (abilityIndex == aList.size() - 1 && eh != null && eh.getExistsOnly())
			{
				eh.setNoMoreItems(true);
			}

			if (tokenSource.endsWith(".DESC"))
			{
				retString = pc.getDescription(abilities);
			}
			else if (tokenSource.endsWith(".BENEFIT"))
			{
				retString = BenefitFormatting.getBenefits(pc, abilities);
			}
			else if (tokenSource.endsWith(".TYPE"))
			{
				retString = aAbility.getType().toUpperCase();
			}
			else if (tokenSource.endsWith(".ASSOCIATED"))
			{
				List<String> assocs = new ArrayList<>();
				for (CNAbility cna : abilities)
				{
					assocs.addAll(pc.getAssociationExportList(cna));
				}
				Collections.sort(assocs);
				retString = StringUtil.join(assocs, ",");
			}
			else if (tokenSource.contains(".ASSOCIATED."))
			{
				final String key = tokenSource.substring(tokenSource.indexOf(".ASSOCIATED.") + 12);
				retString = getAssociationString(pc, abilities, key);
			}
			else if (tokenSource.endsWith(".ASSOCIATEDCOUNT"))
			{
				int count = 0;
				for (CNAbility cna : abilities)
				{
					count += pc.getDetailedAssociationCount(cna);
				}
				retString = Integer.toString(count);
			}
			else if (tokenSource.endsWith(".SOURCE"))
			{
				retString = SourceFormat.getFormattedString(aAbility, Globals.getSourceDisplay(), true);
			}
			else if (tokenSource.endsWith(".SOURCESHORT"))
			{
				retString = SourceFormat.formatShort(aAbility, 8);
			}
			else if (tokenSource.endsWith(".ASPECT"))
			{
				retString = getAspectString(pc, abilities);
			}
			else if (tokenSource.contains(".ASPECT."))
			{
				final String key = tokenSource.substring(tokenSource.indexOf(".ASPECT.") + 8);
				retString = getAspectString(pc, abilities, key);
			}
			else if (tokenSource.endsWith(".ASPECTCOUNT"))
			{
				retString = Integer.toString(aAbility.getSafeSizeOfMapFor(MapKey.ASPECT));
			}
			else if (tokenSource.contains(".HASASPECT."))
			{
				final String key = tokenSource.substring(tokenSource.indexOf(".HASASPECT.") + 11);
				retString = getHasAspectString(pc, aAbility, AspectName.getConstant(key));
			}
			else if (tokenSource.contains(".NAME"))
			{
				retString = aAbility.getDisplayName();
			}
			else if (tokenSource.contains(".KEY"))
			{
				retString = aAbility.getKeyName();
			}
			else
			{
				retString = QualifiedName.qualifiedName(pc, abilities);
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

	private String getAssociationString(PlayerCharacter pc, List<CNAbility> abilities, String key)
	{
		int index = Integer.parseInt(key);
		if (index < 0)
		{
			return Constants.EMPTY_STRING;
		}
		List<String> assocs = new ArrayList<>();
		for (CNAbility cna : abilities)
		{
			assocs.addAll(pc.getAssociationExportList(cna));
		}
		Collections.sort(assocs);
		int count = assocs.size();
		if (index < count)
		{
			return assocs.get(index);
		}
		//index was too large
		return Constants.EMPTY_STRING;
	}

	/**
	 * Gets the aspect string.
	 * 
	 * @param pc
	 *            The character being exported.
	 * @param abilities
	 *            The ability
	 * 
	 * @return the aspect string
	 */
	private String getAspectString(PlayerCharacter pc, List<CNAbility> abilities)
	{
		if (abilities.isEmpty())
		{
			return "";
		}
		Ability sampleAbilityObject = abilities.get(0).getAbility();
		Set<AspectName> aspectKeys = sampleAbilityObject.getKeysFor(MapKey.ASPECT);
		SortedSet<AspectName> sortedKeys = new TreeSet<>(aspectKeys);
		StringBuilder buff = new StringBuilder();
		for (AspectName key : sortedKeys)
		{
			if (buff.length() > 0)
			{
				buff.append(", ");
			}
			buff.append(Aspect.printAspect(pc, key, abilities));
		}
		return buff.toString();
	}

	/**
	 * Gets the aspect string for an aspect identified by position or name.
	 * 
	 * @param pc
	 *            The character being exported.
	 * @param abilities
	 *            The ability being queried.
	 * @param key
	 *            The key (number or name) of the aspect to retrieve
	 * 
	 * @return the aspect string
	 */
	private String getAspectString(PlayerCharacter pc, List<CNAbility> abilities, String key)
	{
		if (key == null)
		{
			return "";
		}
		if (abilities.isEmpty())
		{
			return "";
		}
		Ability sampleAbilityObject = abilities.get(0).getAbility();

		try
		{
			int index = Integer.parseInt(key);
			if ((index >= 0) && (index < sampleAbilityObject.getSafeSizeOfMapFor(MapKey.ASPECT)))
			{
				Set<AspectName> aspectKeys = sampleAbilityObject.getKeysFor(MapKey.ASPECT);
				List<AspectName> sortedKeys = new ArrayList<>(aspectKeys);
				Collections.sort(sortedKeys);
				AspectName aspectName = sortedKeys.get(index);
				return Aspect.printAspect(pc, aspectName, abilities);
			}
			else
			{
				return "";
			}
		}
		catch (NumberFormatException e)
		{
			// Ignore exception - expect this as we can get a String at this point
			AspectName aspectName = AspectName.getConstant(key);
			return Aspect.printAspectValue(pc, aspectName, abilities);
		}
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
	private String getHasAspectString(PlayerCharacter pc, Ability ability, AspectName key)
	{
		List<Aspect> aspects = ability.get(MapKey.ASPECT, key);
		Aspect aspect = Aspect.lastPassingAspect(aspects, pc, ability);
		if (aspect == null)
		{
			return "N";
		}
		return "Y";
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
	protected MapToList<Ability, CNAbility> getAbilityList(PlayerCharacter pc, final AbilityCategory aCategory)
	{
		final MapToList<Ability, CNAbility> listOfAbilities = new HashMapToList<>();
		Collection<AbilityCategory> allCats = SettingsHandler.getGameAsProperty().get().getAllAbilityCategories();
		for (AbilityCategory aCat : allCats)
		{
			if (AbilityCategory.ANY.equals(aCategory) || aCat.getParentCategory().equals(aCategory))
			{
				for (CNAbility cna : pc.getPoolAbilities(aCat, Nature.NORMAL))
				{
					listOfAbilities.addToListFor(cna.getAbility(), cna);
				}
			}
		}
		return listOfAbilities;
	}

	/**
	 * @param v the view to set
	 */
	protected void setView(View v)
	{
		this.view = v;
	}

}
