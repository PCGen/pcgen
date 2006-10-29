/*
 *  Die.java
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

import java.util.Random;

/** Abstract class describing a die of any kind
 * @author Soulcatcher
 * @since May 24, 2003
 */
public abstract class Die
{
	/** Random number seed */
	protected static Random rand = new Random();

	/** Holds the rolls of each die */
	public int[] rolls;

	/** Die modifier */
	public int modifier;

	/** Number of dice */
	public int num;

	/** Number of sides */
	public int sides;

	/**  Number of times rolled */
	public int timesRolled;

	/** Total from last die roll */
	public int total;

	/** Roll the die, and get back a value
	 * @return Result of the die roll
	 */
	public abstract int roll();

	/** Writes out the die name (like 2d6+1)
	 * @return Die name
	 */
	public abstract String toString();

	/** Sets the random Die object. Allows you to put in a seeded random for better randomness.
	 * @param rand Random
	 */
	public void setRandom(Random rand)
	{
		Die.rand = rand;
	}

	/** Returns the last roll.
	 * @return The last roll
	 */
	public int value()
	{
		return total;
	}
}
