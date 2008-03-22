/*
 * PreArmourProficiency.java
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

import pcgen.core.ArmorProf;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.prereq.AbstractPrerequisiteTest;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteException;
import pcgen.core.prereq.PrerequisiteTest;
import pcgen.util.PropertyFactory;

/**
 * <code>PreArmorProfTester</code> does the testing of armor proficiency 
 * prerequisites. 
 *
 * @author Chris Ward <frugal@purplewombat.co.uk>
 * @version $Revision$
 */
public class PreArmorProfTester extends AbstractPrerequisiteTest implements
		PrerequisiteTest
{

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#passes(pcgen.core.PlayerCharacter)
	 */
	@Override
	public int passes(final Prerequisite prereq, final PlayerCharacter character) throws PrerequisiteException
	{
		int runningTotal = 0;

		final int number;
		try
		{
			number = Integer.parseInt(prereq.getOperand());
		}
		catch (NumberFormatException exceptn)
		{
			throw new PrerequisiteException(PropertyFactory.getFormattedString(
				"Prereq.error", "PREARMOR", prereq.toString())); //$NON-NLS-1$ //$NON-NLS-2$
		}

		ArmorProf keyProf = Globals.getArmorProfKeyed(prereq.getKey());
		final boolean isType =
			prereq.getKey().startsWith("TYPE") && prereq.getKey().length() > 5;
		
		for (String profName : character.getArmorProfList())
		{
			if (profName.equalsIgnoreCase(prereq.getKey()))
			{
				runningTotal++;
			}
			else if (isType && profName.startsWith("TYPE")
					&& profName.substring(5).equalsIgnoreCase(prereq.getKey()))
			{
				// TYPE=Light equals TYPE.Light
				runningTotal++;
			}
			else if (profName.startsWith("ARMORTYPE"))
			{
				String profType = profName.substring(10);
				if (profType.equalsIgnoreCase(prereq.getKey()))
				{
					runningTotal++;
				}
				else if (isType
					&& profType.equalsIgnoreCase(prereq.getKey().substring(5)))
				{
					runningTotal++;
				}
				else if (keyProf != null)
				{
					for (String keyProfType : keyProf.getTypeList(false))
					{
						if (profType.equalsIgnoreCase(keyProfType))
						{
							runningTotal++;
							break;
						}
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
	public String kindHandled()
	{
		return "armorprof"; //$NON-NLS-1$
	}

}
