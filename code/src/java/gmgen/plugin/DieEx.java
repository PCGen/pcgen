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
 *  DiceSuccess.java
 *
 *  Created on January 24, 2002, 11:15 AM
 */
package gmgen.plugin;

import gmgen.plugin.dice.Die;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

/**
 * This class does the rolling of the dice for the GMGen system.
 */
public class DieEx
{


	/** Number of sides */
	private final int sides;

	/** Total from last die roll */
	private int total;

	/** Number of dice */
	private final int num;

	/** Drop high roll */
	private boolean highDrop;

	/** Holds the rolls of each die */
	private int[] rolls;

	/** Dice roll that is dropped */
	private int drops;

	/** Creates an instance of this class to vet values as a die roll.
	 * @param roll Roll that needs to be made
	 */
	private DieEx(String roll)
	{
		StringTokenizer strTok = new StringTokenizer(roll, "d ");
		String hold = "";
		num = Integer.parseInt(strTok.nextToken());
		sides = Integer.parseInt(strTok.nextToken());
		rolls = new int[num];

		if (strTok.hasMoreTokens())
		{
			try
			{
				hold = strTok.nextToken();
				hold = strTok.nextToken();
			}
			catch (NoSuchElementException e)
			{
				drops = 0;
			}

			try
			{
				drops = Integer.parseInt(hold);
			}
			catch (NoSuchElementException e)
			{
				drops = 0;
			}

			try
			{
				hold = strTok.nextToken();
			}
			catch (NoSuchElementException e)
			{
				hold = "";
			}

			highDrop = !(hold.equals("lowest") || hold.equals(""));
		}
	}

	/** Method used for testing and running on it's own
	 * @param args Command line arguments
	 */
	public static void main(final String[] args)
	{
		DieEx DieRoller;
		StringBuilder temp = new StringBuilder();

		for (final String arg : args)
		{
			temp.append(arg).append(" ");
		}

		DieRoller = new DieEx(temp.toString());
		System.out.println("you rolled " + DieRoller.roll());
	}

	/** Rolls the die using the paramaters set
	 * @return Value of the die rolls
	 */
    @Override
	public int roll()
	{
		int total = 0;

		for (int x = 0; x < num; x++)
		{
			rolls[x] = Die.rand.nextInt(sides) + 1;
			total += rolls[x];
		}

		if (drops != 0)
		{
			// sort rolls first or this doesn't work.
			Arrays.sort(rolls);

			if (highDrop)
			{
				for (int x = rolls.length - 1; x > (rolls.length - drops - 1); x--)
				{
					total -= rolls[x];
				}
			} else
			{
				for (int x = 0; (x < drops) && (x < rolls.length); x++)
				{
					total -= rolls[x];
				}
			}
		}

		return total;
	}


	/** Creates a {@code String} representation of this class
	 * @return This class as a {@code String}.
	 */
	@Override
	public String toString()
	{
		return num + "d" + sides;
	}
}
