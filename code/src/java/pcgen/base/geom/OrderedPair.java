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
package pcgen.base.geom;

import pcgen.base.lang.NumberUtilities;
import pcgen.cdom.base.Constants;

/**
 * A OrderedPair is an object that stores the x and y values of the pair as
 * Number objects. This allows significant precision when necessary.
 */
public class OrderedPair
{

	/**
	 * The x value of this OrderedPair.
	 * 
	 * Value will not be null after OrderedPair is constructed
	 */
	private Number x;

	/**
	 * The y value of this OrderedPair
	 * 
	 * Value will not be null after OrderedPair is constructed
	 */
	private Number y;

	/**
	 * Constructs a new OrderedPair from the given x and y values.
	 * 
	 * @param x
	 *            The x value of this OrderedPair
	 * @param y
	 *            The y value of this OrderedPair
	 * @throws IllegalArgumentException
	 *             if the given x or y value is null
	 */
	public OrderedPair(Number x, Number y)
	{
		if (x == null)
		{
			throw new IllegalArgumentException(
				"x value of OrderedPair cannot be null");
		}
		if (y == null)
		{
			throw new IllegalArgumentException(
				"y value of OrderedPair cannot be null");
		}
		this.x = x;
		this.y = y;
	}

	/**
	 * Returns the x value of this OrderedPair to double precision. The actual
	 * value may not match the x value of this OrderedPair due to rounding or
	 * other modification to ensure the value can fit within a double.
	 */
	public double getX()
	{
		return x.doubleValue();
	}

	/**
	 * Returns the y value of this OrderedPair to double precision. The actual
	 * value may not match the y value of this OrderedPair due to rounding or
	 * other modification to ensure the value can fit within a double.
	 */
	public double getY()
	{
		return y.doubleValue();
	}

	/**
	 * Returns the precise x value of this OrderedPair to the original
	 * precision.
	 * 
	 * @return the precise x value of this OrderedPair to the original precision
	 */
	public Number getPreciseX()
	{
		return x;
	}

	/**
	 * Returns the precise y value of this OrderedPair to the original
	 * precision.
	 * 
	 * @return the precise y value of this OrderedPair to the original precision
	 */
	public Number getPreciseY()
	{
		return y;
	}

	/**
	 * Constructs a new OrderedPair from the given String. The String should be
	 * in the format "x,y" where x and y are valid numbers.
	 * 
	 * @param value
	 *            The String used to construct a new OrderedPair
	 * @return a new OrderedPair with the x and y values set based on the
	 *         contents of the given String
	 * @throws IllegalArgumentException
	 *             if the given String is not a String representation of a
	 *             OrderedPair
	 */
	public static OrderedPair valueOf(String value)
	{
		int commaLoc = value.indexOf(Constants.COMMA);
		if (commaLoc != value.lastIndexOf(Constants.COMMA))
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
		Number height;
		try
		{
			String widthString = value.substring(0, commaLoc).trim();
			width = NumberUtilities.getPreciseNumber(widthString);
		}
		catch (NumberFormatException nfe)
		{
			throw new IllegalArgumentException(
				"Misunderstood first value in OrderedPair: " + value);
		}
		try
		{
			String heightString = value.substring(commaLoc + 1).trim();
			height = NumberUtilities.getPreciseNumber(heightString);
		}
		catch (NumberFormatException ne)
		{
			throw new IllegalArgumentException(
				"Misunderstood second value in OrderedPair: " + value);
		}
		return new OrderedPair(width, height);
	}

	/**
	 * Returns a String representation of this OrderedPair.
	 * 
	 * Note that this method is designed to return a String that can be properly
	 * parsed by the valueOf method of OrderedPair
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return x.toString() + "," + y.toString();
	}
}
