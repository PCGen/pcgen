/*
 * Copyright (c) 2006-15 Tom Parker <thpr@users.sourceforge.net>
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.base.math;

import java.util.Objects;

import pcgen.base.lang.NumberUtilities;

/**
 * An OrderedPair is a pair of ordered, numeric values. They are thought of much
 * like a point or area, with the two values being x and y.
 * 
 * It is effectively an arbitrary precision, immutable peer of Point2D, but is
 * intended for use beyond geometry.
 * 
 * This is distinguished from Point2D.Double in at least two ways.
 * 
 * First, this object is arbitrary precision, in that it stores a Number, which
 * could be a Double, Integer or any other Number. This allows the object using
 * the OrderedPair to determine the necessary amount of precision.
 * 
 * Second, this is an immutable object. There is no setter for the internal
 * values and the fields are private.
 */
public class OrderedPair
{

	/**
	 * Stores the x value of this OrderedPair.
	 */
	private final Number x;

	/**
	 * Stores the y value of this OrderedPair.
	 */
	private final Number y;

	/**
	 * Constructs a new OrderedPair from the given Numbers.
	 * 
	 * @param x
	 *            The x value of the OrderedPair
	 * @param y
	 *            The y value of the OrderedPair
	 */
	public OrderedPair(Number x, Number y)
	{
		this.x = Objects.requireNonNull(x);
		this.y = Objects.requireNonNull(y);
	}

	/**
	 * Returns the x value of this OrderedPair at full (original) precision.
	 * This means the incoming Number is returned.
	 * 
	 * Note: This makes no attempt to clone the Number being returned. It is
	 * assumed that the Number provided at construction is Immutable or meant to
	 * be shared with objects calling this method.
	 * 
	 * @return the x value of this OrderedPair in full (original) precision.
	 */
	public Number getPreciseX()
	{
		return x;
	}

	/**
	 * Returns the y value of this OrderedPair at full (original) precision.
	 * This means the incoming Number is returned.
	 * 
	 * Note: This makes no attempt to clone the Number being returned. It is
	 * assumed that the Number provided at construction is Immutable or meant to
	 * be shared with objects calling this method.
	 * 
	 * @return the y value of this OrderedPair in full (original) precision.
	 */
	public Number getPreciseY()
	{
		return y;
	}

	/**
	 * Converts a String representation of a OrderedPair into a OrderedPair
	 * object.
	 * 
	 * @param value
	 *            The String representation to be converted into a OrderedPair
	 * @return a OrderedPair object with the x and y values defined by the given
	 *         String
	 * @throws IllegalArgumentException
	 *             if the given String does not represent a valid OrderedPair
	 */
	public static OrderedPair valueOf(String value)
	{
		int commaLoc = value.indexOf(',');
		if (commaLoc != value.lastIndexOf(','))
		{
			throw new IllegalArgumentException(
				"OrderedPair must have only one comma.  "
					+ "Must be of the form: <num>,<num>");
		}
		if (commaLoc == -1)
		{
			throw new IllegalArgumentException(
				"OrderedPair must have a comma.  "
					+ "Must be of the form: <num>,<num>");
		}
		if (commaLoc == 0)
		{
			throw new IllegalArgumentException(
				"OrderedPair should not start with a comma.  "
					+ "Must be of the form: <num>,<num>");
		}
		if (commaLoc == value.length() - 1)
		{
			throw new IllegalArgumentException(
				"OrderedPair should not end with a comma.  "
					+ "Must be of the form: <num>,<num>");
		}
		Number width;
		try
		{
			String widthString = value.substring(0, commaLoc).trim();
			width = NumberUtilities.getPreciseNumber(widthString);
		}
		catch (NumberFormatException nfe)
		{
			throw new IllegalArgumentException(
				"Misunderstood first value in OrderedPair: " + value, nfe);
		}
		Number height;
		try
		{
			String heightString = value.substring(commaLoc + 1).trim();
			height = NumberUtilities.getPreciseNumber(heightString);
		}
		catch (NumberFormatException nfe)
		{
			throw new IllegalArgumentException(
				"Misunderstood second value in OrderedPair: " + value, nfe);
		}
		return new OrderedPair(width, height);
	}

	/**
	 * Returns a String representation of this OrderedPair.
	 * 
	 * This String representation is also meant to be a human-readable
	 * "serialization" of the OrderedPair which can be converted by the valueOf
	 * method of OrderedPair into a OrderedPair.
	 * 
	 * Note: This will return an arbitrary-precision number for the x and y
	 * values of this OrderedPair. No attempt is made to format it for display.
	 * If formatting is required, it is advised that you use getPreciseX() and
	 * getPreciseY() and do the formatting as necessary.
	 * 
	 * @return A String representation of this OrderedPair.
	 */
	@Override
	public String toString()
	{
		return x + "," + y;
	}

	@Override
	public int hashCode()
	{
		return x.hashCode() ^ y.hashCode();
	}

	@Override
	public boolean equals(Object o)
	{
		if (o == this)
		{
			return true;
		}
		if (o instanceof OrderedPair)
		{
			OrderedPair other = (OrderedPair) o;
			return other.x.equals(x) && other.y.equals(y);
		}
		return false;
	}
}
