/*
 * PreShieldProficiency.java
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

import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.ShieldProf;
import pcgen.core.prereq.AbstractPrerequisiteTest;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteTest;

/**
 * @author wardc
 *
 */
public class PreShieldProfTester extends AbstractPrerequisiteTest implements
		PrerequisiteTest
{

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#passes(pcgen.core.PlayerCharacter)
	 */
	@Override
	public int passes(final Prerequisite prereq, final PlayerCharacter character)
	{
		final int numberRequired = Integer.parseInt(prereq.getOperand());
		int runningTotal = 0;

		final String aString = prereq.getKey();
		final boolean isType =
				aString.startsWith("TYPE") && aString.length() > 5;
		ShieldProf keyProf = Globals.getShieldProfKeyed(aString);
		for (String profName : character.getShieldProfList())
		{
			if (profName.equalsIgnoreCase(aString))
			{
				runningTotal++;
			}
			else if (isType && profName.startsWith("TYPE")
				&& profName.substring(5).equalsIgnoreCase(aString.substring(5)))
			{
				runningTotal++;
			}
			else if (profName.startsWith("SHIELDTYPE"))
			{
				String profType = profName.substring(11);
				if (profType.equalsIgnoreCase(prereq.getKey()))
				{
					runningTotal++;
				}
				else if (isType && profType.equalsIgnoreCase(
					aString.substring(5)))
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

		runningTotal =
				prereq.getOperator().compare(runningTotal, numberRequired);
		return countedTotal(prereq, runningTotal);
	}

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#kindsHandled()
	 */
	public String kindHandled()
	{
		return "SHIELDPROF"; //$NON-NLS-1$
	}

}
