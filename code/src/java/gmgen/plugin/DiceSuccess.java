/*
 *  DiceSuccess.java
 *  Copyright (C) 2002 Devon D Jones
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 *  The author of this program grants you the ability to use this code
 *  in conjunction with code that is covered under the Open Gaming License
 *
 */
package gmgen.plugin;


/** Success Dice.  Pass in the number of dice, and the difficulty and this returns the number of successes.  Does not give any
 * extra successes if you roll the max number
 * @author Soulcatcher
 * @since May 24, 2003
 */
public class DiceSuccess extends Die
{
	/** Die to do the behind the scenes rolling */
	protected Dice die;

	/** Difficulty of the roll */
	private int difficulty;

	/**  Constructor for the DiceSuccess object
	 * @param num Number of dice in the pool
	 * @param sides Number of sides per die
	 * @param modifier Modifier to each roll
	 * @param difficulty Difficulty of a success
	 */
	public DiceSuccess(int num, int sides, int modifier, int difficulty)
	{
		this.num = num;
		this.sides = sides;
		this.difficulty = difficulty;
		rolls = new int[num];
		die = new Dice(1, sides);
		roll();
	}

	/** Constructor for the DiceSuccess object
	 * @param num Number of dice in the pool
	 * @param sides Number of sides per die
	 * @param difficulty Difficulty of a success
	 */
	public DiceSuccess(int num, int sides, int difficulty)
	{
		this(num, sides, 0, difficulty);
	}

	/** Roll all the dice in the pool, and return the number of successes
	 * @return Number of successes rolled.
	 */
    @Override
	public int roll()
	{
		int i;
		total = 0;

		for (i = 0; i < num; i++)
		{
			rolls[i] = die.roll();

			if (rolls[i] >= difficulty)
			{
				total++;
			}
		}

		return total;
	}

	/** Name of the dice in the nds format
	 * @return Name of the die pool
	 */
	@Override
	public String toString()
	{
		return num + "d" + sides;
	}
}
