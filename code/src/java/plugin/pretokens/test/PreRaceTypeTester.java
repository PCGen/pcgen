/*
 * PreRace.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
 * Copyright 2003 (C) Chris Ward <frugal@purplewombat.co.uk>
 * Copyright 2005 (C) Greg Bingleman <byngl@hotmail.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on November 28, 2003
 *
 * Current Ver: $Revision: 7951 $
 * Last Editor: $Author: thpr $
 * Last Edited: $Date: 2008-10-05 15:21:21 -0400 (Sun, 05 Oct 2008) $
 *
 */
package plugin.pretokens.test;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.RaceType;
import pcgen.core.PlayerCharacter;
import pcgen.core.prereq.AbstractPrerequisiteTest;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteTest;

/**
 * @author	wardc
 * @author	byngl <byngl@hotmail.com>
 *
 */
public class PreRaceTypeTester extends AbstractPrerequisiteTest implements PrerequisiteTest
{

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * pcgen.core.prereq.PrerequisiteTest#passes(pcgen.core.PlayerCharacter)
	 */
	@Override
	public int passes(final Prerequisite prereq, final PlayerCharacter character, CDOMObject source)
	{
		final int reqnumber = Integer.parseInt(prereq.getOperand());
		final String requiredRaceType = prereq.getKey();
		int runningTotal = 0;

		try
		{
			RaceType preRaceType = RaceType.valueOf(requiredRaceType);
			if (preRaceType.equals(character.getRace().get(ObjectKey.RACETYPE)))
			{
				runningTotal++;
			}
		}
		catch (IllegalArgumentException e)
		{
			//Can't match
		}
		if (character.getCritterType().indexOf(requiredRaceType) >= 0)
		{
			runningTotal++;
		}
		runningTotal = prereq.getOperator().compare(runningTotal, reqnumber);
		return countedTotal(prereq, runningTotal);
	}

	/**
	 * Get the type of prerequisite handled by this token.
	 * @return the type of prerequisite handled by this token.
	 */
    @Override
	public String kindHandled()
	{
		return "RACETYPE"; //$NON-NLS-1$
	}

}
