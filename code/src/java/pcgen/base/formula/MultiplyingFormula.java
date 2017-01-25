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
 * 
 * A MultiplyingFormula represents a 'deferred calculation' of sorts, designed
 * to be stored and capable of multiplying a predetermined value with a given
 * input number.
 * 
 * A MultiplyingFormula will always return an Integer
 */
public class MultiplyingFormula implements ReferenceFormula<Integer>
{

	/**
	 * The value to be multiplied with the value input into the resolve method
	 */
	private final int multiplier;

	/**
	 * Creates a new MultiplyingFormula with the given int as the value to be
	 * multiplied with the input to the resolve method
	 * 
	 * @param mult
	 *            multiplier, the int to be multiplied with the input when this
	 *            MultiplyingFormula is used
	 */
	public MultiplyingFormula(int mult)
	{
		multiplier = mult;
	}

	/**
	 * Executes this MultiplyingFormula, multiplying the number provided in the
	 * constructor of this MultiplyingFormula with the given Number. Only one
	 * input Number is permitted.
	 * 
	 * This method is value-semantic and will not modify or maintain a reference
	 * to the given Array of Numbers.
	 * 
	 * @param numbers
	 *            The array of Numbers used to resolve the value of this
	 *            MultiplyingFormula (array length must be 1 and the Number must
	 *            be non-null)
	 * @return the result of the multiplication
	 * @throws IllegalArgumentException
	 *             if more than one Number is provided as an argument
	 * @throws NullPointerException
	 *             if the Number provided is null
	 * @see pcgen.base.formula.ReferenceFormula#resolve(Number...)
	 */
	@Override
	public Integer resolve(Number... numbers)
	{
		if (numbers == null || numbers.length != 1)
		{
			throw new IllegalArgumentException(
					"MultiplyingFormula only has one backreference");
		}
		//Must calculate before rounding, consider 1.4 * 3
		double d = numbers[0].doubleValue() * multiplier;
		return (int) d;
	}

	/**
	 * Returns a String representation of this MultiplyingFormula
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "*" + multiplier;
	}

	/**
	 * Consistent-with-equals hashCode method
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return multiplier;
	}

	/**
	 * Returns true if this MultiplyingFormula is equal to the given Object.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof MultiplyingFormula
				&& ((MultiplyingFormula) obj).multiplier == multiplier;
	}
}
