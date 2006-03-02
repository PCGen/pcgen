/*
 * PreSpell.java
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
 * Current Ver: $Revision: 1.14 $
 * Last Editor: $Author: binkley $
 * Last Edited: $Date: 2005/10/22 18:33:23 $
 *
 */
package plugin.pretokens.test;

import pcgen.core.CharacterDomain;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.core.prereq.AbstractPrerequisiteTest;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteTest;
import pcgen.core.spell.Spell;
import pcgen.util.Logging;
import pcgen.util.PropertyFactory;

import java.util.Iterator;
import java.util.List;

/**
 * @author wardc
 *
 */
public class PreSpellTester extends AbstractPrerequisiteTest implements PrerequisiteTest {

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#passes(pcgen.core.PlayerCharacter)
	 */
	public int passes(final Prerequisite prereq, final PlayerCharacter character) {
		int requiredNumber = 0;
		try
		{
			requiredNumber = Integer.parseInt(prereq.getOperand());
		}
		catch (NumberFormatException e)
		{
			Logging.errorPrint(PropertyFactory.getString("PreSpell.error.badly_formed_attribute") + prereq.toString()); //$NON-NLS-1$
		}


		// Build a list of all possible spells
		final List aArrayList = character.aggregateSpellList("Any", "", "", "", 0, 20); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		//Needs to add domain spells as well
		for (Iterator domains = character.getCharacterDomainList().iterator(); domains.hasNext();)
		{
			final CharacterDomain aCD = (CharacterDomain) domains.next();
			if ((aCD != null) && (aCD.getDomain() != null))
			{
				aArrayList.addAll(Globals.getSpellsIn(-1, "", aCD.getDomain().toString())); //$NON-NLS-1$
			}
		}

		//Are there Innate Spell-like abilities?
		if (character.getAutoSpells())
		{
		    Race pcRace = character.getRace();
		    List raceList = pcRace.getSpellList();
		    if (raceList != null)
		    {
		        for (Iterator e = raceList.iterator(); e.hasNext();)
		        {
		            aArrayList.add(Globals.getSpellNamed(e.next().toString()));
		        }
		    }
		}


		final String spellName = prereq.getKey();
		int runningTotal=0;

		if (!aArrayList.isEmpty())
		{
			for (Iterator e1 = aArrayList.iterator(); e1.hasNext();)
			{
				final Spell aSpell = (Spell) e1.next();
				if (aSpell.getName().equalsIgnoreCase(spellName))
				{
					runningTotal++;
				}
			}
		}
		runningTotal = prereq.getOperator().compare(runningTotal, requiredNumber);
		return countedTotal(prereq, runningTotal);
	}

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#kindsHandled()
	 */
	public String kindHandled() {
		return "SPELL"; //$NON-NLS-1$
	}


	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#toHtmlString(pcgen.core.prereq.Prerequisite)
	 */
	public String toHtmlString(final Prerequisite prereq) {
		final Object[] args = new Object[] { prereq.getOperator().toDisplayString(),
				prereq.getOperand(),
				prereq.getKey()
		};
		return PropertyFactory.getFormattedString("PreSpell.toHtml", args); //$NON-NLS-1$
	}


}
