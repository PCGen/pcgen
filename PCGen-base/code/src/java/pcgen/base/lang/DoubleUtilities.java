/*
 * Copyright 2014 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.base.lang;

/**
 * A set of utilities related to java.lang.Double.
 */
public final class DoubleUtilities
{

	/**
	 * Represents a small error between two items.
	 */
	public static final double SMALL_ERROR = Math.pow(10, -10);

	private DoubleUtilities()
	{
		//Do not instantiate a utility class
	}

	/**
	 * Provides an expanded ability to compare two numbers within a given
	 * "delta" (or epsilon as it is often known).
	 * 
	 * Returns true if the absolute value of the difference between d1 and d2 is
	 * less than the provided delta.
	 * 
	 * @param d1
	 *            The first double for comparison
	 * @param d2
	 *            The second double for comparison
	 * @param delta
	 *            The acceptable difference between the two given double values
	 *            for comparison
	 * @return true if the absolute value of the difference between d1 and d2 is
	 *         less than the provided delta; false otherwise
	 */
	public static boolean doubleEqual(double d1, double d2, double delta)
	{
		if (delta < 0)
		{
			throw new IllegalArgumentException(
				"Delta for doubleEqual cannot be < 0: " + delta);
		}
		double diff = d1 - d2;
		return ((diff >= 0) && (diff < delta))
			|| ((diff < 0) && (diff > -delta));
	}

}
