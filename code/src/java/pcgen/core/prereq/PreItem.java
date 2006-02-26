/*
 * PreItem.java
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

import pcgen.core.Equipment;
import pcgen.core.PlayerCharacter;
import pcgen.core.utils.CoreUtility;
import pcgen.util.PropertyFactory;

import java.util.Iterator;
import java.util.List;

/**
 * @author wardc
 *
 */
public class PreItem extends AbstractPrerequisiteTest implements PrerequisiteTest {

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#passes(pcgen.core.PlayerCharacter)
	 */
	public int passes(final Prerequisite prereq, final PlayerCharacter character) throws PrerequisiteException {
		final int number;
		try
		{
			number = Integer.parseInt( prereq.getOperand() );
		}
		catch (NumberFormatException e)
		{
			throw new PrerequisiteException(PropertyFactory.getFormattedString("PreItem.error.bad_operand", prereq.toString()) ); //$NON-NLS-1$
		}


		int runningTotal = 0;

		if (!character.getEquipmentList().isEmpty())
		{
			// Work out exactlywhat we are going to test.
			final String aString = prereq.getKey();
			List typeList = null;
			if (aString.startsWith("TYPE=") || aString.startsWith("TYPE.")) //$NON-NLS-1$ //$NON-NLS-2$
			{
				typeList = CoreUtility.split(aString.substring(5), '.');
			}


			for (Iterator e1 = character.getEquipmentList().iterator(); e1.hasNext();)
			{
				final Equipment eq = (Equipment) e1.next();
				if (typeList != null)
				{
					// Check to see if the equipment matches
					// all of the types in the requested list;
					boolean bMatches = true;
					for (int i = 0, x = typeList.size(); i < x; ++i)
					{
						if (!eq.isType((String) typeList.get(i)))
						{
							bMatches = false;
							break;
						}
					}
					if (bMatches)
					{
						runningTotal++;
					}
				}
				else
				{ //not a TYPE string
					if (aString.indexOf('%') >= 0) //handle wildcards (always assume they end the line)
					{
						if (eq.getName().toUpperCase().startsWith(aString.substring(0, aString.indexOf('%'))))
						{
							runningTotal++;
						}
					}
					else if (eq.getName().equalsIgnoreCase(aString)) //just a straight String compare
					{
						runningTotal++;
					}
				}
			}
		}

		runningTotal = prereq.getOperator().compare(runningTotal, number);
		return countedTotal(prereq, runningTotal);
	}




	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#kindsHandled()
	 */
	public String kindHandled() {
		return "ITEM"; //$NON-NLS-1$
	}

}
