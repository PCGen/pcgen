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
 *  Dice.java
 *
 *  Created on January 24, 2002, 11:15 AM
 */
package gmgen.plugin;


import gmgen.plugin.dice.AppendModifier;
import gmgen.plugin.dice.ResultModifier;
import gmgen.plugin.dice.SimpleModifier;
import gmgen.plugin.dice.SimpleSumCounter;

/** A normal die
 * @author Soulcatcher
 * @since May 24, 2003
 */
public class Dice extends Die
{
	/** Die modifier */
	private final int aModifier;

	/** Constructor for the Dice object
	 * @param num Number of dice
	 * @param sides Number of sides
	 * @param modifier Modifier to the die roll
	 */
	public Dice(final int num, final int sides, final int modifier)
	{
		this.num = num;
		this.sides = sides;
		this.aModifier = modifier;
		/* Holds the rolls of each die */
		roll();
	}

	/** Constructor for the Dice object
	 * @param num Number of dice
	 * @param sides Number of sides per die
	 */
	public Dice(final int num, final int sides)
	{
		this(num, sides, 0);
	}

	/** Rolls the die, and returns the result.
	 * @return Result of the die roll
	 */
    @Override
	public int roll()
	{
		return new SimpleSumCounter().totalCount(
				ResultModifier.modify(
						new AppendModifier(num, sides, Die.random),
						new SimpleModifier(aModifier)
				)
		);
	}

	/** Name of the die in the nds+m format
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
