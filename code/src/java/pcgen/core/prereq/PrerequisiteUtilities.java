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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.ChooseInformation;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.Domain;
import pcgen.core.Equipment;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.core.Skill;
import pcgen.core.WeaponProf;
import pcgen.core.spell.Spell;

/**
 * @author Tom Parker <thpr@sourceforge.net>
 *
 * This is a utility class related to PreReq objects.
 */
public final class PrerequisiteUtilities
{
	/**
	 * Private Constructor, prevents instantiation.
	 */
	private PrerequisiteUtilities()
	{
		// Don't allow instantiation of utility class
	}

	/**
	 * Tests a list of prerequisites against a given PC and a given Source.  It then
	 * generates an HTML representation of whether they passed.
	 * @param aPC The PC to test the prerequisites against.
	 * @param aObj The source of the PreRequisite.
	 * @param aList A list of prerequisite objects.
	 * @param includeHeader Whether to wrap the generated string in html tags.
	 * @return An HTML representation of whether a set of PreRequisites passed for a given PC and Source.
	 */
	public static String preReqHTMLStringsForList(
		final PlayerCharacter aPC,
		final CDOMObject aObj,
		final Collection<Prerequisite> aList,
		final boolean includeHeader)
	{
		if ((aList == null) || aList.isEmpty())
		{
			return "";
		}

		final StringBuffer pString = new StringBuffer(aList.size() * 20);

		final List<Prerequisite> newList = new ArrayList<Prerequisite>();
		boolean first = true;

		for (Prerequisite prereq : aList)
		{
			newList.clear();

			newList.add(prereq);

			if (first)
			{
				first = false;
			}
			else
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
	 * Check if the character passes the ability prerequisite. Refactored here 
	 * for use by both PREFEAT and PREABILITY.
	 *
	 * @param prereq The prerequisite to be run.
	 * @param character The character to be checked.
	 * @param numMatches The number of matches required.
	 * @param categoryName The name of the required category, null if any category will be matched.
	 * @return The number of matches made, 0 if not enough matches were made.
	 */
	public static int passesAbilityTest(
		final Prerequisite prereq,
		final PlayerCharacter character,
		final int numMatches,
		String categoryName)
	{

		final boolean countMults = prereq.isCountMultiples();

		final boolean keyIsAny  = prereq.getKey().equalsIgnoreCase(Constants.LST_ANY);
		final boolean keyIsType = isTypeTest(prereq.getKey());

		final String strippedKey = keyIsType
			? prereq.getKey().substring(Constants.SUBSTRING_LENGTH_FIVE)
			: prereq.getKey();

		int runningTotal = 0;

		final List<Ability> abilityList = buildAbilityList(character, categoryName);

		if (!abilityList.isEmpty())
		{
			for (Ability ability : abilityList)
			{
				final String abilityKey = ability.getKeyName();

				if (keyIsAny
					|| (!keyIsType && abilityKey.equalsIgnoreCase(strippedKey))
					|| (keyIsType && ability.isType(strippedKey)))
				{
					// either this feat has matched on the name, or the type

					if (prereq.getSubKey() != null)
					{
						runningTotal += dealWithSubKey(
							prereq,
							character,
							strippedKey,
							ability);
					}
					else
					{
						// subKey == null

						runningTotal++;
						if (ability.getSafe(ObjectKey.MULTIPLE_ALLOWED) && countMults)
						{

							/*
							 * SERVESAS occurrences might mean this is less than zero,
							 * in which case ignore it. This still leaves the instance
							 * where more than one of an item is desired, and one
							 * instance is a SERVESAS, but that is a high cost corner case.
							 */

							final int num =
							   character.getSelectCorrectedAssociationCount(ability) - 1;
							if (num > 0)
							{
								runningTotal += num;
							}
						}
					}
				}
				else
				{

					if (prereq.getSubKey() != null)
					{
						final int len = Constants.SUBSTRING_LENGTH_FIVE;

						final boolean subKeyIsType = isTypeTest(prereq.getSubKey());
						final String subKey = subKeyIsType
							? prereq.getSubKey().substring(len)
							: prereq.getSubKey();

						final String s1 = strippedKey + " (" + subKey + ")";
						final String s2 = strippedKey + "(" + subKey + ")";
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
	 * This operation deals with matching subKeys against abilities where the the main key
	 * has already matched.

	 * The subKey may be prefixed with TYPE(=|.) in which case the Choice string from the
	 * ability will be used to check for the type of chooser.  Possibilities are SKILL,
	 * WEAPONPROFICIENCY, DOMAIN, or SPELL.  A list of keys will be retrieved from the ability's
	 * associated object list.  The objects matching these keys are retrieved and checked
	 * for type against subKey.  A count is returned (respects countMults).
	 *
	 * If the subKey is not specifying a type, then the key of the prerequisite is checked
	 * against the key of the ability, if they match and the ability object is associated
	 * with the character via the subkey, then a count of the number of instances is returned.
	 *
	 * Finally,the subkey may specify a wildcard.  If it does, the list of associations
	 * between the ability object and the PC are checked. A count is returned of teh number
	 * that begin with the wildcard string.
	 *
	 * @param prereq The prerequisite to be checked.
	 * @param character  The character to be checked.
	 * @param key the Key from the prerequisite which has been stripped the prefixes TYPE= abd TYPE.
	 * @param ability The ability being checked for a match.
	 * @return A count which respects countMults.
	 */
	private static int dealWithSubKey(
		Prerequisite prereq,
		PlayerCharacter character,
		String key,
		Ability ability)
	{
		final boolean countMults = prereq.isCountMultiples();
		int runningTotal = 0;

		final String  subKey = prereq.getSubKey();
		final boolean subKeyIsType = isTypeTest(subKey);

		final int wildCardPos = subKey.indexOf('%');

		if (subKeyIsType)
		{
			final String type = prereq.getSubKey().substring(Constants.SUBSTRING_LENGTH_FIVE);
			runningTotal = countSubKeyType(character, ability, type, countMults);
		}

		else if (ability.getKeyName().equalsIgnoreCase(key)
			&& character.containsAssociated(ability, subKey))
		{

			if (countMults && ability.getSafe(ObjectKey.MULTIPLE_ALLOWED))
			{
				runningTotal = character.getSelectCorrectedAssociationCount(ability);
			}
			else
			{
				runningTotal = 1;
			}
		}

		else if (wildCardPos > -1)
		{
			String preWildcard = (wildCardPos == 0)
				? Constants.EMPTY_STRING
				: subKey.substring(0, wildCardPos).toUpperCase();
			runningTotal = countSubKeyWildcardMatch(
				character,
				countMults,
				preWildcard,
				ability);
		}

		return runningTotal;
	}

	/**
	 * Is this string a Type selector?
	 * @param key the string to test
	 * @return true if key begins with TYPE= or TYPE.
	 */
	private static boolean isTypeTest(String key)
	{
		return key.startsWith(Constants.LST_TYPE_EQUAL) || key.startsWith(Constants.LST_TYPE_DOT);
	}

	/**
	 * Having matched the ability on the other criteria, check for a match 
	 * against the subKey.
	 *
	 * @param character
	 *              The character being tested.
	 * @param countMults
	 *              Should multiple occurrences be counted?
	 * @param preWilcard
	 *              The portion of the prerequisite's subkey that appears
	 *              before the wilcard character '%'
	 * @param ability
	 *              The ability being checked for a match.
	 * @return The number of matches made
	 */
	private static int countSubKeyWildcardMatch(
		final PlayerCharacter character,
		final boolean countMults,
		String preWilcard,
		Ability ability)
	{

		int runningTotal = 0;

		for (String assoc : character.getAssociationList(ability))
		{
			final String fString = assoc.toUpperCase();

			if (preWilcard.length() == 0 || fString.startsWith(preWilcard))
			{
				runningTotal++;
				if (!countMults)
				{
					break;
				}
			}
		}
		return runningTotal;
	}

	private static int countSubKeyType(
		PlayerCharacter aPC,
		Ability ability, String type, boolean countMults)
	{
		final List<String> selectedList = aPC.getAssociationList(ability);

		ChooseInformation<?> chooseInformation = ability.getSafe(ObjectKey.CHOOSE_INFO);
		final String aChoiceString = chooseInformation.getName();
		
		if (aChoiceString.startsWith("SKILL")) //$NON-NLS-1$
		{
			return subKeySkill(countMults, type, selectedList);
		}
		else if (aChoiceString.startsWith("WEAPONPROFICIENCY")) //$NON-NLS-1$
		{
			return subKeyWeaponProf(countMults, type, selectedList);
		}
		else if (aChoiceString.startsWith("DOMAIN")) //$NON-NLS-1$
		{
			return subKeyDomain(countMults, type, selectedList);
		}
		else if (aChoiceString.startsWith("SPELL")) //$NON-NLS-1$
		{
			return subKeySpell(countMults, type, selectedList);
		}

		return 0;
	}

	/**
	 * Build up a list of the character's abilities which match the category requirements.
	 *
	 * @param character The character to be tested.
	 * @param categoryName The name of the required category, null if any category will be matched.
	 * @return A list of categories matching.
	 */
	private static List<Ability> buildAbilityList(
		final PlayerCharacter character,
		String categoryName)
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
					if (aCat.getParentCategory().getKeyName().equalsIgnoreCase(categoryName))
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
					for(CDOMReference<Ability> ref
						: ability.getSafeListFor(ListKey.SERVES_AS_ABILITY))
					{
						for (Ability ab : ref.getContainedObjects())
						{
							if (categoryName == null
								|| categoryName.equals(ab.getCategory()))
							{
								abilityList.add(ab);
							}
						}
					}
				}
			}
		}
		return abilityList;
	}

	/**
	 * Count the number of spells associated with the ability being tested of types cType.
	 *
	 * @param countMults Should multiple occurrences be counted?
	 * @param cType The type to check for.
	 * @param selectedList The list of spells associated with the ability being tested.
	 * @return int
	 */
	private static int subKeySpell(
		final boolean countMults,
		final String cType,
		final List<String> selectedList)
	{
		int returnTotal = 0;
		for (String spell : selectedList)
		{
			final Spell sp = Globals.getSpellKeyed(spell);

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
	 * Count the number of domains associated with the ability being tested of types cType.
	 *
	 * @param countMults Should multiple occurrences be counted?
	 * @param cType The type to check for.
	 * @param selectedList The list of domains associated with the ability being tested.
	 * @return int
	 */
	private static int subKeyDomain(
		final boolean countMults,
		final String cType,
		final List<String> selectedList)
	{
		int returnTotal = 0;

		for (String domain : selectedList)
		{
			final Domain dom;
			dom = Globals.getContext().ref.silentlyGetConstructedCDOMObject(Domain.class, domain);
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
	 * Count the number of weaponprofs associated with the ability being tested of types cType.
	 *
	 * @param countMults Should multiple occurrences be counted?
	 * @param cType The type to check for.
	 * @param selectedList The list of weaponprofs associated with the ability being tested.
	 * @return int
	 */
	private static int subKeyWeaponProf(
		final boolean countMults,
		final String cType,
		final List<String> selectedList)
	{
		int returnTotal = 0;

		for (String weaponprof : selectedList)
		{
			final WeaponProf wp = Globals.getContext().ref.silentlyGetConstructedCDOMObject(
				WeaponProf.class, weaponprof);

			if (wp == null)
			{
				continue;
			}

			if (wp.isType(cType))
			{
				returnTotal++;
				if (!countMults)
				{
					break;
				}
				continue;
			}

			final Equipment eq = Globals.getContext().ref.silentlyGetConstructedCDOMObject(
				Equipment.class, wp.getKeyName());

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
	 * Count the number of skills associated with the ability being tested of types cType.
	 *
	 * @param countMults Should multiple occurrences be counted?
	 * @param cType The type to check for.
	 * @param selectedList The list of skills associated with the ability being tested.
	 * @return int
	 */
	private static int subKeySkill(
		final boolean countMults,
		final String cType,
		final List<String> selectedList)
	{
		int returnTotal = 0;

		for (String skill : selectedList)
		{
			final Skill sk;
			sk = Globals.getContext().ref.silentlyGetConstructedCDOMObject(Skill.class, skill);
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
	 * @return true if we got as match.
	 */
	public static boolean hasPreReqKindOf(final Prerequisite prereq, String matchKind)
	{
		if (prereq == null)
		{
			return false;
		}

		if (matchKind == prereq.getKind()
				|| matchKind.equalsIgnoreCase(prereq.getKind()))
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
	 * descendant of the required kind.  Kind is either FEAT or ABILITY, the
	 * key is the name of an ability object.
	 *
	 * @param prereq Prerequisite to be checked
	 * @param matchKind Kind to be checked for.
	 * @param matchKey The name of an ability object.
	 * @return true if we got a match
	 */
	public static boolean hasPreReqMatching(
		final Prerequisite prereq,
		String matchKind,
		String matchKey)
	{
		if (prereq == null)
		{
			return false;
		}

		if ((matchKind == prereq.getKind())
			|| (matchKind.equalsIgnoreCase(prereq.getKind())))
		{
			if ((matchKey == prereq.getKey())
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
