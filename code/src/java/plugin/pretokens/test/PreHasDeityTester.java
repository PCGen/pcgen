/*
 * PreHasDeity.java
 *
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
 *
 *
 *
 *
 */
package plugin.pretokens.test;

import pcgen.cdom.base.CDOMObject;
import pcgen.core.display.CharacterDisplay;
import pcgen.core.prereq.AbstractDisplayPrereqTest;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteOperator;
import pcgen.core.prereq.PrerequisiteTest;

/**
 *
 */
public class PreHasDeityTester extends AbstractDisplayPrereqTest implements
		PrerequisiteTest
{

	@Override
	public int passes(final Prerequisite prereq, final CharacterDisplay display, CDOMObject source)
	{
		int runningTotal;
		final boolean charHasDeity = display.getDeity() != null;

		final String ucOp = prereq.getKey().toUpperCase();
		final boolean flag =
				(ucOp.startsWith("Y") && charHasDeity)
					|| (ucOp.startsWith("N") && !charHasDeity); //$NON-NLS-1$ //$NON-NLS-2$
		if (prereq.getOperator().equals(PrerequisiteOperator.EQ)
			|| prereq.getOperator().equals(PrerequisiteOperator.GTEQ))
		{
			runningTotal = flag == true ? 1 : 0;
		}
		else
		{
			runningTotal = flag == false ? 1 : 0;
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
		return "HAS.DEITY"; //$NON-NLS-1$
	}
}
