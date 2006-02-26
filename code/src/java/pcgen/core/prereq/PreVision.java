/*
 * PreVision.java
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
 * Current Ver: $Revision: 1.12 $
 * Last Editor: $Author: karianna $
 * Last Edited: $Date: 2005/09/19 15:04:03 $
 *
 */
package pcgen.core.prereq;

import pcgen.core.PlayerCharacter;
import pcgen.core.utils.CoreUtility;


/**
 * @author wardc
 *
 * Checks a characters vision..
 */
public class PreVision extends AbstractPrerequisiteTest implements PrerequisiteTest {

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#passes(pcgen.core.PlayerCharacter)
	 */
	public int passes(final Prerequisite prereq, final PlayerCharacter character) {
		final String requiredVision = prereq.getKey().toUpperCase();
		final int requiredRange = Integer.parseInt( prereq.getOperand() );
		int charVisionRange = 0;
		boolean foundVision = false;

		final String[] charVisions = character.getVision().split(","); //$NON-NLS-1$
		for (int i = 0; i < charVisions.length && !foundVision ; i++) {
			String charVision = charVisions[i];
			charVision = CoreUtility.replaceAll(charVision, " ", ""); //$NON-NLS-1$ //$NON-NLS-2$
			charVision = CoreUtility.replaceAll(charVision, "'", ""); //$NON-NLS-1$ //$NON-NLS-2$

			if (charVision.toUpperCase().startsWith( requiredVision )) {
				// Extract the range integer from the character
				// vision string
				foundVision = true;
				if (charVision.indexOf("(") > 0) //$NON-NLS-1$
				{
					String wString = charVision.substring(charVision.indexOf("(") + 1); //$NON-NLS-1$
					wString = wString.substring(0, wString.length() - 1);
					try
					{
						charVisionRange = Integer.parseInt(wString);
					}
					catch (NumberFormatException e)
					{
						charVisionRange = 0;
					}
				}
			}
		}

		final int runningTotal = prereq.getOperator().compare(charVisionRange, requiredRange);
		return countedTotal(prereq, runningTotal);
	}



	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#kindsHandled()
	 */
	public String kindHandled() {
		return "VISION"; //$NON-NLS-1$
	}

}
