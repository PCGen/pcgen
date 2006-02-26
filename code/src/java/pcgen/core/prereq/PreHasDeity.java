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
 * Created on 19-Dec-2003
 *
 * Current Ver: $Revision: 1.6 $
 *
 * Last Editor: $Author: byngl $
 *
 * Last Edited: $Date: 2005/10/03 13:54:30 $
 *
 */
package pcgen.core.prereq;

import pcgen.core.PlayerCharacter;

/**
 * @author wardc
 *
 */
public class PreHasDeity extends AbstractPrerequisiteTest implements PrerequisiteTest {

	public int passes(final Prerequisite prereq, final PlayerCharacter character) {
		int runningTotal;
		final boolean charHasDeity = character.getDeity() != null;

		final boolean flag = (("Y".equals(prereq.getOperand().toUpperCase()) && (charHasDeity)) || ("N".equals(prereq.getOperand().toUpperCase()) && (!charHasDeity))); //$NON-NLS-1$ //$NON-NLS-2$
		if (prereq.getOperator().equals( PrerequisiteOperator.EQ ))
		{
			runningTotal = flag==true ? 1 : 0;
		}
		else
		{
			runningTotal = flag==false ? 1 : 0;
		}

		return countedTotal(prereq, runningTotal);
	}

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#kindsHandled()
	 */
	public String kindHandled() {
		return "HAS.DEITY"; //$NON-NLS-1$
	}
}
