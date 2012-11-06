/*
 *  DiceFudge.java
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


/** Fudge Die
 * @author Soulcatcher
 * @since May 24, 2003
 */
public class DiceFudge extends Die
{
	/**  Number of sides */
	private static final int aSides = 6;

	/**  Die to do the rolling behind the scenes */
	protected Dice die;

	/** Die modifier */
	private int aModifier = 0;

	/** Constructor for the DiceFudge object
	 * @param num Number of fudge dice
	 * @param modifier Modifier to the rolls
	 */
	public DiceFudge(int num, int modifier)
	{
		this.num = num;
		this.aModifier = modifier;
		rolls = new int[num];
		die = new Dice(1, aSides);
		roll();
	}

	/** Constructor for the DiceFudge object
	 * @param num Number of Fudge Dice
	 */
	public DiceFudge(int num)
	{
		this(num, 0);
	}

	/** Roll the dice
	 * @return Result of the roll
	 */
    @Override
	public int roll()
	{
		int i;
		total = 0;

		for (i = 0; i < num; i++)
		{
			int thisRoll = die.roll();
			rolls[i] = thisRoll;

			if ((thisRoll == 1) || (thisRoll == 2))
			{
				total--;
			}
			else if ((thisRoll == 5) || (thisRoll == 6))
			{
				total++;
			}
		}

		total += aModifier;

		return total;
	}

	/**  The name of the die in the ndF format
	 * @return ndF
	 */
	@Override
	public String toString()
	{
		return num + "dF";
	}
}
