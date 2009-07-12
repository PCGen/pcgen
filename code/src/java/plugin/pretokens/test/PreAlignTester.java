/*
 * PreAlign.java
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

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Equipment;
import pcgen.core.Globals;
import pcgen.core.PCAlignment;
import pcgen.core.PlayerCharacter;
import pcgen.core.analysis.AlignmentConverter;
import pcgen.core.prereq.AbstractPrerequisiteTest;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteTest;
import pcgen.util.PropertyFactory;

/**
 * @author wardc
 *
 */
public class PreAlignTester extends AbstractPrerequisiteTest implements
		PrerequisiteTest
{

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#passes(pcgen.core.PlayerCharacter)
	 */
	@Override
	public int passes(final Prerequisite prereq, final Equipment equipment,
		final PlayerCharacter aPC)
	{
		if (aPC == null)
		{
			return 0;
		}
		return passes(prereq, aPC);
	}

	@Override
	public int passes(final Prerequisite prereq, final PlayerCharacter character)
	{
		//
		// If game mode doesn't support alignment, then pass the prereq
		//
		int runningTotal = 0;

		if (Globals.getGameModeAlignmentText().length() == 0)
		{
			runningTotal = 1;
		}
		else
		{
			String desiredAlignment = prereq.getKey();
			PCAlignment al = getPCAlignment(desiredAlignment);
			final PCAlignment charAlignment = character.getPCAlignment();

			if (al.equals(charAlignment))
			{
				runningTotal = 1;
			}
			else if ((desiredAlignment.equalsIgnoreCase("Deity"))
				&& (character.getDeity() != null))
			{
				final PCAlignment deityAlignStr =
						character.getDeity().get(ObjectKey.ALIGNMENT);
				if (charAlignment.equals(deityAlignStr))
				{
					runningTotal = 1;
				}
			}
		}

		return countedTotal(prereq, runningTotal);
	}

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#kindsHandled()
	 */
	public String kindHandled()
	{
		return "align"; //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#toHtmlString(pcgen.core.prereq.Prerequisite)
	 */
	@Override
	public String toHtmlString(final Prerequisite prereq)
	{
		String alignment = prereq.getKey();
		PCAlignment al = getPCAlignment(alignment);
		return PropertyFactory
			.getFormattedString(
				"PreAlign.toHtml", prereq.getOperator().toDisplayString(), al.getKeyName()); //$NON-NLS-1$
	}

	private PCAlignment getPCAlignment(String desiredAlignIdentifier)
	{
		return AlignmentConverter.getPCAlignment(desiredAlignIdentifier);
	}
}
