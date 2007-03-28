/*
 * PreMove.java
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

import java.util.List;

import pcgen.core.Movement;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.core.prereq.AbstractPrerequisiteTest;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteException;
import pcgen.core.prereq.PrerequisiteTest;
import pcgen.util.PropertyFactory;

/**
 * Passes PREMOVE tags
 */
public class PreMoveTester extends AbstractPrerequisiteTest implements
		PrerequisiteTest
{

	/** Constructor */
	public PreMoveTester()
	{
		super();
	}

	/* 
	 * (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#passes(pcgen.core.prereq.Prerequisite, pcgen.core.PlayerCharacter)
	 */
	@Override
	public int passes(final Prerequisite prereq, final PlayerCharacter character)
		throws PrerequisiteException
	{
		int runningTotal = 0;
		int moveAmount = 0;

		if (hasMovement(character))
		{
			final String moveType = prereq.getKey();

			try
			{
				moveAmount = Integer.parseInt(prereq.getOperand());
			}
			catch (NumberFormatException e)
			{
				throw new PrerequisiteException(PropertyFactory
					.getFormattedString(
						"PreMove.error.bad_operand", prereq.toString())); //$NON-NLS-1$
			}

			for (int x = 0; x < character.getNumberOfMovements(); ++x)
			{
				final String type = character.getMovementType(x);
				final int speed = character.getMovement(x).intValue();
				if (moveType.equalsIgnoreCase(type) && speed >= moveAmount)
				{
					runningTotal += character.getMovement(x).intValue();
				}
			}
		}

		runningTotal = prereq.getOperator().compare(runningTotal, moveAmount);
		return countedTotal(prereq, runningTotal);
	}

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#kindsHandled()
	 */
	public String kindHandled()
	{
		return "MOVE"; //$NON-NLS-1$
	}

	/**
	 * Returns true if character's movements can be found
	 * 
	 * @param character
	 * @return true or false
	 */
	private boolean hasMovement(PlayerCharacter character)
	{
		if (character == null)
		{
			return false;
		}
		Race r = character.getRace();
		if (r == null)
		{
			return false;
		}
		List<Movement> movements = r.getMovements();
		if (movements == null || movements.isEmpty())
		{
			return false;
		}
		return movements.get(0).getNumberOfMovementTypes() != 0;
	}

}
