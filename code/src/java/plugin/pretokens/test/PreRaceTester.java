/*
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
 */
package plugin.pretokens.test;

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.RaceSubType;
import pcgen.cdom.enumeration.RaceType;
import pcgen.core.Race;
import pcgen.core.display.CharacterDisplay;
import pcgen.core.prereq.AbstractDisplayPrereqTest;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteTest;

public class PreRaceTester extends AbstractDisplayPrereqTest implements PrerequisiteTest
{

	@Override
	public int passes(final Prerequisite prereq, final CharacterDisplay display, CDOMObject source)
	{
		final int reqnumber = Integer.parseInt(prereq.getOperand());
		final String requiredRace = prereq.getKey();
		int runningTotal = 0;
		final Race pcRace = display.getRace();
		Set<Race> racesImitated = getRacesImitatedBy(pcRace);

		if (requiredRace.startsWith("TYPE=") || requiredRace.startsWith("TYPE.")) //$NON-NLS-1$ //$NON-NLS-2$
		{

			StringTokenizer tok = new StringTokenizer(requiredRace.substring(5), ".");

			String type;
			boolean match = false;
			int count = 0;
			int matchCount = 0;

			while (tok.hasMoreTokens())
			{
				count++;
				match = false;
				type = tok.nextToken();
				if (pcRace.isType(type))
				{
					matchCount++;
					match = true;
					continue;
				}
                for (Race mock : racesImitated)
                {
                    if (mock.isType(type))
                    {
                        matchCount++;
                        match = true;
                        break;
                    }
                }
            }
			if (count == matchCount)
			{
				++runningTotal;
			}

		}
		else if (requiredRace.startsWith("RACETYPE=") //$NON-NLS-1$
				|| requiredRace.startsWith("RACETYPE.")) //$NON-NLS-1$
		{
			String raceToMatch = requiredRace.substring(9);
			String raceType = display.getRaceType();
			boolean isMatchingRaceType = raceType.equalsIgnoreCase(requiredRace.substring(9)) ? true : false;
			if (isMatchingRaceType)
			{
				++runningTotal;
			}
			else
			{
				for (Race mock : racesImitated)
				{
					RaceType mockRaceType = mock.get(ObjectKey.RACETYPE);
					if (mockRaceType != null && mockRaceType.toString().equalsIgnoreCase(raceToMatch))
					{
						++runningTotal;
					}
				}
			}
		}
		else if (requiredRace.startsWith("RACESUBTYPE=") || requiredRace.startsWith("RACESUBTYPE."))
		{
			final String reqType = requiredRace.substring(12);
			RaceSubType st = RaceSubType.getConstant(reqType);
			if (display.containsRacialSubType(st))
			{
				++runningTotal;
			}
			if (runningTotal == 0)
			{
				for (Race mock : racesImitated)
				{
					if (mock.containsInList(ListKey.RACESUBTYPE, st))
					{
						++runningTotal;
						break;
					}
				}
			}
		}
		else
		{
			boolean isUnselected = (pcRace == null) || pcRace.isUnselected();
			final int wild = requiredRace.indexOf('%');
			if (wild == 0)
			{
				if (!isUnselected)
				{
					++runningTotal;
				}
			}
			else if (wild > 0)
			{
				if (!isUnselected && pcRace.getKeyName().regionMatches(true, 0, requiredRace, 0, wild))
				{
					++runningTotal;
				}
				else
				{
					runningTotal += checkForServesAsRaceWildcard(requiredRace, wild, racesImitated);
				}
			}
			else
			{
				if (!isUnselected && pcRace.getKeyName().equalsIgnoreCase(requiredRace))
				{
					++runningTotal;
				}
				else
				{
					for (Race mock : racesImitated)
					{
						if (mock.getDisplayName().equalsIgnoreCase(requiredRace))
						{
							++runningTotal;
							break;
						}
					}
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

	private int checkForServesAsRaceWildcard(String requiredRace, int wild, Set<Race> imitatedRaces)
	{
		for (Race mock : imitatedRaces)
		{
			if (mock.getDisplayName().regionMatches(true, 0, requiredRace, 0, wild))
			{
				return 1;
			}
		}
		return 0;
	}

	private Set<Race> getRacesImitatedBy(Race pcRace)
	{
		Set<Race> servesAs = new HashSet<>();
		if (pcRace != null)
		{
			for (CDOMReference<Race> ref : pcRace.getSafeListFor(ListKey.SERVES_AS_RACE))
			{
				servesAs.addAll(ref.getContainedObjects());
			}
		}
		return servesAs;
	}

	/**
	 * Get the type of prerequisite handled by this token.
	 * @return the type of prerequisite handled by this token.
	 */
	@Override
	public String kindHandled()
	{
		return "RACE"; //$NON-NLS-1$
	}

}
