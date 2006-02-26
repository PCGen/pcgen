/*
 * PreSpellSchoolSub.java
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
 * Last Editor: $Author: binkley $
 * Last Edited: $Date: 2005/10/18 20:23:37 $
 *
 */
package pcgen.core.prereq;

import pcgen.core.PlayerCharacter;
import pcgen.util.PropertyFactory;

import java.util.List;

/**
 * @author wardc
 *
 */
public class PreSpellSchoolSub extends AbstractPrerequisiteTest implements PrerequisiteTest {

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#passes(pcgen.core.PlayerCharacter)
	 */
	public int passes(final Prerequisite prereq, final PlayerCharacter character) {
		final String subSchool = prereq.getKey();
		final int requiredLevel = Integer.parseInt( prereq.getSubKey() );
		final int requiredNumber = Integer.parseInt( prereq.getOperand() );

		final List aArrayList = character.aggregateSpellList("Any", "No-Match", subSchool, "No-Match", requiredLevel, 20); //$NON-NLS-1$ //$NON-NLS-2$

		final int runningTotal = prereq.getOperator().compare(aArrayList.size(), requiredNumber );
		return countedTotal(prereq, runningTotal);
	}

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#kindsHandled()
	 */
	public String kindHandled() {
		return "SPELL.SUBSCHOOL"; //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#toHtmlString(pcgen.core.prereq.Prerequisite)
	 */
	public String toHtmlString(final Prerequisite prereq) {
		return PropertyFactory.getFormattedString("PreSpellSchoolSub.toHtml_spell_sub_school", //$NON-NLS-1$
				new Object[] {
									 		prereq.getOperator().toDisplayString(),
									 		prereq.getOperand(),
									 		prereq.getKey(),
									 		prereq.getSubKey()
					} );
	}
}
