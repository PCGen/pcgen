/*
 * PreMult.java
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
package pcgen.core.prereq;

import pcgen.core.Equipment;
import pcgen.core.PlayerCharacter;
import pcgen.util.Logging;
import pcgen.util.PropertyFactory;

import java.util.Iterator;

/**
 * @author frugal@purplewombat.co.uk
 *
 */
public class PreMult  extends AbstractPrerequisiteTest implements PrerequisiteTest {

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#passes(pcgen.core.PlayerCharacter)
	 */
	public int passes(final Prerequisite prereq, final PlayerCharacter character) throws PrerequisiteException {
		int runningTotal=0;
		final int targetNumber = Integer.parseInt( prereq.getOperand() );

		for (Iterator iter = prereq.getPrerequisites().iterator(); iter.hasNext();) {
			final Prerequisite element = (Prerequisite) iter.next();

			final PrerequisiteTestFactory factory = PrerequisiteTestFactory.getInstance();
			final PrerequisiteTest test = factory.getTest(element.getKind());
			if (test != null) {
				runningTotal += test.passes(element, character);
			}
			else {
				Logging.errorPrintLocalised("PreMult.cannot_find_subtest", element.getKind()); //$NON-NLS-1$
			}
		}

		runningTotal = prereq.getOperator().compare(runningTotal, targetNumber);
		return countedTotal(prereq, runningTotal);
	}



	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#kindsHandled()
	 */
	public String kindHandled() {
		return "MULT"; //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#passes(pcgen.core.prereq.Prerequisite, pcgen.core.Equipment)
	 */
	public int passes(final Prerequisite prereq, final Equipment equipment, PlayerCharacter aPC) throws PrerequisiteException {
		int runningTotal=0;
		final int targetNumber = Integer.parseInt( prereq.getOperand() );

		for (Iterator iter = prereq.getPrerequisites().iterator(); iter.hasNext();) {
			final Prerequisite element = (Prerequisite) iter.next();

			final PrerequisiteTestFactory factory = PrerequisiteTestFactory.getInstance();
			final PrerequisiteTest test = factory.getTest(element.getKind());
			runningTotal += test.passes(element, equipment, aPC);
		}

		runningTotal = prereq.getOperator().compare(runningTotal, targetNumber);
		return countedTotal(prereq, runningTotal);
	}


	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#toHtmlString(pcgen.core.prereq.Prerequisite)
	 */
	public String toHtmlString(final Prerequisite prereq) {
		String str = ""; //$NON-NLS-1$

		final PrerequisiteTestFactory factory = PrerequisiteTestFactory.getInstance();

		String delimiter = ""; //$NON-NLS-1$
		for (Iterator iter = prereq.getPrerequisites().iterator(); iter.hasNext();) {
			final Prerequisite element = (Prerequisite) iter.next();

			final PrerequisiteTest test = factory.getTest(element.getKind());
			if (test==null)
			{
				Logging.errorPrintLocalised("PreMult.cannot_find_subformatter", element.getKind() ); //$NON-NLS-1$
			}
			else {
				str += delimiter;
				str += test.toHtmlString( element );
				delimiter = PropertyFactory.getString("PreMult.html_delimiter"); //$NON-NLS-1$
			}
		}

		return PropertyFactory.getFormattedString("PreMult.toHtml",  //$NON-NLS-1$
				new Object[] {prereq.getOperator().toDisplayString(),
						prereq.getOperand(),
						str} );

	}

}
