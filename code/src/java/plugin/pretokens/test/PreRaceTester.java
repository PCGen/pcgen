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
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package plugin.pretokens.test;

import java.util.List;
import java.util.StringTokenizer;

import pcgen.core.Constants;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.core.prereq.AbstractPrerequisiteTest;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteTest;

import java.util.Iterator;

/**
 * @author	wardc
 * @author	byngl <byngl@hotmail.com>
 *
 */
public class PreRaceTester extends AbstractPrerequisiteTest implements PrerequisiteTest
{

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#passes(pcgen.core.PlayerCharacter)
	 */
	public int passes(final Prerequisite prereq, final PlayerCharacter character)
	{
		final int reqnumber = Integer.parseInt(prereq.getOperand());
		final String requiredRace = prereq.getKey();
		int runningTotal = 0;

		if (requiredRace.startsWith("TYPE=") || requiredRace.startsWith("TYPE."))	//$NON-NLS-1$ //$NON-NLS-2$
		{
			final Race pcRace = character.getRace();
			StringTokenizer tok = new StringTokenizer(requiredRace.substring(5), ".");
			boolean match = true;
			//
			// Must match all listed types in order to qualify
			//
			while(tok.hasMoreTokens())
			{
				final String type = tok.nextToken();
				if (!pcRace.isType(type))
				{
					match = false;
					break;
				}
			}
			if (match)
			{
				++runningTotal;
			}
		}
		else if (requiredRace.startsWith("RACETYPE=") || requiredRace.startsWith("RACETYPE."))	//$NON-NLS-1$ //$NON-NLS-2$
		{
			return character.getRaceType().equalsIgnoreCase(requiredRace.substring(9)) ? 1 : 0;
		}
		else if (requiredRace.startsWith("RACESUBTYPE=") || requiredRace.startsWith("RACESUBTYPE."))
		{
			List subTypes = character.getRacialSubTypes();
			final String reqType = requiredRace.substring(12);
			for (Iterator i = subTypes.iterator(); i.hasNext(); )
			{
				final String subType = (String)i.next();
				if (reqType.equalsIgnoreCase(subType))
				{
					++runningTotal;
				}
			}
		}
		else
		{
			final String characterRace = character.getRace().getKeyName();
			final int wild = requiredRace.indexOf('%');
			if (wild == 0)
			{
				//
				// Matches as long as race is not <none selected>
				//
				if (!characterRace.equalsIgnoreCase(Constants.s_NONESELECTED))
				{
					++runningTotal;
				}
			}
			else if (wild > 0)
			{
				if (characterRace.regionMatches(true, 0, requiredRace, 0, wild))
				{
					++runningTotal;
				}
			}
			else
			{
				if (characterRace.equalsIgnoreCase(requiredRace))
				{
					++runningTotal;
				}
			}
		}

		if (runningTotal > reqnumber)
		{
			runningTotal = reqnumber;
		}
		runningTotal = prereq.getOperator().compare(runningTotal, reqnumber);
		return countedTotal(prereq, runningTotal);
	}

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#kindsHandled()
	 */
	public String kindHandled()
	{
		return "RACE"; //$NON-NLS-1$
	}

}
