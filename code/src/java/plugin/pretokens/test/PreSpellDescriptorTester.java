/*
 * PreSpellDescriptor.java
 * Copyright 2004 (C) Anders Lindgren <blithwyn@yahoo.co.uk>
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
 * Created on August 21, 2004
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package plugin.pretokens.test;

import pcgen.core.PlayerCharacter;
import pcgen.core.prereq.AbstractPrerequisiteTest;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteTest;
import pcgen.util.PropertyFactory;

import java.util.List;

/**
 * @author blithwyn
 *
 */
public class PreSpellDescriptorTester  extends AbstractPrerequisiteTest implements PrerequisiteTest {

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#passes(pcgen.core.PlayerCharacter)
	 */
	public int passes(final Prerequisite prereq, final PlayerCharacter character) {
		final String descriptor = prereq.getKey();
		final int requiredLevel = Integer.parseInt( prereq.getSubKey() );
		final int requiredNumber = Integer.parseInt( prereq.getOperand() );

		final List aArrayList = character.aggregateSpellList("Any", "No-Match", "A", descriptor, requiredLevel, 20); //$NON-NLS-1$ //$NON-NLS-2$

		final int runningTotal = prereq.getOperator().compare(aArrayList.size(), requiredNumber );
		return countedTotal(prereq, runningTotal);
	}

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#kindsHandled()
	 */
	public String kindHandled() {
		return "SPELL.DESCRIPTOR"; //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#toHtmlString(pcgen.core.prereq.Prerequisite)
	 */
	public String toHtmlString(final Prerequisite prereq) {
		final Object[] args = new Object[] {
					 prereq.getOperator().toDisplayString(),
					 prereq.getOperand(),
					 prereq.getSubKey(),
					 prereq.getKey()
		};
		return PropertyFactory.getFormattedString("PreSpellDescriptor.toHtml", args); //$NON-NLS-1$
	}
}

