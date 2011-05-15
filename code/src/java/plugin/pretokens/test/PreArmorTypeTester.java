/*
 * PreArmourType.java
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
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package plugin.pretokens.test;

import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMObject;
import pcgen.core.Equipment;
import pcgen.core.PlayerCharacter;
import pcgen.core.prereq.AbstractPrerequisiteTest;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteTest;

/**
 * @author wardc
 *
 */
public class PreArmorTypeTester extends AbstractPrerequisiteTest implements PrerequisiteTest
{

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#passes(pcgen.core.PlayerCharacter)
	 */

	// TODO All the equipment related PRE tag code should be refactored into a
	// common base class.
	@Override
	public int passes(final Prerequisite prereq, final PlayerCharacter character, CDOMObject source)
	{
		int runningTotal = 0;

		if (character.hasEquipment())
		{
			final String desiredType = prereq.getKey();
			for (Equipment eq : character.getEquippedEquipmentSet())
			{
				if (!eq.isArmor())
				{
					continue;
				}

				// Match against a TYPE of armour
				if (desiredType.startsWith("TYPE=")
					|| desiredType.startsWith("TYPE.")) //$NON-NLS-1$ //$NON-NLS-2$
				{
					StringTokenizer tok =
							new StringTokenizer(desiredType.substring(5)
								.toUpperCase(), ".");
					boolean match = false;
					if (tok.hasMoreTokens())
					{
						match = true;
					}
					//
					// Must match all listed types to qualify
					//
					while (tok.hasMoreTokens())
					{
						final String type = tok.nextToken();
						if (!eq.isType(type))
						{
							match = false;
							break;
						}
					}
					if (match)
					{
						runningTotal++;
						break;
					}
				}
				else
				{ //not a TYPE string
					final String eqName = eq.getName().toUpperCase();
					if (desiredType.indexOf('%') >= 0)
					{
						//handle wildcards (always assume they
						// end the line)
						final int percentPos = desiredType.indexOf('%');
						final String substring =
								desiredType.substring(0, percentPos)
									.toUpperCase();
						if (eqName.startsWith(substring))
						{
							runningTotal++;
							break;
						}
					}
					else if (desiredType.indexOf("LIST") >= 0) //$NON-NLS-1$
					{
						if (character.isProficientWith(eq))
						{
							runningTotal++;
							break;
						}
					}
					else if (eqName.equals(desiredType)) //just a straight String compare
					{
						runningTotal++;
						break;
					}
				}
			}
		}
		return countedTotal(prereq, runningTotal);
	}

	/**
	 * Get the type of prerequisite handled by this token.
	 * @return the type of prerequisite handled by this token.
	 */
	public String kindHandled()
	{
		return "ARMORTYPE"; //$NON-NLS-1$
	}

}
