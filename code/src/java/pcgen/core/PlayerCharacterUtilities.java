/*
 * PlayerCharacterUtilities.java
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
 * Current Version: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 * Copyright 2005 Andrew Wilson <nuance@sourceforge.net>
 * Class created by migrating code from PlayerCharacter
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
 */
package pcgen.core;

import pcgen.core.pclevelinfo.PCLevelInfo;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.util.Logging;

import java.util.List;
import java.util.StringTokenizer;

/**
 * Utilities class for PlayerCharacter.  Holds various static methods.
 *
 * @author  Andrew Wilson <nuance@sourceforge.net>
 */
public class PlayerCharacterUtilities
{
	/**
	 * Adds a String to a name, for example, adding "Longsword" to "Weapon
	 * Specialisation" gives "Weapon Specialisation (Longsword)"
	 *
	 * @param   aName    The Name to add to
	 * @param   aString  The string to add
	 *
	 * @return  The modified name
	 */
	static String appendToName(final String aName, final String aString)
	{
		final StringBuffer aBuf = new StringBuffer(aName);
		final int          iLen = aBuf.length() - 1;

		if (aBuf.charAt(iLen) == ')')
		{
			aBuf.setCharAt(iLen, '/');
		}
		else
		{
			aBuf.append(" (");
		}

		aBuf.append(aString);
		aBuf.append(')');

		return aBuf.toString();
	}

	/**
	 * Convert the name of an Ability (currently only handles FEATS) into an
	 * Ability object and add it to theFeatList TODO expand this routine so that
	 * it can handle more than feats.  This may involve changes where it is
	 * called so that they pass a different list depending on the category of
	 * Ability object, but they would need to pass the category as well and it
	 * may be simpler to pass AbilityInfo objects instead.
	 *
	 * @param  theAbilityList  A list of abilities to add to
	 * @param  abilityName     The name of the Ability to Add
	 */
	static void addToFeatList(final List theAbilityList, final String abilityName)
	{
		String altName = "";
		String subName = "";

		if (abilityName.endsWith(")"))
		{
			// we want what is inside the outermost parens.
			subName = abilityName.substring(
				    abilityName.indexOf('(') + 1,
				    abilityName.lastIndexOf(')'));
			altName = abilityName.substring(0, abilityName.indexOf('(')).trim();
		}

		Ability anAbility = AbilityUtilities.getFeatNamedInList(
			    theAbilityList,
			    abilityName);

		if ((anAbility == null) && (altName.length() != 0))
		{
			anAbility = AbilityUtilities.getFeatNamedInList(theAbilityList, altName);
		}

		/* This feat is not in autoFeatList, get the global definition, clone it, attach
		 * sub-type (if any) and add */

		if (anAbility == null)
		{
			anAbility = Globals.getAbilityNamed("FEAT", abilityName);

			if ((anAbility == null) && (altName.length() != 0))
			{
				anAbility = Globals.getAbilityNamed("FEAT", altName);
			}

			if (anAbility != null)
			{
				anAbility = (Ability) anAbility.clone();

				if (subName.length() != 0)
				{
					anAbility.addAssociated(subName);
				}

				anAbility.setFeatType(Ability.ABILITY_AUTOMATIC);
				theAbilityList.add(anAbility);
			}
			else
			{
				ShowMessageDelegate.showMessageDialog(
				    "Adding unknown feat: " + abilityName,
				    Constants.s_APPNAME,
				    MessageType.INFORMATION);
			}
		}

		/* Already have feat, add sub-type (if any) */

		else
		{
			if (subName.length() != 0)
			{
				if (
				    anAbility.isStacks() ||
				    (anAbility.isMultiples() &&
				        !anAbility.containsAssociated(subName)))
				{
					anAbility.addAssociated(subName);
				}
			}
		}
	}

	/**
	 * Bryan wanted this to be optional, but if you can reassign racial auto
	 * feats, when you reopen the character, you get the feats that were
	 * exchanged back
	 *
	 * @return  false
	 */
	static boolean canReassignRacialFeats()
	{
		return false;
	}

	/**
	 * Bryan wanted this to be optional, but if you can reassign racial auto
	 * feats, when you reopen the character, you get the feats that were
	 * exchanged back
	 *
	 * @return  false
	 */
	static boolean canReassignTemplateFeats()
	{
		return false;
	}

	/**
	 * Picks the biggest die size from two strings in the form V|WdX, YdZ (where
	 * the WdX represents W X sided dice).  If Z is larger than X, returns
	 * V|YdZ, otherwise it returns V|WdX
	 *
	 * @param   oldString  2|1d3
	 * @param   newString  1d4
	 *
	 * @return  in the example parameters given, will return 2|1d4 (because the
	 *          4 is bigger than the 3). If the last figure in the new string
	 *          isn't larger, it returns the original string.
	 */
	static String getBestUDamString(final String oldString, final String newString)
	{
		if ((newString == null) || (newString.length() < 2))
		{
			return oldString;
		}

		StringTokenizer aTok      = new StringTokenizer(oldString, "|");
		int             sides     = Integer.parseInt(aTok.nextToken());
		String          retString = oldString;

		aTok = new StringTokenizer(newString, " dD+-(x)");

		if (aTok.countTokens() > 1)
		{
			aTok.nextToken();

			final int i = Integer.parseInt(aTok.nextToken());

			if (sides < i)
			{
				sides     = i;
				retString = sides + "|" + newString;
			}
		}

		return retString;
	}

	/**
	 * Returns the number of experience points needed for level
	 *
	 * @param   level  character level to calculate experience for
	 * @param   pc     the PC that we are asking about (ECL of character can
	 *                 affect the result)
	 *
	 * @return  The experience points needed
	 */
	static int minXPForLevel(final int level, final PlayerCharacter pc)
	{
		LevelInfo lInfo = (LevelInfo) Globals.getLevelInfo().get(String.valueOf(level));

		if (lInfo == null)
		{
			lInfo = (LevelInfo) Globals.getLevelInfo().get("LEVEL");
		}

		if ((level > 0) && (lInfo != null))
		{
			return lInfo.getMinXP(level, pc);
		}
		// do something sensible if no level info
		return 0;
	}

	/**
	 * Set the Weapon proficiency of one piece of Equipment to the same as the
	 * Proficiency in another piece of Equipment.  For some bizarre reason, as
	 * well as setting the roficiency,  this zeros out the Weight and cost of
	 * the equipment.
	 *
	 * @param  equip  the Weapon to get the proficiency from
	 * @param  eqm    the weapon to set the preoficiency in
	 */
	static void setProf(final Equipment equip, final Equipment eqm)
	{
		// Make sure the proficiency is set
		String profName = equip.rawProfName();

		if (profName.length() == 0)
		{
			profName = equip.getName();
		}

		eqm.setProfName(profName);

		// In case this is used somewhere it shouldn't be used,
		// set weight and cost to 0
		eqm.setWeight("0");
		eqm.setCost("0");
	}

	/**
	 * Add multiple feats from a String list separated by commas.
	 * @param playerCharacterLevelInfo
	 * @param aList
	 * @param addIt
	 * @param all
	 */
	static void modFeatsFromList(final PlayerCharacter aPC,
			                     final PCLevelInfo     playerCharacterLevelInfo,
			                     final String          aList,
			                     final boolean         addIt,
			                     final boolean         all)
	{
		final StringTokenizer aTok = new StringTokenizer(aList, ",");
	
		while (aTok.hasMoreTokens())
		{
			String aString = aTok.nextToken();
			Ability anAbility = aPC.getFeatNamed(aString);
			StringTokenizer bTok = null;
	
			if (anAbility != null)
			{
				continue;
			}
	
			// does not already have feat
			anAbility = Globals.getAbilityNamed("FEAT", aString);
	
			if (anAbility == null)
			{
				// could not find Feat, try trimming off contents of parenthesis
				bTok = new StringTokenizer(aString, "()", true);
	
				final String bString = bTok.nextToken();
				final int beginIndex = bString.length() + 1;
				final int endIndex = aString.lastIndexOf(')');
	
				if (beginIndex <= aString.length())
				{
					if (endIndex >= beginIndex)
					{
						bTok = new StringTokenizer(aString.substring(beginIndex, endIndex), ",");
					}
					else
					{
						bTok = new StringTokenizer(aString.substring(beginIndex), ",");
					}
				}
				else
				{
					bTok = null;
				}
				aString = bString.replace('(', ' ').replace(')', ' ').trim();
			}
			else
			{
				final Ability tempAbility = aPC.getFeatNamed(anAbility.getName());
				if (tempAbility != null)
				{
					anAbility = tempAbility;
				}
				else
				{
					// add the Feat found, as a CharacterFeat
					anAbility = (Ability) anAbility.clone();
					aPC.addFeat(anAbility, playerCharacterLevelInfo);
				}
			}
	
			if (anAbility == null)
			{
				// if we still haven't found it, try a different string
				if (!addIt)
				{
					return;
				}
	
				anAbility = Globals.getAbilityNamed("FEAT", aString);
	
				if (anAbility == null)
				{
					Logging.errorPrint("Feat not found in PlayerCharacter.modFeatsFromList: " + aString);
	
					return;
				}
	
				anAbility = (Ability) anAbility.clone();
				aPC.addFeat(anAbility, playerCharacterLevelInfo);
			}
	
			if ((bTok != null) && bTok.hasMoreTokens())
			{
				while (bTok.hasMoreTokens())
				{
					aString = bTok.nextToken();
	
					if ("DEITYWEAPON".equals(aString))
					{
						WeaponProf wp = null;
	
						if (aPC.getDeity() != null)
						{
							wp = Globals.getWeaponProfNamed(aPC.getDeity().getFavoredWeapon());
						}
	
						if (wp != null)
						{
							if (addIt) // TODO: condition always true
							{
								anAbility.addAssociated(wp.getName());
							}
							else
							{
								anAbility.removeAssociated(wp.getName());
							}
						}
					}
					else
					{
						if (addIt) // TODO: condition always true
						{
							anAbility.addAssociated(aString);
						}
						else
						{
							anAbility.removeAssociated(aString);
						}
					}
				}
			}
			else
			{
				if (!all && !anAbility.isMultiples())
				{
					if (addIt)
					{
						aPC.adjustFeats(anAbility.getCost(aPC));
					}
					else
					{
						aPC.adjustFeats(-anAbility.getCost(aPC));
					}
				}
	
				AbilityUtilities.modFeat(aPC, playerCharacterLevelInfo, aString, addIt, all);
			}
	
			if (anAbility.getName().endsWith("Weapon Proficiency"))
			{
				for (int e = 0; e < anAbility.getAssociatedCount(); ++e)
				{
					final String wprof = anAbility.getAssociated(e);
					final WeaponProf wp = Globals.getWeaponProfNamed(wprof);
	
					if (wp != null)
					{
						aPC.addWeaponProfToChosenFeats(wprof);
					}
				}
			}
		}
	
		aPC.setAutomaticFeatsStable(false);
	}
}
