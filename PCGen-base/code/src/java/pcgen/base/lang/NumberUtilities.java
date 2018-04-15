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
import java.util.Comparator;

/**
 * A Set of utilities related to java.lang.Number.
 */
public final class NumberUtilities
{

	/**
	 * A Comparator for Number.
	 */
	public static final Comparator<Number> NUMBER_COMPARATOR = new NumberComparator();

	/**
	 * Private Constructor for Utility Class.
	 */
	private NumberUtilities()
	{
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
	@SuppressWarnings({"checkstyle:emptyblock", "PMD.EmptyCatchBlock"})
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
	@SuppressWarnings({"checkstyle:emptyblock", "PMD.EmptyCatchBlock"})
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

	/**
	 * Adds two numbers, returning the resulting Number. Maintains Integer math
	 * if possible.
	 * 
	 * @param a
	 *            The first number to be added
	 * @param b
	 *            The second number to be added
	 * @return The result of the addition of the two given numbers
	 */
	public static Number add(Number a, Number b)
	{
		if (a instanceof Integer && b instanceof Integer)
		{
			return Integer.valueOf(a.intValue() + b.intValue());
		}
		return Double.valueOf(a.doubleValue() + b.doubleValue());
	}

	/**
	 * Subtracts two numbers, returning the resulting Number. Maintains Integer math if
	 * possible.
	 * 
	 * @param a
	 *            The Number from which the second Number will be subtracted
	 * @param b
	 *            The Number to subtract from the first Number
	 * @return The result of the subtraction of the two given Numbers
	 */
	public static Number subtract(Number a, Number b)
	{
		if (a instanceof Integer && b instanceof Integer)
		{
			return Integer.valueOf(a.intValue() - b.intValue());
		}
		return Double.valueOf(a.doubleValue() - b.doubleValue());
	}

	/**
	 * Divides two numbers, returning the resulting Number. Maintains Integer
	 * math if possible.
	 * 
	 * @param numerator
	 *            The numerator in the division
	 * @param divisor
	 *            The divisor in the division
	 * @return The result of the division of the two given numbers
	 */
	public static Number divide(Number numerator, Number divisor)
	{
		if (numerator instanceof Integer && divisor instanceof Integer)
		{
			int num = numerator.intValue();
			int div = divisor.intValue();
			if ((div != 0) && (num % div == 0))
			{
				return Integer.valueOf(num / div);
			}
		}
		return Double.valueOf(numerator.doubleValue() / divisor.doubleValue());
	}

	/**
	 * Returns the greater of the two given numbers.
	 * 
	 * @param a
	 *            The first number to be checked
	 * @param b
	 *            The second number to be checked
	 * @return The greater of the two given numbers
	 */
	public static Number max(Number a, Number b)
	{
		return (a.doubleValue() > b.doubleValue()) ? a : b;
	}

	/**
	 * Returns the lesser of the two given numbers.
	 * 
	 * @param a
	 *            The first number to be checked
	 * @param b
	 *            The second number to be checked
	 * @return The lesser of the two given numbers
	 */
	public static Number min(Number a, Number b)
	{
		return (a.doubleValue() < b.doubleValue()) ? a : b;
	}

	/**
	 * Multiplies two numbers, returning the resulting Number. Maintains Integer
	 * math if possible.
	 * 
	 * @param a
	 *            The first number to be multiplied
	 * @param b
	 *            The second number to be multiplied
	 * @return The result of the multiplication of the two given numbers
	 */
	public static Number multiply(Number a, Number b)
	{
		if (a instanceof Integer && b instanceof Integer)
		{
			return Integer.valueOf(a.intValue() * b.intValue());
		}
		return Double.valueOf(a.doubleValue() * b.doubleValue());
	}
}
