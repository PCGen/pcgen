/*
 * PreApply.java
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
import pcgen.core.prereq.PrerequisiteTestFactory;
import pcgen.util.Logging;
import pcgen.system.LanguageBundle;

/**
 * @author wardc
 *
 */
public class PreApplyTester extends AbstractPrerequisiteTest implements PrerequisiteTest
{

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#passes(pcgen.core.PlayerCharacter)
	 */
	@Override
	public int passes(final Prerequisite prereq, final PlayerCharacter character, CDOMObject source)
	{
		return 0;
	}

	@Override
	public int passes(final Prerequisite prereq, final Equipment equipment,
		PlayerCharacter aPC) throws PrerequisiteException
	{
		// PREAPPLY target could be a Player or Equipment types
		//
		// PREAPPLY:Ranged,Thrown
		//  -> applied to Ranged Thrown weapons
		// PREAPPLY:Melee;Ranged
		//  -> applied to Melee or Ranged weapons
		//  -> so , means AND
		//  -> so ; mean OR
		// PREAPPLY:PC
		//  -> applied to PlayerCharacter object
		// PREAPPLY:ANYPC
		//  -> applied to PlayerCharacter object
		//

		int runningTotal = 0;
		int targetNumber;
		String requiredType = null;

		try
		{
			targetNumber = Integer.parseInt(prereq.getOperand());
		}
		catch (NumberFormatException ne)
		{
			// Not an error, just a subtype
			requiredType = prereq.getOperand();
			targetNumber = 1;
		}

		for (Prerequisite element : prereq.getPrerequisites())
		{
			final PrerequisiteTestFactory factory =
					PrerequisiteTestFactory.getInstance();
			final PrerequisiteTest test = factory.getTest(element.getKind());
			if (test != null)
			{
				runningTotal += test.passes(element, equipment, aPC);
			}
			else
			{
				Logging.errorPrintLocalised(
					"PreApply.cannot_find_test", element.getKind()); //$NON-NLS-1$
			}
		}

		if (requiredType == null)
		{
			return runningTotal >= targetNumber ? 1 : 0;
		}

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
			throw new PrerequisiteException(LanguageBundle.getFormattedString(
				"PreApply.invalid_comparison", prereq.getOperator().toString())); //$NON-NLS-1$
		}

		runningTotal = countedTotal(prereq, runningTotal);
		return runningTotal;
	}

	/**
	 * Get the type of prerequisite handled by this token.
	 * @return the type of prerequisite handled by this token.
	 */
	public String kindHandled()
	{
		return "APPLY"; //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#toHtmlString(pcgen.core.prereq.Prerequisite)
	 */
	@Override
	public String toHtmlString(final Prerequisite prereq)
	{
		return LanguageBundle
			.getFormattedString(
				"PreApply.toHtml", prereq.getOperator().toString(), prereq.getOperand()); //$NON-NLS-1$
	}

}
