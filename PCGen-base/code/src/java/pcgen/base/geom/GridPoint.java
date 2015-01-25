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

/**
 * A GridPoint is an arbitrary precision, immutable peer of Point2D.
 * 
 * This is distinguished from Point2D.Double in at least two ways.
 * 
 * First, this object is arbitrary precision, in that it stores a Number, which
 * could be a Double, Integer or any other Number. This allows the object using
 * the GridPoint to determine the necessary amount of precision.
 * 
 * Second, this is an immutable object. There is no setter for the internal
 * values and the fields are private.
 */
public class GridPoint
{

	/**
	 * Stores the x value of this GridPoint
	 */
	private Number x;

	/**
	 * Stores the y value of this GridPoint
	 */
	private Number y;

	/**
	 * Constructs a new GridPoint from the given Numbers
	 * 
	 * @param x
	 *            The x value of the GridPoint
	 * @param y
	 *            The y value of the GridPoint
	 * @throws IllegalArgumentException
	 *             if either the x or y value is null
	 */
	public GridPoint(Number x, Number y)
	{
		if (x == null)
		{
			throw new IllegalArgumentException("x value cannot be null");
		}
		if (y == null)
		{
			throw new IllegalArgumentException("x value cannot be null");
		}
		this.x = x;
		this.y = y;
	}

	/**
	 * Returns the x value of this GridPoint at full (original) precision. This
	 * means the incoming Number is returned.
	 * 
	 * Note: This makes no attempt to clone the Number being returned. It is
	 * assumed that the Number provided at construction is Immutable or meant to
	 * be shared with objects calling this method.
	 * 
	 * @return the x value of this GridPoint in full (original) precision.
	 */
	public Number getPreciseX()
	{
		return x;
	}

	/**
	 * Returns the y value of this GridPoint at full (original) precision. This
	 * means the incoming Number is returned.
	 * 
	 * Note: This makes no attempt to clone the Number being returned. It is
	 * assumed that the Number provided at construction is Immutable or meant to
	 * be shared with objects calling this method.
	 * 
	 * @return the y value of this GridPoint in full (original) precision.
	 */
	public Number getPreciseY()
	{
		return y;
	}

	/**
	 * Converts a String representation of a GridPoint into a GridPoint object.
	 * 
	 * @param value
	 *            The String representation to be converted into a GridPoint
	 * @return a GridPoint object with the x and y values defined by the given
	 *         String
	 * @throws IllegalArgumentException
	 *             if the given String does not represent a valid GridPoint
	 */
	public static GridPoint valueOf(String value)
	{
		int commaLoc = value.indexOf(',');
		if (commaLoc != value.lastIndexOf(','))
		{
			throw new IllegalArgumentException(
				"GridPoint must have only one comma.  "
					+ "Must be of the form: <num>,<num>");
		}
		if (commaLoc == -1)
		{
			throw new IllegalArgumentException("GridPoint must have a comma.  "
				+ "Must be of the form: <num>,<num>");
		}
		if (commaLoc == 0)
		{
			throw new IllegalArgumentException(
				"GridPoint should not start with a comma.  "
					+ "Must be of the form: <num>,<num>");
		}
		if (commaLoc == value.length() - 1)
		{
			throw new IllegalArgumentException(
				"GridPoint should not end with a comma.  "
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
				"Misunderstood first value in GridPoint: " + value);
		}
		try
		{
			String heightString = value.substring(commaLoc + 1).trim();
			height = NumberUtilities.getPreciseNumber(heightString);
		}
		catch (NumberFormatException ne)
		{
			throw new IllegalArgumentException(
				"Misunderstood second value in GridPoint: " + value);
		}
		return new GridPoint(width, height);
	}

	/**
	 * Returns a String representation of this GridPoint.
	 * 
	 * This String representation is also meant to be a human-readable
	 * "serialization" of the GridPoint which can be converted by the valueOf
	 * method of GridPoint into a GridPoint.
	 * 
	 * Note: This will return an arbitrary-precision number for the x and y
	 * values of this GridPoint. No attempt is made to format it for display. If
	 * formatting is required, it is advised that you use getPreciseX() and
	 * getPreciseY() and do the formatting as necessary.
	 * 
	 * @return A String representation of this GridPoint.
	 */
	@Override
	public String toString()
	{
		return x.toString() + "," + y.toString();
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return x.hashCode() ^ y.hashCode();
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o)
	{
		if (o == this)
		{
			return true;
		}
		if (o instanceof GridPoint)
		{
			GridPoint other = (GridPoint) o;
			return other.x.equals(x) && other.y.equals(y);
		}
		return false;
	}
}
