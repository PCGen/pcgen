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

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.Movement;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.core.prereq.AbstractPrerequisiteTest;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteException;
import pcgen.core.prereq.PrerequisiteTest;
import pcgen.system.LanguageBundle;

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
	public int passes(final Prerequisite prereq, final PlayerCharacter character, CDOMObject source)
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
				throw new PrerequisiteException(LanguageBundle
					.getFormattedString(
						"PreMove.error.bad_operand", prereq.toString())); //$NON-NLS-1$
			}

			int speed = character.getMovementOfType(moveType).intValue();
			if (speed >= moveAmount)
			{
				runningTotal += speed;
			}
		}

		runningTotal = prereq.getOperator().compare(runningTotal, moveAmount);
		return countedTotal(prereq, runningTotal);
	}

	/**
	 * Get the type of prerequisite handled by this token.
	 * @return the type of prerequisite handled by this token.
	 */
    @Override
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
		List<Movement> movements = r.getListFor(ListKey.MOVEMENT);
		if (movements == null || movements.isEmpty())
		{
			return false;
		}
		return movements.get(0).getNumberOfMovementTypes() != 0;
	}

}
