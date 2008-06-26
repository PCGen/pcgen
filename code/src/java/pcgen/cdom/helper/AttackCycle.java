/*
 * Copyright 2007, 2008 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.helper;

import pcgen.cdom.base.Constants;
import pcgen.util.enumeration.AttackType;

/**
 * An AttackCycle represents an AttackType that achieves additional attacks with
 * a given frequency.
 */
public class AttackCycle
{

	/**
	 * The AttackType for this AttackCycle
	 */
	private final AttackType type;

	/**
	 * The value indicating the value of the modifier required to get an
	 * additional attack of the given type.
	 */
	private final int value;

	/**
	 * Creates a new AttackCycle with the given AttackType and improvement
	 * frequency.
	 * 
	 * @param key
	 *            The AttackType for this AttackCycle
	 * @param val
	 *            The value indicating the value of the modifier required to get
	 *            an additional attack of the given type.
	 * @throws IllegalArgumentException
	 *             if the given AttackType is null
	 */
	public AttackCycle(AttackType key, int val)
	{
		if (key == null)
		{
			throw new IllegalArgumentException(
					"Attack Type for AttackCycle cannot be null");
		}
		type = key;
		value = val;
	}

	/**
	 * Returns the AttackType for this AttackCycle
	 * 
	 * @return The AttackType for this AttackCycle
	 */
	public AttackType getAttackType()
	{
		return type;
	}

	/**
	 * Returns the value indicating the value of the modifier required to get an
	 * additional attack of the given type.
	 * 
	 * @return The value indicating the value of the modifier required to get an
	 *         additional attack of the given type.
	 */
	public int getValue()
	{
		return value;
	}

	/**
	 * Returns the consistent-with-equals hashCode for this FixedSizeFormula
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return type.hashCode() ^ value;
	}

	/**
	 * Returns true if this FixedSizeFormula is equal to the given Object.
	 * Equality is defined as being another FixedSizeFormula object with equal
	 * underlying SizeAdjustment
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o)
	{
		if (o instanceof AttackCycle)
		{
			AttackCycle other = (AttackCycle) o;
			return type.equals(other.type) && value == other.value;
		}
		return false;
	}

	/**
	 * Returns a String representation of this AttackCycle, primarily for
	 * purposes of debugging. It is strongly advised that no dependency on this
	 * method be created, as the return value may be changed without warning.
	 */
	@Override
	public String toString()
	{
		return type.getIdentifier() + Constants.PIPE + value;
	}
}
