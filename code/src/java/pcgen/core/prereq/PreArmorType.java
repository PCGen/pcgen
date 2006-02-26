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
 * Current Ver: $Revision: 1.7 $
 * Last Editor: $Author: binkley $
 * Last Edited: $Date: 2005/10/18 20:23:37 $
 *
 */
package pcgen.core.prereq;

import pcgen.core.Equipment;
import pcgen.core.PlayerCharacter;

import java.util.Iterator;


/**
 * @author wardc
 *
 */
public class PreArmorType extends AbstractPrerequisiteTest implements PrerequisiteTest {

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#passes(pcgen.core.PlayerCharacter)
	 */
	public int passes(final Prerequisite prereq, final PlayerCharacter character) {
		int runningTotal=0;

		if (!character.getEquipmentList().isEmpty())
		{
			final String desiredType = prereq.getKey();
			for (Iterator e1 = character.getEquipmentList().iterator(); e1.hasNext();)
			{
				// For every item of equipment the character has.
				final Equipment eq = (Equipment) e1.next();

				// Match against a TYPE of armour
				if (desiredType.startsWith("TYPE=") || desiredType.startsWith("TYPE.")) //$NON-NLS-1$ //$NON-NLS-2$
				{
					if ((eq.getType().indexOf("ARMOR." + desiredType.substring(5).toUpperCase()) >= 0) && eq.isEquipped()) //$NON-NLS-1$
					{
						runningTotal++;
						break;
					}
				}
				else
				{ //not a TYPE string
					if (desiredType.indexOf('%') >= 0) //handle wildcards (always assume they end the line)
					{
						if ((eq.getName().startsWith(desiredType.substring(0, desiredType.indexOf('%')))) && (eq.isEquipped()))
						{
							runningTotal++;
							break;
						}
					}
					else if (desiredType.indexOf("LIST") >= 0) //$NON-NLS-1$
					{
						for (Iterator e2 = character.getArmorProfList().iterator(); e2.hasNext();)
						{
							String aprof = (String) e2.next();
							aprof = "ARMOR." + aprof; //$NON-NLS-1$

							if ((eq.getType().indexOf(aprof) >= 0) && eq.isEquipped())
							{
								runningTotal++;
								break;
							}
						}
					}
					else if ((eq.getName().equalsIgnoreCase(desiredType)) && (eq.isEquipped())) //just a straight String compare
					{
						runningTotal++;
						break;
					}
				}
			}
		}
		return countedTotal(prereq, runningTotal);
	}

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#kindsHandled()
	 */
	public String kindHandled() {
		return "ARMORTYPE"; //$NON-NLS-1$
	}

}
