/*
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
 * Copyright 2005 (C) Tom Parker <thpr@sourceforge.net>
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
 * Refactored out of PObject July 22, 2005
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 */
package pcgen.core.prereq;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.Domain;
import pcgen.core.Equipment;
import pcgen.core.EquipmentList;
import pcgen.core.Globals;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.core.Skill;
import pcgen.core.WeaponProf;
import pcgen.core.spell.Spell;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.output.prereq.PrerequisiteWriter;
import pcgen.util.Logging;

/**
 * @author Tom Parker <thpr@sourceforge.net>
 *
 * This is a utility class related to PreReq objects.
 */
public final class PrerequisiteUtilities
{
	/**
	 * Private Constructor
	 */
	private PrerequisiteUtilities()
	{
		// Don't allow instantiation of utility class
	}

	public static final String preReqHTMLStringsForList(final PlayerCharacter aPC, final PObject aObj, final Collection<Prerequisite> aList, final boolean includeHeader)
	{
		if ((aList == null) || aList.isEmpty())
		{
			return "";
		}

		final StringBuffer pString = new StringBuffer(aList.size() * 20);

		final List<Prerequisite> newList = new ArrayList<Prerequisite>();
		int iter = 0;

		for ( Prerequisite prereq : aList )
		{
			newList.clear();

			newList.add(prereq);

			if (iter++ > 0)
			{
				pString.append(" and ");
			}

			final String bString = PrereqHandler.toHtmlString(newList);

			final boolean flag;


			if (aObj instanceof Equipment)
			{
				flag = PrereqHandler.passesAll(newList, (Equipment) aObj, aPC);
			}
			else
			{
				flag = PrereqHandler.passesAll(newList, aPC, null);
			}

			if (!flag)
			{
				pString.append(SettingsHandler.getPrereqFailColorAsHtmlStart());
				pString.append("<i>");
			}

			final StringTokenizer aTok = new StringTokenizer(bString, "&<>", true);

			while (aTok.hasMoreTokens())
			{
				final String aString = aTok.nextToken();

				if (aString.equals("<"))
				{
					pString.append("&lt;");
				}
				else if (aString.equals(">"))
				{
					pString.append("&gt;");
				}
				else if (aString.equals("&"))
				{
					pString.append("&amp;");
				}
				else
				{
					pString.append(aString);
				}
			}

			if (!flag)
			{
				pString.append("</i>");
				pString.append(SettingsHandler.getPrereqFailColorAsHtmlEnd());
			}
		}

		if (pString.toString().indexOf('<') >= 0)
		{
			// seems that ALIGN and STAT have problems in
			// HTML display, so wrapping in <font> tag.
			pString.insert(0, "<font>");
			pString.append("</font>");

			if (includeHeader)
			{
				if (pString.toString().indexOf('<') >= 0)
				{
					pString.insert(0, "<html>");
					pString.append("</html>");
				}
			}
		}

		return pString.toString();
	}


	/**
	 * Build the LST syntax to represent the list of prerequisites.
	 * 
	 * @param preReqs The list of prerequisites.
	 * @param separator The character to separate each prereq from each other.
	 */
	public static String getPrerequisitePCCText(final List preReqs, final String separator)
	{
		final StringBuffer sBuff = new StringBuffer();
		if ((preReqs != null) && (preReqs.size() > 0)) {
			final StringWriter writer = new StringWriter();
			final PrerequisiteWriter preReqWriter = new PrerequisiteWriter();
			for (Iterator preReqIter = preReqs.iterator(); preReqIter.hasNext();) {
				final Prerequisite preReq = (Prerequisite) preReqIter.next();
				try {
					preReqWriter.write(writer, preReq);
				} catch (PersistenceLayerException e) {
					Logging.errorPrint("Failed to encode prereq: ", e);
				}
				if (preReqIter.hasNext()) {
					writer.write(separator);
				}
			}
			sBuff.append(separator);
			sBuff.append(writer.toString());
		}
		return sBuff.toString();
	}
	

	/**
	 * Check if the character passes the ability prerequisite. Refactored here 
	 * for use by both PREFEAT and PREABILITY.
	 *  
	 * @param prereq The prerequisite to be run.
	 * @param character The character to be checked.
	 * @param countMults Should multiple occurrences be counted.
	 * @param numMatches The number of matches required.
	 * @param key The key that needs to be matched 
	 * @param subKey The sub key that needs to be matched.
	 * @param categoryName The name of the required category, null if any category will be matched.
	 * @param category The category to be matched
	 * @return The number of matches made, 0 if not enough matches were made.
	 */
	public static int passesAbilityTest(final Prerequisite prereq,
		final PlayerCharacter character, final boolean countMults,
		final int numMatches, String key, String subKey, String categoryName,
		AbilityCategory category)
	{
		final boolean keyIsAny =
			key.equalsIgnoreCase("ANY"); //$NON-NLS-1$
		final boolean keyIsType =
				key.startsWith("TYPE=") || key.startsWith("TYPE."); //$NON-NLS-1$ //$NON-NLS-2$
		final boolean subKeyIsType =
				subKey != null
					&& (subKey.startsWith("TYPE=") || subKey.startsWith("TYPE.")); //$NON-NLS-1$ //$NON-NLS-2$
		if (keyIsType)
		{
			key = key.substring(5);
		}
		if (subKeyIsType)
		{
			subKey = subKey.substring(5);
		}

		int runningTotal = 0;
		final List<Ability> abilityList =
				buildAbilityList(character, categoryName, category);
		if (!abilityList.isEmpty())
		{
			for (Ability ability : abilityList)
			{
				final String abilityKey = ability.getKeyName();
				if (keyIsAny || (!keyIsType && abilityKey.equalsIgnoreCase(key))
					|| (keyIsType && ability.isType(key)))
				{
					// either this feat has matched on the name, or the type

					if (subKey != null)
					{
						runningTotal +=
								checkForSubKeyMatch(character, countMults, key,
									subKey, subKeyIsType, ability);
					}
					else
					{
						// Subkey == null

						runningTotal++;
						if (ability.isMultiples() && countMults)
						{
							runningTotal += (ability.getAssociatedCount() - 1);
						}
					}
				}
				else
				{
					if (subKey != null)
					{
						final String s1 = key + " (" + subKey + ")";
						final String s2 = key + "(" + subKey + ")";
						if (abilityKey.equalsIgnoreCase(s1)
							|| ability.getKeyName().equalsIgnoreCase(s2))
						{
							runningTotal++;
							if (!countMults)
							{
								break;
							}
						}
					}
				}
			}
		}

		runningTotal = prereq.getOperator().compare(runningTotal, numMatches);
		return runningTotal;
	}

	/**
	 * Having matched the ability on the other criteria, check for a match 
	 * against the sub-key.
	 * 
	 * @param character The character being tested.
	 * @param countMults Should multiple occurrences be counted?
	 * @param key The key that needs to be matched 
	 * @param subKey The sub key that needs to be matched.
	 * @param subKeyIsType Does the subkey refer to a type?
	 * @param aFeat The ability being checked for a match.
	 * @return The number of matches made
	 */
	private static int checkForSubKeyMatch(final PlayerCharacter character, final boolean countMults, String key, String subKey, final boolean subKeyIsType, Ability aFeat)
	{
		final String cType = subKey;
		final List availableList = new ArrayList();
		final List selectedList = new ArrayList();
		final String aChoiceString = aFeat.getChoiceString();
		int runningTotal = 0;

		aFeat.modChoices(availableList, selectedList, false,
			character, true, null);
		availableList.clear();

		if (subKeyIsType) // TYPE syntax
		{
			if (aChoiceString.startsWith("SKILL")) //$NON-NLS-1$
			{
				runningTotal =
						subKeySkill(countMults, runningTotal,
							cType, selectedList);
			}
			else if (aChoiceString.startsWith("WEAPONPROFS")) //$NON-NLS-1$
			{
				runningTotal =
						subKeyWeaponProf(countMults,
							runningTotal, cType, selectedList);
			}
			else if (aChoiceString.startsWith("DOMAIN")) //$NON-NLS-1$
			{
				runningTotal =
						subKeyDomain(countMults, runningTotal,
							cType, selectedList);
			}
			else if (aChoiceString.startsWith("SPELL")) //$NON-NLS-1$
			{
				runningTotal =
						subKeySpell(countMults, runningTotal,
							cType, selectedList);
			}
			// End. subKeyIsType
		}
		else
		{
			if (aFeat.getKeyName().equalsIgnoreCase(key)
				&& aFeat.containsAssociated(subKey))
			{
				runningTotal++;
				if (aFeat.isMultiples() && countMults)
				{
					runningTotal +=
							(aFeat.getAssociatedCount() - 1);
				}
			}
			else
			{
				final int wildCardPos = subKey.indexOf('%');

				if (wildCardPos > -1)
				{
					for (int k = 0; k < aFeat
						.getAssociatedCount(); ++k)
					{

						final String fString =
								aFeat.getAssociated(k)
									.toUpperCase();
						if (wildCardPos == 0
							|| fString.startsWith(subKey
								.substring(0, wildCardPos - 1)
								.toUpperCase()))
						{
							runningTotal++;
							if (!countMults)
							{
								break;
							}
						}
					}
				}
			}
		}
		return runningTotal;
	}

	/**
	 * Build up a list of the character's abilities which match the category requirements.
	 * @param character The character to be tested.
	 * @param categoryName The name of the required category, null if any category will be matched.
	 * @param category The category to be matched
	 * @return A list of categories matching.
	 */
	private static List<Ability> buildAbilityList(final PlayerCharacter character, String categoryName, AbilityCategory category)
	{
		final List<Ability> abilityList = new ArrayList<Ability>();
		if (character != null)
		{
			Collection<AbilityCategory> allCats = SettingsHandler.getGame().getAllAbilityCategories();
			if (categoryName == null)
			{
				for (AbilityCategory aCat : allCats)
				{
					abilityList.addAll(character.getAggregateAbilityList(aCat));
				}
			}
			else
			{
				for (AbilityCategory aCat : allCats)
				{
					if (aCat.getAbilityCategory().equalsIgnoreCase(categoryName))
					{
						abilityList.addAll(character.getAggregateAbilityList(aCat));
					}
				}
			}
			
			// Now scan for relevant SERVESAS occurrences
			for (AbilityCategory aCat : allCats)
			{
				for (Ability ability : character.getAggregateAbilityList(aCat))
				{
					final Map<String, List<String>> servesAsMap = ability.getServesAs();
					for (String cat : servesAsMap.keySet())
					{
						if (categoryName == null || categoryName.equals(cat))
						{
							for (String abilityKey : servesAsMap.get(cat))
							{
								AbilityCategory saCat =
										SettingsHandler.getGame()
											.getAbilityCategory(cat);
								Ability saAbility =
										Globals.getAbilityKeyed(saCat, abilityKey);
								if (saAbility != null)
								{
									abilityList.add(saAbility);
								}
							}
						}
					}
				}
			}
		}
		return abilityList;
	}

	/**
	 * @param countMults
	 * @param runningTotal
	 * @param cType
	 * @param selectedList
	 * @return int
	 */
	private static int subKeySpell(final boolean countMults, int runningTotal,
		final String cType, final List selectedList)
	{
		int returnTotal = runningTotal;
		for (Object aObj : selectedList)
		{
			final Spell sp;
			String spellKey = null;
			if (aObj instanceof PObject)
			{
				spellKey = ((PObject) aObj).getKeyName();
			}
			else
			{
				spellKey = aObj.toString();
			}
			sp = Globals.getSpellKeyed(spellKey);
			if (sp == null)
			{
				continue;
			}
			if (sp.isType(cType))
			{
				returnTotal++;
				if (!countMults)
				{
					break;
				}
			}
		}
		return returnTotal;
	}

	/**
	 * @param countMults
	 * @param runningTotal
	 * @param cType
	 * @param selectedList
	 * @return int
	 */
	private static int subKeyDomain(final boolean countMults, int runningTotal,
		final String cType, final List selectedList)
	{
		int returnTotal = runningTotal;
		for (Object aObj : selectedList)
		{
			final Domain dom;
			dom = Globals.getDomainKeyed(aObj.toString());
			if (dom == null)
			{
				continue;
			}
			if (dom.isType(cType))
			{
				returnTotal++;
				if (!countMults)
				{
					break;
				}
			}
		}
		return returnTotal;
	}

	/**
	 * @param countMults
	 * @param runningTotal
	 * @param cType
	 * @param selectedList
	 * @return int
	 */
	private static int subKeyWeaponProf(final boolean countMults, int runningTotal,
		final String cType, final List selectedList)
	{
		int returnTotal = runningTotal;
		for (Object aObj : selectedList)
		{
			final WeaponProf wp;
			wp = Globals.getWeaponProfKeyed(aObj.toString());
			if (wp == null)
			{
				continue;
			}
			final Equipment eq;
			eq = EquipmentList.getEquipmentKeyed(wp.getKeyName());
			if (eq == null)
			{
				continue;
			}
			if (eq.isType(cType))
			{
				returnTotal++;
				if (!countMults)
				{
					break;
				}
			}
		}
		return returnTotal;
	}

	/**
	 * @param countMults
	 * @param runningTotal
	 * @param cType
	 * @param selectedList
	 * @return int
	 */
	private static int subKeySkill(final boolean countMults, int runningTotal,
		final String cType, final List selectedList)
	{
		int returnTotal = runningTotal;
		for (Object aObj : selectedList)
		{
			final Skill sk;
			sk = Globals.getSkillKeyed(aObj.toString());
			if (sk == null)
			{
				continue;
			}
			if (sk.isType(cType))
			{
				returnTotal++;
				if (!countMults)
				{
					break;
				}
			}
		}
		return returnTotal;
	}

	/**
	 * Identify if the prerequisite is itself of the supplied kind or has a 
	 * descendant of the required kind.
	 * @param prereq Prerequisite to be checked
	 * @param matchKind Kind to be checked for.
	 * @return
	 */
	public static final boolean hasPreReqKindOf(final Prerequisite prereq, String matchKind)
	{
		if (prereq == null)
		{
			return false;
		}

		if (matchKind == null && prereq.getKind() == null)
		{
			return true;
		}
		if (matchKind.equalsIgnoreCase(prereq.getKind()))
		{
			return true;
		}
		
		for (Prerequisite childPrereq : prereq.getPrerequisites())
		{
			if (hasPreReqKindOf(childPrereq, matchKind))
			{
				return true;
			}
		}
	
		return false;
	}

	/**
	 * Identify if the prerequisite is itself of the supplied kind or has a 
	 * descendant of the required kind.
	 * @param prereq Prerequisite to be checked
	 * @param matchKind Kind to be checked for.
	 * @return
	 */
	public static final boolean hasPreReqMatching(final Prerequisite prereq, String matchKind, String matchKey)
	{
		if (prereq == null)
		{
			return false;
		}

		if ((matchKind == null && prereq.getKind() == null)
			|| (matchKind.equalsIgnoreCase(prereq.getKind())))
		{
			if ((matchKey == null && prereq.getKey() == null)
				|| (matchKey.equalsIgnoreCase(prereq.getKey())))
			{
				return true;
			}
		}
		
		for (Prerequisite childPrereq : prereq.getPrerequisites())
		{
			if (hasPreReqMatching(childPrereq, matchKind, matchKey))
			{
				return true;
			}
		}
	
		return false;
	}
	
}
