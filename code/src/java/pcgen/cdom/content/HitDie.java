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
package pcgen.cdom.content;

import pcgen.cdom.base.ConcretePrereqObject;
import pcgen.core.SettingsHandler;
import pcgen.util.Logging;

/**
 * A HitDie is intended to be a type-safe wrapper for an integer hit die.
 * 
 * HitDie also provides other methods to support additional features for
 * establish the sequence of HitDie objects.
 */
public class HitDie extends ConcretePrereqObject implements Comparable<HitDie>
{

	/**
	 * A HitDie for the integer constant ZERO. This is done in order to minimize
	 * memory usage and construction speed in the many cases where a default
	 * HitDie of ZERO is required.
	 */
	public static final HitDie ZERO = new HitDie(0);

	/**
	 * The integer die for this HitDie
	 */
	private final int die;

	/**
	 * Constructs a new HitDie with the given int value.
	 * 
	 * @param dieSize
	 *            The die size for this HitDie
	 * @throws IllegalArgumentException
	 *             if the given die size is negative
	 */
	public HitDie(int dieSize)
	{
		if (dieSize < 0)
		{
			throw new IllegalArgumentException("HitDie can not have a negative die size");
		}
		die = dieSize;
	}

	/**
	 * Returns the die size of this HitDie
	 * 
	 * @return The die size of this HitDie
	 */
	public int getDie()
	{
		return die;
	}

	/**
	 * Returns the next (i.e. next larger) HitDie in the globally defined
	 * sequence of Hit Dice.
	 * 
	 * The behavior of this method is not defined if the HitDie on which this
	 * method is called is not in the globally defined sequence of Hit Dice.
	 * 
	 * If this is the largest HitDie in the globally defined sequence of Hit
	 * Dice, then this method will return the current HitDie.
	 * 
	 * @return the next HitDie in the globally defined sequence of Hit Dice.
	 */
	public HitDie getNext()
	{
		int[] dieSizes = SettingsHandler.getGameAsProperty().get().getDieSizes();
		int length = dieSizes.length;
		for (int i = 0; i < length; ++i)
		{
			if (die == dieSizes[i])
			{
				if (i == length - 1)
				{
					if (Logging.isDebugMode())
					{
						Logging.debugPrint("Hit Die: " + die + " is Highest Hit Die in Die Sizes");
					}
					return this;
				}
				else
				{
					return new HitDie(dieSizes[i + 1]);
				}
			}
		}
		Logging.errorPrint("Cannot find Hit Die: " + die + " in Global Die Sizes");
		return this;
	}

	/**
	 * Returns the previous (i.e. next smaller) HitDie in the globally defined
	 * sequence of Hit Dice.
	 * 
	 * The behavior of this method is not defined if the HitDie on which this
	 * method is called is not in the globally defined sequence of Hit Dice.
	 * 
	 * If this is the smallest HitDie in the globally defined sequence of Hit
	 * Dice, then this method will return the current HitDie.
	 * 
	 * @return the previous HitDie in the globally defined sequence of Hit Dice.
	 */
	public HitDie getPrevious()
	{
		int[] dieSizes = SettingsHandler.getGameAsProperty().get().getDieSizes();
		int length = dieSizes.length;
		for (int i = 0; i < length; ++i)
		{
			if (die == dieSizes[i])
			{
				if (i == 0)
				{
					if (Logging.isDebugMode())
					{
						Logging.debugPrint("Hit Die: " + die + " is Lowest Hit Die in Die Sizes");
					}
				}
				else
				{
					return new HitDie(dieSizes[i - 1]);
				}
			}
		}
		Logging.errorPrint("Cannot find Hit Die: " + die + " in Global Die Sizes");
		return this;
	}

	@Override
	public int hashCode()
	{
		return die;
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof HitDie && ((HitDie) obj).die == die;
	}

	/**
	 * Returns a String representation of this HitDie, primarily for purposes of
	 * debugging. It is strongly advised that no dependency on this method be
	 * created, as the return value may be changed without warning.
	 */
	@Override
	public String toString()
	{
		return "HitDie: " + die;
	}

	/**
	 * Compares this HitDie to another HitDie.
	 * 
	 * @param other
	 *            The HitDie to be compared to this HitDie.
	 * @return 0 if this HitDie is equal to the given HitDie; -1 if this HitDie
	 *         has a die size less than the given HitDie; +1 if this HitDie has
	 *         a die size greater than the given HitDie
	 * @throws NullPointerException
	 *             if the given HitDie is null
	 */
	@Override
	public int compareTo(HitDie other)
	{
		return Integer.compare(die, other.die);
	}

}
