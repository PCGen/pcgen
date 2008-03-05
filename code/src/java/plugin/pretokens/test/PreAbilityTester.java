/*
 * PreAbilityTester.java
 * Copyright 2007 (C) James Dempsey <jdempsey@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.       See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on January 23, 2006
 *
 * Current Ver: $Revision: 1777 $
 * Last Editor: $Author: jdempsey $
 * Last Edited: $Date: 2006-12-17 15:36:01 +1100 (Sun, 17 Dec 2006) $
 *
 */
package plugin.pretokens.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.Equipment;
import pcgen.core.GameMode;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.core.Ability.Nature;
import pcgen.core.prereq.AbstractPrerequisiteTest;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteException;
import pcgen.core.prereq.PrerequisiteTest;
import pcgen.core.prereq.PrerequisiteUtilities;
import pcgen.util.Logging;
import pcgen.util.PropertyFactory;

/**
 * <code>PreAbilityParser</code> tests whether a character passes ability
 * prereqs.
 *
 * Last Editor: $Author: jdempsey $
 * Last Edited: $Date: 2006-12-17 15:36:01 +1100 (Sun, 17 Dec 2006) $
 *
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision: 1777 $
 */
public class PreAbilityTester extends AbstractPrerequisiteTest implements
		PrerequisiteTest
{

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#passes(pcgen.core.PlayerCharacter)
	 */
	@Override
	public int passes(final Prerequisite prereq, final Equipment equipment,
		final PlayerCharacter aPC) throws PrerequisiteException
	{
		if (aPC == null)
		{
			return 0;
		}
		return passes(prereq, aPC);
	}

	@Override
	public int passes(final Prerequisite prereq, final PlayerCharacter character)
		throws PrerequisiteException
	{
		final boolean countMults = prereq.isCountMultiples();

		final int number;
		try
		{
			number = Integer.parseInt(prereq.getOperand());
		}
		catch (NumberFormatException exceptn)
		{
			throw new PrerequisiteException(PropertyFactory.getFormattedString(
				"PreAbility.error", prereq.toString())); //$NON-NLS-1$
		}

		GameMode gameMode = SettingsHandler.getGame();
		String key = prereq.getKey();
		String subKey = prereq.getSubKey();
		String categoryName = prereq.getCategoryName();
		AbilityCategory category = gameMode.getAbilityCategory(categoryName);
		
		Set<Ability> servesAsList = getServesAsList(character);
		int runningTotal;

		runningTotal = PrerequisiteUtilities.passesAbilityTest(prereq, character,
						countMults, number, key, subKey, categoryName, category);
		if (runningTotal ==0)
		{
			for (Ability ability: servesAsList)
			{
				try
				{
					Prerequisite newPre = prereq.clone();
					String newCatName = ability.getCategory();
					AbilityCategory newCategory = gameMode.getAbilityCategory(newCatName);
					String newKey = ability.getDisplayName();
					
					newPre.setCategoryName(newCatName);
					newPre.setKey(newKey);
					runningTotal = PrerequisiteUtilities.passesAbilityTest(newPre, character,
						countMults, number, newKey, subKey, newCatName, newCategory);	
					if (runningTotal > 0)
					{
						break;
					}
				}
				catch (CloneNotSupportedException e)
				{
					Logging.debugPrint("");
				}
				
				
			}
		}
		return countedTotal(prereq, runningTotal);
	}

	/**
	 * Returns a set for all the characters abilities that have 
	 * a servesAs list.
	 * 
	 * @param character
	 * @return Set<Ability>
	 */
	private Set<Ability> getServesAsList(final PlayerCharacter character)
	{
		Map<Nature, Set<Ability>> allAbilities = character.getAbilitiesSet();
		Set<Ability> theAbilitiesToCheck = new TreeSet<Ability>();
		for(Nature nature: allAbilities.keySet())
		{
			Set<Ability> abilityItems = allAbilities.get(nature);
			for (Ability ab: abilityItems)
			{
				if (ab.getServesAs().size() > 0)
				{
					theAbilitiesToCheck.add(ab);
				}
			}			
		}
		return theAbilitiesToCheck;
	}

	@Override
	public String toHtmlString(final Prerequisite prereq)
	{
		String aString = prereq.getKey();
		if ((prereq.getSubKey() != null) && !prereq.getSubKey().equals(""))
		{
			aString = aString + " ( " + prereq.getSubKey() + " )";
		}

		if (aString.startsWith("TYPE=")) //$NON-NLS-1$
		{
			if (prereq.getCategoryName().length() > 0)
			{
				// {0} {1} {2}(s) of type {3}
				return PropertyFactory.getFormattedString("PreAbility.type.toHtml", //$NON-NLS-1$
					new Object[]{prereq.getOperator().toDisplayString(),
						prereq.getOperand(),
						prereq.getCategoryName(),
						aString.substring(5)});
			}
			else
			{
				// {0} {1} ability(s) of type {2}
				return PropertyFactory.getFormattedString("PreAbility.type.noCat.toHtml",  //$NON-NLS-1$ 
					new Object[]{prereq.getOperator().toDisplayString(),
						prereq.getOperand(),
						aString.substring(5)});
			}
				
		}
		// {2} {3} {1} {0}
		return PropertyFactory.getFormattedString("PreAbility.toHtml",  //$NON-NLS-1$
			new Object[]{prereq.getCategoryName(),
				aString, prereq.getOperator().toDisplayString(),
				prereq.getOperand()}); 
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see pcgen.core.prereq.PrerequisiteTest#kindsHandled()
	 */
	public String kindHandled()
	{
		return "ABILITY"; //$NON-NLS-1$
	}

}
