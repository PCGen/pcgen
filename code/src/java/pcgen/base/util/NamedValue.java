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
	 * The name of the NamedValue
	 */
	public final String name;

	/**
	 * The (double) value contained in the the NamedValue
	 */
	private double weight;

	/**
	 * Creates a new NamedValue for the given name. The Double value defaults to
	 * 0.0.
	 * 
	 * @param s
	 *            The String to be used as the name of the NamedValue.
	 */
	public NamedValue(String s)
	{
		name = s;
	}

	/**
	 * Creates a new NamedValue for the given name and double value.
	 * 
	 * @param s
	 *            The String to be used as the name of the NamedValue.
	 * @param d
	 *            The double value of the NamedValue.
	 */
	public NamedValue(String s, double d)
	{
		this(s);
		weight = d;
	}

	/**
	 * Returns the weight of the NamedValue
	 * 
	 * @return The double weight of the NamedValue
	 */
	public double getWeight()
	{
		return weight;
	}

	/**
	 * Adds weight to a NamedValue
	 * 
	 * @param d
	 *            the weight to add to this NamedValue
	 */
	public void addWeight(double d)
	{
		weight += d;
		// CONSIDER what if less than zero?
	}

	/**
	 * Returns a String representation of this NamedValue
	 */
	@Override
	public String toString()
	{
		return name + ":" + weight;
	}

	public void removeWeight(double d)
	{
		weight -= d;
		// CONSIDER what if less than zero?
	}

	public String getName()
	{
		return name;
	}

}