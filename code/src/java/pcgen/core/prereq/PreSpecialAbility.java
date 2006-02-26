/*
 * PreSpecialAbility.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
 * Copyright 2003 (C) Chris Ward <frugal@purplewombat.co.uk>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	   See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on November 28, 2003
 *
 * Current Ver: $Revision: 1.11 $
 * Last Editor: $Author: binkley $
 * Last Edited: $Date: 2005/10/18 20:23:37 $
 *
 */
package pcgen.core.prereq;

import pcgen.core.PCTemplate;
import pcgen.core.PlayerCharacter;
import pcgen.core.SpecialAbility;
import pcgen.util.PropertyFactory;

import java.util.Iterator;
import java.util.List;

/**
 * @author wardc
 *
 */
public class PreSpecialAbility extends AbstractPrerequisiteTest implements PrerequisiteTest {

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#passes(pcgen.core.PlayerCharacter)
	 */
	public int passes(final Prerequisite prereq, final PlayerCharacter character) throws PrerequisiteException {
		int runningTotal=0;
		int number;
		try
		{
			number = Integer.parseInt(prereq.getOperand());
		}
		catch (NumberFormatException exceptn)
		{
			throw new PrerequisiteException(PropertyFactory.getFormattedString("PreSpecialAbility.error.bad_operand", prereq.toString() )); //$NON-NLS-1$
		}


		final String aString = prereq.getKey().toUpperCase();
		if (!character.getSpecialAbilityList().isEmpty())
		{
			for (Iterator e1 = character.getSpecialAbilityList().iterator(); e1.hasNext();)
			{
				//final String e1String = ((SpecialAbility)e1.next()).getName();
				final Object obj = e1.next();
				String e1String = ((SpecialAbility) obj).getName();
				e1String = e1String.toUpperCase();
				if (e1String.startsWith(aString))
				{
					runningTotal++;
				}
			}
		}

		//
		// Now check any templates
		//
		if (!character.getTemplateList().isEmpty())
		{
			for (Iterator e1 = character.getTemplateList().iterator(); e1.hasNext();)
			{

				final PCTemplate aTempl = (PCTemplate) e1.next();
				final List SAs = aTempl.getSpecialAbilityList(character.getTotalLevels(), character.totalHitDice());

				if (SAs != null)
				{
					for (Iterator e2 = SAs.iterator(); e2.hasNext();)
					{
						final Object obj = e2.next();
						String e1String;
						if (obj instanceof String)
						{
							e1String = (String) obj;
						}
						else
						{
							e1String = ((SpecialAbility) obj).getName();
						}
						e1String = e1String.toUpperCase();
						if (e1String.startsWith(aString))
						{
							runningTotal++;
						}
					}
				}
			}
		}

		runningTotal = prereq.getOperator().compare( runningTotal, number);
		return countedTotal(prereq, runningTotal);
	}

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#kindsHandled()
	 */
	public String kindHandled() {
		return "SA"; //$NON-NLS-1$
	}

}
