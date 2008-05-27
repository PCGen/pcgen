/*
 * Copyright 2007 (C) Tom Parker <thpr@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.cdom.modifier;

import pcgen.cdom.content.AbstractHitDieModifier;
import pcgen.cdom.content.HitDie;

public class HitDieStep extends AbstractHitDieModifier
{

	private final int numSteps;

	private final HitDie dieLimit;

	public HitDieStep(int steps, HitDie stopAt)
	{
		if (steps == 0)
		{
			throw new IllegalArgumentException();
		}
		numSteps = steps;
		dieLimit = stopAt;
	}

	@Override
	public HitDie applyModifier(HitDie hd, Object context)
	{
		int steps = numSteps;
		HitDie currentDie = hd;
		while (steps != 0)
		{
			if (dieLimit.equals(currentDie))
			{
				return currentDie;
			}
			if (steps > 0)
			{
				currentDie = currentDie.getNext();
				steps--;
			}
			else
			{
				assert steps < 0;
				currentDie = currentDie.getPrevious();
				steps++;
			}
		}
		return currentDie;
		// TODO Auto-generated method stub
		/*
		 * Theoretically, the die sizes here should be stored as ... what? A
		 * AbstractSequencedConstant, effectively? This gives the ability to
		 * look up the next one... that makes HitDie not really storing an
		 * Int... so Hit Die really should be a helper, or an enumeration?
		 * 
		 * So it looks like an enumeration is OUT because the MODs will actually
		 * alter to unexpected values... like 8 * 3 = 24... therefore, this
		 * really needs to be thought through to determine what is best...
		 */
	}

	@Override
	public String getLSTformat()
	{
		StringBuilder sb = new StringBuilder();
		sb.append('%');
		if (dieLimit == null)
		{
			sb.append('H');
		}
		if (numSteps > 0)
		{
			sb.append("up");
		}
		else
		{
			sb.append("down");
		}
		sb.append(Math.abs(numSteps));
		return sb.toString();
	}

	@Override
	public int hashCode()
	{
		return dieLimit == null ? numSteps : numSteps + dieLimit.hashCode()
			* 29;
	}

	@Override
	public boolean equals(Object o)
	{
		if (o instanceof HitDieStep)
		{
			HitDieStep other = (HitDieStep) o;
			return other.numSteps == numSteps
				&& (dieLimit == null && other.dieLimit == null || dieLimit != null
					&& dieLimit.equals(other.dieLimit));
		}
		return false;
	}
}
