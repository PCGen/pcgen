/*
 * PreAgeSetTester.java
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.    See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on December 30, 2006
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package plugin.pretokens.test;

import java.util.List;

import pcgen.core.PlayerCharacter;
import pcgen.core.prereq.AbstractPrerequisiteTest;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteException;
import pcgen.core.prereq.PrerequisiteTest;
import pcgen.core.Globals;
import pcgen.util.PropertyFactory;

/**
 * @author perchrh
 *
 */
public class PreAgeSetTester extends AbstractPrerequisiteTest implements
		PrerequisiteTest
{

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#passes(pcgen.core.PlayerCharacter)
	 */
	@Override
	public int passes(final Prerequisite prereq, final PlayerCharacter character) throws PrerequisiteException
	{
		final int ageset = Globals.getBioSet().getPCAgeSet(character);
		int runningTotal;

		try
		{
			final int anInt = Integer.parseInt(prereq.getOperand());
			runningTotal = prereq.getOperator().compare(ageset, anInt);
		}
		catch (NumberFormatException exc)
		{
			final int anInt = Globals.getBioSet().getAgeSetNamed(prereq.getOperand());
			runningTotal = prereq.getOperator().compare(ageset, anInt);
		}
		catch (Exception e){
			throw new PrerequisiteException(PropertyFactory.getFormattedString("PreAgeSet.error.badly_formed_attribute", prereq.getOperand())); //$NON-NLS-1$
		}
		
		return countedTotal(prereq, runningTotal);
		
	}

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#kindsHandled()
	 */
	public String kindHandled()
	{
		return "AGESET"; //$NON-NLS-1$
	}

}
