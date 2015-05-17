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

import java.awt.geom.Point2D;

import pcgen.base.lang.NumberUtilities;
import pcgen.cdom.base.Constants;

/**
 * A GridPoint is a {@link Point2D} that stores the x and y values of the
 * Point2D as Number objects. This allows significant precision when necessary.
 */
public class GridPoint extends Point2D
{

	/**
	 * The x value of this GridPoint.
	 * 
	 * Value will not be null after GridPoint is constructed
	 */
	private Number x;

	/**
	 * The y value of this GridPoint
	 * 
	 * Value will not be null after GridPoint is constructed
	 */
	private Number y;

	/**
	 * Constructs a new GridPoint from the given x and y values.
	 * 
	 * @param x
	 *            The x value of this GridPoint
	 * @param y
	 *            The y value of this GridPoint
	 * @throws IllegalArgumentException
	 *             if the given x or y value is null
	 */
	public GridPoint(Number x, Number y)
	{
		if (x == null)
		{
			throw new IllegalArgumentException(
				"x value of GridPoint cannot be null");
		}
		if (y == null)
		{
			throw new IllegalArgumentException(
				"y value of GridPoint cannot be null");
		}
		this.x = x;
		this.y = y;
	}

	/**
	 * Returns the x value of this GridPoint to double precision. The actual
	 * value may not match the x value of this GridPoint due to rounding or
	 * other modification to ensure the value can fit within a double.
	 * 
	 * @see java.awt.geom.Point2D#getX()
	 */
	@Override
	public double getX()
	{
		return x.doubleValue();
	}

	/**
	 * Returns the y value of this GridPoint to double precision. The actual
	 * value may not match the y value of this GridPoint due to rounding or
	 * other modification to ensure the value can fit within a double.
	 * 
	 * @see java.awt.geom.Point2D#getY()
	 */
	@Override
	public double getY()
	{
		return y.doubleValue();
	}

	/**
	 * Returns the precise x value of this GridPoint to the original precision.
	 * 
	 * @return the precise x value of this GridPoint to the original precision
	 */
	public Number getPreciseX()
	{
		return x;
	}

	/**
	 * Returns the precise y value of this GridPoint to the original precision.
	 * 
	 * @return the precise y value of this GridPoint to the original precision
	 */
	public Number getPreciseY()
	{
		return y;
	}

	/**
	 * @see java.awt.geom.Point2D#setLocation(double, double)
	 */
	@Override
	public void setLocation(double x, double y)
	{
		this.x = x;
		this.y = y;
	}

	/**
	 * Constructs a new GridPoint from the given String. The String should be in
	 * the format "x,y" where x and y are valid numbers.
	 * 
	 * @param value
	 *            The String used to construct a new GridPoint
	 * @return a new GridPoint with the x and y values set based on the contents
	 *         of the given String
	 * @throws IllegalArgumentException
	 *             if the given String is not a String representation of a
	 *             GridPoint
	 */
	public static GridPoint valueOf(String value)
	{
		int commaLoc = value.indexOf(Constants.COMMA);
		if (commaLoc != value.lastIndexOf(Constants.COMMA))
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
	 * Note that this method is designed to return a String that can be properly
	 * parsed by the valueOf method of GridPoint
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return x.toString() + "," + y.toString();
	}
}
