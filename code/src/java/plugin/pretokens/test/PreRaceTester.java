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
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import pcgen.core.Constants;
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
public class PreRaceTester extends AbstractPrerequisiteTest implements
		PrerequisiteTest
{

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#passes(pcgen.core.PlayerCharacter)
	 */
	@Override
	public int passes(final Prerequisite prereq, final PlayerCharacter character)
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
							if(raceType.equalsIgnoreCase(raceToMatch))
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
			for (String subType : character.getRacialSubTypes())
			{
				if (reqType.equalsIgnoreCase(subType))
				{
					++runningTotal;
				}
			}
			if(runningTotal == 0)
			{
				boolean match  = false;
BREAKOUT:		for (Race imitator: servesAsRace.keySet())
					{
						for (Race mock: servesAsRace.get(imitator))
						{
							for(String subType: mock.getRacialSubTypes())
							{
								if (reqType.equalsIgnoreCase(subType))
								{
									match = true;
									++runningTotal;
								}
							}
							if (match)
							{
								break BREAKOUT;
							}
						}
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
	
	private void getImitators(HashMap<Race, HashSet<Race>> serveAsRaces,PlayerCharacter character)
	{
		Map<String, Race> allRaces = Globals.getRaces();		
		for(String aRace: allRaces.keySet())
		{
			Race theRace = allRaces.get(aRace);
			Race finalRace = null;
			Set<Race> servesAs = new HashSet<Race>();
			if (theRace == null)
			{
				return;
			}
			for(String fakeRace: theRace.getServesAs(""))
			{
				finalRace = Globals.getRaceKeyed(fakeRace);
				if (finalRace == null)
				{
					continue;
				}
				servesAs.add(finalRace);
			}			
			if(servesAs.size() > 0)
			{
				serveAsRaces.put(theRace, (HashSet<Race>) servesAs);
			}
		}		
	}

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#kindsHandled()
	 */
	public String kindHandled()
	{
		return "RACE"; //$NON-NLS-1$
	}

}
