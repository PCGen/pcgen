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


/** A normal die
 * @author Soulcatcher
 * @since May 24, 2003
 */
public class Dice extends Die
{
	/** Die modifier */
	private int aModifier;

	/** Constructor for the Dice object
	 * @param num Number of dice
	 * @param sides Number of sides
	 * @param modifier Modifier to the die roll
	 */
	public Dice(int num, int sides, int modifier)
	{
		this.num = num;
		this.sides = sides;
		this.aModifier = modifier;
		rolls = new int[num];
		roll();
	}

	/** Constructor for the Dice object
	 * @param num Number of dice
	 * @param sides Number of sides per die
	 */
	public Dice(int num, int sides)
	{
		this(num, sides, 0);
	}

	/** Rolls the die, and returns the result.
	 * I made it final as it's called from the constructor.
	 * @return Result of the die roll
	 */
    @Override
	public final int roll()
	{
		int value = 0;
		int i;
		total = 0;

		for (i = 0; i < num; i++)
		{
			rolls[i] = rand.nextInt(sides) + 1;
			value = rolls[i] + value;
		}

		total = value + aModifier;
		timesRolled++;

		return total;
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
