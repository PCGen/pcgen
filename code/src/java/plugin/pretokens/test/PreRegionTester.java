/*
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
 */
package plugin.pretokens.test;

import pcgen.cdom.base.CDOMObject;
import pcgen.core.Equipment;
import pcgen.core.display.CharacterDisplay;
import pcgen.core.prereq.AbstractDisplayPrereqTest;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteException;
import pcgen.core.prereq.PrerequisiteOperator;
import pcgen.core.prereq.PrerequisiteTest;
import pcgen.system.LanguageBundle;


public class PreRegionTester extends AbstractDisplayPrereqTest implements PrerequisiteTest
{

	@Override
	public int passes(Prerequisite prereq, Equipment equipment,
		CharacterDisplay display) throws PrerequisiteException
	{
		if (display == null)
		{
			return 0;
		}
		return passes(prereq, display, equipment);
	}

	/**
	 * @see pcgen.core.prereq.PrerequisiteTest#passes(pcgen.core.PlayerCharacter)
	 */
	@Override
	public int passes(final Prerequisite prereq, final CharacterDisplay display, CDOMObject source)
		throws PrerequisiteException
	{

		final String requiredRegion = prereq.getKey().toUpperCase();
		final String characterRegion = display.getFullRegion().toUpperCase();

		final boolean sameRegion = characterRegion.startsWith(requiredRegion);

		int runningTotal;
		if (prereq.getOperator().equals(PrerequisiteOperator.EQ))
		{
			runningTotal = sameRegion ? 1 : 0;
		}
		else if (prereq.getOperator().equals(PrerequisiteOperator.NEQ))
		{
			runningTotal = sameRegion ? 0 : 1;
		}
		else
		{
			throw new PrerequisiteException(LanguageBundle.getFormattedString(
				"PreRegion.error.invalid_comparator", prereq.toString())); //$NON-NLS-1$
		}

		return countedTotal(prereq, runningTotal);
	}

	/**
	 * Get the type of prerequisite handled by this token.
	 * @return the type of prerequisite handled by this token.
	 */
    @Override
	public String kindHandled()
	{
		return "REGION"; //$NON-NLS-1$
	}

}
