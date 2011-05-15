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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.RaceSubType;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.core.prereq.AbstractPrerequisiteTest;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteTest;

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
	@Override
	public int passes(final Prerequisite prereq, final PlayerCharacter character, CDOMObject source)
	{
		final int reqnumber = Integer.parseInt(prereq.getOperand());
		final String requiredRace = prereq.getKey();
		int runningTotal = 0;
		HashMap<Race, HashSet<Race>> servesAsRace = new HashMap<Race, HashSet<Race>>();
		getImitators(servesAsRace, character);
		final Race pcRace = character.getRace();
		
		if (requiredRace.startsWith("TYPE=") || requiredRace.startsWith("TYPE.")) //$NON-NLS-1$ //$NON-NLS-2$
		{
			
			StringTokenizer tok =
					new StringTokenizer(requiredRace.substring(5), ".");
			
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
					match= true;
					continue;
				}
				if (!match)
				{					
BREAKOUT:			for(Race imitators : servesAsRace.keySet())
					{
						if (servesAsRace.get(imitators).contains(pcRace))
						{
							for (Race mock: servesAsRace.get(imitators))
							{
								if(mock.isType(type))
								{
									matchCount++;
									match = true;
									break;
								}
							}
							if(match)
							{
								break BREAKOUT;
							}
						}
					}
				}
			}
			if (count == matchCount)
			{
				++runningTotal;
			} 
			
		}
		else if (requiredRace.startsWith("RACETYPE=") || requiredRace.startsWith("RACETYPE.")) //$NON-NLS-1$ //$NON-NLS-2$
		{
			String raceToMatch = requiredRace.substring(9);
			String raceType = character.getRaceType();
			boolean isMatchingRaceType = raceType.equalsIgnoreCase(
				requiredRace.substring(9)) ? true : false;
			if (isMatchingRaceType) 
			{
				return 1;
			}
			else
			{
				for(Race imitators : servesAsRace.keySet())
				{
					if (servesAsRace.get(imitators).contains(pcRace))
					{
						for (Race mock: servesAsRace.get(imitators))
						{
							if (mock.get(ObjectKey.RACETYPE).toString()
								.equalsIgnoreCase(raceToMatch))
							{
								return 1;
							}
						}
					}
				return 0;
				}
			}
		}
		else if (requiredRace.startsWith("RACESUBTYPE=")
			|| requiredRace.startsWith("RACESUBTYPE."))
		{
			final String reqType = requiredRace.substring(12);
			RaceSubType st = RaceSubType.getConstant(reqType);
			if (character.containsRacialSubType(st))
			{
				++runningTotal;
			}
			if(runningTotal == 0)
			{
BREAKOUT:		for (Race imitator: servesAsRace.keySet())
					{
						for (Race mock: servesAsRace.get(imitator))
						{
							if (mock.containsInList(ListKey.RACESUBTYPE, st))
							{
								++runningTotal;
								break BREAKOUT;
							}
						}
					}
			}
		}
		else
		{
			final String characterRace = pcRace.getKeyName();
			final int wild = requiredRace.indexOf('%');
			if (wild == 0)
			{
				//
				// Matches as long as race is not <none selected>
				//
				if (!characterRace.equalsIgnoreCase(Constants.NONESELECTED))
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
				else
				{
					runningTotal += checkForServesAsRaceWildcard(requiredRace, wild, pcRace, servesAsRace);
				}
			}
			else
			{
				if (characterRace.equalsIgnoreCase(requiredRace))
				{
					++runningTotal;
				}
				else 
				{
BREAKOUT:			for(Race imitators : servesAsRace.keySet())
					{
						if (servesAsRace.get(imitators).contains(pcRace) 
								&& imitators.getDisplayName().equalsIgnoreCase(requiredRace))
						{
							++runningTotal;
							break BREAKOUT;
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
	
	private int checkForServesAsRaceWildcard(String requiredRace, int wild, Race pcRace, HashMap<Race, HashSet<Race>> servesAsRace)
	{
		for(Race imitators : servesAsRace.keySet())
		{
			if (servesAsRace.get(imitators).contains(pcRace) 
					&& imitators.getDisplayName().regionMatches(true, 0, requiredRace, 0, wild))
			{
				return 1;
			}
		}
		return 0;
	}
	
	private void getImitators(HashMap<Race, HashSet<Race>> serveAsRaces,PlayerCharacter character)
	{
		for (Race theRace : Globals.getContext().ref.getConstructedCDOMObjects(Race.class))
		{
			Set<Race> servesAs = new HashSet<Race>();
			for(CDOMReference<Race> ref: theRace.getSafeListFor(ListKey.SERVES_AS_RACE))
			{
				servesAs.addAll(ref.getContainedObjects());
			}
			if(servesAs.size() > 0)
			{
				serveAsRaces.put(theRace, (HashSet<Race>) servesAs);
			}
		}		
	}

	/**
	 * Get the type of prerequisite handled by this token.
	 * @return the type of prerequisite handled by this token.
	 */
	public String kindHandled()
	{
		return "RACE"; //$NON-NLS-1$
	}

}
