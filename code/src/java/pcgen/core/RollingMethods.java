/*
 * RollingMethods.java
 * Copyright 2001 (C) Mario Bonassin
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.     See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 *
 * Created on April 21, 2001, 2:15 PM
 *
 * $Id$
 */
package pcgen.core;

import pcgen.util.DiceExpression;
import pcgen.util.Logging;
import pcgen.util.ParseException;

import java.util.Arrays;

/**
 * <code>RollingMethods</code>.
 *
 * @author Mario Bonassin <zebuleon@users.sourceforge.net>
 * @author <a href="mailto:binkley@alumni.rice.edu">B. K. Oxley (binkley)</a>
 * @version $Revision$
 */
public final class RollingMethods
{
	/**
	 * Roll <var>times</var> number of dice with <var>sides</var>
	 * shape.
	 *
	 * @param times int how many dice to roll?
	 * @param sides int what shape dice?
	 *
	 * @return int dice total
	 */
	public static int roll(final int times, final int sides)
	{
		return roll(times, sides, times, 0, 0);
	}

	/**
	 * One random number between 1 and <var>sides</var>, good, for
	 * example, for rolling percentage dice.
	 *
	 * @param sides int what shape die?
	 *
	 * @return int die roll
	 */
	public static int roll(final int sides)
	{
		return Globals.getRandomInt(sides) + 1;
	}

	/**
	 * Roll <var>times</var> dice with <var>sides</var> shape,
	 * sort them, and return the sum of only those listed in
	 * <var>keep</var> (0-indexed).
	 *
	 * @param times int how many dice to roll?
	 * @param sides int what shape dice?
	 * @param keep int[] which dice to keep (0-indexed)?
	 *
	 * @return int dice total
	 */
	public static int roll(int times, final int sides, final int[] keep)
	{
		// return roll (times, sides, keep, 0, 0);
		final int[] rolls = new int[times];

		while (--times >= 0)
		{
			rolls[times] = Globals.getRandomInt(sides);
		}

		java.util.Arrays.sort(rolls);

		int total = keep.length; // keep the +1 at the end

		for (int i = 0; i < keep.length; ++i)
		{
			total += rolls[keep[i]]; // 0-indexed
		}

		return total;
	}

	/**
	 * Roll <var>times</var> bizarre dice.
	 *
	 * @param times int how many dice to roll?
	 * @param shape int[] array of values of sides of die
	 *
	 * @return what the die says
	 */
	public static int roll(int times, final int[] shape)
	{
		int total = 0;

		while (--times >= 0)
		{
			total += shape[Globals.getRandomInt(shape.length)];
		}

		return total;
	}

	/**
	 * Roll <var>times</var> bizarre dice, keeping
	 * <var>keep</keep> of them in ascending order.
	 *
	 * @param times int how many dice to roll?
	 * @param shape int[] array of values of sides of die
	 * @param keep int[] which dice to keep
	 *
	 * @return what the die says
	 */
	public static int roll(int times, final int[] shape, final int[] keep)
	{
		final int[] rolls = new int[times];

		while (--times >= 0)
		{
			rolls[times] = shape[Globals.getRandomInt(shape.length)];
		}

		Arrays.sort(rolls);

		int total = 0;

		for (int i = 0; i < keep.length; ++i)
		{
			total += rolls[keep[i]]; // 0-indexed
		}

		return total;
	}

	/**
	 * Takes many forms including "2d6-2" and returns the result
	 * Whitespace is ignored; case insensitive;  Most simple math
	 * operations (including exponentiation) are supported
	 * Functions builtin include max, min, roll
	 *  Add new functions to DiceExpressionFunctions
	 *
	 * @see pcgen.util.DiceExpression
	 *
	 * @param method String formatted string representing dice roll
	 *
	 * @return int dice total
	 */
	public static int roll(final String method)
	{
		int r = 0;

		if (method.length() <= 0)
		{
			return r;
		}

		final DiceExpression parser = new DiceExpression(method);

		try
		{
			r = parser.rollDice();

			if (r == DiceExpression.BAD_VALUE)
			{
				r = 0;
			}
		}
		catch (ParseException ex)
		{
			Logging.errorPrint("Bad dice: " + method + ": " + ex);
		}

		return r;
	}

	/**
	 * Roll {<code>times</code>} 1d{<code>sides</code>}, reroll any result <= {<code>reroll</code>}.
	 * Add together the highest {<code>numToKeep</code>} dice then add {<code>modifier</code>}
	 * and return the result.
	 *
	 * @param times
	 * @param sides
	 * @param numToKeep
	 * @param reroll
	 * @param modifier
	 * @return the result of the die roll
	 */
	private static int roll(
			final int times,
			final int sides,
			final int numToKeep,
			final int reroll,
			final int modifier)
	{
		final int[] dieRoll = new int[times];
		int         total   = 0;
		final int   keep    = (numToKeep > times) ? times : numToKeep;

		for (int i = 0; i < times; ++i)
		{
			dieRoll[i] = roll(sides - reroll) + reroll;
		}

		Arrays.sort(dieRoll);

		if (Logging.isDebugMode())
		{
			final StringBuffer rollString = new StringBuffer(times << 2);
			rollString.append(dieRoll[0]);

			if (times > 1)
			{
				for (int i = 1; i < times; ++i)
				{
					rollString.append(" + ").append(dieRoll[i]);
				}
			}
			Logging.debugPrint(rollString.toString());
		}

		// Now add together the highest "keep" dice

		for (int j = times - keep; j < times; j++) {total += dieRoll[j];}

		return total + modifier;
	}
}
