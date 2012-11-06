/*
 * PreText.java
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

import pcgen.cdom.base.CDOMObject;
import pcgen.core.Equipment;
import pcgen.core.PlayerCharacter;
import pcgen.core.prereq.AbstractPrerequisiteTest;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteException;
import pcgen.core.prereq.PrerequisiteOperator;
import pcgen.core.prereq.PrerequisiteTest;
import pcgen.util.Logging;
import pcgen.system.LanguageBundle;

/**
 * Prerequisite tester, tests for the presence of a type.
 *
 */
public class PreTypeTester extends AbstractPrerequisiteTest implements
		PrerequisiteTest
{

	/**
	 * Get the type of prerequisite handled by this token.
	 * @return the type of prerequisite handled by this token.
	 */
    @Override
	public String kindHandled()
	{
		return "TYPE"; //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#passes(pcgen.core.prereq.Prerequisite, pcgen.core.Equipment)
	 */
	@Override
	public int passes(final Prerequisite prereq, final Equipment equipment,
		PlayerCharacter aPC) throws PrerequisiteException
	{

		final String requiredType = prereq.getKey();
		int runningTotal = 0;

		if (prereq.getOperator().equals(PrerequisiteOperator.EQ))
		{
			if (equipment.isPreType(requiredType))
			{
				runningTotal++;
			}
		}
		else if (prereq.getOperator().equals(PrerequisiteOperator.NEQ))
		{
			if (!equipment.isPreType(requiredType))
			{
				runningTotal++;
			}
		}
		else
		{
			throw new PrerequisiteException(
				LanguageBundle
					.getFormattedString(
						"PreType.error.invalidComparison", prereq.getOperator().toString(), prereq.toString())); //$NON-NLS-1$
		}

		runningTotal = countedTotal(prereq, runningTotal);
		return runningTotal;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#passes(pcgen.core.prereq.Prerequisite, pcgen.core.PlayerCharacter)
	 */
	@Override
	public int passes(final Prerequisite prereq, final PlayerCharacter aPC, CDOMObject source)
	{

		Logging
			.errorPrint("PRETYPE has been deprecated for non-Equipment Prerequisites."
				+ "\n  Please use PRERACE as an alternative");
		if (aPC == null)
		{
			return 0;
		}
		
		final String requiredType = prereq.getKey();
		
		Logging.errorPrint("  PRETYPE value was: " + requiredType + "\n");
		
		final int numRequired = Integer.parseInt(prereq.getOperand());
		int runningTotal = 0;

		for (String element : aPC.getTypes())
		{
			if (element.equalsIgnoreCase(requiredType))
			{
				runningTotal++;
			}
		}

		runningTotal = prereq.getOperator().compare(runningTotal, numRequired);
		return countedTotal(prereq, runningTotal);
	}

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#toHtmlString(pcgen.core.prereq.Prerequisite)
	 */
	@Override
	public String toHtmlString(final Prerequisite prereq)
	{
		return LanguageBundle
			.getFormattedString(
				"PreType.toHtml", prereq.getOperator().toDisplayString(), prereq.getKey()); //$NON-NLS-1$
	}

}
