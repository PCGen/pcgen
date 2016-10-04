/*
 * Copyright 2008 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.base.util;

import java.util.Objects;

/**
 * A NamedValue is a String-double pair (similar to a Map.Entry). This is
 * designed specifically for use in a setting where a key-value pair is
 * necessary, but a hash isn't appropriate (due to only one entry).
 * 
 * This is also can be considered an alternative to a WeightedCollection, in
 * that a NamedValue can store double values, whereas a WeightedCollection can
 * only store integer values.
 */
public final class NamedValue
{
	/**
	 * The name of the NamedValue.
	 */
	private final String name;

	/**
	 * The (double) value contained in the NamedValue.
	 */
	private double weight;

	/**
	 * Creates a new NamedValue for the given name. The Double value defaults to
	 * 0.0.
	 * 
	 * @param nvName
	 *            The String to be used as the name of the NamedValue.
	 */
	public NamedValue(String nvName)
	{
		name = Objects.requireNonNull(nvName);
	}

	/**
	 * Creates a new NamedValue for the given name and double value.
	 * 
	 * @param nvName
	 *            The String to be used as the name of the NamedValue.
	 * @param startingValue
	 *            The double value of the NamedValue.
	 */
	public NamedValue(String nvName, double startingValue)
	{
		this(nvName);
		weight = startingValue;
	}

	/**
	 * Returns the weight of the NamedValue.
	 * 
	 * @return The double weight of the NamedValue
	 */
	public double getWeight()
	{
		return weight;
	}

	/**
	 * Adds weight to a NamedValue.
	 * 
	 * @param addedWeight
	 *            the weight to add to this NamedValue
	 */
	public void addWeight(double addedWeight)
	{
		weight += addedWeight;
		// CONSIDER what if less than zero?
	}

	/**
	 * Returns a String representation of this NamedValue.
	 */
	@Override
	public String toString()
	{
		return name + ":" + weight;
	}

	/**
	 * Removes an amount of the "weight" from this NamedValue.
	 * 
	 * @param removedWeight
	 *            The amount the value of this NamedValue should be reduced by.
	 */
	public void removeWeight(double removedWeight)
	{
		weight -= removedWeight;
		// CONSIDER what if less than zero?
	}

	/**
	 * Returns the Name of this NamedValue.
	 * 
	 * @return The Name of this NamedValue.
	 */
	public String getName()
	{
		return name;
	}

}
