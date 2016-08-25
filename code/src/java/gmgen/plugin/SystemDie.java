/*
 *  Initiative - A role playing utility to track turns
 *  Copyright (C) 2002 Devon D Jones
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 *  SystemDie.java
 */
package gmgen.plugin;


import gmgen.plugin.dice.ResultModifier;
import gmgen.plugin.dice.SimpleModifier;
import gmgen.plugin.dice.SimpleSumCounter;
import gmgen.plugin.dice.SystemModifier;

/** A d20 die, applies a +10 on a 20, and a -10 on a 1
 * @author Soulcatcher
 * @since May 24, 2003
 */
class SystemDie extends Die
{
	/**  Modifier to each roll. */
	private final int aModifier;

	/** Constructor for the SystemDie object
	 * @param modifier Modifier to each roll
	 */
	private SystemDie(final int modifier)
	{
		this.num = 1;
		this.sides = 20;
		this.aModifier = modifier;
		rolls = new int[num];
	}

	/**  Constructor for the SystemDie object */
	SystemDie()
	{
		this(0);
	}

	/** Roll the die.  If the roll is 20, return 30, if it's 1, returns -9.
	 * @return result from the roll
	 */
    @Override
	public int roll()
	{

		for (int i = 0; i < num; i++)
		{
			int thisRoll = Die.rand.nextInt(sides) + 1;
			rolls[i] = thisRoll;
		}
		return new SimpleSumCounter().totalCount(
				ResultModifier.modify(rolls,
						new SystemModifier(),
						new SimpleModifier(aModifier)
				)
		);

	}

	/** Name of the die in nds+m form
	 * @return Name of the die
	 */
	@Override
	public String toString()
	{
		if (aModifier == 0)
		{
			return num + "d" + sides;
		}
		return num + "d" + sides + "+" + aModifier;
	}
}
