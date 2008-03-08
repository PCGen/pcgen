/*
 * PreSkill.java
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
 */package plugin.pretokens.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.print.attribute.standard.Severity;

import pcgen.core.PlayerCharacter;
import pcgen.core.Skill;
import pcgen.core.prereq.AbstractPrerequisiteTest;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteOperator;
import pcgen.core.prereq.PrerequisiteTest;
import pcgen.util.PropertyFactory;
import pcgen.core.Globals;
import plugin.lsttokens.ServesAsToken;
/**
 * @author wardc
 *
 */
public class PreSkillTester extends AbstractPrerequisiteTest implements
		PrerequisiteTest
{

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#passes(pcgen.core.PlayerCharacter)
	 */
	@Override
	public int passes(final Prerequisite prereq, final PlayerCharacter character)
	{
		final int requiredRanks = Integer.parseInt(prereq.getOperand());
		SkillMatcher matcher = this.getSkillMatcher();

		// Compute the skill name from the Prerequisite
		String requiredSkillKey = prereq.getKey().toUpperCase();
		if (prereq.getSubKey() != null)
		{
			requiredSkillKey += " (" + prereq.getSubKey().toUpperCase() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
		}

		final boolean isType =
				(requiredSkillKey.startsWith("TYPE.") || requiredSkillKey.startsWith("TYPE=")); //$NON-NLS-1$ //$NON-NLS-2$
		if (isType)
		{
			requiredSkillKey = requiredSkillKey.substring(5);
		}
		final String skillKey = requiredSkillKey;

		// Now locate all instances of this skillname and test them
		final int percentageSignPosition = skillKey.lastIndexOf('%');
		
		HashMap<Skill,HashSet<Skill>> serveAsSkills = new HashMap<Skill, HashSet<Skill>>();
		Set<Skill> imitators = new HashSet<Skill>();
		this.getImitators(serveAsSkills, imitators, character);
		
		
		int runningTotal = 0;

		boolean foundMatch = false;
		boolean foundSkill = false;
		final List<Skill> skillList = new ArrayList<Skill>(character.getSkillList());
		
		for (Skill aSkill : skillList)
		{
			final String aSkillKey = aSkill.getKeyName().toUpperCase();
			if (isType)
			{
				if (percentageSignPosition >= 0)
				{
					foundMatch = matchesTypeWildCard(aSkillKey, percentageSignPosition, foundSkill, aSkill);
					foundSkill = (foundMatch)? true: false;
					runningTotal = getRunningTotal(aSkill, character
						, prereq, foundMatch, runningTotal, requiredRanks);
				}
				else if (aSkill.isType(skillKey))
				{
					foundMatch = true;
					foundSkill = true;
					runningTotal = getRunningTotal(aSkill, character
						, prereq, foundMatch, runningTotal, requiredRanks);
				}
			}
			else if (aSkillKey.equals(skillKey)
				|| ((percentageSignPosition >= 0) && aSkillKey
					.startsWith(skillKey.substring(0, percentageSignPosition))))
			{
				foundMatch = true;
				foundSkill = true;
				runningTotal = getRunningTotal(aSkill, character
					, prereq, foundMatch, runningTotal, requiredRanks);
			}

			if (prereq.isCountMultiples() || prereq.isTotalValues())
			{
				// For counted totals we want to count all occurances, not just the first
				foundMatch = false;
			}
			if (foundMatch)
			{
				break;
			}
		}
		if (isType)
		{
			if(percentageSignPosition >= 0)
			{
				
			}
			else
			{
				
			}
		}
		else
		{
			for(Skill mock: serveAsSkills.keySet()) 
			{
				HashSet<Skill> targets = serveAsSkills.get(mock);
				for(Skill target: targets)
				{
					if(target.getDisplayName().equalsIgnoreCase(skillKey))
					{
						foundSkill = true;
						foundMatch = true;
						int theTotal = getRunningTotal(mock, character, prereq, foundMatch
							, runningTotal, requiredRanks);
						runningTotal += theTotal;
					}
				}
			}
		}


		// If we are looking for a negative test i.e. !PRESKILL and the PC
		// doesn't have the skill we have to return a match
		if (foundSkill == false)
		{
			if (prereq.getOperator() == PrerequisiteOperator.LT)
			{
				runningTotal++;
			}
		}
		return countedTotal(prereq, runningTotal);
	}

	private void getImitators(
		HashMap<Skill, HashSet<Skill>> serveAsSkills, Set<Skill> imitators,
		PlayerCharacter character)
	{
		List<Skill> allSkills = Globals.getSkillList();		
		for(Skill aSkill: allSkills)
		{
			Skill finalSkill = null ;
			Set<Skill> servesAs = new HashSet<Skill>();
			for(String fakeSkill: aSkill.getServesAs().keySet())
			{
				finalSkill = Globals.getSkillKeyed(fakeSkill);
				servesAs.add(finalSkill);
			}
			
			if(servesAs.size() > 0)
			{
				imitators.add(aSkill);
				serveAsSkills.put(aSkill, (HashSet<Skill>) servesAs);
			}
		}		
	}

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#kindsHandled()
	 */
	public String kindHandled()
	{
		return "SKILL"; //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#toHtmlString(pcgen.core.prereq.Prerequisite)
	 */
	@Override
	public String toHtmlString(final Prerequisite prereq)
	{
		String skillName = prereq.getKey();
		if (prereq.getSubKey() != null && !prereq.getSubKey().equals("")) //$NON-NLS-1$
		{
			skillName += " (" + prereq.getSubKey() + ")"; //$NON-NLS-1$ //$NON-NLS-2$

		}

		final String foo =
				PropertyFactory.getFormattedString("PreSkill.toHtml", //$NON-NLS-1$
					new Object[]{prereq.getOperator().toDisplayString(),
						prereq.getOperand(), skillName});
		return foo;
	}
	/**
	 * Mar 6, 2008 - Joe.Frazier
	 * @param skillKey
	 * @param percentageSignPosition
	 * @param foundMatch
	 * @param aSkill
	 * @return
	 */
	private boolean matchesTypeWildCard(final String skillKey,
		final int percentageSignPosition, boolean found, Skill aSkill)
	{
		for (String type : aSkill.getTypeList(false))
		{
			if (type.startsWith(
				skillKey.substring(0, percentageSignPosition)))
			{
				found = true;
				break;
			}
		}
		return found;
	}
	private int getRunningTotal(Skill aSkill, PlayerCharacter character, Prerequisite prereq
		, boolean foundMatch, int runningTotal, int requiredRanks )
	{
		if (foundMatch)
		{
			if (prereq.isTotalValues())
			{
				runningTotal +=
						aSkill.getTotalRank(character).intValue();
			}
			else
			{
				if (prereq.getOperator().compare(
					aSkill.getTotalRank(character).intValue(),
					requiredRanks) > 0)
				{
					runningTotal++;
				}
			}
			if (runningTotal == 0)
			{
				foundMatch = false;
			}
		}
		return runningTotal;
	}
	private SkillMatcher getSkillMatcher()
	{
		return new SkillMatcher();
	}
	public class SkillMatcher
	{
		private int runningTotal=0;
		private boolean matchesFound = false;
		private boolean foundOneMatch = false;
		private int totalRanks =0;
		
		public final int getTotalRanks()
		{
			return totalRanks;
		}
		public final void incrementTotalRanks(final int num)
		{
			totalRanks += num;
		}
		protected final int getRunningTotal()
		{
			return runningTotal;
		}
		protected final void setRunningTotal(int runningTotal)
		{
			this.runningTotal = runningTotal;
		}
		protected final boolean isMatchesFound()
		{
			return matchesFound;
		}
		protected final void setMatchesFound(boolean matchesFound)
		{
			this.matchesFound = matchesFound;
		}
		protected final boolean isFoundOneMatch()
		{
			return foundOneMatch;
		}
		protected final void setFoundOneMatch(boolean SkillFound)
		{
			this.foundOneMatch = SkillFound;
		}
		protected final void setTotalRanks(int totalRanks)
		{
			this.totalRanks = totalRanks;
		}		
	}
}
