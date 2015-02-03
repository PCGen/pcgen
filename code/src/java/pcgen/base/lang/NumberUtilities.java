/*
 * Copyright 2014-15 (C) Tom Parker <thpr@users.sourceforge.net>
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

import java.math.BigDecimal;

/**
 * A Set of utilities related to java.lang.Number.
 */
public final class NumberUtilities
{

	private NumberUtilities()
	{
		//Do not instantiate
	}

	/**
	 * Returns a Number for the given String. Will preferentially return an
	 * Integer when possible, otherwise returns a Double.
	 * 
	 * @param number
	 *            The number (as a String) to be converted into a Number
	 * @return A Number for the given String
	 * @throws NumberFormatException
	 *             if the given String cannot be converted to a Number
	 */
	public static Number getNumber(String number)
	{
		if (number.length() < 8)
		{
			try
			{
				return Integer.valueOf(number);
			}
			catch (NumberFormatException e)
			{
				//Fall through to below (same as if number was very long)
			}
		}
		//Let this throw NumberFormatException if it fails
		//CONSIDER what to do if the input String is NaN?  Do we throw an exception?
		return Double.valueOf(number);
	}

	/**
	 * Returns a Number for the given String. Will preferentially return an
	 * Integer when possible, otherwise returns a BigDecimal.
	 * 
	 * @param number
	 *            The number (as a String) to be converted into a Number
	 * @return A Number for the given String
	 * @throws NumberFormatException
	 *             if the given String cannot be converted to a Number
	 */
	public static Number getPreciseNumber(String number)
	{
		//CONSIDER is there a way to *reliably* know if a decimal can be stored in a Double?
		if (number.length() < 8)
		{
			try
			{
				return Integer.valueOf(number);
			}
			catch (NumberFormatException e)
			{
				//Fall through to below
			}
		}
		//CONSIDER what to do if the input String is NaN?  Do we throw an exception?
		return new BigDecimal(number);
	}

}
