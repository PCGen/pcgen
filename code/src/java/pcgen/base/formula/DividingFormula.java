/*
 * Copyright (c) 2006 Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.base.formula;

/**
 * @author Thomas Parker (thpr [at] yahoo.com)
 * 
 * A DividingFormula represents a 'deferred calculation' of sorts, designed to
 * be stored and capable of dividing a given input number by a predetermined
 * value
 * 
 * A DividingFormula will always return an Integer
 */
public class DividingFormula implements ReferenceFormula<Integer>
{

	/**
	 * The value the input into the resolve method will be divided by
	 */
	private final int denom;

	/**
	 * Creates a new DividingFormula with the given int as the value to divide
	 * the input to the resolve method by
	 * 
	 * @param denominator
	 *            the int to be divide the input by when this DividingFormula is
	 *            used
	 */
	public DividingFormula(int denominator)
	{
		if (denominator == 0)
		{
			throw new IllegalArgumentException(
				"Cannot build a DividingFormula that divides by Zero - "
					+ "will always cause an ArithmeticException when resolved");
		}
		denom = denominator;
	}

	/**
	 * Executes this DividingFormula, dividing the number provided by the
	 * integer given in the constructor of this DividingFormula. Only one input
	 * Number is permitted.
	 * 
	 * @return the result of the division
	 * @throws IllegalArgumentException
	 *             if more than one Number is provided as an argument
	 * @see pcgen.base.formula.ReferenceFormula#resolve(java.lang.Number[])
	 */
	public Integer resolve(Number... nums)
	{
		if (nums == null || nums.length != 1)
		{
			throw new IllegalArgumentException(
				"DividingFormula only has one backreference");
		}
		/*
		 * Note that there is NOT an order of operations issue here with
		 * rounding, and rounding first results in a faster & more accurate
		 * calculation.
		 */
		return Integer.valueOf(nums[0].intValue() / denom);
	}

	/**
	 * Returns a String representation of this DividingFormula
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "/" + denom;
	}

	/**
	 * Consistent-with-equals hashCode method
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return denom;
	}

	/**
	 * Returns true if this DividingFormula is equal to the given Object.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o)
	{
		return o instanceof DividingFormula
			&& ((DividingFormula) o).denom == denom;
	}
}
