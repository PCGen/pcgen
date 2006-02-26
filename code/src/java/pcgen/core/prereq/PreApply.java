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
 * Current Ver: $Revision: 1.14 $
 * Last Editor: $Author: binkley $
 * Last Edited: $Date: 2005/10/18 20:23:37 $
 *
 */
package pcgen.core.prereq;

import pcgen.core.Equipment;
import pcgen.core.PlayerCharacter;
import pcgen.util.Logging;
import pcgen.util.PropertyFactory;

import java.util.Iterator;

/**
 * @author wardc
 *
 */
public class PreApply extends AbstractPrerequisiteTest implements PrerequisiteTest {

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#passes(pcgen.core.PlayerCharacter)
	 */
	public int passes(final Prerequisite prereq, final PlayerCharacter character)
	{
		return 0;
	}

	public int passes(final Prerequisite prereq, final Equipment equipment, PlayerCharacter aPC) throws PrerequisiteException
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
			targetNumber = Integer.parseInt( prereq.getOperand() );
		}
		catch (NumberFormatException ne)
		{
			// Not an error, just a subtype
			requiredType = prereq.getOperand();
			targetNumber = 1;
		}

		for (Iterator iter = prereq.getPrerequisites().iterator(); iter.hasNext();)
		{
			final Prerequisite element = (Prerequisite) iter.next();
			final PrerequisiteTestFactory factory = PrerequisiteTestFactory.getInstance();
                        final PrerequisiteTest test = factory.getTest(element.getKind());
                        if (test != null) {
                                runningTotal += test.passes(element, equipment, aPC);
                        }
                        else {
                                Logging.errorPrintLocalised("PreApply.cannot_find_test", element.getKind()); //$NON-NLS-1$
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
			throw new PrerequisiteException(PropertyFactory.getFormattedString("PreApply.invalid_comparison", prereq.getOperator().toString()) ); //$NON-NLS-1$
		}

		runningTotal = countedTotal(prereq, runningTotal);
		return runningTotal;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#kindsHandled()
	 */
	public String kindHandled()
	{
		return "APPLY"; //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#toHtmlString(pcgen.core.prereq.Prerequisite)
	 */
	public String toHtmlString(final Prerequisite prereq) {
		return PropertyFactory.getFormattedString("PreApply.toHtml", prereq.getOperator().toString(), prereq.getOperand() ); //$NON-NLS-1$
	}

}
